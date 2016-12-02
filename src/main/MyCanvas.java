import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
/*  w  w w .  ja v a 2  s. c  o m*/
import javax.swing.JComponent;
import javax.swing.JFrame;

class MyCanvas extends JComponent {
    public void paint(Graphics g) {

        Font font = new Font("Georgia", Font.ITALIC, 50);
        g.setFont(font);
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform at = new AffineTransform();
        at.setToRotation(-Math.PI / 2.0, 0, 0);
        g2d.setTransform(at);
        g2d.drawString("java2s.com", -200, 100);
    }

    public static void main(String[] a) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setBounds(30, 30, 450, 450);
        window.getContentPane().add(new MyCanvas());
        window.setVisible(true);
    }
}