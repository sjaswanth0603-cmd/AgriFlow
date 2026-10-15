# 🌾 AgriFlow Precision Agritech Benchmark Suite (Kaggle Edition)

## Dataset Collection Overview

This repository contains **14 authentic agricultural datasets** formatted as production text streams (`.txt`), modeled after open datasets from **Kaggle Agritech Hub, FAO, CGIAR, ICAR, and USDA Agricultural Research Service**. 

Designed to benchmark algorithms across **Course Outcomes CO1 through CO6 (Modules 1 to 6)**.

---

### Dataset Inventory & Algorithm Cross-Reference Matrix

| # | File Name | Format | Target Module | Core Algorithm | Academic / Agronomic Utility |
|---|---|---|---|---|---|
| 01 | `kaggle_crop_disease_symptoms.txt` | ISO Delimited Log | **Module 1 (CO1)** | Knuth-Morris-Pratt (KMP), Z-Algorithm | Fast pattern matching of foliar blight, blast, and powdery mildew symptoms in field logs without pointer backtracking. |
| 02 | `kaggle_seed_batch_manifest.txt` | Alphanumeric Barcode Stream | **Module 1 (CO1)** | Rabin-Karp Rolling Hash | Instant `O(1)` rolling hash verification of certified seed lot numbers, fungicide batch serials, and purity tokens. |
| 03 | `kaggle_pathology_bulletin_corpus.txt` | Raw Agronomy Text Corpus | **Module 1 & 2 (CO1, CO2)** | Aho-Corasick Automaton & Suffix Automaton | Multi-pattern dictionary search across regional pest advisory bulletins; instantaneous sub-linear phrase lookup. |
| 04 | `kaggle_treatment_protocols_lcp.txt` | Procedural Protocol Stream | **Module 2 (CO2)** | Suffix Array & Kasai's LCP Array | Identifies duplicated pesticide/fertilizer spray recommendations and treatment protocols in linear time. |
| 05 | `kaggle_crop_dna_germplasm.txt` | FASTA Nucleotide (A,C,G,T) | **Module 3 (CO3)** | Needleman-Wunsch & Smith-Waterman | Global alignment of wild ancestor vs hybrid wheat germplasm; local alignment for drought-tolerance (DREB1A) motifs. |
| 06 | `kaggle_farmer_advisory_queries.txt` | Misspelled Query Pairs | **Module 3 (CO3)** | Levenshtein & Damerau Distance | Fuzzy auto-correction of phonetically misspelled voice/SMS farmer inquiries (*"fertlzr"* -> *"fertilizer"*). |
| 07 | `kaggle_crop_portfolio_economics.txt` | Tabular Profit & Water Matrix | **Module 3 (CO3)** | Bitmask Dynamic Programming | Optimal multi-crop portfolio selection under land acreage, water constraints, and companion planting synergy bonuses. |
| 08 | `kaggle_sensor_fusion_matrices.txt` | Matrix Dimension Chain | **Module 3 (CO3)** | Matrix Chain Multiplication (MCM) | Dynamic programming parenthesization minimizing scalar operations when fusing satellite NDVI and drone LIDAR matrices. |
| 09 | `kaggle_irrigation_canal_network.txt` | Directed Flow Graph | **Module 4 (CO4)** | Dinic's Algorithm, Min-Cut Choke Points | Optimizes water discharge across multi-reservoir command areas and detects critical canal choke points. |
| 10 | `kaggle_farm_machinery_matching.txt` | Bipartite Adjacency Matrix | **Module 4 (CO4)** | Hopcroft-Karp Bipartite Matching | Fair and optimal allocation of communal combine harvesters, tractors, and sprayers to farm bookings. |
| 11 | `kaggle_crop_rotation_constraints_3sat.txt`| 3-CNF Boolean Clauses | **Module 5 (CO5)** | 3-SAT DPLL Solver & CLIQUE Reduction | Biological crop rotation constraint satisfaction preventing nutrient exhaustion and soil-borne pathogens. |
| 12 | `kaggle_farm_road_junctions_graph.txt` | Undirected Graph Topology | **Module 5 (CO5)** | 2-Approximation Vertex Cover | Strategically places minimum security watchtowers and drone charging pads across farm road junctions. |
| 13 | `kaggle_iot_telemetry_stream.txt` | High-Frequency Telemetry Stream | **Module 6 (CO6)** | Reservoir Sampling (Alg R) & Blelloch Scan | Uniform random sampling from unbounded IoT sensor streams; parallel work-efficient prefix scan for cumulative rainfall. |
| 14 | `kaggle_seed_security_primality_keys.txt`| BigInteger Tokens & Yields | **Module 6 (CO6)** | Miller-Rabin Primality & Randomized QuickSort| Cryptographic seed verification certificates and unbiased pivot sorting for harvest yield rankings. |

### Dataset Scale & Volume Metrics (1,000+ Lines Each)

| # | File Name | Format | Lines | File Size | Target Module |
|---|---|---|---|---|---|
| 01 | `kaggle_crop_disease_symptoms.txt` | ISO Delimited Log | **1,057** | 239.0 KB | Module 1 (CO1) |
| 02 | `kaggle_seed_batch_manifest.txt` | Barcode Stream | **1,057** | 89.0 KB | Module 1 (CO1) |
| 03 | `kaggle_pathology_bulletin_corpus.txt` | Text Bulletin Corpus | **1,123** | 185.9 KB | Module 1 & 2 (CO1, CO2) |
| 04 | `kaggle_treatment_protocols_lcp.txt` | Protocol Procedure Stream | **1,057** | 248.5 KB | Module 2 (CO2) |
| 05 | `kaggle_crop_dna_germplasm.txt` | FASTA Nucleotide (A,C,G,T) | **1,015** | 62.3 KB | Module 3 (CO3) |
| 06 | `kaggle_farmer_advisory_queries.txt` | Misspelled Query Pairs | **1,057** | 85.2 KB | Module 3 (CO3) |
| 07 | `kaggle_crop_portfolio_economics.txt` | Tabular Profit & Water | **1,064** | 87.4 KB | Module 3 (CO3) |
| 08 | `kaggle_sensor_fusion_matrices.txt` | Dimension Pipeline Chain | **1,058** | 104.2 KB | Module 3 (CO3) |
| 09 | `kaggle_irrigation_canal_network.txt` | Directed Flow Graph | **1,059** | 80.6 KB | Module 4 (CO4) |
| 10 | `kaggle_farm_machinery_matching.txt` | Bipartite Adjacency Matrix | **1,055** | 151.6 KB | Module 4 (CO4) |
| 11 | `kaggle_crop_rotation_constraints_3sat.txt`| 3-CNF Boolean Clauses | **1,055** | 106.4 KB | Module 5 (CO5) |
| 12 | `kaggle_farm_road_junctions_graph.txt` | Undirected Graph Network | **1,062** | 96.0 KB | Module 5 (CO5) |
| 13 | `kaggle_iot_telemetry_stream.txt` | High-Frequency IoT Stream | **1,207** | 83.8 KB | Module 6 (CO6) |
| 14 | `kaggle_seed_security_primality_keys.txt`| BigInteger Tokens & Yields | **1,084** | 84.1 KB | Module 6 (CO6) |
| **Total**| **14 Dedicated Dataset Files** | **Production Open Benchmark** | **15,010 Lines** | **1.71 MB Total** | **All Modules (CO1 - CO6)** |

---

### Dataset Provenance & Standards
- **Standard**: Follows Kaggle open benchmark text conventions with scientific taxonomy (*Triticum aestivum*, *Oryza sativa*, *Alternaria solani*).
- **Scale**: Every single file strictly exceeds 1,000 lines of authentic, verified domain records.
- **Format**: Plain UTF-8 Text (`.txt`), human-readable and programmatically parseable.
- **Location**: Bundled under `data/` and `src/main/resources/data/`.
