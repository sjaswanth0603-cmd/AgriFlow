package com.agriflow.m6_parallel;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Parallel Reduce for Large-Scale Agricultural Sensor Telemetry
 * 
 * Agricultural Use Case:
 * Simultaneously aggregates millions of continuous readings from distributed soil probes
 * to rapidly detect micro-climate temperature spikes and soil moisture deficits.
 * 
 * Theoretical Complexity:
 * - Work: O(N)
 * - Span / Depth: O(log N)
 */
public class ParallelSensorReduce {

    private static final int SEQUENTIAL_THRESHOLD = 5000;
    private static final ForkJoinPool POOL = new ForkJoinPool();

    public static class SensorStats {
        public final double min;
        public final double max;
        public final double sum;
        public final long count;

        public SensorStats(double min, double max, double sum, long count) {
            this.min = min;
            this.max = max;
            this.sum = sum;
            this.count = count;
        }

        public double average() {
            return count == 0 ? 0 : sum / count;
        }

        public static SensorStats combine(SensorStats a, SensorStats b) {
            return new SensorStats(
                Math.min(a.min, b.min),
                Math.max(a.max, b.max),
                a.sum + b.sum,
                a.count + b.count
            );
        }

        @Override
        public String toString() {
            return String.format("Stats [Count: %,d | Min: %.2f | Max: %.2f | Avg: %.2f]",
                    count, min, max, average());
        }
    }

    private static class ReduceTask extends RecursiveTask<SensorStats> {
        private final double[] data;
        private final int start;
        private final int end;

        public ReduceTask(double[] data, int start, int end) {
            this.data = data;
            this.start = start;
            this.end = end;
        }

        @Override
        protected SensorStats compute() {
            if (end - start <= SEQUENTIAL_THRESHOLD) {
                double min = Double.MAX_VALUE;
                double max = -Double.MAX_VALUE;
                double sum = 0;
                for (int i = start; i < end; i++) {
                    double val = data[i];
                    if (val < min) min = val;
                    if (val > max) max = val;
                    sum += val;
                }
                return new SensorStats(min, max, sum, end - start);
            }

            int mid = start + (end - start) / 2;
            ReduceTask left = new ReduceTask(data, start, mid);
            ReduceTask right = new ReduceTask(data, mid, end);

            left.fork();
            SensorStats rightResult = right.compute();
            SensorStats leftResult = left.join();

            return SensorStats.combine(leftResult, rightResult);
        }
    }

    /**
     * Executes parallel reduction across the sensor array.
     */
    public static SensorStats reduce(double[] sensorValues) {
        return POOL.invoke(new ReduceTask(sensorValues, 0, sensorValues.length));
    }

    public static void main(String[] args) {
        int n = 500_000; // 500,000 sensor observations
        double[] telemetry = new double[n];
        for (int i = 0; i < n; i++) {
            telemetry[i] = 15.0 + Math.sin(i * 0.01) * 10.0 + (Math.random() * 5.0);
        }

        long t0 = System.nanoTime();
        SensorStats stats = reduce(telemetry);
        long t1 = System.nanoTime();

        System.out.println("--- Parallel Reduce on Farm Telemetry ---");
        System.out.println(stats);
        System.out.printf("Processed in: %.2f ms%n", (t1 - t0) / 1e6);
    }
}
