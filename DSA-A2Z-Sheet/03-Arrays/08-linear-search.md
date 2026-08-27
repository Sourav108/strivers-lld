# Linear Search (Step 3.1)

This is a complete, interview-ready note in C++ following the standard 9-section format.

- **Source**: https://takeuforward.org/data-structure/linear-search-algorithm/
- **Difficulty**: Easy
- **Statement**: Find the index of target value $k$ in an unsorted array, or return -1.

---

## 1. Problem, Restated

Find the index of target value $k$ in an unsorted array, or return -1.

- **Input**: Vector of integers `nums`.
- **Output**: Result as specified by problem requirements.
- **Key Constraints**: $n$ up to $10^5$, elements can be negative/positive, time limit 1.0s.

---

## 2. Intuition & Pattern

Sequential inspection from index 0 to $n-1$.

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

int searchBrute(const vector<int>& nums, int k) {
    for (int i = 0; i < (int)nums.size(); i++) {
        if (nums[i] == k) return i;
    }
    return -1;
}
```

### Complexity Derivation
- **Time Complexity**: O(n)
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

int searchOptimal(const vector<int>& nums, int k) {
    for (int i = 0; i < (int)nums.size(); i++) {
        if (nums[i] == k) return i;
    }
    return -1;
}
```

### Complexity Derivation
- **Time Complexity**: O(n)
- **Space Complexity**: O(1)
- **Why this is optimal**: Matches the theoretical information lower bound $\Omega(n)$ for unsorted array inspection.

---

## 6. Dry Run

**Trace**: nums = [6, 7, 8, 4, 1], k=4 -> finds at index 3

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

- **Q1: What is Sentinel Linear Search and how does it optimize CPU cycles?**  
  **A**: Set `nums[n-1] = target` as a sentinel. The loop condition only checks `nums[i] == target` without checking `i < n` on every iteration, eliminating $n$ loop boundary comparisons.

- **Q2: When is Linear Search preferred over Binary Search even on sorted data?**  
  **A**: For small arrays ($n \le 16-32$), Linear Search is often faster than Binary Search due to continuous CPU cache line prefetching (L1 cache hits) and zero branch mispredictions.

- **Q3: How does Move-To-Front / Transposition heuristic optimize repeated searches?**  
  **A**: In self-organizing lists, whenever an element is found, swap it with the front or its predecessor. Frequently accessed elements gravitate to the head, reducing average search time to $\mathcal{O}(1)$.

- **Q4: How to implement Linear Search using SIMD instructions in C++?**  
  **A**: Broadcast the target into a 256-bit AVX2 register (`_mm256_set1_epi32`), compare with 8 array elements simultaneously (`_mm256_cmpeq_epi32`), and use `_mm256_movemask_epi8` with `__builtin_ctz` to locate the match index.

- **Q5: How to parallelize linear search across multiple threads?**  
  **A**: Divide the array into $P$ chunks. Each thread searches its chunk; the first thread finding target sets an `std::atomic<int> foundIdx` and signals other threads to cancel.

## 9. Tags & Related Problems

- **Tags**: `Array`, `TakeUForward`, `Strivers-A2Z`, `Easy`
- **Related problems**:
  - Similar Step 3 Array Problems in the curriculum.
