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
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;
import mmorpg.model.Entity;
import mmorpg.model.living.Living;
import mmorpg.room.Room;
import mmorpg.util.ColorCode;

/**
 *
 * @author beecrofs
 */
public class Broadcast {

    public static void send(String message, Room r) {
        // message = ColorEncode.encode(message);

        Entity[] ents = r.getEntities();
        for (int i = 0; i < ents.length; i++) {
            if (ents[i] instanceof Living) {
                send(message, (Living) ents[i]);
            }
        }
    }

    public static void send(String message, Living sender, Level target) {
        /*
         if( target != Level.ROOM){
         message = ColorEncode.encode(message);
         }
         */

        if (target == Level.ROOM) {
            send(message, sender.getRoom());
        } else if (target == Level.GAME) {
            Set set = MMORPG.users.keySet();
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                String k = (String) itr.next();
                Living l = (Living) MMORPG.users.get(k);
                if (!l.isSleeping() && !l.isDead()) {
                    send(message, l);
                }
            }
        } else if (target == Level.PLAYER) {
            send(message, sender);
        }
    }

    public static void send(String message, Living l) {
        Console wr = l.getLog();
        wr.println(message);
    }
}
