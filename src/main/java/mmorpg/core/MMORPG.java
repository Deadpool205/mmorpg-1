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
package mmorpg.core;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.io.FileInputStream;


import mmorpg.util.Command;
import mmorpg.room.Room;
import java.util.HashMap;
import java.util.Properties;
import java.util.Timer;
import mmorpg.model.Entity;
import mmorpg.model.items.Item;
import mmorpg.model.living.Living;
import mmorpg.room.RoomLink;
import mmorpg.util.CustomClassLoader;

/**
 *
 * @author beecrofs
 */
public class MMORPG {

    public static Properties properties = new Properties();
    public static Room root = null;
    public static HashMap alias = new HashMap();
    public static HashMap users = new HashMap();
    public static HashMap rooms = new HashMap();
    public static double version = 1.12;
    public static Timer timer = new Timer();
    public static String path = "./build/classes/"; // configure for class loader.

    static {
        try {
            properties.load(new FileInputStream("./configure.properties"));
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        alias.put("D", "DOWN");
        alias.put("U", "UP");
        alias.put("E", "EAST");
        alias.put("N", "NORTH");
        alias.put("S", "SOUTH");
        alias.put("W", "WEST");
        alias.put("L", Command.LOOK.toString());
        alias.put("READ", Command.LOOK.toString());
        alias.put("INSPECT", Command.LOOK.toString());
        alias.put("I", Command.INVENTORY.toString());
        alias.put("K", Command.ATTACK.toString());
        alias.put("KILL", Command.ATTACK.toString());
        alias.put("R", Command.REPEAT.toString());
        alias.put("REMOVE", Command.UNWEAR.toString());
        alias.put("TAKEOFF", Command.UNWEAR.toString());

    }
    // This line must occur after the static initializer above.
    public static Database db = new Database();

    /*
     public Room build() {
     Citizen npc = new Citizen();
     npc.setName("Sandra");
     npc.add(new GoldCoin());
     npc.setGender(Gender.FEMALE);
     npc.setDescription("She is in good shape");

     Citizen barry = new Citizen();
     barry.setName("Barry");
     barry.setDescription("This is Barry. He is in good shape. Barry is better than he was yesterday.");
     barry.setGender(Gender.MALE);
     barry.addDialog("Sean is my newphew.");
     barry.addDialog("I love my new room, I can see the ocean.");
     barry.addDialog("One! - Hah, just kidding..");

     Room room = new Room();
     room.setName("Small white room");
     room.setDescription("This is a small white room. Four walls and a white floor. The room has a small door. White ceiling.");
     room.add(new RedHerb());
     room.add(new Underwear());
     room.add(new PurplePoison());
     room.add(new LeatherGloves());
     room.add(new Hammer());
     room.add(new GoldRing());
     room.add(new SurvivalKnife());
     room.add(new Glasses());
     room.add(new RolexWatch());

     room.add(new ButterKnife());

     Room r2 = new Room();
     r2.setDescription("This is an empty, barren room.");
     r2.add(new GoldCoin());
     r2.setName("Dead End");
     r2.add(new SurvivalKnife());
     r2.add("door", room);

     Room r3 = new Room();
     r3.setDescription("This is great room with beautiful hardwood floors and delicate white painted trim. The room has a large bay window directly overlooking the Ocean.");
     r3.add(new GoldCoin());
     r3.setName("Room with a View");
     r3.add(new Pizza());
     r3.add(new Pop());
     r3.add(new ChickenWing());
     r3.add("archway", room);

     Room mr = new Room();
     mr.setName("Mindy's Room");
     mr.setDescription("You are in Mindys room. Mindys room has beautiful high ceilings and a nice window overlooking the Ocean. You feel relaxed, and calm when you think about this room.");
     mr.add(new RolexWatch());
     mr.add(new GoldCoin());
     mr.add(new Dress());
     mr.add(new Tomato());
     mr.add(new Tomato());
     mr.add(new Desk());
     mr.add(new Computer());
     mr.add(new PottedPlant());
     mr.add("south", room);

     Room sr = new Room();
     sr.setName("Sean's room");
     sr.add(new Cheese());
     sr.add(new Suit());
     sr.add(new ChickenDrumstick());
     sr.add(new ChickenWing());
     sr.add(new ChickenWing());
     sr.add(new Heiniken());
     sr.add(new Wine());
     sr.setDescription("You are in Sean's room. This room is on top of the main room below. This room has a nice view of the Ocean. You can hear the waves crashing into the beach in the distance.");

     Butler npc3 = new Butler();
     npc3.setName("Butler");
     npc3.setDescription("This is Sean's butler.");

     sr.add(npc3);
     sr.add("ladder", room);

     room.add("ladder", sr);
     room.add("door", r2);
     room.add("north", mr);
     room.add("archway", r3);
     room.add(npc);
     r3.add(barry);

     return room;
     }
     */
    private static String getTagValue(String sTag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();

        Node nValue = (Node) nlList.item(0);

        return nValue.getNodeValue();
    }

    public Room build(String file) {
        Room r = new Room();
        try {
            File fXmlFile = new File(file);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            String rid = doc.getDocumentElement().getAttribute("id");
            int irid = Integer.parseInt(rid);
            r.setId(irid);
            NodeList nList = doc.getDocumentElement().getChildNodes();
            System.out.println("-----------------------");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    if (eElement.getNodeName().equals("title")) {
                        String title = eElement.getTextContent();
                        r.setName(title);
                        System.out.println("Added title:" + title);
                    } else if (eElement.getNodeName().equals("description")) {
                        String desc = eElement.getTextContent();
                        r.setDescription(desc);
                        System.out.println("Added desc:" + desc);
                    } else if (eElement.getNodeName().equals("exit")) {
                        String id = eElement.getAttribute("id");
                        String label = eElement.getAttribute("label");
                        String key = eElement.getAttribute("key");
                        // we need a lazy room reference resolver.
                        int _id = Integer.parseInt(id);
                        RoomLink link = new RoomLink(label, _id);
                        if (key != null && !key.equals("")) {
                            System.out.println("Trying to loading key:" + key);
                            CustomClassLoader loader = new CustomClassLoader(link.getClass().getClassLoader(), MMORPG.path);
                            Class c = loader.loadClass(key);
                            link.setKey((Item) c.newInstance());
                            link.setLocked(true);
                        }

                        r.add(label, link);
                        System.out.println("Added exit:" + label + " " + _id);
                    } else if (eElement.getNodeName().equals("entity")) {
                        String clazz = eElement.getAttribute("class");
                        try {
                            String fclazz = clazz.replaceAll("\\.", "/");
                            System.out.println("Trying to load:" + fclazz);
                            CustomClassLoader loader = new CustomClassLoader(clazz.getClass().getClassLoader(), MMORPG.path);
                            Class c = loader.loadClass(clazz);

                            if (c != null) {
                                Entity ent = (Entity) c.newInstance();
                                if (ent instanceof Living) {
                                    String name = eElement.getAttribute("name");
                                    ent.setName(name);
                                }
                                r.add(ent);
                            } else {
                                System.out.println("c was null");
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return r;
    }
}
