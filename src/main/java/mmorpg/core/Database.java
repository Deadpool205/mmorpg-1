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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import mmorpg.model.Entity;
import mmorpg.model.items.Item;
import mmorpg.model.living.Living;
import mmorpg.model.living.Player;
import mmorpg.util.CustomClassLoader;
import mmorpg.util.Gender;
import mmorpg.util.Util;

/**
 *
 * @author beecrofs
 */
public class Database {

    Connection connection = null;

    public void install(String createSQLFile) {
        try {
            FileInputStream fis = new FileInputStream(new File(createSQLFile));
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line = "";
            if( connection == null ){
                Database db = new Database();
                connection = db.getConnection();
            }
            Statement statement = connection.createStatement();
            while ((line = reader.readLine()) != null) {
                try {
                    System.out.println("Run:" + line);
                    statement.executeUpdate(line);
                    System.out.println("Successfully ran:" + line);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            System.out.println("Unable to install database.");
            System.out.println("Reason:");
            ex.printStackTrace();
        }
    }

    public Database() {
        try {
            String driver = MMORPG.properties.getProperty("driver");
            Class.forName(driver);
            System.out.println("Loaded driver successfully");
        } catch (Exception ex) {
        }
        connection = getConnection() ;
    }

    public Connection getConnection()
    {
        try {
            String connectionURL = MMORPG.properties.getProperty("connectionURL");
            String userid = MMORPG.properties.getProperty("userid");
            String password = MMORPG.properties.getProperty("password");

            connection = DriverManager.getConnection(connectionURL, userid, password);
            System.out.println("Established connection successfully...");
            return connection;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    public boolean exists(String userid) {
        try {
            userid = userid.toLowerCase();
            Statement statement = connection.createStatement();

            ResultSet r = statement.executeQuery("select userid from player where userid = '" + userid + "'");
            if (r.next()) {
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public Player load(String userid, String password) {
        try {
            Statement statement = connection.createStatement();
            Statement statement2 = connection.createStatement();

            String sql = "select * from player where userid = '" + userid + "' and password = '" + Util.encrypt(password) + "'";
            System.out.println(sql);
            ResultSet r = statement.executeQuery(sql);
            if (r.next()) {
                Player p = new Player();
                p.setName(r.getString("name"));
                p.setId(r.getInt("id"));
                String role = r.getString("mrole");
                p.setRole(role);
                p.setGender(Util.genderFromString(r.getString("gender")));
                p.setBalance(r.getDouble("balance"));
                p.setUserID(r.getString("userid"));
                p.setPassword(Util.decrypt(r.getString("password")));
                ResultSet r2 = statement2.executeQuery("select * from inventory where id = " + p.getId() + "");
                while (r2.next()) {
                    try {
                        String clazz = r2.getString("clazz");
                        CustomClassLoader loader = new CustomClassLoader(clazz.getClass().getClassLoader(), MMORPG.path);
                        Class c = loader.loadClass(clazz);
                        if (c != null) {
                            Item ent = (Item) c.newInstance();
                            p.add(ent);
                        } else {
                            System.out.println("clazz was null");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                return p;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();

        }
        return null;
    }

    public void save(Player ent) {

        if (!exists(ent.getUserID())) {
            try {
                // new player, lets try to save.
                Statement statement = connection.createStatement();
                String sql = "insert into player (userid, name, password, gender, balance, eckey) values ('" + 
                        ent.getUserID() + "', '" + 
                        ent.getName() + "', '" + 
                        Util.encrypt(ent.getPassword()) + "', '" + 
                        Util.toGenderChar(ent.getGender()) + "', " + 
                        ent.getBalance() + ", 'ECKEY')";
                System.out.println(sql);
                int m = statement.executeUpdate(sql);

                // okay - so lets bind the id.
                Statement statement2 = connection.createStatement();
                sql = "select * from player where userid = '" + ent.getUserID() + "'";
                ResultSet r = statement2.executeQuery(sql);
                if (r.next()) {
                    int id = r.getInt("id");
                    ent.setId(id);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            try {
                // lets try to save inventory.
                Statement statement = connection.createStatement();
                statement.executeUpdate("update player set balance=" + ent.getBalance() + ", mrole='" + ent.getRole() + "' where userid = '" + ent.getUserID() + "'");
                statement.executeUpdate("delete from inventory where id = " + ent.getId());
                Item[] list = ent.getInventory();
                for (int i = 0; i < list.length; i++) {
                    statement.executeUpdate("insert into inventory values (" + ent.getId() + ", '" + list[i].getClass().getCanonicalName() + "')");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
