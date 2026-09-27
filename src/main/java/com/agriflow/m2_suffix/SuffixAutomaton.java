package com.agriflow.m2_suffix;

import java.util.HashMap;
import java.util.Map;

/**
 * Suffix Automaton (Directed Acyclic Word Graph - DAWG)
 * 
 * Agricultural Use Case:
 * Ultra-fast substring indexing for continuous sensor logs and genetic marker sequences.
 * Determines if any crop sensor pattern exists in O(M) time and computes total distinct substrings.
 * 
 * Time Complexity: O(N) construction time, O(M) query time
 * Space Complexity: O(N) states (at most 2N - 1 states, at most 3N - 4 transitions)
 */
public class SuffixAutomaton {

    public static class State {
        public int len;
        public int link;
        public Map<Character, Integer> next = new HashMap<>();

        public State(int len, int link) {
            this.len = len;
            this.link = link;
        }
    }

    private State[] states;
    private int size;
    private int last;

    public SuffixAutomaton(String text) {
        int maxStates = 2 * text.length();
        states = new State[maxStates];
        states[0] = new State(0, -1);
        size = 1;
        last = 0;

        for (int i = 0; i < text.length(); i++) {
            extend(text.charAt(i));
        }
    }

    /**
     * Adds character c to the Suffix Automaton in amortized O(1).
     */
    private void extend(char c) {
        int cur = size++;
        states[cur] = new State(states[last].len + 1, 0);

        int p = last;
        while (p != -1 && !states[p].next.containsKey(c)) {
            states[p].next.put(c, cur);
            p = states[p].link;
        }

        if (p == -1) {
            states[cur].link = 0;
        } else {
            int q = states[p].next.get(c);
            if (states[p].len + 1 == states[q].len) {
                states[cur].link = q;
            } else {
                int clone = size++;
                states[clone] = new State(states[p].len + 1, states[q].link);
                states[clone].next.putAll(states[q].next);

                while (p != -1 && states[p].next.get(c) == q) {
                    states[p].next.put(c, clone);
                    p = states[p].link;
                }

                states[q].link = clone;
                states[cur].link = clone;
            }
        }
        last = cur;
    }

    /**
     * Checks if a pattern exists in the indexed agricultural text in O(M) time.
     */
    public boolean contains(String pattern) {
        int cur = 0;
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (!states[cur].next.containsKey(c)) {
                return false;
            }
            cur = states[cur].next.get(c);
        }
        return true;
    }

    /**
     * Counts the total number of distinct substrings in the agricultural text in O(N).
     */
    public long countDistinctSubstrings() {
        long total = 0;
        for (int i = 1; i < size; i++) {
            total += states[i].len - states[states[i].link].len;
        }
        return total;
    }

    public int getStateCount() {
        return size;
    }

    public static void main(String[] args) {
        String geneStream = "ATGCGTACGTTAGC";
        SuffixAutomaton sam = new SuffixAutomaton(geneStream);

        System.out.println("Gene stream: " + geneStream);
        System.out.println("Automaton states created: " + sam.getStateCount());
        System.out.println("Total distinct genetic sub-sequences: " + sam.countDistinctSubstrings());

        String probe = "TACGTT";
        System.out.println("Contains probe '" + probe + "': " + sam.contains(probe));
    }
}
