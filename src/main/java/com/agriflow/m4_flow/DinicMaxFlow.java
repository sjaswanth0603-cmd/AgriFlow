package com.agriflow.m4_flow;

import java.util.*;

/**
 * Dinic's Algorithm for Large-Scale Agricultural Water Distribution Networks
 * 
 * Agricultural Use Case:
 * Evaluates regional watershed networks containing hundreds of gates, sluices, and farm spurs.
 * Much faster than Edmonds-Karp for large sparse agricultural networks.
 * 
 * Time Complexity: O(V^2 * E) for general networks, O(E * sqrt(V)) for unit networks
 * Space Complexity: O(V + E)
 */
public class DinicMaxFlow {

    private final FlowNetwork network;
    private final int source;
    private final int sink;
    private final int[] level;
    private final int[] ptr;

    public DinicMaxFlow(FlowNetwork network, int source, int sink) {
        this.network = network;
        this.source = source;
        this.sink = sink;
        this.level = new int[network.getNumVertices()];
        this.ptr = new int[network.getNumVertices()];
    }

    /**
     * Builds the Level Graph using BFS.
     * Returns true if sink is reachable from source in the residual network.
     */
    private boolean bfs() {
        Arrays.fill(level, -1);
        level[source] = 0;

        Queue<Integer> queue = new LinkedList<>();
        queue.add(source);

        while (!queue.isEmpty()) {
            int u = queue.poll();

            for (FlowNetwork.Edge edge : network.getEdgesFrom(u)) {
                if (edge.remainingCapacity() > 0 && level[edge.v] == -1) {
                    level[edge.v] = level[u] + 1;
                    queue.add(edge.v);
                }
            }
        }

        return level[sink] != -1;
    }

    /**
     * Pushes blocking flow along admissible edges using DFS.
     */
    private int dfs(int u, int pushed) {
        if (pushed == 0 || u == sink) {
            return pushed;
        }

        List<FlowNetwork.Edge> edges = network.getEdgesFrom(u);
        for (; ptr[u] < edges.size(); ptr[u]++) {
            FlowNetwork.Edge edge = edges.get(ptr[u]);
            int tr = edge.v;

            if (level[u] + 1 != level[tr] || edge.remainingCapacity() == 0) {
                continue;
            }

            int push = dfs(tr, Math.min(pushed, edge.remainingCapacity()));
            if (push == 0) {
                continue;
            }

            edge.augment(push);
            return push;
        }

        return 0;
    }

    /**
     * Computes the maximum flow from source to sink.
     */
    public int computeMaxFlow() {
        int flow = 0;
        while (bfs()) {
            Arrays.fill(ptr, 0);
            while (true) {
                int pushed = dfs(source, Integer.MAX_VALUE);
                if (pushed == 0) break;
                flow += pushed;
            }
        }
        return flow;
    }

    public static void main(String[] args) {
        FlowNetwork net = new FlowNetwork(6);
        net.setVertexName(0, "Dam Reservoir");
        net.setVertexName(1, "Canal North");
        net.setVertexName(2, "Canal South");
        net.setVertexName(3, "Substation East");
        net.setVertexName(4, "Substation West");
        net.setVertexName(5, "Farm Fields Combined Sink");

        net.addEdge(0, 1, 10);
        net.addEdge(0, 2, 10);
        net.addEdge(1, 2, 2);
        net.addEdge(1, 3, 4);
        net.addEdge(1, 4, 8);
        net.addEdge(2, 4, 9);
        net.addEdge(3, 5, 10);
        net.addEdge(4, 5, 10);

        DinicMaxFlow dinic = new DinicMaxFlow(net, 0, 5);
        int maxFlow = dinic.computeMaxFlow();

        System.out.println("--- Dinic Max Flow Result ---");
        System.out.println("Maximum delivered water: " + maxFlow + " ML/day");
    }
}
