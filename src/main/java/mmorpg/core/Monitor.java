/**
 * Copyright (c) 2012 Sean Beecroft, Permission is hereby granted, free of
 * charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * For more information on this project and others, please visit my google code 
 * repository:
 * https://code.google.com/u/seanbeecroft@gmail.com/
 */
package mmorpg.core;

import mmorpg.util.Level;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.Scanner;
import java.util.logging.Logger;
import mmorpg.model.items.Item;
import mmorpg.model.living.Player;
import mmorpg.util.Gender;
import mmorpg.util.InputValidator;
import mmorpg.util.Util;

public class Monitor extends Thread {

    DecimalFormat dfc = new DecimalFormat("0.00");
    DecimalFormat df = new DecimalFormat("0");
    Socket s = null;
    Player p = null;

    public Monitor(Socket sock, Player p) {
        this.p = p;
        s = sock;
    }

    public void prompt() {
        Console log = p.getLog();
        char u = 181;
        log.print(df.format(p.getHealth().getValue()) + "/" + df.format(p.getHealth().getMax()) + "hp " + df.format(p.getExperience().getValue()) + "xp " + df.format(p.getEnergy().getValue()) + "/" + df.format(p.getEnergy().getMax()) + "e " + dfc.format(p.getBalance()) + "c>");

    }
    
    public void displayScreen(Console log)
    {
// Read Screen
            try {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                        new FileInputStream(
                        new File("./WELCOME.txt"))));
                String line = null;
                while ((line = br.readLine()) != null) {
                    log.println(line);
                }
            } catch (Exception ex) {
            }        
    }
    
    public void doLogin(Player p, Console log, BufferedReader reader)
    {
// Read Email
        try { 
            Gender gender = Gender.MALE;
            String name = "";
            String email = "";
            String password = "";
            String cpassword = "";

            email = InputValidator.getInput("Enter Userid (Email) & Press Enter:", log, reader, InputValidator.Type.EMAIL);
            email = email.toLowerCase();

            password = InputValidator.getInput("Enter Password & Press Enter:", log, reader, InputValidator.Type.PASSWORD);
            password = password.toLowerCase();

            Player _p = MMORPG.db.load(email, password);

            if (_p == null) {

                if (MMORPG.db.exists(email)) {
                    log.println("Passwords don't match.");
                    log.println("Goodbye");
                    return;
                } else {

                    log.println("New Player, Welcome.");
                    cpassword = InputValidator.getInput("Re-Enter Password (Confirm) & Press Enter:", log, reader, InputValidator.Type.PASSWORD);
                    cpassword = cpassword.toLowerCase();
                    if (password.equals(cpassword)) {
                        name = InputValidator.getInput("Choose your Screen Name & Press Enter:", log, reader, InputValidator.Type.NAME);
                        name = Util.capitalize(name.toLowerCase());
                        // Set name and enter the world.
                        String sGenger = InputValidator.getInput("Select Gender: M/F", log, reader, InputValidator.Type.GENDER);
                        if(sGenger.equalsIgnoreCase("M")){
                            p.setGender(Gender.MALE);
                        } else {
                            p.setGender(Gender.FEMALE);                            
                        }
                        p.setName(name);
                        p.setUserID(email);
                        p.setPassword(password);

                    } else {
                        log.println("Passwords don't match. (Re-Login Required)");
                        log.println("Goodbye");
                        return;
                    }
                }
            } else {
                log.println("Welcome Back " + _p.getName() + ".");
                // HACK
                Item[] list = _p.getInventory();
                for (int i = 0; i < list.length; i++) {
                    p.add(list[i]);
                }
                p.setUserID(_p.getUserID());
                p.setGender(_p.getGender());
                p.setBalance(_p.getBalance());
                p.setRole(_p.getRole());
                p.setName(_p.getName());
                p.setId(_p.getId());
            } 
            
        } catch(Exception ex) { 
            System.out.println("doLogin had an exception - " + ex.getMessage());
        }
    }

    public void run() {
        PrintWriter wr = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            wr = new PrintWriter(s.getOutputStream(), true);

            Console log = new Console(p);
            log.setWriter(wr);
            p.setLog(log);

            displayScreen(log);
            boolean flag = true;

            doLogin(p, log, reader);

            MMORPG.root.add(p);
            p.setRoom(MMORPG.root);
            p.setLoggedIn(true);
            
            log.println("Play at your own risk.");
            log.println("type 'help' to get commands.");

            Broadcast.send("A whole in the sky appears and " + p.getName() + " falls down into the world!", p, Level.GAME);
            p.setDescription("This is " + p.getName());
            p.setSocket(s);
            MMORPG.users.put(p.getUserID(), p);
            p.save();

            flag = true;

            long idle = 0;
            while (p.isLoggedIn()) {
                try {
                    prompt();
                    String command = reader.readLine();
                    String original = command;
                    command = command.toUpperCase();
                    String[] words = command.split(" ");
                    String cmd = words[0];
                    String k = (String) MMORPG.alias.get(cmd);
                    if (k != null) {
                        cmd = k;
                    }
                    
                    String what = original.substring(words[0].length(), command.length());
                    what = what.trim();
                    if (Util.isExit(cmd)) {
                        break;
                    }
                    p.process(cmd, what);
                    Thread.sleep((long)(300 - p.getSpeed().getValue()));
                } catch (Exception ex) {
                    //break;
                }
            }
            Util.logout(p);
        } catch (IOException ex) {
            try {
                //ex.printStackTrace();
                s.close();
            } catch (IOException ex1) {
            }
        } catch (Exception e) {
            try {
                s.close();
                //e.printStackTrace();
            } catch (IOException ex) {
            }
        }
    }
}
