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
package mmorpg.util;

import mmorpg.model.living.Living;
import java.util.TimerTask;
import mmorpg.model.living.Player;

public class Heartbeat extends TimerTask {

    long sleepCheck = 0;
    long clockCheck = 0;
    long lastRun = System.currentTimeMillis();
    long hungerCheck = 0;
    Living m = null;

    public Heartbeat(Living m) {
        this.m = m;
    }

    @Override
    public void run() {
        try {
            lastRun = System.currentTimeMillis();
            m.think();
            if (m.getHealth().getValue() <= m.getHealth().getMin() && m.isDead() == false) {
                m.die();
            }

            if (m.getHunger().getValue() <= 0) {
                m.die();
            }

            if (System.currentTimeMillis() - clockCheck > Util.THREE_MINUTES) {
                m.getHunger().decrement();
                clockCheck = System.currentTimeMillis();
            }

            if (m.isSleeping()) {
                if ((System.currentTimeMillis() - sleepCheck) > Util.THREE_SECONDS) {
                    m.getEnergy().increase(5);
                }
            }

            if (m.getHunger().getValue() <= (m.getHunger().getMax() / 30)) {
                if ((System.currentTimeMillis() - hungerCheck) > Util.ONE_MINUTE) {
                    hungerCheck = System.currentTimeMillis();
                    if (!m.isDead()) {
                        Player p = (Player) m;
                        if (p.isLoggedIn()) {
                            m.getLog().println("");
                            m.getLog().println("You're starving");
                            m.getLog().println("");
                        }
                    }
                }
            } else if (m.getHunger().getValue() <= (m.getHunger().getMax() / 10)) {
                if ((System.currentTimeMillis() - hungerCheck) > Util.THREE_MINUTES) {
                    hungerCheck = System.currentTimeMillis();
                    Player p = (Player) m;
                    if (p.isLoggedIn()) {
                        m.getLog().println("");
                        m.getLog().println("You're hungry");
                        m.getLog().println("");
                    }
                }
            }

            if ((System.currentTimeMillis() - m.getIdle()) > Util.TEN_MINUTES) {
                if (m instanceof Player) {
                    m.getLog().println("");
                    m.getLog().println("Idle too long... (Automatic logout)");
                    m.getLog().println("");
                    Util.logout((Player) m);
                }
            }

        } catch (Exception ex) {
        }

    }
}
