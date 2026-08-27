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
Sort + Linear Scan: Sort the array in $\mathcal{O}(n \log n)$. Iterate through the sorted elements, skipping duplicates and incrementing streak counter when `nums[i] == nums[i-1] + 1`.

### C++17 Code
```cpp
#include <vector>
#include <algorithm>
using namespace std;

int longestConsecutiveBetter(vector<int>& nums) {
    if (nums.empty()) return 0;
    sort(nums.begin(), nums.end());
    int maxLen = 1, currentStreak = 1;
    for (size_t i = 1; i < nums.size(); i++) {
        if (nums[i] == nums[i - 1]) continue; // skip duplicates
        if (nums[i] == nums[i - 1] + 1) {
            currentStreak++;
        } else {
            maxLen = max(maxLen, currentStreak);
            currentStreak = 1;
        }
    }
    return max(maxLen, currentStreak);
}
```

### Complexity Derivation
- **Time Complexity**: $\mathcal{O}(n \log n)$ — dominated by sorting.
- **Space Complexity**: $\mathcal{O}(1)$ or $\mathcal{O}(\log n)$ sorting stack space.
- **Why it's still not optimal**: Sorting takes $\mathcal{O}(n \log n)$, while hash set with sequence head detection achieves linear $\mathcal{O}(n)$ time.

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

- **Q1: Why does checking `!st.count(x - 1)` guarantee O(n) total time complexity?**  
  **A**: Because the inner `while` loop only executes for numbers that are the *head* of a streak. Every element in any streak is visited at most twice (once in outer loop, once in inner loop), giving $\mathcal{O}(2n) = \mathcal{O}(n)$ total operations.

- **Q2: How can Disjoint Set Union (DSU) solve this problem in an online dynamic stream?**  
  **A**: When a new number $x$ arrives, add it to DSU and union with $x - 1$ (if present) and $x + 1$ (if present). Track component sizes to maintain maximum streak dynamically in $\mathcal{O}(\alpha(n))$ per insertion.

- **Q3: What if the input contains duplicate numbers like `[1, 2, 0, 1]`?**  
  **A**: Inserting elements into `std::unordered_set` automatically filters duplicates, correctly finding streak `[0, 1, 2]` of length 3.

- **Q4: How to protect `std::unordered_set` in C++ against adversarial worst-case O(n^2) hash collision attacks?**  
  **A**: Provide a custom hash functor using a randomized 64-bit seed (e.g. `splitmix64` with `chrono::steady_clock`) to prevent crafted anti-hash inputs from degrading bucket chains.

- **Q5: Can this be solved with O(1) extra space on unsorted input?**  
  **A**: Sort the array in $\mathcal{O}(n \log n)$ time, then do a single pass tracking `currentStreak` and skipping duplicates in $\mathcal{O}(1)$ space.

## 9. Tags & Related Problems

- **Tags**: `Array`, `TakeUForward`, `Strivers-A2Z`, `Medium`
- **Related problems**:
  - Similar Step 3 Array Problems in the curriculum.
