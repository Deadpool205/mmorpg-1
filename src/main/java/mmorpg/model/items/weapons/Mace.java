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
package mmorpg.model.items.weapons;

import mmorpg.model.living.Living;

/**
 *
 * @author beecrofs
 */
public class Mace extends Weapon {

    public Mace() {
        setName("Spiked Mace");
        addHandle("Mace");
        addHandle("Spiked Mace");
        setDescription("This is a steel spiked mace on a chain with a wooden handle.");
        setDamage(420);
        setMass(1850);

    }

    @Override
    public void use(Living entity) {
    }

    @Override
    public String getDamageWords() {

        return (Math.random() > 0.5) ? "chops; blood gushes out" : "chops deep into the flesh; it sounds like a thud as blood squirts out.";
    }
}
