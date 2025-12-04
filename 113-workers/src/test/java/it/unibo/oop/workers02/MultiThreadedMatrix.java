package it.unibo.oop.workers02;

import java.util.Arrays;
import java.util.stream.IntStream;

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
        
        return IntStream
                .iterate(0, start -> start + size)
                .limit(nthread)
                .mapToObj(start -> new Worker(list, start, size))
                .peek(Thread::start)
                .peek(MultiThreadedMatrix::joinUninterruptibly)
                .mapToDouble(Worker::getResult)
                .sum();
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

    @SuppressWarnings("PMD.AvoidPrintStackTrace")
    private static void joinUninterruptibly(final Thread target) {
        var joined = false;
        while (!joined) {
            try {
                target.join();
                joined = true;
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
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
        @SuppressWarnings("PMD.SystemPrintln")
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