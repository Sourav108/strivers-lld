# Count Subarrays with Sum Equals K (Step 3.2)

This is a complete, interview-ready note in C++ following the standard 9-section format.

- **Source**: https://takeuforward.org/arrays/count-subarray-sum-equals-k/
- **Difficulty**: Medium
- **Statement**: Find total number of continuous subarrays whose sum equals $k$.

---

## 1. Problem, Restated

Find total number of continuous subarrays whose sum equals $k$.

- **Input**: Vector of integers `nums`.
- **Output**: Result as specified by problem requirements.
- **Key Constraints**: $n$ up to $10^5$, elements can be negative/positive, time limit 1.0s.

---

## 2. Intuition & Pattern

Prefix Sum Frequency Map (`prefixSum - k` seen count).

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

int subarraySumBrute(const vector<int>& nums, int k) {
    int cnt = 0, n = nums.size();
    for (int i = 0; i < n; i++) {
        int s = 0;
        for (int j = i; j < n; j++) { s += nums[j]; if (s == k) cnt++; }
    }
    return cnt;
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

int subarraySumOptimal(const vector<int>& nums, int k) {
    unordered_map<int, int> prefixFreq;
    prefixFreq[0] = 1; // base case: empty prefix
    int sum = 0, count = 0;
    for (int x : nums) {
        sum += x;
        int rem = sum - k;
        if (prefixFreq.count(rem)) count += prefixFreq[rem];
        prefixFreq[sum]++;
    }
    return count;
}
```

### Complexity Derivation
- **Time Complexity**: O(n)
- **Space Complexity**: O(n)
- **Why this is optimal**: Matches the theoretical information lower bound $\Omega(n)$ for unsorted array inspection.

---

## 6. Dry Run

**Trace**: nums = [1, 1, 1], k=2 -> subarrays [1,1] (2 times) -> count = 2

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

- **Q1: Why must we initialize `prefixFreq[0] = 1` in the hash map?**  
  **A**: Because if a prefix sum equals $k$ exactly at index $i$ (`sum == k`), then `sum - k = 0`. The count `prefixFreq[0] = 1` credits the valid subarray extending from index 0 to index $i$.

- **Q2: Why does the Sliding Window approach fail when negative numbers are present?**  
  **A**: Sliding window requires monotonicity: expanding the window must strictly increase the sum, and shrinking must decrease it. Negative numbers destroy monotonicity, so a hash map is required.

- **Q3: How to find the number of subarrays whose sum is DIVISIBLE by K (LeetCode 974)?**  
  **A**: Use modulo arithmetic: store frequencies of normalized remainders `rem = (sum % k + k) % k`. If `prefixMap.count(rem)`, add count to total in $\mathcal{O}(n)$ time.

- **Q4: What if the array is purely positive and we want all subarrays summing to K?**  
  **A**: Two Pointers sliding window finds count in $\mathcal{O}(n)$ time and $\mathcal{O}(1)$ space by maintaining running sum and adjusting window edges.

- **Q5: How to parallelize counting subarrays summing to K?**  
  **A**: Compute parallel prefix sums across chunks, construct local prefix frequency maps, and cross-correlate prefix sums between chunk boundaries.

## 9. Tags & Related Problems

- **Tags**: `Array`, `TakeUForward`, `Strivers-A2Z`, `Medium`
- **Related problems**:
  - Similar Step 3 Array Problems in the curriculum.
