# Analysis and Benchmark Report

## 1. Algorithm Descriptions

### DSU
- **Steps**:
  - Maintained `parent` and `size` arrays of length `n`.
  - Used iterative two-pass find to compress paths.
  - In `union`, compared tree sizes and attached smaller root under larger root. Kept track of connected components and current largest set size.
- **Invariant & Correctness**: Trees maintain disjoint partition of vertices. Union-by-size and path compression yield $O(\alpha(n))$ amortized time per operation.
- **Edge cases**: Handles $n=0$ by returning empty list; handles self-loops (`u == v`) and repeated unions without altering component counts.
- **Complexity**: $O(n + q \alpha(n))$ time, $O(n + q)$ memory.

### Kruskal
- **Steps**:
  - Stored edge indices `0..m-1` and sorted them by edge weight.
  - Used DSU to greedily pick non-cycle edges until all candidates processed.
  - Kept running cost in a 64-bit `long`.
- **Invariant & Correctness**: Preserves cut property; greedily picks minimal available cross-component edge without creating cycles.
- **Edge cases**: $n=0$, disconnected graphs with isolated nodes, negative weights, parallel edges, self-loops.
- **Complexity**: $O(n + m \log m)$ time, $O(n + m)$ memory.

### Prim
- **Steps**:
  - Built adjacency list filtering out trivial self-loops.
  - Processed each unvisited vertex as a new component root.
  - Used `PriorityQueue<int[]>` keyed by edge weight. When an unvisited neighbor is reached, added edge weight to total and pushed adjacent edges.
- **Invariant & Correctness**: Expands MST cut component by component. Disconnected parts are handled by the outer loop restart.
- **Edge cases**: $n=0$, negative weights, parallel edges, isolated vertices.
- **Complexity**: $O(n + m \log m)$ time, $O(n + m)$ memory.

### Kosaraju SCC
- **Steps**:
  - Built forward and reverse graphs with compact arrays (`head`, `to`, `next`).
  - First pass: iterative DFS pushing finished vertices into a list.
  - Second pass: traversed vertices in reverse finish order on transposed graph with iterative DFS, assigning SCC IDs.
  - Condensation: collected unique pairs `comp[u] -> comp[v]` for `comp[u] != comp[v]` using a `HashSet<Long>`.
- **Invariant & Correctness**: Processing in decreasing finish times guarantees exploration does not leak to other components in reversed graph. Explicit stack prevents stack overflow on deep paths up to $N = 100\,000$.
- **Edge cases**: $n=0$, isolated vertices, cycles of length 1 (self-loops), multigraph edges.
- **Complexity**: $O(n + m)$ time, $O(n + m)$ memory.

### Topological Sort (DFS)
- **Steps**:
  - Three states: 0 = unvisited, 1 = in progress (active), 2 = finished.
  - Iterative DFS maintaining an edge pointer per vertex.
  - If a neighbor with state 1 is seen, a directed cycle exists -> returns `acyclic = false` and empty order.
  - Finished vertices collected and reversed.
- **Invariant & Correctness**: Any back-edge to an active vertex proves a cycle; reverse post-order produces valid topological ordering for DAGs.
- **Edge cases**: $n=0$, self-loops, disconnected cycles.
- **Complexity**: $O(n + m)$ time and space.

### DAG Shortest Paths
- **Steps**:
  - Checked for cycles across the whole graph via 3-state DFS. If cyclic, returned `acyclic = false`.
  - Initialized `distance` with `null` (`Long[]`) and `parent_edge` with `-1`. Set `distance[source] = 0`.
  - Processed vertices in topological order; relaxed outgoing edges from reachable vertices.
- **Route reconstruction helper**:
  - Traversed backwards from target to source using `parent_edge`.
  - For reachable targets, returns `[source, ..., target]`. For source itself, returns `[source]`. For unreachable nodes, returns `[]`.
- **Complexity**: $O(n + m)$ time and space for shortest paths; $O(L)$ for reconstructing a route of length $L$.

---

## 2. Route Reconstruction Verification

Tested via `RouteTest`:
- Reachable target: source 0 to target 3 returned `[0, 1, 2, 3]` (edges 0, 2, 3 with total weight 0).
- Source vertex: source 0 to target 0 returned `[0]`.
- Unreachable target: source 0 to target 5 returned `[]`.
- Tied/parallel edges: source 0 to target 2 returned `[0, 1, 2]` with valid total cost 7.

---

## 3. Kruskal vs Prim Benchmark

### Setup
- Machine: AMD Ryzen 5, Windows 11, OpenJDK 17.0.12.
- 10 warmup iterations prior to timing.
- Execution measured with `System.nanoTime()` around algorithm execution only (no IO/JSON parsing).
- Tested on identical graph instances generated with fixed random seeds.

### Results

| Size | n | m | Kruskal Trials (ms) | Prim Trials (ms) | Median Kruskal | Median Prim |
|---|---|---|---|---|---|---|
| Small | 1,000 | 3,000 | 3.237, 2.571, 1.360, 1.336, 1.313 | 3.149, 6.231, 4.645, 2.283, 1.592 | **1.360 ms** | **3.149 ms** |
| Medium | 5,000 | 20,000 | 10.281, 10.836, 11.697, 17.671, 16.502 | 8.848, 15.796, 9.964, 7.273, 10.657 | **11.697 ms** | **9.964 ms** |
| Large | 20,000 | 80,000 | 106.607, 77.328, 56.674, 66.600, 50.912 | 58.119, 49.480, 52.576, 38.995, 41.423 | **66.600 ms** | **49.480 ms** |

### Discussion
- On small sparse graphs ($n=1000, m=3000$), Kruskal is slightly faster because sorting a small array is fast and cache-friendly.
- On larger graphs, Kruskal suffers from sorting overhead and Integer boxing in Java, while Prim's priority queue processes edges incrementally, resulting in lower median execution times on medium and large test sets.

---

## 4. Run Commands

```bash
# Build
javac -d build src/Java/*.java

# Public tests
python tools/run.py --setup
python tools/run.py --check

# Custom tests
python tools/run.py --input tests/my_cases.jsonl
```
