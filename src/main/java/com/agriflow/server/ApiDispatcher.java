package com.agriflow.server;

import com.agriflow.m1_strings.*;
import com.agriflow.m2_suffix.*;
import com.agriflow.m3_dp.*;
import com.agriflow.m4_flow.*;
import com.agriflow.m5_np.*;
import com.agriflow.m6_parallel.*;

import java.math.BigInteger;
import java.util.*;

/**
 * REST API Dispatcher (Modules 1 to 6: CO1 to CO6)
 * Connects the web frontend to the core Java DSA algorithms.
 */
public class ApiDispatcher {

    public static String handleRequest(String path, Map<String, String> params) {
        try {
            switch (path) {
                // Status
                case "/api/status":
                    return "{\"status\":\"active\",\"project\":\"AgriFlow\",\"scope\":\"Modules 1 to 6 (CO1 to CO6)\",\"modules\":[\"M1\",\"M2\",\"M3\",\"M4\",\"M5\",\"M6\"],\"ready\":true}";

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
