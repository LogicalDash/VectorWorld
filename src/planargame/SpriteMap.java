/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package planargame;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author sanotehu
 */
public class SpriteMap extends HashMap {


    @Override
    public Object put(Object key, Object value) {
        if(value.getClass().equals(BufferedImage.class)) {
            return super.put(key, value);
        }
        else return null;
    }

    @Override
    public void putAll(Map m) {
        super.putAll(m);
    }
    
}
