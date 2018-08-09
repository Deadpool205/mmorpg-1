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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import mmorpg.model.Entity;

public class Room {

    private int id;
    private String name;
    private String description;
    private double length, width, height;
    private HashMap entities = new HashMap();
    private HashMap exits = new HashMap();

    public Entity find(String what) {
        if (what == null) {
            return null;
        }
        Entity[] list = getEntities();
        for (int i = 0; i < list.length; i++) {
            if (list[i] != null) {
                if (list[i].hasHandle(what)) {
                    return list[i];
                }
            }
        }
        return null;
    }

    public void add(String exit, RoomLink link) {
        exits.put(exit, link);
    }

    public void add(Entity ent) {
        ent.setRoom(this);
        entities.put(ent.hashCode(), ent);

    }

    public void remove(Entity ent) {
        entities.remove(ent.hashCode());

    }

    public Entity[] getEntities() {
        Set set = entities.keySet();
        Entity[] list = new Entity[entities.size()];
        Iterator i = set.iterator();
        int ctr = 0;
        while (i.hasNext()) {
            Integer k = (Integer) i.next();
            Entity ent = (Entity) entities.get(k);
            list[ctr++] = ent;
        }
        return list;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the length
     */
    public double getLength() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(double length) {
        this.length = length;
    }

    /**
     * @return the width
     */
    public double getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(double width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public double getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * @param entities the entities to set
     */
    public void setEntities(HashMap entities) {
        this.entities = entities;
    }

    /**
     * @return the exits
     */
    public HashMap getExits() {
        return exits;
    }

    /**
     * @param exits the exits to set
     */
    public void setExits(HashMap exits) {
        this.exits = exits;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
}
