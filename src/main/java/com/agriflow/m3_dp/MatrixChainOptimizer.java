package com.agriflow.m3_dp;

/**
 * Matrix Chain Multiplication (MCM) Optimization
 * 
 * Agricultural Use Case:
 * Optimizes the sequence of matrix transformations applied to multispectral satellite images,
 * drone LIDAR point clouds, and IoT field-sensor coordinate projections (e.g., NDVI index mapping,
 * elevation gradient interpolation, water-table stress estimation).
 * 
 * Time Complexity: O(K^3) where K is number of transformation matrices
 * Space Complexity: O(K^2)
 */
public class MatrixChainOptimizer {

    public static class McmResult {
        public final int minScalarMultiplications;
        public final String optimalParenthesization;

        public McmResult(int minScalarMultiplications, String optimalParenthesization) {
            this.minScalarMultiplications = minScalarMultiplications;
            this.optimalParenthesization = optimalParenthesization;
        }

        @Override
        public String toString() {
            return String.format("Minimum Scalar Multiplications: %,d\nOptimal Pipeline Order: %s",
                    minScalarMultiplications, optimalParenthesization);
        }
    }

    /**
     * Solves the optimal order of matrix multiplications.
     * 
     * @param dims Array of dimensions where Matrix i has dimension dims[i-1] x dims[i].
     * @param matrixLabels Names of the agricultural transformation stages (e.g. Drone, Satellite, Soil, NDVI).
     */
    public static McmResult optimize(int[] dims, String[] matrixLabels) {
        int n = dims.length - 1; // Number of matrices
        int[][] m = new int[n + 1][n + 1];
        int[][] s = new int[n + 1][n + 1];

        // Chain length L from 2 to n
        for (int L = 2; L <= n; L++) {
            for (int i = 1; i <= n - L + 1; i++) {
                int j = i + L - 1;
                m[i][j] = Integer.MAX_VALUE;

                for (int k = i; k < j; k++) {
                    int cost = m[i][k] + m[k + 1][j] + (dims[i - 1] * dims[k] * dims[j]);
                    if (cost < m[i][j]) {
                        m[i][j] = cost;
                        s[i][j] = k;
                    }
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        reconstructParenthesization(s, 1, n, matrixLabels, sb);

        return new McmResult(m[1][n], sb.toString());
    }

    private static void reconstructParenthesization(int[][] s, int i, int j, String[] labels, StringBuilder sb) {
        if (i == j) {
            sb.append(labels != null && i - 1 < labels.length ? labels[i - 1] : ("M" + i));
            return;
        }
        sb.append("(");
        reconstructParenthesization(s, i, s[i][j], labels, sb);
        sb.append(" x ");
        reconstructParenthesization(s, s[i][j] + 1, j, labels, sb);
        sb.append(")");
    }

    public static void main(String[] args) {
        // Transformation matrices for:
        // A1: Drone Elevation Matrix (10 x 100)
        // A2: Soil Sensor Feature Matrix (100 x 5)
        // A3: Satellite NDVI Spectral Band (5 x 50)
        // A4: Crop Yield Projection Vector (50 x 1)
        int[] dimensions = {10, 100, 5, 50, 1};
        String[] stageNames = {"DroneElevation", "SoilSensors", "SatelliteNDVI", "YieldProjection"};

        McmResult result = optimize(dimensions, stageNames);
        System.out.println("--- Agricultural Geo-Data Matrix Optimization ---");
        System.out.println(result);
    }
}
