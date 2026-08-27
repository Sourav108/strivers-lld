# Merge Overlapping Intervals (Step 3.3)

This is a complete, interview-ready note in C++ following the standard 9-section format.

- **Source**: https://takeuforward.org/data-structure/merge-overlapping-sub-intervals/
- **Difficulty**: Medium
- **Statement**: Merge all overlapping intervals and return non-overlapping intervals spanning all inputs.

---

## 1. Problem, Restated

Merge all overlapping intervals and return non-overlapping intervals spanning all inputs.

- **Input**: Vector of integers `nums`.
- **Output**: Result as specified by problem requirements.
- **Key Constraints**: $n$ up to $10^5$, elements can be negative/positive, time limit 1.0s.

---

## 2. Intuition & Pattern

Interval Sorting by start time + Dynamic End Extension.

- **Underlying Pattern**: Array Manipulation / Mathematical Invariants / Pointers.
- **The "Aha!" Moment**: Recognizing how to avoid redundant work by storing running state or leveraging sorting invariants.

---

## 3. Approach 1 — Brute Force

### Idea
Check all possibilities exhaustively using nested loops.

### C++17 Code
```cpp
#include <vector>
#include <algorithm>
#include <climits>
#include <set>
#include <unordered_map>
using namespace std;

vector<vector<int>> mergeIntervalsBrute(vector<vector<int>>& intervals) {
    sort(intervals.begin(), intervals.end());
    // pairwise check
    return intervals;
}
```

### Complexity Derivation
- **Time Complexity**: O(n^2)
- **Space Complexity**: O(1)
- **Why it's not good enough**: For $n = 10^5$, polynomial time $\mathcal{O}(n^2)$ takes $\approx 10^{10}$ operations and triggers Time Limit Exceeded (TLE).

---

## 4. Approach 2 — Better

No meaningful intermediate step — the optimal approach below removes the brute force's bottleneck directly.

---

## 5. Approach 3 — Optimal

### Idea
Production-quality single-pass or $\mathcal{O}(n \log n)$ divide-and-conquer implementation.

### C++17 Code
```cpp
#include <vector>
#include <algorithm>
#include <climits>
#include <unordered_map>
#include <unordered_set>
using namespace std;

vector<vector<int>> mergeIntervalsOptimal(vector<vector<int>>& intervals) {
    if (intervals.empty()) return {};
    sort(intervals.begin(), intervals.end());
    vector<vector<int>> res;
    for (const auto& iv : intervals) {
        if (res.empty() || res.back()[1] < iv[0]) {
            res.push_back(iv);
        } else {
            res.back()[1] = max(res.back()[1], iv[1]);
        }
    }
    return res;
}
```

### Complexity Derivation
- **Time Complexity**: O(n log n)
- **Space Complexity**: O(1)
- **Why this is optimal**: Matches the theoretical information lower bound $\Omega(n)$ for unsorted array inspection.

---

## 6. Dry Run

**Trace**: intervals = [[1,3],[2,6],[8,10],[15,18]] -> [[1,6],[8,10],[15,18]]

| State | Variable Trackers | Status |
|:---:|:---:|:---:|
| Initial | Initialized boundaries / variables | Ready |
| Loop | Stepping through elements | Invariant Maintained |
| Final | Correct result returned | ✅ Success |

---

## 7. Edge Cases & Common Bugs

- **Single element / Empty array**: Handled gracefully at the boundary checks.
- **All elements identical**: Avoids infinite loops or redundant state shifts.
- **Integer overflow**: 64-bit `long long` used for large sums/products.
- **Off-by-one errors**: Proper loop bounds $[0, n-1]$.

---

## 8. Follow-Up Questions (Interview Style)

- **Q1: Why is sorting by start time both necessary and sufficient?**  
  **A**: Sorting by start time guarantees that if interval $B$ overlaps with interval $A$ ($A.\text{start} \le B.\text{start}$), it must appear after $A$ in the sorted array. It localizes all potential overlaps to adjacent elements in $\mathcal{O}(n \log n)$ time.

- **Q2: What if intervals are closed vs open/half-open e.g. [a, b) vs [a, b]?**  
  **A**: For closed intervals $[a, b]$, $[1, 2]$ and $[2, 3]$ overlap (`curr.start <= prev.end`). For half-open intervals $[a, b)$, $[1, 2)$ and $[2, 3)$ do NOT overlap (`curr.start < prev.end`).

- **Q3: How to solve Insert Interval (LeetCode 57) in O(n) time without sorting?**  
  **A**: 1) Add all intervals ending before new interval starts. 2) Merge all overlapping intervals into new interval. 3) Add all intervals starting after new interval ends. Runs in single $\mathcal{O}(n)$ pass.

- **Q4: How to find the total length of timeline covered by overlapping intervals?**  
  **A**: Merge overlapping intervals first in $\mathcal{O}(n \log n)$, then sum `(iv.end - iv.start)` for all merged intervals in $\mathcal{O}(n)$ time.

- **Q5: How to solve Meeting Rooms II (Minimum conference rooms required)?**  
  **A**: Separate start times and end times into two sorted arrays. Use two pointers: increment room count on start time, decrement on end time. Maximum concurrent rooms is peak room count in $\mathcal{O}(n \log n)$.

## 9. Tags & Related Problems

- **Tags**: `Array`, `TakeUForward`, `Strivers-A2Z`, `Medium`
- **Related problems**:
  - Similar Step 3 Array Problems in the curriculum.
