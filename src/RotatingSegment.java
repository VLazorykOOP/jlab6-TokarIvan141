import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RotatingSegment extends JPanel implements ActionListener {
    private double t = 0.0;
    private double angle = 0.0;
    private boolean movingForward = true;

    private final int ROTATING_SEGMENT_LENGTH = 80;
    private Timer timer;

    public RotatingSegment() {
        timer = new Timer(40, this);
        timer.start();

        setPreferredSize(new Dimension(600, 400));
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        int x1 = 100;
        int y1 = height / 2;
        int x2 = width - 100;
        int y2 = height / 2;

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(x1, y1, x2, y2);

        int pointX = (int) (x1 + t * (x2 - x1));
        int pointY = (int) (y1 + t * (y2 - y1));

        g2d.setColor(Color.RED);
        g2d.fillOval(pointX - 5, pointY - 5, 10, 10);

        int rotatingX1 = pointX + (int) (ROTATING_SEGMENT_LENGTH * Math.cos(angle));
        int rotatingY1 = pointY + (int) (ROTATING_SEGMENT_LENGTH * Math.sin(angle));

        int rotatingX2 = pointX - (int) (ROTATING_SEGMENT_LENGTH * Math.cos(angle));
        int rotatingY2 = pointY - (int) (ROTATING_SEGMENT_LENGTH * Math.sin(angle));

        g2d.setColor(Color.BLUE);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(rotatingX1, rotatingY1, rotatingX2, rotatingY2);

        g2d.setColor(Color.DARK_GRAY);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("Позиція точки: " + String.format("%.2f", t), 10, 20);
        g2d.drawString("Кут обертання: " + String.format("%.1f", Math.toDegrees(angle)) + "°", 10, 40);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (movingForward) {
            t += 0.01;
            if (t >= 1.0) {
                t = 1.0;
                movingForward = false;
            }
        } else {
            t -= 0.01;
            if (t <= 0.0) {
                t = 0.0;
                movingForward = true;
            }
        }

        angle += Math.PI / 100;
        if (angle >= 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }

        repaint();
    }

    public static void createAndShowGUI() {
        JFrame frame = new JFrame("Обертовий відрізок - Лабораторна №6");
        frame.add(new RotatingSegment());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}