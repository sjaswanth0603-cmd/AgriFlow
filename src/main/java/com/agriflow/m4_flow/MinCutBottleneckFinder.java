package com.agriflow.m4_flow;

import java.util.*;

/**
 * Max-Flow Min-Cut Bottleneck Identifier
 * 
 * Agricultural Use Case:
 * Identifies the exact bottleneck canals and pipes in an irrigation network.
 * By the Max-Flow Min-Cut Theorem, these edges are fully saturated and directly restrict
 * total water delivery. Upgrading them yields the maximum increase in irrigation capacity.
 * 
 * Time Complexity: O(V + E) post max-flow calculation
 * Space Complexity: O(V)
 */
public class MinCutBottleneckFinder {

    public static class BottleneckCanal {
        public final String from;
        public final String to;
        public final int capacity;

        public BottleneckCanal(String from, String to, int capacity) {
            this.from = from;
            this.to = to;
            this.capacity = capacity;
        }

        @Override
        public String toString() {
            return String.format("Bottleneck: %s -> %s (Capacity: %d units)", from, to, capacity);
        }
    }

    /**
     * Finds the minimum cut edges after running max flow.
     */
    public static List<BottleneckCanal> findBottlenecks(FlowNetwork network, int source) {
        List<BottleneckCanal> bottlenecks = new ArrayList<>();
        int n = network.getNumVertices();
        boolean[] reachableFromSource = new boolean[n];

        // Perform BFS on residual graph starting from source
        Queue<Integer> queue = new LinkedList<>();
        queue.add(source);
        reachableFromSource[source] = true;

        while (!queue.isEmpty()) {
            int u = queue.poll();

            for (FlowNetwork.Edge edge : network.getEdgesFrom(u)) {
                // If edge has remaining capacity in residual graph and v not visited
                if (edge.remainingCapacity() > 0 && !reachableFromSource[edge.v]) {
                    reachableFromSource[edge.v] = true;
                    queue.add(edge.v);
                }
            }
        }

        // Any original edge from a reachable vertex to an unreachable vertex is in the Min-Cut!
        for (int u = 0; u < n; u++) {
            if (reachableFromSource[u]) {
                for (FlowNetwork.Edge edge : network.getEdgesFrom(u)) {
                    // We only look at forward edges with positive original capacity
                    if (edge.capacity > 0 && !reachableFromSource[edge.v]) {
                        bottlenecks.add(new BottleneckCanal(
                                network.getVertexName(u),
                                network.getVertexName(edge.v),
                                edge.capacity
                        ));
                    }
                }
            }
        }

        return bottlenecks;
    }

    public static void main(String[] args) {
        FlowNetwork net = new FlowNetwork(4);
        net.setVertexName(0, "Reservoir");
        net.setVertexName(1, "Canal North");
        net.setVertexName(2, "Canal South");
        net.setVertexName(3, "Farm Fields");

        net.addEdge(0, 1, 10);
        net.addEdge(0, 2, 5);
        net.addEdge(1, 2, 15);
        net.addEdge(1, 3, 10);
        net.addEdge(2, 3, 10);

        // Run Edmonds-Karp or Dinic to saturate network
        EdmondsKarp.computeMaxFlow(net, 0, 3);

        List<BottleneckCanal> cuts = findBottlenecks(net, 0);
        System.out.println("--- S-T Min Cut Bottlenecks ---");
        for (BottleneckCanal b : cuts) {
            System.out.println(" * " + b);
        }
    }
}
