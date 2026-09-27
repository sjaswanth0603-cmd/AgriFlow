package com.agriflow.m5_np;

import java.util.*;

/**
 * 2-Approximation Algorithm for Minimum Vertex Cover
 * 
 * Agricultural Use Case:
 * Determines the minimum number and locations for installing IoT sensor towers,
 * weather stations, and drone monitoring repeaters such that every connecting
 * canal, access road, and perimeter fence is covered by at least one tower.
 * 
 * Guaranteed Approximation Ratio: <= 2 * OPT
 * Time Complexity: O(V + E)
 * Space Complexity: O(V)
 */
public class VertexCoverApproximation {

    public static class RoadSegment {
        public final String junctionA;
        public final String junctionB;

        public RoadSegment(String junctionA, String junctionB) {
            this.junctionA = junctionA;
            this.junctionB = junctionB;
        }

        @Override
        public String toString() {
            return junctionA + " <---> " + junctionB;
        }
    }

    public static class TowerPlan {
        public final Set<String> towerLocations;
        public final List<RoadSegment> matchedPivotalEdges;

        public TowerPlan(Set<String> towerLocations, List<RoadSegment> matchedPivotalEdges) {
            this.towerLocations = towerLocations;
            this.matchedPivotalEdges = matchedPivotalEdges;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("2-Approximation Tower Placement (%d towers placed):\n", towerLocations.size()));
            for (String loc : towerLocations) {
                sb.append("  [TOWER] ").append(loc).append("\n");
            }
            sb.append("Selected Maximal Matching Edges:\n");
            for (RoadSegment edge : matchedPivotalEdges) {
                sb.append("  * ").append(edge).append("\n");
            }
            return sb.toString();
        }
    }

    /**
     * Finds a 2-approximation vertex cover using Maximal Matching.
     */
    public static TowerPlan approximateCover(List<String> junctions, List<RoadSegment> pathways) {
        Set<String> cover = new LinkedHashSet<>();
        List<RoadSegment> matchedEdges = new ArrayList<>();
        Set<String> coveredVertices = new HashSet<>();

        for (RoadSegment road : pathways) {
            // If neither endpoint of the road is in the cover yet
            if (!coveredVertices.contains(road.junctionA) && !coveredVertices.contains(road.junctionB)) {
                // Add BOTH endpoints to cover (the 2-approximation property)
                cover.add(road.junctionA);
                cover.add(road.junctionB);

                coveredVertices.add(road.junctionA);
                coveredVertices.add(road.junctionB);

                matchedEdges.add(road);
            }
        }

        return new TowerPlan(cover, matchedEdges);
    }

    public static void main(String[] args) {
        List<String> junctions = List.of(
            "Central Silo", "North Ditch", "South Ditch", "East Gate", "West Orchard", "Greenhouse"
        );

        List<RoadSegment> canalsAndRoads = List.of(
            new RoadSegment("Central Silo", "North Ditch"),
            new RoadSegment("Central Silo", "South Ditch"),
            new RoadSegment("North Ditch", "East Gate"),
            new RoadSegment("South Ditch", "West Orchard"),
            new RoadSegment("East Gate", "Greenhouse"),
            new RoadSegment("West Orchard", "Greenhouse")
        );

        TowerPlan plan = approximateCover(junctions, canalsAndRoads);
        System.out.println("--- Agricultural Infrastructure Coverage Plan ---");
        System.out.println(plan);
    }
}
