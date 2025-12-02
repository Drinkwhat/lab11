package it.unibo.oop.reactivegui02;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import it.unibo.oop.JFrameUtil;

import java.io.Serial;
import java.lang.reflect.InvocationTargetException;

/**
 * Second example of reactive GUI.
 */
public final class ConcurrentGUI extends JFrame {

    @Serial
    private static final long serialVersionUID = 1L;
    private final JLabel display = new JLabel();

    /**
     * Builds a new CGUI.
     */
    public ConcurrentGUI() {
        super();
        JFrameUtil.dimensionJFrame(this);

        final JPanel panel = new JPanel();
        final JButton up = new JButton("up");
        final JButton down = new JButton("down");
        final JButton stop = new JButton("stop");

        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);

        final Counter counter = new Counter();
        new Thread(counter).start();

        up.addActionListener(e -> counter.increment());
        down.addActionListener(e -> counter.decrement());
        stop.addActionListener(e -> {
            counter.stopCounting();
            up.setEnabled(false);
            down.setEnabled(false);
            stop.setEnabled(false);
        });
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
                    SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.display.setText(nextText));

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
}
