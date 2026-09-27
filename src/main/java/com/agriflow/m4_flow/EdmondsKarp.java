package com.agriflow.m4_flow;

import java.util.*;

/**
 * Edmonds-Karp Algorithm for Maximum Irrigation Water Flow
 * 
 * Agricultural Use Case:
 * Models irrigation canal networks from a primary water reservoir (source)
 * through canal sluices and pumps to multiple farm plots (sink), finding max throughput.
 * 
 * Time Complexity: O(V * E^2)
 * Space Complexity: O(V + E)
 */
public class EdmondsKarp {

    public static int computeMaxFlow(FlowNetwork network, int source, int sink) {
        int maxFlow = 0;
        int n = network.getNumVertices();

        while (true) {
            // BFS to find the shortest augmenting path in the residual network
            FlowNetwork.Edge[] edgeTo = new FlowNetwork.Edge[n];
            Queue<Integer> queue = new LinkedList<>();
            boolean[] visited = new boolean[n];

            queue.add(source);
            visited[source] = true;

            while (!queue.isEmpty() && !visited[sink]) {
                int curr = queue.poll();

                for (FlowNetwork.Edge edge : network.getEdgesFrom(curr)) {
                    if (!visited[edge.v] && edge.remainingCapacity() > 0) {
                        visited[edge.v] = true;
                        edgeTo[edge.v] = edge;
                        queue.add(edge.v);
                    }
                }
            }

            // If no augmenting path reached the sink, max flow is attained
            if (!visited[sink]) {
                break;
            }

            // Find bottleneck capacity along the path
            int pathFlow = Integer.MAX_VALUE;
            for (int v = sink; v != source; v = edgeTo[v].u) {
                pathFlow = Math.min(pathFlow, edgeTo[v].remainingCapacity());
            }

            // Augment flow along the path
            for (int v = sink; v != source; v = edgeTo[v].u) {
                edgeTo[v].augment(pathFlow);
            }

            maxFlow += pathFlow;
        }

        return maxFlow;
    }

    public static void main(String[] args) {
        // Simple Irrigation Network:
        // 0: Main Reservoir (Source)
        // 1: Primary Canal North
        // 2: Primary Canal South
        // 3: Intermediate Pumping Station
        // 4: Farm Zones A & B (Sink)
        FlowNetwork net = new FlowNetwork(5);
        net.setVertexName(0, "Main Reservoir");
        net.setVertexName(1, "Canal North");
        net.setVertexName(2, "Canal South");
        net.setVertexName(3, "Pumping Station");
        net.setVertexName(4, "Farm Plots Sink");

        net.addEdge(0, 1, 100); // 100 kL/hr
        net.addEdge(0, 2, 80);  // 80 kL/hr
        net.addEdge(1, 2, 30);
        net.addEdge(1, 3, 70);
        net.addEdge(2, 3, 60);
        net.addEdge(3, 4, 120);

        int maxWaterFlow = computeMaxFlow(net, 0, 4);
        System.out.println("--- Edmonds-Karp Irrigation Flow ---");
        System.out.println("Maximum Water Throughput: " + maxWaterFlow + " kL/hour");
    }
}
