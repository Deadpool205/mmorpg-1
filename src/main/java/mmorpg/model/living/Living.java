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

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import mmorpg.core.Broadcast;
import mmorpg.core.Console;
import mmorpg.util.Level;
import mmorpg.util.Command;
import mmorpg.model.Entity;
import mmorpg.util.Gender;
import mmorpg.core.MMORPG;
import mmorpg.model.items.Chest;
import mmorpg.model.items.clothing.Clothing;
import mmorpg.model.items.Item;
import mmorpg.model.items.food.Drink;
import mmorpg.model.items.food.Food;
import mmorpg.model.items.properties.Immortal;
import mmorpg.model.items.properties.Immovable;
import mmorpg.model.items.properties.InfinitelyUsable;
import mmorpg.model.items.weapons.Weapon;
import mmorpg.room.Room;
import mmorpg.room.RoomLink;
import mmorpg.util.EntityComparator;
import mmorpg.util.Heartbeat;
import mmorpg.util.Inflector;
import mmorpg.util.Role;
import mmorpg.util.Statistic;
import mmorpg.util.Util;

public abstract class Living extends Entity implements DeathListener {

    private boolean fighting = false;
    private Living opponent = null;
    ArrayList deathListeners = new ArrayList();
    DecimalFormat df = new DecimalFormat("0.00");
    Stack history = new Stack();
    private String performance;
    private boolean performing = false;
    private Statistic defense = new Statistic("defense", 0, 100);
    private Statistic speed = new Statistic("speed", 0, 100);
    private Statistic wisdom = new Statistic("wisdom", 0, 100);
    private Statistic experience = new Statistic("experience", 0, 100);
    private Statistic intelligence = new Statistic("intelligence", 0, 100);
    private Statistic strength = new Statistic("strength", 0, 100);
    private Statistic health = new Statistic("health", 0, 100);
    private Statistic energy = new Statistic("energy", 0, 100);
    private Statistic hunger = new Statistic("hunger", 0, 100);
    private double balance;
    private boolean dead = false;
    private boolean sleeping = false;
    private Gender gender = Gender.MALE;
    protected HashMap inventory = new HashMap();
    protected ArrayList clothes = new ArrayList();
    private Weapon weapon = null;
    private Socket socket = null;
    private Console log = new Console(this);
    Heartbeat heartbeat = null;
    private boolean admin = false;
    private String role = "";

    public void addDeathListener(DeathListener listener) {
        if (!deathListeners.contains(listener)) {
            deathListeners.add(listener);
        }
    }

    public void removeDeathListener(DeathListener listener) {
        deathListeners.remove(listener);
    }

    // Anonymous instance initializer
    {
        try {
            heartbeat = new Heartbeat(this);
            MMORPG.timer.scheduleAtFixedRate(heartbeat, 0, 300);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Living() {
    }

    public void died(Living ent) {
        try {
            if (isDead()) {
                return;
            }

            double b = ent.getBalance();
            ent.withdraw(b);
            log.println("You got " + df.format(b) + " credits from the " + ent.getLongName());
            deposit(b);
            Item[] items = ent.getInventory();
            Broadcast.send("", this, Level.ROOM);
            for (int i = 0; i < items.length; i++) {
                add(items[i]);
                ent.remove(items[i]);
                Broadcast.send(getName() + " got a " + items[i].getName() + " from the " + ent.getLongName(), this, Level.ROOM);
            }
            Broadcast.send("", this, Level.ROOM);
            ent.removeDeathListener(this);
        } catch (Exception ex) {
        }
    }

    /*
     * I had to disable this for a short while; working on sql. 
     * will be good to reintroduce colour.
     * 
     public String getName() {
     return "<@name " + super.getName() + " @>";
     }
     */
    /**
     *
     * @param addColorTag Whether to surround the name with color code tags
     * @return the name in chosen format
     * @Override public String getName(boolean addColorTag){ if(addColorTag){
     * return "<@name " + super.getName() + " @>"; } return super.getName(); }
     */
    public String getLongName() {
        Socket s = getSocket();
        if (s != null) {
            if (!s.isConnected()) {
                return "ghost of " + getName();
            }
        }
        if (isDead()) {
            return "corpse of " + getName();
        } else if (isSleeping()) {
            return getName() + " is sleeping";
        } else if (isPerforming()) {
            return getName() + " is " + performance;
        } else {
            return getName();
        }
    }

    public abstract void think();
    private long idle = System.currentTimeMillis();

    public void process(String command, String what) {
        setIdle(System.currentTimeMillis());

        if (!command.equals(Command.REPEAT.toString())) {
            history.push(command + " " + what);
        }

        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        } else if (command.equals("")) {
            return;
        }

        command = command.trim();
        what = what.trim();

        if (isSleeping()) {
            if (command.equalsIgnoreCase(Command.WAKE.toString())) {
                wake();
            } else if (command.equalsIgnoreCase(Command.HELP.toString())) {
            } else {
                log.println("Zzz... ");
                return;
            }
        }
        if (isPerforming()) {
            if (command.equalsIgnoreCase(Command.STOP.toString())
                    || command.equalsIgnoreCase(Command.PERFORM.toString())) {
                stopPerforming();
            } else if (command.equalsIgnoreCase(Command.HELP.toString())) {
            } else {
                log.println("Too busy " + performance + " to " + command + ".");
                return;
            }
        }

        if (isDead()) {
            log.println("You cannot do that because you're dead.");
            return;
        } else if (command.equals(Command.BROADCAST.toString())) {
            broadcast(what);
        } else if (command.equals(Command.ATTACK.toString())) {
            attack(what);
        } else if (command.equals(Command.DROP.toString())) {
            drop(what);
        } else if (command.equals(Command.UNLOCK.toString())) {
            unlock(what);
        } else if (command.equals(Command.LOCK.toString())) {
            lock(what);
        } else if (command.equals(Command.GET.toString())) {
            get(what);
        } else if (command.equals(Command.USE.toString())) {
            use(what);
        } else if (command.equals(Command.EAT.toString())) {
            eat(what);
        } else if (command.equals(Command.DRINK.toString())) {
            drink(what);
        } else if (command.equals(Command.LOOK.toString())) {
            look(what);
        } else if (command.equals(Command.SHUTDOWN.toString())) {
            shutdown();
        } else if (command.equals(Command.INVENTORY.toString())) {
            inventory();
        } else if (command.equals(Command.STATS.toString())) {
            stats();
        } else if (command.equals(Command.SAY.toString())) {
            say(what);
        } else if (command.equals(Command.COMMAND.toString())) {
            command(what);
        } else if (command.equals(Command.GIVE.toString())) {
            give(what);
        } else if (command.equals(Command.REPEAT.toString())) {
            repeat();
        } else if (command.equals(Command.WEAR.toString())) {
            wear(what);
        } else if (command.equals(Command.UNWEAR.toString())) {
            unwear(what);
        } else if (command.equals(Command.WIELD.toString())) {
            wield(what);
        } else if (command.equals(Command.UNWIELD.toString())) {
            unwield(what);
        } else if (command.equals(Command.UNWIELD.toString())) {
            unwield(what);
        } else if (command.equals(Command.HELP.toString())) {
            help(what);
        } else if (command.equals(Command.TRANSPORT.toString())) {
            transport(what);
        } else if (command.equals(Command.WHO.toString())) {
            who();
        } else if (command.equals(Command.DEPOSIT.toString())) {
            deposit(what);
        } else if (command.equals(Command.WITHDRAW.toString())) {
            withdraw(what);
        } else if (command.equals(Command.SLEEP.toString())) {
            sleep();
        } else if (command.equals(Command.WAKE.toString())) {
            //...
        } else if (command.equals(Command.SAVE.toString())) {
            save();
        } else if (command.equals(Command.EMOTE.toString())) {
            emote(what);
        } else if (command.equals(Command.PERFORM.toString())) {
            perform(what);
        } else if (command.equals(Command.STOP.toString())) {
            stopPerforming();
        } else {

            // try to exit room!
            if (!tryExit(command)) {
                log.println("What? Command not recognized, try 'help'.");
            }
        }
    }

    public void shutdown() {
        try {
            if (hasAccess()) {
                Broadcast.send("", this, Level.ROOM);
                Broadcast.send("System will SHUTDOWN NOW...", this, Level.GAME);
                Broadcast.send("", this, Level.ROOM);
                System.exit(1);
            }
        } catch (Exception ex) {
        }
    }

    public void broadcast(String what) {
        Broadcast.send("", this, Level.ROOM);
        Broadcast.send("+mmorpg:" + this.getName() + ":" + what, this, Level.GAME);
        Broadcast.send("", this, Level.ROOM);
    }

    public void transport(String what) {
        try {
            if (hasAccess()) {
                int id = Integer.parseInt(what);
                String label = "magic";
                RoomLink link = new RoomLink(label, id);
                tryLink(link, label);
                Broadcast.send("", this, Level.ROOM);
                Broadcast.send(getName() + " disappears suddenly in a cloud of smoke.", this, Level.ROOM);
                Broadcast.send("", this, Level.ROOM);
                return;
            }
            Broadcast.send("", this, Level.ROOM);
            Broadcast.send(getName() + " tried to do some magic but failed.", this, Level.ROOM);
            Broadcast.send("", this, Level.ROOM);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void lock(String what) {
        Room r = getRoom();
        HashMap map = r.getExits();

        try {
            Item item = (Item) r.find(what);
            if (item != null && item instanceof Chest) {
                Chest c = (Chest) item;
                c.lock(this);
                return;
            }
        } catch (Exception ex) {
        }

        String str = what.toLowerCase();
        RoomLink nRoomLink = (RoomLink) map.get(str);
        Item k = nRoomLink.getKey();
        if (find(k.getName()) != null) {
            nRoomLink.setLocked(true);
            Broadcast.send("", this, Level.ROOM);
            Broadcast.send(getName() + " locks " + str, this, Level.ROOM);
            Broadcast.send("", this, Level.ROOM);
        } else {
            log.println("You don't have that key");
        }

    }

    public void unlock(String what) {
        Room r = getRoom();
        HashMap map = r.getExits();
        String str = what.toLowerCase();

        try {
            Item item = (Item) r.find(str);
            if (item != null && item instanceof Chest) {
                Chest c = (Chest) item;
                c.unlock(this);
                return;
            }
        } catch (Exception ex) {
        }

        RoomLink nRoomLink = (RoomLink) map.get(str);
        Item k = nRoomLink.getKey();

        if (find(k.getName()) != null) {
            nRoomLink.setLocked(false);
            Broadcast.send("", this, Level.ROOM);
            Broadcast.send(getName() + " unlocks " + str, this, Level.ROOM);
            Broadcast.send("", this, Level.ROOM);
        } else {
            log.println("You don't have that key");
        }

    }

    public boolean tryExit(String command) {
        Room r = getRoom();
        HashMap map = r.getExits();
        Set set = map.keySet();
        Iterator itr = set.iterator();
        while (itr.hasNext()) {
            String str = (String) itr.next();
            String letter = "" + command.charAt(0);
            letter = letter.toLowerCase();
            if (str.equalsIgnoreCase(command) || letter.equalsIgnoreCase(command)) {
                RoomLink nRoomLink = (RoomLink) map.get(str);
                tryLink(nRoomLink, str);
                return true;
            }
        }
        return false;
    }

    public void tryLink(RoomLink nRoomLink, String str) {
        Room nRoom = nRoomLink.getRoom();
        if( nRoom == null ){
            log.println("Unknown destination.");            
            return;
        }
        if (getCapacity() < 0) {
            log.println("You're carrying too much.");
            return;
        }
        if (getEnergy().getValue() == 0) {
            log.println("You're too tired.");
            return;
        }

        if (!nRoomLink.isLocked()) {
            Broadcast.send("", this, Level.ROOM);
            Broadcast.send(getName() + " goes " + str, this, Level.ROOM);
            Broadcast.send("", this, Level.ROOM);
            Room oldRoom = getRoom();
            deathListeners.clear();
            setFighting(false);
            setOpponent(null);
            setRoom(nRoom);
            oldRoom.remove(this);
            nRoom.add(this);
            getEnergy().decrement();
            Broadcast.send("", this, Level.ROOM);
            Broadcast.send(getName() + " enters the room.", this, Level.ROOM);
            Broadcast.send("", this, Level.ROOM);
            process(Command.LOOK.toString(), "");
        } else {
            Broadcast.send("", this, Level.ROOM);
            Broadcast.send(getName() + " tries to go " + str + " but fails because this exit is locked.", this, Level.ROOM);
            Broadcast.send("", this, Level.ROOM);
        }
    }

    public void sleep() {
        Broadcast.send("", this, Level.ROOM);
        Broadcast.send(getName() + " goes to sleep.", this, Level.ROOM);
        Broadcast.send("", this, Level.ROOM);
        setSleeping(true);
    }

    public void emote(String what) {
        Broadcast.send("", this, Level.ROOM);
        Broadcast.send(getName() + " " + what, this, Level.ROOM);
        Broadcast.send("", this, Level.ROOM);
    }

    public void wake() {
        Broadcast.send("", this, Level.ROOM);
        Broadcast.send(getName() + " awakens.", this, Level.ROOM);
        Broadcast.send("", this, Level.ROOM);
        setSleeping(false);
    }

    public void save() {
        log.println("Saving player...");
        MMORPG.db.save((Player) this);
        log.println("Done.");
    }

    private void perform(String what) {
        Broadcast.send("", this, Level.ROOM);
        Broadcast.send(getName() + " started " + what, this, Level.ROOM);
        Broadcast.send("", this, Level.ROOM);
        setPerformance(what);
    }

    private void stopPerforming() {
        if (isPerforming()) {
            Broadcast.send(getName() + " stopped " + performance, this, Level.ROOM);
            setPerformance("");
        }
    }

    public void who() {
        Set set = MMORPG.users.keySet();
        Iterator itr = set.iterator();
        while (itr.hasNext()) {
            String name = (String) itr.next();
            Player p = (Player) MMORPG.users.get(name);
            Socket soc = p.getSocket();
            String msg = p.getName() + " : socket : " + soc.getInetAddress().toString() + " : is connected : " + soc.isConnected();
            System.out.println(msg);
            log.println(msg);
        }
    }

    public void help(String what) {
        log.println("Command   |         Usage / Function ");
        log.println("--------------------------");
        log.println("help      |         Display this message");
        log.println("look      |         look <item/person/thing> View the thing you want to see");
        log.println("kill      |         kill <person> Attack someone");
        log.println("get       |         get <what> Pick an item up");
        log.println("drop      |         drop <what> Drop an item");
        log.println("wear      |         wear <what> Wear an item");
        log.println("unwear    |         unwear <what> Take an item off");
        log.println("wield     |         wield <what> Wield an weapon");
        log.println("unwield   |         unwield <what> Unwield an weapon");
        log.println("withdraw  |         withdraw <amount> <bitcoin address>");
        log.println("deposit   |         deposit to an internal wallet address");
        log.println("say       |         say <what> Say something to people in the room");
        log.println("give      |         give <what> to <who> Give something to people in the room");
        log.println("sleep     |         Goto sleep");
        log.println("wake      |         Awaken");
        log.println("eat       |         eat <what> Eat an item");
        log.println("drink     |         drink <what> Drink an item");
        log.println("stats     |         Display your stats");
        log.println("lock      |         lock <exit> lock an exit");
        log.println("unlock    |         unlock <exit> unlock an exit");
        log.println("inventory |         Display your inventory");
        log.println("emote     |         emote <what> Display an emote");
        log.println("broadcast |         broadcast <message> broadcast a channel message");

        log.println("--------------------------");
        log.println("Admin Functions");
        log.println("command   |         command <who> <what>");
        log.println("shutdown  |         Shutdown the server");
    }

    public Clothing[] getClothing() {
        Clothing[] list = new Clothing[clothes.size()];
        for (int i = 0; i < clothes.size(); i++) {
            list[i] = (Clothing) clothes.get(i);
        }
        return list;
    }

    public void wear(String what) {
        try {
            if (what.equalsIgnoreCase("all")) {
                Item[] list = getInventory();
                for (int i = 0; i < list.length; i++) {
                    if (list[i] instanceof Clothing) {
                        wear(list[i].getName());
                    }
                }
                return;
            }
            Clothing c = (Clothing) find(what);
            remove((Item) c);
            Broadcast.send("", this, Level.ROOM);
            Broadcast.send(getName() + " wears a " + c.getName(), this, Level.ROOM);;
            Broadcast.send("", this, Level.ROOM);
            clothes.add(c);
        } catch (Exception ex) {
            log.println(getName() + " was unable to wear " + what);
        }
    }
    
    

    public void wield(String what) {
        try {
            Weapon w = (Weapon) find(what);
            if (getWeapon() != null) {
                log.println(getName() + " is already wielding a weapon, unwield first");
            } else {
                remove((Item) w);
                setWeapon(w);
                Broadcast.send("", this, Level.ROOM);
                Broadcast.send(getName() + " wields a " + getWeapon().getName(), this, Level.ROOM);
                Broadcast.send("", this, Level.ROOM);
            }
        } catch (Exception ex) {
            log.println("You were unable to unwield that");
        }

    }

    public void unwield(String what) {
        try {
            Weapon w = (Weapon) find(what);
            if (getWeapon() == null) {
                log.println(getName() + " is not wielding anything");
            } else {
                Broadcast.send("", this, Level.ROOM);
                Broadcast.send(getName() + " unwields a " + getWeapon().getName(), this, Level.ROOM);
                Broadcast.send("", this, Level.ROOM);
                add(getWeapon());
                setWeapon(null);
            }
        } catch (Exception ex) {
            log.println("You were unable to unwield a " + getWeapon().getName());
        }
    }

    public void unwearAll() {
        Clothing c = null;
        for (int i = 0; i < clothes.size(); i++) {
            Clothing cstr = (Clothing) clothes.get(i);
            unwear(cstr.getName());
        }
    }

    public void unwear(String what) {
        Clothing c = null;
        if (what.equalsIgnoreCase("all")) {
            unwearAll();
            return;
        }
        for (int i = 0; i < clothes.size(); i++) {
            Clothing cstr = (Clothing) clothes.get(i);
            if (cstr.getName().equalsIgnoreCase(what)) {
                clothes.remove(cstr);
                c = cstr;
                break;
            }
        }
        add((Item) c);
        Broadcast.send("", this, Level.ROOM);
        Broadcast.send(getName() + " takes off the " + what, this, Level.ROOM);
        Broadcast.send("", this, Level.ROOM);
    }

    public void repeat() {
        String command = (String) history.peek();
        String original = command;
        command = command.toUpperCase();
        String[] words = command.split(" ");
        String cmd = words[0];
        String k = (String) MMORPG.alias.get(cmd);
        if (k != null) {
            cmd = k;
        }
        String what = original.substring(words[0].length(), command.length());
        what = what.trim();
        process(cmd, what);
    }

    public void give(String what) {
        // find what,
        try {
            String[] w = what.split(" ");
            what = what.toUpperCase();
            String cs = " TO ";
            int indx = what.indexOf(cs);
            String itemName = what.substring(0, indx);
            String who = what.substring(indx + cs.length(), what.length());
            Item i = find(itemName);

            Entity e = getRoom().find(who);
            Living l = (Living) e;
            l.add(i);
            remove(i);
            Broadcast.send("", this, Level.ROOM);
            Broadcast.send(getName() + " gave " + l.getName() + " a " + i.getName(), this, Level.ROOM);
            Broadcast.send("", this, Level.ROOM);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // find who
        // give who what
    }

    public void stats() {
        log.println("Health:            " + df.format(getHealth().getValue()) + "/" + df.format(getHealth().getMax()));
        log.println("Experience:        " + df.format(getExperience().getValue()));
        log.println("Balance:           " + df.format(getBalance()) + "c");
        log.println("Strength:          " + df.format(getStrength().getValue()) + "/" + df.format(getStrength().getMax()));
        log.println("Intelligence:      " + df.format(getIntelligence().getValue()) + "/" + df.format(getIntelligence().getMax()));
        log.println("Speed:             " + df.format(getSpeed().getValue()) + "/" + df.format(getSpeed().getMax()));
        log.println("Defense:           " + df.format(getDefense().getValue()) + "/" + df.format(getDefense().getMax()));
        log.println("Wisdom:            " + df.format(getWisdom().getValue()) + "/" + df.format(getWisdom().getMax()));
        log.println("Energy:            " + df.format(getEnergy().getValue()) + "/" + df.format(getEnergy().getMax()));
        log.println("Hunger:            " + df.format(getHunger().getValue()) + "/" + df.format(getHunger().getMax()));
        log.println("Weight:            " + df.format(super.getMass() / 1000) + "kg");
        log.println("You are carrying:  " + df.format(getEncumbrance()) + "kg");
        log.println("You could carry:   " + df.format(getCapacity()) + "kg in additional weight");
    }

    public double getCapacity() {
        return (getStrength().getValue() - getEncumbrance());
    }

    public double getEncumbrance() {
        return (getMass() - super.getMass()) / 1000;
    }

    public void die() {
        if (this instanceof Immortal) {
            getHealth().setValue(getHealth().getMax());
            return;
        }
        if (isDead()) {
            return;
        }

        Broadcast.send("", this, Level.ROOM);
        Broadcast.send(getName() + " drops dead.", this, Level.ROOM);
        Broadcast.send("", this, Level.ROOM);
        getHealth().setValue(getHealth().getMin());
        setDead(true);

        for (int i = 0; i < deathListeners.size(); i++) {
            DeathListener listener = (DeathListener) deathListeners.get(i);
            listener.died(this);
        }

        deathListeners.clear();

    }

    public boolean hasAccess() {
        try {
            System.out.println("Checking Role:" + getRole());
            if (getRole() != null && getRole().equals("ADMIN")) {
                System.out.println("true!");
                return true;
            } else {
                log.println("Access Denied");
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;

    }

    public void command(String what) {
        if (hasAccess()) {
            String[] words = what.split(" ");
            String who = words[0];
            Entity ent = getRoom().find(who);
            if (ent instanceof Living) {
                try {
                    Living living = (Living) ent;
                    String command = what.substring(who.length(), what.length());
                    String[] w = command.split(" ");
                    String left = command.substring(w[0].length(), command.length());
                    left = left.toUpperCase();
                    left = left.trim();
                    w = left.split(" ");

                    String _what = "";
                    for (int i = 1; i < w.length; i++) {
                        _what += w[i] + " ";
                    }
                    living.process(w[0], _what);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void drop(String what) {
        if (what == null) {
            log.println("Drop what?");
            return;
        }
        if (what.equalsIgnoreCase("all")) {
            Entity[] list = getInventory();
            for (int i = 0; i < list.length; i++) {
                if (list[i] instanceof Item) {
                    Entity ent = list[i];
                    inventory.remove(ent.hashCode());
                    getRoom().add(ent);
                    Broadcast.send("", this, Level.ROOM);
                    Broadcast.send(getName() + " drops a " + ent.getName(), this, Level.ROOM);
                    Broadcast.send("", this, Level.ROOM);
                }
            }
            return;
        }
        //log.println(getName() + " try to drop:" + what);
        Item ent = find(what);
        if (ent == null) {
            log.println("You don't have a " + what);
            return;
        }
        inventory.remove(ent.hashCode());
        getRoom().add(ent);
        Broadcast.send("", this, Level.ROOM);
        Broadcast.send(getName() + " drops a " + ent.getName(), this, Level.ROOM);
        Broadcast.send("", this, Level.ROOM);

    }

    public void get(String what) {
        //log.println(getName() + " try to get:" + what);


        if (what.startsWith("all")) {
            Entity[] list = getRoom().getEntities();
            for (int i = 0; i < list.length; i++) {
                if (list[i] instanceof Item && !(list[i] instanceof Immovable)) {
                    Entity ent = list[i];
                    inventory.put(ent.hashCode(), ent);
                    getRoom().remove(ent);
                    Broadcast.send(getName() + " got a " + ent.getName(), this, Level.ROOM);
                } else if (list[i] instanceof Chest) {
                    try {
                        Chest c = (Chest) list[i];
                        Item[] _list = c.getAll(this);
                        if (_list != null) {
                            for (int itr = 0; itr < _list.length; itr++) {
                                Item item = (Item) _list[itr];
                                Broadcast.send(getName() + " got a " + item.getName() + " from the " + list[i].getName(), this, Level.ROOM);
                                add(item);
                                c.remove(item);
                            }
                        } else {
                            if (_list.length == 0) {
                                log.println("This chest is empty.");
                            } else {
                                log.println("This chest is locked.");
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
                //else {
                //log.println("You can't get a " + list[i].getName());
                //}
            }
            return;
        }

        Entity ent = getRoom().find(what);
        if (ent instanceof Living) {
            log.println(getName() + " cannot get a living thing");
        } else if (ent instanceof Item && !(ent instanceof Immovable)) {
            inventory.put(ent.hashCode(), ent);
            getRoom().remove(ent);
            Broadcast.send(getName() + " got a " + ent.getName(), this, Level.ROOM);
        }

    }

    public void say(String what) {

        Broadcast.send("", this, Level.ROOM);
        Broadcast.send(getName() + " says <@message '" + what + "' @>", this, Level.ROOM);
        Broadcast.send("", this, Level.ROOM);
    }

    public void attack(String who) {
        Entity ent = getRoom().find(who);
        if (getEnergy().getValue() == 0) {
            log.println("You're too tired.");
        }
        if (ent instanceof Immortal) {
            Broadcast.send("", this, Level.ROOM);
            Broadcast.send(getName() + " tries to attack " + ent.getName() + " but fails.", this, Level.ROOM);
            Broadcast.send("", this, Level.ROOM);
            return;
        }
        if (ent instanceof Living) {
            Living living = (Living) ent;
            living.setFighting(true);
            living.setOpponent(this);
            living.addDeathListener(this);

            if (!living.isDead()) {
                double damage = 0;
                if (getWeapon() != null) {
                    damage = getWeapon().getDamage() + (getStrength().getValue());
                } else {
                    damage = getStrength().getValue() * Math.random();
                }
                living.getHealth().setValue(living.getHealth().getValue() - damage);
                DecimalFormat df = new DecimalFormat("0.00");
                getEnergy().decrement(); // change to a formula.

                if (getWeapon() != null) {
                    Broadcast.send("", this, Level.ROOM);
                    Broadcast.send(getName() + " attacks " + living.getName() + " with a " + getWeapon().getName() + " and " + getWeapon().getDamageWords() + " ", this, Level.ROOM);
                    Broadcast.send("", this, Level.ROOM);
                } else {
                    Broadcast.send("", this, Level.ROOM);
                    Broadcast.send(getName() + " attacks " + living.getName() + " and " + Util.toDamageWords(damage, this) + " " + living.getName() + ".", this, Level.ROOM);
                    Broadcast.send("", this, Level.ROOM);
                }
            } else {
                if (!(ent instanceof Citizen)) {
                    Broadcast.send("", this, Level.ROOM);
                    Broadcast.send(getName() + " attacked a corpse.", this, Level.ROOM);
                    Broadcast.send("", this, Level.ROOM);
                }
            }
        }
    }

    public void use(String what) {
        //log.println("You used a " + what);
        Item item = find(what);
        if (item != null) {
            item.use(this);
            if (!(item instanceof InfinitelyUsable)) {
                remove(item);
            }
        } else {
            log.println("You don't have a " + what);
        }
    }

    public void lookInventory(String what) {

        // not in the room, lets check inventory.
        Entity ent = find(what);
        if (ent == null) {
            log.println(getName() + " do not see that here");
        } else {
            lookEntity(ent);
        }

    }

    public void lookEntity(Entity ent) {
        log.println("You see a " + ent.getName());
        log.println(ent.getDescription());
        if (ent instanceof Living) {

            Living living = (Living) ent;
            if (living.isDead()) {
                log.println(living.getName() + " is dead! ");
            }

            Clothing[] list = living.getClothing();
            if (list.length == 0) {
                log.println(living.getName() + " is naked! ");
                //return;
            } else {
                log.print(living.getName() + " is wearing ");

                for (int i = 0; i < list.length; i++) {
                    log.print(list[i].getName() + ", ");
                }
            }
            if (living.getWeapon() == null) {
                log.print(living.getName() + " is not wielding anything ");
            } else {
                log.print(living.getName() + " is wielding " + living.getWeapon().getName());
            }

            log.println("");

        }
    }

    public void lookRoom(Room r) {
        log.println(r.getName());
        log.println(r.getDescription());
        Entity[] entities = r.getEntities();
        for (int i = 0; i < entities.length; i++) {
            Entity e = entities[i];
            if (e instanceof Item) {
                log.print(e.getName() + ", ");
            }
        }
        log.println("");
        for (int i = 0; i < entities.length; i++) {
            Entity e = entities[i];
            if (e instanceof Living) {
                Living l = (Living) e;
                log.print(l.getLongName() + ", ");
            }
        }
        log.println("");
        HashMap exits = r.getExits();
        Set set = exits.keySet();
        Iterator itr = set.iterator();
        log.print("Exits:");
        while (itr.hasNext()) {
            String key = (String) itr.next();
            RoomLink rr = (RoomLink) exits.get(key);
            log.print(key + ", ");
        }
        log.println("");

    }

    public void look(String what) {


        if (what != null && !what.equals("")) {
            if (what.equalsIgnoreCase("me")) {
                what = getName();
            }
            Entity ent = getRoom().find(what);
            if (ent == null) {
                lookInventory(what);
            } else {
                lookEntity(ent);
            }
        } else {
            lookRoom(getRoom());
        }
    }

    public Item find(String what) {
        Item[] list = getInventory();
        for (int i = 0; i < list.length; i++) {
            if (list[i].hasHandle(what)) {
                return list[i];
            }
        }
        return null;
    }

    public void eat(String what) {
        Item item = find(what);
        if (item != null) {
            if (item instanceof Food && !(item instanceof Drink)) {
                if (!(getHunger().getValue() >= getHunger().getMax())) {
                    remove(item);
                    item.use(this);

                    Broadcast.send("", this, Level.ROOM);
                    Broadcast.send(getName() + " ate a " + item.getName(), this, Level.ROOM);
                    Broadcast.send("", this, Level.ROOM);
                } else {
                    log.println("You can't eat that, you're full");
                }
            } else {
                log.println("You can't eat that.");
            }
        } else {
            log.println(getName() + " don't have a " + what);
        }
    }

    public void drink(String what) {
        Item item = find(what);
        if (item != null) {
            if ((item instanceof Drink)) {
                remove(item);
                item.use(this);
                Broadcast.send("", this, Level.ROOM);
                Broadcast.send(getName() + " drank a " + item.getName(), this, Level.ROOM);
                Broadcast.send("", this, Level.ROOM);
            } else {
                log.println("You can't eat that.");
            }
        } else {
            log.println(getName() + " don't have a " + what);
        }
    }

    public void inventory() {
        Item[] list = getInventory();
        if (list.length == 0) {
            log.println("You are not carrying anything");
            return;
        }

        Arrays.sort(list, new EntityComparator());

        log.println("You are carrying:");
        int cnt = 1;
        String oName = "";
        try { 
        for (int i = 0; i < list.length; i++) {
            String name = list[i].getName();

            if (name.equals(oName)) {
                cnt++;
            } else {
                Inflector inflector = Inflector.getInstance();
                if (!oName.equals("")) {
                    log.println(Util.capitalize(Util.convert((long) cnt)) + " " + inflector.pluralize(oName, cnt) + ", ");
                }
                cnt = 1;
                oName = name;
            }
        }
        } catch(Exception ex) { 
            ex.printStackTrace();
        }
    }

    public Item[] getInventory() {
        Set set = inventory.keySet();
        Item[] list = new Item[inventory.size()];
        Iterator i = set.iterator();
        int ctr = 0;
        while (i.hasNext()) {
            Integer k = (Integer) i.next();
            Item item = (Item) inventory.get(k);
            list[ctr++] = item;
        }
        return list;
    }

    public void add(Item item) {
        inventory.put(item.hashCode(), item);
    }

    public void remove(Item item) {
        inventory.remove(item.hashCode());
    }

    /**
     * @return the balance
     */
    public double getBalance() {
        return balance;
    }

    public void deposit(String what) {
        log.println("Deposit is not presently working.");
        log.println("Please send donations to: 1C4jMt2Xt8z9tZf4je39DPmx3mrTjYKHZi");
        log.println("This will help speed up the process as it will motivate us!");
    }

    public void withdraw(String what) {
        String[] words = what.split(" ");
        String amount = words[0];
        String address = words[1];
        try {
            Double damount = Double.parseDouble(amount);
            try {
                withdraw(damount);
                FileOutputStream fos = new FileOutputStream(new File("./transactions.pending"), true);
                PrintWriter writer = new PrintWriter(fos);
                Date d = new Date(System.currentTimeMillis());
                writer.println(address + "," + damount + "," + getName() + "," + d.toString());
                writer.flush();
                writer.close();

                log.println("You withdrew " + amount + " credits and sent them to " + address + " bitcoin address.");
                log.println("Transactions may take up to 24 hours.");

            } catch (InsufficientFundingException ex) {
                log.println("withdrawl unsuccessful, insufficient funds.");
            } catch (InvalidAmountException ex) {
                log.println("invalid amount.");
            } catch (Exception ex) {
                log.println("withdrawl unsuccessful.");
            }

        } catch (Exception ex) {
        }
    }

    protected void deposit(double amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException();
        }
        setBalance(getBalance() + amount);
    }

    protected void withdraw(double amount) throws InsufficientFundingException, InvalidAmountException {
        if ((getBalance() - amount) < 0) {
            throw new InsufficientFundingException();
        }
        if (amount <= 0) {
            throw new InvalidAmountException();
        }
        setBalance(getBalance() - amount);
    }

    /**
     * @param inventory the inventory to set
     */
    public void setInventory(HashMap inventory) {
        this.inventory = inventory;
    }

    /**
     * @return the dead
     */
    public boolean isDead() {
        return dead;
    }

    /**
     * @param dead the dead to set
     */
    public void setDead(boolean dead) {
        this.dead = dead;
    }

    /**
     * @return the gender
     */
    public Gender getGender() {
        return gender;
    }

    /**
     * @param gender the gender to set
     */
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    /**
     * @return the weapon
     */
    public Weapon getWeapon() {
        return weapon;
    }

    /**
     * @param weapon the weapon to set
     */
    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    /**
     * @return the log
     */
    public Console getLog() {
        return log;
    }

    /**
     * @param log the log to set
     */
    public void setLog(Console log) {
        this.log = log;
    }

    /**
     * @return the sleeping
     */
    public boolean isSleeping() {
        return sleeping;
    }

    /**
     * @param sleeping the sleeping to set
     */
    public void setSleeping(boolean sleeping) {
        this.sleeping = sleeping;
    }

    public void setPerformance(String performance) {
        this.performance = performance;
        this.performing = (performance.length() > 0);
    }

    public String getPerformance() {
        return performance;
    }

    public void setPerforming(boolean performing) {
        this.performing = performing;
    }

    public boolean isPerforming() {
        return performing;
    }

    /**
     * @return the socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * @param socket the socket to set
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    /**
     * @return the fighting
     */
    public boolean isFighting() {
        return fighting;
    }

    /**
     * @param fighting the fighting to set
     */
    public void setFighting(boolean fighting) {
        this.fighting = fighting;
    }

    /**
     * @return the opponent
     */
    public Living getOpponent() {
        return opponent;
    }

    /**
     * @param opponent the opponent to set
     */
    public void setOpponent(Living opponent) {
        this.opponent = opponent;
    }

    /**
     * @return the defense
     */
    public Statistic getDefense() {
        return defense;
    }

    /**
     * @param defense the defense to set
     */
    public void setDefense(Statistic defense) {
        this.defense = defense;
    }

    /**
     * @return the speed
     */
    public Statistic getSpeed() {
        return speed;
    }

    /**
     * @param speed the speed to set
     */
    public void setSpeed(Statistic speed) {
        this.speed = speed;
    }

    /**
     * @return the wisdom
     */
    public Statistic getWisdom() {
        return wisdom;
    }

    /**
     * @param wisdom the wisdom to set
     */
    public void setWisdom(Statistic wisdom) {
        this.wisdom = wisdom;
    }

    /**
     * @return the experience
     */
    public Statistic getExperience() {
        return experience;
    }

    /**
     * @param experience the experience to set
     */
    public void setExperience(Statistic experience) {
        this.experience = experience;
    }

    /**
     * @return the intelligence
     */
    public Statistic getIntelligence() {
        return intelligence;
    }

    /**
     * @param intelligence the intelligence to set
     */
    public void setIntelligence(Statistic intelligence) {
        this.intelligence = intelligence;
    }

    /**
     * @return the strength
     */
    public Statistic getStrength() {
        return strength;
    }

    /**
     * @param strength the strength to set
     */
    public void setStrength(Statistic strength) {
        this.strength = strength;
    }

    /**
     * @return the health
     */
    public Statistic getHealth() {
        return health;
    }

    /**
     * @param health the health to set
     */
    public void setHealth(Statistic health) {
        this.health = health;
    }

    /**
     * @return the energy
     */
    public Statistic getEnergy() {
        return energy;
    }

    /**
     * @param energy the energy to set
     */
    public void setEnergy(Statistic energy) {
        this.energy = energy;
    }

    /**
     * @return the hunger
     */
    public Statistic getHunger() {
        return hunger;
    }

    /**
     * @param hunger the hunger to set
     */
    public void setHunger(Statistic hunger) {
        this.hunger = hunger;
    }

    /**
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(String role) {
        System.out.println("setRole:" + role + ":");

        this.role = role;
    }

    /**
     * @param balance the balance to set
     */
    public void setBalance(double balance) {
        this.balance = balance;
    }

    /**
     * @return the idle
     */
    public long getIdle() {
        return idle;
    }

    /**
     * @param idle the idle to set
     */
    public void setIdle(long idle) {
        this.idle = idle;
    }

    public double getMass() {
        double m = super.getMass();
        Item[] items = getInventory();
        for (int i = 0; i < items.length; i++) {
            m += items[i].getMass();
        }
        return m;
    }
}
