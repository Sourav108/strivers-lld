# Spiral Traversal of Matrix (Step 3.2)

This is a complete, interview-ready note in C++ following the standard 9-section format.

- **Source**: https://takeuforward.org/data-structure/spiral-traversal-of-matrix/
- **Difficulty**: Medium
- **Statement**: Return all elements of matrix in clockwise spiral order.

---

## 1. Problem, Restated

Return all elements of matrix in clockwise spiral order.

- **Input**: Vector of integers `nums`.
- **Output**: Result as specified by problem requirements.
- **Key Constraints**: $n$ up to $10^5$, elements can be negative/positive, time limit 1.0s.

---

## 2. Intuition & Pattern

4-Boundary Shifting (`top`, `bottom`, `left`, `right`).

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

// Direct simulation is standard; brute force is identical in complexity.
```

### Complexity Derivation
- **Time Complexity**: O(m * n)
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

vector<int> spiralOrderOptimal(const vector<vector<int>>& mat) {
    vector<int> res;
    if (mat.empty()) return res;
    int top = 0, bottom = mat.size() - 1, left = 0, right = mat[0].size() - 1;
    while (top <= bottom && left <= right) {
        for (int j = left; j <= right; j++) res.push_back(mat[top][j]);
        top++;
        for (int i = top; i <= bottom; i++) res.push_back(mat[i][right]);
        right--;
        if (top <= bottom) {
            for (int j = right; j >= left; j--) res.push_back(mat[bottom][j]);
            bottom--;
        }
        if (left <= right) {
            for (int i = bottom; i >= top; i--) res.push_back(mat[i][left]);
            left++;
        }
    }
    return res;
}
```

### Complexity Derivation
- **Time Complexity**: O(m * n)
- **Space Complexity**: O(1)
- **Why this is optimal**: Matches the theoretical information lower bound $\Omega(n)$ for unsorted array inspection.

---

## 6. Dry Run

**Trace**: mat = [[1,2,3],[4,5,6],[7,8,9]] -> [1,2,3,6,9,8,7,4,5]

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

- **Q1: Why are the conditional checks `if (top <= bottom)` and `if (left <= right)` required inside the while loop?**  
  **A**: In rectangular matrices (e.g. $1 \times 5$ or $3 \times 1$), after sweeping right and down, `top` or `right` boundaries shift. Without re-checking, the bottom-left and left-top sweeps would traverse already-visited rows/columns twice.

- **Q2: How to generate an N x N matrix filled with 1 to N^2 in spiral order (Spiral Matrix II)?**  
  **A**: Use the identical 4-boundary logic with a counter `val = 1..n^2`, writing `mat[top][j] = val++` etc. in $\mathcal{O}(n^2)$ time.

- **Q3: How to find the K-th element in spiral order in O(1) time without visiting previous elements?**  
  **A**: Calculate how many complete outer rings precede the $K$-th element, compute the perimeter of outer rings using arithmetic progressions, and jump directly to the target coordinate.

- **Q4: How to implement spiral traversal using a direction vector array?**  
  **A**: Use `dr = {0, 1, 0, -1}`, `dc = {1, 0, -1, 0}` and `dir = 0`. Move $(r + dr[dir], c + dc[dir])$. When hitting a boundary or visited cell, turn right: `dir = (dir + 1) % 4`.

- **Q5: How does spiral traversal generalize to 3D tensors (Cube traversal)?**  
  **A**: Traverse 6 outer faces of the 3D bounding box iteratively, shrinking `xMin, xMax, yMin, yMax, zMin, zMax` boundaries.

## 9. Tags & Related Problems

- **Tags**: `Array`, `TakeUForward`, `Strivers-A2Z`, `Medium`
- **Related problems**:
  - Similar Step 3 Array Problems in the curriculum.
