package it.unibo.oop.reactivegui03;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import it.unibo.oop.JFrameUtil;

import java.io.Serial;
import java.lang.reflect.InvocationTargetException;

/**
 * Third experiment with reactive gui.
 */
public final class AnotherConcurrentGUI extends JFrame {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final int TIME_SLEEP = 10_000;
    private final transient Counter counter = new Counter();
    private final transient Stopper stopper = new Stopper();
    private final JLabel display = new JLabel();
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");
    private final JButton stop = new JButton("stop");

    /**
     * Builds a new CGUI.
     */
    public AnotherConcurrentGUI() {
        super();
        JFrameUtil.dimensionJFrame(this);

        final JPanel panel = new JPanel();

        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);

        new Thread(counter).start();
        new Thread(stopper).start();

        up.addActionListener(e -> counter.increment());
        down.addActionListener(e -> counter.decrement());
        stop.addActionListener(e -> disableButtonsAndStopCounting());
    }

    private void disableButtonsAndStopCounting() {
        counter.stopCounting();
        up.setEnabled(false);
        down.setEnabled(false);
        stop.setEnabled(false);
    }

    /**
     * Counter agent that updates the display periodically.
     */
    private final class Counter implements Runnable {

        private volatile boolean stop;
        private volatile boolean isIncrement = true;
        private int value;

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    final var nextText = Integer.toString(this.value);
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextText));

                    if (this.isIncrement) {
                        this.value++;
                    } else {
                        this.value--;
                    }

                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    ex.printStackTrace(); // NOPMD: this is just an example
                }
            }
        }

        public void stopCounting() {
            this.stop = true;
        }

        public void increment() {
            this.isIncrement = true;
        }

        public void decrement() {
            this.isIncrement = false;
        }
    }

    private final class Stopper implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(TIME_SLEEP);
                disableButtonsAndStopCounting();
            } catch (final InterruptedException e) {
                e.printStackTrace(); // NOPMD: this is just an example
            }
        }
    }
}
