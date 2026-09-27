package com.agriflow.m1_strings;

import java.util.ArrayList;
import java.util.List;

/**
 * Z-Algorithm for Pattern Matching & Periodic Phrase Detection
 * 
 * Agricultural Use Case:
 * Detects recurring phrases, repeated sensor error signatures, or anomalies
 * in continuous soil monitoring transcripts and crop disease observation logs.
 * 
 * Time Complexity: O(N + M) linear time
 * Space Complexity: O(N + M) for the Z-array
 */
public class ZAlgorithm {

    /**
     * Computes the Z-array for a given string S.
     * Z[i] stores the length of the longest substring starting from S[i]
     * that is also a prefix of S.
     */
    public static int[] calculateZArray(String str) {
        int n = str.length();
        int[] z = new int[n];
        int left = 0, right = 0;

        for (int i = 1; i < n; i++) {
            if (i > right) {
                // We are outside the current Z-box; start manual comparison
                left = right = i;
                while (right < n && str.charAt(right) == str.charAt(right - left)) {
                    right++;
                }
                z[i] = right - left;
                right--;
            } else {
                // We are inside the current Z-box [left, right]
                int k = i - left;
                // If value does not stretch past right edge, copy from earlier prefix
                if (z[k] < right - i + 1) {
                    z[i] = z[k];
                } else {
                    // It touches or exceeds boundary; extend manually
                    left = i;
                    while (right < n && str.charAt(right) == str.charAt(right - left)) {
                        right++;
                    }
                    z[i] = right - left;
                    right--;
                }
            }
        }
        return z;
    }

    /**
     * Searches for occurrences of a pattern in a text using the Z-array.
     * Constructs the concatenation: pattern + "$" + text
     */
    public static List<Integer> search(String text, String pattern) {
        List<Integer> occurrences = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return occurrences;
        }

        String concat = pattern + "$" + text;
        int[] z = calculateZArray(concat);
        int patternLen = pattern.length();

        for (int i = 0; i < z.length; i++) {
            // When Z[i] equals pattern length, an exact match starts here
            if (z[i] == patternLen) {
                // Adjust index relative to the original text
                occurrences.add(i - patternLen - 1);
            }
        }
        return occurrences;
    }

    public static void main(String[] args) {
        String log = "SOIL_MOISTURE_CRITICAL at Sensor 1. SOIL_MOISTURE_CRITICAL at Sensor 4.";
        String keyword = "SOIL_MOISTURE_CRITICAL";
        List<Integer> matches = search(log, keyword);
        System.out.println("Z-Algorithm Found matches at: " + matches);
    }
}
