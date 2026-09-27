package com.agriflow.server;

import com.agriflow.m1_strings.*;
import com.agriflow.m2_suffix.*;
import com.agriflow.m3_dp.*;

import java.util.*;

/**
 * REST API Dispatcher
 * Connects the web frontend to the core Java DSA algorithms.
 */
public class ApiDispatcher {

    public static String handleRequest(String path, Map<String, String> params) {
        try {
            switch (path) {
                // Status
                case "/api/status":
                    return "{\"status\":\"active\",\"project\":\"AgriFlow\",\"scope\":\"Modules 1-3 (CO1-CO3)\",\"ready\":true}";

                // Module 1: String Searching
                case "/api/m1/search":
                    return handleM1Search(params);

                // Module 2: Suffix Structures
                case "/api/m2/lcp":
                    return handleM2Lcp(params);
                case "/api/m2/query":
                    return handleM2Query(params);

                // Module 3: Advanced Dynamic Programming
                case "/api/m3/align":
                    return handleM3Align(params);
                case "/api/m3/bitmask":
                    return handleM3Bitmask(params);
                case "/api/m3/autocorrect":
                    return handleM3Autocorrect(params);
                case "/api/m3/mcm":
                    return handleM3Mcm(params);

                // Modules 4-6 (Beyond current course scope)
                case "/api/m4/maxflow":
                case "/api/m4/bipartite":
                case "/api/m5/sat":
                case "/api/m5/vertexcover":
                case "/api/m6/reservoir":
                case "/api/m6/blelloch":
                case "/api/m6/brent":
                case "/api/m6/millerrabin":
                    return "{\"error\":\"Modules 4-6 are beyond current course scope (Modules 1-3 | CO1-CO3).\"}";

                default:
                    return "{\"error\": \"Unknown API endpoint: " + path + "\"}";
            }
        } catch (Exception e) {
            return "{\"error\": \"" + escapeJson(e.getMessage()) + "\"}";
        }
    }

    private static String handleM1Search(Map<String, String> params) {
        String text = params.getOrDefault("text", "Field inspection found early blight and late blight symptoms on tomato leaves.");
        String pattern = params.getOrDefault("pattern", "blight");
        String algo = params.getOrDefault("algo", "kmp").toLowerCase();

        if ("compare".equals(algo)) {
            // Benchmark all 4 algorithms on the same text & pattern
            long t0Kmp = System.nanoTime();
            List<Integer> kmpMatches = KmpMatcher.search(text, pattern);
            long timeKmp = System.nanoTime() - t0Kmp;

            long t0Z = System.nanoTime();
            List<Integer> zMatches = ZAlgorithm.search(text, pattern);
            long timeZ = System.nanoTime() - t0Z;

            long t0Rk = System.nanoTime();
            List<Integer> rkMatches = RabinKarpMatcher.search(text, pattern);
            long timeRk = System.nanoTime() - t0Rk;

            long t0Aho = System.nanoTime();
            List<String> keywords = Arrays.asList(pattern.split(","));
            for (int i = 0; i < keywords.size(); i++) keywords.set(i, keywords.get(i).trim());
            AhoCorasickDiseaseDetector detector = new AhoCorasickDiseaseDetector(keywords);
            var ahoMatches = detector.scan(text);
            long timeAho = System.nanoTime() - t0Aho;

            StringBuilder sb = new StringBuilder("{");
            sb.append("\"mode\":\"compare\",");
            sb.append("\"textLength\":").append(text.length()).append(",");
            sb.append("\"pattern\":\"").append(escapeJson(pattern)).append("\",");
            sb.append("\"kmp\":{\"name\":\"Knuth-Morris-Pratt\",\"timeNs\":").append(timeKmp).append(",\"count\":").append(kmpMatches.size()).append("},");
            sb.append("\"z\":{\"name\":\"Z-Algorithm\",\"timeNs\":").append(timeZ).append(",\"count\":").append(zMatches.size()).append("},");
            sb.append("\"rabin\":{\"name\":\"Rabin-Karp\",\"timeNs\":").append(timeRk).append(",\"count\":").append(rkMatches.size()).append("},");
            sb.append("\"aho\":{\"name\":\"Aho-Corasick\",\"timeNs\":").append(timeAho).append(",\"count\":").append(ahoMatches.size()).append("}");
            sb.append("}");
            return sb.toString();
        }

        long t0 = System.nanoTime();
        String algorithmName;
        StringBuilder matchesJson = new StringBuilder("[");
        int matchCount = 0;

        if ("aho".equals(algo)) {
            algorithmName = "Aho-Corasick Multi-Pattern";
            List<String> keywords = Arrays.asList(pattern.split(","));
            for (int i = 0; i < keywords.size(); i++) keywords.set(i, keywords.get(i).trim());
            AhoCorasickDiseaseDetector detector = new AhoCorasickDiseaseDetector(keywords);
            List<AhoCorasickDiseaseDetector.MatchResult> ahoHits = detector.scan(text);
            long t1 = System.nanoTime();
            matchCount = ahoHits.size();

            for (int i = 0; i < ahoHits.size(); i++) {
                var hit = ahoHits.get(i);
                int start = Math.max(0, hit.index - 15);
                int end = Math.min(text.length(), hit.index + hit.keyword.length() + 15);
                String snippet = (start > 0 ? "..." : "") + text.substring(start, end).replace("\n", " ") + (end < text.length() ? "..." : "");

                matchesJson.append(String.format(Locale.US,
                    "{\"index\":%d,\"length\":%d,\"keyword\":\"%s\",\"snippet\":\"%s\"}",
                    hit.index, hit.keyword.length(), escapeJson(hit.keyword), escapeJson(snippet)));
                if (i < ahoHits.size() - 1) matchesJson.append(",");
            }
            matchesJson.append("]");

            return String.format(Locale.US,
                "{\"algorithm\":\"%s\",\"timeNs\":%d,\"matchCount\":%d,\"matches\":%s,\"text\":\"%s\"}",
                algorithmName, (t1 - t0), matchCount, matchesJson.toString(), escapeJson(text));
        } else {
            List<Integer> occurrences;
            if ("z".equals(algo)) {
                algorithmName = "Z-Algorithm";
                occurrences = ZAlgorithm.search(text, pattern);
            } else if ("rabin".equals(algo)) {
                algorithmName = "Rabin-Karp Rolling Hash";
                occurrences = RabinKarpMatcher.search(text, pattern);
            } else {
                algorithmName = "Knuth-Morris-Pratt (KMP)";
                occurrences = KmpMatcher.search(text, pattern);
            }
            long t1 = System.nanoTime();
            matchCount = occurrences.size();

            for (int i = 0; i < occurrences.size(); i++) {
                int idx = occurrences.get(i);
                int start = Math.max(0, idx - 15);
                int end = Math.min(text.length(), idx + pattern.length() + 15);
                String snippet = (start > 0 ? "..." : "") + text.substring(start, end).replace("\n", " ") + (end < text.length() ? "..." : "");

                matchesJson.append(String.format(Locale.US,
                    "{\"index\":%d,\"length\":%d,\"keyword\":\"%s\",\"snippet\":\"%s\"}",
                    idx, pattern.length(), escapeJson(pattern), escapeJson(snippet)));
                if (i < occurrences.size() - 1) matchesJson.append(",");
            }
            matchesJson.append("]");

            return String.format(Locale.US,
                "{\"algorithm\":\"%s\",\"timeNs\":%d,\"matchCount\":%d,\"matches\":%s,\"text\":\"%s\"}",
                algorithmName, (t1 - t0), matchCount, matchesJson.toString(), escapeJson(text));
        }
    }

    private static String handleM2Lcp(Map<String, String> params) {
        String text = params.getOrDefault("text", "Apply organic potassium fertilizer during early flowering. Soil inspection required. Apply organic potassium fertilizer during early flowering.");
        long t0 = System.nanoTime();
        int[] sa = SuffixArrayBuilder.buildSuffixArray(text);
        int[] lcp = KasaiLcp.buildLcpArray(text, sa);
        KasaiLcp.CommonPhraseResult repeated = KasaiLcp.findLongestRepeatedPhrase(text, sa, lcp);
        SuffixAutomaton sam = new SuffixAutomaton(text);
        long t1 = System.nanoTime();

        // Top 8 sorted suffixes for preview
        StringBuilder saPreview = new StringBuilder("[");
        int previewCount = Math.min(8, sa.length);
        for (int i = 0; i < previewCount; i++) {
            int start = sa[i];
            String preview = text.substring(start, Math.min(text.length(), start + 25)).replace("\n", " ");
            saPreview.append(String.format(Locale.US,
                "{\"rank\":%d,\"index\":%d,\"preview\":\"%s\"}",
                i, start, escapeJson(preview)));
            if (i < previewCount - 1) saPreview.append(",");
        }
        saPreview.append("]");

        return String.format(Locale.US,
            "{\"phrase\":\"%s\",\"length\":%d,\"totalSubstrings\":%d,\"automatonStates\":%d,\"timeNs\":%d,\"topSuffixes\":%s}",
            escapeJson(repeated.phrase), repeated.length, sam.countDistinctSubstrings(), sam.getStateCount(), (t1 - t0), saPreview.toString()
        );
    }

    private static String handleM2Query(Map<String, String> params) {
        String text = params.getOrDefault("text", "Apply organic potassium fertilizer during early flowering. Soil inspection required.");
        String pattern = params.getOrDefault("pattern", "potassium");

        long t0 = System.nanoTime();
        SuffixAutomaton sam = new SuffixAutomaton(text);
        long tBuildSam = System.nanoTime() - t0;

        long t1 = System.nanoTime();
        boolean samExists = sam.contains(pattern);
        long tQuerySam = System.nanoTime() - t1;

        long t2 = System.nanoTime();
        int[] sa = SuffixArrayBuilder.buildSuffixArray(text);
        long tBuildSa = System.nanoTime() - t2;

        long t3 = System.nanoTime();
        int saMatchIndex = SuffixArrayBuilder.search(text, pattern, sa);
        long tQuerySa = System.nanoTime() - t3;

        return String.format(Locale.US,
            "{\"pattern\":\"%s\",\"exists\":%b,\"samQueryTimeNs\":%d,\"saMatchIndex\":%d,\"saQueryTimeNs\":%d}",
            escapeJson(pattern), samExists, tQuerySam, saMatchIndex, tQuerySa
        );
    }

    private static String handleM3Align(Map<String, String> params) {
        String seq1 = params.getOrDefault("seq1", "ATGCGTAGCAGTAGCT");
        String seq2 = params.getOrDefault("seq2", "ATGCTACGGTAGCT");
        String type = params.getOrDefault("type", "global").toLowerCase();

        long t0 = System.nanoTime();
        if ("local".equals(type)) {
            SmithWatermanAligner sw = new SmithWatermanAligner();
            var res = sw.align(seq1, seq2);
            long t1 = System.nanoTime();
            return String.format(Locale.US,
                "{\"type\":\"Local Alignment (Smith-Waterman)\",\"score\":%d,\"seq1\":\"%s\",\"seq2\":\"%s\",\"pos1\":%d,\"pos2\":%d,\"timeNs\":%d}",
                res.maxScore, res.localSeq1, res.localSeq2, res.startPos1, res.startPos2, (t1 - t0)
            );
        } else {
            NeedlemanWunschAligner nw = new NeedlemanWunschAligner();
            var res = nw.align(seq1, seq2);
            long t1 = System.nanoTime();
            return String.format(Locale.US,
                "{\"type\":\"Global Alignment (Needleman-Wunsch)\",\"score\":%d,\"identity\":%.1f,\"align1\":\"%s\",\"matchBar\":\"%s\",\"align2\":\"%s\",\"timeNs\":%d}",
                res.score, res.identityPercentage, res.alignedSeq1, escapeJson(res.matchBar), res.alignedSeq2, (t1 - t0)
            );
        }
    }

    private static String handleM3Autocorrect(Map<String, String> params) {
        String query = params.getOrDefault("query", "fertlzr");
        long t0 = System.nanoTime();
        var topSuggestions = LevenshteinDistance.findTopMatches(query, 4);
        long t1 = System.nanoTime();

        var best = topSuggestions.isEmpty() ? new LevenshteinDistance.Suggestion("none", 999) : topSuggestions.get(0);

        StringBuilder topJson = new StringBuilder("[");
        for (int i = 0; i < topSuggestions.size(); i++) {
            var s = topSuggestions.get(i);
            topJson.append(String.format(Locale.US, "{\"term\":\"%s\",\"distance\":%d}", escapeJson(s.term), s.distance));
            if (i < topSuggestions.size() - 1) topJson.append(",");
        }
        topJson.append("]");

        return String.format(Locale.US,
            "{\"input\":\"%s\",\"suggested\":\"%s\",\"distance\":%d,\"timeNs\":%d,\"alternatives\":%s}",
            escapeJson(query), escapeJson(best.term), best.distance, (t1 - t0), topJson.toString());
    }

    private static String handleM3Bitmask(Map<String, String> params) {
        int maxLand = Integer.parseInt(params.getOrDefault("land", "65"));
        int maxWater = Integer.parseInt(params.getOrDefault("water", "85"));

        List<BitmaskCropPlanner.Crop> crops = List.of(
            new BitmaskCropPlanner.Crop(0, "Organic Wheat", 30, 40, 12000),
            new BitmaskCropPlanner.Crop(1, "Soybean (Nitrogen Fixer)", 25, 25, 9500),
            new BitmaskCropPlanner.Crop(2, "Sweet Corn", 35, 55, 14000),
            new BitmaskCropPlanner.Crop(3, "Tomatoes (High Yield)", 15, 30, 11000),
            new BitmaskCropPlanner.Crop(4, "Alfalfa (Fodder)", 20, 20, 6000)
        );

        int[][] synergy = new int[5][5];
        synergy[0][1] = 2000;
        synergy[1][0] = 2000;

        long t0 = System.nanoTime();
        var plan = BitmaskCropPlanner.optimize(crops, maxLand, maxWater, synergy);
        long t1 = System.nanoTime();

        StringBuilder sb = new StringBuilder("{");
        sb.append("\"profit\":").append(plan.totalProfit).append(",");
        sb.append("\"landUsed\":").append(plan.totalLandUsed).append(",");
        sb.append("\"waterUsed\":").append(plan.totalWaterUsed).append(",");
        sb.append("\"maxLand\":").append(maxLand).append(",");
        sb.append("\"maxWater\":").append(maxWater).append(",");
        sb.append("\"timeNs\":").append(t1 - t0).append(",");

        sb.append("\"crops\":[");
        for (int i = 0; i < plan.selectedCrops.size(); i++) {
            var c = plan.selectedCrops.get(i);
            sb.append(String.format(Locale.US,
                "{\"name\":\"%s\",\"land\":%d,\"water\":%d,\"profit\":%d}",
                escapeJson(c.name), c.landAcres, c.waterRequiredUnits, c.expectedProfit));
            if (i < plan.selectedCrops.size() - 1) sb.append(",");
        }
        sb.append("],");

        sb.append("\"allCrops\":[");
        for (int i = 0; i < crops.size(); i++) {
            var c = crops.get(i);
            sb.append(String.format(Locale.US,
                "{\"name\":\"%s\",\"land\":%d,\"water\":%d,\"profit\":%d}",
                escapeJson(c.name), c.landAcres, c.waterRequiredUnits, c.expectedProfit));
            if (i < crops.size() - 1) sb.append(",");
        }
        sb.append("]");

        sb.append("}");
        return sb.toString();
    }

    private static String handleM3Mcm(Map<String, String> params) {
        String dimsStr = params.getOrDefault("dims", "10,100,5,50,1");
        String labelsStr = params.getOrDefault("labels", "DroneElevation,SoilSensors,SatelliteNDVI,YieldProjection");

        String[] parts = dimsStr.split(",");
        int[] dims = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            dims[i] = Integer.parseInt(parts[i].trim());
        }

        String[] labels = labelsStr.split(",");
        for (int i = 0; i < labels.length; i++) labels[i] = labels[i].trim();

        long t0 = System.nanoTime();
        var res = MatrixChainOptimizer.optimize(dims, labels);
        long t1 = System.nanoTime();

        return String.format(Locale.US,
            "{\"minMultiplications\":%d,\"pipelineOrder\":\"%s\",\"dims\":\"%s\",\"timeNs\":%d}",
            res.minScalarMultiplications, escapeJson(res.optimalParenthesization), escapeJson(dimsStr), (t1 - t0));
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
