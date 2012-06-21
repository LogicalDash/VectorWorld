/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PlanarFrame.java
 *
 * Created on Mar 8, 2012, 11:20:36 AM
 */
package planargame;

import java.awt.image.BufferedImage;

/**
 *
 * @author zachary
 */
public class PlanarFrame extends javax.swing.JFrame {
    public static PlanarFrame frame;
    public volatile ImageLoader ilo;
    public static final boolean DEBUG_GFX = false;
    public static final boolean DEBUG_LOAD_PUZZ = false;
    public static final boolean DEBUG_LOAD_IMG = false;
    public static final boolean DEBUG_OLAP = false;
    public volatile BufferedImage oImg;

    /** Creates new form PlanarFrame */
    public PlanarFrame() {
        try {
            ilo = new ImageLoader("sprites.txt");
            ilo.ready();
            oImg = new BufferedImage(WIDTH, HEIGHT,
                    BufferedImage.TYPE_INT_RGB);
        } catch (Exception ex) {
            System.err.print(ex);
            System.exit(1);
        }
        initComponents();
    }
    
    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new GamePanel(this.ilo, this.oImg);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(new java.awt.BorderLayout());
        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PlanarFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PlanarFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PlanarFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PlanarFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                frame = new PlanarFrame();
                frame.setVisible(true);
            }
        });
        
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
