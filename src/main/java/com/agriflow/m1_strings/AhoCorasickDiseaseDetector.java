package com.agriflow.m1_strings;

import java.util.*;

/**
 * Aho-Corasick Multi-Pattern String Matching Algorithm
 * 
 * Agricultural Use Case:
 * Scans comprehensive crop diagnostics, extension agent notes, and research bulletins
 * to detect dozens of disease and pest symptoms simultaneously in a single linear pass.
 * 
 * Time Complexity: O(N + M + Z) where N is text length, M is sum of pattern lengths, Z is match count
 * Space Complexity: O(M * AlphabetSize) for the Trie with failure transitions
 */
public class AhoCorasickDiseaseDetector {

    public static class MatchResult {
        public final int index; // starting index in text
        public final String keyword;

        public MatchResult(int index, String keyword) {
            this.index = index;
            this.keyword = keyword;
        }

        @Override
        public String toString() {
            return "[" + keyword + " @ index " + index + "]";
        }
    }

    private static class Node {
        Map<Character, Node> children = new HashMap<>();
        Node fail = null;
        List<String> output = new ArrayList<>();
    }

    private final Node root;

    public AhoCorasickDiseaseDetector(List<String> keywords) {
        root = new Node();
        buildTrie(keywords);
        buildFailureLinks();
    }

    /**
     * Phase 1: Build the standard Trie from agricultural dictionary keywords.
     */
    private void buildTrie(List<String> keywords) {
        for (String keyword : keywords) {
            if (keyword == null || keyword.isEmpty()) continue;
            Node current = root;
            for (char ch : keyword.toLowerCase().toCharArray()) {
                current = current.children.computeIfAbsent(ch, c -> new Node());
            }
            current.output.add(keyword);
        }
    }

    /**
     * Phase 2: Construct failure links using Breadth-First Search (BFS).
     */
    private void buildFailureLinks() {
        Queue<Node> queue = new LinkedList<>();

        // Level 1 children fail back to root
        for (Node child : root.children.values()) {
            child.fail = root;
            queue.add(child);
        }

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            for (Map.Entry<Character, Node> entry : current.children.entrySet()) {
                char ch = entry.getKey();
                Node child = entry.getValue();
                queue.add(child);

                Node failNode = current.fail;
                while (failNode != null && !failNode.children.containsKey(ch)) {
                    failNode = failNode.fail;
                }

                child.fail = (failNode != null) ? failNode.children.get(ch) : root;
                // Merge matched keywords from failure node
                if (child.fail != null) {
                    child.output.addAll(child.fail.output);
                }
            }
        }
    }

    /**
     * Phase 3: Scan agricultural report text in a single pass.
     */
    public List<MatchResult> scan(String text) {
        List<MatchResult> results = new ArrayList<>();
        if (text == null || text.isEmpty()) return results;

        Node current = root;
        String lower = text.toLowerCase();

        for (int i = 0; i < text.length(); i++) {
            char ch = lower.charAt(i);

            // Follow failure transitions if child does not exist
            while (current != root && !current.children.containsKey(ch)) {
                current = current.fail;
            }

            if (current.children.containsKey(ch)) {
                current = current.children.get(ch);
            } else {
                current = root;
            }

            // Report any matched keywords that end at this position
            for (String matched : current.output) {
                int startIndex = i - matched.length() + 1;
                results.add(new MatchResult(startIndex, matched));
            }
        }

        return results;
    }

    public static void main(String[] args) {
        List<String> diseaseKeywords = Arrays.asList("Blight", "Rust", "Powdery Mildew", "Root Rot", "Aphids", "Canker");
        AhoCorasickDiseaseDetector detector = new AhoCorasickDiseaseDetector(diseaseKeywords);

        String sampleAgronomyReport = 
            "Field Inspection Report: Section A displays initial symptoms of Powdery Mildew and Leaf Rust. " +
            "No sign of Root Rot was observed, though sporadic Aphids were noted near the irrigation ditch.";

        List<MatchResult> hits = detector.scan(sampleAgronomyReport);
        System.out.println("Discovered Disease/Pest mentions (" + hits.size() + "):");
        for (MatchResult hit : hits) {
            System.out.println(" -> " + hit);
        }
    }
}
