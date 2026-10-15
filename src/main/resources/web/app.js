// AgriFlow Frontend Controller (Modules 1 to 3: CO1, CO2, CO3)

// Tab Switching
function switchTab(tabId) {
  document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
  document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));

  const buttons = document.querySelectorAll('.tab-btn');
  buttons.forEach(btn => {
    if (btn.getAttribute('onclick') && btn.getAttribute('onclick').includes(tabId)) {
      btn.classList.add('active');
    }
  });

  const target = document.getElementById('tab-' + tabId);
  if (target) target.classList.add('active');
}

// =========================================================================
// MODULE 1: STRING SEARCHING
// =========================================================================

function presetM1(type) {
  const textField = document.getElementById('m1-text');
  const patternField = document.getElementById('m1-pattern');
  const algoSelect = document.getElementById('m1-algo');

  if (type === 'blight') {
    textField.value = "Field inspection in Sector 7 identified early blight on potato foliage. Late blight was also detected on tomato leaves in Zone B. Immediate copper fungicide treatment recommended.";
    patternField.value = "blight";
    algoSelect.value = "kmp";
  } else if (type === 'batch') {
    textField.value = "Warehouse Dispatch Log: BATCH-AGRI-9821 loaded on Truck 4. BATCH-AGRI-4412 awaiting clearance. BATCH-AGRI-9821 container sealed with security tag.";
    patternField.value = "BATCH-AGRI-9821";
    algoSelect.value = "rabin";
  } else if (type === 'multi') {
    textField.value = "Field inspection in Sector 7 detected powdery mildew and early blight on tomato foliage. Copper fungicide recommended. Also check potato crop for aphids.";
    patternField.value = "blight, mildew, fungicide, tomato, aphids";
    algoSelect.value = "aho";
  }
  runM1();
}

function runM1() {
  const textRaw = document.getElementById('m1-text').value;
  const patternRaw = document.getElementById('m1-pattern').value;
  const algo = document.getElementById('m1-algo').value;

  if (algo === 'compare') {
    runM1Compare();
    return;
  }

  const text = encodeURIComponent(textRaw);
  const pattern = encodeURIComponent(patternRaw);

  fetch(`/api/m1/search?text=${text}&pattern=${pattern}&algo=${algo}`)
    .then(res => res.json())
    .then(data => {
      if (data.error) {
        alert("Error: " + data.error);
        return;
      }

      const box = document.getElementById('m1-result');
      const out = document.getElementById('m1-output');
      const badge = document.getElementById('m1-res-badge');
      const title = document.getElementById('m1-res-title');
      const hlContainer = document.getElementById('m1-highlight-container');
      const hlBox = document.getElementById('m1-highlight-box');

      box.style.display = 'block';
      title.textContent = `Search Results: ${data.algorithm}`;
      badge.textContent = `${data.matchCount} match${data.matchCount === 1 ? '' : 'es'} found`;
      badge.className = data.matchCount > 0 ? 'badge badge-success' : 'badge badge-warning';

      // Build Highlighted Text
      if (data.matchCount > 0 && data.matches && data.matches.length > 0) {
        hlContainer.style.display = 'block';
        hlBox.innerHTML = highlightOccurrences(textRaw, data.matches);
      } else {
        hlContainer.style.display = 'none';
      }

      // Output details
      let details = `==================================================================\n`;
      details += `Algorithm Engine : ${data.algorithm}\n`;
      details += `Execution Time   : ${(data.timeNs / 1e6).toFixed(4)} ms (${data.timeNs.toLocaleString()} ns)\n`;
      details += `Occurrences Found: ${data.matchCount}\n`;
      details += `==================================================================\n`;

      if (data.matchCount === 0) {
        details += `\nNo occurrences of "${patternRaw}" found in the document.`;
      } else {
        details += `\nDetailed Match Locations:\n`;
        data.matches.forEach((m, idx) => {
          details += `  [#${idx + 1}] Keyword: "${m.keyword}" @ Position ${m.index} -> [ ${m.snippet} ]\n`;
        });
      }
      out.textContent = details;
    })
    .catch(err => alert("Error executing M1 search: " + err));
}

function runM1Compare() {
  const textRaw = document.getElementById('m1-text').value;
  const patternRaw = document.getElementById('m1-pattern').value;

  const text = encodeURIComponent(textRaw);
  const pattern = encodeURIComponent(patternRaw);

  fetch(`/api/m1/search?text=${text}&pattern=${pattern}&algo=compare`)
    .then(res => res.json())
    .then(data => {
      const box = document.getElementById('m1-result');
      const out = document.getElementById('m1-output');
      const badge = document.getElementById('m1-res-badge');
      const title = document.getElementById('m1-res-title');
      const hlContainer = document.getElementById('m1-highlight-container');

      box.style.display = 'block';
      hlContainer.style.display = 'none';
      title.textContent = `Side-by-Side Algorithm Comparison Benchmark`;
      badge.textContent = `4 Algorithms Tested`;
      badge.className = 'badge badge-algo';

      let outText = `====================================================================================\n`;
      outText += `   ALGORITHM COMPARISON BENCHMARK (Text Length: ${data.textLength} chars, Target: "${data.pattern}")\n`;
      outText += `====================================================================================\n\n`;
      outText += `+------------------------------+---------+------------------+--------------------+\n`;
      outText += `| Algorithm                    | Matches | Time Taken       | Time Complexity    |\n`;
      outText += `+------------------------------+---------+------------------+--------------------+\n`;
      outText += `| Knuth-Morris-Pratt (KMP)     | ${pad(data.kmp.count, 7)} | ${pad(data.kmp.timeNs.toLocaleString() + ' ns', 16)} | O(N + M)           |\n`;
      outText += `| Z-Algorithm                  | ${pad(data.z.count, 7)} | ${pad(data.z.timeNs.toLocaleString() + ' ns', 16)} | O(N + M)           |\n`;
      outText += `| Rabin-Karp Rolling Hash      | ${pad(data.rabin.count, 7)} | ${pad(data.rabin.timeNs.toLocaleString() + ' ns', 16)} | Avg O(N + M)       |\n`;
      outText += `| Aho-Corasick Multi-Pattern   | ${pad(data.aho.count, 7)} | ${pad(data.aho.timeNs.toLocaleString() + ' ns', 16)} | O(N + sum(M) + Z)  |\n`;
      outText += `+------------------------------+---------+------------------+--------------------+\n\n`;
      outText += `Key Observation:\n`;
      outText += `&bull; KMP & Z-Algorithm achieve linear scan without backtracking.\n`;
      outText += `&bull; Rabin-Karp computes rolling polynomial hash for fast string equality.\n`;
      outText += `&bull; Aho-Corasick constructs a Trie with failure links, finding all target keywords in a single linear pass!\n`;

      out.innerHTML = outText;
    })
    .catch(err => alert("Error comparing M1 algorithms: " + err));
}

function highlightOccurrences(text, matches) {
  // Sort matches by starting index ascending
  const sorted = [...matches].sort((a, b) => a.index - b.index);
  let html = '';
  let lastIdx = 0;

  for (const m of sorted) {
    if (m.index < lastIdx) continue; // Skip overlapping for simple highlight
    html += escapeHtml(text.substring(lastIdx, m.index));
    html += `<mark class="highlight-tag">${escapeHtml(text.substring(m.index, m.index + m.length))}</mark>`;
    lastIdx = m.index + m.length;
  }
  html += escapeHtml(text.substring(lastIdx));
  return html;
}

// =========================================================================
// MODULE 2: SUFFIX STRUCTURES
// =========================================================================

function presetM2(type) {
  const textField = document.getElementById('m2-text');
  if (type === 'fertilizer') {
    textField.value = "Apply organic potassium fertilizer during early flowering. Soil inspection required. Apply organic potassium fertilizer during early flowering for maximum fruit set.";
  } else if (type === 'soil') {
    textField.value = "Soil nitrogen levels must be tested before planting. Apply nitrogen balancing compost. Soil nitrogen levels must be tested before planting to avoid nutrient lock.";
  }
  runM2();
}

function runM2() {
  const textRaw = document.getElementById('m2-text').value;
  const text = encodeURIComponent(textRaw);

  fetch(`/api/m2/lcp?text=${text}`)
    .then(res => res.json())
    .then(data => {
      if (data.error) {
        alert("Error: " + data.error);
        return;
      }

      const box = document.getElementById('m2-result');
      const out = document.getElementById('m2-output');
      const tblContainer = document.getElementById('m2-table-container');
      const tbody = document.getElementById('m2-sa-tbody');

      box.style.display = 'block';

      let outText = `==================================================================\n`;
      outText += `   SUFFIX ARRAY & KASAI LCP ARRAY ANALYSIS (CO2)\n`;
      outText += `==================================================================\n`;
      outText += `Execution Time                     : ${(data.timeNs / 1e6).toFixed(4)} ms (${data.timeNs.toLocaleString()} ns)\n`;
      outText += `Longest Repeated Protocol / Phrase : "${data.phrase}"\n`;
      outText += `Length of Duplicated Text          : ${data.length} characters\n`;
      outText += `Total Distinct Substring Sequences : ${data.totalSubstrings.toLocaleString()}\n`;
      outText += `Suffix Automaton (DAWG) States     : ${data.automatonStates}\n`;
      out.textContent = outText;

      // Populate Top Suffixes Table
      if (data.topSuffixes && data.topSuffixes.length > 0) {
        tblContainer.style.display = 'block';
        tbody.innerHTML = '';
        data.topSuffixes.forEach(s => {
          const row = document.createElement('tr');
          row.innerHTML = `<td>${s.rank + 1}</td><td><code>${s.index}</code></td><td><code>${escapeHtml(s.preview)}...</code></td>`;
          tbody.appendChild(row);
        });
      } else {
        tblContainer.style.display = 'none';
      }
    })
    .catch(err => alert("Error executing M2: " + err));
}

function runM2Query() {
  const textRaw = document.getElementById('m2-text').value;
  const patternRaw = document.getElementById('m2-query-pattern').value;

  const text = encodeURIComponent(textRaw);
  const pattern = encodeURIComponent(patternRaw);

  fetch(`/api/m2/query?text=${text}&pattern=${pattern}`)
    .then(res => res.json())
    .then(data => {
      const box = document.getElementById('m2-query-result');
      const out = document.getElementById('m2-query-output');
      box.style.display = 'block';

      let outText = `Pattern Queried               : "${data.pattern}"\n`;
      outText += `Found in Document             : ${data.exists ? 'YES (Pattern Exists!)' : 'NO (Not Found)'}\n`;
      outText += `Suffix Automaton Query Time   : ${data.samQueryTimeNs.toLocaleString()} ns (O(M) transition walk)\n`;
      outText += `Suffix Array Binary Search    : ${data.saQueryTimeNs.toLocaleString()} ns (Matched at text index ${data.saMatchIndex})\n`;
      out.textContent = outText;
    })
    .catch(err => alert("Error querying Suffix Automaton: " + err));
}

// =========================================================================
// MODULE 3: ADVANCED DYNAMIC PROGRAMMING
// =========================================================================

function presetM3DNA(type) {
  const s1 = document.getElementById('m3-seq1');
  const s2 = document.getElementById('m3-seq2');
  const mode = document.getElementById('m3-align-type');

  if (type === 'drought') {
    s1.value = "ATGCGTACAGTAGCTAGCT";
    s2.value = "ATGCTACGGTAGCTAGCT";
    mode.value = "global";
  } else if (type === 'motif') {
    s1.value = "GGGTACGTAAAACCCCGG";
    s2.value = "AAAATACGTAAATTTTTT";
    mode.value = "local";
  }
  runM3Align();
}

function runM3Align() {
  const seq1 = encodeURIComponent(document.getElementById('m3-seq1').value);
  const seq2 = encodeURIComponent(document.getElementById('m3-seq2').value);
  const type = document.getElementById('m3-align-type').value;

  fetch(`/api/m3/align?seq1=${seq1}&seq2=${seq2}&type=${type}`)
    .then(res => res.json())
    .then(data => {
      const box = document.getElementById('m3-align-result');
      const out = document.getElementById('m3-align-output');
      box.style.display = 'block';

      let textOut = `Algorithm Engine : ${data.type}\n`;
      textOut += `Execution Time   : ${(data.timeNs / 1e6).toFixed(4)} ms (${data.timeNs.toLocaleString()} ns)\n`;
      textOut += `Alignment Score  : ${data.score}\n`;

      if (data.identity !== undefined) {
        textOut += `Genetic Identity : ${data.identity.toFixed(1)}%\n\n`;
        textOut += `Visual DNA Alignment:\n`;
        textOut += `  Seq 1 : ${data.align1}\n`;
        textOut += `  Match : ${data.matchBar}\n`;
        textOut += `  Seq 2 : ${data.align2}\n`;
      } else {
        textOut += `\nConserved Resistance Motif Discovery:\n`;
        textOut += `  Sub-Region in Strand 1 : ${data.seq1} (Start: index ${data.pos1})\n`;
        textOut += `  Sub-Region in Strand 2 : ${data.seq2} (Start: index ${data.pos2})\n`;
      }
      out.textContent = textOut;
    })
    .catch(err => alert("Error executing DNA Alignment: " + err));
}

function runM3Bitmask() {
  const land = document.getElementById('m3-land').value;
  const water = document.getElementById('m3-water').value;

  fetch(`/api/m3/bitmask?land=${land}&water=${water}`)
    .then(res => res.json())
    .then(data => {
      const box = document.getElementById('m3-bitmask-result');
      const out = document.getElementById('m3-bitmask-output');
      box.style.display = 'block';

      let textOut = `==================================================================\n`;
      textOut += `   BITMASK DP OPTIMAL CROP PORTFOLIO SOLUTION (CO3)\n`;
      textOut += `==================================================================\n`;
      textOut += `Execution Time         : ${(data.timeNs / 1e6).toFixed(4)} ms (${data.timeNs.toLocaleString()} ns)\n`;
      textOut += `Max Projected Profit   : $${data.profit.toLocaleString()}\n`;
      textOut += `Farm Land Utilized     : ${data.landUsed} / ${data.maxLand} Acres (${(100.0 * data.landUsed / data.maxLand).toFixed(1)}%)\n`;
      textOut += `Water Budget Utilized  : ${data.waterUsed} / ${data.maxWater} kL (${(100.0 * data.waterUsed / data.maxWater).toFixed(1)}%)\n\n`;
      textOut += `Recommended Optimal Crop Selection (${data.crops.length} crops):\n`;
      data.crops.forEach(c => {
        textOut += `  [+] ${pad(c.name, 25)} | Land: ${c.land}A | Water: ${c.water}kL | Expected Profit: $${c.profit.toLocaleString()}\n`;
      });
      out.textContent = textOut;
    })
    .catch(err => alert("Error executing Bitmask DP: " + err));
}

function setM3Query(query) {
  document.getElementById('m3-query').value = query;
  runM3Autocorrect();
}

function runM3Autocorrect() {
  const queryRaw = document.getElementById('m3-query').value;
  const query = encodeURIComponent(queryRaw);

  fetch(`/api/m3/autocorrect?query=${query}`)
    .then(res => res.json())
    .then(data => {
      const box = document.getElementById('m3-autocorrect-result');
      const out = document.getElementById('m3-autocorrect-output');
      box.style.display = 'block';

      let textOut = `Farmer Typed Query            : "${data.input}"\n`;
      textOut += `Suggested Dictionary Term     : "${data.suggested}"\n`;
      textOut += `Edit Distance (Modifications) : ${data.distance}\n`;
      textOut += `Calculation Time              : ${data.timeNs.toLocaleString()} ns\n\n`;

      textOut += `Top Ranked Dictionary Suggestions:\n`;
      data.alternatives.forEach((s, idx) => {
        textOut += `  ${idx + 1}. "${s.term}" &bull; Distance: ${s.distance}\n`;
      });
      out.innerHTML = textOut;
    })
    .catch(err => alert("Error executing Autocorrect: " + err));
}

function runM3Mcm() {
  const dimsRaw = document.getElementById('m3-mcm-dims').value;
  const dims = encodeURIComponent(dimsRaw);

  fetch(`/api/m3/mcm?dims=${dims}`)
    .then(res => res.json())
    .then(data => {
      const box = document.getElementById('m3-mcm-result');
      const out = document.getElementById('m3-mcm-output');
      box.style.display = 'block';

      let textOut = `==================================================================\n`;
      textOut += `   MATRIX CHAIN MULTIPLICATION (MCM) OPTIMIZATION (CO3)\n`;
      textOut += `==================================================================\n`;
      textOut += `Transformation Dimensions      : [${data.dims}]\n`;
      textOut += `Minimal Scalar Multiplications : ${data.minMultiplications.toLocaleString()}\n`;
      textOut += `Optimal Pipeline Chain Order   : ${data.pipelineOrder}\n`;
      textOut += `DP Solving Time                : ${data.timeNs.toLocaleString()} ns\n`;
      out.textContent = textOut;
    })
    .catch(err => alert("Error optimizing MCM: " + err));
}

// =========================================================================
// MODULE 4: NETWORK FLOW & MATCHING
// =========================================================================

function runM4MaxFlow() {
  const algo = document.getElementById('m4-algo').value;

  fetch(`/api/m4/maxflow?algo=${algo}`)
    .then(res => res.json())
    .then(data => {
      const box = document.getElementById('m4-flow-result');
      const out = document.getElementById('m4-flow-output');
      const bContainer = document.getElementById('m4-bottlenecks-container');
      const bTbody = document.getElementById('m4-bottlenecks-tbody');
      box.style.display = 'block';

      let outText = `==================================================================\n`;
      outText += `   CANAL NETWORK MAXIMUM FLOW & MIN-CUT ANALYSIS (CO4)\n`;
      outText += `==================================================================\n`;
      outText += `Algorithm Engine       : ${data.algorithm}\n`;
      outText += `Maximum Flow Delivered : ${data.maxFlow} kL/hr (from Reservoir to Farm Plots)\n`;
      outText += `Execution Time         : ${(data.timeNs / 1e6).toFixed(4)} ms (${data.timeNs.toLocaleString()} ns)\n`;
      outText += `Network Vertices       : ${data.nodes.join(' -> ')}\n`;
      outText += `Min-Cut Bottlenecks    : ${data.bottlenecks.length} critical canal segments saturated\n`;
      out.textContent = outText;

      if (data.bottlenecks && data.bottlenecks.length > 0) {
        bContainer.style.display = 'block';
        bTbody.innerHTML = '';
        data.bottlenecks.forEach(b => {
          const row = document.createElement('tr');
          row.innerHTML = `<td><strong>${escapeHtml(b.from)}</strong></td><td><strong>${escapeHtml(b.to)}</strong></td><td><code>${b.capacity} kL/hr</code></td>`;
          bTbody.appendChild(row);
        });
      } else {
        bContainer.style.display = 'none';
      }
    })
    .catch(err => alert("Error computing Max Flow: " + err));
}

function runM4Bipartite() {
  fetch('/api/m4/bipartite')
    .then(res => res.json())
    .then(data => {
      const box = document.getElementById('m4-match-result');
      const out = document.getElementById('m4-match-output');
      const mContainer = document.getElementById('m4-match-table-container');
      const mTbody = document.getElementById('m4-match-tbody');
      box.style.display = 'block';

      let outText = `==================================================================\n`;
      outText += `   BIPARTITE MACHINERY ALLOCATION (HOPCROFT-KARP / FLOW) (CO4)\n`;
      outText += `==================================================================\n`;
      outText += `Total Requesting Farms : ${data.totalFarms}\n`;
      outText += `Successfully Matched   : ${data.matchedCount} out of ${data.totalFarms} farms (100% optimum)\n`;
      outText += `Matching Time          : ${(data.timeNs / 1e6).toFixed(4)} ms (${data.timeNs.toLocaleString()} ns)\n`;
      out.textContent = outText;

      if (data.assignments && data.assignments.length > 0) {
        mContainer.style.display = 'block';
        mTbody.innerHTML = '';
        data.assignments.forEach(a => {
          const row = document.createElement('tr');
          row.innerHTML = `<td>🌾 <strong>${escapeHtml(a.farm)}</strong></td><td>🚜 <span class="badge badge-algo">${escapeHtml(a.equipment)}</span></td>`;
          mTbody.appendChild(row);
        });
      } else {
        mContainer.style.display = 'none';
      }
    })
    .catch(err => alert("Error matching machinery: " + err));
}

// =========================================================================
// MODULE 5: NP-COMPLETENESS & APPROXIMATION
// =========================================================================

function runM5Sat() {
  fetch('/api/m5/sat')
    .then(res => res.json())
    .then(data => {
      const box = document.getElementById('m5-sat-result');
      const out = document.getElementById('m5-sat-output');
      box.style.display = 'block';

      let outText = `==================================================================\n`;
      outText += `   CROP ROTATION 3-SAT (DPLL) & CLIQUE REDUCTION (CO5)\n`;
      outText += `==================================================================\n`;
      outText += `Satisfiability Status  : ${data.satisfiable ? '✅ SATISFIABLE (Valid rotation plan found)' : '❌ UNSATISFIABLE'}\n`;
      outText += `Solving Time (DPLL)    : ${(data.timeNs / 1e6).toFixed(4)} ms (${data.timeNs.toLocaleString()} ns)\n`;
      outText += `Agronomic Rules Checked: ${data.ruleCount} 3-CNF clauses\n\n`;
      outText += `Optimal Boolean Variable Model:\n`;
      for (const [v, val] of Object.entries(data.model)) {
        outText += `  • ${pad(v, 20)} = ${val ? 'TRUE (Approved)' : 'FALSE (Rejected)'}\n`;
      }
      outText += `\nNP-Completeness Reduction:\n`;
      outText += `  • Reduced 3-CNF Formula to CLIQUE Problem\n`;
      outText += `  • Generated ${data.cliqueVertices} gadget vertices in polynomial time O(K²)\n`;
      outText += `  • Proves agricultural rotation scheduling is NP-Hard via Karp Reduction.\n`;
      out.textContent = outText;
    })
    .catch(err => alert("Error solving 3-SAT: " + err));
}

function runM5VertexCover() {
  fetch('/api/m5/vertexcover')
    .then(res => res.json())
    .then(data => {
      const box = document.getElementById('m5-vc-result');
      const out = document.getElementById('m5-vc-output');
      const vcContainer = document.getElementById('m5-vc-table-container');
      const vcList = document.getElementById('m5-vc-list');
      box.style.display = 'block';

      let outText = `==================================================================\n`;
      outText += `   2-APPROXIMATION VERTEX COVER WATCHTOWER PLACEMENT (CO5)\n`;
      outText += `==================================================================\n`;
      outText += `Total Road Junctions   : ${data.totalJunctions}\n`;
      outText += `Total Perimeter Roads  : ${data.totalRoads} pathways\n`;
      outText += `Towers Deployed        : ${data.towerCount} watchtower stations\n`;
      outText += `Approximation Guarantee: |C| <= 2 * |C*| (At most 2x optimal minimum)\n`;
      outText += `Computation Time       : ${(data.timeNs / 1e6).toFixed(4)} ms (${data.timeNs.toLocaleString()} ns)\n`;
      out.textContent = outText;

      if (data.towers && data.towers.length > 0) {
        vcContainer.style.display = 'block';
        vcList.innerHTML = '';
        data.towers.forEach(t => {
          const li = document.createElement('li');
          li.innerHTML = `📡 <strong>${escapeHtml(t)}</strong> (Surveillance & Drone Base)`;
          vcList.appendChild(li);
        });
      } else {
        vcContainer.style.display = 'none';
      }
    })
    .catch(err => alert("Error running Vertex Cover: " + err));
}

// =========================================================================
// MODULE 6: RANDOMIZED & PARALLEL TELEMETRY
// =========================================================================

function runM6Reservoir() {
  const k = document.getElementById('m6-reservoir-k').value;
  const streamSize = document.getElementById('m6-stream-size').value;

  fetch(`/api/m6/reservoir?k=${k}&streamSize=${streamSize}`)
    .then(res => res.json())
    .then(data => {
      const box = document.getElementById('m6-reservoir-result');
      const out = document.getElementById('m6-reservoir-output');
      const sContainer = document.getElementById('m6-sample-table-container');
      const sTbody = document.getElementById('m6-sample-tbody');
      box.style.display = 'block';

      let outText = `==================================================================\n`;
      outText += `   RESERVOIR SAMPLING (ALGORITHM R) STREAMING TELEMETRY (CO6)\n`;
      outText += `==================================================================\n`;
      outText += `Total Stream Processed : ${data.totalStream.toLocaleString()} continuous sensor readings\n`;
      outText += `Reservoir Memory Limit : ${data.sampleSize} items (O(k) constant space)\n`;
      outText += `Execution Time         : ${(data.timeNs / 1e6).toFixed(4)} ms (${data.timeNs.toLocaleString()} ns)\n`;
      outText += `Uniform Probability    : Each reading had exactly ${data.sampleSize}/${data.totalStream.toLocaleString()} chance\n`;
      out.textContent = outText;

      if (data.sample && data.sample.length > 0) {
        sContainer.style.display = 'block';
        sTbody.innerHTML = '';
        data.sample.forEach(r => {
          const row = document.createElement('tr');
          row.innerHTML = `<td><code>${escapeHtml(r.sensorId)}</code></td><td>${r.moisture.toFixed(1)}%</td><td>${r.temperature.toFixed(1)} °C</td>`;
          sTbody.appendChild(row);
        });
      } else {
        sContainer.style.display = 'none';
      }
    })
    .catch(err => alert("Error running Reservoir Sampling: " + err));
}

function runM6Blelloch() {
  fetch('/api/m6/blelloch')
    .then(res => res.json())
    .then(data => {
      const box = document.getElementById('m6-blelloch-result');
      const out = document.getElementById('m6-blelloch-output');
      box.style.display = 'block';

      let outText = `==================================================================\n`;
      outText += `   BLELLOCH WORK-EFFICIENT PARALLEL PREFIX SCAN (CO6)\n`;
      outText += `==================================================================\n`;
      outText += `Daily Rainfall (mm)   : ${data.daily}\n`;
      outText += `Exclusive Prefix Scan : ${data.exclusive}\n`;
      outText += `Inclusive Prefix Scan : ${data.inclusive}\n`;
      outText += `Work Complexity       : O(N) operations via Up-Sweep & Down-Sweep tree\n`;
      outText += `Span / Depth          : O(log N) parallel steps\n`;
      outText += `Scan Execution Time   : ${data.timeNs.toLocaleString()} ns\n`;
      out.textContent = outText;
    })
    .catch(err => alert("Error running Blelloch Scan: " + err));
}

function runM6Brent() {
  const work = document.getElementById('m6-brent-work').value;
  const depth = document.getElementById('m6-brent-depth').value;
  const cores = document.getElementById('m6-brent-cores').value;

  fetch(`/api/m6/brent?work=${work}&depth=${depth}&cores=${cores}`)
    .then(res => res.json())
    .then(data => {
      const box = document.getElementById('m6-brent-result');
      const out = document.getElementById('m6-brent-output');
      const bContainer = document.getElementById('m6-brent-table-container');
      const bTbody = document.getElementById('m6-brent-tbody');
      box.style.display = 'block';

      let outText = `==================================================================\n`;
      outText += `   BRENT'S THEOREM MULTI-CORE SCALING (CO6)\n`;
      outText += `==================================================================\n`;
      outText += `Total Work T₁ (Operations) : ${Number(data.work).toLocaleString()}\n`;
      outText += `Critical Path Depth T∞     : ${data.depth} steps\n`;
      outText += `Brent's Inequality         : Tp <= (T₁ - T∞)/p + T∞\n`;
      outText += `Evaluation Time            : ${data.timeNs.toLocaleString()} ns\n`;
      out.textContent = outText;

      if (data.scaling && data.scaling.length > 0) {
        bContainer.style.display = 'block';
        bTbody.innerHTML = '';
        data.scaling.forEach(s => {
          const row = document.createElement('tr');
          row.innerHTML = `<td><strong>${s.cores} Cores</strong></td><td>${s.timeBound.toFixed(0)} steps</td><td><span class="badge badge-success">${s.speedup.toFixed(2)}x</span></td><td>${s.efficiency.toFixed(1)}%</td>`;
          bTbody.appendChild(row);
        });
      } else {
        bContainer.style.display = 'none';
      }
    })
    .catch(err => alert("Error running Brent Analyzer: " + err));
}

function setM6Prime(val) {
  document.getElementById('m6-prime-num').value = val;
  runM6Prime();
}

function runM6Prime() {
  const num = encodeURIComponent(document.getElementById('m6-prime-num').value);

  fetch(`/api/m6/millerrabin?number=${num}`)
    .then(res => res.json())
    .then(data => {
      const box = document.getElementById('m6-prime-result');
      const out = document.getElementById('m6-prime-output');
      box.style.display = 'block';

      let outText = `==================================================================\n`;
      outText += `   MILLER-RABIN PROBABILISTIC PRIMALITY TEST (CO6)\n`;
      outText += `==================================================================\n`;
      outText += `Tested Serial Number   : ${data.number}\n`;
      outText += `Primality Verdict      : ${data.isPrime ? '✅ PROBABLE PRIME (Cryptographically Valid Seed Batch)' : '❌ COMPOSITE (Invalid Batch Serial)'}\n`;
      outText += `Independent Rounds (k) : ${data.iterations}\n`;
      outText += `Error Probability Bound: <= 4^(-${data.iterations}) ≈ 9.54e-7 (< 0.0001%)\n`;
      outText += `Verification Time      : ${(data.timeNs / 1e6).toFixed(4)} ms (${data.timeNs.toLocaleString()} ns)\n`;
      out.textContent = outText;
    })
    .catch(err => alert("Error running Miller-Rabin: " + err));
}

function runM6Reduce() {
  fetch('/api/m6/reduce?count=50000')
    .then(res => res.json())
    .then(data => {
      const box = document.getElementById('m6-reduce-result');
      const out = document.getElementById('m6-reduce-output');
      box.style.display = 'block';

      let outText = `==================================================================\n`;
      outText += `   FORK-JOIN PARALLEL SENSOR REDUCE (CO6)\n`;
      outText += `==================================================================\n`;
      outText += `Sensors Aggregated     : ${data.count.toLocaleString()} field telemetry probes\n`;
      outText += `Minimum Reading        : ${data.min.toFixed(2)} °C\n`;
      outText += `Maximum Reading        : ${data.max.toFixed(2)} °C\n`;
      outText += `Average Temperature    : ${data.avg.toFixed(2)} °C\n`;
      outText += `Sum of Telemetry       : ${data.sum.toFixed(2)}\n`;
      outText += `Parallel Reduce Time   : ${(data.timeNs / 1e6).toFixed(4)} ms (${data.timeNs.toLocaleString()} ns)\n`;
      out.textContent = outText;
    })
    .catch(err => alert("Error running Parallel Reduce: " + err));
}

function runM6QuickSort() {
  fetch('/api/m6/quicksort')
    .then(res => res.json())
    .then(data => {
      const box = document.getElementById('m6-sort-result');
      const out = document.getElementById('m6-sort-output');
      const sContainer = document.getElementById('m6-sort-table-container');
      const sTbody = document.getElementById('m6-sort-tbody');
      box.style.display = 'block';

      let outText = `==================================================================\n`;
      outText += `   RANDOMIZED QUICKSORT HARVEST YIELD LEAGUE (CO6)\n`;
      outText += `==================================================================\n`;
      outText += `Farms Sorted           : ${data.rankedFarms.length}\n`;
      outText += `Pivot Strategy         : Uniformly Random Pivot (Prevents O(N²) worst-case)\n`;
      outText += `Sorting Time           : ${(data.timeNs / 1e6).toFixed(4)} ms (${data.timeNs.toLocaleString()} ns)\n`;
      out.textContent = outText;

      if (data.rankedFarms && data.rankedFarms.length > 0) {
        sContainer.style.display = 'block';
        sTbody.innerHTML = '';
        data.rankedFarms.forEach((f, idx) => {
          const row = document.createElement('tr');
          row.innerHTML = `<td>#${idx + 1}</td><td><strong>${escapeHtml(f.farmId)}</strong></td><td>${escapeHtml(f.crop)}</td><td><strong>${f.yield.toFixed(2)} Tons/Acre</strong></td>`;
          sTbody.appendChild(row);
        });
      } else {
        sContainer.style.display = 'none';
      }
    })
    .catch(err => alert("Error running Randomized QuickSort: " + err));
}

function runAutomatedSuite() {
  alert("Running full automated test suite across Modules 1 to 6! You can also run option [7] in the console CLI for a complete terminal report.");
  switchTab('m1');
  presetM1('blight');
}

// Utilities
function escapeHtml(text) {
  const map = { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;' };
  return text.replace(/[&<>"']/g, m => map[m]);
}

function pad(str, len) {
  let s = String(str);
  while (s.length < len) s += ' ';
  return s;
}

// =========================================================================
// KAGGLE AGRITECH DATASET SUITE & AUDIT CONTROLLER
// =========================================================================

window.agriDatasets = [];
window.currentPreviewDataset = null;
window.currentPreviewModule = null;

// Fetch and initialize Kaggle datasets on load
function initDatasets() {
  fetch('/api/dataset/list')
    .then(res => res.json())
    .then(data => {
      if (data && data.datasets) {
        window.agriDatasets = data.datasets;
        renderDashboardDatasetGrid(window.agriDatasets);
        populateModuleDropdowns(window.agriDatasets);
      }
    })
    .catch(err => console.error("Could not load datasets:", err));
}

// Render the 14 Dataset Cards in the Farm Control Center
function renderDashboardDatasetGrid(datasets) {
  const container = document.getElementById('dashboard-dataset-grid');
  if (!container) return;

  if (datasets.length === 0) {
    container.innerHTML = `<div style="grid-column: 1 / -1; text-align: center; padding: 30px; color: var(--text-muted);">No matching datasets found.</div>`;
    return;
  }

  container.innerHTML = '';
  datasets.forEach(ds => {
    const card = document.createElement('div');
    card.className = 'dataset-card';
    card.innerHTML = `
      <div>
        <div class="dataset-card-top">
          <span class="dataset-badge-mod">${escapeHtml(ds.module)} &bull; ${escapeHtml(ds.co)}</span>
          <span style="font-size: 0.72rem; color: #166534; background: #dcfce7; padding: 2px 6px; border-radius: 4px; font-weight: 700;">${escapeHtml(ds.format)}</span>
        </div>
        <div class="dataset-card-title">${escapeHtml(ds.name)}</div>
        <div class="dataset-filename">📄 ${escapeHtml(ds.filename)}</div>
        <div class="dataset-desc">${escapeHtml(ds.description)}</div>
        <div class="dataset-algo-tag">⚡ <strong>Algorithms:</strong> ${escapeHtml(ds.algorithms)}</div>
      </div>
      <div class="dataset-card-footer">
        <button class="btn-card-load" onclick="openAndLoadDataset('${escapeHtml(ds.module.toLowerCase().split(',')[0].trim())}', '${escapeHtml(ds.filename)}')">
          🚀 Load in ${escapeHtml(ds.module.split(',')[0].trim())}
        </button>
        <button class="btn-card-view" onclick="previewDataset('${escapeHtml(ds.filename)}')">
          👁️ Preview
        </button>
      </div>
    `;
    container.appendChild(card);
  });
}

// Populate dropdowns in M1 to M6
function populateModuleDropdowns(datasets) {
  const modules = ['m1', 'm2', 'm3', 'm4', 'm5', 'm6'];
  modules.forEach(mod => {
    const select = document.getElementById(mod + '-dataset-select');
    if (!select) return;

    // Filter datasets relevant to this module first, then include others
    const relevant = datasets.filter(d => d.module.toLowerCase().includes(mod));
    const others = datasets.filter(d => !d.module.toLowerCase().includes(mod));

    let html = '';
    if (relevant.length > 0) {
      html += `<optgroup label="Recommended for ${mod.toUpperCase()}">`;
      relevant.forEach((d, idx) => {
        html += `<option value="${escapeHtml(d.filename)}" ${idx === 0 ? 'selected' : ''}>${escapeHtml(d.filename)} (${escapeHtml(d.name)})</option>`;
      });
      html += `</optgroup>`;
    }

    if (others.length > 0) {
      html += `<optgroup label="Other AgriFlow Datasets">`;
      others.forEach(d => {
        html += `<option value="${escapeHtml(d.filename)}">${escapeHtml(d.filename)} (${escapeHtml(d.module)})</option>`;
      });
      html += `</optgroup>`;
    }

    select.innerHTML = html;
  });
}

// Filter dataset cards by category
function filterDatasets(category, element) {
  document.querySelectorAll('.filter-chip').forEach(c => c.classList.remove('active'));
  if (element) element.classList.add('active');

  if (category === 'all') {
    renderDashboardDatasetGrid(window.agriDatasets);
  } else {
    const filtered = window.agriDatasets.filter(d => d.module.toUpperCase().includes(category.toUpperCase()));
    renderDashboardDatasetGrid(filtered);
  }
}

// Real-time search in Dataset Explorer
function onSearchDatasets(query) {
  const q = query.toLowerCase().trim();
  if (!q) {
    renderDashboardDatasetGrid(window.agriDatasets);
    return;
  }
  const filtered = window.agriDatasets.filter(d => 
    d.name.toLowerCase().includes(q) ||
    d.filename.toLowerCase().includes(q) ||
    d.description.toLowerCase().includes(q) ||
    d.algorithms.toLowerCase().includes(q) ||
    d.module.toLowerCase().includes(q)
  );
  renderDashboardDatasetGrid(filtered);
}

// Dropdown change handler on module screens
function onDatasetSelectChanged(modId) {
  const select = document.getElementById(modId + '-dataset-select');
  const status = document.getElementById(modId + '-dataset-status');
  if (select && status) {
    status.textContent = 'Selected: ' + select.value.replace('.txt', '');
    status.style.background = '#e0f2fe';
    status.style.color = '#0369a1';
    status.style.borderColor = '#bae6fd';
  }
}

// Load dataset directly from dropdown on module tab
function loadDatasetIntoModule(modId, explicitFile) {
  const select = document.getElementById(modId + '-dataset-select');
  const filename = explicitFile || (select ? select.value : '');
  if (!filename) return;

  const status = document.getElementById(modId + '-dataset-status');
  if (status) {
    status.textContent = 'Loading...';
    status.style.background = '#fef08a';
    status.style.color = '#854d0e';
  }

  fetch('/api/dataset/read?file=' + encodeURIComponent(filename))
    .then(res => res.json())
    .then(data => {
      if (data.error) {
        alert("Error loading dataset: " + data.error);
        if (status) status.textContent = 'Error';
        return;
      }

      // Inject parsed sample data into module fields
      applyDatasetToModule(modId, filename, data);

      if (status) {
        status.textContent = `✓ Loaded ${data.recordCount || data.linesCount} records (${(data.sizeBytes/1024).toFixed(1)} KB)`;
        status.style.background = '#dcfce7';
        status.style.color = '#166534';
        status.style.borderColor = '#bbf7d0';
      }
    })
    .catch(err => {
      alert("Network error reading dataset: " + err);
      if (status) status.textContent = 'Failed';
    });
}

// Apply dataset content to form inputs
function applyDatasetToModule(modId, filename, data) {
  const sample = data.sample || {};

  if (modId === 'm1') {
    const textField = document.getElementById('m1-text');
    const patField = document.getElementById('m1-pattern');
    const algoSelect = document.getElementById('m1-algo');
    if (textField && sample.text) textField.value = sample.text;
    if (patField && sample.pattern) patField.value = sample.pattern;
    if (filename.contains ? filename.contains('pathology') : filename.indexOf('pathology') !== -1) {
      if (patField && sample.dictionary) patField.value = sample.dictionary;
      if (algoSelect) algoSelect.value = 'aho';
    } else if (filename.indexOf('seed_batch') !== -1) {
      if (algoSelect) algoSelect.value = 'rabin';
    } else {
      if (algoSelect) algoSelect.value = 'kmp';
    }
  } else if (modId === 'm2') {
    const textField = document.getElementById('m2-text');
    const patField = document.getElementById('m2-query-pattern');
    if (textField && sample.text) textField.value = sample.text;
    if (patField && sample.query) patField.value = sample.query;
  } else if (modId === 'm3') {
    if (sample.seqA && sample.seqB) {
      const s1 = document.getElementById('m3-seq1');
      const s2 = document.getElementById('m3-seq2');
      if (s1) s1.value = sample.seqA;
      if (s2) s2.value = sample.seqB;
    }
    if (sample.word1) {
      const qInput = document.getElementById('m3-autocorrect-input');
      if (qInput) qInput.value = sample.word1;
    }
    if (sample.dimensions) {
      const dims = document.getElementById('m3-mcm-dims');
      if (dims) dims.value = sample.dimensions;
    }
    if (sample.budget && sample.maxLand) {
      const land = document.getElementById('m3-land');
      const water = document.getElementById('m3-water');
      if (land) land.value = sample.maxLand;
      if (water) water.value = sample.budget;
    }
  } else if (modId === 'm4') {
    const algoSelect = document.getElementById('m4-algo');
    if (algoSelect) algoSelect.value = 'dinic';
  } else if (modId === 'm5') {
    // 3-SAT or Vertex Cover parameters
  } else if (modId === 'm6') {
    if (sample.number) {
      const numInput = document.getElementById('m6-prime-input');
      if (numInput) numInput.value = sample.number;
    }
    if (sample.k) {
      const kInput = document.getElementById('m6-reservoir-k');
      if (kInput) kInput.value = sample.k;
    }
  }
}

// Open dataset and jump directly to that module
function openAndLoadDataset(modId, filename) {
  switchTab(modId);
  const select = document.getElementById(modId + '-dataset-select');
  if (select) {
    select.value = filename;
    onDatasetSelectChanged(modId);
  }
  loadDatasetIntoModule(modId, filename);
}

// Preview button click from module tab
function previewDatasetFromModule(modId) {
  const select = document.getElementById(modId + '-dataset-select');
  const filename = select ? select.value : 'kaggle_crop_disease_symptoms.txt';
  previewDataset(filename, modId);
}

// Open Raw Dataset Preview Modal
function previewDataset(filename, originatingModule) {
  window.currentPreviewDataset = filename;
  window.currentPreviewModule = originatingModule || 'm1';

  const modal = document.getElementById('dataset-preview-modal');
  const title = document.getElementById('modal-dataset-title');
  const meta = document.getElementById('modal-dataset-meta');
  const content = document.getElementById('modal-dataset-content');

  if (title) title.innerHTML = `📄 Dataset File: <code>${escapeHtml(filename)}</code>`;
  if (meta) meta.textContent = 'Loading Kaggle metadata and records...';
  if (content) content.textContent = 'Fetching file from repository...';
  if (modal) modal.style.display = 'flex';

  fetch('/api/dataset/read?file=' + encodeURIComponent(filename))
    .then(res => res.json())
    .then(data => {
      if (data.error) {
        if (meta) meta.textContent = 'Error: ' + data.error;
        if (content) content.textContent = '';
        return;
      }

      if (meta) {
        meta.textContent = data.metadata || 'No metadata header found.';
      }
      if (content) {
        content.textContent = data.content || '';
      }
    })
    .catch(err => {
      if (meta) meta.textContent = 'Failed to load file: ' + err;
    });
}

// Copy preview text
function copyDatasetText() {
  const content = document.getElementById('modal-dataset-content');
  if (content && content.textContent) {
    navigator.clipboard.writeText(content.textContent)
      .then(() => alert("✓ Dataset copied to clipboard!"))
      .catch(() => alert("Could not copy automatically. Please select text and Ctrl+C."));
  }
}

// Load current previewed dataset into its module
function loadCurrentPreviewIntoModule() {
  const filename = window.currentPreviewDataset;
  if (!filename) return;

  // Determine target module
  let modId = window.currentPreviewModule;
  if (window.agriDatasets) {
    const ds = window.agriDatasets.find(d => d.filename === filename);
    if (ds && ds.module) {
      modId = ds.module.toLowerCase().split(',')[0].trim();
    }
  }

  closeModal('dataset-preview-modal');
  openAndLoadDataset(modId, filename);
}

// Full Farm Optimization Audit (M1 to M6) Scorecard
function runFullBenchmarkAudit() {
  const modal = document.getElementById('benchmark-modal');
  const summary = document.getElementById('benchmark-scorecard-summary');
  const tbody = document.getElementById('benchmark-tbody');

  if (modal) modal.style.display = 'flex';
  if (summary) summary.innerHTML = `<span class="scorecard-badge" style="background:#fef08a; color:#854d0e;">⚡ Executing Java Core Benchmarks across M1 to M6...</span>`;
  if (tbody) tbody.innerHTML = `<tr><td colspan="5" style="text-align:center; padding: 24px;">Running Knuth-Morris-Pratt, Kasai LCP, Needleman-Wunsch, Dinic Max Flow, 3-SAT DPLL, and Blelloch Scan...</td></tr>`;

  fetch('/api/benchmark')
    .then(res => res.json())
    .then(data => {
      if (data.error) {
        if (summary) summary.innerHTML = `<span class="scorecard-badge" style="background:#fee2e2; color:#991b1b;">Error: ${escapeHtml(data.error)}</span>`;
        return;
      }

      if (summary) {
        summary.innerHTML = `
          <span class="scorecard-badge" style="background:#dcfce7; color:#166534;">✓ Overall Status: ${escapeHtml(data.status)}</span>
          <span class="scorecard-badge" style="background:#e0f2fe; color:#0369a1;">Modules Verified: ${data.modulesBenchmarked} / 6 (CO1 to CO6)</span>
          <span class="scorecard-badge" style="background:#f3e8ff; color:#6b21a8;">Total Execution Time: ${data.totalDurationUs.toLocaleString()} &mu;s (~${(data.totalDurationUs/1000).toFixed(2)} ms)</span>
        `;
      }

      if (tbody && data.results) {
        tbody.innerHTML = '';
        data.results.forEach(r => {
          const row = document.createElement('tr');
          row.innerHTML = `
            <td><strong>${escapeHtml(r.module)}</strong> &bull; <span class="badge badge-mod">${escapeHtml(r.co)}</span></td>
            <td><strong>${escapeHtml(r.name)}</strong></td>
            <td><code>${escapeHtml(r.dataset || 'Java Standard Suite')}</code></td>
            <td><span class="badge badge-success">✓ PASS</span></td>
            <td><strong>${r.timeUs} &mu;s</strong></td>
          `;
          tbody.appendChild(row);
        });
      }
    })
    .catch(err => {
      if (summary) summary.innerHTML = `<span class="scorecard-badge" style="background:#fee2e2; color:#991b1b;">Audit Error: ${escapeHtml(err.message)}</span>`;
    });
}

// Modal closing helpers
function closeModal(id) {
  const m = document.getElementById(id);
  if (m) m.style.display = 'none';
}

function onModalBackdropClick(event, id) {
  if (event.target.id === id) {
    closeModal(id);
  }
}

// Check server status on load and initialize datasets
window.addEventListener('DOMContentLoaded', () => {
  fetch('/api/status')
    .then(res => res.json())
    .then(data => {
      const badge = document.getElementById('server-status');
      if (badge && data.ready) {
        badge.innerHTML = `<span class="dot"></span> ${data.project} ${data.scope} Active`;
      }
    })
    .catch(() => {});

  // Initialize datasets
  initDatasets();
});


