/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * GamePanel.java
 *
 * Created on Feb 9, 2012, 11:16:11 AM
 */
package planargame;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author zachary
 */
public final class GamePanel extends javax.swing.JPanel implements Runnable,
        ImageObserver {

    public int overlaps;
    public Point olapCounterPos, nextButtonPos, puzzNamePos;
    public String VERTEX_SPRITE = "orb";
    public static final Color BGCOLOR = Color.white;
    private int PWIDTH, PHEIGHT;
    private Thread animator;
    private volatile boolean running = false;
    private volatile boolean gameOver = false;
    private ImageLoader loader;
    private Vertex context, grabbed;
    private boolean grabbing = false;
    private volatile Puzzle puzz;
    private ColorModel cm;
    private BufferedImage oImg;
    private Graphics2D offscreen;
    private Graphics paint;
    private int puzzNumber = 1;
    private boolean goNext = false;

    public GamePanel(ImageLoader ilo, BufferedImage oImg) {
        loader = ilo;
        this.setBackground(BGCOLOR);
        context = new Vertex(loader, this);
        if (this.oImg == null && PlanarFrame.DEBUG_GFX) {
            System.err.println("oImg is null after being created?!");
        }
        try {
            puzz = new Puzzle(context, puzzNumber);
        } catch (PuzzleException ex) {
            System.err.println("Couldn't load puzzle number "
                    + puzzNumber + ".\n" + ex);
        }
        this.overlaps = puzz.countOverlaps();
        this.PWIDTH = puzz.getWidth();
        this.PHEIGHT = puzz.getHeight();
        this.olapCounterPos = new Point(PWIDTH / 2, PHEIGHT - 20);
        this.nextButtonPos = new Point(PWIDTH - 90, PHEIGHT - 90);
        this.puzzNamePos = new Point(PWIDTH / 2, 20);
        this.cm = ilo.getColorModel();
        this.setPreferredSize(puzz.preferredDimension);
        this.oImg = new BufferedImage(PWIDTH, PHEIGHT,
                BufferedImage.TYPE_BYTE_INDEXED);
        PlanarMouseListener l = new PlanarMouseListener(this);
        this.addMouseListener(l);
        this.addMouseMotionListener((MouseMotionListener) l);
        this.initComponents();
        this.startGame();
    }
    
    private void gameUpdate() {
        if (!gameOver) {
            try {
                this.overlaps = puzz.countOverlaps();
            } catch (IllegalArgumentException ex) {
                System.err.println(ex);
            }
            if (this.goNext) {
                try {
                    puzz = new Puzzle(context, puzzNumber);
                    goNext = false;
                } catch (NullPointerException ex) {
                    System.out.println("Good game!");
                    gameOver = true;
                } catch (PuzzleException ex) {
                    System.err.println(ex);
                    System.exit(1);
                }
            }
        } else {
            System.exit(0);
        }
    }
    
    private void gameRender() throws CanvasException {
        if (oImg == null) {
            if (PlanarFrame.DEBUG_GFX) {
                System.err.println("oImg is null");
            }
            throw new CanvasException("oImg is null");
        }
        oImg.flush();
        offscreen = oImg.createGraphics();
        if (offscreen == null) {
            if (PlanarFrame.DEBUG_GFX) {
                System.err.println("offscreen is null");
            }
            throw new CanvasException("offscreen is null");
        }

        drawBackground(offscreen);
        drawEdges(offscreen);
        drawVerts(offscreen);
        if(PlanarFrame.DEBUG_OLAP) {
            drawOlapCounter(offscreen);
        }
        drawNextButton(offscreen);
        drawPuzzleName(offscreen, puzz.getName());

        offscreen.dispose();
    }
    
    private void drawBackground(Graphics2D gfx) {
        gfx.setColor(Color.white);
        gfx.fillRect(0, 0, PWIDTH, PHEIGHT);
        gfx.setColor(Color.BLACK);
        if(loader.hasSprite(puzz.getBackground())) {
            BufferedImage background = loader.getSprite(puzz.getBackground());
            gfx.drawImage(background, 0, 0, this);
        }
    }
    
    public Puzzle getPuzzle() {
        return puzz;
    }

    public void dragVert(MouseEvent e) {
        if (!grabbing) {
            this.detectGrab(e);
        }
        if (grabbing) {
            // Move the grabbed vertex to the location of the mouse pointer,
            // then nudge it up and left so that the pointer's on the same
            // spot on the vertex as it was when the player started 
            // dragging it.
            Point draggedTo = e.getPoint();
            draggedTo.x -= grabbed.grabPoint.x;
            draggedTo.y -= grabbed.grabPoint.y;
            grabbed.moveTo(draggedTo);
        }
    }

    public void click(MouseEvent e) {
        Point p = e.getPoint();
        if (p.x > this.nextButtonPos.x) {
            if (p.x < this.nextButtonPos.x + 64) {
                if (p.y > this.nextButtonPos.y) {
                    if (p.y < this.nextButtonPos.y + 64) {
                        if (this.overlaps <= puzz.getGoal()) {
                            puzzNumber++;
                            goNext = true;
                        }
                    }
                }
            }
        }
    }

    /** Creates new form GamePanel */


    public void detectGrab(MouseEvent e) {
        Vertex v;
        Point p = e.getPoint();
        LinkedList<Vertex> unchecked;
        unchecked = new LinkedList<Vertex>(puzz.getVertCol());
        LinkedList<Vertex> checked = new LinkedList<Vertex>();
        while(!unchecked.isEmpty()) {
            v = unchecked.remove();
            if(checked.contains(v)) continue;
            checked.add(v);
            if(v.containsPoint(p)) {
                this.grabbing = true;
                v.grabPoint.x = p.x - v.getLeftEdge();
                v.grabPoint.y = p.x - v.getTopEdge();
                this.grabbed = v;
            }
        }
        /*
        for (int i = 0; i < numVerts; i++) {
            v = puzz.getVert(i);
            Point p = e.getPoint();
            if (v.containsPoint(p)) {
                grabbing = true;
                v.grabPoint.x = p.x - v.getLeftEdge();
                v.grabPoint.y = p.y - v.getTopEdge();
                grabbed = v;
            }
        }
         * 
         */
    }

    public void releaseGrab(MouseEvent e) {
        grabbing = false;
    }

    /*
     * Now I have the information I need to update the game state. 
     * I just need to update it.
     */
    public void paintScreen() {
        try {
            if (!this.isDisplayable()) {
                return;
            }
            paint = this.getGraphics();
            Toolkit.getDefaultToolkit().sync();
            if (PlanarFrame.DEBUG_GFX) {
                if (paint == null || oImg == null) {
                    if (paint == null) {
                        System.err.println("No graphics context for panel");
                    }
                    if (oImg == null) {
                        System.err.println("No offscreen image");
                    }
                }
            }
            paint.drawImage(oImg, 0, 0, this);
            paint.dispose();
        } catch (Exception e) {
            System.err.println("In paintScreen:");
            System.err.println(e);
            System.exit(1);
        }
    }

    @Override
    public void addNotify() {
        //Wait for the JPanel to be added to the JFrame/JApplet before starting.
        super.addNotify(); //creates the peer
        startGame(); //starts the thread
    }

    public void startGame() {
        if (animator == null || !running) {
            animator = new Thread(this);
            animator.start();
        }
    }

    public void stopGame() {
        running = false;
    }

    @Override
    public void run() {
        running = true;


        while (running) {
            gameUpdate();
            try {
                gameRender();
            } catch (Exception ex) {
                System.err.println("Error while rendering:\n" + ex);
            }
            try {
                paintScreen();
            } catch (Exception ex) {
                System.err.println("Error while drawing:\n" + ex);
            }
        }



        System.exit(0);
    }

    
    
    private void drawPuzzleName(Graphics2D gfx, String name) {
        gfx.drawString(name, puzzNamePos.x, puzzNamePos.y);
    }

    private void drawNextButton(Graphics2D gfx) {
        Image button;
        if (this.overlaps <= puzz.getGoal()) {
            button = this.loader.getSprite("next");
        } else {
            button = this.loader.getSprite("next-inactive");
        }
        gfx.drawImage(button, this.nextButtonPos.x, this.nextButtonPos.y,
                this);

    }

    private void drawOlapCounter(Graphics2D gfx) {
        gfx.drawString(this.overlaps + " intersections, vs. goal " + puzz.getGoal(), olapCounterPos.x, olapCounterPos.y);
    }

    private void drawEdges(Graphics2D gfx) {
        int numEdges = puzz.numEdges();
        Edge e;
        if (PlanarFrame.DEBUG_GFX) {
            System.out.println("Drawing " + numEdges + " edges");
        }
        for (int i = 0; i < numEdges; i++) {
            e = puzz.getEdge(i);
            this.drawEdge(gfx, e);
        }
    }

    private void drawEdge(Graphics2D g, Edge e) {
        Point start, end;
        start = e.getStart().getCenter();
        end = e.getEnd().getCenter();
        if (PlanarFrame.DEBUG_GFX) {
            System.out.println("Drawing edge from (" + start.x
                    + "," + start.y + ") to (" + end.x + ","
                    + end.y + ").");

        }
        g.drawLine(start.x, start.y, end.x, end.y);
    }

    private void drawVerts(Graphics2D gfx) {
        int numVerts = puzz.numVerts();
        Vertex v;
        for (int i = 0; i < numVerts; i++) {
            v = puzz.getVert(i);
            this.drawVert(gfx, v);
        }
    }

    public void drawVert(Graphics2D gfx, Vertex v) {
        if (PlanarFrame.DEBUG_GFX) {
            System.out.println("Drawing vertex at (" + v.getx()
                    + "," + v.gety() + ")");
        }
        BufferedImage vs = loader.getSprite(VERTEX_SPRITE);

        gfx.drawImage(vs, v.getx(), v.gety(), v.getWidth(), v.getHeight(), this);


    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
