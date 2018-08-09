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
package mmorpg.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mmorpg.core.Console;

/**
 *
 * @author beecrofs
 */
public class InputValidator {

    public static enum Type {

        EMAIL, PASSWORD, NAME, GENDER
    };

    public static boolean isValidEmail(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        if (password.length() < 6) {
            return false;
        }
        if (password.length() > 15) {
            return false;
        }
        if (!password.matches("[a-zA-Z0-9]*")) {
            return false;
        }
        if (password.matches("[a-zA-Z]*99")) {
            return false;
        }
        return true;
    }

    public static boolean isValidName(String name) {
        if (name == null) {
            return false;
        }
        if (name.length() < 3) {
            return false;
        }
        if (name.indexOf(" ") != -1) {
            return false;
        }
        return true;
    }

    public static boolean isValidGender(String g) {
        if (g == null) {
            return false;
        }
        if (g.length() < 1) {
            return false;
        }
        if (g.equalsIgnoreCase("M")||g.equalsIgnoreCase("F")) {
            return true;
        }
        return false;
    }
    
    public static String getInput(String message, Console log, BufferedReader reader, Type t) {
        String retVal = "";
        boolean flag = true;
        while (flag) {
            try {
                log.println(message);
                
                String input = reader.readLine();
                if (input == null || input.equals("")) {
                    continue;
                }
                input = input.trim();
                input = input.toLowerCase();
                if (t == Type.PASSWORD) {
                    if (!isValidPassword(input)) {
                        log.println("Invalid Password.");
                        log.println("Must be 6 - 9 characters and contain letters and numbers.");
                        continue;
                    } else {
                        return input;
                    }
                } else if (t == Type.EMAIL) {
                    if (!isValidEmail(input)) {
                        log.println("Invalid Email.");
                        continue;
                    } else {
                        return input;
                    }
                } else if (t == Type.NAME) {
                    if (!isValidName(input)) {
                        log.println("Invalid Name");
                        log.println("Must not contain spaces and be at least 3 characters.");
                        continue;
                    } else {
                        return input;
                    }
                } else if (t == Type.GENDER) {
                    if (!isValidGender(input)) {
                        log.println("Invalid GENDER");
                        log.println("Must be either M or F.");
                        continue;
                    } else {
                        return input;
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return retVal;
    }
    
    public static void main(String[] args)
    {
         System.out.println("Is valid email:" + isValidEmail("seanbeecroft@gmail.com"));
    }
}
