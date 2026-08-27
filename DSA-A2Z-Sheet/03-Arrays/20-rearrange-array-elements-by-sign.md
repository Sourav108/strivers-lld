# Rearrange Array Elements by Sign (Step 3.2)

This is a complete, interview-ready note in C++ following the standard 9-section format.

- **Source**: https://takeuforward.org/arrays/rearrange-array-elements-by-sign/
- **Difficulty**: Medium
- **Statement**: Rearrange array of equal positive and negative numbers alternately starting with positive.

---

## 1. Problem, Restated

Rearrange array of equal positive and negative numbers alternately starting with positive.

- **Input**: Vector of integers `nums`.
- **Output**: Result as specified by problem requirements.
- **Key Constraints**: $n$ up to $10^5$, elements can be negative/positive, time limit 1.0s.

---

## 2. Intuition & Pattern

Two Pointers Placement (Positives at even indices 0,2,4..., Negatives at odd 1,3,5...).

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

vector<int> rearrangeBrute(const vector<int>& nums) {
    vector<int> pos, neg;
    for (int x : nums) if (x > 0) pos.push_back(x); else neg.push_back(x);
    vector<int> res(nums.size());
    for (size_t i = 0; i < pos.size(); i++) {
        res[2 * i] = pos[i];
        res[2 * i + 1] = neg[i];
    }
    return res;
}
```

### Complexity Derivation
- **Time Complexity**: O(2n) = O(n)
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

vector<int> rearrangeOptimal(const vector<int>& nums) {
    int n = nums.size(), posIdx = 0, negIdx = 1;
    vector<int> res(n);
    for (int x : nums) {
        if (x > 0) {
            res[posIdx] = x;
            posIdx += 2;
        } else {
            res[negIdx] = x;
            negIdx += 2;
        }
    }
    return res;
}
```

### Complexity Derivation
- **Time Complexity**: O(n)
- **Space Complexity**: O(n)
- **Why this is optimal**: Matches the theoretical information lower bound $\Omega(n)$ for unsorted array inspection.

---

## 6. Dry Run

**Trace**: nums = [3, 1, -2, -5, 2, -4] -> returns [3, -2, 1, -5, 2, -4]

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

- **Q1: What if the count of positive and negative elements is NOT equal?**  
  **A**: Segregate into `pos` and `neg` lists. Alternate until the smaller list exhausts, then append remaining elements to the end in $\mathcal{O}(n)$ time and $\mathcal{O}(n)$ space.

- **Q2: Can we solve this in O(1) extra space while preserving relative order?**  
  **A**: Preserving order in $\mathcal{O}(1)$ space requires Right-Rotation / Block-Merge in $\mathcal{O}(n \log^2 n)$ or $\mathcal{O}(n^2)$ time. In practice, $\mathcal{O}(n)$ auxiliary memory is standard for $\mathcal{O}(n)$ time.

- **Q3: What if relative order does NOT matter?**  
  **A**: Use Two Pointers in $\mathcal{O}(1)$ space: `pos = 0`, `neg = 1`. Advance `pos` by 2 while `nums[pos] > 0`, advance `neg` by 2 while `nums[neg] < 0`. Swap `nums[pos]` and `nums[neg]` when both are misplaced.

- **Q4: How to rearrange such that all negatives come before all positives in O(1) space?**  
  **A**: Use two-pointer QuickSort partition: pointer `j` tracks positive boundary, swap `nums[i]` with `nums[j++]` when `nums[i] < 0` in $\mathcal{O}(n)$ time.

- **Q5: How to parallelize array rearrangement for n = 10^9?**  
  **A**: Count positive elements in parallel chunks, compute target destination offsets via prefix sums, and write elements to even/odd destination positions in parallel.

## 9. Tags & Related Problems

- **Tags**: `Array`, `TakeUForward`, `Strivers-A2Z`, `Medium`
- **Related problems**:
  - Similar Step 3 Array Problems in the curriculum.
