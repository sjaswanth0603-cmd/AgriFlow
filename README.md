# 🌾 AgriFlow – Smart Farm Management & Resource Optimization Platform
### Advanced Data Structures & Algorithms (DSA 3 &bull; Modules 1 to 3 &bull; CO1 to CO3)

**AgriFlow** is an end-to-end Smart Agriculture Platform designed for an Advanced Data Structures & Algorithms (DSA) project. It bridges foundational computer science algorithms with pressing modern agricultural challenges: crop disease pattern recognition, agronomic research indexing, genomic trait alignment, crop portfolio planning, and geo-sensor matrix pipeline optimization.

This version is specifically configured and scoped for **Modules 1, 2, and 3 (Course Outcomes CO1, CO2, and CO3)**, providing full interactive functionality via **both the Command Line Interface (CLI) and the Modern Web Dashboard**.

---

## 📋 Syllabus & Algorithm Mapping (Modules 1 to 3)

| Module & Outcome | Algorithm | Agricultural Problem Solved | Time Complexity | Space Complexity |
| :--- | :--- | :--- | :--- | :--- |
| **M1: String Algorithms (CO1)** | **KMP (Knuth-Morris-Pratt)** | Scans disease symptoms and varieties in field logs without pointer backtracking | $O(N + M)$ | $O(M)$ |
| | **Z-Algorithm** | Identifies recurring disease phrases and periodic sensor patterns in text logs | $O(N + M)$ | $O(N + M)$ |
| | **Rabin-Karp** | Rolling hash verification of seed lot barcodes and chemical batch numbers | Average $O(N + M)$ | $O(1)$ |
| | **Aho-Corasick** | Simultaneous detection of dozens of crop pests & disease keywords in one pass | $O(N + \sum M + Z)$ | $O(\sum M \cdot \Sigma)$ |
| **M2: Suffix Structures (CO2)** | **Suffix Array (Prefix Doubling)** | High-speed lexical indexing of agronomy research papers and extension bulletins | $O(N \log^2 N)$ | $O(N)$ |
| | **Kasai's LCP** | Detects duplicated agricultural treatment protocols and research sections | $O(N)$ | $O(N)$ |
| | **Suffix Automaton (DAWG)** | Instantaneous $O(M)$ substring queries and distinct sensor pattern counting | $O(N)$ build, $O(M)$ query | $O(N)$ |
| **M3: Advanced DP (CO3)** | **Levenshtein Distance** | Auto-corrects misspelled farmer voice and text queries (e.g. `"fertlzr"` $\rightarrow$ `"fertilizer"`) | $O(N \cdot M)$ | $O(N \cdot M)$ |
| | **Needleman-Wunsch** | Global crop DNA sequence alignment (e.g., wild wheat vs drought-resistant hybrid) | $O(N \cdot M)$ | $O(N \cdot M)$ |
| | **Smith-Waterman** | Local alignment to identify conserved disease-resistance genetic motifs | $O(N \cdot M)$ | $O(N \cdot M)$ |
| | **Bitmask DP** | Max-profit crop portfolio selection under land acreage and water budget limits | $O(2^N \cdot N)$ | $O(2^N)$ |
| | **Matrix Chain Multiplication** | Optimizes satellite NDVI and drone elevation transformation matrix pipelines | $O(K^3)$ | $O(K^2)$ |

---

## 🚀 How to Run the Project (Dual-Mode: CLI & Web)

The application starts an embedded HTTP Web Server in the background and launches an interactive CLI menu in the foreground. Both modes work concurrently!

### Option 1: One-Click Runner (Windows)
Double-click `run.bat` or run in PowerShell / Command Prompt:
```bat
run.bat
```
This automatically compiles the codebase, synchronizes web assets, starts the server on **`http://localhost:8080`**, and enters the interactive CLI loop.

---

### Option 2: Standard Command Line (`javac` & `java`)
1. **Compile**:
```bash
javac -encoding UTF-8 -d bin src/main/java/com/agriflow/m1_strings/*.java src/main/java/com/agriflow/m2_suffix/*.java src/main/java/com/agriflow/m3_dp/*.java src/main/java/com/agriflow/server/*.java src/main/java/com/agriflow/Main.java
```

2. **Copy Web Assets**:
```bash
# Windows
xcopy /E /I /Y src\main\resources\web bin\web
```

3. **Run**:
```bash
java -cp bin com.agriflow.Main
```

4. **Access Web Dashboard**: Open your browser at **`http://localhost:8080`**.

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
           Modules 1 to 3 (CO1, CO2, CO3) - Advanced DSA         
==================================================================
  [Active Web Server: http://localhost:8080]
------------------------------------------------------------------
Select an option to test in the Interactive Console:
  [1] Module 1: String Searching (KMP, Z-Algorithm, Rabin-Karp, Aho-Corasick)
  [2] Module 2: Suffix Structures (Suffix Array, Kasai LCP, Suffix Automaton)
  [3] Module 3: Advanced Dynamic Programming (Levenshtein, NW DNA, SW Motif, Bitmask DP, MCM)
  [4] Run Full Automated Benchmark across Modules 1 to 3
  [5] Open Web Dashboard in Browser (http://localhost:8080)
  [0] Exit Application

Enter your choice (0-5): 
```

### CLI Features:
- **Module 1**:
  - Test KMP, Z-Algorithm, or Rabin-Karp with custom text and pattern or agronomic presets.
  - Test Aho-Corasick multi-pathogen scanner with custom or preset keyword dictionaries.
  - Side-by-side benchmark comparing match count and execution times in nanoseconds.
- **Module 2**:
  - View Suffix Array with rank and preview table.
  - Compute Kasai's LCP array to isolate duplicate treatment protocols.
  - Query Suffix Automaton: type any substring to check existence in instant $O(M)$ time.
  - Count total distinct substrings in the document.
- **Module 3**:
  - Test typo correction for farmer queries (Levenshtein & Damerau).
  - Global crop DNA sequence alignment (Needleman-Wunsch) with visual match bar (`|`, `.`).
  - Local crop DNA motif alignment (Smith-Waterman) to find conserved resistance markers.
  - Bitmask DP crop planning with custom land and water limits.
  - Matrix Chain Multiplication (MCM) pipeline optimizer.
- **Option 4**: Run automated benchmark of all algorithms across Modules 1 to 3 in sequence.

---

## 🌐 2. Web Dashboard Guide

The web dashboard is available at **`http://localhost:8080`**:

1. **🌾 Syllabus Overview**: Complete course outcome mapping, algorithm descriptions, time/space complexity analysis, and quick test suite runner.
2. **🔍 Module 1: String Searching**:
   - Presets: *Foliage Blight Outbreak*, *Chemical & Seed Batch Codes*, *Multi-Pathogen Screening*.
   - Live highlighted document view with `<mark>` tags around matched keywords.
   - Detailed occurrence table with context snippets and nanosecond timings.
   - Side-by-side 4-algorithm benchmark mode.
3. **📑 Module 2: Suffix Structures**:
   - Longest common repeated recommendation extractor via Kasai's LCP.
   - Lexicographically sorted suffixes preview table.
   - Interactive Suffix Automaton $O(M)$ search box.
4. **🧬 Module 3: Advanced Dynamic Programming**:
   - DNA Sequence Alignment viewer with Needleman-Wunsch & Smith-Waterman.
   - Bitmask DP multi-crop portfolio planner with dynamic land and water sliders.
   - Farmer query typo autocorrection with clickable quick-test chips (`fertlzr`, `tmtos`, `blite`, `potasium`, `irigation`).
   - Matrix Chain Multiplication (MCM) pipeline parenthesization optimizer.

---

## 📂 Project Structure

```text
AgriFlow/
├── pom.xml                               # Maven project configuration
├── run.bat                               # One-click Windows runner
├── README.md                             # Documentation & manual
├── bin/                                  # Compiled class files & web assets
└── src/main/
    ├── java/com/agriflow/
    │   ├── Main.java                     # Main entry point & interactive CLI runner
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
    │   └── server/                       # Embedded HTTP Server & REST API
    │       ├── AgriFlowServer.java
    │       └── ApiDispatcher.java
    └── resources/web/                    # Web Dashboard Assets
        ├── index.html                    # Dashboard UI
        ├── app.js                        # Controller logic & API integration
        └── style.css                     # Modern agricultural theme
```
