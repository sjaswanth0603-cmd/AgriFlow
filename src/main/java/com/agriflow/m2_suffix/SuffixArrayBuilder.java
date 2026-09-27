package com.agriflow.m2_suffix;

import java.util.Arrays;

/**
 * Suffix Array Construction and Document Indexing
 * 
 * Agricultural Use Case:
 * Creates a compact lexical index of thousands of agricultural research papers,
 * government seed catalogues, and field reports. Enables instantaneous substring queries.
 * 
 * Time Complexity:
 * - Construction: O(N log^2 N) or O(N log N) using prefix doubling
 * - Pattern Search: O(M log N) via binary search
 * Space Complexity: O(N)
 */
public class SuffixArrayBuilder {

    public static class Suffix implements Comparable<Suffix> {
        public int index;
        public int rank;
        public int nextRank;

        @Override
        public int compareTo(Suffix other) {
            if (this.rank != other.rank) {
                return Integer.compare(this.rank, other.rank);
            }
            return Integer.compare(this.nextRank, other.nextRank);
        }
    }

    /**
     * Builds the Suffix Array using Prefix Doubling algorithm.
     * 
     * @param text The agricultural text to index.
     * @return Array of starting positions representing sorted suffixes.
     */
    public static int[] buildSuffixArray(String text) {
        int n = text.length();
        Suffix[] suffixes = new Suffix[n];

        // Initialize suffixes with 1-character ranks
        for (int i = 0; i < n; i++) {
            suffixes[i] = new Suffix();
            suffixes[i].index = i;
            suffixes[i].rank = text.charAt(i);
            suffixes[i].nextRank = (i + 1 < n) ? text.charAt(i + 1) : -1;
        }

        Arrays.sort(suffixes);

        // Indices array to map suffix index to its position in sorted array
        int[] ind = new int[n];

        // Double the prefix length: 2, 4, 8, 16...
        for (int k = 4; k < 2 * n; k *= 2) {
            int rank = 0;
            int prevRank = suffixes[0].rank;
            suffixes[0].rank = rank;
            ind[suffixes[0].index] = 0;

            // Assigning ranks to suffixes
            for (int i = 1; i < n; i++) {
                if (suffixes[i].rank == prevRank && suffixes[i].nextRank == suffixes[i - 1].nextRank) {
                    prevRank = suffixes[i].rank;
                    suffixes[i].rank = rank;
                } else {
                    prevRank = suffixes[i].rank;
                    suffixes[i].rank = ++rank;
                }
                ind[suffixes[i].index] = i;
            }

            // Assign nextRank for each suffix
            for (int i = 0; i < n; i++) {
                int nextIndex = suffixes[i].index + k / 2;
                suffixes[i].nextRank = (nextIndex < n) ? suffixes[ind[nextIndex]].rank : -1;
            }

            Arrays.sort(suffixes);
        }

        int[] suffixArr = new int[n];
        for (int i = 0; i < n; i++) {
            suffixArr[i] = suffixes[i].index;
        }
        return suffixArr;
    }

    /**
     * Searches for a pattern in text using binary search over the suffix array.
     */
    public static int search(String text, String pattern, int[] suffixArr) {
        int left = 0;
        int right = suffixArr.length - 1;
        int m = pattern.length();

        while (left <= right) {
            int mid = left + (right - left) / 2;
            int suffixStart = suffixArr[mid];
            String sub = text.substring(suffixStart, Math.min(suffixStart + m, text.length()));

            int cmp = pattern.compareTo(sub);
            if (cmp == 0) {
                return suffixStart; // Match found
            } else if (cmp < 0) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        return -1; // Not found
    }

    public static void main(String[] args) {
        String report = "corn_maize_soil_nitrogen_organic_corn_nitrogen";
        int[] sa = buildSuffixArray(report);

        System.out.println("Text: " + report);
        System.out.println("Suffix Array:");
        for (int idx : sa) {
            System.out.printf("%2d: %s%n", idx, report.substring(idx));
        }

        String query = "nitrogen";
        int matchIdx = search(report, query, sa);
        System.out.println("Query '" + query + "' found at index: " + matchIdx);
    }
}
