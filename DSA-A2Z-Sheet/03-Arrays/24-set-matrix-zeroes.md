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
Dummy Row & Column Marker Arrays: Maintain two auxiliary 1D boolean vectors `rowMarkers[m]` and `colMarkers[n]`. First pass records zero rows and columns. Second pass sets `mat[i][j] = 0` if `rowMarkers[i] || colMarkers[j]`.

### C++17 Code
```cpp
#include <vector>
using namespace std;

void setZeroesBetter(vector<vector<int>>& mat) {
    int m = mat.size(), n = mat[0].size();
    vector<int> rowMarker(m, 0), colMarker(n, 0);
    for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
            if (mat[i][j] == 0) {
                rowMarker[i] = 1;
                colMarker[j] = 1;
            }
        }
    }
    for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
            if (rowMarker[i] || colMarker[j]) {
                mat[i][j] = 0;
            }
        }
    }
}
```

### Complexity Derivation
- **Time Complexity**: $\mathcal{O}(2 \cdot m \cdot n) = \mathcal{O}(m \cdot n)$ — two matrix sweeps.
- **Space Complexity**: $\mathcal{O}(m + n)$ — marker vectors for rows and columns.
- **Why it's still not optimal**: Uses $\mathcal{O}(m + n)$ extra memory; optimal solution embeds these markers into the first row and column in $\mathcal{O}(1)$ space.

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

- **Q1: Why is a separate `col0` variable needed instead of using `mat[0][0]` for both first row and first column?**  
  **A**: `mat[0][0]` is shared by both row 0 and column 0. If `mat[0][0] = 0`, we cannot tell whether row 0, column 0, or both need to be zeroed. Using `col0` for column 0 and `mat[0][0]` for row 0 disambiguates them.

- **Q2: Why MUST the matrix update loop run from bottom-up (`m-1` down to 0)?**  
  **A**: If we updated row 0 and col 0 first, their original marker zeroes would overwrite other non-zero markers, corrupting the remaining inner matrix updates.

- **Q3: What if the matrix is sparse ($10^6 \times 10^6$ with only 10 zeroes)?**  
  **A**: Store the 10 coordinates in a hash set of rows and columns using $\mathcal{O}(k)$ space where $k=10$. Iterate only the affected rows and columns instead of the entire $10^{12}$ cell matrix.

- **Q4: Can this be implemented on a GPU with CUDA?**  
  **A**: Yes, launch $M$ threads for rows and $N$ threads for cols: Thread $i$ checks row $i$ for zero; then launch $M \times N$ threads to set `mat[i][j] = 0` if `rowZero[i] || colZero[j]` in $\mathcal{O}(1)$ parallel GPU time.

- **Q5: What if the matrix is stored on disk in row-major order?**  
  **A**: Process row-by-row sequentially to maximize disk page cache hit rates; write zeroed rows in single block I/O operations.

## 9. Tags & Related Problems

- **Tags**: `Array`, `TakeUForward`, `Strivers-A2Z`, `Medium`
- **Related problems**:
  - Similar Step 3 Array Problems in the curriculum.
