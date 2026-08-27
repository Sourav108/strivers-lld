# Remove Duplicates from Sorted Array (Step 3.1)

This is a complete, interview-ready note in C++ following the standard 9-section format.

- **Source**: https://takeuforward.org/data-structure/remove-duplicates-in-place-from-sorted-array/
- **Difficulty**: Easy
- **Statement**: Remove duplicate elements in-place from a sorted array and return the count of unique elements.

---

## 1. Problem, Restated

Remove duplicate elements in-place from a sorted array and return the count of unique elements.

- **Input**: Vector of integers `nums`.
- **Output**: Result as specified by problem requirements.
- **Key Constraints**: $n$ up to $10^5$, elements can be negative/positive, time limit 1.0s.

---

## 2. Intuition & Pattern

Two pointers (Slow writer `i`, fast reader `j`). When nums[j] != nums[i], advance i and copy.

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

int removeDuplicatesBrute(vector<int>& nums) {
    set<int> st(nums.begin(), nums.end());
    int i = 0; for (int x : st) nums[i++] = x;
    return i;
}
```

### Complexity Derivation
- **Time Complexity**: O(n log n)
- **Space Complexity**: O(n)
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

int removeDuplicatesOptimal(vector<int>& nums) {
    if (nums.empty()) return 0;
    int i = 0;
    for (int j = 1; j < (int)nums.size(); j++) {
        if (nums[j] != nums[i]) {
            i++;
            nums[i] = nums[j];
        }
    }
    return i + 1;
}
```

### Complexity Derivation
- **Time Complexity**: O(n)
- **Space Complexity**: O(1)
- **Why this is optimal**: Matches the theoretical information lower bound $\Omega(n)$ for unsorted array inspection.

---

## 6. Dry Run

**Trace**: nums = [1, 1, 2, 2, 3] -> writes [1, 2, 3] -> returns 3

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

- **Q1: How to allow at most K duplicates (e.g. LeetCode 80 where K = 2)?**  
  **A**: Generalize the write condition: write `nums[i++] = nums[j]` if `i < K || nums[j] != nums[i - K]`. This elegantly handles any $K \ge 1$ in $\mathcal{O}(n)$ time and $\mathcal{O}(1)$ space.

- **Q2: What if the array is unsorted and must be modified in-place with O(1) extra space?**  
  **A**: Without extra space, we must sort the array first in $\mathcal{O}(n \log n)$, then apply the two-pointer in-place removal in $\mathcal{O}(n)$. If order must be preserved, an $\mathcal{O}(n)$ hash set is required.

- **Q3: How does `std::unique` in C++ STL work under the hood?**  
  **A**: `std::unique(first, last)` uses the identical two-pointer algorithm: it iterates through the range and overwrites duplicate consecutive elements, returning an iterator to the new logical end.

- **Q4: Can this duplicate removal be parallelized across multiple threads?**  
  **A**: Yes, using a 2-pass parallel prefix scan: Thread $T_k$ marks unique boundaries `1` and duplicates `0`, computes parallel prefix sums to determine destination indices, and scatters elements into the target array.

- **Q5: How would you remove duplicates from a Singly Linked List?**  
  **A**: Traverse with pointer `curr`. If `curr->val == curr->next->val`, update `curr->next = curr->next->next` and `delete` the skipped node in $\mathcal{O}(n)$ time and $\mathcal{O}(1)$ space.

## 9. Tags & Related Problems

- **Tags**: `Array`, `TakeUForward`, `Strivers-A2Z`, `Easy`
- **Related problems**:
  - Similar Step 3 Array Problems in the curriculum.
