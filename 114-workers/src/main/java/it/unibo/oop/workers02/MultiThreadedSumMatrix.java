package it.unibo.oop.workers02;

import java.util.stream.IntStream;


public class MultiThreadedSumMatrix implements SumMatrix{

    private final int nthread;
    
    public MultiThreadedSumMatrix(final int nthread) {
        this.nthread = nthread;
    }

    private static class Worker extends Thread {
        private final double[][] matrix;
        private final int startpos;
        private final int nelem;
        private double res;

        Worker(final double[][] matrix, final int startpos, final int nelem) {
            super();
            this.matrix = matrix;
            this.startpos = startpos;
            this.nelem = nelem;
        }

        @Override
        @SuppressWarnings("PMD.SystemPrintln")
        public void run() {
            System.out.println("Working from position" + startpos + "to position" + (startpos + nelem - 1));
            for (int i = startpos; i < matrix.length && i < startpos + nelem; i++){
                for (double[] d : matrix) {
                    this.res += d[i];
                }
            }
        }

        public double getResult() {
            return this.res;
        }
    }

    @Override
    public double sum(double[][] matrix) {
        final int size = matrix.length % nthread + matrix.length /nthread;
        return IntStream
                .iterate(0, start -> start + size)
                .limit(nthread)
                .mapToObj(start -> new Worker(matrix, start, size))
                .peek(Thread::start)
                .peek(MultiThreadedSumMatrix::joinUninterruptibly)
                .mapToDouble(Worker::getResult)
                .sum();
    }

    private static void joinUninterruptibly(final Thread target) {
        var joined = false;
        while (!joined) {
            try {
                target.join();
                joined = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
