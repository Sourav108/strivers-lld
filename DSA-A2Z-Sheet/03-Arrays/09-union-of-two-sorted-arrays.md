# Union of Two Sorted Arrays (Step 3.1)

This is a complete, interview-ready note in C++ following the standard 9-section format.

- **Source**: https://takeuforward.org/data-structure/union-of-two-sorted-arrays/
- **Difficulty**: Easy
- **Statement**: Return a sorted array containing the distinct union of elements from two sorted arrays.

---

## 1. Problem, Restated

Return a sorted array containing the distinct union of elements from two sorted arrays.

- **Input**: Vector of integers `nums`.
- **Output**: Result as specified by problem requirements.
- **Key Constraints**: $n$ up to $10^5$, elements can be negative/positive, time limit 1.0s.

---

## 2. Intuition & Pattern

Two Pointers Merge. Traverse both arrays in lockstep, picking the smaller element and appending if distinct.

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

vector<int> unionBrute(const vector<int>& a, const vector<int>& b) {
    set<int> s(a.begin(), a.end());
    s.insert(b.begin(), b.end());
    return vector<int>(s.begin(), s.end());
}
```

### Complexity Derivation
- **Time Complexity**: O((n+m) log(n+m))
- **Space Complexity**: O(n+m)
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

vector<int> unionOptimal(const vector<int>& a, const vector<int>& b) {
    vector<int> res;
    int i = 0, j = 0, n = a.size(), m = b.size();
    while (i < n && j < m) {
        if (a[i] <= b[j]) {
            if (res.empty() || res.back() != a[i]) res.push_back(a[i]);
            i++;
        } else {
            if (res.empty() || res.back() != b[j]) res.push_back(b[j]);
            j++;
        }
    }
    while (i < n) {
        if (res.empty() || res.back() != a[i]) res.push_back(a[i]);
        i++;
    }
    while (j < m) {
        if (res.empty() || res.back() != b[j]) res.push_back(b[j]);
        j++;
    }
    return res;
}
```

### Complexity Derivation
- **Time Complexity**: O(n + m)
- **Space Complexity**: O(n + m)
- **Why this is optimal**: Matches the theoretical information lower bound $\Omega(n)$ for unsorted array inspection.

---

## 6. Dry Run

**Trace**: a = [1, 2, 3, 4, 5], b = [2, 3, 4, 4, 5, 6] -> union = [1, 2, 3, 4, 5, 6]

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

- **Q1: How to adapt this algorithm to find the INTERSECTION of two sorted arrays?**  
  **A**: Advance `i` when `a[i] < b[j]`, advance `j` when `b[j] < a[i]`. When `a[i] == b[j]`, append to result (if not duplicate) and advance both `i++` and `j++` in $\mathcal{O}(n + m)$ time.

- **Q2: What if one array has size n = 10^7 and the other has size m = 5?**  
  **A**: Two-pointer takes $\mathcal{O}(n + m) = 10^7$ operations. Instead, iterate through the $m=5$ elements and Binary Search each inside the $10^7$ array in $\mathcal{O}(m \log n) \approx 5 \times 24 \approx 120$ operations — $80,000\times$ faster!

- **Q3: How to find the Symmetric Difference (A XOR B) of two sorted arrays?**  
  **A**: When `a[i] < b[j]`, take `a[i++]`. When `b[j] < a[i]`, take `b[j++]`. When `a[i] == b[j]`, skip both `i++` and `j++` without adding either.

- **Q4: How to handle multi-set union (preserving duplicate counts)?**  
  **A**: If element $x$ appears 3 times in $A$ and 2 times in $B$, append $x$ exactly $\max(3, 2) = 3$ times in union, or $\min(3, 2) = 2$ times in intersection.

- **Q5: How is this used in database query engines (Sort-Merge Join)?**  
  **A**: Relational database query engines use Sort-Merge Join to combine two indexed table columns in $\mathcal{O}(n + m)$ time using the identical two-pointer merge traversal.

## 9. Tags & Related Problems

- **Tags**: `Array`, `TakeUForward`, `Strivers-A2Z`, `Easy`
- **Related problems**:
  - Similar Step 3 Array Problems in the curriculum.
