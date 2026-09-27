package com.agriflow.m5_np;

import java.util.*;

/**
 * 3-SAT Solver for Agricultural Planning and Constraint Satisfaction
 * 
 * Agricultural Use Case:
 * Evaluates complex agronomic rules and farmer constraints:
 * - Crop rotation laws (e.g., must plant legumes after nitrogen-depleting grain)
 * - Soil compatibility rules
 * - Solar exposure and chemical spray restrictions
 * 
 * Uses Davis-Putnam-Logemann-Loveland (DPLL) with Unit Propagation.
 * Time Complexity: Exponential in worst-case O(2^V), but fast in practice for farm constraints.
 */
public class CropConstraintSatSolver {

    public static class Clause {
        // A list of signed integer literals: positive for variable, negative for negation
        // e.g., 1 = Plant Corn, -1 = Do Not Plant Corn
        public final List<Integer> literals;

        public Clause(int... lits) {
            this.literals = new ArrayList<>();
            for (int l : lits) literals.add(l);
        }

        public Clause(List<Integer> lits) {
            this.literals = new ArrayList<>(lits);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("(");
            for (int i = 0; i < literals.size(); i++) {
                int lit = literals.get(i);
                sb.append(lit > 0 ? "x" + lit : "~x" + (-lit));
                if (i < literals.size() - 1) sb.append(" v ");
            }
            sb.append(")");
            return sb.toString();
        }
    }

    public static class SatResult {
        public final boolean satisfiable;
        public final Map<Integer, Boolean> model;

        public SatResult(boolean satisfiable, Map<Integer, Boolean> model) {
            this.satisfiable = satisfiable;
            this.model = model;
        }

        @Override
        public String toString() {
            if (!satisfiable) return "UNSATISFIABLE (Conflicting Constraints)";
            StringBuilder sb = new StringBuilder("SATISFIABLE\nValid Assignment:\n");
            for (Map.Entry<Integer, Boolean> entry : model.entrySet()) {
                sb.append(String.format("  Variable x%d = %s\n", entry.getKey(), entry.getValue()));
            }
            return sb.toString();
        }
    }

    /**
     * Solves the CNF formula using DPLL algorithm.
     */
    public static SatResult solve(List<Clause> clauses, int numVars) {
        Map<Integer, Boolean> assignment = new HashMap<>();
        boolean sat = dpll(clauses, assignment, numVars);
        return new SatResult(sat, assignment);
    }

    private static boolean dpll(List<Clause> clauses, Map<Integer, Boolean> assignment, int numVars) {
        // 1. Unit propagation
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Clause c : clauses) {
                // Determine clause status under current partial assignment
                int unassignedLit = 0;
                int unassignedCount = 0;
                boolean clauseSatisfied = false;

                for (int lit : c.literals) {
                    int var = Math.abs(lit);
                    boolean sign = lit > 0;
                    if (assignment.containsKey(var)) {
                        if (assignment.get(var) == sign) {
                            clauseSatisfied = true;
                            break;
                        }
                    } else {
                        unassignedCount++;
                        unassignedLit = lit;
                    }
                }

                if (clauseSatisfied) continue;

                // Conflict detected: all literals evaluated to false
                if (unassignedCount == 0) {
                    return false;
                }

                // Unit clause: exactly one unassigned literal
                if (unassignedCount == 1) {
                    int var = Math.abs(unassignedLit);
                    boolean val = unassignedLit > 0;
                    assignment.put(var, val);
                    changed = true;
                    break;
                }
            }
        }

        // 2. Check if all clauses are satisfied
        boolean allSatisfied = true;
        for (Clause c : clauses) {
            boolean sat = false;
            for (int lit : c.literals) {
                int var = Math.abs(lit);
                boolean sign = lit > 0;
                if (assignment.containsKey(var) && assignment.get(var) == sign) {
                    sat = true;
                    break;
                }
            }
            if (!sat) {
                allSatisfied = false;
                break;
            }
        }
        if (allSatisfied) return true;

        // 3. Choose next unassigned variable for branching
        int nextVar = -1;
        for (int v = 1; v <= numVars; v++) {
            if (!assignment.containsKey(v)) {
                nextVar = v;
                break;
            }
        }
        if (nextVar == -1) return false;

        // Try assigning TRUE
        assignment.put(nextVar, true);
        if (dpll(clauses, assignment, numVars)) return true;

        // Backtrack and try assigning FALSE
        assignment.put(nextVar, false);
        if (dpll(clauses, assignment, numVars)) return true;

        // Backtrack completely
        assignment.remove(nextVar);
        return false;
    }

    public static void main(String[] args) {
        // Variables:
        // x1: Plant Corn in Plot A
        // x2: Plant Soybeans in Plot A
        // x3: Use High Nitrogen Drip Irrigation
        List<Clause> farmRules = List.of(
            new Clause(1, 2),      // Must plant either Corn (x1) or Soybeans (x2)
            new Clause(-1, -2),    // Cannot plant both Corn and Soybeans in the same plot
            new Clause(-1, 3),     // If Corn (x1) is planted, high nitrogen drip (x3) is required
            new Clause(-2, -3)     // If Soybeans (x2) are planted, high nitrogen drip (-x3) is prohibited (they fix nitrogen naturally!)
        );

        System.out.println("Farm Planning Constraints:");
        for (Clause c : farmRules) System.out.println("  " + c);

        SatResult res = solve(farmRules, 3);
        System.out.println("\nResult:");
        System.out.println(res);
    }
}
