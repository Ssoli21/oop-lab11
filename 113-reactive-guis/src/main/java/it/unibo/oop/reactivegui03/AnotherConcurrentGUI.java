package it.unibo.oop.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Third experiment with reactive gui.
 */
@SuppressWarnings("PMD.AvoidPrintStackTrace")
public final class AnotherConcurrentGUI extends JFrame {
    
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel();
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");
    private final JButton stop = new JButton("stop");
    private static final long WAITING_TIME = TimeUnit.SECONDS.toMillis(10);
    final Agent agent = new Agent();

    public AnotherConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth()*WIDTH_PERC), (int) (screenSize.getHeight()*HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(stop);
        panel.add(up);
        panel.add(down);
        this.getContentPane().add(panel);
        this.setVisible(true);

        stop.addActionListener((e) -> stopCounting());
        up.addActionListener((e) -> agent.isIncreasing(true));
        down.addActionListener((e) -> agent.isIncreasing(false));

        new Thread(() -> {
            try {
                Thread.sleep(WAITING_TIME);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            stopCounting();
        }).start();
        new Thread(agent).start();
        
    }
    private void stopCounting() {
        agent.stopCounting();
        SwingUtilities.invokeLater(() -> {
            stop.setEnabled(false);
            up.setEnabled(false);
            down.setEnabled(false);
        });
    }

    private class Agent implements Runnable {

        private volatile boolean stop, increasing = true;
        private int counter;

        @Override
        public void run() {
            while (!this.stop) {
                try { 
                    final var nextText = Integer.toString(this.counter);
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextText));
                    if(increasing) {
                        this.counter++;
                    } else {
                        this.counter--;
                    }
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        
        public void stopCounting() {
            this.stop = true;
        }
        
        public void isIncreasing(final boolean inc) {
            if (inc) {
                this.increasing = true;
            } else {
                this.increasing = false;
            }
        }
    }
}
