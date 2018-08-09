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
package mmorpg.model.living;

import mmorpg.model.Entity;
import mmorpg.model.items.Elixer;
import mmorpg.model.items.clothing.FurPelt;
import mmorpg.model.items.food.Heart;
import mmorpg.model.items.food.Meat;
import mmorpg.room.Room;

/**
 *
 * @author beecrofs
 */
public class Wolf extends Beast {

    public Wolf() {
        super();
        setName("Wolf");
        setDescription("This is a large grey wolf.");
        addHandle("Wolf");
        getHealth().setValue(1500);
        add(new FurPelt());
        add(new Elixer());
        add(new FurPelt());
        add(new Meat());
        add(new Meat());
        add(new Meat());
        add(new Heart());
        
        
    }

    @Override
    public void think() {
        if (!isFighting()) {
            doAttack();
        }
    }

    public void doAttack() {
        Room r = getRoom();
        Entity[] ents = r.getEntities();
        for (int i = 0; i < ents.length; i++) {
            if (ents[i] instanceof Player) {
                Living living = (Living) ents[i];
                if (!living.isDead()) {
                    attack(ents[i].getName());
                }
            }
        }
    }
}
