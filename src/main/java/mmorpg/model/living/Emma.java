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

import mmorpg.model.items.GoldCoin;
import mmorpg.model.items.clothing.Dress;
import mmorpg.model.items.clothing.Suit;
import mmorpg.model.items.clothing.Tuxedo;
import mmorpg.model.items.clothing.Underwear;
import mmorpg.model.items.properties.Immortal;
import mmorpg.model.items.weapons.Axe;
import mmorpg.model.items.weapons.SurvivalKnife;

/**
 *
 * @author beecrofs
 */
public class Emma extends Citizen implements Immortal {

    public Emma() {
        addHandle("Robot");
        addHandle("Emma");
        addHandle("Robot Emma");
        addHandle("Daughter");
        this.add(new Dress());
        this.add(new GoldCoin());
        commonDialog.clear();
        addDialog("Ola");
        addDialog("Googoo ga ga");
    }
}
