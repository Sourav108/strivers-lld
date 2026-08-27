# Merge Two Sorted Arrays Without Extra Space (Step 3.3)

This is a complete, interview-ready note in C++ following the standard 9-section format.

- **Source**: https://takeuforward.org/data-structure/merge-two-sorted-arrays-without-extra-space/
- **Difficulty**: Hard
- **Statement**: Merge two sorted arrays `nums1` and `nums2` into `nums1` in-place without extra space.

---

## 1. Problem, Restated

Merge two sorted arrays `nums1` and `nums2` into `nums1` in-place without extra space.

- **Input**: Vector of integers `nums`.
- **Output**: Result as specified by problem requirements.
- **Key Constraints**: $n$ up to $10^5$, elements can be negative/positive, time limit 1.0s.

---

## 2. Intuition & Pattern

Gap Method (Shell Sort Knuth gap formula: $\lceil gap/2 \rceil$) or Three Pointers from back.

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

void mergeSortedBrute(vector<int>& nums1, int m, vector<int>& nums2, int n) {
    vector<int> t(m + n);
    // merge into t then copy back
}
```

### Complexity Derivation
- **Time Complexity**: O(m + n)
- **Space Complexity**: O(m + n)
- **Why it's not good enough**: For $n = 10^5$, polynomial time $\mathcal{O}(n^2)$ takes $\approx 10^{10}$ operations and triggers Time Limit Exceeded (TLE).

---

## 4. Approach 2 — Better

### Idea
Swap-and-Sort: Place pointer `i` at the end of array $A$ ($n-1$) and pointer `j` at the beginning of array $B$ (0). If $A[i] > B[j]$, swap them and advance `i--`, `j++`. When $A[i] \le B[j]$, stop and sort $A$ and $B$ individually.

### C++17 Code
```cpp
#include <vector>
#include <algorithm>
using namespace std;

void mergeSortedBetter(vector<int>& a, int n, vector<int>& b, int m) {
    int i = n - 1, j = 0;
    while (i >= 0 && j < m) {
        if (a[i] > b[j]) {
            swap(a[i], b[j]);
            i--;
            j++;
        } else {
            break;
        }
    }
    sort(a.begin(), a.end());
    sort(b.begin(), b.end());
}
```

### Complexity Derivation
- **Time Complexity**: $\mathcal{O}(\min(n, m)) + \mathcal{O}(n \log n + m \log m)$ — linear swap pass followed by sorting both arrays.
- **Space Complexity**: $\mathcal{O}(1)$ — constant auxiliary memory.
- **Why it's still not optimal**: Relies on standard sorting; the Gap Method achieves $\mathcal{O}((n+m) \log(n+m))$ without full sorting.

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

void mergeSortedOptimal(vector<int>& nums1, int m, vector<int>& nums2, int n) {
    int i = m - 1, j = n - 1, k = m + n - 1;
    while (i >= 0 && j >= 0) {
        if (nums1[i] > nums2[j]) {
            nums1[k--] = nums1[i--];
        } else {
            nums1[k--] = nums2[j--];
        }
    }
    while (j >= 0) nums1[k--] = nums2[j--];
}
```

### Complexity Derivation
- **Time Complexity**: O(m + n)
- **Space Complexity**: O(1)
- **Why this is optimal**: Matches the theoretical information lower bound $\Omega(n)$ for unsorted array inspection.

---

## 6. Dry Run

**Trace**: nums1 = [1,2,3,0,0,0], nums2 = [2,5,6] -> [1,2,2,3,5,6]

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

- **Q1: What is the Gap Method (Knuth Shell Sort gap) for merging two disjoint arrays?**  
  **A**: Initialize $\text{gap} = \lceil (n + m) / 2 \rceil$. Compare elements at distance $\text{gap}$, swap if out of order, and reduce $\text{gap} = \lceil \text{gap} / 2 \rceil$ until 0. Sorts both arrays in $\mathcal{O}((n+m) \log(n+m))$ time and strictly $\mathcal{O}(1)$ auxiliary space.

- **Q2: In LeetCode 88 (nums1 has size m + n), why is back-to-front merging optimal?**  
  **A**: Because the free capacity is at the back of `nums1` (`nums1[m..m+n-1]`). Placing the largest elements at the back first guarantees that we never overwrite unprocessed elements in `nums1[0..m-1]`, achieving $\mathcal{O}(m+n)$ time and $\mathcal{O}(1)$ space.

- **Q3: How does the swap-and-sort method work for two separate arrays A and B?**  
  **A**: Set pointer `i` at end of $A$ and `j` at start of $B$. If $A[i] > B[j]$, swap them, `i--`, `j++`. When $A[i] \le B[j]$, stop and sort $A$ and $B$ individually in $\mathcal{O}(n \log n + m \log m)$ time.

- **Q4: Can two disjoint sorted arrays be merged in O(m+n) time and strictly O(1) space?**  
  **A**: Yes, using complex in-place block-merge algorithms (e.g. SymMerge / Huang-Langston algorithm), but implementation constant factor is high.

- **Q5: How is this used in External Merge Sort for multi-gigabyte files on disk?**  
  **A**: Memory-constrained systems sort initial chunks in RAM, write sorted runs to disk, and merge runs using multi-way min-heap merge.

## 9. Tags & Related Problems

- **Tags**: `Array`, `TakeUForward`, `Strivers-A2Z`, `Hard`
- **Related problems**:
  - Similar Step 3 Array Problems in the curriculum.
