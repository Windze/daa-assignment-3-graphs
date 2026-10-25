# Custom Tests

This directory contains test cases and justifications for testing graph algorithms.

## Commands

Run tests through Java entry point:
```bash
python tools/run.py --input tests/my_cases.jsonl
```

Or execute directly via compiled Java bytecode:
```bash
java -cp build Main < tests/my_cases.jsonl
```

## Test Cases and Justifications

### 1. `dsu`
- **Input**: `n=5`, operations: `[[0, 1], [2, 3], [0, 2], [1, 3], [4, 4]]`
- **Expected**: `{"states":[[4,2],[3,2],[2,4],[2,4],[2,4]]}`
- **Reasoning**:
  - `[0,1]`: components = 4, largest = 2 ({0,1})
  - `[2,3]`: components = 3, largest = 2 ({2,3})
  - `[0,2]`: components = 2, largest = 4 ({0,1,2,3})
  - `[1,3]`: vertices 1 and 3 already in same component; components and largest remain unchanged (`[2,4]`)
  - `[4,4]`: self-union does not affect component partition; remains `[2,4]`

### 2. `kruskal` & `prim`
- **Input**: `n=5`, edges: `[[0,1,10],[1,2,-5],[2,3,2],[3,0,8],[0,2,4]]`
- **Expected**: `cost = 1`, `components = 2` (vertex 4 is isolated)
- **Reasoning**:
  - Sorted edges: `[1,2,-5]`, `[2,3,2]`, `[0,2,4]`, `[3,0,8]`, `[0,1,10]`
  - Kruskal picks edge 1 (weight -5), edge 2 (weight 2), edge 4 (weight 4). Total cost = -5 + 2 + 4 = 1.
  - Prim starting from component {0,1,2,3} builds identical minimum spanning tree with cost 1.
  - Isolated vertex 4 forms a separate component, giving total components = 2.

### 3. `scc`
- **Input**: `n=6`, edges: `[[0,1],[1,2],[2,0],[2,3],[3,4],[4,5],[5,3]]`
- **Expected**: `{"component":[0,0,0,1,1,1],"condensation":[[0,1]]}`
- **Reasoning**:
  - Directed cycle 0 -> 1 -> 2 -> 0 forms first SCC {0, 1, 2}.
  - Directed cycle 3 -> 4 -> 5 -> 3 forms second SCC {3, 4, 5}.
  - Directed edge 2 -> 3 creates a single condensation edge between component 0 and component 1.

### 4. `topo`
- **Input**: `n=6`, edges: `[[5,2],[5,0],[4,0],[4,1],[2,3],[3,1]]`
- **Expected**: `{"acyclic":true,"order":[5,4,2,3,1,0]}`
- **Reasoning**:
  - Graph is a DAG. Valid topological sort orders all dependencies correctly forward.

### 5. `dag` (Shortest paths)
- **Input**: `n=6`, `source=0`, edges: `[[0,1,5],[0,2,3],[1,3,6],[2,3,7],[3,4,-1],[2,4,4],[4,5,-2]]`
- **Expected**: `{"acyclic":true,"distance":[0,5,3,10,7,5],"parent_edge":[-1,0,1,3,5,6]}`
- **Reasoning**:
  - Path to 1: 0 -> 1 (cost 5, edge 0)
  - Path to 2: 0 -> 2 (cost 3, edge 1)
  - Path to 3: 0 -> 2 -> 3 (cost 3+7=10, edge 3 is shorter than 0->1->3 with 5+6=11)
  - Path to 4: 0 -> 2 -> 4 (cost 3+4=7, edge 5 is shorter than 0->2->3->4 with 10+(-1)=9)
  - Path to 5: 0 -> 2 -> 4 -> 5 (cost 7+(-2)=5, edge 6)

### 6. `dag` with cycle
- **Input**: `n=4`, `source=0`, edges: `[[0,1,2],[1,2,3],[2,3,4],[3,1,1]]`
- **Expected**: `{"acyclic":false,"distance":[],"parent_edge":[]}`
- **Reasoning**: Directed cycle 1 -> 2 -> 3 -> 1 exists in the graph, so `acyclic` is false.
