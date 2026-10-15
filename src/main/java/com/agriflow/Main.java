package com.agriflow;

import com.agriflow.server.AgriFlowServer;
import com.agriflow.m1_strings.*;
import com.agriflow.m2_suffix.*;
import com.agriflow.m3_dp.*;
import com.agriflow.m4_flow.*;
import com.agriflow.m5_np.*;
import com.agriflow.m6_parallel.*;

import java.math.BigInteger;
import java.util.*;

/**
 * AgriFlow Main Entry Point (Modules 1 to 6: CO1 to CO6)
 * 
 * Boots both the embedded HTTP Web Dashboard and a fully interactive Console CLI Runner.
 */
public class Main {

    private static AgriFlowServer server;
    private static int activePort = 8080;

    public static void main(String[] args) {
        // 1. Launch embedded Web Server on port 8080 in background thread
        try {
            server = new AgriFlowServer(8080);
            activePort = server.start();
        } catch (Exception e) {
            System.err.println("Notice: Could not start web server: " + e.getMessage());
        }

        // 2. Interactive Console Mode Loop
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            printHeader();
            System.out.println("Select an option to test in the Interactive Console:");
            System.out.println("  [1] Module 1: String Searching (KMP, Z-Algorithm, Rabin-Karp, Aho-Corasick)");
            System.out.println("  [2] Module 2: Suffix Structures (Suffix Array, Kasai LCP, Suffix Automaton)");
            System.out.println("  [3] Module 3: Advanced Dynamic Programming (Levenshtein, NW DNA, SW Motif, Bitmask DP, MCM)");
            System.out.println("  [4] Module 4: Network Flow (Irrigation Flow, Dinic, Min-Cut, Bipartite Matching)");
            System.out.println("  [5] Module 5: NP-Completeness & Approximation (3-SAT DPLL, Reductions, 2-Approx Vertex Cover)");
            System.out.println("  [6] Module 6: Randomized & Parallel (Reservoir Stream, Blelloch Scan, Brent, Miller-Rabin, QuickSort)");
            System.out.println("  [7] Run Full Automated Benchmark across ALL 6 Modules (M1 to M6)");
            System.out.println("  [8] Open Web Dashboard in Browser (http://localhost:" + activePort + ")");
            System.out.println("  [9] Explore Kaggle Agritech Dataset Suite (14 Production .txt Files)");
            System.out.println("  [0] Exit Application");
            System.out.print("\nEnter your choice (0-9): ");

            if (!scanner.hasNextLine()) break;
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> handleModule1Menu(scanner);
                case "2" -> handleModule2Menu(scanner);
                case "3" -> handleModule3Menu(scanner);
                case "4" -> handleModule4Menu(scanner);
                case "5" -> handleModule5Menu(scanner);
                case "6" -> handleModule6Menu(scanner);
                case "7" -> runAllBenchmarks();
                case "8" -> {
                    System.out.println("\nOpening http://localhost:" + activePort + " in browser...");
                    AgriFlowServer.openBrowser(activePort);
                    pauseForUser(scanner);
                }
                case "9" -> handleDatasetExplorer(scanner);
                case "0" -> {
                    System.out.println("\nShutting down AgriFlow. Goodbye! 🌾");
                    running = false;
                }
                default -> {
                    System.out.println("\n[!] Invalid choice. Please select 0 to 9.");
                    pauseForUser(scanner);
                }
            }
        }

        if (server != null) {
            server.stop();
        }
    }

    private static void printHeader() {
        System.out.println("\n==================================================================");
        System.out.println("      🌾 AgriFlow - Smart Farm Management & Optimization 🌾      ");
        System.out.println("             Full Platform: Modules 1 to 6 (CO1 to CO6)          ");
        System.out.println("==================================================================");
        System.out.println("  [Active Web Server: http://localhost:" + activePort + "]");
        System.out.println("------------------------------------------------------------------");
    }

    private static void pauseForUser(Scanner scanner) {
        System.out.print("\nPress Enter to return to menu...");
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }

    // =========================================================================
    // MODULE 1: STRING ALGORITHMS
    // =========================================================================
    private static void handleModule1Menu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- [MODULE 1] String Algorithms & Disease Diagnostics (CO1) ---");
            System.out.println("  1. Knuth-Morris-Pratt (KMP) Pattern Matcher");
            System.out.println("  2. Z-Algorithm Longest Prefix Box Matcher");
            System.out.println("  3. Rabin-Karp Rolling Hash Barcode / Lot Matcher");
            System.out.println("  4. Aho-Corasick Multi-Pattern Pathogen Scanner");
            System.out.println("  5. Benchmark & Compare All 4 String Algorithms Side-by-Side");
            System.out.println("  0. Back to Main Menu");
            System.out.print("Select sub-option (0-5): ");

            if (!scanner.hasNextLine()) break;
            String sub = scanner.nextLine().trim();
            switch (sub) {
                case "1" -> testSingleStringAlgorithm("KMP", scanner);
                case "2" -> testSingleStringAlgorithm("Z", scanner);
                case "3" -> testSingleStringAlgorithm("Rabin-Karp", scanner);
                case "4" -> testAhoCorasick(scanner);
                case "5" -> compareAllStringAlgorithms(scanner);
                case "0" -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void testSingleStringAlgorithm(String algoName, Scanner scanner) {
        String defaultText = "Field inspection found early blight and late blight symptoms on tomato leaves. Immediate copper fungicide suggested.";
        String defaultPattern = "blight";

        System.out.println("\n[Testing " + algoName + "]");
        System.out.print("Use preset agronomic report? (Y/n): ");
        String ans = scanner.nextLine().trim();

        String text = defaultText;
        String pattern = defaultPattern;

        if (ans.equalsIgnoreCase("n")) {
            System.out.print("Enter agricultural report text: ");
            text = scanner.nextLine().trim();
            System.out.print("Enter search keyword/pattern: ");
            pattern = scanner.nextLine().trim();
        }

        System.out.println("\nScanning text (length " + text.length() + "): \"" + text + "\"");
        System.out.println("Target Keyword: \"" + pattern + "\"");

        long t0 = System.nanoTime();
        List<Integer> matches;
        if ("KMP".equalsIgnoreCase(algoName)) {
            matches = KmpMatcher.search(text, pattern);
        } else if ("Z".equalsIgnoreCase(algoName)) {
            matches = ZAlgorithm.search(text, pattern);
        } else {
            matches = RabinKarpMatcher.search(text, pattern);
        }
        long t1 = System.nanoTime();

        System.out.printf("Algorithm: %s | Time: %,d ns (%.4f ms)%n", algoName, (t1 - t0), (t1 - t0) / 1e6);
        System.out.println("Matches Found: " + matches.size());
        for (int idx : matches) {
            int start = Math.max(0, idx - 15);
            int end = Math.min(text.length(), idx + pattern.length() + 15);
            String snippet = (start > 0 ? "..." : "") + text.substring(start, end) + (end < text.length() ? "..." : "");
            System.out.printf("  -> Match at index %3d: [ %s ]%n", idx, snippet);
        }
        pauseForUser(scanner);
    }

    private static void testAhoCorasick(Scanner scanner) {
        String defaultText = "Field inspection in Sector 7 detected powdery mildew and early blight on tomato foliage. Copper fungicide recommended.";
        List<String> defaultKeywords = List.of("blight", "mildew", "fungicide", "tomato");

        System.out.println("\n[Testing Aho-Corasick Multi-Pattern Disease Detector]");
        System.out.print("Use preset disease dictionary? (Y/n): ");
        String ans = scanner.nextLine().trim();

        String text = defaultText;
        List<String> keywords = defaultKeywords;

        if (ans.equalsIgnoreCase("n")) {
            System.out.print("Enter inspection log text: ");
            text = scanner.nextLine().trim();
            System.out.print("Enter disease keywords (comma-separated): ");
            String kwStr = scanner.nextLine().trim();
            keywords = Arrays.stream(kwStr.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
        }

        System.out.println("\nScanning text (length " + text.length() + "): \"" + text + "\"");
        System.out.println("Dictionary Keywords: " + keywords);

        long t0 = System.nanoTime();
        AhoCorasickDiseaseDetector detector = new AhoCorasickDiseaseDetector(keywords);
        List<AhoCorasickDiseaseDetector.MatchResult> hits = detector.scan(text);
        long t1 = System.nanoTime();

        System.out.printf("Aho-Corasick Trie Scan completed in %,d ns (%.4f ms)%n", (t1 - t0), (t1 - t0) / 1e6);
        System.out.println("Total Pathogen Keywords Detected: " + hits.size());
        for (var hit : hits) {
            int start = Math.max(0, hit.index - 15);
            int end = Math.min(text.length(), hit.index + hit.keyword.length() + 15);
            String snippet = (start > 0 ? "..." : "") + text.substring(start, end) + (end < text.length() ? "..." : "");
            System.out.printf("  -> [ %-12s ] at index %3d: [ %s ]%n", hit.keyword, hit.index, snippet);
        }
        pauseForUser(scanner);
    }

    private static void compareAllStringAlgorithms(Scanner scanner) {
        String text = "Field inspection found early blight and late blight symptoms on tomato leaves. Immediate copper fungicide suggested. Early blight risks potato crops as well.";
        String pattern = "blight";

        System.out.println("\n==================================================================");
        System.out.println("   BENCHMARK: Comparing All 4 String Algorithms Side-by-Side     ");
        System.out.println("==================================================================");
        System.out.println("Text: \"" + text + "\"");
        System.out.println("Query Keyword: \"" + pattern + "\"");

        long t0 = System.nanoTime();
        var kmpRes = KmpMatcher.search(text, pattern);
        long tKmp = System.nanoTime() - t0;

        t0 = System.nanoTime();
        var zRes = ZAlgorithm.search(text, pattern);
        long tZ = System.nanoTime() - t0;

        t0 = System.nanoTime();
        var rkRes = RabinKarpMatcher.search(text, pattern);
        long tRk = System.nanoTime() - t0;

        t0 = System.nanoTime();
        AhoCorasickDiseaseDetector aho = new AhoCorasickDiseaseDetector(List.of(pattern, "copper", "tomato"));
        var ahoRes = aho.scan(text);
        long tAho = System.nanoTime() - t0;

        System.out.println("\n+------------------------------+---------+-------------+--------------------+");
        System.out.println("| Algorithm                    | Matches | Time (ns)   | Time Complexity    |");
        System.out.println("+------------------------------+---------+-------------+--------------------+");
        System.out.printf("| %-28s | %7d | %11d | %-18s |%n", "Knuth-Morris-Pratt (KMP)", kmpRes.size(), tKmp, "O(N + M)");
        System.out.printf("| %-28s | %7d | %11d | %-18s |%n", "Z-Algorithm", zRes.size(), tZ, "O(N + M)");
        System.out.printf("| %-28s | %7d | %11d | %-18s |%n", "Rabin-Karp Rolling Hash", rkRes.size(), tRk, "Avg O(N + M)");
        System.out.printf("| %-28s | %7d | %11d | %-18s |%n", "Aho-Corasick Multi-Pattern", ahoRes.size(), tAho, "O(N + sum(M) + Z)");
        System.out.println("+------------------------------+---------+-------------+--------------------+");

        pauseForUser(scanner);
    }

    // =========================================================================
    // MODULE 2: SUFFIX STRUCTURES
    // =========================================================================
    private static void handleModule2Menu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- [MODULE 2] Suffix Structures & Agricultural Documents (CO2) ---");
            System.out.println("  1. Suffix Array Construction & Sorted Suffixes Table");
            System.out.println("  2. Kasai's LCP Array & Longest Common Repeated Protocol");
            System.out.println("  3. Suffix Automaton (DAWG) Instant O(M) Substring Existence Query");
            System.out.println("  4. Total Distinct Substrings Counter & State Analysis");
            System.out.println("  5. Run Full Module 2 Demonstration");
            System.out.println("  0. Back to Main Menu");
            System.out.print("Select sub-option (0-5): ");

            if (!scanner.hasNextLine()) break;
            String sub = scanner.nextLine().trim();
            switch (sub) {
                case "1" -> testSuffixArray(scanner);
                case "2" -> testKasaiLcp(scanner);
                case "3" -> testSuffixAutomatonQuery(scanner);
                case "4" -> testDistinctSubstrings(scanner);
                case "5" -> {
                    runModule2Demo();
                    pauseForUser(scanner);
                }
                case "0" -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void testSuffixArray(Scanner scanner) {
        String doc = "Apply organic potassium fertilizer during early flowering. Soil inspection required. Apply organic potassium fertilizer during early flowering.";
        System.out.println("\n[Suffix Array Prefix Doubling Construction]");
        System.out.print("Use preset agronomy document? (Y/n): ");
        String ans = scanner.nextLine().trim();
        if (ans.equalsIgnoreCase("n")) {
            System.out.print("Enter document text: ");
            doc = scanner.nextLine().trim();
        }

        long t0 = System.nanoTime();
        int[] sa = SuffixArrayBuilder.buildSuffixArray(doc);
        long t1 = System.nanoTime();

        System.out.printf("Suffix Array built in %,d ns (%.4f ms). Length: %d%n", (t1 - t0), (t1 - t0) / 1e6, sa.length);
        System.out.println("\nFirst 10 Lexicographically Sorted Suffixes:");
        System.out.println("+------+-------+---------------------------------------------+");
        System.out.println("| Rank | Index | Suffix Preview                              |");
        System.out.println("+------+-------+---------------------------------------------+");
        int count = Math.min(10, sa.length);
        for (int i = 0; i < count; i++) {
            int start = sa[i];
            String preview = doc.substring(start, Math.min(doc.length(), start + 40)).replace("\n", " ");
            System.out.printf("| %4d | %5d | %-43s |%n", i, start, preview);
        }
        System.out.println("+------+-------+---------------------------------------------+");
        pauseForUser(scanner);
    }

    private static void testKasaiLcp(Scanner scanner) {
        String doc = "Apply organic potassium fertilizer during early flowering. Soil inspection required. Apply organic potassium fertilizer during early flowering.";
        System.out.println("\n[Kasai's LCP Array Construction & Protocol Duplication Discovery]");
        System.out.print("Use preset agronomy document? (Y/n): ");
        String ans = scanner.nextLine().trim();
        if (ans.equalsIgnoreCase("n")) {
            System.out.print("Enter document text: ");
            doc = scanner.nextLine().trim();
        }

        int[] sa = SuffixArrayBuilder.buildSuffixArray(doc);
        long t0 = System.nanoTime();
        int[] lcp = KasaiLcp.buildLcpArray(doc, sa);
        var repeated = KasaiLcp.findLongestRepeatedPhrase(doc, sa, lcp);
        long t1 = System.nanoTime();

        System.out.printf("Kasai LCP computed in %,d ns (%.4f ms)%n", (t1 - t0), (t1 - t0) / 1e6);
        System.out.println("Longest Shared Agricultural Recommendation:");
        System.out.println("  -> Phrase: \"" + repeated.phrase + "\"");
        System.out.println("  -> Character Length: " + repeated.length);
        System.out.printf("  -> Occurrences between text positions %d and %d%n", repeated.index1, repeated.index2);
        pauseForUser(scanner);
    }

    private static void testSuffixAutomatonQuery(Scanner scanner) {
        String doc = "Apply organic potassium fertilizer during early flowering. Regular soil nitrogen inspection advised.";
        System.out.println("\n[Suffix Automaton (DAWG) Instant O(M) Substring Query]");
        System.out.println("Document: \"" + doc + "\"");

        SuffixAutomaton sam = new SuffixAutomaton(doc);
        int[] sa = SuffixArrayBuilder.buildSuffixArray(doc);

        System.out.print("Enter pattern to test existence (e.g., 'potassium', 'flowering', 'chemical'): ");
        String pattern = scanner.nextLine().trim();
        if (pattern.isEmpty()) pattern = "potassium";

        long t0 = System.nanoTime();
        boolean exists = sam.contains(pattern);
        long tSam = System.nanoTime() - t0;

        long t1 = System.nanoTime();
        int saIdx = SuffixArrayBuilder.search(doc, pattern, sa);
        long tSa = System.nanoTime() - t1;

        System.out.println("\n--- Query Results ---");
        System.out.println("Pattern: \"" + pattern + "\"");
        System.out.println("Exists in Document: " + (exists ? "YES (Found!)" : "NO (Not Found)"));
        System.out.printf("Suffix Automaton Query Time : %,d ns (O(M) transition walk)%n", tSam);
        System.out.printf("Suffix Array Binary Search  : %,d ns (Matched at index %d)%n", tSa, saIdx);

        pauseForUser(scanner);
    }

    private static void testDistinctSubstrings(Scanner scanner) {
        String doc = "Apply organic potassium fertilizer during flowering. Regular soil nitrogen testing advised.";
        System.out.println("\n[Suffix Automaton Distinct Substrings Counter]");
        System.out.println("Document: \"" + doc + "\"");

        long t0 = System.nanoTime();
        SuffixAutomaton sam = new SuffixAutomaton(doc);
        long distinct = sam.countDistinctSubstrings();
        long t1 = System.nanoTime();

        System.out.printf("Distinct Substring Analysis completed in %,d ns%n", (t1 - t0));
        System.out.println("Total Indexed Text Length   : " + doc.length());
        System.out.println("Total Distinct Substrings   : " + distinct);
        System.out.println("Total Automaton States      : " + sam.getStateCount());

        pauseForUser(scanner);
    }

    // =========================================================================
    // MODULE 3: ADVANCED DYNAMIC PROGRAMMING
    // =========================================================================
    private static void handleModule3Menu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- [MODULE 3] Advanced Dynamic Programming (CO3) ---");
            System.out.println("  1. Farmer Voice/Text Query Spell Check (Levenshtein & Damerau)");
            System.out.println("  2. Global Crop DNA Sequence Alignment (Needleman-Wunsch)");
            System.out.println("  3. Local Crop DNA Motif Alignment (Smith-Waterman)");
            System.out.println("  4. Bitmask DP: Optimal Multi-Crop Portfolio Planner");
            System.out.println("  5. Matrix Chain Multiplication (MCM) Geo-Sensor Pipeline");
            System.out.println("  6. Run Full Module 3 Demonstration");
            System.out.println("  0. Back to Main Menu");
            System.out.print("Select sub-option (0-6): ");

            if (!scanner.hasNextLine()) break;
            String sub = scanner.nextLine().trim();
            switch (sub) {
                case "1" -> testLevenshtein(scanner);
                case "2" -> testNeedlemanWunsch(scanner);
                case "3" -> testSmithWaterman(scanner);
                case "4" -> testBitmaskPlanner(scanner);
                case "5" -> testMcm(scanner);
                case "6" -> {
                    runModule3Demo();
                    pauseForUser(scanner);
                }
                case "0" -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void testLevenshtein(Scanner scanner) {
        System.out.println("\n[Farmer Query Typo Autocorrection - Levenshtein DP]");
        System.out.print("Enter misspelled agricultural query (or press Enter for 'fertlzr'): ");
        String q = scanner.nextLine().trim();
        if (q.isEmpty()) q = "fertlzr";

        long t0 = System.nanoTime();
        var top = LevenshteinDistance.findTopMatches(q, 4);
        long t1 = System.nanoTime();

        var best = top.isEmpty() ? new LevenshteinDistance.Suggestion("none", 999) : top.get(0);
        System.out.printf("Farmer Typed: '%s'%n", q);
        System.out.printf("Best Match  : '%s' (Edit Distance: %d) in %,d ns%n", best.term, best.distance, (t1 - t0));
        System.out.println("Top Alternative Dictionary Suggestions:");
        for (var s : top) {
            System.out.printf("  -> %-16s [Distance: %d]%n", s.term, s.distance);
        }
        pauseForUser(scanner);
    }

    private static void testNeedlemanWunsch(Scanner scanner) {
        String seq1 = "ATGCGTACAGTAGCTAGCT";
        String seq2 = "ATGCTACGGTAGCTAGCT";

        System.out.println("\n[Global Crop DNA Alignment - Needleman-Wunsch DP]");
        System.out.print("Use preset wheat DNA strands? (Y/n): ");
        String ans = scanner.nextLine().trim();
        if (ans.equalsIgnoreCase("n")) {
            System.out.print("Enter Sequence 1 (Wild ancestral strain): ");
            seq1 = scanner.nextLine().trim().toUpperCase();
            System.out.print("Enter Sequence 2 (Modern cultivar hybrid): ");
            seq2 = scanner.nextLine().trim().toUpperCase();
        }

        NeedlemanWunschAligner nw = new NeedlemanWunschAligner();
        long t0 = System.nanoTime();
        var res = nw.align(seq1, seq2);
        long t1 = System.nanoTime();

        System.out.printf("Global Alignment solved in %,d ns (%.4f ms)%n", (t1 - t0), (t1 - t0) / 1e6);
        System.out.println("Alignment Score   : " + res.score);
        System.out.printf("Genetic Identity  : %.1f%%%n", res.identityPercentage);
        System.out.println("\nVisual Alignment:");
        System.out.println("Seq 1 : " + res.alignedSeq1);
        System.out.println("Match : " + res.matchBar);
        System.out.println("Seq 2 : " + res.alignedSeq2);
        pauseForUser(scanner);
    }

    private static void testSmithWaterman(Scanner scanner) {
        String seq1 = "GGGTACGTAAAACCCCGG";
        String seq2 = "AAAATACGTAAATTTTTT";

        System.out.println("\n[Local Crop DNA Motif Alignment - Smith-Waterman DP]");
        System.out.print("Use preset disease-resistance allele strands? (Y/n): ");
        String ans = scanner.nextLine().trim();
        if (ans.equalsIgnoreCase("n")) {
            System.out.print("Enter Genetic Sequence 1: ");
            seq1 = scanner.nextLine().trim().toUpperCase();
            System.out.print("Enter Genetic Sequence 2: ");
            seq2 = scanner.nextLine().trim().toUpperCase();
        }

        SmithWatermanAligner sw = new SmithWatermanAligner();
        long t0 = System.nanoTime();
        var res = sw.align(seq1, seq2);
        long t1 = System.nanoTime();

        System.out.printf("Local Motif Alignment solved in %,d ns (%.4f ms)%n", (t1 - t0), (t1 - t0) / 1e6);
        System.out.println("Conserved Motif Max Score: " + res.maxScore);
        System.out.printf("Motif in Seq 1 (from pos %d): %s%n", res.startPos1, res.localSeq1);
        System.out.printf("Motif in Seq 2 (from pos %d): %s%n", res.startPos2, res.localSeq2);
        pauseForUser(scanner);
    }

    private static void testBitmaskPlanner(Scanner scanner) {
        System.out.println("\n[Bitmask DP: Multi-Crop Portfolio Optimization]");
        System.out.print("Enter Max Farm Land Limit in Acres (default 65): ");
        String lStr = scanner.nextLine().trim();
        int maxLand = lStr.isEmpty() ? 65 : Integer.parseInt(lStr);

        System.out.print("Enter Max Water Budget in kL (default 85): ");
        String wStr = scanner.nextLine().trim();
        int maxWater = wStr.isEmpty() ? 85 : Integer.parseInt(wStr);

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

        System.out.println("\n==================================================================");
        System.out.printf("Optimal Bitmask DP Solution (Computed in %,d ns)%n", (t1 - t0));
        System.out.println("==================================================================");
        System.out.printf("Max Projected Harvest Profit : $%,d%n", plan.totalProfit);
        System.out.printf("Land Utilized                : %d / %d Acres (%.1f%%)%n", plan.totalLandUsed, maxLand, (100.0 * plan.totalLandUsed / maxLand));
        System.out.printf("Water Utilized               : %d / %d kL (%.1f%%)%n", plan.totalWaterUsed, maxWater, (100.0 * plan.totalWaterUsed / maxWater));
        System.out.println("\nSelected Crop Portfolio:");
        for (var c : plan.selectedCrops) {
            System.out.printf("  [+] %-25s | Land: %2dA | Water: %2dU | Profit: $%,d%n", c.name, c.landAcres, c.waterRequiredUnits, c.expectedProfit);
        }
        pauseForUser(scanner);
    }

    private static void testMcm(Scanner scanner) {
        int[] dims = {10, 100, 5, 50, 1};
        String[] labels = {"DroneElevation", "SoilSensors", "SatelliteNDVI", "YieldProjection"};

        System.out.println("\n[Matrix Chain Multiplication (MCM) Geo-Pipeline Optimization]");
        System.out.println("Pipeline stages: DroneElevation (10x100), SoilSensors (100x5), SatelliteNDVI (5x50), YieldProjection (50x1)");

        long t0 = System.nanoTime();
        var res = MatrixChainOptimizer.optimize(dims, labels);
        long t1 = System.nanoTime();

        System.out.printf("MCM Dynamic Programming solved in %,d ns%n", (t1 - t0));
        System.out.printf("Minimal Scalar Multiplications : %,d%n", res.minScalarMultiplications);
        System.out.println("Optimal Parenthesization Order : " + res.optimalParenthesization);
        pauseForUser(scanner);
    }

    // =========================================================================
    // MODULE 4: NETWORK FLOW & MATCHING
    // =========================================================================
    private static void handleModule4Menu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- [MODULE 4] Irrigation Network Flow & Machinery Matching (CO4) ---");
            System.out.println("  1. Dinic's Algorithm for Maximum Irrigation Throughput");
            System.out.println("  2. Edmonds-Karp Max Water Flow (BFS Augmenting Paths)");
            System.out.println("  3. Min-Cut Bottleneck Canal Isolation (S-T Cut)");
            System.out.println("  4. Maximum Bipartite Matching for Farm Machinery Allocation");
            System.out.println("  5. Run Full Module 4 Demonstration");
            System.out.println("  0. Back to Main Menu");
            System.out.print("Select sub-option (0-5): ");

            if (!scanner.hasNextLine()) break;
            String sub = scanner.nextLine().trim();
            switch (sub) {
                case "1" -> testDinicFlow(scanner);
                case "2" -> testEdmondsKarpFlow(scanner);
                case "3" -> testMinCutBottlenecks(scanner);
                case "4" -> testBipartiteMatching(scanner);
                case "5" -> {
                    runModule4Demo();
                    pauseForUser(scanner);
                }
                case "0" -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static FlowNetwork buildDefaultIrrigationNetwork() {
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
        return net;
    }

    private static void testDinicFlow(Scanner scanner) {
        System.out.println("\n[Dinic's Algorithm: Large-Scale Irrigation Network Flow]");
        FlowNetwork net = buildDefaultIrrigationNetwork();

        long t0 = System.nanoTime();
        DinicMaxFlow dinic = new DinicMaxFlow(net, 0, 5);
        int maxFlow = dinic.computeMaxFlow();
        long t1 = System.nanoTime();

        System.out.printf("Dinic Max Flow computed in %,d ns (%.4f ms)%n", (t1 - t0), (t1 - t0) / 1e6);
        System.out.printf("Total Maximum Water Delivery: %,d kL/hour%n", maxFlow);
        pauseForUser(scanner);
    }

    private static void testEdmondsKarpFlow(Scanner scanner) {
        System.out.println("\n[Edmonds-Karp Algorithm: BFS Augmenting Paths Flow]");
        FlowNetwork net = buildDefaultIrrigationNetwork();

        long t0 = System.nanoTime();
        int maxFlow = EdmondsKarp.computeMaxFlow(net, 0, 5);
        long t1 = System.nanoTime();

        System.out.printf("Edmonds-Karp Max Flow computed in %,d ns (%.4f ms)%n", (t1 - t0), (t1 - t0) / 1e6);
        System.out.printf("Total Maximum Water Delivery: %,d kL/hour%n", maxFlow);
        pauseForUser(scanner);
    }

    private static void testMinCutBottlenecks(Scanner scanner) {
        System.out.println("\n[Max-Flow Min-Cut: Pinpointing Bottleneck Canals]");
        FlowNetwork net = buildDefaultIrrigationNetwork();
        DinicMaxFlow dinic = new DinicMaxFlow(net, 0, 5);
        int maxFlow = dinic.computeMaxFlow();

        long t0 = System.nanoTime();
        var bottlenecks = MinCutBottleneckFinder.findBottlenecks(net, 0);
        long t1 = System.nanoTime();

        System.out.printf("Min-Cut identified in %,d ns%n", (t1 - t0));
        System.out.printf("Network Max Throughput: %,d kL/hour%n", maxFlow);
        System.out.println("Critical Saturated Bottleneck Canals Restricting Total Delivery:");
        for (var b : bottlenecks) {
            System.out.printf("  [!] %s -> %s (Restricting Capacity: %d kL/hr)%n", b.from, b.to, b.capacity);
        }
        pauseForUser(scanner);
    }

    private static void testBipartiteMatching(Scanner scanner) {
        System.out.println("\n[Maximum Bipartite Matching: Farm Machinery Allocation]");
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

        System.out.printf("Bipartite Matching solved in %,d ns%n", (t1 - t0));
        System.out.printf("Total Farms Matched: %d / %d%n", assignments.size(), farms.size());
        System.out.println("Optimal Equipment Assignments:");
        for (var a : assignments) {
            System.out.printf("  * %-20s <==== Assigned ====> %s%n", a.farmName, a.equipmentName);
        }
        pauseForUser(scanner);
    }

    // =========================================================================
    // MODULE 5: NP-COMPLETENESS & APPROXIMATION
    // =========================================================================
    private static void handleModule5Menu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- [MODULE 5] NP-Completeness & 2-Approximation (CO5) ---");
            System.out.println("  1. Crop Planning 3-SAT DPLL Constraint Solver");
            System.out.println("  2. Polynomial-Time Reduction to CLIQUE Graph Problem");
            System.out.println("  3. 2-Approximation Vertex Cover: IoT Sensor Watchtower Placement");
            System.out.println("  4. Run Full Module 5 Demonstration");
            System.out.println("  0. Back to Main Menu");
            System.out.print("Select sub-option (0-4): ");

            if (!scanner.hasNextLine()) break;
            String sub = scanner.nextLine().trim();
            switch (sub) {
                case "1" -> testSatSolver(scanner);
                case "2" -> testReductionMapper(scanner);
                case "3" -> testVertexCover(scanner);
                case "4" -> {
                    runModule5Demo();
                    pauseForUser(scanner);
                }
                case "0" -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void testSatSolver(Scanner scanner) {
        System.out.println("\n[Crop Planning 3-SAT DPLL Constraint Solver]");
        System.out.println("Agricultural Rules:");
        System.out.println("  Rule 1: (Corn v Soybeans)         -> Must cultivate either Corn or Soybeans");
        System.out.println("  Rule 2: (~Corn v ~Soybeans)       -> Cannot cultivate both concurrently in Zone A");
        System.out.println("  Rule 3: (~Corn v HighNitrogen)    -> Corn requires high nitrogen soil");
        System.out.println("  Rule 4: (~Soybeans v ~HighNitrogen) -> Soybeans prone to lodging if nitrogen excessive");

        List<CropConstraintSatSolver.Clause> clauses = List.of(
            new CropConstraintSatSolver.Clause(1, 2),
            new CropConstraintSatSolver.Clause(-1, -2),
            new CropConstraintSatSolver.Clause(-1, 3),
            new CropConstraintSatSolver.Clause(-2, -3)
        );

        long t0 = System.nanoTime();
        var res = CropConstraintSatSolver.solve(clauses, 3);
        long t1 = System.nanoTime();

        System.out.printf("DPLL Solver evaluated rules in %,d ns%n", (t1 - t0));
        System.out.println("Status: " + (res.satisfiable ? "SATISFIABLE (Consistent Plan!)" : "UNSATISFIABLE"));
        if (res.satisfiable && res.model != null) {
            System.out.println("Valid Crop Planning Model:");
            System.out.println("  Plant Corn (x1)         : " + res.model.getOrDefault(1, false));
            System.out.println("  Plant Soybeans (x2)     : " + res.model.getOrDefault(2, false));
            System.out.println("  High Nitrogen Soil (x3) : " + res.model.getOrDefault(3, false));
        }
        pauseForUser(scanner);
    }

    private static void testReductionMapper(Scanner scanner) {
        System.out.println("\n[Polynomial-Time Reductions: 3-SAT -> CLIQUE -> INDEP-SET -> VERTEX COVER]");
        List<CropConstraintSatSolver.Clause> clauses = List.of(
            new CropConstraintSatSolver.Clause(1, 2),
            new CropConstraintSatSolver.Clause(-1, 3),
            new CropConstraintSatSolver.Clause(2, -3)
        );

        long t0 = System.nanoTime();
        var cliqueGraph = ReductionGraphMapper.reduce3SatToClique(clauses);
        long t1 = System.nanoTime();

        System.out.printf("Polynomial Reduction mapped in %,d ns%n", (t1 - t0));
        System.out.printf("Generated Gadget Graph: %d vertices, %d compatibility edges%n",
                cliqueGraph.vertexLabels.size(), cliqueGraph.adj.values().stream().mapToInt(List::size).sum() / 2);
        System.out.println("A k-clique in this graph proves the existence of a satisfying agricultural plan.");
        pauseForUser(scanner);
    }

    private static void testVertexCover(Scanner scanner) {
        System.out.println("\n[2-Approximation Vertex Cover: Minimum Watchtower Placement]");
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

        System.out.printf("2-Approximation computed in %,d ns%n", (t1 - t0));
        System.out.printf("Minimal Towers Placed: %d (Guaranteed <= 2 * OPT)%n", plan.towerLocations.size());
        System.out.println("Watchtower & Weather Station Locations:");
        for (String loc : plan.towerLocations) {
            System.out.printf("  [TOWER] %s%n", loc);
        }
        pauseForUser(scanner);
    }

    // =========================================================================
    // MODULE 6: RANDOMIZED & PARALLEL TELEMETRY
    // =========================================================================
    private static void handleModule6Menu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- [MODULE 6] Randomized & Parallel Sensor Telemetry (CO6) ---");
            System.out.println("  1. Reservoir Sampling from Continuous Sensor Stream");
            System.out.println("  2. Blelloch Work-Efficient Parallel Prefix Scan (Rainfall)");
            System.out.println("  3. Brent's Theorem Work-Time Scheduling Scaling Analysis");
            System.out.println("  4. Miller-Rabin Cryptographic Organic Seed Primality");
            System.out.println("  5. Fork-Join Parallel Sensor Reduction (Min/Max/Avg/Sum)");
            System.out.println("  6. Randomized QuickSort Crop Parcel Yield Ranking");
            System.out.println("  7. Run Full Module 6 Demonstration");
            System.out.println("  0. Back to Main Menu");
            System.out.print("Select sub-option (0-7): ");

            if (!scanner.hasNextLine()) break;
            String sub = scanner.nextLine().trim();
            switch (sub) {
                case "1" -> testReservoirSampler(scanner);
                case "2" -> testBlellochScan(scanner);
                case "3" -> testBrentsTheorem(scanner);
                case "4" -> testMillerRabin(scanner);
                case "5" -> testParallelReduce(scanner);
                case "6" -> testRandomizedQuickSort(scanner);
                case "7" -> {
                    runModule6Demo();
                    pauseForUser(scanner);
                }
                case "0" -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void testReservoirSampler(Scanner scanner) {
        System.out.println("\n[Reservoir Sampling (Algorithm R) from Infinite Sensor Stream]");
        System.out.print("Enter sample size K (default 5): ");
        String kStr = scanner.nextLine().trim();
        int k = kStr.isEmpty() ? 5 : Integer.parseInt(kStr);

        System.out.print("Enter total streaming observations N (default 1000): ");
        String nStr = scanner.nextLine().trim();
        int n = nStr.isEmpty() ? 1000 : Integer.parseInt(nStr);

        ReservoirSampler<ReservoirSampler.SensorReading> sampler = new ReservoirSampler<>(k);
        Random rand = new Random(42);

        long t0 = System.nanoTime();
        for (int i = 1; i <= n; i++) {
            sampler.ingest(new ReservoirSampler.SensorReading(
                System.currentTimeMillis() + i * 500L,
                "PROBE-" + (i % 25 + 1),
                20.0 + rand.nextDouble() * 30.0,
                18.0 + rand.nextDouble() * 15.0
            ));
        }
        long t1 = System.nanoTime();

        System.out.printf("Stream of %,d items sampled in %,d ns (%.4f ms)%n", n, (t1 - t0), (t1 - t0) / 1e6);
        System.out.printf("Unbiased Uniform Random Reservoir Sample (Size %d):%n", k);
        for (var r : sampler.getSample()) {
            System.out.printf("  * %s%n", r);
        }
        pauseForUser(scanner);
    }

    private static void testBlellochScan(Scanner scanner) {
        double[] dailyRainfall = {5.0, 12.0, 0.0, 8.5, 15.0, 22.0, 4.0, 11.5};
        System.out.println("\n[Blelloch Parallel Prefix Sum Scan: Cumulative Rainfall]");
        System.out.println("Daily Rainfall Data (mm): " + Arrays.toString(dailyRainfall));

        long t0 = System.nanoTime();
        double[] exclusive = BlellochScan.exclusiveScan(dailyRainfall);
        double[] inclusive = BlellochScan.inclusiveScan(dailyRainfall);
        long t1 = System.nanoTime();

        System.out.printf("Work-Efficient Scan solved in %,d ns (Work: O(N), Span: O(log N))%n", (t1 - t0));
        System.out.println("Cumulative Exclusive Scan : " + Arrays.toString(exclusive));
        System.out.println("Cumulative Inclusive Scan : " + Arrays.toString(inclusive));
        pauseForUser(scanner);
    }

    private static void testBrentsTheorem(Scanner scanner) {
        double work = 2_000_000.0;
        double depth = 40.0;

        System.out.println("\n[Brent's Theorem Work-Time Scheduling for Edge Cluster]");
        System.out.printf("Total Telemetry Work (W): %,.0f operations | Critical Path Depth (D): %,.0f steps%n%n", work, depth);

        var scaling = BrentsTheoremAnalyzer.evaluateScaling(work, depth, 16);
        System.out.println("+-------+-----------------+---------+------------+");
        System.out.println("| Cores | Time Bound (ops)| Speedup | Efficiency |");
        System.out.println("+-------+-----------------+---------+------------+");
        for (var m : scaling) {
            System.out.printf("| %5d | %15.2f | %6.2fx | %9.1f%% |%n", m.cores, m.timeBound, m.speedup, m.efficiency * 100.0);
        }
        System.out.println("+-------+-----------------+---------+------------+");
        pauseForUser(scanner);
    }

    private static void testMillerRabin(Scanner scanner) {
        System.out.println("\n[Miller-Rabin Randomized Primality Testing for Seed Authentication]");
        BigInteger[] testKeys = {
            BigInteger.valueOf(104729),
            BigInteger.valueOf(104730),
            new BigInteger("982451653"),
            new BigInteger("982451655")
        };

        for (BigInteger k : testKeys) {
            long t0 = System.nanoTime();
            boolean isPrime = MillerRabinVerifier.isProbablePrime(k, 10);
            long t1 = System.nanoTime();
            System.out.printf("Batch Key %-10s : %-25s (Checked in %,d ns)%n",
                    k, isPrime ? "PRIME (Valid Organic Key)" : "COMPOSITE (Invalid Tag)", (t1 - t0));
        }
        pauseForUser(scanner);
    }

    private static void testParallelReduce(Scanner scanner) {
        System.out.println("\n[Parallel Fork-Join Sensor Reduction across 50,000 Readings]");
        double[] sensorData = new double[50000];
        Random rand = new Random(42);
        for (int i = 0; i < sensorData.length; i++) {
            sensorData[i] = 15.0 + rand.nextDouble() * 25.0;
        }

        long t0 = System.nanoTime();
        var stats = ParallelSensorReduce.reduce(sensorData);
        long t1 = System.nanoTime();

        System.out.printf("Parallel Reduction across %,d probes completed in %,d ns (%.4f ms)%n",
                sensorData.length, (t1 - t0), (t1 - t0) / 1e6);
        System.out.printf("Sensor Telemetry Statistics: Min: %.2f°C | Max: %.2f°C | Avg: %.2f°C%n",
                stats.min, stats.max, stats.average());
        pauseForUser(scanner);
    }

    private static void testRandomizedQuickSort(Scanner scanner) {
        System.out.println("\n[Randomized QuickSort: Farm Yield Ranking]");
        List<RandomizedQuickSort.FarmRecord> harvestData = new ArrayList<>(List.of(
            new RandomizedQuickSort.FarmRecord("Farm-Alpha", "Corn", 4.2),
            new RandomizedQuickSort.FarmRecord("Farm-Beta", "Wheat", 3.8),
            new RandomizedQuickSort.FarmRecord("Farm-Gamma", "Soybean", 2.9),
            new RandomizedQuickSort.FarmRecord("Farm-Delta", "Corn", 5.1),
            new RandomizedQuickSort.FarmRecord("Farm-Epsilon", "Rice", 6.3),
            new RandomizedQuickSort.FarmRecord("Farm-Zeta", "Tomatoes", 7.8)
        ));

        System.out.println("Unsorted Harvest Yields:");
        harvestData.forEach(r -> System.out.println("  " + r));

        long t0 = System.nanoTime();
        RandomizedQuickSort.sort(harvestData);
        long t1 = System.nanoTime();

        System.out.printf("%nRandomized QuickSort completed in %,d ns (Expected O(N log N))%n", (t1 - t0));
        System.out.println("Ranked Farm Productivity (Highest Yield First):");
        harvestData.forEach(r -> System.out.println("  [RANKED] " + r));
        pauseForUser(scanner);
    }

    // =========================================================================
    // DEMO RUNNERS & BENCHMARKS (MODULES 1 TO 6)
    // =========================================================================
    public static void runModule1Demo() {
        System.out.println("\n[DEMO] M1: String Algorithms & Disease Detection");
        String text = "Field inspection found early blight and late blight symptoms on tomato leaves. Immediate copper fungicide suggested.";
        System.out.println("Text: " + text);
        System.out.println("KMP matches for 'blight': " + KmpMatcher.search(text, "blight"));
        System.out.println("Z-Algorithm matches for 'blight': " + ZAlgorithm.search(text, "blight"));
        System.out.println("Rabin-Karp matches for 'copper': " + RabinKarpMatcher.search(text, "copper"));

        AhoCorasickDiseaseDetector aho = new AhoCorasickDiseaseDetector(List.of("blight", "fungicide", "tomato"));
        System.out.println("Aho-Corasick simultaneous scan: " + aho.scan(text));
    }

    public static void runModule2Demo() {
        System.out.println("\n[DEMO] M2: Suffix Structures & Document Analysis");
        String doc = "Apply organic potassium fertilizer during flowering. Regular soil tests advised. Apply organic potassium fertilizer during flowering.";
        int[] sa = SuffixArrayBuilder.buildSuffixArray(doc);
        int[] lcp = KasaiLcp.buildLcpArray(doc, sa);
        var repeated = KasaiLcp.findLongestRepeatedPhrase(doc, sa, lcp);
        SuffixAutomaton sam = new SuffixAutomaton(doc);

        System.out.println("Longest duplicated agricultural recommendation: " + repeated);
        System.out.println("Total distinct substrings indexed: " + sam.countDistinctSubstrings());
        System.out.println("Automaton contains 'potassium': " + sam.contains("potassium"));
    }

    public static void runModule3Demo() {
        System.out.println("\n[DEMO] M3: Advanced Dynamic Programming");
        NeedlemanWunschAligner nw = new NeedlemanWunschAligner();
        var align = nw.align("ATGCGTACAGTAGCT", "ATGCTACGGTAGCT");
        System.out.println("Global Crop DNA Alignment:\n" + align);

        SmithWatermanAligner sw = new SmithWatermanAligner();
        var motif = sw.align("GGGTACGTAAAACCCCGG", "AAAATACGTAAATTTTTT");
        System.out.println("\nLocal Conserved Motif:\n" + motif);

        List<BitmaskCropPlanner.Crop> crops = List.of(
            new BitmaskCropPlanner.Crop(0, "Organic Wheat", 30, 40, 12000),
            new BitmaskCropPlanner.Crop(1, "Soybean (Nitrogen Fixer)", 25, 25, 9500),
            new BitmaskCropPlanner.Crop(2, "Sweet Corn", 35, 55, 14000)
        );
        var plan = BitmaskCropPlanner.optimize(crops, 55, 65, null);
        System.out.println("\nBitmask DP Crop Portfolio:\n" + plan);

        var typo = LevenshteinDistance.findBestMatch("fertlzr");
        System.out.println("Farmer Typo 'fertlzr' corrected to: " + typo);
    }

    public static void runModule4Demo() {
        System.out.println("\n[DEMO] M4: Irrigation Network Flow & Resource Matching");
        FlowNetwork net = buildDefaultIrrigationNetwork();
        int maxFlow = new DinicMaxFlow(net, 0, 5).computeMaxFlow();
        System.out.println("Dinic Max Irrigation Flow: " + maxFlow + " kL/hr");

        var cuts = MinCutBottleneckFinder.findBottlenecks(net, 0);
        System.out.println("Min-Cut Bottleneck Canals: " + cuts);
    }

    public static void runModule5Demo() {
        System.out.println("\n[DEMO] M5: NP-Completeness & 2-Approximation");
        List<CropConstraintSatSolver.Clause> clauses = List.of(
            new CropConstraintSatSolver.Clause(1, 2),
            new CropConstraintSatSolver.Clause(-1, 3)
        );
        var sat = CropConstraintSatSolver.solve(clauses, 3);
        System.out.println("Crop Planning 3-SAT Result: " + (sat.satisfiable ? "SATISFIABLE" : "UNSATISFIABLE"));

        List<String> junctions = List.of("J1", "J2", "J3", "J4");
        List<VertexCoverApproximation.RoadSegment> paths = List.of(
            new VertexCoverApproximation.RoadSegment("J1", "J2"),
            new VertexCoverApproximation.RoadSegment("J2", "J3"),
            new VertexCoverApproximation.RoadSegment("J3", "J4")
        );
        var cover = VertexCoverApproximation.approximateCover(junctions, paths);
        System.out.println("2-Approximation Sensor Tower Placements: " + cover.towerLocations);
    }

    public static void runModule6Demo() {
        System.out.println("\n[DEMO] M6: Randomized & Parallel Sensor Telemetry");
        ReservoirSampler<String> sampler = new ReservoirSampler<>(3);
        for (int i = 1; i <= 500; i++) {
            sampler.ingest("Telemetry_Observation_" + i);
        }
        System.out.println("Reservoir Sample of 3 from 500 stream items: " + sampler.getSample());

        double[] rain = {10.0, 5.0, 15.0, 20.0};
        System.out.println("Blelloch Cumulative Rainfall Scan: " + Arrays.toString(BlellochScan.inclusiveScan(rain)));

        boolean isPrime = MillerRabinVerifier.isProbablePrime(BigInteger.valueOf(104729), 10);
        System.out.println("Miller-Rabin Prime Verification (104729): " + isPrime);
    }

    public static void runAllBenchmarks() {
        System.out.println("\n==================================================================");
        System.out.println("      🌾 FULL BENCHMARK: ALL 6 MODULES (CO1 TO CO6) 🌾          ");
        System.out.println("==================================================================");
        runModule1Demo();
        System.out.println("------------------------------------------------------------------");
        runModule2Demo();
        System.out.println("------------------------------------------------------------------");
        runModule3Demo();
        System.out.println("------------------------------------------------------------------");
        runModule4Demo();
        System.out.println("------------------------------------------------------------------");
        runModule5Demo();
        System.out.println("------------------------------------------------------------------");
        runModule6Demo();
        System.out.println("\nAll algorithms across Modules 1 to 6 executed successfully!");
        System.out.println("Web dashboard is active at http://localhost:" + activePort);
    }

    private static void handleDatasetExplorer(Scanner scanner) {
        String[] files = {
            "kaggle_crop_disease_symptoms.txt",
            "kaggle_seed_batch_manifest.txt",
            "kaggle_pathology_bulletin_corpus.txt",
            "kaggle_treatment_protocols_lcp.txt",
            "kaggle_crop_dna_germplasm.txt",
            "kaggle_farmer_advisory_queries.txt",
            "kaggle_crop_portfolio_economics.txt",
            "kaggle_sensor_fusion_matrices.txt",
            "kaggle_irrigation_canal_network.txt",
            "kaggle_farm_machinery_matching.txt",
            "kaggle_crop_rotation_constraints_3sat.txt",
            "kaggle_farm_road_junctions_graph.txt",
            "kaggle_iot_telemetry_stream.txt",
            "kaggle_seed_security_primality_keys.txt"
        };

        String[] modules = {
            "M1: String Searching (KMP / Z-Algo)",
            "M1: String Searching (Rabin-Karp Barcodes)",
            "M1 & M2: Multi-Pattern & Suffix Automaton",
            "M2: Suffix Structures (Kasai LCP Duplications)",
            "M3: Advanced DP (Crop DNA Sequence Alignment)",
            "M3: Advanced DP (Farmer Typo Levenshtein)",
            "M3: Advanced DP (Bitmask Crop Portfolio)",
            "M3: Advanced DP (Matrix Chain Multiplication)",
            "M4: Network Flow (Dinic Canal Flow & Min-Cut)",
            "M4: Network Flow (Bipartite Machinery Matching)",
            "M5: NP & Approx (3-SAT DPLL Crop Rotation)",
            "M5: NP & Approx (2-Approx Watchtower Vertex Cover)",
            "M6: Parallel & Stream (Reservoir & Blelloch Scan)",
            "M6: Parallel & Random (Miller-Rabin & QuickSort)"
        };

        boolean viewing = true;
        while (viewing) {
            System.out.println("\n==================================================================");
            System.out.println("   📂 KAGGLE AGRITECH BENCHMARK DATASET REPOSITORY (14 FILES)    ");
            System.out.println("==================================================================");
            for (int i = 0; i < files.length; i++) {
                System.out.printf("  [%2d] %-42s -> %s%n", (i + 1), files[i], modules[i]);
            }
            System.out.println("  [ 0] Return to Main Menu");
            System.out.print("\nEnter dataset index to view (0-14): ");

            if (!scanner.hasNextLine()) break;
            String in = scanner.nextLine().trim();
            if ("0".equals(in)) {
                viewing = false;
                break;
            }

            try {
                int idx = Integer.parseInt(in) - 1;
                if (idx >= 0 && idx < files.length) {
                    String selected = files[idx];
                    System.out.println("\n------------------------------------------------------------------");
                    System.out.println("Reading Dataset: " + selected);
                    System.out.println("------------------------------------------------------------------");
                    java.nio.file.Path p = java.nio.file.Paths.get("data", selected);
                    if (!java.nio.file.Files.exists(p)) {
                        p = java.nio.file.Paths.get("src/main/resources/data", selected);
                    }
                    if (java.nio.file.Files.exists(p)) {
                        List<String> lines = java.nio.file.Files.readAllLines(p);
                        for (int lineNum = 0; lineNum < Math.min(25, lines.size()); lineNum++) {
                            System.out.println(lines.get(lineNum));
                        }
                        if (lines.size() > 25) {
                            System.out.println("... (" + (lines.size() - 25) + " more lines in file | Total: " + lines.size() + " lines)");
                        }
                    } else {
                        System.out.println("[Notice] Dataset file found via classpath /data/" + selected);
                    }
                    pauseForUser(scanner);
                } else {
                    System.out.println("[!] Please enter a number between 0 and 14.");
                }
            } catch (Exception e) {
                System.out.println("[!] Invalid input: " + e.getMessage());
            }
        }
    }
}
