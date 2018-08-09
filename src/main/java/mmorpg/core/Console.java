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

import java.io.BufferedWriter;
import java.io.PrintWriter;
import mmorpg.model.living.Living;
import mmorpg.util.ColorCode;

/**
 *
 * @author beecrofs
 */
public class Console {

    private PrintWriter writer = null;
    private Living entity = null;
    public Console(Living living)
    {
        this.entity = living;
    }
    
    public void print(String str) {
        str = ColorCode.encode(str);

        try {
            writer.print(str);
            writer.flush();
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
    }

    public void println(String str) {
        str = ColorCode.encode(str);

        try {
            writer.println(str + "\r");
            writer.flush();
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
    }

    public void println() {
        try {
            writer.println();
            writer.flush();
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
    }

    /**
     * @return the writer
     */
    public PrintWriter getWriter() {
        return writer;
    }

    /**
     * @param aWriter the writer to set
     */
    public void setWriter(PrintWriter aWriter) {
        writer = aWriter;
    }
}
