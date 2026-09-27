package com.agriflow.m3_dp;

/**
 * Needleman-Wunsch Dynamic Programming Algorithm for Global Sequence Alignment
 * 
 * Agricultural Use Case:
 * Aligns full-length genomic sequences of crop varieties (e.g., comparing wild drought-resistant
 * ancestral rice strains against modern high-yield cultivars).
 * 
 * Time Complexity: O(N * M)
 * Space Complexity: O(N * M)
 */
public class NeedlemanWunschAligner {

    private final int matchScore;
    private final int mismatchPenalty;
    private final int gapPenalty;

    public NeedlemanWunschAligner() {
        this(1, -1, -2); // Standard scoring parameters
    }

    public NeedlemanWunschAligner(int matchScore, int mismatchPenalty, int gapPenalty) {
        this.matchScore = matchScore;
        this.mismatchPenalty = mismatchPenalty;
        this.gapPenalty = gapPenalty;
    }

    public static class AlignmentResult {
        public final String alignedSeq1;
        public final String alignedSeq2;
        public final String matchBar;
        public final int score;
        public final double identityPercentage;

        public AlignmentResult(String alignedSeq1, String alignedSeq2, String matchBar, int score, double identityPercentage) {
            this.alignedSeq1 = alignedSeq1;
            this.alignedSeq2 = alignedSeq2;
            this.matchBar = matchBar;
            this.score = score;
            this.identityPercentage = identityPercentage;
        }

        @Override
        public String toString() {
            return String.format("Score: %d | Identity: %.1f%%\nSeq 1: %s\nMatch: %s\nSeq 2: %s",
                    score, identityPercentage, alignedSeq1, matchBar, alignedSeq2);
        }
    }

    /**
     * Executes global alignment on two genetic sequences.
     */
    public AlignmentResult align(String seq1, String seq2) {
        int n = seq1.length();
        int m = seq2.length();

        int[][] dp = new int[n + 1][m + 1];

        // Initialization with cumulative gap penalties
        for (int i = 0; i <= n; i++) dp[i][0] = i * gapPenalty;
        for (int j = 0; j <= m; j++) dp[0][j] = j * gapPenalty;

        // Fill DP Matrix
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int matchMismatch = dp[i - 1][j - 1] +
                        (seq1.charAt(i - 1) == seq2.charAt(j - 1) ? matchScore : mismatchPenalty);
                int delete = dp[i - 1][j] + gapPenalty;
                int insert = dp[i][j - 1] + gapPenalty;

                dp[i][j] = Math.max(matchMismatch, Math.max(delete, insert));
            }
        }

        // Traceback from bottom-right to top-left
        StringBuilder align1 = new StringBuilder();
        StringBuilder align2 = new StringBuilder();
        StringBuilder matchBar = new StringBuilder();

        int i = n, j = m;
        int matches = 0;

        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] +
                    (seq1.charAt(i - 1) == seq2.charAt(j - 1) ? matchScore : mismatchPenalty)) {
                char c1 = seq1.charAt(i - 1);
                char c2 = seq2.charAt(j - 1);
                align1.append(c1);
                align2.append(c2);
                if (c1 == c2) {
                    matchBar.append("|");
                    matches++;
                } else {
                    matchBar.append(".");
                }
                i--;
                j--;
            } else if (i > 0 && dp[i][j] == dp[i - 1][j] + gapPenalty) {
                align1.append(seq1.charAt(i - 1));
                align2.append("-");
                matchBar.append(" ");
                i--;
            } else {
                align1.append("-");
                align2.append(seq2.charAt(j - 1));
                matchBar.append(" ");
                j--;
            }
        }

        String a1 = align1.reverse().toString();
        String a2 = align2.reverse().toString();
        String mb = matchBar.reverse().toString();
        double identity = a1.isEmpty() ? 0 : (100.0 * matches / a1.length());

        return new AlignmentResult(a1, a2, mb, dp[n][m], identity);
    }

    public static void main(String[] args) {
        NeedlemanWunschAligner aligner = new NeedlemanWunschAligner();
        String wildWheat = "ATGCGTACAGTAGCTAGCT";
        String hybridWheat = "ATGCTACGGTAGCTAGCT";

        AlignmentResult result = aligner.align(wildWheat, hybridWheat);
        System.out.println("--- Global Crop DNA Alignment ---");
        System.out.println(result);
    }
}
