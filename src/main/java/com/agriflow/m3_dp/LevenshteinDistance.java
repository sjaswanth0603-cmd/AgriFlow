package com.agriflow.m3_dp;

import java.util.*;

/**
 * Levenshtein & Damerau-Levenshtein Distance for Agricultural Query Correction
 * 
 * Agricultural Use Case:
 * Corrects typographical errors in farmer voice/text queries
 * (e.g., "potatos" -> "potatoes", "fertlizr" -> "fertilizer", "blite" -> "blight").
 * 
 * Time Complexity: O(N * M)
 * Space Complexity: O(N * M) or O(min(N, M))
 */
public class LevenshteinDistance {

    public static final List<String> AGRI_DICTIONARY = Arrays.asList(
        "wheat", "barley", "maize", "rice", "potatoes", "tomatoes", "cotton", "sugarcane",
        "fertilizer", "pesticide", "irrigation", "nitrogen", "phosphorus", "potassium",
        "early blight", "late blight", "powdery mildew", "leaf rust", "root rot", "anthracnose"
    );

    public static class Suggestion {
        public final String term;
        public final int distance;

        public Suggestion(String term, int distance) {
            this.term = term;
            this.distance = distance;
        }

        @Override
        public String toString() {
            return term + " (distance: " + distance + ")";
        }
    }

    /**
     * Calculates Damerau-Levenshtein distance (supports transposition of adjacent characters).
     */
    public static int computeDistance(String s1, String s2) {
        if (s1 == null || s2 == null) return Integer.MAX_VALUE;
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int n = s1.length();
        int m = s2.length();
        int[][] dp = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) dp[i][0] = i;
        for (int j = 0; j <= m; j++) dp[0][j] = j;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;

                dp[i][j] = Math.min(
                    Math.min(dp[i - 1][j] + 1,      // Deletion
                             dp[i][j - 1] + 1),     // Insertion
                    dp[i - 1][j - 1] + cost         // Substitution
                );

                // Damerau transposition check
                if (i > 1 && j > 1 &&
                    s1.charAt(i - 1) == s2.charAt(j - 2) &&
                    s1.charAt(i - 2) == s2.charAt(j - 1)) {
                    dp[i][j] = Math.min(dp[i][j], dp[i - 2][j - 2] + 1);
                }
            }
        }

        return dp[n][m];
    }

    /**
     * Suggests the closest dictionary agricultural term for a user query.
     */
    public static Suggestion findBestMatch(String inputQuery) {
        List<Suggestion> top = findTopMatches(inputQuery, 1);
        return top.isEmpty() ? new Suggestion("none", Integer.MAX_VALUE) : top.get(0);
    }

    /**
     * Returns the top K closest dictionary suggestions ranked by edit distance.
     */
    public static List<Suggestion> findTopMatches(String inputQuery, int limit) {
        List<Suggestion> list = new ArrayList<>();
        if (inputQuery == null || inputQuery.trim().isEmpty()) {
            return list;
        }

        String cleaned = inputQuery.trim();
        for (String term : AGRI_DICTIONARY) {
            int dist = computeDistance(cleaned, term);
            list.add(new Suggestion(term, dist));
        }

        list.sort(Comparator.comparingInt(s -> s.distance));
        return list.subList(0, Math.min(limit, list.size()));
    }

    public static void main(String[] args) {
        String[] farmerQueries = {"fertlzr", "tmtos", "blite", "potasium", "irigation"};

        System.out.println("--- Farmer Query Autocorrection ---");
        for (String query : farmerQueries) {
            Suggestion match = findBestMatch(query);
            System.out.printf("Farmer typed: '%-10s' -> Suggested Term: '%s' (Edit Distance: %d)%n",
                    query, match.term, match.distance);
        }
    }
}
