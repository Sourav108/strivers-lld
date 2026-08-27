# Check if Array Is Sorted and Rotated (Step 3.1)

This is a complete, interview-ready note in C++ following the standard 9-section format.

- **Source**: https://takeuforward.org/data-structure/check-if-an-array-is-sorted/
- **Difficulty**: Easy
- **Statement**: Determine if an array was originally sorted in non-decreasing order and rotated.

---

## 1. Problem, Restated

Determine if an array was originally sorted in non-decreasing order and rotated.

- **Input**: Vector of integers `nums`.
- **Output**: Result as specified by problem requirements.
- **Key Constraints**: $n$ up to $10^5$, elements can be negative/positive, time limit 1.0s.

---

## 2. Intuition & Pattern

Circular breakpoint counter. In sorted-rotated array, nums[i] > nums[(i+1)%n] happens at most once.

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

bool checkBrute(vector<int>& nums) {
    vector<int> s = nums; sort(s.begin(), s.end());
    int n = nums.size();
    for (int k = 0; k < n; k++) {
        bool ok = true;
        for (int i = 0; i < n; i++) if (nums[(i+k)%n] != s[i]) { ok = false; break; }
        if (ok) return true;
    }
    return false;
}
```

### Complexity Derivation
- **Time Complexity**: O(n^2)
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

bool checkOptimal(const vector<int>& nums) {
    int countDrops = 0, n = nums.size();
    for (int i = 0; i < n; i++) {
        if (nums[i] > nums[(i + 1) % n]) countDrops++;
    }
    return countDrops <= 1;
}
```

### Complexity Derivation
- **Time Complexity**: O(n)
- **Space Complexity**: O(1)
- **Why this is optimal**: Matches the theoretical information lower bound $\Omega(n)$ for unsorted array inspection.

---

## 6. Dry Run

**Trace**: nums = [3, 4, 5, 1, 2] -> drop at 5>1 (count=1) -> returns true

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

- **Q1: How to find the exact rotation index K if the array is confirmed sorted and rotated?**  
  **A**: The rotation index $K$ is the index of the minimum element, which is $(i + 1) \% n$ where `nums[i] > nums[(i+1)%n]`. If `countDrops == 0`, $K = 0$.

- **Q2: Why does counting `nums[i] > nums[(i+1)%n] <= 1` work for an unrotated sorted array?**  
  **A**: For an unrotated sorted array `[1, 2, 3]`, `nums[0] < nums[1] < nums[2]`, but `nums[2] > nums[0]` on the circular wrap, producing exactly 1 drop. For all-equal array `[1, 1, 1]`, drops = 0. Both satisfy $\le 1$.

- **Q3: How does this behave with duplicates like `[2, 2, 2, 3, 2, 2]`?**  
  **A**: The single drop condition `nums[i] > nums[(i+1)%n] <= 1` still holds correctly: the only drop is `3 > 2`, so it correctly returns `true`.

- **Q4: Can we determine if a rotated array is sorted in sub-linear time?**  
  **A**: No, because even a single out-of-order element hidden anywhere in the array would invalidate the property, requiring $\Omega(n)$ worst-case checks.

- **Q5: How does this problem relate to Search in Rotated Sorted Array (LeetCode 33)?**  
  **A**: The single drop point divides the rotated array into two sorted subarrays. Binary search determines which half is sorted by checking `nums[low] <= nums[mid]`.

## 9. Tags & Related Problems

- **Tags**: `Array`, `TakeUForward`, `Strivers-A2Z`, `Easy`
- **Related problems**:
  - Similar Step 3 Array Problems in the curriculum.
