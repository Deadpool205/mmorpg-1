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

import mmorpg.model.items.clothing.Suit;

/**
 *
 * @author beecrofs
 */
public class Citizen extends NPC {

    public Citizen() {
        super();
        addHandle("person");
        addDialog("Hell of a day isn't it?");
        addDialog("I heard there is big changes coming down the pike...");
        addDialog("Rumour has it Sean is going to add money to this place.");
        addDialog("If you're here, it's probably because you're a programmer, or you know Sean.");
        addFightingDialog("Meet my fist.");
        addFightingDialog("Here it comes!");
        addFightingDialog("You're going to regret ever stepping into this place!");
        addFightingDialog("I'm going to rip you a new asphalt!");
    }
}
