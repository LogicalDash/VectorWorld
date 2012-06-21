/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vectorworld;

import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 *
 * @author zachary
 */
public class Vertex {

    private Point location;
    private String name;
    private String spriteName = "orb";
    private int width;
    private int height;
    private BufferedImage sprite = null;
    public Point grabPoint;
    // The grabPoint is the location at which the user has grabbed the vertex,
    // with respect to the location of the vertex--
    // that is, the upper-left corner of the sprite.

    
    private void init() {
        this.grabPoint = new Point();
        sprite = VectorWorld.getImageLoader().getSprite(spriteName);
        width = sprite.getWidth();
        height = sprite.getHeight();
    }
    
    Vertex() {
        init();
    }

    Vertex(int x, int y) {
        this.centerTo(x, y);
        init();
    }
    
    Vertex(int x, int y, String name) {
        this.centerTo(x, y);
        this.name = name;
        init();
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
        return width;
    }

    public int getHeight() {
        return height;
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
}
