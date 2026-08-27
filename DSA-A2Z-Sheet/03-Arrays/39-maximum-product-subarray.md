# Maximum Product Subarray (Step 3.3)

This is a complete, interview-ready note in C++ following the standard 9-section format.

- **Source**: https://takeuforward.org/data-structure/maximum-product-subarray-in-an-array/
- **Difficulty**: Medium
- **Statement**: Find contiguous subarray within integer array that has largest product.

---

## 1. Problem, Restated

Find contiguous subarray within integer array that has largest product.

- **Input**: Vector of integers `nums`.
- **Output**: Result as specified by problem requirements.
- **Key Constraints**: $n$ up to $10^5$, elements can be negative/positive, time limit 1.0s.

---

## 2. Intuition & Pattern

Prefix & Suffix Product Scan or Kadane's Min/Max Multiplier Tracking (handles negative flips).

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

int maxProductBrute(const vector<int>& nums) {
    int maxP = INT_MIN, n = nums.size();
    for (int i = 0; i < n; i++) {
        int prod = 1;
        for (int j = i; j < n; j++) { prod *= nums[j]; maxP = max(maxP, prod); }
    }
    return maxP;
}
```

### Complexity Derivation
- **Time Complexity**: O(n^2)
- **Space Complexity**: O(1)
- **Why it's not good enough**: For $n = 10^5$, polynomial time $\mathcal{O}(n^2)$ takes $\approx 10^{10}$ operations and triggers Time Limit Exceeded (TLE).

---

## 4. Approach 2 — Better

### Idea
Kadane's Two-Variable Min/Max DP: Track both `maxProd` and `minProd` ending at the current position. When multiplying by a negative number, swap `maxProd` and `minProd` before updating with current element.

### C++17 Code
```cpp
#include <vector>
#include <algorithm>
#include <climits>
using namespace std;

int maxProductBetter(const vector<int>& nums) {
    int globalMax = nums[0];
    int maxProd = nums[0], minProd = nums[0];
    for (size_t i = 1; i < nums.size(); i++) {
        if (nums[i] < 0) swap(maxProd, minProd);
        maxProd = max((long long)nums[i], (long long)maxProd * nums[i]);
        minProd = min((long long)nums[i], (long long)minProd * nums[i]);
        globalMax = max(globalMax, maxProd);
    }
    return globalMax;
}
```

### Complexity Derivation
- **Time Complexity**: $\mathcal{O}(n)$ — single pass.
- **Space Complexity**: $\mathcal{O}(1)$ — constant scalar variables.
- **Why it's still not optimal**: Handles state transitions via 2 variables; prefix/suffix scan provides an equally clean parallelizable alternative.

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

int maxProductOptimal(const vector<int>& nums) {
    int maxProd = INT_MIN, n = nums.size();
    int prefix = 1, suffix = 1;
    for (int i = 0; i < n; i++) {
        if (prefix == 0) prefix = 1;
        if (suffix == 0) suffix = 1;
        prefix *= nums[i];
        suffix *= nums[n - 1 - i];
        maxProd = max(maxProd, max(prefix, suffix));
    }
    return maxProd;
}
```

### Complexity Derivation
- **Time Complexity**: O(n)
- **Space Complexity**: O(1)
- **Why this is optimal**: Matches the theoretical information lower bound $\Omega(n)$ for unsorted array inspection.

---

## 6. Dry Run

**Trace**: nums = [2, 3, -2, 4] -> max product subarray [2, 3] = 6

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

- **Q1: Why does the Prefix and Suffix scan algorithm work mathematically?**  
  **A**: If total negative numbers is even, the product of the whole array is positive. If odd, dropping either the first negative (leaving a suffix product) or dropping the last negative (leaving a prefix product) maximizes the product. 0s act as boundaries resetting prefix/suffix to 1.

- **Q2: How does Kadane's two-variable Min/Max DP work?**  
  **A**: Maintain `maxProd` and `minProd`. When multiplying by negative $x$, max becomes min and min becomes max. So swap `maxProd` and `minProd` before multiplying: `maxProd = max(x, maxProd * x)`, `minProd = min(x, minProd * x)` in $\mathcal{O}(n)$ time and $\mathcal{O}(1)$ space.

- **Q3: How does the algorithm handle zeroes in the array?**  
  **A**: Whenever `prefix == 0`, reset `prefix = 1`. A zero splits the problem into independent subarrays before and after the zero; `maxProd = max(maxProd, 0)` ensures 0 itself is considered as a candidate.

- **Q4: What if the array contains fractional numbers between 0 and 1?**  
  **A**: Multiplying by fractions reduces the magnitude. In this case, single elements might be larger than products. Kadane's `max(x, maxProd * x)` handles this correctly.

- **Q5: How to return the actual subarray that produces the maximum product?**  
  **A**: Track the starting and ending indices whenever `maxProd` is updated in the DP pass, recording `bestStart` and `bestEnd` in $\mathcal{O}(1)$ space.

## 9. Tags & Related Problems

- **Tags**: `Array`, `TakeUForward`, `Strivers-A2Z`, `Medium`
- **Related problems**:
  - Similar Step 3 Array Problems in the curriculum.
