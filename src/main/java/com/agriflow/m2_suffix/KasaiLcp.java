package com.agriflow.m2_suffix;

/**
 * Kasai's Algorithm for Longest Common Prefix (LCP) Array Construction
 * 
 * Agricultural Use Case:
 * Compares research papers and diagnostic bulletins across different agricultural research institutes
 * to detect shared findings, common treatment protocols, and document overlap in linear time.
 * 
 * Time Complexity: O(N) linear time
 * Space Complexity: O(N) auxiliary space
 */
public class KasaiLcp {

    public static class CommonPhraseResult {
        public final String phrase;
        public final int length;
        public final int index1;
        public final int index2;

        public CommonPhraseResult(String phrase, int length, int index1, int index2) {
            this.phrase = phrase;
            this.length = length;
            this.index1 = index1;
            this.index2 = index2;
        }

        @Override
        public String toString() {
            return String.format("Phrase: \"%s\" (Length: %d) between indices %d and %d", phrase, length, index1, index2);
        }
    }

    /**
     * Builds the LCP array in O(N) using Kasai's algorithm.
     * 
     * @param text The source text.
     * @param sa   The precomputed Suffix Array for the text.
     * @return An integer array where lcp[i] is the length of common prefix between sa[i] and sa[i+1].
     */
    public static int[] buildLcpArray(String text, int[] sa) {
        int n = text.length();
        int[] lcp = new int[n];
        int[] rank = new int[n];

        // Store inverse of suffix array (the rank of suffix starting at i)
        for (int i = 0; i < n; i++) {
            rank[sa[i]] = i;
        }

        int h = 0; // Current LCP length

        // Process suffixes in order of their position in the original text
        for (int i = 0; i < n; i++) {
            if (rank[i] > 0) {
                int j = sa[rank[i] - 1]; // Suffix that comes immediately before sa[rank[i]] in sorted order

                // Extend matching characters
                while (i + h < n && j + h < n && text.charAt(i + h) == text.charAt(j + h)) {
                    h++;
                }

                lcp[rank[i]] = h;

                // For the next suffix, LCP is at least h - 1 (Kasai's key observation)
                if (h > 0) {
                    h--;
                }
            } else {
                lcp[0] = 0;
            }
        }

        return lcp;
    }

    /**
     * Finds the longest repeated phrase in an agricultural report using SA and LCP.
     */
    public static CommonPhraseResult findLongestRepeatedPhrase(String text, int[] sa, int[] lcp) {
        int maxLen = 0;
        int maxIndex = -1;

        for (int i = 1; i < lcp.length; i++) {
            if (lcp[i] > maxLen) {
                maxLen = lcp[i];
                maxIndex = i;
            }
        }

        if (maxLen > 0 && maxIndex != -1) {
            int start = sa[maxIndex];
            String phrase = text.substring(start, start + maxLen);
            return new CommonPhraseResult(phrase, maxLen, sa[maxIndex - 1], sa[maxIndex]);
        }

        return new CommonPhraseResult("", 0, -1, -1);
    }

    public static void main(String[] args) {
        String report = "Apply potassium fertilizer during early flowering. Inspect weekly. Apply potassium fertilizer during early flowering.";
        int[] sa = SuffixArrayBuilder.buildSuffixArray(report);
        int[] lcp = buildLcpArray(report, sa);

        CommonPhraseResult longest = findLongestRepeatedPhrase(report, sa, lcp);
        System.out.println("Longest repeated agricultural protocol:");
        System.out.println(longest);
    }
}
