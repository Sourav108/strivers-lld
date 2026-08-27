# Sort Colors (0s, 1s, 2s) (Step 3.2)

This is a complete, interview-ready note in C++ following the standard 9-section format.

- **Source**: https://takeuforward.org/data-structure/sort-an-array-of-0s-1s-and-2s/
- **Difficulty**: Medium
- **Statement**: Sort an array consisting only of 0s, 1s, and 2s in-place in single pass.

---

## 1. Problem, Restated

Sort an array consisting only of 0s, 1s, and 2s in-place in single pass.

- **Input**: Vector of integers `nums`.
- **Output**: Result as specified by problem requirements.
- **Key Constraints**: $n$ up to $10^5$, elements can be negative/positive, time limit 1.0s.

---

## 2. Intuition & Pattern

Dutch National Flag (3-Way Partitioning with low, mid, high pointers).

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

void sortColorsBrute(vector<int>& nums) { sort(nums.begin(), nums.end()); }
```

### Complexity Derivation
- **Time Complexity**: O(n log n)
- **Space Complexity**: O(1)
- **Why it's not good enough**: For $n = 10^5$, polynomial time $\mathcal{O}(n^2)$ takes $\approx 10^{10}$ operations and triggers Time Limit Exceeded (TLE).

---

## 4. Approach 2 — Better

### Idea
Requires two passes.

### C++17 Code
```cpp
#include <vector>
#include <algorithm>
#include <climits>
#include <unordered_map>
#include <unordered_set>
using namespace std;

void sortColorsBetter(vector<int>& nums) {
    int c0 = 0, c1 = 0, c2 = 0;
    for (int x : nums) { if (x == 0) c0++; else if (x == 1) c1++; else c2++; }
    int idx = 0;
    while (c0--) nums[idx++] = 0;
    while (c1--) nums[idx++] = 1;
    while (c2--) nums[idx++] = 2;
}
```

### Complexity Derivation
- **Time Complexity**: O(2n) = O(n)
- **Space Complexity**: O(1)
- **Why it's still not optimal**: Requires two passes.

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

void sortColorsOptimal(vector<int>& nums) {
    int low = 0, mid = 0, high = (int)nums.size() - 1;
    while (mid <= high) {
        if (nums[mid] == 0) {
            swap(nums[low], nums[mid]);
            low++; mid++;
        } else if (nums[mid] == 1) {
            mid++;
        } else {
            swap(nums[mid], nums[high]);
            high--;
        }
    }
}
```

### Complexity Derivation
- **Time Complexity**: O(n)
- **Space Complexity**: O(1)
- **Why this is optimal**: Matches the theoretical information lower bound $\Omega(n)$ for unsorted array inspection.

---

## 6. Dry Run

**Trace**: nums = [2, 0, 2, 1, 1, 0] -> 3-way partition sorts to [0, 0, 1, 1, 2, 2]

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

- **Q1: Why do we NOT increment `mid` when swapping with `high` (`swap(nums[mid], nums[high])`)?**  
  **A**: Because the element swapped from `high` was previously unexamined; it could be 0, 1, or 2. We must keep `mid` at the same index to inspect it in the next iteration.

- **Q2: Why DO we increment `mid` when swapping with `low` (`swap(nums[low], nums[mid])`)?**  
  **A**: Because `low <= mid` and all elements before `mid` have already been processed. The element swapped from `low` is guaranteed to be 1, which is valid at `mid`.

- **Q3: How does Dutch National Flag generalize to 3-Way QuickSort (Fat Partitioning)?**  
  **A**: In QuickSort with many duplicate keys, 3-way partitioning divides the array into $[< \text{pivot}]$, $[== \text{pivot}]$, and $[> \text{pivot}]$. Recursion only recurses on the strictly smaller and strictly larger subarrays, reducing QuickSort from $\mathcal{O}(n^2)$ to $\mathcal{O}(n)$ on duplicate arrays.

- **Q4: How to generalize Dutch National Flag to 4 colors (0, 1, 2, 3)?**  
  **A**: Use 4 pointers (`p0, p1, p2, p3`) or execute two passes of 3-way partitioning in $\mathcal{O}(n)$ time and $\mathcal{O}(1)$ space.

- **Q5: How would you sort 0s, 1s, and 2s in a Singly Linked List?**  
  **A**: Create 3 dummy heads (`zeroHead, oneHead, twoHead`), iterate the list appending nodes to their respective dummy chains in $\mathcal{O}(n)$ time, and link `zeroTail->next = oneHead->next; oneTail->next = twoHead->next`.

## 9. Tags & Related Problems

- **Tags**: `Array`, `TakeUForward`, `Strivers-A2Z`, `Medium`
- **Related problems**:
  - Similar Step 3 Array Problems in the curriculum.
