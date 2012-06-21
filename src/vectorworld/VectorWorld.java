/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vectorworld;

import java.awt.image.BufferedImage;

/**
 *
 * @author sanotehu
 */
public class VectorWorld {
        private static volatile ImageLoader ilo;
    private static volatile BufferedImage oImg;
    private static int PANEL_WIDTH = 800;
    private static int PANEL_HEIGHT = 600;
    private static volatile GameFrame frame;
    private static volatile GamePanel pan;
    public static boolean DEBUG_LOAD_PUZZ = false;
    public static boolean DEBUG_OLAP = false;
    public static boolean DEBUG_GFX = false;
    public static boolean DEBUG_LOAD_IMG = false;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            ilo = new ImageLoader("sprites.txt");
            ilo.ready();
            oImg = new BufferedImage(PANEL_WIDTH, PANEL_HEIGHT,
                    BufferedImage.TYPE_INT_RGB);
            pan = new GamePanel();
            frame = new GameFrame();
            frame.setVisible(true);
        } catch (Exception ex) {
            System.err.println(ex);
            System.exit(1);
        }
    }
    
    public static ImageLoader getImageLoader() {
        return ilo;
    }
    
    public static BufferedImage getOffscreenImage() {
        return oImg;
    }
    
    public static GameFrame getGameFrame() {
        return frame;
    }
    
    public static GamePanel getGamePanel() {
        return pan;
    }
}
