# Next Permutation (Step 3.2)

This is a complete, interview-ready note in C++ following the standard 9-section format.

- **Source**: https://takeuforward.org/data-structure/next_permutation-find-next-lexicographically-greater-permutation/
- **Difficulty**: Medium
- **Statement**: Rearrange numbers into lexicographically next greater permutation in-place.

---

## 1. Problem, Restated

Rearrange numbers into lexicographically next greater permutation in-place.

- **Input**: Vector of integers `nums`.
- **Output**: Result as specified by problem requirements.
- **Key Constraints**: $n$ up to $10^5$, elements can be negative/positive, time limit 1.0s.

---

## 2. Intuition & Pattern

Single Pass 3-Step: Find pivot (nums[i] < nums[i+1]), swap with smallest greater element to right, reverse suffix.

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

void nextPermutationBrute(vector<int>& nums) {
    // Generate all n! permutations, sort lexicographically, find next
}
```

### Complexity Derivation
- **Time Complexity**: O(n! * n)
- **Space Complexity**: O(n!)
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

void nextPermutationOptimal(vector<int>& nums) {
    int n = nums.size(), i = n - 2;
    while (i >= 0 && nums[i] >= nums[i + 1]) i--;
    if (i >= 0) {
        int j = n - 1;
        while (nums[j] <= nums[i]) j--;
        swap(nums[i], nums[j]);
    }
    reverse(nums.begin() + i + 1, nums.end());
}
```

### Complexity Derivation
- **Time Complexity**: O(n)
- **Space Complexity**: O(1)
- **Why this is optimal**: Matches the theoretical information lower bound $\Omega(n)$ for unsorted array inspection.

---

## 6. Dry Run

**Trace**: nums = [1, 2, 3] -> [1, 3, 2]

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

- **Q1: Why do we REVERSE the suffix from i+1 to n-1 instead of sorting it?**  
  **A**: Because by definition of the pivot search, `nums[i+1..n-1]` is in strictly non-increasing (descending) order. Reversing a descending range in $\mathcal{O}(n)$ time automatically transforms it into ascending order without sorting overhead.

- **Q2: How to compute the PREVIOUS permutation (lexicographically smaller)?**  
  **A**: Symmetric logic: find first $i$ where `nums[i] > nums[i+1]`, find largest $j$ where `nums[j] < nums[i]`, swap `nums[i]` and `nums[j]`, and reverse suffix `nums[i+1..n-1]`.

- **Q3: What if the array is already at the maximum permutation `[3, 2, 1]`?**  
  **A**: Index $i$ reaches $-1$ (no pivot). The algorithm skips the swap step and reverses the entire array to `[1, 2, 3]`, which is the minimal permutation as required.

- **Q4: How to find the K-th Permutation directly without calling nextPermutation K times (LeetCode 60)?**  
  **A**: Use the Factorial Number System: for $n$ numbers, there are $(n-1)!$ permutations starting with each digit. Compute index $k / (n-1)!$, pick that digit from a list, update $k = k \% (n-1)!$, and repeat in $\mathcal{O}(n^2)$ time.

- **Q5: How many unique permutations exist for an array with duplicates?**  
  **A**: Formula $\frac{n!}{c_1! \cdot c_2! \dots c_k!}$ where $c_i$ is the frequency of the $i$-th distinct element.

## 9. Tags & Related Problems

- **Tags**: `Array`, `TakeUForward`, `Strivers-A2Z`, `Medium`
- **Related problems**:
  - Similar Step 3 Array Problems in the curriculum.
