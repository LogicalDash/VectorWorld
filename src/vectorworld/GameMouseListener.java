/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vectorworld;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 *
 * @author sanotehu
 */
public class GameMouseListener implements MouseListener, MouseMotionListener {

    private GamePanel pan;

    GameMouseListener(GamePanel p) {
        this.pan = p;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        pan.dragVert(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        pan.detectGrab(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        pan.releaseGrab(e);
    }
}
