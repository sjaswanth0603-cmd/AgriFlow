package com.agriflow.m6_parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Randomized QuickSort for Ranking Farm Records and Yield Metrics
 * 
 * Agricultural Use Case:
 * Ranks thousands of farm parcels, harvest yields, and sensor anomaly scores.
 * The randomized pivot eliminates the O(N^2) vulnerability on sorted or partially sorted field data.
 * 
 * Expected Time Complexity: O(N log N)
 * Worst-Case Time Complexity: O(N^2) with probability approaching zero
 * Space Complexity: O(log N) recursive call stack
 */
public class RandomizedQuickSort {

    private static final Random RNG = new Random();

    public static class FarmRecord implements Comparable<FarmRecord> {
        public final String farmId;
        public final String cropType;
        public final double yieldTonsPerAcre;

        public FarmRecord(String farmId, String cropType, double yieldTonsPerAcre) {
            this.farmId = farmId;
            this.cropType = cropType;
            this.yieldTonsPerAcre = yieldTonsPerAcre;
        }

        @Override
        public int compareTo(FarmRecord other) {
            // Descending order of yield (highest yield first)
            return Double.compare(other.yieldTonsPerAcre, this.yieldTonsPerAcre);
        }

        @Override
        public String toString() {
            return String.format("%s (%s): %.2f tons/acre", farmId, cropType, yieldTonsPerAcre);
        }
    }

    public static void sort(List<FarmRecord> records) {
        if (records == null || records.size() <= 1) return;
        quickSort(records, 0, records.size() - 1);
    }

    private static void quickSort(List<FarmRecord> list, int low, int high) {
        if (low < high) {
            int pivotIndex = randomizedPartition(list, low, high);
            quickSort(list, low, pivotIndex - 1);
            quickSort(list, pivotIndex + 1, high);
        }
    }

    private static int randomizedPartition(List<FarmRecord> list, int low, int high) {
        // Pick a random pivot index between low and high
        int randomIndex = low + RNG.nextInt(high - low + 1);
        swap(list, randomIndex, high);
        return partition(list, low, high);
    }

    private static int partition(List<FarmRecord> list, int low, int high) {
        FarmRecord pivot = list.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (list.get(j).compareTo(pivot) <= 0) { // Using compareTo for ranking
                i++;
                swap(list, i, j);
            }
        }
        swap(list, i + 1, high);
        return i + 1;
    }

    private static void swap(List<FarmRecord> list, int i, int j) {
        FarmRecord temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }

    public static void main(String[] args) {
        List<FarmRecord> harvestData = new ArrayList<>(List.of(
            new FarmRecord("Farm-Alpha", "Corn", 4.2),
            new FarmRecord("Farm-Beta", "Wheat", 3.8),
            new FarmRecord("Farm-Gamma", "Soybean", 2.9),
            new FarmRecord("Farm-Delta", "Corn", 5.1),
            new FarmRecord("Farm-Epsilon", "Rice", 6.3)
        ));

        System.out.println("Before Sorting:");
        harvestData.forEach(r -> System.out.println("  " + r));

        sort(harvestData);

        System.out.println("\nAfter Randomized QuickSort (Ranked by Productivity):");
        harvestData.forEach(r -> System.out.println("  " + r));
    }
}
