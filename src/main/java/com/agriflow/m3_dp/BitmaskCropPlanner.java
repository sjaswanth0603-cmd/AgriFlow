package com.agriflow.m3_dp;

import java.util.ArrayList;
import java.util.List;

/**
 * Bitmask Dynamic Programming for Multi-Crop Portfolio Optimization
 * 
 * Agricultural Use Case:
 * A farm has N candidate crops with distinct water requirements, land needs, and expected revenues.
 * Some crops have companion planting synergies (+bonus) or rotational conflicts (-penalty).
 * Bitmask DP finds the global profit-maximizing combination without violating resource limits.
 * 
 * Time Complexity: O(2^N * N)
 * Space Complexity: O(2^N)
 */
public class BitmaskCropPlanner {

    public static class Crop {
        public final int id;
        public final String name;
        public final int landAcres;
        public final int waterRequiredUnits;
        public final int expectedProfit;

        public Crop(int id, String name, int landAcres, int waterRequiredUnits, int expectedProfit) {
            this.id = id;
            this.name = name;
            this.landAcres = landAcres;
            this.waterRequiredUnits = waterRequiredUnits;
            this.expectedProfit = expectedProfit;
        }

        @Override
        public String toString() {
            return String.format("%s (Land: %dA, Water: %dU, Profit: $%d)", name, landAcres, waterRequiredUnits, expectedProfit);
        }
    }

    public static class OptimizationPlan {
        public final List<Crop> selectedCrops;
        public final int totalProfit;
        public final int totalLandUsed;
        public final int totalWaterUsed;

        public OptimizationPlan(List<Crop> selectedCrops, int totalProfit, int totalLandUsed, int totalWaterUsed) {
            this.selectedCrops = selectedCrops;
            this.totalProfit = totalProfit;
            this.totalLandUsed = totalLandUsed;
            this.totalWaterUsed = totalWaterUsed;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Optimized Harvest Plan (Max Profit: $%d | Land: %dA | Water: %dU):\n",
                    totalProfit, totalLandUsed, totalWaterUsed));
            for (Crop c : selectedCrops) {
                sb.append("  [+] ").append(c).append("\n");
            }
            return sb.toString();
        }
    }

    /**
     * Solves the optimal crop selection problem using Bitmask DP.
     */
    public static OptimizationPlan optimize(List<Crop> crops, int maxLand, int maxWater, int[][] synergyBonus) {
        int n = crops.size();
        int totalMasks = 1 << n;

        int[] dpProfit = new int[totalMasks];
        int[] dpLand = new int[totalMasks];
        int[] dpWater = new int[totalMasks];

        // Precompute resource sums for each subset mask
        for (int mask = 0; mask < totalMasks; mask++) {
            int land = 0;
            int water = 0;
            int baseProfit = 0;

            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {
                    land += crops.get(i).landAcres;
                    water += crops.get(i).waterRequiredUnits;
                    baseProfit += crops.get(i).expectedProfit;
                }
            }

            // Add synergy bonus between pairs in mask
            if (synergyBonus != null) {
                for (int i = 0; i < n; i++) {
                    if ((mask & (1 << i)) != 0) {
                        for (int j = i + 1; j < n; j++) {
                            if ((mask & (1 << j)) != 0) {
                                baseProfit += synergyBonus[i][j];
                            }
                        }
                    }
                }
            }

            dpLand[mask] = land;
            dpWater[mask] = water;
            dpProfit[mask] = (land <= maxLand && water <= maxWater) ? baseProfit : -1;
        }

        // Find mask with highest valid profit
        int bestMask = 0;
        int maxProfit = -1;

        for (int mask = 0; mask < totalMasks; mask++) {
            if (dpProfit[mask] > maxProfit) {
                maxProfit = dpProfit[mask];
                bestMask = mask;
            }
        }

        List<Crop> chosen = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if ((bestMask & (1 << i)) != 0) {
                chosen.add(crops.get(i));
            }
        }

        return new OptimizationPlan(chosen, maxProfit, dpLand[bestMask], dpWater[bestMask]);
    }

    public static void main(String[] args) {
        List<Crop> candidates = List.of(
            new Crop(0, "Organic Wheat", 30, 40, 12000),
            new Crop(1, "Soybean (Nitrogen Fixer)", 25, 25, 9500),
            new Crop(2, "Sweet Corn", 35, 55, 14000),
            new Crop(3, "Tomatoes (High Value)", 15, 30, 11000),
            new Crop(4, "Alfalfa (Fodder)", 20, 20, 6000)
        );

        // Soybean + Wheat gives a +$2000 soil health synergy bonus
        int[][] synergy = new int[5][5];
        synergy[0][1] = 2000;
        synergy[1][0] = 2000;

        int farmLandLimit = 65; // 65 acres
        int waterBudget = 85;   // 85 units

        OptimizationPlan plan = optimize(candidates, farmLandLimit, waterBudget, synergy);
        System.out.println(plan);
    }
}
