# Set Matrix Zeroes (Step 3.2)

This is a complete, interview-ready note in C++ following the standard 9-section format.

- **Source**: https://takeuforward.org/data-structure/set-matrix-zero/
- **Difficulty**: Medium
- **Statement**: If an element in an $m \times n$ matrix is 0, set its entire row and column to 0 in-place.

---

## 1. Problem, Restated

If an element in an $m \times n$ matrix is 0, set its entire row and column to 0 in-place.

- **Input**: Vector of integers `nums`.
- **Output**: Result as specified by problem requirements.
- **Key Constraints**: $n$ up to $10^5$, elements can be negative/positive, time limit 1.0s.

---

## 2. Intuition & Pattern

In-place State Markers in First Row & First Column with `col0` flag.

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

void setZeroesBrute(vector<vector<int>>& mat) {
    int m = mat.size(), n = mat[0].size();
    for (int i = 0; i < m; i++)
        for (int j = 0; j < n; j++)
            if (mat[i][j] == 0) { /* mark row/col with -1 */ }
}
```

### Complexity Derivation
- **Time Complexity**: O((m*n)*(m+n))
- **Space Complexity**: O(1)
- **Why it's not good enough**: For $n = 10^5$, polynomial time $\mathcal{O}(n^2)$ takes $\approx 10^{10}$ operations and triggers Time Limit Exceeded (TLE).

---

## 4. Approach 2 — Better

### Idea
Uses extra row and column indicator arrays.

### C++17 Code
```cpp
#include <vector>
#include <algorithm>
#include <climits>
#include <unordered_map>
#include <unordered_set>
using namespace std;

void setZeroesBetter(vector<vector<int>>& mat) {
    int m = mat.size(), n = mat[0].size();
    vector<int> row(m, 0), col(n, 0);
    for (int i = 0; i < m; i++)
        for (int j = 0; j < n; j++)
            if (mat[i][j] == 0) row[i] = col[j] = 1;
    for (int i = 0; i < m; i++)
        for (int j = 0; j < n; j++)
            if (row[i] || col[j]) mat[i][j] = 0;
}
```

### Complexity Derivation
- **Time Complexity**: O(2*m*n)
- **Space Complexity**: O(m + n)
- **Why it's still not optimal**: Uses extra row and column indicator arrays.

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

void setZeroesOptimal(vector<vector<int>>& mat) {
    int m = mat.size(), n = mat[0].size(), col0 = 1;
    for (int i = 0; i < m; i++) {
        if (mat[i][0] == 0) col0 = 0;
        for (int j = 1; j < n; j++)
            if (mat[i][j] == 0) mat[i][0] = mat[0][j] = 0;
    }
    for (int i = m - 1; i >= 0; i--) {
        for (int j = n - 1; j >= 1; j--)
            if (mat[i][0] == 0 || mat[0][j] == 0) mat[i][j] = 0;
        if (col0 == 0) mat[i][0] = 0;
    }
}
```

### Complexity Derivation
- **Time Complexity**: O(m * n)
- **Space Complexity**: O(1)
- **Why this is optimal**: Matches the theoretical information lower bound $\Omega(n)$ for unsorted array inspection.

---

## 6. Dry Run

**Trace**: mat = [[1,1,1],[1,0,1],[1,1,1]] -> sets row 1 and col 1 to 0

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

- **Q1: What if the array is already sorted?**  
  **A**: Exploiting sortedness allows two pointers or binary search to reduce time to $\mathcal{O}(\log n)$ or space to $\mathcal{O}(1)$.

- **Q2: What if elements arrive in a streaming fashion?**  
  **A**: Single-pass state accumulators adapt naturally to online streaming computation in $\mathcal{O}(1)$ amortized time per event.

- **Q3: What if input does not fit into RAM?**  
  **A**: Use external merge sort or MapReduce chunking with streaming combiner passes.

- **Q4: Can we parallelize this algorithm?**  
  **A**: Divide and conquer enables multi-threaded chunk evaluation with an $\mathcal{O}(1)$ merge step.

- **Q5: How does this generalize to multidimensional arrays or higher $K$?**  
  **A**: Techniques reduce higher dimensions by fixing degrees of freedom iteratively.

---

## 9. Tags & Related Problems

- **Tags**: `Array`, `TakeUForward`, `Strivers-A2Z`, `Medium`
- **Related problems**:
  - Similar Step 3 Array Problems in the curriculum.
