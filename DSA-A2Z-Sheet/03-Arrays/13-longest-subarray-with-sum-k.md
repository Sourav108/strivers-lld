# Longest Subarray with Sum K (Step 3.1)

This is a complete, interview-ready note in C++ following the standard 9-section format.

- **Source**: https://takeuforward.org/data-structure/longest-subarray-with-given-sum-k/
- **Difficulty**: Medium
- **Statement**: Find length of longest subarray having sum equal to $K$ (positives & negatives).

---

## 1. Problem, Restated

Find length of longest subarray having sum equal to $K$ (positives & negatives).

- **Input**: Vector of integers `nums`.
- **Output**: Result as specified by problem requirements.
- **Key Constraints**: $n$ up to $10^5$, elements can be negative/positive, time limit 1.0s.

---

## 2. Intuition & Pattern

Prefix Sum with Hash Map (stores earliest occurrence of prefix sum). For positives-only, Two Pointers is O(1) space.

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

int longestSubarrayBrute(const vector<int>& nums, int k) {
    int maxLen = 0, n = nums.size();
    for (int i = 0; i < n; i++) {
        long long sum = 0;
        for (int j = i; j < n; j++) {
            sum += nums[j];
            if (sum == k) maxLen = max(maxLen, j - i + 1);
        }
    }
    return maxLen;
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

int longestSubarrayOptimal(const vector<int>& nums, int k) {
    unordered_map<long long, int> prefixMap; // prefixSum -> earliest index
    long long sum = 0;
    int maxLen = 0;
    for (int i = 0; i < (int)nums.size(); i++) {
        sum += nums[i];
        if (sum == k) maxLen = i + 1;
        long long rem = sum - k;
        if (prefixMap.find(rem) != prefixMap.end()) {
            maxLen = max(maxLen, i - prefixMap[rem]);
        }
        if (prefixMap.find(sum) == prefixMap.end()) {
            prefixMap[sum] = i; // only store first occurrence for max length
        }
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

**Trace**: nums = [10, 5, 2, 7, 1, 9], k=15 -> subarray [5, 2, 7, 1] len 4

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

- **Q1: If the array contains ONLY POSITIVE integers, how can we achieve O(1) space?**  
  **A**: Use the Two-Pointer / Sliding Window technique: maintain `left` and `right` with `currentSum`. Expand `right` by adding `nums[right]`. While `currentSum > k`, subtract `nums[left++]`. When `currentSum == k`, update `maxLen = max(maxLen, right - left + 1)`. Runs in $\mathcal{O}(n)$ time and $\mathcal{O}(1)$ space.

- **Q2: Why does the Two-Pointer sliding window approach FAIL when negative numbers are present?**  
  **A**: Because adding a negative number decreases `currentSum`, and subtracting a negative number increases `currentSum`. The monotonic relationship between window size and window sum is broken, so shrinking/expanding cannot guarantee finding the target sum.

- **Q3: Why do we only store the FIRST occurrence of each prefix sum in the hash map?**  
  **A**: Because we want to maximize subarray length $j - i$. The earliest index $i$ where prefix sum was seen gives the largest difference $j - i$.

- **Q4: How to adapt this to find the SHORTEST subarray with sum K?**  
  **A**: Store the *latest* index of each prefix sum: overwrite `prefixMap[sum] = i` on every step, and update `minLen = min(minLen, i - prefixMap[rem])`.

- **Q5: How to count the total NUMBER of subarrays with sum K?**  
  **A**: Maintain a frequency map `prefixFreq` mapping `prefixSum -> count`. Add `prefixFreq[sum - k]` to `totalCount` on each step.

## 9. Tags & Related Problems

- **Tags**: `Array`, `TakeUForward`, `Strivers-A2Z`, `Medium`
- **Related problems**:
  - Similar Step 3 Array Problems in the curriculum.
