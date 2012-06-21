/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vectorworld;

import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.awt.geom.Line2D;

/**
 *
 * @author zachary
 */
public class Edge {

    private Vertex start, end;

    public Edge(Vertex a, Vertex b) {
        start = a;
        end = b;
    }

    public Polygon getShape() {
        int[] xes = new int[2];
        xes[0] = start.centerX();
        xes[1] = end.centerX();

        int[] ys = new int[2];
        xes[0] = start.centerY();
        xes[1] = end.centerY();

        return new Polygon(xes, ys, 2);
    }

    public Vertex lowerVertex() {
        if (start.gety() > end.gety()) {
            return start;
        } else {
            return end;
        }
    }

    public Vertex higherVertex() {
        if (start.gety() < end.gety()) {
            return start;
        } else {
            return end;
        }
    }

    public Vertex righterVertex() {
        if (start.getx() > end.getx()) {
            return start;
        } else {
            return end;
        }
    }

    public Vertex lefterVertex() {
        if (start.getx() < end.getx()) {
            return start;
        } else {
            return end;
        }
    }

    public Vertex getStart() {
        return start;
    }

    public Vertex getEnd() {
        return end;
    }

    public int getLength() {
        Vertex higher = higherVertex();
        Vertex lower = lowerVertex();
        Vertex righter = righterVertex();
        Vertex lefter = lefterVertex();
        int a = lower.gety() - higher.gety();
        int b = righter.getx() - lefter.getx();
        int csquared = a * a + b * b;
        return (int) Math.sqrt((float) csquared);
    }

    public int yOf(int x) throws IllegalArgumentException {
        //trivial rejections
        if (x < this.lefterVertex().centerX()
                || x > this.righterVertex().centerX()) {
            throw new IllegalArgumentException("x out of range: "
                    + x);
        }
        if (this.isVertical()) {
            throw new IllegalArgumentException("Vertical lines have too many "
                    + "ys.");
        }

        //I can find the y of my endpoints with the best of them
        if (x == this.getStart().centerX()) {
            return this.getStart().centerY();
        }
        if (x == this.getEnd().centerX()) {
            return this.getEnd().centerY();
        }

        //What's my slope?
        double m = this.getStart().centerY() / this.getEnd().centerY();

        //Start at the start vertex and move toward the end some ways.
        int y;
        //If the end is higher than the start, I'll be moving upward.
        if (this.getEnd().isAbove(this.getStart())) {
            try {
                y = (int) Math.round(this.getStart().centerY() - (m * x));
            } catch (ArithmeticException ex) {
                throw new IllegalArgumentException(ex);
            }
        } else {
            //The end is lower than the start. Move downward.
            try {
                y = (int) Math.round(this.getStart().centerY() + (m * x));
            } catch (ArithmeticException ex) {
                throw new IllegalArgumentException(ex);
            }
        }
        //Java's coordinate system uses only positive numbers.
        if (y < 0) {
            throw new IllegalArgumentException("Oh dear, somehow you've "
                    + "gotten all the way down here with an x that doesn't "
                    + "give a legitimate y.");
        }
        return y;
    }
    
    public double getSlope() throws ArithmeticException {
        if(isVertical()) {
            throw new ArithmeticException("I'm vertical.");
        }
        double y1, y2, m;
        int x1, x2;
        x1 = this.lefterVertex().centerX();
        x2 = this.righterVertex().centerX();
        y1 = this.lefterVertex().centerY();
        y2 = this.righterVertex().centerY();
        m = (y2 - y1)/(x2 - x1);
        return m;
    }

    public boolean hasX(int x) {
        return x > this.lefterVertex().centerX()
                && x < this.righterVertex().centerX();
    }
    
    public boolean xInInterval(int l, int r) {
        int lx = this.lefterVertex().centerX();
        int rx = this.righterVertex().centerX();
        //If either of my endpoints have x in the interval,
        //then so do I.
        if(lx > l && lx < r) return true;
        if(rx > l && rx < r) return true;
        //If both my endpoints are outside the interval, part of me is
        //inside it.
        if(lx < l && rx > r) return true;
        //Otherwise, I'm off to one side.
        return false;
    }
    
    public boolean yInInterval(int t, int b) {
        int ty = this.higherVertex().centerX();
        int by = this.lowerVertex().centerX();
        if(ty > t && ty < b) return true;
        if(by > t && by < b) return true;
        if(ty < t && by > b) return true;
        return false;
    }
    
    public boolean hasY(int y) {
        return y < this.higherVertex().centerY()
                && y > this.lowerVertex().centerY();
    }

    public boolean isVertical() {
        int x2 = this.righterVertex().centerX();
        int x1 = this.lefterVertex().centerX();
        return (x2 - x1) == 0;
    }

    public boolean intersects(Edge that) {
        // This function is always returning false. It shouldn't.
        // trivial rejections
        if (that == null) {
            return false;
        }
        if (that.higherVertex().isBelow(this.lowerVertex())) {
            return false;
        }
        if (that.lowerVertex().isAbove(this.higherVertex())) {
            return false;
        }
        if (that.lefterVertex().isRightOf(this.righterVertex())) {
            return false;
        }
        if (that.righterVertex().isLeftOf(this.lefterVertex())) {
            return false;
        }
        double x1, y1, x2, y2, x3, y3, x4, y4;
        x1 = (double) this.start.centerX();
        y1 = (double) this.start.centerY();
        x2 = (double) this.end.centerX();
        y2 = (double) this.end.centerY();
        x3 = (double) that.getStart().centerX();
        y3 = (double) that.getStart().centerY();
        x4 = (double) that.getEnd().centerX();
        y4 = (double) that.getEnd().centerY();
        return Line2D.linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4);
    }

    public boolean overlapsAny(ArrayList<Edge> them) {
        if (VectorWorld.DEBUG_OLAP) {
            System.out.println("Does the edge from "
                    + this.getStart().getName() + " to "
                    + this.getEnd().getName() + "overlap any of the others?");
        }
        Edge that;
        for (int i = 0; i < them.size(); i++) {
            that = them.get(i);
            if (that == null) {
                continue;
            }
            if (VectorWorld.DEBUG_OLAP) {
                System.out.println("Does it overlap the edge from "
                        + that.getStart().getName() + " to "
                        + that.getEnd().getName() + "?");
            }
            if (this.intersects(that)) {
                if (VectorWorld.DEBUG_OLAP) {
                    System.out.println("Yes, it does!");
                }
                return true;
            } else if (VectorWorld.DEBUG_OLAP) {
                System.out.println("No, it doesn't.");
            }

        }
        if (VectorWorld.DEBUG_OLAP) {
            System.out.println("I didn't find any overlaps.");
        }
        return false;
    }

    public int countOverlapsIn(ArrayList<Edge> them) {
        int overlaps = 0;
        for (int i = 0; i < them.size(); i++) {
            if (this.intersects(them.get(i))) {
                overlaps++;
            }
        }
        return overlaps;
    }

    public ArrayList<Edge> allOverlapsIn(ArrayList<Edge> them) {
        ArrayList<Edge> results = new ArrayList<Edge>();
        for (int i = 0; i < them.size(); i++) {
            if (this.intersects(them.get(i))) {
                results.add(them.get(i));
            }
        }
        return results;
    }
}
