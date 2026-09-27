package com.agriflow.m4_flow;

import java.util.ArrayList;
import java.util.List;

/**
 * Graph Data Structure for Agricultural Irrigation & Pipeline Networks
 */
public class FlowNetwork {

    public static class Edge {
        public final int u;
        public final int v;
        public final int capacity;
        public int flow;
        public Edge residual;

        public Edge(int u, int v, int capacity) {
            this.u = u;
            this.v = v;
            this.capacity = capacity;
            this.flow = 0;
        }

        public int remainingCapacity() {
            return capacity - flow;
        }

        public void augment(int bottleneck) {
            flow += bottleneck;
            residual.flow -= bottleneck;
        }
    }

    private final int numVertices;
    private final List<List<Edge>> adj;
    private final String[] vertexNames;

    public FlowNetwork(int numVertices) {
        this.numVertices = numVertices;
        this.adj = new ArrayList<>(numVertices);
        this.vertexNames = new String[numVertices];
        for (int i = 0; i < numVertices; i++) {
            adj.add(new ArrayList<>());
            vertexNames[i] = "Node_" + i;
        }
    }

    public void setVertexName(int v, String name) {
        if (v >= 0 && v < numVertices) {
            vertexNames[v] = name;
        }
    }

    public String getVertexName(int v) {
        return vertexNames[v];
    }

    public void addEdge(int u, int v, int capacity) {
        Edge forward = new Edge(u, v, capacity);
        Edge backward = new Edge(v, u, 0); // Residual backward edge
        forward.residual = backward;
        backward.residual = forward;
        adj.get(u).add(forward);
        adj.get(v).add(backward);
    }

    public int getNumVertices() {
        return numVertices;
    }

    public List<Edge> getEdgesFrom(int u) {
        return adj.get(u);
    }

    public List<List<Edge>> getAdj() {
        return adj;
    }
}
