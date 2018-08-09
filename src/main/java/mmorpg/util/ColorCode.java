/*
 * The MIT License
 *
 * Copyright 2012 Mike Sebele <mike.sebele@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package mmorpg.util;

/**
 *
 * @author Mike Sebele <mike.sebele@gmail.com>
 */
public class ColorCode {

    private static final String ANSI_ESCAPE = "\u001B[";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "31";
    private static final String ANSI_GREEN = "32";
    private static final String ANSI_YELLOW = "33";
    private static final String ANSI_BLUE = "34";
    private static final String ANSI_PURPLE = "35";
    private static final String ANSI_CYAN = "36";
    private static final String ANSI_WHITE = "37";
    private static final String ANSI_BOLD = "1";
    private static final String NAME = ANSI_ESCAPE + ANSI_YELLOW + "m";
    private static final String ITEM = ANSI_ESCAPE + ANSI_GREEN + "m";
    private static final String MESSAGE = ANSI_ESCAPE + ANSI_WHITE + "m";
    private static boolean on = true;

    public static boolean isOn() {
        return on;
    }

    /**
     * If ColorCoding is on, replaces all ColorCode tags in the text with ANSI
     * color escape sequences. If ColorCoding is off, removes all ColorCode
     * tags.
     *
     * @param text The string to encode
     * @return The encoded string
     */
    public static String encode(String text) {
        if (on) {
            if (text.matches(".*\\s@>[^(<@)]*\\z")) {
                // System.out.println("Safe tags in: " + text);
                // the string ends with a close tag
                text = text.replaceAll("\\s@>", ANSI_RESET);
                text = text.replaceAll("<@name\\s", NAME);
                text = text.replaceAll("<@item\\s", ITEM);
                text = text.replaceAll("<@message\\s", MESSAGE);
            } else {
                // doesn't end with a close tag
                if (text.matches(".*<@\\S*\\s.*")) {
                    // System.out.println("Corrupt tags in: " + text);
                    // the string has an open tag, tags corrupt, remove them
                    text = text.replaceAll("\\s@>", "");
                    text = text.replaceAll("<@\\S*\\s", "");
                }
                //else, doesn't have any tags, no need to encode
            }
        } else {
            text = text.replaceAll("\\s@>", "");
            text = text.replaceAll("<@\\S*\\s", "");
        }
        return text;
    }

    public static String removeCodes(String text) {
        text = text.replaceAll("\\s?@>", "");
        text = text.replaceAll("<@\\S*\\s", "");
        return text;
    }
}
