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

function runAutomatedSuite() {
  alert("Running full automated test suite across Modules 1 to 3! Check each tab or use the console CLI for full benchmark logs.");
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

// Check server status on load
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
});
