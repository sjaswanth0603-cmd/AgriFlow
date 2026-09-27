package com.agriflow.m1_strings;

import java.util.ArrayList;
import java.util.List;

/**
 * Rabin-Karp Algorithm using Polynomial Rolling Hash
 * 
 * Agricultural Use Case:
 * Rapidly verifies crop seed batch identifiers, fertilizer container tags,
 * and logistics barcodes across large warehouse inventories and dispatch logs.
 * 
 * Time Complexity: Average O(N + M), Worst-case O(N * M)
 * Space Complexity: O(1) auxiliary space
 */
public class RabinKarpMatcher {

    private static final int PRIME = 101; // Base prime for hash modulus
    private static final int CHAR_SET = 256; // Character alphabet size

    /**
     * Searches for occurrences of 'pattern' in 'text' using rolling hash.
     */
    public static List<Integer> search(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return matches;
        }

        int n = text.length();
        int m = pattern.length();
        long patternHash = 0;
        long textWindowHash = 0;
        long h = 1;

        // The value of h would be "pow(CHAR_SET, m-1) % PRIME"
        for (int i = 0; i < m - 1; i++) {
            h = (h * CHAR_SET) % PRIME;
        }

        // Calculate initial hash value of pattern and first window of text
        for (int i = 0; i < m; i++) {
            patternHash = (CHAR_SET * patternHash + pattern.charAt(i)) % PRIME;
            textWindowHash = (CHAR_SET * textWindowHash + text.charAt(i)) % PRIME;
        }

        // Slide the pattern over text one by one
        for (int i = 0; i <= n - m; i++) {
            // Check if hash values match
            if (patternHash == textWindowHash) {
                // Confirm character by character to avoid spurious hash collision
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    matches.add(i);
                }
            }

            // Calculate hash value for next text window: Remove leading digit, add trailing digit
            if (i < n - m) {
                textWindowHash = (CHAR_SET * (textWindowHash - text.charAt(i) * h) + text.charAt(i + m)) % PRIME;

                // We might get negative value of textWindowHash, converting it to positive
                if (textWindowHash < 0) {
                    textWindowHash = (textWindowHash + PRIME);
                }
            }
        }

        return matches;
    }

    public static void main(String[] args) {
        String inventoryLog = "BATCH-AGRI-9821, BATCH-AGRI-4412, BATCH-AGRI-9821-EX, BATCH-AGRI-1002";
        String searchBatch = "BATCH-AGRI-9821";
        List<Integer> found = search(inventoryLog, searchBatch);
        System.out.println("Rabin-Karp matched batch at positions: " + found);
    }
}
