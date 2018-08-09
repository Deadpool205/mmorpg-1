/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mmorpg.model.items.clothing;

import java.util.Date;
import mmorpg.model.living.Living;

/**
 *
 * @author beecrofs
 */
public class RolexWatch extends Clothing {

    public RolexWatch() {
        setName("Rolex Watch");
        addHandle("Rolex");
        addHandle("Watch");
        addHandle("Clock");
        addHandle("Rolex Watch");
    }

    @Override
    public String getDescription() {
        return "This is a beautiful, 22kt gold rolex.\n\rThe time says " + new Date(System.currentTimeMillis()).toString();
    }

    @Override
    public void use(Living entity) {
        entity.getLog().println(new Date(System.currentTimeMillis()).toString());
    }
}
