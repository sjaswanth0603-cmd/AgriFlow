package com.agriflow.m6_parallel;

import java.util.Arrays;

/**
 * Blelloch Parallel Prefix Sum (Work-Efficient Scan) Algorithm
 * 
 * Agricultural Use Case:
 * Computes cumulative rainfall across irrigation zones and running soil-moisture absorption totals.
 * 
 * Theoretical Complexity:
 * - Work: O(N) operations (work-efficient)
 * - Span / Depth: O(log N) parallel steps
 */
public class BlellochScan {

    /**
     * Executes the Blelloch Exclusive Scan on an array whose length is a power of 2.
     * If array length is not a power of 2, it pads internally.
     * 
     * @param input Array of daily rainfall or sensor readings (e.g. in mm).
     * @return Array of cumulative exclusive prefix sums.
     */
    public static double[] exclusiveScan(double[] input) {
        int originalLen = input.length;
        int n = 1;
        while (n < originalLen) n *= 2; // Next power of 2

        double[] a = new double[n];
        System.arraycopy(input, 0, a, 0, originalLen);

        // Phase 1: Up-Sweep (Parallel Reduction Phase)
        // Builds partial sums in a balanced binary tree
        for (int step = 1; step < n; step *= 2) {
            for (int i = 2 * step - 1; i < n; i += 2 * step) {
                a[i] = a[i] + a[i - step];
            }
        }

        // The root contains the total sum of all elements.
        // Save total for inclusive scan if needed, set root to zero for exclusive scan:
        a[n - 1] = 0;

        // Phase 2: Down-Sweep (Distribution Phase)
        // Traverses down the tree to compute prefix sums
        for (int step = n / 2; step >= 1; step /= 2) {
            for (int i = 2 * step - 1; i < n; i += 2 * step) {
                double temp = a[i - step];
                a[i - step] = a[i];
                a[i] = a[i] + temp;
            }
        }

        // Extract original length
        double[] result = new double[originalLen];
        System.arraycopy(a, 0, result, 0, originalLen);
        return result;
    }

    /**
     * Inclusive Scan (running total including the current day's rainfall).
     */
    public static double[] inclusiveScan(double[] input) {
        double[] excl = exclusiveScan(input);
        double[] incl = new double[input.length];
        for (int i = 0; i < input.length; i++) {
            incl[i] = excl[i] + input[i];
        }
        return incl;
    }

    public static void main(String[] args) {
        // Daily rainfall in mm across 8 consecutive days
        double[] dailyRainfallMm = {5.0, 12.0, 0.0, 8.5, 15.0, 22.0, 4.0, 11.5};

        System.out.println("Daily Rainfall (mm): " + Arrays.toString(dailyRainfallMm));

        double[] cumulativeExclusive = exclusiveScan(dailyRainfallMm);
        System.out.println("Exclusive Blelloch Scan: " + Arrays.toString(cumulativeExclusive));

        double[] cumulativeInclusive = inclusiveScan(dailyRainfallMm);
        System.out.println("Cumulative Rainfall Totals (Inclusive): " + Arrays.toString(cumulativeInclusive));
    }
}
