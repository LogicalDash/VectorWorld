/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package planargame;

import java.awt.Point;
import javax.swing.JPanel;

/**
 *
 * @author zachary
 */
public final class Vertex {

    private Point location;
    private ImageLoader lo;
    private GamePanel pan;
    private Puzzle puzz = null;
    private String name = null;
    private String spriteName = "orb";
    private int weight = 1;
    public Point grabPoint;
    // The grabPoint is the location at which the user has grabbed the vertex,
    // with respect to the location of the vertex--
    // that is, the upper-left corner of the sprite.

    Vertex(ImageLoader lo, JPanel pan) {
        this.lo = lo;
        this.pan = (GamePanel) pan;
        this.validate();
    }

    Vertex(ImageLoader lo, GamePanel pan) {
        this.lo = lo;
        this.pan = pan;
        this.validate();
    }

    Vertex(ImageLoader lo, GamePanel pan, int x, int y) {
        this.lo = lo;
        this.pan = pan;
        this.centerTo(x, y);
        this.validate();
    }
    
        Vertex(ImageLoader lo, GamePanel pan, int x, int y, int w) {
        this.lo = lo;
        this.pan = pan;
        this.centerTo(x, y);
        this.weight = w;
        this.validate();
    }
    
    Vertex(ImageLoader lo, GamePanel pan, int x, int y, String name) {
        this.lo = lo;
        this.pan = pan;
        this.centerTo(x, y);
        this.name = name;
        this.validate();
    }
    
    Vertex(ImageLoader lo, GamePanel pan, int x, int y, int w, String name) {
        this.lo = lo;
        this.pan = pan;
        this.centerTo(x, y);
        this.name = name;
        this.weight = w;
        this.validate();
    }

    private void validate() {
        this.puzz = pan.getPuzzle();
        this.grabPoint = new Point();
        assert(this.location.x>=0);
        assert(this.location.y>=0);
        assert(this.weight>=1);
    }

    public Vertex make(int x, int y) {
        if (lo == null || pan == null) {
            String err = "I'm trying to make a vertex without knowing "
                    + "what image loader and gamepanel I'm using. This is "
                    + "stupid.\n";
            if (lo == null) {
                err += "no image loader\n";
            }
            if (pan == null) {
                err += "no gamepanel";
            }
            System.out.println(err);
        }
        return new Vertex(lo, pan, x, y);
    }
    
    public Vertex make(int x, int y, String name) {
        if (lo == null || pan == null) {
            String err = "I'm trying to make a vertex without knowing "
                    + "what image loader and gamepanel I'm using. This is "
                    + "stupid.\n";
            if (lo == null) {
                err += "no image loader\n";
            }
            if (pan == null) {
                err += "no gamepanel";
            }
            System.out.println(err);
        }
        return new Vertex(lo, pan, x, y, name);
    }
    
    public boolean isNamed(String name) {
        return name.compareTo(this.name) == 0;
    }

    public int getx() {
        return location.x;
    }

    public int gety() {
        return location.y;
    }

    public void setx(int x) {
        this.location.x = x;
    }

    public void sety(int y) {
        this.location.y = y;
    }

    public void moveTo(Point p) {
        setx(p.x);
        sety(p.y);
    }

    public Point getPos() {
        return new Point(getx(), gety());
    }

    public Point getCenter() {
        return new Point(getx() + getRadius(), gety() + getRadius());
    }
    
    public String getName() {
        return name;
    }

    public void centerTo(int x, int y) {
        if (location != null) {
            setx(x - getRadius());
            sety(y - getRadius());
        } else {
            location = new Point(x - getRadius(), y - getRadius());
        }
    }

    public int getWidth() {
        return lo.getSprite(spriteName).getWidth();
    }

    public int getHeight() {
        return lo.getSprite(spriteName).getWidth();
    }

    public int getRadius() {
        //Width and height SHOULD be the same...
        return getWidth() / 2;
    }

    public int getLeftEdge() {
        return this.getCenter().x - getRadius();
    }

    public int getRightEdge() {
        return this.getCenter().x + getRadius();
    }

    public int getTopEdge() {
        return this.getCenter().y - getRadius();
    }

    public int getBottomEdge() {
        return this.getCenter().y + getRadius();
    }

    public boolean containsPoint(Point p) {
        if (p.x > getLeftEdge()) {
            if (p.x < getRightEdge()) {
                if (p.y > getTopEdge()) {
                    if (p.y < getBottomEdge()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public int centerX() {
        return this.getCenter().x;
    }
    
    public int centerY() {
        return this.getCenter().y;
    }
    
    public boolean isAbove(Vertex other) {
        return this.getCenter().y < other.getCenter().y;
    }
    
    public boolean isBelow(Vertex other) {
        return this.getCenter().y > other.getCenter().y;
    }
    
    public boolean isLeftOf(Vertex other) {
        return this.getCenter().x < other.getCenter().x;
    }
    
    public boolean isRightOf(Vertex other) {
        return this.getCenter().x > other.getCenter().x;
    }
    
    public Puzzle getPuzzle() {
        return puzz;
    }
}
