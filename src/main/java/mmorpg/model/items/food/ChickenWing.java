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
package mmorpg.model.items.food;

import mmorpg.model.living.Living;

/**
 *
 * @author beecrofs
 */
public class ChickenWing extends Food {

    public ChickenWing() {
        setName("Chicken Wing");
        addHandle("Chicken");
        addHandle("Chicken Wing");
        addHandle("Wing");
        addHandle("Meat");
        setMass(78.1);
        setDescription("This is a chicken wing, looks crispy, delicious, and lathered in hotsauce");
    }

    @Override
    public void use(Living entity) {
        entity.getHunger().increase(getNurishment().getValue());
        entity.getEnergy().increase(getNurishment().getValue());
    }
}
