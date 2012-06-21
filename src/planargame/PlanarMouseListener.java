/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package planargame;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 *
 * @author sanotehu
 */
public class PlanarMouseListener implements MouseListener, MouseMotionListener {

    private GamePanel pan;

    PlanarMouseListener(GamePanel p) {
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
        pan.click(e);
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
