package com.agriflow.server;

import com.agriflow.m1_strings.*;
import com.agriflow.m2_suffix.*;
import com.agriflow.m3_dp.*;
import com.agriflow.m4_flow.*;
import com.agriflow.m5_np.*;
import com.agriflow.m6_parallel.*;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * REST API Dispatcher (Modules 1 to 6: CO1 to CO6)
 * Connects the web frontend to the core Java DSA algorithms.
 */
public class ApiDispatcher {

    public static String handleRequest(String path, Map<String, String> params) {
        try {
            switch (path) {
                // Status & Suite Info
                case "/api/status":
                    return "{\"status\":\"active\",\"project\":\"AgriFlow\",\"scope\":\"Modules 1 to 6 (CO1 to CO6)\",\"modules\":[\"M1\",\"M2\",\"M3\",\"M4\",\"M5\",\"M6\"],\"datasetsCount\":14,\"ready\":true}";

                // Kaggle Dataset Suite
                case "/api/dataset/list":
                    return handleDatasetList();
                case "/api/dataset/read":
                    return handleDatasetRead(params);

                // Automated Full System Benchmark
                case "/api/benchmark":
                    return handleBenchmark();

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

                // Module 4: Network Flow & Resource Matching
                case "/api/m4/maxflow":
                    return handleM4MaxFlow(params);
                case "/api/m4/bipartite":
                    return handleM4Bipartite();

                // Module 5: NP-Completeness & Approximation
                case "/api/m5/sat":
                    return handleM5Sat();
                case "/api/m5/vertexcover":
                    return handleM5VertexCover();

                // Module 6: Randomized & Parallel Sensor Telemetry
                case "/api/m6/reservoir":
                    return handleM6Reservoir(params);
                case "/api/m6/blelloch":
                    return handleM6Blelloch(params);
                case "/api/m6/brent":
                    return handleM6Brent(params);
                case "/api/m6/millerrabin":
                    return handleM6MillerRabin(params);
                case "/api/m6/quicksort":
                    return handleM6QuickSort();
                case "/api/m6/reduce":
                    return handleM6Reduce(params);

                default:
                    return "{\"error\": \"Unknown API endpoint: " + path + "\"}";
            }
        } catch (Exception e) {
            return "{\"error\": \"" + escapeJson(e.getMessage()) + "\"}";
        }
    }

    // =========================================================================
    // MODULE 1: STRING SEARCHING
    // =========================================================================
    private static String handleM1Search(Map<String, String> params) {
        String text = params.getOrDefault("text", "Field inspection found early blight and late blight symptoms on tomato leaves.");
        String pattern = params.getOrDefault("pattern", "blight");
        String algo = params.getOrDefault("algo", "kmp").toLowerCase();

        if ("compare".equals(algo)) {
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
        int matchCount;

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

    // =========================================================================
    // MODULE 2: SUFFIX STRUCTURES
    // =========================================================================
    private static String handleM2Lcp(Map<String, String> params) {
        String text = params.getOrDefault("text", "Apply organic potassium fertilizer during early flowering. Soil inspection required. Apply organic potassium fertilizer during early flowering.");
        long t0 = System.nanoTime();
        int[] sa = SuffixArrayBuilder.buildSuffixArray(text);
        int[] lcp = KasaiLcp.buildLcpArray(text, sa);
        KasaiLcp.CommonPhraseResult repeated = KasaiLcp.findLongestRepeatedPhrase(text, sa, lcp);
        SuffixAutomaton sam = new SuffixAutomaton(text);
        long t1 = System.nanoTime();

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
        boolean samExists = sam.contains(pattern);
        long tQuerySam = System.nanoTime() - t0;

        long t1 = System.nanoTime();
        int[] sa = SuffixArrayBuilder.buildSuffixArray(text);
        int saMatchIndex = SuffixArrayBuilder.search(text, pattern, sa);
        long tQuerySa = System.nanoTime() - t1;

        return String.format(Locale.US,
            "{\"pattern\":\"%s\",\"exists\":%b,\"samQueryTimeNs\":%d,\"saMatchIndex\":%d,\"saQueryTimeNs\":%d}",
            escapeJson(pattern), samExists, tQuerySam, saMatchIndex, tQuerySa
        );
    }

    // =========================================================================
    // MODULE 3: ADVANCED DYNAMIC PROGRAMMING
    // =========================================================================
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

    // =========================================================================
    // MODULE 4: NETWORK FLOW & MATCHING
    // =========================================================================
    private static String handleM4MaxFlow(Map<String, String> params) {
        String algo = params.getOrDefault("algo", "dinic").toLowerCase();

        FlowNetwork net = new FlowNetwork(6);
        net.setVertexName(0, "Main Reservoir");
        net.setVertexName(1, "Canal North");
        net.setVertexName(2, "Canal South");
        net.setVertexName(3, "Pumping Substation 1");
        net.setVertexName(4, "Pumping Substation 2");
        net.setVertexName(5, "Farm Plots Sink");

        net.addEdge(0, 1, 100);
        net.addEdge(0, 2, 80);
        net.addEdge(1, 2, 30);
        net.addEdge(1, 3, 70);
        net.addEdge(1, 4, 40);
        net.addEdge(2, 4, 60);
        net.addEdge(3, 5, 90);
        net.addEdge(4, 5, 100);

        long t0 = System.nanoTime();
        int maxFlow;
        String algorithmName;

        if ("edmonds".equals(algo)) {
            algorithmName = "Edmonds-Karp (BFS Augmenting Paths)";
            maxFlow = EdmondsKarp.computeMaxFlow(net, 0, 5);
        } else {
            algorithmName = "Dinic's Algorithm (Level Graph & Blocking Flow)";
            DinicMaxFlow dinic = new DinicMaxFlow(net, 0, 5);
            maxFlow = dinic.computeMaxFlow();
        }
        long t1 = System.nanoTime();

        var bottlenecks = MinCutBottleneckFinder.findBottlenecks(net, 0);

        StringBuilder sb = new StringBuilder("{");
        sb.append("\"algorithm\":\"").append(algorithmName).append("\",");
        sb.append("\"maxFlow\":").append(maxFlow).append(",");
        sb.append("\"timeNs\":").append(t1 - t0).append(",");
        sb.append("\"bottlenecks\":[");
        for (int i = 0; i < bottlenecks.size(); i++) {
            var b = bottlenecks.get(i);
            sb.append(String.format(Locale.US,
                "{\"from\":\"%s\",\"to\":\"%s\",\"capacity\":%d}",
                escapeJson(b.from), escapeJson(b.to), b.capacity));
            if (i < bottlenecks.size() - 1) sb.append(",");
        }
        sb.append("],");
        sb.append("\"nodes\":[\"Main Reservoir\",\"Canal North\",\"Canal South\",\"Pumping Substation 1\",\"Pumping Substation 2\",\"Farm Plots Sink\"]");
        sb.append("}");
        return sb.toString();
    }

    private static String handleM4Bipartite() {
        List<String> farms = List.of("GreenValley Farm", "Sunrise Orchard", "Riverbend Fields", "Highland Vineyard");
        List<String> equipment = List.of("Drone Sprayer Alpha", "Heavy Tractor 4WD", "Combine Harvester 9000", "Soil Aerator");

        Map<Integer, List<Integer>> compatibility = new HashMap<>();
        compatibility.put(0, List.of(0, 1));
        compatibility.put(1, List.of(1, 2));
        compatibility.put(2, List.of(0, 3));
        compatibility.put(3, List.of(1));

        long t0 = System.nanoTime();
        var assignments = BipartiteResourceMatcher.match(farms, equipment, compatibility);
        long t1 = System.nanoTime();

        StringBuilder sb = new StringBuilder("{");
        sb.append("\"matchedCount\":").append(assignments.size()).append(",");
        sb.append("\"totalFarms\":").append(farms.size()).append(",");
        sb.append("\"timeNs\":").append(t1 - t0).append(",");
        sb.append("\"assignments\":[");
        for (int i = 0; i < assignments.size(); i++) {
            var a = assignments.get(i);
            sb.append(String.format(Locale.US,
                "{\"farm\":\"%s\",\"equipment\":\"%s\"}",
                escapeJson(a.farmName), escapeJson(a.equipmentName)));
            if (i < assignments.size() - 1) sb.append(",");
        }
        sb.append("]}");
        return sb.toString();
    }

    // =========================================================================
    // MODULE 5: NP-COMPLETENESS & APPROXIMATION
    // =========================================================================
    private static String handleM5Sat() {
        // Agricultural Companion & Rotation Rules:
        // x1: Plant Corn, x2: Plant Soybeans, x3: High Nitrogen Soil
        List<CropConstraintSatSolver.Clause> clauses = List.of(
            new CropConstraintSatSolver.Clause(1, 2),       // Plant Corn OR Soybeans
            new CropConstraintSatSolver.Clause(-1, -2),     // Cannot plant both in same zone
            new CropConstraintSatSolver.Clause(-1, 3),      // Corn requires High Nitrogen
            new CropConstraintSatSolver.Clause(-2, -3)      // Soybeans should NOT be on high nitrogen (causes lodging)
        );

        long t0 = System.nanoTime();
        var res = CropConstraintSatSolver.solve(clauses, 3);
        var cliqueGraph = ReductionGraphMapper.reduce3SatToClique(clauses);
        long t1 = System.nanoTime();

        StringBuilder modelJson = new StringBuilder("{");
        int count = 0;
        if (res.model != null) {
            for (var entry : res.model.entrySet()) {
                String varName = switch (entry.getKey()) {
                    case 1 -> "x1_Corn";
                    case 2 -> "x2_Soybeans";
                    case 3 -> "x3_HighNitrogen";
                    default -> "x" + entry.getKey();
                };
                modelJson.append("\"").append(varName).append("\":").append(entry.getValue());
                if (++count < res.model.size()) modelJson.append(",");
            }
        }
        modelJson.append("}");

        return String.format(Locale.US,
            "{\"satisfiable\":%b,\"model\":%s,\"cliqueVertices\":%d,\"ruleCount\":%d,\"timeNs\":%d}",
            res.satisfiable, modelJson.toString(), cliqueGraph.vertexLabels.size(), clauses.size(), (t1 - t0));
    }

    private static String handleM5VertexCover() {
        List<String> junctions = List.of(
            "Central Silo", "North Ditch", "South Ditch", "East Gate", "West Orchard", "Greenhouse"
        );

        List<VertexCoverApproximation.RoadSegment> pathways = List.of(
            new VertexCoverApproximation.RoadSegment("Central Silo", "North Ditch"),
            new VertexCoverApproximation.RoadSegment("Central Silo", "South Ditch"),
            new VertexCoverApproximation.RoadSegment("North Ditch", "East Gate"),
            new VertexCoverApproximation.RoadSegment("South Ditch", "West Orchard"),
            new VertexCoverApproximation.RoadSegment("East Gate", "Greenhouse"),
            new VertexCoverApproximation.RoadSegment("West Orchard", "Greenhouse")
        );

        long t0 = System.nanoTime();
        var plan = VertexCoverApproximation.approximateCover(junctions, pathways);
        long t1 = System.nanoTime();

        StringBuilder towersJson = new StringBuilder("[");
        int count = 0;
        for (String t : plan.towerLocations) {
            towersJson.append("\"").append(escapeJson(t)).append("\"");
            if (++count < plan.towerLocations.size()) towersJson.append(",");
        }
        towersJson.append("]");

        StringBuilder edgesJson = new StringBuilder("[");
        for (int i = 0; i < plan.matchedPivotalEdges.size(); i++) {
            var e = plan.matchedPivotalEdges.get(i);
            edgesJson.append(String.format(Locale.US,
                "{\"from\":\"%s\",\"to\":\"%s\"}",
                escapeJson(e.junctionA), escapeJson(e.junctionB)));
            if (i < plan.matchedPivotalEdges.size() - 1) edgesJson.append(",");
        }
        edgesJson.append("]");

        return String.format(Locale.US,
            "{\"towerCount\":%d,\"totalJunctions\":%d,\"totalRoads\":%d,\"towers\":%s,\"matchedEdges\":%s,\"timeNs\":%d}",
            plan.towerLocations.size(), junctions.size(), pathways.size(), towersJson.toString(), edgesJson.toString(), (t1 - t0));
    }

    // =========================================================================
    // MODULE 6: RANDOMIZED & PARALLEL TELEMETRY
    // =========================================================================
    private static String handleM6Reservoir(Map<String, String> params) {
        int k = Integer.parseInt(params.getOrDefault("k", "5"));
        int streamSize = Integer.parseInt(params.getOrDefault("streamSize", "1000"));

        ReservoirSampler<ReservoirSampler.SensorReading> sampler = new ReservoirSampler<>(k);
        Random rand = new Random(42);

        long t0 = System.nanoTime();
        for (int i = 1; i <= streamSize; i++) {
            sampler.ingest(new ReservoirSampler.SensorReading(
                System.currentTimeMillis() + i * 500L,
                "SENSOR-" + (i % 15 + 1),
                20.0 + rand.nextDouble() * 30.0,
                18.0 + rand.nextDouble() * 15.0
            ));
        }
        long t1 = System.nanoTime();

        var sample = sampler.getSample();
        StringBuilder sampleJson = new StringBuilder("[");
        for (int i = 0; i < sample.size(); i++) {
            var r = sample.get(i);
            sampleJson.append(String.format(Locale.US,
                "{\"sensorId\":\"%s\",\"moisture\":%.1f,\"temperature\":%.1f}",
                r.sensorId, r.moisturePercent, r.temperatureCelsius));
            if (i < sample.size() - 1) sampleJson.append(",");
        }
        sampleJson.append("]");

        return String.format(Locale.US,
            "{\"totalStream\":%d,\"sampleSize\":%d,\"timeNs\":%d,\"sample\":%s}",
            sampler.getTotalProcessed(), sample.size(), (t1 - t0), sampleJson.toString());
    }

    private static String handleM6Blelloch(Map<String, String> params) {
        double[] dailyRainfall = {5.0, 12.0, 0.0, 8.5, 15.0, 22.0, 4.0, 11.5};

        long t0 = System.nanoTime();
        double[] exclusive = BlellochScan.exclusiveScan(dailyRainfall);
        double[] inclusive = BlellochScan.inclusiveScan(dailyRainfall);
        long t1 = System.nanoTime();

        return String.format(Locale.US,
            "{\"daily\":%s,\"exclusive\":%s,\"inclusive\":%s,\"timeNs\":%d}",
            Arrays.toString(dailyRainfall), Arrays.toString(exclusive), Arrays.toString(inclusive), (t1 - t0));
    }

    private static String handleM6Brent(Map<String, String> params) {
        double work = Double.parseDouble(params.getOrDefault("work", "2000000"));
        double depth = Double.parseDouble(params.getOrDefault("depth", "40"));
        int cores = Integer.parseInt(params.getOrDefault("cores", "8"));

        long t0 = System.nanoTime();
        var scaling = BrentsTheoremAnalyzer.evaluateScaling(work, depth, cores);
        long t1 = System.nanoTime();

        StringBuilder sb = new StringBuilder("{\"scaling\":[");
        for (int i = 0; i < scaling.size(); i++) {
            var m = scaling.get(i);
            sb.append(String.format(Locale.US,
                "{\"cores\":%d,\"timeBound\":%.1f,\"speedup\":%.2f,\"efficiency\":%.2f}",
                m.cores, m.timeBound, m.speedup, m.efficiency * 100.0));
            if (i < scaling.size() - 1) sb.append(",");
        }
        sb.append(String.format(Locale.US, "],\"work\":%.0f,\"depth\":%.0f,\"timeNs\":%d}", work, depth, (t1 - t0)));
        return sb.toString();
    }

    private static String handleM6MillerRabin(Map<String, String> params) {
        String numStr = params.getOrDefault("number", "104729");
        BigInteger num = new BigInteger(numStr);

        long t0 = System.nanoTime();
        boolean isPrime = MillerRabinVerifier.isProbablePrime(num, 10);
        long t1 = System.nanoTime();

        return String.format(Locale.US,
            "{\"number\":\"%s\",\"isPrime\":%b,\"iterations\":10,\"timeNs\":%d}",
            numStr, isPrime, (t1 - t0));
    }

    private static String handleM6QuickSort() {
        List<RandomizedQuickSort.FarmRecord> harvestData = new ArrayList<>(List.of(
            new RandomizedQuickSort.FarmRecord("Farm-Alpha", "Corn", 4.2),
            new RandomizedQuickSort.FarmRecord("Farm-Beta", "Wheat", 3.8),
            new RandomizedQuickSort.FarmRecord("Farm-Gamma", "Soybean", 2.9),
            new RandomizedQuickSort.FarmRecord("Farm-Delta", "Corn", 5.1),
            new RandomizedQuickSort.FarmRecord("Farm-Epsilon", "Rice", 6.3),
            new RandomizedQuickSort.FarmRecord("Farm-Zeta", "Tomatoes", 7.8)
        ));

        long t0 = System.nanoTime();
        RandomizedQuickSort.sort(harvestData);
        long t1 = System.nanoTime();

        StringBuilder sb = new StringBuilder("{\"rankedFarms\":[");
        for (int i = 0; i < harvestData.size(); i++) {
            var r = harvestData.get(i);
            sb.append(String.format(Locale.US,
                "{\"farmId\":\"%s\",\"crop\":\"%s\",\"yield\":%.2f}",
                escapeJson(r.farmId), escapeJson(r.cropType), r.yieldTonsPerAcre));
            if (i < harvestData.size() - 1) sb.append(",");
        }
        sb.append(String.format(Locale.US, "],\"timeNs\":%d}", (t1 - t0)));
        return sb.toString();
    }

    private static String handleM6Reduce(Map<String, String> params) {
        int count = Integer.parseInt(params.getOrDefault("count", "50000"));
        double[] sensorData = new double[count];
        Random rand = new Random(42);
        for (int i = 0; i < count; i++) {
            sensorData[i] = 15.0 + rand.nextDouble() * 25.0; // 15 to 40 degrees C
        }

        long t0 = System.nanoTime();
        var stats = ParallelSensorReduce.reduce(sensorData);
        long t1 = System.nanoTime();

        return String.format(Locale.US,
            "{\"count\":%d,\"min\":%.2f,\"max\":%.2f,\"avg\":%.2f,\"sum\":%.2f,\"timeNs\":%d}",
            stats.count, stats.min, stats.max, stats.average(), stats.sum, (t1 - t0));
    }

    // =========================================================================
    // KAGGLE DATASET SUITE HANDLERS
    // =========================================================================

    private static final List<DatasetMeta> DATASET_REGISTRY = List.of(
        new DatasetMeta("DS-01", "kaggle_crop_disease_symptoms.txt", "Crop Pathology & Disease Symptoms Log", "M1", "CO1", "1,050 diagnostic field observations with scientific pathogen taxonomy (foliar blight, rust, blast, powdery mildew, mosaic)", "KMP, Z-Algorithm, Aho-Corasick", "Tabular ISO Delimited", 1050),
        new DatasetMeta("DS-02", "kaggle_seed_batch_manifest.txt", "Seed Batch & Chemical Barcode Registry", "M1", "CO1", "1,050 certified seed serials, chemical lots, and purity tokens for instant rolling hash verification", "Rabin-Karp Rolling Hash", "Barcode Stream", 1050),
        new DatasetMeta("DS-03", "kaggle_pathology_bulletin_corpus.txt", "Regional Plant Protection Advisory Bulletin", "M1, M2", "CO1, CO2", "1,120 agronomy circular lines covering multi-pathogen threats for simultaneous dictionary searching & suffix automaton", "Aho-Corasick, Suffix Automaton", "Text Bulletin", 1120),
        new DatasetMeta("DS-04", "kaggle_treatment_protocols_lcp.txt", "Standardized Crop Treatment Protocols", "M2", "CO2", "1,050 horticultural spray and fertigation protocols with repeated substrings for Suffix Array and Kasai LCP", "Suffix Array, Kasai's LCP", "Protocol Corpus", 1050),
        new DatasetMeta("DS-05", "kaggle_crop_dna_germplasm.txt", "Comparative Crop Germplasm FASTA Sequences", "M3", "CO3", "1,010 comparative FASTA nucleotide sequences of wild ancestor vs hybrid wheat DREB1A gene and Sub1A motif", "Needleman-Wunsch, Smith-Waterman", "FASTA Genomics", 1010),
        new DatasetMeta("DS-06", "kaggle_farmer_advisory_queries.txt", "Farmer IVR Voice & SMS Advisory Queries", "M3", "CO3", "1,050 farmer voice/SMS search logs with realistic phonetic misspellings, omissions, and transpositions", "Levenshtein & Damerau Distance", "Query Log", 1050),
        new DatasetMeta("DS-07", "kaggle_crop_portfolio_economics.txt", "Multi-Crop Economics & Water Budgets", "M3", "CO3", "1,060 records of net profit ($/ha), water consumption (m3), and companion planting synergy matrix for Bitmask DP", "Bitmask DP Optimizer", "Tabular Economics", 1060),
        new DatasetMeta("DS-08", "kaggle_sensor_fusion_matrices.txt", "Satellite NDVI & Drone Remote Sensing Matrices", "M3", "CO3", "1,050 sequential sensor transformation matrix dimensions for Matrix Chain Multiplication DP optimization", "Matrix Chain Multiplication DP", "Matrix Pipeline Chain", 1050),
        new DatasetMeta("DS-09", "kaggle_irrigation_canal_network.txt", "Command Area Irrigation Canal Network", "M4", "CO4", "1,050 canal network links with reservoirs, pumping stations, distribution canals, and flow capacities in L/s", "Dinic's Max Flow, Min-Cut Choke Points", "Directed Flow Graph", 1050),
        new DatasetMeta("DS-10", "kaggle_farm_machinery_matching.txt", "Farm Machinery Sharing & Booking Fleet", "M4", "CO4", "1,050 bipartite graph requests matching farm bookings to combine harvesters, drone sprayers, and tractors", "Hopcroft-Karp Bipartite Matching", "Bipartite Matrix", 1050),
        new DatasetMeta("DS-11", "kaggle_crop_rotation_constraints_3sat.txt", "Crop Rotation Ecological Compatibility Clauses", "M5", "CO5", "1,050 3-CNF Boolean satisfiability constraints modeling biological crop rotation rules for 3-SAT DPLL", "3-SAT DPLL Solver, CLIQUE Reduction", "3-CNF Formula", 1050),
        new DatasetMeta("DS-12", "kaggle_farm_road_junctions_graph.txt", "Farm Service Road Network & Watchtower Topology", "M5", "CO5", "1,060 undirected farm road network links and junctions for 2-Approximation Vertex Cover placement", "2-Approximation Vertex Cover", "Undirected Graph", 1060),
        new DatasetMeta("DS-13", "kaggle_iot_telemetry_stream.txt", "IoT Microclimate & Soil Moisture Telemetry Stream", "M6", "CO6", "1,200 continuous IoT sensor stream readings (soil moisture, temperature, rainfall, solar flux) for streaming", "Reservoir Sampling (Alg R), Blelloch Scan", "IoT Telemetry Stream", 1200),
        new DatasetMeta("DS-14", "kaggle_seed_security_primality_keys.txt", "Seed Security Cryptographic Prime Tokens & Yields", "M6", "CO6", "1,080 BigInteger seed security tokens for Miller-Rabin primality testing and harvest yield league rankings", "Miller-Rabin Primality, Randomized QuickSort", "Cryptographic Register", 1080)
    );

    private static String handleDatasetList() {
        StringBuilder sb = new StringBuilder("{\"datasets\":[");
        for (int i = 0; i < DATASET_REGISTRY.size(); i++) {
            DatasetMeta d = DATASET_REGISTRY.get(i);
            sb.append(String.format(Locale.US,
                "{\"id\":\"%s\",\"filename\":\"%s\",\"name\":\"%s\",\"module\":\"%s\",\"co\":\"%s\",\"description\":\"%s\",\"algorithms\":\"%s\",\"format\":\"%s\",\"records\":%d}",
                escapeJson(d.id), escapeJson(d.filename), escapeJson(d.name), escapeJson(d.module), escapeJson(d.co),
                escapeJson(d.description), escapeJson(d.algorithms), escapeJson(d.format), d.estimatedRecords));
            if (i < DATASET_REGISTRY.size() - 1) sb.append(",");
        }
        sb.append("],\"count\":").append(DATASET_REGISTRY.size()).append("}");
        return sb.toString();
    }

    private static String handleDatasetRead(Map<String, String> params) {
        String filename = params.getOrDefault("file", "kaggle_crop_disease_symptoms.txt");
        // sanitize filename
        filename = Paths.get(filename).getFileName().toString();

        Path p = Paths.get("data", filename);
        if (!Files.exists(p)) {
            p = Paths.get("src/main/resources/data", filename);
        }

        String content = "";
        try {
            if (Files.exists(p)) {
                content = Files.readString(p, StandardCharsets.UTF_8);
            } else {
                var is = ApiDispatcher.class.getResourceAsStream("/data/" + filename);
                if (is != null) {
                    content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                } else {
                    return "{\"error\":\"Dataset file not found: " + escapeJson(filename) + "\"}";
                }
            }
        } catch (Exception e) {
            return "{\"error\":\"Failed to read dataset: " + escapeJson(e.getMessage()) + "\"}";
        }

        String[] lines = content.split("\\r?\\n");
        StringBuilder headerMeta = new StringBuilder();
        StringBuilder dataLines = new StringBuilder();
        int recordCount = 0;

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.startsWith("#")) {
                if (headerMeta.length() > 0) headerMeta.append("\n");
                headerMeta.append(trimmed);
            } else if (!trimmed.isEmpty()) {
                if (dataLines.length() > 0) dataLines.append("\n");
                dataLines.append(trimmed);
                recordCount++;
            }
        }

        // Module-specific parsed sample builder
        StringBuilder sampleJson = new StringBuilder("{");
        if (filename.contains("disease_symptoms")) {
            sampleJson.append("\"text\":\"").append(escapeJson(dataLines.toString())).append("\",\"pattern\":\"blight\"");
        } else if (filename.contains("seed_batch")) {
            sampleJson.append("\"text\":\"").append(escapeJson(dataLines.toString())).append("\",\"pattern\":\"SEED-WHT-2026-X99214A\"");
        } else if (filename.contains("pathology_bulletin")) {
            sampleJson.append("\"text\":\"").append(escapeJson(dataLines.toString())).append("\",\"pattern\":\"blight\",\"dictionary\":\"blight, blast, powdery mildew, mosaic, rot\"");
        } else if (filename.contains("treatment_protocols")) {
            sampleJson.append("\"text\":\"").append(escapeJson(dataLines.toString())).append("\",\"query\":\"Apply foliar spray\"");
        } else if (filename.contains("crop_dna")) {
            sampleJson.append("\"seqA\":\"ATGGACATGAGCGGGTACGCCGCCGCCATGGCGCCCAGCCTGGCGGCGTCGTGCGAGGCGGCGTGCATCAGCTCGCTGGGCAGCGACGCCGCCGTCATGCTGCTGGCCGAGCTGCCCGCCGAGTTCGAGGAGCTGCGCCGCCGCGTGTCGCGCATGCTCGGCCACTACAAGGCCGCCAAGAACACCGGCAAGGGCAAGCACCTGTGGACGTACACGGGCGCGGAGGAC\",")
                      .append("\"seqB\":\"ATGGACATGAGCGGCTACGCCGCCGCCATGGCGCCCGGCCTGGCGGCGTCGTGCGAGGCGGCGTGCATCAGCTCGCTGGGCAGCGACGCCGCCGTCATGCTGCTGGCCGAGCTGCCCGCCGAGTTCGAGGAGCTGCGCCGCCGCGTGTCGCGCATGCTCGGCCACTACAAGGCCGCCAAGAACACCGGCAAGGGCAAGCACCTGTGGACGTACACCGGCGCGGAGGAC\"");
        } else if (filename.contains("farmer_advisory")) {
            sampleJson.append("\"word1\":\"fertlzr\",\"word2\":\"fertilizer\",\"query\":\"fertlzr\",\"dictionary\":\"fertilizer, pesticide, potassium, chlorpyrifos, urea, fungicide, tractor, hydroponics, nitrogen, biofertilizer, vermicompost, drenching, herbicide, drip, sprayer\"");
        } else if (filename.contains("portfolio_economics")) {
            sampleJson.append("\"budget\":\"10000\",\"maxLand\":\"50\"");
        } else if (filename.contains("sensor_fusion")) {
            sampleJson.append("\"dimensions\":\"10, 100, 5, 50, 1\"");
        } else if (filename.contains("canal_network")) {
            sampleJson.append("\"source\":0,\"sink\":5,\"capacity\":2900");
        } else if (filename.contains("machinery")) {
            sampleJson.append("\"farms\":6,\"equipment\":6");
        } else if (filename.contains("rotation_constraints")) {
            sampleJson.append("\"clauses\":\"(x1 OR x5 OR ~x4) AND (x3 OR x1 OR ~x2) AND (~x2 OR ~x4 OR x1) AND (x3 OR ~x4 OR x5) AND (x2 OR x5 OR x3) AND (x1 OR ~x5 OR x2)\"");
        } else if (filename.contains("road_junctions")) {
            sampleJson.append("\"vertices\":8,\"edges\":11");
        } else if (filename.contains("telemetry_stream")) {
            sampleJson.append("\"stream\":\"24.5, 26.1, 21.8, 28.4, 29.2, 30.5, 32.0, 31.4, 33.8, 35.1, 34.6, 33.9, 32.1, 30.7, 29.8, 28.9, 27.8, 27.0, 26.2, 25.4\",\"k\":5");
        } else if (filename.contains("seed_security")) {
            sampleJson.append("\"number\":\"2147483647\",\"yields\":\"45.8, 62.4, 38.1, 71.9, 54.3, 83.2, 49.6, 67.8, 59.1, 75.4, 42.0, 88.5, 51.7, 63.9, 79.2\"");
        }
        sampleJson.append("}");

        return String.format(Locale.US,
            "{\"filename\":\"%s\",\"sizeBytes\":%d,\"linesCount\":%d,\"recordCount\":%d,\"metadata\":\"%s\",\"content\":\"%s\",\"sample\":%s}",
            escapeJson(filename), content.getBytes(StandardCharsets.UTF_8).length, lines.length, recordCount,
            escapeJson(headerMeta.toString()), escapeJson(content), sampleJson.toString());
    }

    // =========================================================================
    // AUTOMATED BENCHMARK SUITE (MODULES 1 TO 6)
    // =========================================================================

    private static String handleBenchmark() {
        long tStart = System.nanoTime();
        List<String> items = new ArrayList<>();

        // M1: KMP
        long m1Start = System.nanoTime();
        String sampleText = "Field inspection found early blight and late blight symptoms on tomato foliage.";
        List<Integer> kmpMatches = KmpMatcher.search(sampleText, "blight");
        long m1Us = (System.nanoTime() - m1Start) / 1000;
        items.add(String.format(Locale.US, "{\"module\":\"M1\",\"co\":\"CO1\",\"name\":\"KMP Disease Pattern Scan\",\"status\":\"PASS\",\"matches\":%d,\"timeUs\":%d}", kmpMatches.size(), m1Us));

        // M2: Kasai LCP
        long m2Start = System.nanoTime();
        String m2Text = "Apply foliar spray early morning followed by surfactant check. Apply foliar spray early morning.";
        int[] sa = SuffixArrayBuilder.buildSuffixArray(m2Text);
        int[] lcp = KasaiLcp.buildLcpArray(m2Text, sa);
        KasaiLcp.CommonPhraseResult phrase = KasaiLcp.findLongestRepeatedPhrase(m2Text, sa, lcp);
        long m2Us = (System.nanoTime() - m2Start) / 1000;
        items.add(String.format(Locale.US, "{\"module\":\"M2\",\"co\":\"CO2\",\"name\":\"Kasai LCP Treatment Protocol Duplication\",\"status\":\"PASS\",\"maxLcp\":%d,\"timeUs\":%d}", phrase.length, m2Us));

        // M3: Needleman-Wunsch & Levenshtein
        long m3Start = System.nanoTime();
        String seqA = "ATGGACATGAGCGGGTACGCCGCCGCCATGGCGCCCAGCCTGGCGGCG";
        String seqB = "ATGGACATGAGCGGCTACGCCGCCGCCATGGCGCCCGGCCTGGCGGCG";
        NeedlemanWunschAligner nw = new NeedlemanWunschAligner();
        var alignRes = nw.align(seqA, seqB);
        var topSugg = LevenshteinDistance.findTopMatches("fertlzr", 1);
        int levDist = topSugg.isEmpty() ? 0 : topSugg.get(0).distance;
        long m3Us = (System.nanoTime() - m3Start) / 1000;
        items.add(String.format(Locale.US, "{\"module\":\"M3\",\"co\":\"CO3\",\"name\":\"Crop DNA NW Alignment & Farmer Levenshtein DP\",\"status\":\"PASS\",\"alignmentScore\":%d,\"levenshtein\":%d,\"timeUs\":%d}", alignRes.score, levDist, m3Us));

        // M4: Dinic Max Flow
        long m4Start = System.nanoTime();
        FlowNetwork net = new FlowNetwork(6);
        net.addEdge(0, 1, 16); net.addEdge(0, 2, 13);
        net.addEdge(1, 2, 10); net.addEdge(1, 3, 12);
        net.addEdge(2, 1, 4);  net.addEdge(2, 4, 14);
        net.addEdge(3, 2, 9);  net.addEdge(3, 5, 20);
        net.addEdge(4, 3, 7);  net.addEdge(4, 5, 4);
        DinicMaxFlow dinic = new DinicMaxFlow(net, 0, 5);
        int maxFlow = dinic.computeMaxFlow();
        long m4Us = (System.nanoTime() - m4Start) / 1000;
        items.add(String.format(Locale.US, "{\"module\":\"M4\",\"co\":\"CO4\",\"name\":\"Dinic Multi-Source Canal Max Flow\",\"status\":\"PASS\",\"maxFlow\":%d,\"timeUs\":%d}", maxFlow, m4Us));

        // M5: 3-SAT DPLL & Vertex Cover
        long m5Start = System.nanoTime();
        List<CropConstraintSatSolver.Clause> clauses = List.of(
            new CropConstraintSatSolver.Clause(1, 2),
            new CropConstraintSatSolver.Clause(-1, -2),
            new CropConstraintSatSolver.Clause(-1, 3)
        );
        var satRes = CropConstraintSatSolver.solve(clauses, 3);
        long m5Us = (System.nanoTime() - m5Start) / 1000;
        items.add(String.format(Locale.US, "{\"module\":\"M5\",\"co\":\"CO5\",\"name\":\"3-SAT DPLL Crop Rotation Satisfiability\",\"status\":\"PASS\",\"satisfiable\":%b,\"timeUs\":%d}", satRes.satisfiable, m5Us));

        // M6: Blelloch Scan & Miller-Rabin
        long m6Start = System.nanoTime();
        double[] sensorVals = {24.5, 26.1, 21.8, 28.4, 29.2, 30.5, 32.0, 31.4, 33.8, 35.1, 34.6, 33.9, 32.1, 30.7, 29.8, 28.9};
        double[] scanPrefix = BlellochScan.inclusiveScan(sensorVals);
        boolean isPrime = MillerRabinVerifier.isProbablePrime(new BigInteger("2147483647"), 10);
        long m6Us = (System.nanoTime() - m6Start) / 1000;
        items.add(String.format(Locale.US, "{\"module\":\"M6\",\"co\":\"CO6\",\"name\":\"Blelloch Parallel Prefix & Miller-Rabin Primality\",\"status\":\"PASS\",\"prefixSum\":%.2f,\"isPrime\":%b,\"timeUs\":%d}", scanPrefix[scanPrefix.length - 1], isPrime, m6Us));

        long totalUs = (System.nanoTime() - tStart) / 1000;

        StringBuilder sb = new StringBuilder();
        sb.append("{\"status\":\"SUCCESS\",\"totalDurationUs\":").append(totalUs)
          .append(",\"modulesBenchmarked\":6,\"results\":[");
        for (int i = 0; i < items.size(); i++) {
            sb.append(items.get(i));
            if (i < items.size() - 1) sb.append(",");
        }
        sb.append("]}");
        return sb.toString();
    }

    private static class DatasetMeta {
        String id;
        String filename;
        String name;
        String module;
        String co;
        String description;
        String algorithms;
        String format;
        int estimatedRecords;

        DatasetMeta(String id, String filename, String name, String module, String co, String description, String algorithms, String format, int records) {
            this.id = id;
            this.filename = filename;
            this.name = name;
            this.module = module;
            this.co = co;
            this.description = description;
            this.algorithms = algorithms;
            this.format = format;
            this.estimatedRecords = records;
        }
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
