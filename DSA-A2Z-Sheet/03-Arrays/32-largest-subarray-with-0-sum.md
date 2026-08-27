# Largest Subarray with 0 Sum (Step 3.3)

This is a complete, interview-ready note in C++ following the standard 9-section format.

- **Source**: https://takeuforward.org/data-structure/length-of-the-longest-subarray-with-zero-sum/
- **Difficulty**: Medium
- **Statement**: Find the length of the longest subarray with sum equal to 0.

---

## 1. Problem, Restated

Find the length of the longest subarray with sum equal to 0.

- **Input**: Vector of integers `nums`.
- **Output**: Result as specified by problem requirements.
- **Key Constraints**: $n$ up to $10^5$, elements can be negative/positive, time limit 1.0s.

---

## 2. Intuition & Pattern

Prefix Sum Map: If prefix sum $S$ repeats at index $j$ that was first seen at index $i$, subarray $(i, j]$ sums to 0.

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

int maxLenZeroSumBrute(const vector<int>& nums) {
    int maxL = 0, n = nums.size();
    for (int i = 0; i < n; i++) {
        int sum = 0;
        for (int j = i; j < n; j++) { sum += nums[j]; if (sum == 0) maxL = max(maxL, j - i + 1); }
    }
    return maxL;
}
```

### Complexity Derivation
- **Time Complexity**: O(n^2)
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

int maxLenZeroSumOptimal(const vector<int>& nums) {
    unordered_map<int, int> prefixMap;
    int sum = 0, maxLen = 0;
    for (int i = 0; i < (int)nums.size(); i++) {
        sum += nums[i];
        if (sum == 0) maxLen = i + 1;
        else if (prefixMap.count(sum)) maxLen = max(maxLen, i - prefixMap[sum]);
        else prefixMap[sum] = i;
    }
    return maxLen;
}
```

### Complexity Derivation
- **Time Complexity**: O(n)
- **Space Complexity**: O(n)
- **Why this is optimal**: Matches the theoretical information lower bound $\Omega(n)$ for unsorted array inspection.

---

## 6. Dry Run

**Trace**: nums = [15, -2, 2, -8, 1, 7, 10, 23] -> subarray [-2, 2, -8, 1, 7] len = 5

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

- **Q1: Why does a repeating prefix sum guarantee that the intermediate subarray sums to 0?**  
  **A**: Let $S_i$ be prefix sum up to index $i$ and $S_j$ up to $j$ ($i < j$). $S_j = S_i + \sum_{k=i+1}^j nums[k]$. If $S_j = S_i$, then $\sum_{k=i+1}^j nums[k] = 0$. The subarray $nums[i+1..j]$ has sum 0 and length $j - i$.

- **Q2: Why do we only store the FIRST occurrence of a prefix sum in the hash map?**  
  **A**: Because we want to maximize length $j - i$. Keeping the smallest possible index $i$ for any prefix sum guarantees the longest span when that sum is encountered again at index $j$.

- **Q3: How does this solve Contiguous Array (LeetCode 525 - Equal 0s and 1s)?**  
  **A**: Transform the array: replace all 0s with -1s. The problem is now identical to finding the longest subarray with sum 0 in $\mathcal{O}(n)$ time and $\mathcal{O}(n)$ space.

- **Q4: What if the array contains only non-negative integers?**  
  **A**: If all elements are non-negative, any subarray with sum 0 must consist entirely of zeroes. The problem reduces to finding the maximum consecutive zeroes in $\mathcal{O}(n)$ time and $\mathcal{O}(1)$ space.

- **Q5: How to return the actual elements of the longest subarray with 0 sum?**  
  **A**: Record `bestStart = prefixMap[sum] + 1` and `bestEnd = i` whenever a new maximum length is found, then return `vector<int>(nums.begin() + bestStart, nums.begin() + bestEnd + 1)`.

## 9. Tags & Related Problems

- **Tags**: `Array`, `TakeUForward`, `Strivers-A2Z`, `Medium`
- **Related problems**:
  - Similar Step 3 Array Problems in the curriculum.
