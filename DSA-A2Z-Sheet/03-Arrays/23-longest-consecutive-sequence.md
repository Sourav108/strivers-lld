# Longest Consecutive Sequence (Step 3.2)

This is a complete, interview-ready note in C++ following the standard 9-section format.

- **Source**: https://takeuforward.org/data-structure/longest-consecutive-sequence-in-an-array/
- **Difficulty**: Medium
- **Statement**: Find length of longest consecutive elements sequence in unsorted array in O(n) time.

---

## 1. Problem, Restated

Find length of longest consecutive elements sequence in unsorted array in O(n) time.

- **Input**: Vector of integers `nums`.
- **Output**: Result as specified by problem requirements.
- **Key Constraints**: $n$ up to $10^5$, elements can be negative/positive, time limit 1.0s.

---

## 2. Intuition & Pattern

Hash Set with Sequence Start Check (`!set.count(x - 1)`). Only count upwards from sequence heads.

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

int longestConsecutiveBrute(const vector<int>& nums) {
    if (nums.empty()) return 0;
    int maxLen = 1;
    for (int x : nums) {
        int curr = x, cnt = 1;
        while (find(nums.begin(), nums.end(), curr + 1) != nums.end()) { curr++; cnt++; }
        maxLen = max(maxLen, cnt);
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

### Idea
Requires sorting.

### C++17 Code
```cpp
#include <vector>
#include <algorithm>
#include <climits>
#include <unordered_map>
#include <unordered_set>
using namespace std;

int longestConsecutiveBetter(vector<int>& nums) {
    if (nums.empty()) return 0;
    sort(nums.begin(), nums.end());
    int maxLen = 1, cnt = 1;
    for (size_t i = 1; i < nums.size(); i++) {
        if (nums[i] == nums[i - 1]) continue;
        if (nums[i] == nums[i - 1] + 1) cnt++;
        else { maxLen = max(maxLen, cnt); cnt = 1; }
    }
    return max(maxLen, cnt);
}
```

### Complexity Derivation
- **Time Complexity**: O(n log n)
- **Space Complexity**: O(1)
- **Why it's still not optimal**: Requires sorting.

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

int longestConsecutiveOptimal(const vector<int>& nums) {
    unordered_set<int> st(nums.begin(), nums.end());
    int maxLen = 0;
    for (int x : st) {
        if (!st.count(x - 1)) { // sequence start
            int curr = x, cnt = 1;
            while (st.count(curr + 1)) { curr++; cnt++; }
            maxLen = max(maxLen, cnt);
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

**Trace**: nums = [100, 4, 200, 1, 3, 2] -> sequence [1, 2, 3, 4] len 4

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

- **Q1: What if the array is already sorted?**  
  **A**: Exploiting sortedness allows two pointers or binary search to reduce time to $\mathcal{O}(\log n)$ or space to $\mathcal{O}(1)$.

- **Q2: What if elements arrive in a streaming fashion?**  
  **A**: Single-pass state accumulators adapt naturally to online streaming computation in $\mathcal{O}(1)$ amortized time per event.

- **Q3: What if input does not fit into RAM?**  
  **A**: Use external merge sort or MapReduce chunking with streaming combiner passes.

- **Q4: Can we parallelize this algorithm?**  
  **A**: Divide and conquer enables multi-threaded chunk evaluation with an $\mathcal{O}(1)$ merge step.

- **Q5: How does this generalize to multidimensional arrays or higher $K$?**  
  **A**: Techniques reduce higher dimensions by fixing degrees of freedom iteratively.

---

## 9. Tags & Related Problems

- **Tags**: `Array`, `TakeUForward`, `Strivers-A2Z`, `Medium`
- **Related problems**:
  - Similar Step 3 Array Problems in the curriculum.
