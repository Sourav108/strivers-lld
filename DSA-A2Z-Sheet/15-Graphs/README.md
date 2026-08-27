# 15 — Graphs

> **Topic Problem Count**: 53 Problems  
> **Language**: C++ (with Intuition, Multi-Tier Approaches, and Complexity Analysis)

## 📌 Overview
BFS/DFS, Topological Sort, Shortest Paths (Dijkstra, Bellman-Ford, Floyd-Warshall), Disjoint Set Union, MST (Kruskal/Prim), Bridges, and Kosaraju SCC.

---

## 📋 Problem Checklist

### Step 15.1: Learning (5 Problems)

- [ ] **Problem 01**: [Introduction to Graphs, Degrees, and Types](https://takeuforward.org/graph/introduction-to-graph/) — 🟢 `Easy`
- [ ] **Problem 02**: [Graph Representation in C++ (Adjacency Matrix & Adjacency List)](https://takeuforward.org/graph/graph-representation-in-c/) — 🟢 `Easy`
- [ ] **Problem 03**: [Connected Components in Graph](https://takeuforward.org/graph/connected-components-in-graph/) — 🟢 `Easy`
- [ ] **Problem 04**: [Breadth First Search (BFS) Traversal](https://takeuforward.org/data-structure/breadth-first-search-bfs-level-order-traversal/) — 🟢 `Easy`
- [ ] **Problem 05**: [Depth First Search (DFS) Traversal](https://takeuforward.org/data-structure/depth-first-search-dfs-traversal-graph/) — 🟢 `Easy`

### Step 15.2: Problems on BFS / DFS (14 Problems)

- [ ] **Problem 06**: [Number of Provinces (Connected Components in Disjoint Graph)](https://takeuforward.org/data-structure/number-of-provinces/) — 🟡 `Medium`
- [ ] **Problem 07**: [Rotting Oranges (Multi-source BFS)](https://takeuforward.org/data-structure/rotton-oranges-min-time-to-rot-all-oranges-bfs/) — 🟡 `Medium`
- [ ] **Problem 08**: [Flood Fill Algorithm](https://takeuforward.org/data-structure/flood-fill-algorithm/) — 🟢 `Easy`
- [ ] **Problem 09**: [Detect Cycle in an Undirected Graph using BFS](https://takeuforward.org/data-structure/detect-cycle-in-an-undirected-graph-using-bfs/) — 🟡 `Medium`
- [ ] **Problem 10**: [Detect Cycle in an Undirected Graph using DFS](https://takeuforward.org/data-structure/detect-cycle-in-an-undirected-graph-using-dfs/) — 🟡 `Medium`
- [ ] **Problem 11**: [0/1 Matrix (Distance of Nearest Cell having 1)](https://takeuforward.org/data-structure/distance-of-nearest-cell-having-1/) — 🟡 `Medium`
- [ ] **Problem 12**: [Surrounded Regions (Replace O's with X's on Board)](https://takeuforward.org/data-structure/surrounded-regions-replace-os-with-xs/) — 🟡 `Medium`
- [ ] **Problem 13**: [Number of Enclaves (Count unreachable land cells)](https://takeuforward.org/data-structure/number-of-enclaves/) — 🟡 `Medium`
- [ ] **Problem 14**: [Word Ladder I (Shortest transformation sequence length)](https://takeuforward.org/data-structure/word-ladder-i-shortest-paths/) — 🔴 `Hard`
- [ ] **Problem 15**: [Word Ladder II (Find all shortest transformation sequences)](https://takeuforward.org/data-structure/word-ladder-ii-shortest-paths/) — 🔴 `Hard`
- [ ] **Problem 16**: [Number of Distinct Islands (DFS with shape serialization)](https://takeuforward.org/data-structure/number-of-distinct-islands/) — 🟡 `Medium`
- [ ] **Problem 17**: [Check if Graph is Bipartite (2-Coloring via BFS/DFS)](https://takeuforward.org/data-structure/bipartite-graph/) — 🟡 `Medium`
- [ ] **Problem 18**: [Detect Cycle in a Directed Graph using DFS (Recursion Stack)](https://takeuforward.org/data-structure/detect-a-cycle-in-directed-graph-using-dfs/) — 🟡 `Medium`
- [ ] **Problem 19**: [Number of Islands (Grid DFS/BFS)](https://takeuforward.org/data-structure/find-the-number-of-islands-using-dsu/) — 🟡 `Medium`

### Step 15.3: Topological Sort and Kahn's Algorithm (6 Problems)

- [ ] **Problem 20**: [Topological Sort using DFS (Finish time stack)](https://takeuforward.org/data-structure/topological-sort-algorithm-dfs/) — 🟡 `Medium`
- [ ] **Problem 21**: [Kahn's Algorithm (Topological Sort using BFS In-Degree)](https://takeuforward.org/data-structure/topological-sort-bfs/) — 🟡 `Medium`
- [ ] **Problem 22**: [Detect Cycle in a Directed Graph using BFS (Kahn's Algorithm)](https://takeuforward.org/data-structure/detect-a-cycle-in-directed-graph-using-bfs/) — 🟡 `Medium`
- [ ] **Problem 23**: [Course Schedule I (Prerequisites cycle check)](https://takeuforward.org/data-structure/course-schedule-i-and-ii/) — 🟡 `Medium`
- [ ] **Problem 24**: [Course Schedule II (Find valid course ordering)](https://takeuforward.org/data-structure/course-schedule-i-and-ii/) — 🟡 `Medium`
- [ ] **Problem 25**: [Find Eventual Safe States & Alien Dictionary](https://takeuforward.org/data-structure/alien-dictionary/) — 🔴 `Hard`

### Step 15.4: Shortest Path Algorithms (13 Problems)

- [ ] **Problem 26**: [Shortest Path in Undirected Graph with Unit Weights](https://takeuforward.org/data-structure/shortest-path-in-undirected-graph-with-unit-distance/) — 🟡 `Medium`
- [ ] **Problem 27**: [Shortest Path in Directed Acyclic Graph (DAG) using Topo Sort](https://takeuforward.org/data-structure/shortest-path-in-directed-acyclic-graph-dag/) — 🟡 `Medium`
- [ ] **Problem 28**: [Dijkstra's Algorithm using Priority Queue / Set](https://takeuforward.org/data-structure/dijkstras-algorithm-using-priority-queue-g-32/) — 🟡 `Medium`
- [ ] **Problem 29**: [Print Shortest Path in Weighted Undirected Graph (Dijkstra Parent array)](https://takeuforward.org/data-structure/g-35-print-shortest-path-dijkstras-algorithm/) — 🟡 `Medium`
- [ ] **Problem 30**: [Shortest Path in Binary Matrix (Maze BFS)](https://takeuforward.org/data-structure/g-36-shortest-distance-in-a-binary-maze/) — 🟡 `Medium`
- [ ] **Problem 31**: [Path with Minimum Effort (Dijkstra on 2D Matrix)](https://takeuforward.org/data-structure/g-37-path-with-minimum-effort/) — 🟡 `Medium`
- [ ] **Problem 32**: [Cheapest Flights Within K Stops](https://takeuforward.org/data-structure/g-38-cheapest-flights-within-k-stops/) — 🟡 `Medium`
- [ ] **Problem 33**: [Network Delay Time](https://takeuforward.org/data-structure/network-delay-time/) — 🟡 `Medium`
- [ ] **Problem 34**: [Number of Ways to Arrive at Destination](https://takeuforward.org/data-structure/g-40-number-of-ways-to-arrive-at-destination/) — 🟡 `Medium`
- [ ] **Problem 35**: [Minimum Multiplications to Reach End (Modulo BFS)](https://takeuforward.org/data-structure/g-39-minimum-multiplications-to-reach-end/) — 🟡 `Medium`
- [ ] **Problem 36**: [Bellman-Ford Algorithm (Negative Weights & Negative Cycle Detection)](https://takeuforward.org/data-structure/bellman-ford-algorithm-g-41/) — 🟡 `Medium`
- [ ] **Problem 37**: [Floyd-Warshall Algorithm (All-Pairs Shortest Path in O(V^3))](https://takeuforward.org/data-structure/floyd-warshall-algorithm-g-42/) — 🟡 `Medium`
- [ ] **Problem 38**: [Find the City with the Smallest Number of Neighbors at a Threshold Distance](https://takeuforward.org/data-structure/find-the-city-with-the-smallest-number-of-neighbors-at-a-threshold-distance-g-43/) — 🟡 `Medium`

### Step 15.5: Minimum Spanning Tree & Disjoint Set Union (10 Problems)

- [ ] **Problem 39**: [Prim's Algorithm for Minimum Spanning Tree (MST)](https://takeuforward.org/data-structure/prims-algorithm-minimum-spanning-tree-c-and-java-g-45/) — 🟡 `Medium`
- [ ] **Problem 40**: [Disjoint Set Union (DSU by Rank & Size with Path Compression)](https://takeuforward.org/data-structure/disjoint-set-union-by-rank-union-by-size-path-compression-g-46/) — 🟡 `Medium`
- [ ] **Problem 41**: [Kruskal's Algorithm for Minimum Spanning Tree (Edge list sort + DSU)](https://takeuforward.org/data-structure/kruskals-algorithm-minimum-spanning-tree-g-47/) — 🟡 `Medium`
- [ ] **Problem 42**: [Number of Operations to Make Network Connected](https://takeuforward.org/data-structure/number-of-operations-to-make-network-connected-dsu-g-49/) — 🟡 `Medium`
- [ ] **Problem 43**: [Most Stones Removed with Same Row or Column](https://takeuforward.org/data-structure/most-stones-removed-with-same-row-or-column-dsu-g-53/) — 🟡 `Medium`
- [ ] **Problem 44**: [Accounts Merge (String identifier grouping with DSU)](https://takeuforward.org/data-structure/accounts-merge-dsu-g-50/) — 🔴 `Hard`
- [ ] **Problem 45**: [Number of Islands II (Online Dynamic Island Grid with DSU)](https://takeuforward.org/data-structure/number-of-islands-ii-online-queries-dsu-g-51/) — 🔴 `Hard`
- [ ] **Problem 46**: [Making a Large Island (Flip at most one 0 to 1 with DSU)](https://takeuforward.org/data-structure/making-a-large-island-dsu-g-52/) — 🔴 `Hard`
- [ ] **Problem 47**: [Swim in Rising Water (Dijkstra / Binary Search + DSU)](https://takeuforward.org/data-structure/swim-in-rising-water/) — 🔴 `Hard`
- [ ] **Problem 48**: [Redundant Connection (Find cycle edge in tree via DSU)](https://takeuforward.org/data-structure/redundant-connection/) — 🟡 `Medium`

### Step 15.6: Other Advanced Graph Algorithms (5 Problems)

- [ ] **Problem 49**: [Bridges in Graph (Tarjan's Algorithm with Time of Insertion & Low Time)](https://takeuforward.org/data-structure/bridges-in-graph-using-tarjans-algorithm-g-55/) — 🔴 `Hard`
- [ ] **Problem 50**: [Articulation Points in Graph (Cut Vertices via Tarjan's Low Array)](https://takeuforward.org/data-structure/articulation-point-in-graph-g-56/) — 🔴 `Hard`
- [ ] **Problem 51**: [Strongly Connected Components (Kosaraju's Algorithm 3-Step)](https://takeuforward.org/data-structure/strongly-connected-components-kosarajus-algorithm-g-54/) — 🔴 `Hard`
- [ ] **Problem 52**: [Eulerian Circuit and Path in Directed & Undirected Graphs](https://takeuforward.org/graph/eulerian-path-and-circuit/) — 🔴 `Hard`
- [ ] **Problem 53**: [Tarjan's Algorithm for Strongly Connected Components in Single Pass](https://takeuforward.org/graph/tarjans-scc-algorithm/) — 🔴 `Hard`

---

## 💡 How to Add Solutions

To add a solution for any problem in this section:
1. Copy the prompt template from [`../AI_PROMPT_TEMPLATE.md`](../AI_PROMPT_TEMPLATE.md).
2. Generate the C++ solution with intuition, brute-force, better, and optimal implementations.
3. Save the solution as `NN-problem-slug.md` inside this folder and tick off the checklist box above!
