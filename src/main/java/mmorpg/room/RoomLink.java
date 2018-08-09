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
package mmorpg.room;

import mmorpg.core.MMORPG;
import mmorpg.model.items.Item;

/**
 *
 * @author beecrofs
 */
public class RoomLink {

    int id = 0;
    String label = null;
    private boolean locked = false;
    private Item key = null;

    public RoomLink(String label, int id) {
        this.label = label;
        this.id = id;
    }

    public Room getRoom() {

        return (Room) MMORPG.rooms.get(id);
    }

    /**
     * @return the locked
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * @param locked the locked to set
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    /**
     * @return the key
     */
    public Item getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(Item key) {
        this.key = key;
    }
}
