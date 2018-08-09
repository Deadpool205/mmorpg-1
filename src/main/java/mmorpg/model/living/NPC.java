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

import java.util.ArrayList;
import mmorpg.model.living.Living;

public abstract class NPC extends Living {

    ArrayList fightingDialog = new ArrayList();
    ArrayList commonDialog = new ArrayList();

    public NPC() {
        super();
        try {
            deposit(Math.random() * 10);
        } catch (Exception ex) {
        }
        getExperience().setValue(100);
        getHealth().setValue(100);
        getStrength().setValue(10);
        getWisdom().setValue(10);
        getIntelligence().setValue(10);
        getSpeed().setValue(10);
    }

    public void addDialog(String str) {
        commonDialog.add(str);
    }

    public void addFightingDialog(String str) {
        fightingDialog.add(str);
    }

    @Override
    public void think() {
        try {
            double m = (Math.random() * 1000);

            if ((int) m == 50) {
                if (!isDead() && !isFighting()) {
                    if (commonDialog.size() != 0) {
                        int n = (int) (Math.random() * commonDialog.size());
                        say((String) commonDialog.get(n));
                    }

                }
            }

            if (isFighting() && !isDead() && m > 995) {
                int n = (int) (Math.random() * fightingDialog.size());
                if (fightingDialog.size() != 0) {
                    say((String) fightingDialog.get(n));
                }
            }

            if (((int) m) < 10) {
                if (isFighting() && !isDead()) {
                    Living o = getOpponent();
                    if (o != null) {
                        attack(o.getName());
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}