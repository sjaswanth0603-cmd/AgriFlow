# 🌾 AgriFlow – Smart Farm Management & Resource Optimization Platform
### Advanced Data Structures & Algorithms (Full Platform &bull; Modules 1 to 6 &bull; CO1 to CO6)

**AgriFlow** is an end-to-end Smart Agriculture Platform designed for an Advanced Data Structures & Algorithms (DSA) project. It bridges foundational and advanced computer science algorithms with pressing modern agricultural challenges: crop disease pattern recognition, agronomic research indexing, genomic trait alignment, multi-crop portfolio planning, irrigation canal flow optimization, companion planting NP-satisfiability, farm perimeter surveillance approximation, unbounded IoT telemetry streaming, and work-efficient parallel telemetry analytics.

This release covers the complete syllabus across **Modules 1 through 6 (Course Outcomes CO1 to CO6)**, functioning seamlessly in **both the Interactive Command Line Interface (CLI) and the Modern Web Page Dashboard**.

---

## 📋 Comprehensive Syllabus & Algorithm Mapping (Modules 1 to 6)

| Module & Outcome | Algorithm | Agricultural Problem Solved | Time Complexity | Space Complexity |
| :--- | :--- | :--- | :--- | :--- |
| **M1: String Algorithms (CO1)** | **Knuth-Morris-Pratt (KMP)** | Scans disease symptoms and varieties in field logs without pointer backtracking | $O(N + M)$ | $O(M)$ |
| | **Z-Algorithm** | Identifies recurring disease phrases and periodic sensor patterns in text logs | $O(N + M)$ | $O(N + M)$ |
| | **Rabin-Karp Rolling Hash** | Fast verification of seed lot barcodes and chemical batch security serials | Average $O(N + M)$ | $O(1)$ |
| | **Aho-Corasick Automaton** | Simultaneous multi-pathogen screening across lengthy agronomy bulletins in one pass | $O(N + \sum M + Z)$ | $O(\sum M \cdot \Sigma)$ |
| **M2: Suffix Structures (CO2)** | **Suffix Array (Prefix Doubling)** | Compact lexical index of agricultural research documents for logarithmic pattern search | $O(N \log^2 N)$ | $O(N)$ |
| | **Kasai's LCP Array** | Detects duplicated agricultural treatment protocols and shared recommendations in linear time | $O(N)$ | $O(N)$ |
| | **Suffix Automaton (DAWG)** | Instantaneous $O(M)$ pattern existence queries and distinct substring counting | $O(N)$ build, $O(M)$ query | $O(N)$ states |
| **M3: Advanced DP (CO3)** | **Levenshtein Distance** | Auto-corrects misspelled farmer voice and SMS text queries (e.g. `"fertlzr"` $\rightarrow$ `"fertilizer"`) | $O(N \cdot M)$ | $O(N \cdot M)$ |
| | **Needleman-Wunsch Aligner** | Global crop DNA sequence alignment (e.g. wild ancestral wheat vs drought-resistant hybrid) | $O(N \cdot M)$ | $O(N \cdot M)$ |
| | **Smith-Waterman Aligner** | Local sequence alignment to isolate conserved disease-resistance genetic motifs | $O(N \cdot M)$ | $O(N \cdot M)$ |
| | **Bitmask DP Optimizer** | Max-profit multi-crop portfolio selection under land acreage, water budget, and crop synergies | $O(2^N \cdot N)$ | $O(2^N)$ |
| | **Matrix Chain Multiplication** | Minimizes scalar multiplications for chaining drone LIDAR, satellite NDVI, and field sensor matrices | $O(K^3)$ | $O(K^2)$ |
| **M4: Network Flow (CO4)** | **Dinic's Algorithm** | Multi-source irrigation canal flow optimization via Level Graphs and Blocking Flows | $O(V^2 E)$ | $O(V + E)$ |
| | **Edmonds-Karp Algorithm** | Augmenting paths BFS for canal water routing baseline verification | $O(V E^2)$ | $O(V + E)$ |
| | **Min-Cut Bottlenecks** | Isolates critical choke-points in canal network to prioritize infrastructure upgrades | $O(V^2 E)$ | $O(V)$ |
| | **Bipartite Matching** | Fair and optimal allocation of communal harvesters, sprayers, and tractors across farms | $O(E \sqrt{V})$ | $O(V + E)$ |
| **M5: NP & Approx (CO5)** | **3-SAT DPLL Solver** | Crop rotation and companion planting satisfaction under soil and biological constraints | $O(2^N)$ worst-case | $O(N)$ |
| | **Reduction to CLIQUE** | Proves NP-hardness by transforming 3-CNF crop constraints into polynomial CLIQUE gadgets | $O(K^2)$ reduction | $O(K^2)$ |
| | **2-Approx Vertex Cover** | Guaranteed 2-approximation placement of farm watchtowers and drone stations on road junctions | $O(V + E)$ | $O(V + E)$ |
| **M6: Parallel & Random (CO6)**| **Reservoir Sampling (Alg R)** | Uniform random sampling from unbounded continuous streams of IoT field sensor telemetry | $O(N)$ time | $O(K)$ space |
| | **Blelloch Parallel Scan** | Work-efficient tree-based prefix scan computing cumulative daily rainfall and soil moisture runoff | $O(N)$ work, $O(\log N)$ depth | $O(N)$ |
| | **Brent's Scheduling Theorem** | Theoretical scaling limits and optimal multi-core processor allocation for drone imagery stitching | $O(1)$ evaluation | $O(1)$ |
| | **Miller-Rabin Primality** | Monte Carlo randomized test for cryptographic seed batch certificates and secure IoT telemetry keys | $O(k \log^3 N)$ | $O(\log N)$ |
| | **Randomized QuickSort** | Pivot randomization preventing worst-case quadratic sorting for agricultural harvest yield leagues | Expected $O(N \log N)$ | $O(\log N)$ |
| | **Fork-Join Parallel Reduce** | Recursive divide-and-conquer aggregation (min, max, mean, sum) across 50,000+ IoT sensors in parallel | $O(N)$ work, $O(\log N)$ span | $O(\log N)$ |

---

## 🚀 How to Run the Project (Dual-Mode: CLI & Web)

The application starts an embedded HTTP Web Server in the background and launches an interactive CLI menu in the foreground. Both modes work concurrently!

### Option 1: One-Click Windows Runner
Double-click `run.bat` or run in PowerShell / Command Prompt:
```bat
run.bat
```
This automatically compiles all 6 module packages, synchronizes web assets, starts the embedded server on **`http://localhost:8080`** (or next available port), and opens the interactive CLI menu.

---

### Option 2: Standard Command Line (`javac` & `java`)
1. **Compile all modules (M1 to M6 + Server + Main)**:
```bash
javac -encoding UTF-8 -d bin src\main\java\com\agriflow\m1_strings\*.java src\main\java\com\agriflow\m2_suffix\*.java src\main\java\com\agriflow\m3_dp\*.java src\main\java\com\agriflow\m4_flow\*.java src\main\java\com\agriflow\m5_np\*.java src\main\java\com\agriflow\m6_parallel\*.java src\main\java\com\agriflow\server\*.java src\main\java\com\agriflow\Main.java
```

2. **Synchronize Web Assets**:
```bash
xcopy /E /I /Y src\main\resources\web bin\web
```

3. **Run Application**:
```bash
java -cp bin com.agriflow.Main
```

4. **Access Web Dashboard**: Open your browser at **`http://localhost:8080`** (or the port indicated on startup).

---

### Option 3: Using Apache Maven
```bash
mvn compile exec:java
```

---

## 💻 1. Interactive Command Line Interface (CLI) Guide

When launched in the terminal, AgriFlow displays the interactive main menu:
```text
==================================================================
      🌾 AgriFlow - Smart Farm Management & Optimization 🌾      
             Full Platform: Modules 1 to 6 (CO1 to CO6)          
==================================================================
  [Active Web Server: http://localhost:8080]
------------------------------------------------------------------
Select an option to test in the Interactive Console:
  [1] Module 1: String Searching (KMP, Z-Algorithm, Rabin-Karp, Aho-Corasick)
  [2] Module 2: Suffix Structures (Suffix Array, Kasai LCP, Suffix Automaton)
  [3] Module 3: Advanced Dynamic Programming (Levenshtein, NW DNA, SW Motif, Bitmask DP, MCM)
  [4] Module 4: Network Flow (Irrigation Flow, Dinic, Min-Cut, Bipartite Matching)
  [5] Module 5: NP-Completeness & Approximation (3-SAT DPLL, Reductions, 2-Approx Vertex Cover)
  [6] Module 6: Randomized & Parallel (Reservoir Stream, Blelloch Scan, Brent, Miller-Rabin, QuickSort)
  [7] Run Full Automated Benchmark across ALL 6 Modules (M1 to M6)
  [8] Open Web Dashboard in Browser (http://localhost:8080)
  [0] Exit Application

Enter your choice (0-8): 
```

### Submenu Walkthrough:
- **`[1]` Module 1: String Searching (CO1)**: Test KMP, Z-Algorithm, Rabin-Karp, and Aho-Corasick; run 4-algorithm side-by-side benchmark.
- **`[2]` Module 2: Suffix Structures (CO2)**: Suffix Array table inspection, Kasai LCP longest duplicated protocol detection, and $O(M)$ Suffix Automaton queries.
- **`[3]` Module 3: Advanced DP (CO3)**: Farmer typo autocorrection (Levenshtein), Needleman-Wunsch global crop DNA alignment, Smith-Waterman local motif discovery, Bitmask DP crop portfolio optimizer, and Matrix Chain Multiplication (MCM) pipeline.
- **`[4]` Module 4: Network Flow & Resource Matching (CO4)**: Dinic's Algorithm vs Edmonds-Karp for maximum canal flow, S-T Min-Cut bottleneck isolation, and maximum bipartite machinery matching.
- **`[5]` Module 5: NP-Completeness & Approximation (CO5)**: Crop rotation 3-SAT DPLL solver, Karp polynomial reduction to CLIQUE gadgets, and 2-approximation Vertex Cover watchtower placement.
- **`[6]` Module 6: Randomized & Parallel Algorithms (CO6)**: Unbounded stream reservoir sampling (Algorithm R), Blelloch work-efficient parallel prefix scan, Brent's Theorem multi-core scaling, Miller-Rabin cryptographic primality test, fork-join parallel sensor reduce (50,000 probes), and Randomized QuickSort yield league ranking.
- **`[7]` Automated Test Suite**: Automatically benchmarks and logs verification results for all 6 modules in a single pass.
- **`[8]` Open Browser**: Launches the web dashboard in the default browser.

---

## 🌐 2. Web Dashboard Guide

The web dashboard is available at **`http://localhost:8080`**:

1. **🌾 Syllabus Overview**: Complete course outcome mapping, algorithm descriptions, time/space complexity analysis, and quick test suite runner.
2. **🔍 Module 1: String Searching (CO1)**:
   - Field diagnosis text box with presets (*Foliage Blight*, *Batch Barcodes*, *Multi-Pathogen*).
   - Live highlighted text display highlighting occurrences.
   - 4-way side-by-side benchmark.
3. **📑 Module 2: Suffix Structures (CO2)**:
   - Longest common repeated recommendation extractor via Kasai's LCP.
   - Lexicographically sorted suffixes preview table.
   - Interactive Suffix Automaton $O(M)$ existence query box.
4. **🧬 Module 3: Advanced Dynamic Programming (CO3)**:
   - DNA Sequence Alignment viewer with Needleman-Wunsch & Smith-Waterman visual match bar.
   - Bitmask DP multi-crop portfolio planner with land and water limits.
   - Farmer query typo autocorrection with clickable quick-test chips (`fertlzr`, `tmtos`, `blite`, `potasium`, `irigation`).
   - Matrix Chain Multiplication (MCM) pipeline optimizer.
5. **🌊 Module 4: Irrigation Flow & Matching (CO4)**:
   - Canal network maximum flow calculator comparing Dinic vs Edmonds-Karp.
   - S-T Minimum Cut table displaying critical saturated bottleneck canals.
   - Bipartite machinery allocation matching requesting farms with heavy equipment.
6. **⚡ Module 5: NP-Completeness & Approximation (CO5)**:
   - 3-SAT DPLL crop rotation satisfiability with boolean truth assignments.
   - Polynomial reduction to CLIQUE graph gadget information.
   - 2-Approximation Vertex Cover displaying watchtower coordinates and guaranteed bound.
7. **🚀 Module 6: Parallel & Streaming Telemetry (CO6)**:
   - Unbounded reservoir sampling on live sensor stream ($k$ items in $O(k)$ memory).
   - Blelloch parallel prefix scan for cumulative rainfall and runoff.
   - Brent's Theorem multi-core processor scheduling evaluation ($T_p \le (T_1 - T_\infty)/p + T_\infty$).
   - Miller-Rabin Monte Carlo primality verifier with error probability $\le 4^{-10}$.
   - Fork-Join 50,000 sensor telemetry parallel reduction and Randomized QuickSort harvest yield rankings.

---

## 📂 Project Structure

```text
AgriFlow/
├── pom.xml                               # Maven project configuration
├── run.bat                               # One-click Windows runner (Modules 1 to 6)
├── README.md                             # Comprehensive documentation & manual
├── bin/                                  # Compiled class files & web assets
└── src/main/
    ├── java/com/agriflow/
    │   ├── Main.java                     # Main entry point & interactive looped CLI runner
    │   ├── m1_strings/                   # Module 1 (CO1): String Algorithms
    │   │   ├── KmpMatcher.java
    │   │   ├── ZAlgorithm.java
    │   │   ├── RabinKarpMatcher.java
    │   │   └── AhoCorasickDiseaseDetector.java
    │   ├── m2_suffix/                    # Module 2 (CO2): Suffix Structures
    │   │   ├── SuffixArrayBuilder.java
    │   │   ├── KasaiLcp.java
    │   │   └── SuffixAutomaton.java
    │   ├── m3_dp/                        # Module 3 (CO3): Advanced Dynamic Programming
    │   │   ├── LevenshteinDistance.java
    │   │   ├── NeedlemanWunschAligner.java
    │   │   ├── SmithWatermanAligner.java
    │   │   ├── BitmaskCropPlanner.java
    │   │   └── MatrixChainOptimizer.java
    │   ├── m4_flow/                      # Module 4 (CO4): Network Flow & Matching
    │   │   ├── FlowNetwork.java
    │   │   ├── DinicMaxFlow.java
    │   │   ├── EdmondsKarp.java
    │   │   ├── MinCutBottleneckFinder.java
    │   │   └── BipartiteResourceMatcher.java
    │   ├── m5_np/                        # Module 5 (CO5): NP-Completeness & Approximation
    │   │   ├── CropConstraintSatSolver.java
    │   │   ├── ReductionGraphMapper.java
    │   │   └── VertexCoverApproximation.java
    │   ├── m6_parallel/                  # Module 6 (CO6): Randomized & Parallel Algorithms
    │   │   ├── ReservoirSampler.java
    │   │   ├── BlellochScan.java
    │   │   ├── BrentsTheoremAnalyzer.java
    │   │   ├── MillerRabinVerifier.java
    │   │   ├── RandomizedQuickSort.java
    │   │   └── ParallelSensorReduce.java
    │   └── server/                       # Embedded HTTP Server & REST API
    │       ├── AgriFlowServer.java
    │       └── ApiDispatcher.java
    └── resources/web/                    # Web Dashboard Assets
        ├── index.html                    # Dashboard UI with tabs for M1 through M6
        ├── app.js                        # Controller logic & API integration for M1 through M6
        └── style.css                     # Modern agricultural theme
```

---

## 🛠️ Zero External Dependencies

AgriFlow is engineered in pure standard **Java 21+** utilizing:
- Standard `java.util`, `java.math.BigInteger`, `java.util.concurrent.ForkJoinPool`
- Native `com.sun.net.httpserver.HttpServer` (zero external frameworks, no Tomcat, no Spring, no external JARs required)
- Vanilla HTML5 / Modern CSS / Vanilla ES6 JavaScript (zero Node.js or npm dependencies required)
