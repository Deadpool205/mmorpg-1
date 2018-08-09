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

import java.io.File;
import java.io.FileInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.Timer;
import mmorpg.model.living.Player;
import mmorpg.room.Room;
import mmorpg.util.Heartbeat;

/**
 *
 * @author beecrofs
 */
public class Server {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {



            System.out.println("MMORPG Server " + MMORPG.version + " Running.");

            MMORPG game = new MMORPG();

            MMORPG.timer.scheduleAtFixedRate(new ConnectionManagerTask(), 0, 5000);

            //else { 
            //    room = game.build();
            //}

            boolean a = true;
            int port = 4000;
            try {
                //MMORPG.properties.load(new FileInputStream("./configure.properties"));
                port = Integer.parseInt(MMORPG.properties.getProperty("port"));
                MMORPG.path = MMORPG.properties.getProperty("path");
            } catch (Exception ex) {
                System.out.println("Unable to read ./configure.properties:" + ex.getMessage());
            }
            Room room1 = null;
            try {
                System.out.println("Loading Rooms...");
                File dir = new File("./rooms");
                File[] list = dir.listFiles();
                for (int i = 0; i < list.length; i++) {
                    String sRoom = list[i].getName();
                    System.out.println("Loading:" + sRoom);
                    Room r = game.build("./rooms/" + sRoom);
                    MMORPG.rooms.put(r.getId(), r);
                }
                // Load ROOT.
                String r = MMORPG.properties.getProperty("root");
                System.out.println("Loading root:" + r);
                int root = Integer.parseInt(r);
                room1 = (Room) MMORPG.rooms.get(root);
                MMORPG.root = room1;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            ServerSocket sock = new ServerSocket(port, 100);
            sock.setReuseAddress(true);

            while (a) {
                System.out.println("MMORPG Server " + MMORPG.version + " Initialized on port " + port + ", waiting for connection(s)...");
                final Socket s = sock.accept();
                s.setKeepAlive(true);
                System.out.println("Got connection, binding to player.");

                final Player p = new Player();

                Thread t = new Thread() {
                    public void run() {
                        Monitor m = new Monitor(s, p);
                        m.start();
                        System.out.println("Done.");
                    }
                };
                t.start();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
