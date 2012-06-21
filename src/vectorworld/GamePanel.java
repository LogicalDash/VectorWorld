/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * GamePanel.java
 *
 * Created on Feb 9, 2012, 11:16:11 AM
 */
package vectorworld;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;

/**
 *
 * @author zachary
 */
public final class GamePanel extends javax.swing.JPanel implements Runnable,
        ImageObserver {

    public int PWIDTH = 500;
    public int PHEIGHT = 400;
    public int overlaps;
    public Point olapCounterPos;
    public String PUZZLE_NAME = "level1";
    public String VERTEX_SPRITE = "orb";
    public static final Color BGCOLOR = Color.white;
    private Thread animator;
    private volatile boolean running = false;
    private volatile boolean gameOver = false;
    private ImageLoader loader;
    private Vertex grabbed;
    private boolean grabbing = false;
    private volatile Puzzle puzz;
    private ColorModel cm;
    private BufferedImage oImg;
    private Graphics2D offscreen;
    private Graphics paint;

    private void gameUpdate() {
        if (!gameOver) {
            if(grabbing) {
                try {
                overlaps = puzz.countOverlaps();
                } catch(IllegalArgumentException ex) {
                    System.err.println(ex);
                }
            }
        }
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

    /** Creates new form GamePanel */
    public GamePanel() {
        loader = VectorWorld.getImageLoader();
        this.setBackground(BGCOLOR);
        if (this.oImg == null && VectorWorld.DEBUG_GFX) {
            System.err.println("oImg is null after being created?!");
        }
        try {
            puzz = new Puzzle(PUZZLE_NAME);
        } catch (PuzzleException ex) {
            System.err.println("Couldn't load the puzzle "
                    + PUZZLE_NAME + ".\n" + ex);
        }
        this.overlaps = puzz.countOverlaps();
        this.olapCounterPos = new Point(PWIDTH / 2, PHEIGHT - 20);
        this.PWIDTH = puzz.getWidth();
        this.PHEIGHT = puzz.getHeight();
        this.cm = loader.getColorModel();
        this.setPreferredSize(puzz.preferredDimension);
        this.oImg = VectorWorld.getOffscreenImage();
        GameMouseListener l = new GameMouseListener(this);
        this.addMouseListener(l);
        this.addMouseMotionListener((MouseMotionListener) l);
        this.initComponents();
        this.startGame();
    }

    public void detectGrab(MouseEvent e) {
        int numVerts = puzz.numVerts();
        Vertex v;
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
            if (VectorWorld.DEBUG_GFX) {
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

    private void gameRender() throws CanvasException {
        if (oImg == null) {
            if (VectorWorld.DEBUG_GFX) {
                System.err.println("oImg is null");
            }
            throw new CanvasException("oImg is null");
        }
        oImg.flush();
        offscreen = oImg.createGraphics();
        if (offscreen == null) {
            if (VectorWorld.DEBUG_GFX) {
                System.err.println("offscreen is null");
            }
            throw new CanvasException("offscreen is null");
        }
        offscreen.setColor(Color.white);
        offscreen.fillRect(0, 0, PWIDTH, PHEIGHT);
        offscreen.setColor(Color.BLACK);

        // TODO: draw graphics to represent game state
        drawEdges(offscreen, puzz);
        drawVerts(offscreen, puzz);
        drawOlapCounter(offscreen,puzz);

        offscreen.dispose();
    }
    
    private void drawOlapCounter(Graphics2D gfx, Puzzle puzz) {
        gfx.drawString(this.overlaps + " intersections", olapCounterPos.x, olapCounterPos.y);
    }

    private void drawEdges(Graphics2D gfx, Puzzle puzz) {
        int numEdges = puzz.numEdges();
        Edge e;
        if (VectorWorld.DEBUG_GFX) {
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
        if (VectorWorld.DEBUG_GFX) {
            System.out.println("Drawing edge from (" + start.x
                    + "," + start.y + ") to (" + end.x + ","
                    + end.y + ").");
            
        }
        g.drawLine(start.x, start.y, end.x, end.y);
    }

    private void drawVerts(Graphics2D gfx, Puzzle puzz) {
        int numVerts = puzz.numVerts();
        Vertex v;
        for (int i = 0; i < numVerts; i++) {
            v = puzz.getVert(i);
            this.drawVert(gfx, v);
        }
    }

    public void drawVert(Graphics2D gfx, Vertex v) {
        if (VectorWorld.DEBUG_GFX) {
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
