package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This is a implementation of summing the elements of a matrix.
 */
public class MultiThreadedMatrix implements SumMatrix {

    private final int nthread;

    /**
     * @param nthread
     *            no. of thread performing the sum.
     */
    public MultiThreadedMatrix(final int nthread) {
        this.nthread = nthread;
    }

    private double sum(final double[] list) {
        final int size = list.length % nthread + list.length / nthread;
        /*
         * Build a list of workers
         */
        final List<Worker> workers = new ArrayList<>(nthread);
        for (int start = 0; start < list.length; start += size) {
            workers.add(new Worker(list, start, size));
        }
        /*
         * Start them
         */
        for (final Worker w: workers) {
            w.start();
        }
        /*
         * Wait for every one of them to finish. This operation is _way_ better done by
         * using barriers and latches, and the whole operation would be better done with
         * futures.
         */
        long sum = 0;
        for (final Worker w: workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (final InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        /*
         * Return the sum
         */
        return sum;
    }

    /**
     * @inheritDoc
     */
    @Override
    public double sum(final double[][] matrix) {
        double res = 0;
        for (final double[] list : matrix) {
            res += sum(list);
        }
       return res;
    }

    private static class Worker extends Thread {
        private final double[] list;
        private double res;

        /**
         * Build a new worker.
         *
         * @param list
         *            the list to sum
         * @param start
         *            the initial position for this worker
         * @param nelem
         *            the no. of elems to sum up for this worker
         */
        Worker(final double[] list, final int start, final int nelem) {
            super();
            this.list = Arrays.copyOfRange(list, start, Math.min(start + nelem, list.length));
        }

        @Override
        // @SuppressWarnings("PMD.SystemPrintln")
        public synchronized void run() {
            //System.out.println("Working from position " + startpos + " to position " + (startpos + nelem - 1));
            for (final var elem : list) {
                this.res += elem;
            }
        }

        /**
         * Returns the result of summing up the integers within the list.
         *
         * @return the sum of every element in the array
         */
        public synchronized double getResult() {
            return this.res;
        }
    }
}
