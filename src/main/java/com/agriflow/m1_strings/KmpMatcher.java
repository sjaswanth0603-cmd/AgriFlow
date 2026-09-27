package com.agriflow.m1_strings;

import java.util.ArrayList;
import java.util.List;

/**
 * Knuth-Morris-Pratt (KMP) Pattern Matching Algorithm
 * 
 * Agricultural Use Case:
 * Rapidly searches for specific crop varieties, disease names, or symptoms
 * within lengthy agronomic field inspection reports and soil logs without backtracking.
 * 
 * Time Complexity: O(N + M) where N = text length, M = pattern length
 * Space Complexity: O(M) for the Longest Prefix Suffix (LPS) array
 */
public class KmpMatcher {

    /**
     * Searches for all occurrences of 'pattern' in 'text'.
     *
     * @param text    The full agricultural report or document.
     * @param pattern The crop, pest, or symptom term to locate.
     * @return A list of 0-based starting indices where the pattern was found.
     */
    public static List<Integer> search(String text, String pattern) {
        List<Integer> occurrences = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return occurrences;
        }

        int[] lps = computeLpsArray(pattern);
        int i = 0; // index for text
        int j = 0; // index for pattern

        while (i < text.length()) {
            // Case 1: Characters match
            if (Character.toLowerCase(text.charAt(i)) == Character.toLowerCase(pattern.charAt(j))) {
                i++;
                j++;
            }

            // If the entire pattern matched, record the starting index
            if (j == pattern.length()) {
                occurrences.add(i - j);
                // Shift pattern according to the LPS array to continue finding overlaps
                j = lps[j - 1];
            } else if (i < text.length() && Character.toLowerCase(text.charAt(i)) != Character.toLowerCase(pattern.charAt(j))) {
                // Case 2: Mismatch after j matches
                if (j != 0) {
                    j = lps[j - 1]; // Skip unnecessary comparisons using precomputed prefix
                } else {
                    i++;
                }
            }
        }

        return occurrences;
    }

    /**
     * Precomputes the Longest Proper Prefix that is also a Suffix (LPS).
     */
    public static int[] computeLpsArray(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int length = 0; // length of the previous longest prefix suffix
        int i = 1;

        lps[0] = 0; // LPS of single character is always 0

        while (i < m) {
            if (Character.toLowerCase(pattern.charAt(i)) == Character.toLowerCase(pattern.charAt(length))) {
                length++;
                lps[i] = length;
                i++;
            } else {
                if (length != 0) {
                    // Fall back to previous prefix
                    length = lps[length - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    // Quick standalone test / demo
    public static void main(String[] args) {
        String report = "Field 4 showed severe Early Blight on tomato leaves. Late Blight was also suspected in Zone B.";
        String query = "Blight";
        List<Integer> matches = search(report, query);
        System.out.println("Searching for: '" + query + "'");
        System.out.println("Matches found at indices: " + matches);
        for (int idx : matches) {
            System.out.println("-> Snippet: " + report.substring(Math.max(0, idx - 10), Math.min(report.length(), idx + query.length() + 10)));
        }
    }
}
