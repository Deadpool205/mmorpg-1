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
 *
 * THIS CODE CONTAINS CODE TAKEN FROM:
 * http://www.rgagnon.com/javadetails/java-0426.html
 */
package mmorpg.util;

import java.io.IOException;
import java.net.Socket;
import java.text.DecimalFormat;

import java.util.logging.Level;
import java.util.logging.Logger;
import mmorpg.core.Broadcast;
import mmorpg.core.Console;
import mmorpg.core.MMORPG;
import mmorpg.model.Entity;
import mmorpg.model.items.weapons.Weapon;
import mmorpg.model.living.Beast;
import mmorpg.model.living.Citizen;
import mmorpg.model.living.Player;

/**
 *
 * @author beecrofs
 */
public class Util {

    public final static long ONE_SECOND = 1000;
    public final static long THREE_SECONDS = 1000 * 3;
    public final static long TEN_SECONDS = 10000;
    public final static long ONE_MINUTE = 10000 * 6;
    public final static long THREE_MINUTES = 10000 * 6 * 3;
    public final static long FIVE_MINUTES = ONE_MINUTE * 5;
    public final static long TEN_MINUTES = ONE_MINUTE * 10;
    public final static long THIRTY_MINUTES = ONE_MINUTE * 30;
    public final static long THIRTY_SECONDS = ONE_SECOND * 30;
    public final static long ONE_HOUR = ONE_MINUTE * 60;
    
    public static void logout( Player p )
    {
        try {
            if( p.isLoggedIn()){
                
                Console log = p.getLog();    
                Broadcast.send(p.getName() + " dissapeared with a *poof*!", p.getRoom());
                p.save();
                p.getRoom().remove(p);
                MMORPG.users.remove(p.getUserID());
                log.println("Goodbye!");   
                Socket s = p.getSocket();
                if( s != null ){
                    s.close();
                }
                p.setLoggedIn(false);
            }
        } catch (IOException ex) {
            //ex.printStackTrace();
        }
    }
    
    static String key = "das08523ufkdsfmnip2o3485mjfkwemfr934859r0mfsdkdlkalskdlaksdlkasdlkasd";
    
    public static String encrypt(String in) {
        return encode(encode(in, key), key);
    }

    public static String decrypt(String in) {
        return decode(decode(in, key), key);
    }

    public static String encode(String s, String key) {
        return base64Encode(xorWithKey(s.getBytes(), key.getBytes()));
    }

    public static String decode(String s, String key) {
        return new String(xorWithKey(base64Decode(s), key.getBytes()));
    }

    private static byte[] xorWithKey(byte[] a, byte[] key) {
        byte[] out = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            out[i] = (byte) (a[i] ^ key[i % key.length]);
        }
        return out;
    }

    private static byte[] base64Decode(String s) {
        Base64 d = new Base64();
        return d.decode(s);
    }

    private static String base64Encode(byte[] bytes) {
        Base64 enc = new Base64();
        return enc.encode(bytes).replaceAll("\\s", "");

    }
    
    public static boolean isExit(String cmd)
    {
        if( cmd == null ){
            return false;
        }
        if( cmd.equalsIgnoreCase("quit")){
            return true;
        } else if( cmd.equalsIgnoreCase("q")){
            return true;
        } else if( cmd.equalsIgnoreCase("~q")){
            return true;
        } else if( cmd.equalsIgnoreCase("exit")){
            return true;
        } else { 
            return false;
        }
    }
    public static Gender genderFromString(String s)
    {
        if( s == null ){
            return Gender.MALE;
        }
        else if( s.equals("")){
            return Gender.MALE;
        }
        else if( s.equalsIgnoreCase("M")){
            return Gender.MALE;            
        }
        else if( s.equalsIgnoreCase("MALE")){
            return Gender.MALE;            
        }
        return Gender.FEMALE;            
    }
    
    public static String toGenderChar(Gender g)
    {
        if( g == Gender.MALE ){
            return "M";
        }
        else {
            return "F";
        }
    }

    public static String toGenderChar(String g)
    {
        if( g.equalsIgnoreCase("MALE") ){
            return "M";
        }
        else {
            return "F";
        }
    }
    
    private static final String[] tensNames = {
        "",
        " ten",
        " twenty",
        " thirty",
        " forty",
        " fifty",
        " sixty",
        " seventy",
        " eighty",
        " ninety"
    };
    private static final String[] numNames = {
        "",
        " one",
        " two",
        " three",
        " four",
        " five",
        " six",
        " seven",
        " eight",
        " nine",
        " ten",
        " eleven",
        " twelve",
        " thirteen",
        " fourteen",
        " fifteen",
        " sixteen",
        " seventeen",
        " eighteen",
        " nineteen"
    };

    private static String convertLessThanOneThousand(int number) {
        String soFar;

        if (number % 100 < 20) {
            soFar = numNames[number % 100];
            number /= 100;
        } else {
            soFar = numNames[number % 10];
            number /= 10;

            soFar = tensNames[number % 10] + soFar;
            number /= 10;
        }
        if (number == 0) {
            return soFar;
        }
        return numNames[number] + " hundred" + soFar;
    }

    public static String convert(long number) {
        // 0 to 999 999 999 999
        if (number == 0) {
            return "zero";
        }

        String snumber = Long.toString(number);

        // pad with "0"
        String mask = "000000000000";
        DecimalFormat df = new DecimalFormat(mask);
        snumber = df.format(number);

        // XXXnnnnnnnnn 
        int billions = Integer.parseInt(snumber.substring(0, 3));
        // nnnXXXnnnnnn
        int millions = Integer.parseInt(snumber.substring(3, 6));
        // nnnnnnXXXnnn
        int hundredThousands = Integer.parseInt(snumber.substring(6, 9));
        // nnnnnnnnnXXX
        int thousands = Integer.parseInt(snumber.substring(9, 12));

        String tradBillions;
        switch (billions) {
            case 0:
                tradBillions = "";
                break;
            case 1:
                tradBillions = convertLessThanOneThousand(billions)
                        + " billion ";
                break;
            default:
                tradBillions = convertLessThanOneThousand(billions)
                        + " billion ";
        }
        String result = tradBillions;

        String tradMillions;
        switch (millions) {
            case 0:
                tradMillions = "";
                break;
            case 1:
                tradMillions = convertLessThanOneThousand(millions)
                        + " million ";
                break;
            default:
                tradMillions = convertLessThanOneThousand(millions)
                        + " million ";
        }
        result = result + tradMillions;

        String tradHundredThousands;
        switch (hundredThousands) {
            case 0:
                tradHundredThousands = "";
                break;
            case 1:
                tradHundredThousands = "one thousand ";
                break;
            default:
                tradHundredThousands = convertLessThanOneThousand(hundredThousands)
                        + " thousand ";
        }
        result = result + tradHundredThousands;

        String tradThousand;
        tradThousand = convertLessThanOneThousand(thousands);
        result = result + tradThousand;

        // remove extra spaces!
        return result.replaceAll("^\\s+", "").replaceAll("\\b\\s{2,}\\b", " ");
    }

    public static String capitalize(String string) {
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'') { // You can add other chars here
                found = false;
            }
        }
        return String.valueOf(chars);
    }

    public static String getGenderWords(Gender g) {
        if (g == Gender.FEMALE) {
            return "her";
        } else if (g == Gender.FEMALE) {
            return "him";
        } else {
            return "it";
        }
    }

    public static String getGenderWordsOwner(Gender g) {
        if (g == Gender.FEMALE) {
            return "hers";
        } else if (g == Gender.FEMALE) {
            return "his";
        } else {
            return "it";
        }
    }

    public static String getGenderWordsGeneral(Gender g) {
        if (g == Gender.FEMALE) {
            return "she";
        } else if (g == Gender.FEMALE) {
            return "he";
        } else {
            return "it";
        }
    }

    /**
     * testing
     *
     * @param args
     */
    public static String toDamageWords(double amount, Entity attacker) {
        if (attacker instanceof Citizen || attacker instanceof Player) {
            if (amount < 1) {
                return "completely misses";
            } else if (amount < 3) {
                return "lightly grazes";
            } else if (amount < 5) {
                return "hits";
            } else if (amount < 6) {
                return "punches";
            } else if (amount < 7) {
                return "kicks";
            } else if (amount < 8) {
                return "smashes";
            } else if (amount < 9) {
                return "crushes";
            }
            return "slugs";
        } else if (attacker instanceof Beast) {
            if (amount < 1) {
                return "completely misses";
            } else if (amount < 3) {
                return "lightly grazes";
            } else if (amount < 5) {
                return "swipes";
            } else if (amount < 6) {
                return "scratches";
            } else if (amount < 7) {
                return "bites";
            } else if (amount < 8) {
                return "chews";
            } else if (amount < 9) {
                return "knaws";
            }
            return "slaps";

        } else {
            return "injures";
        }
    }
}
