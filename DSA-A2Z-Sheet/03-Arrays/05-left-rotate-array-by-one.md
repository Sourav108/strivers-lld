# Left Rotate Array by One (Step 3.1)

This is a complete, interview-ready note in C++ following the standard 9-section format.

- **Source**: https://takeuforward.org/data-structure/left-rotate-the-array-by-one/
- **Difficulty**: Easy
- **Statement**: Shift array elements left by 1 position with wrap-around.

---

## 1. Problem, Restated

Shift array elements left by 1 position with wrap-around.

- **Input**: Vector of integers `nums`.
- **Output**: Result as specified by problem requirements.
- **Key Constraints**: $n$ up to $10^5$, elements can be negative/positive, time limit 1.0s.

---

## 2. Intuition & Pattern

Save first element, shift all elements left by one, place saved element at the end.

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

void rotateByOneBrute(vector<int>& nums) {
    int n = nums.size(); if (n <= 1) return;
    vector<int> t(n);
    for (int i = 1; i < n; i++) t[i-1] = nums[i];
    t[n-1] = nums[0]; nums = t;
}
```

### Complexity Derivation
- **Time Complexity**: O(n)
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

void rotateByOneOptimal(vector<int>& nums) {
    if (nums.size() <= 1) return;
    int temp = nums[0], n = nums.size();
    for (int i = 0; i < n - 1; i++) nums[i] = nums[i + 1];
    nums[n - 1] = temp;
}
```

### Complexity Derivation
- **Time Complexity**: O(n)
- **Space Complexity**: O(1)
- **Why this is optimal**: Matches the theoretical information lower bound $\Omega(n)$ for unsorted array inspection.

---

## 6. Dry Run

**Trace**: nums = [1, 2, 3, 4, 5] -> shifts to [2, 3, 4, 5, 1]

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

- **Q1: Why does calling `rotateByOne` K times take O(n * K) and how does Reversal optimize it?**  
  **A**: Calling it $K$ times moves $n$ elements $K$ times (total $n \cdot K$ memory writes). The Reversal Algorithm achieves rotation by $K$ in exactly $2n$ element swaps ($\mathcal{O}(n)$ time).

- **Q2: How to right rotate by one position instead of left rotate?**  
  **A**: Save `temp = nums[n-1]`, iterate backward from $n-1$ down to 1 setting `nums[i] = nums[i-1]`, and set `nums[0] = temp`.

- **Q3: How does C++ STL `std::rotate` implement rotation?**  
  **A**: `std::rotate(first, middle, last)` chooses between cyclic permutation (for random access iterators), reversal algorithm, or block swap depending on iterator category.

- **Q4: How to rotate a matrix boundary by one position clockwise?**  
  **A**: Treat the 4 outer edges as a 1D unrolled array, perform a 1-step circular shift, and write the values back to the top, right, bottom, and left boundaries.

- **Q5: What if the array is stored on a disk file too large to fit in memory?**  
  **A**: Use memory-mapped files (`mmap`) or read blocks into a small RAM buffer, write the shifted blocks sequentially, and append the first element to the end block.

## 9. Tags & Related Problems

- **Tags**: `Array`, `TakeUForward`, `Strivers-A2Z`, `Easy`
- **Related problems**:
  - Similar Step 3 Array Problems in the curriculum.
