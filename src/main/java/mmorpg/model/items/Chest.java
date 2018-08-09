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

import java.util.ArrayList;
import mmorpg.core.Broadcast;
import mmorpg.core.Console;
import mmorpg.model.Entity;
import mmorpg.model.items.properties.Immovable;
import mmorpg.model.living.Living;
import mmorpg.model.living.Player;
import mmorpg.util.Level;

/**
 *
 * @author beecrofs
 */
public class Chest extends Item implements Immovable {

    ArrayList contents = new ArrayList();
    boolean locked = false;
    SilverKey key = new SilverKey();
    public Chest() {
        setName("Chest");
        addHandle("Chest");
        addHandle("Wooden Chest");
        setDescription("This is a wooden chest");
        for(int i = 0; i < 10; i++){
            contents.add(new GoldCoin());
        }   
    }
    
    @Override
    public String getDescription()
    {
        if( locked ){
            return "This is a wooden chest. This chest is locked";
        }
        else {
            String str = "This is a wooden chest. This chest contains:";
            for(int i = 0; i < contents.size(); i++){
                Item item = (Item)contents.get(i);
                str += item.getName() + ", ";
            }
            return str;
        }
    }
    
    public void unlock(Living ent)
    {
        SilverKey k = (SilverKey)ent.find("Silver Key");
        if( k instanceof SilverKey ){
            locked = false;
            Broadcast.send(ent.getName() + " unlocks " + getName(), ent, Level.ROOM);
                        
        } else {
            ent.getLog().println("You don't have the key for this chest");
        }
    }

    public void lock(Living ent)
    {
        SilverKey k = (SilverKey)ent.find("Silver Key");
        if( k instanceof SilverKey ){
            locked = true;
            Broadcast.send(ent.getName() + " locks " + getName(), ent, Level.ROOM);                        
        } else {
            ent.getLog().println("You don't have the key for this chest");
        }
    }
        
    public void add(Item item)
    {
        contents.add(item);
    }
    
    public void remove(Item what)
    {
        contents.remove(what);
    }
    
    public Item[] getAll(Living ent)
    {
        if( locked ){
            Console log = ent.getLog();
            log.println("This chest is locked.");            
            return null;
        }
        Item[] list = new Item[contents.size()];
        for(int i = 0; i < list.length; i++ ){
            list[i] = (Item)contents.get(i);
        }
        return list;
    }

    @Override
    public void use(Living entity) {
        
    }
}
