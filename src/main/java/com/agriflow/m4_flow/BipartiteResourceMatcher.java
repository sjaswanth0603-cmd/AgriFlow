package com.agriflow.m4_flow;

import java.util.*;

/**
 * Maximum Bipartite Matching for Farm Resource Allocation
 * 
 * Agricultural Use Case:
 * Optimally assigns limited high-value farm equipment (combine harvesters, drone sprayers,
 * subsoil rippers, heavy tractors) to requesting farms during critical planting and harvesting windows.
 * 
 * Time Complexity: O(V * E)
 * Space Complexity: O(V)
 */
public class BipartiteResourceMatcher {

    public static class Assignment {
        public final String farmName;
        public final String equipmentName;

        public Assignment(String farmName, String equipmentName) {
            this.farmName = farmName;
            this.equipmentName = equipmentName;
        }

        @Override
        public String toString() {
            return String.format("%s <==> %s", farmName, equipmentName);
        }
    }

    /**
     * Finds maximum cardinality bipartite matching using augmenting DFS paths.
     */
    public static List<Assignment> match(
            List<String> farms,
            List<String> equipments,
            Map<Integer, List<Integer>> farmToEquipAdj) {

        int numEquip = equipments.size();
        // matchToFarm[j] stores the farm index assigned to equipment j, or -1 if unassigned
        int[] matchToFarm = new int[numEquip];
        Arrays.fill(matchToFarm, -1);

        int matchCount = 0;
        for (int farmIdx = 0; farmIdx < farms.size(); farmIdx++) {
            boolean[] visitedEquip = new boolean[numEquip];
            if (dfsAugment(farmIdx, farmToEquipAdj, visitedEquip, matchToFarm)) {
                matchCount++;
            }
        }

        List<Assignment> assignments = new ArrayList<>();
        for (int eqIdx = 0; eqIdx < numEquip; eqIdx++) {
            if (matchToFarm[eqIdx] != -1) {
                assignments.add(new Assignment(
                        farms.get(matchToFarm[eqIdx]),
                        equipments.get(eqIdx)
                ));
            }
        }

        return assignments;
    }

    private static boolean dfsAugment(
            int farm,
            Map<Integer, List<Integer>> adj,
            boolean[] visitedEquip,
            int[] matchToFarm) {

        List<Integer> compatibleEquip = adj.getOrDefault(farm, Collections.emptyList());

        for (int eq : compatibleEquip) {
            if (!visitedEquip[eq]) {
                visitedEquip[eq] = true;

                // If equipment is free OR its current assigned farm can find an alternative
                if (matchToFarm[eq] == -1 || dfsAugment(matchToFarm[eq], adj, visitedEquip, matchToFarm)) {
                    matchToFarm[eq] = farm;
                    return true;
                }
            }
        }

        return false;
    }

    public static void main(String[] args) {
        List<String> farms = List.of("GreenValley Farm", "Sunrise Orchard", "Riverbend Fields", "Highland Vineyard");
        List<String> equipment = List.of("Drone Sprayer Alpha", "Heavy Tractor 4WD", "Combine Harvester 9000", "Soil Aerator");

        // Preferences / compatibility
        Map<Integer, List<Integer>> compatibility = new HashMap<>();
        compatibility.put(0, List.of(0, 1));       // GreenValley needs Drone or Tractor
        compatibility.put(1, List.of(1, 2));       // Sunrise needs Tractor or Harvester
        compatibility.put(2, List.of(0, 3));       // Riverbend needs Drone or Aerator
        compatibility.put(3, List.of(1));          // Highland needs Tractor

        List<Assignment> results = match(farms, equipment, compatibility);
        System.out.println("--- Optimal Farm Equipment Allocation (" + results.size() + " matched) ---");
        for (Assignment a : results) {
            System.out.println("  * " + a);
        }
    }
}
