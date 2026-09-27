package com.agriflow.m5_np;

import java.util.*;

/**
 * Polynomial-Time Reductions Demonstrator:
 * 3-SAT -> CLIQUE -> INDEPENDENT SET -> VERTEX COVER
 * 
 * Agricultural Use Case:
 * Demonstrates the structural equivalence between:
 * 1. Agricultural planning rules (Satisfiability)
 * 2. Non-conflicting crop portfolios (Independent Set)
 * 3. Minimal infrastructure placement to cover all conflicts (Vertex Cover)
 */
public class ReductionGraphMapper {

    public static class Graph {
        public final int numVertices;
        public final Set<String> vertexLabels;
        public final Map<String, List<String>> adj;

        public Graph(int numVertices) {
            this.numVertices = numVertices;
            this.vertexLabels = new LinkedHashSet<>();
            this.adj = new HashMap<>();
        }

        public void addVertex(String v) {
            vertexLabels.add(v);
            adj.putIfAbsent(v, new ArrayList<>());
        }

        public void addEdge(String u, String v) {
            addVertex(u);
            addVertex(v);
            if (!adj.get(u).contains(v)) adj.get(u).add(v);
            if (!adj.get(v).contains(u)) adj.get(v).add(u);
        }

        public boolean hasEdge(String u, String v) {
            return adj.containsKey(u) && adj.get(u).contains(v);
        }
    }

    public static class ReductionTrace {
        public final String stage;
        public final String explanation;
        public final List<String> details;

        public ReductionTrace(String stage, String explanation, List<String> details) {
            this.stage = stage;
            this.explanation = explanation;
            this.details = details;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== ").append(stage).append(" ===\n");
            sb.append(explanation).append("\n");
            for (String d : details) {
                sb.append("  * ").append(d).append("\n");
            }
            return sb.toString();
        }
    }

    /**
     * Reduces a 3-SAT formula with k clauses to a CLIQUE graph problem.
     * Each literal in each clause becomes a vertex.
     * Edges connect vertices from different clauses that are mutually compatible (not negations).
     */
    public static Graph reduce3SatToClique(List<CropConstraintSatSolver.Clause> clauses) {
        Graph g = new Graph(clauses.size() * 3);

        for (int i = 0; i < clauses.size(); i++) {
            CropConstraintSatSolver.Clause c = clauses.get(i);
            for (int pos = 0; pos < c.literals.size(); pos++) {
                String nodeName = String.format("C%d_L%d[x%d]", i + 1, pos + 1, c.literals.get(pos));
                g.addVertex(nodeName);
            }
        }

        List<String> nodes = new ArrayList<>(g.vertexLabels);
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                String u = nodes.get(i);
                String v = nodes.get(j);

                int clauseU = Integer.parseInt(u.substring(1, u.indexOf('_')));
                int clauseV = Integer.parseInt(v.substring(1, v.indexOf('_')));

                // Connect only if they belong to DIFFERENT clauses and are NOT contradictory
                if (clauseU != clauseV) {
                    int litU = Integer.parseInt(u.substring(u.indexOf('[') + 2, u.indexOf(']')));
                    int litV = Integer.parseInt(v.substring(v.indexOf('[') + 2, v.indexOf(']')));

                    if (litU != -litV) { // Not negations of each other
                        g.addEdge(u, v);
                    }
                }
            }
        }

        return g;
    }

    /**
     * Converts a Graph to its Complement Graph (CLIQUE -> INDEPENDENT SET).
     */
    public static Graph complementGraph(Graph original) {
        Graph complement = new Graph(original.numVertices);
        List<String> nodes = new ArrayList<>(original.vertexLabels);

        for (String node : nodes) {
            complement.addVertex(node);
        }

        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                String u = nodes.get(i);
                String v = nodes.get(j);
                if (!original.hasEdge(u, v)) {
                    complement.addEdge(u, v);
                }
            }
        }

        return complement;
    }

    /**
     * Derives Vertex Cover from an Independent Set:
     * In graph G = (V, E), S is an Independent Set <=> (V \ S) is a Vertex Cover.
     */
    public static Set<String> independentSetToVertexCover(Graph g, Set<String> independentSet) {
        Set<String> vertexCover = new LinkedHashSet<>(g.vertexLabels);
        vertexCover.removeAll(independentSet);
        return vertexCover;
    }

    public static void main(String[] args) {
        List<CropConstraintSatSolver.Clause> clauses = List.of(
            new CropConstraintSatSolver.Clause(1, 2),
            new CropConstraintSatSolver.Clause(-1, 3)
        );

        System.out.println("1. Reducing 3-SAT to CLIQUE Graph...");
        Graph cliqueGraph = reduce3SatToClique(clauses);
        System.out.println("   Constructed " + cliqueGraph.vertexLabels.size() + " vertices and transitions.");

        System.out.println("2. Reducing CLIQUE to INDEPENDENT SET via Graph Complement...");
        Graph complement = complementGraph(cliqueGraph);
        System.out.println("   Complement graph generated successfully.");

        System.out.println("3. Duality: INDEPENDENT SET <-> VERTEX COVER");
        Set<String> sampleIndependentSet = Set.of("C1_L1[x1]", "C2_L2[x3]");
        Set<String> derivedCover = independentSetToVertexCover(cliqueGraph, sampleIndependentSet);
        System.out.println("   Independent Set: " + sampleIndependentSet);
        System.out.println("   Derived Vertex Cover: " + derivedCover);
    }
}
