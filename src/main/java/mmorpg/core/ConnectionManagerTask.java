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

import java.util.Iterator;
import java.util.Set;
import java.util.TimerTask;
import mmorpg.model.living.Player;

/**
 *
 * @author beecrofs
 */
public class ConnectionManagerTask extends TimerTask {

    @Override
    public void run() {
        try {
            Set set = MMORPG.users.keySet();
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                String key = (String) itr.next();
                Player p = (Player) MMORPG.users.get(key);
                if (p.getSocket().isClosed()) {
                    //...
                    p.save();
                    Broadcast.send(p.getName() + " dissapeared with a *poof* of smoke!", p.getRoom());
                    p.getRoom().remove(p);
                    MMORPG.users.remove(p.getUserID());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
