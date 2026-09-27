package com.agriflow.m6_parallel;

/**
 * Brent's Work-Time Scheduling Theorem Analyzer
 * 
 * Agricultural Use Case:
 * Determines how many parallel processor cores (or edge micro-servers installed across farm silos)
 * are required to process continuous sensor telemetry streams within real-time irrigation response windows.
 * 
 * Theorem Formula:
 *   T_p <= (W - D) / p + D
 * Where:
 *   W = Work (total operations, T_1)
 *   D = Depth / Span (critical path length, T_infinity)
 *   p = Number of parallel processing cores
 */
public class BrentsTheoremAnalyzer {

    public static class ParallelMetrics {
        public final int cores;
        public final double timeBound;
        public final double speedup;
        public final double efficiency;

        public ParallelMetrics(int cores, double timeBound, double speedup, double efficiency) {
            this.cores = cores;
            this.timeBound = timeBound;
            this.speedup = speedup;
            this.efficiency = efficiency;
        }

        @Override
        public String toString() {
            return String.format("Cores: %2d | Time Bound: %8.2f ops | Speedup: %5.2fx | Efficiency: %5.1f%%",
                    cores, timeBound, speedup, efficiency * 100.0);
        }
    }

    /**
     * Calculates the upper bound on parallel execution time according to Brent's theorem.
     */
    public static double computeTimeBound(double work, double depth, int processors) {
        if (processors <= 0) return work;
        return ((work - depth) / processors) + depth;
    }

    /**
     * Evaluates speedup and efficiency scaling from 1 to maxCores.
     */
    public static java.util.List<ParallelMetrics> evaluateScaling(double work, double depth, int maxCores) {
        java.util.List<ParallelMetrics> results = new java.util.ArrayList<>();
        double t1 = work;

        for (int p = 1; p <= maxCores; p++) {
            double tp = computeTimeBound(work, depth, p);
            double speedup = t1 / tp;
            double efficiency = speedup / p;
            results.add(new ParallelMetrics(p, tp, speedup, efficiency));
        }

        return results;
    }

    public static void main(String[] args) {
        // Example: Parallel Blelloch Scan on 1,000,000 agricultural sensor readings
        // Work W = 2 * N = 2,000,000 operations
        // Depth D = 2 * log2(N) ~= 40 steps
        double work = 2_000_000.0;
        double depth = 40.0;

        System.out.println("--- Brent's Theorem Evaluation for Farm Sensor Stream ---");
        System.out.printf("Total Work (W): %,.0f ops | Critical Path Depth (D): %,.0f steps%n%n", work, depth);

        var scaling = evaluateScaling(work, depth, 16);
        for (ParallelMetrics m : scaling) {
            System.out.println(m);
        }
    }
}
