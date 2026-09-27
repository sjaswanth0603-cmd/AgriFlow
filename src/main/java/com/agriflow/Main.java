package com.agriflow;

import com.agriflow.server.AgriFlowServer;
import com.agriflow.m1_strings.*;
import com.agriflow.m2_suffix.*;
import com.agriflow.m3_dp.*;

import java.util.*;

/**
 * AgriFlow Main Entry Point (Modules 1 to 3: CO1, CO2, CO3)
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
            System.out.println("  [4] Run Full Automated Benchmark across Modules 1 to 3");
            System.out.println("  [5] Open Web Dashboard in Browser (http://localhost:" + activePort + ")");
            System.out.println("  [0] Exit Application");
            System.out.print("\nEnter your choice (0-5): ");

            if (!scanner.hasNextLine()) break;
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> handleModule1Menu(scanner);
                case "2" -> handleModule2Menu(scanner);
                case "3" -> handleModule3Menu(scanner);
                case "4" -> runAllBenchmarks();
                case "5" -> {
                    System.out.println("\nOpening http://localhost:" + activePort + " in browser...");
                    AgriFlowServer.openBrowser(activePort);
                    pauseForUser(scanner);
                }
                case "0" -> {
                    System.out.println("\nShutting down AgriFlow. Goodbye! 🌾");
                    running = false;
                }
                default -> {
                    System.out.println("\n[!] Invalid choice. Please select 0 to 5.");
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
        System.out.println("           Modules 1 to 3 (CO1, CO2, CO3) - Advanced DSA         ");
        System.out.println("==================================================================");
        System.out.println("  [Active Web Server: http://localhost:" + activePort + "]");
        System.out.println("------------------------------------------------------------------");
    }

    private static void pauseForUser(Scanner scanner) {
        System.out.print("\nPress Enter to return to menu...");
        scanner.nextLine();
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

        // 1. KMP
        long t0 = System.nanoTime();
        var kmpRes = KmpMatcher.search(text, pattern);
        long tKmp = System.nanoTime() - t0;

        // 2. Z
        t0 = System.nanoTime();
        var zRes = ZAlgorithm.search(text, pattern);
        long tZ = System.nanoTime() - t0;

        // 3. Rabin-Karp
        t0 = System.nanoTime();
        var rkRes = RabinKarpMatcher.search(text, pattern);
        long tRk = System.nanoTime() - t0;

        // 4. Aho-Corasick
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

        // Query Suffix Automaton
        long t0 = System.nanoTime();
        boolean exists = sam.contains(pattern);
        long tSam = System.nanoTime() - t0;

        // Query Suffix Array Binary Search
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
            System.out.print("Select sub-option (0-5): ");

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
    // DEMO RUNNERS & BENCHMARKS
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

    public static void runAllBenchmarks() {
        System.out.println("\n==================================================================");
        System.out.println("      🌾 FULL BENCHMARK: MODULES 1 TO 3 (CO1, CO2, CO3) 🌾       ");
        System.out.println("==================================================================");
        runModule1Demo();
        System.out.println("------------------------------------------------------------------");
        runModule2Demo();
        System.out.println("------------------------------------------------------------------");
        runModule3Demo();
        System.out.println("\nAll algorithms in Modules 1, 2, and 3 executed successfully!");
        System.out.println("Web dashboard is active at http://localhost:" + activePort);
    }
}
