package com.agriflow.m3_dp;

/**
 * Smith-Waterman Dynamic Programming Algorithm for Local Sequence Alignment
 * 
 * Agricultural Use Case:
 * Locates conserved local genetic motifs (e.g., fungal resistance alleles, salt-tolerance markers)
 * shared between distinct plant species or varieties.
 * 
 * Time Complexity: O(N * M)
 * Space Complexity: O(N * M)
 */
public class SmithWatermanAligner {

    private final int matchScore;
    private final int mismatchPenalty;
    private final int gapPenalty;

    public SmithWatermanAligner() {
        this(2, -1, -2);
    }

    public SmithWatermanAligner(int matchScore, int mismatchPenalty, int gapPenalty) {
        this.matchScore = matchScore;
        this.mismatchPenalty = mismatchPenalty;
        this.gapPenalty = gapPenalty;
    }

    public static class LocalAlignmentResult {
        public final String localSeq1;
        public final String localSeq2;
        public final int maxScore;
        public final int startPos1;
        public final int startPos2;

        public LocalAlignmentResult(String localSeq1, String localSeq2, int maxScore, int startPos1, int startPos2) {
            this.localSeq1 = localSeq1;
            this.localSeq2 = localSeq2;
            this.maxScore = maxScore;
            this.startPos1 = startPos1;
            this.startPos2 = startPos2;
        }

        @Override
        public String toString() {
            return String.format("Local Max Score: %d\nSub-Region 1 (from %d): %s\nSub-Region 2 (from %d): %s",
                    maxScore, startPos1, localSeq1, startPos2, localSeq2);
        }
    }

    public LocalAlignmentResult align(String seq1, String seq2) {
        int n = seq1.length();
        int m = seq2.length();

        int[][] dp = new int[n + 1][m + 1];
        int maxVal = 0;
        int maxI = 0, maxJ = 0;

        // Fill matrix with local threshold (cannot drop below 0)
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int matchMismatch = dp[i - 1][j - 1] +
                        (seq1.charAt(i - 1) == seq2.charAt(j - 1) ? matchScore : mismatchPenalty);
                int delete = dp[i - 1][j] + gapPenalty;
                int insert = dp[i][j - 1] + gapPenalty;

                dp[i][j] = Math.max(0, Math.max(matchMismatch, Math.max(delete, insert)));

                if (dp[i][j] > maxVal) {
                    maxVal = dp[i][j];
                    maxI = i;
                    maxJ = j;
                }
            }
        }

        // Traceback from maximum scoring cell until reaching 0
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();

        int i = maxI, j = maxJ;
        while (i > 0 && j > 0 && dp[i][j] > 0) {
            if (dp[i][j] == dp[i - 1][j - 1] + (seq1.charAt(i - 1) == seq2.charAt(j - 1) ? matchScore : mismatchPenalty)) {
                sb1.append(seq1.charAt(i - 1));
                sb2.append(seq2.charAt(j - 1));
                i--;
                j--;
            } else if (dp[i][j] == dp[i - 1][j] + gapPenalty) {
                sb1.append(seq1.charAt(i - 1));
                sb2.append("-");
                i--;
            } else {
                sb1.append("-");
                sb2.append(seq2.charAt(j - 1));
                j--;
            }
        }

        return new LocalAlignmentResult(
                sb1.reverse().toString(),
                sb2.reverse().toString(),
                maxVal,
                i,
                j
        );
    }

    public static void main(String[] args) {
        SmithWatermanAligner sw = new SmithWatermanAligner();
        String cropGeneA = "GGGTACGTAAAACCCCGG";
        String cropGeneB = "AAAATACGTAAATTTTTT";

        LocalAlignmentResult result = sw.align(cropGeneA, cropGeneB);
        System.out.println("--- Local Gene Alignment (Resistance Motif Discovery) ---");
        System.out.println(result);
    }
}
