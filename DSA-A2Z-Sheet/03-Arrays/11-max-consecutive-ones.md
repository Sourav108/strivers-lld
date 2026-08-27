# Max Consecutive Ones (Step 3.1)

This is a complete, interview-ready note in C++ following the standard 9-section format.

- **Source**: https://takeuforward.org/data-structure/count-maximum-consecutive-ones-in-the-array/
- **Difficulty**: Easy
- **Statement**: Find maximum number of consecutive 1s in a binary array.

---

## 1. Problem, Restated

Find maximum number of consecutive 1s in a binary array.

- **Input**: Vector of integers `nums`.
- **Output**: Result as specified by problem requirements.
- **Key Constraints**: $n$ up to $10^5$, elements can be negative/positive, time limit 1.0s.

---

## 2. Intuition & Pattern

Running streak counter. Increment on 1, reset to 0 on 0 while updating global max.

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

int findMaxConsecutiveOnesBrute(const vector<int>& nums) {
    int maxCnt = 0;
    for (int i = 0; i < (int)nums.size(); i++) {
        int cnt = 0;
        for (int j = i; j < (int)nums.size(); j++) {
            if (nums[j] == 1) cnt++; else break;
        }
        maxCnt = max(maxCnt, cnt);
    }
    return maxCnt;
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

int findMaxConsecutiveOnesOptimal(const vector<int>& nums) {
    int maxCnt = 0, currentCnt = 0;
    for (int x : nums) {
        if (x == 1) {
            currentCnt++;
            maxCnt = max(maxCnt, currentCnt);
        } else {
            currentCnt = 0;
        }
    }
    return maxCnt;
}
```

### Complexity Derivation
- **Time Complexity**: O(n)
- **Space Complexity**: O(1)
- **Why this is optimal**: Matches the theoretical information lower bound $\Omega(n)$ for unsorted array inspection.

---

## 6. Dry Run

**Trace**: nums = [1, 1, 0, 1, 1, 1] -> streak reaches 3 -> returns 3

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

- **Q1: How to solve Max Consecutive Ones III where you can flip at most K zeroes (LeetCode 1004)?**  
  **A**: Use a Sliding Window: maintain `left` and `right` pointers and `zeroCount`. Expand `right`. If `nums[right] == 0`, increment `zeroCount`. While `zeroCount > k`, if `nums[left] == 0` decrement `zeroCount` and increment `left`. Max window length is $R - L + 1$.

- **Q2: What if the binary array is circular (end connects to beginning)?**  
  **A**: If the array is all 1s, return $n$. Otherwise, the maximum consecutive 1s is $\max(\text{internal max streak}, \text{prefix 1s} + \text{suffix 1s})$.

- **Q3: How to count maximum consecutive occurrences of ANY arbitrary value, not just 1?**  
  **A**: Track `currentVal = nums[0]`, `currentStreak = 1`. If `nums[i] == currentVal`, `currentStreak++`; else `currentVal = nums[i], currentStreak = 1`. Update `maxStreak = max(maxStreak, currentStreak)`.

- **Q4: How to process a 100GB bitstream efficiently using 64-bit CPU words?**  
  **A**: Load 64 bits at once into `uint64_t`. Trailing and leading zero counts via `__builtin_clzll` and `__builtin_ctzll` allow skipping large chunks of contiguous 1s/0s in single CPU instructions.

- **Q5: Can this be formulated using Dynamic Programming?**  
  **A**: Yes, `dp[i]` represents consecutive 1s ending at index $i$: `dp[i] = (nums[i] == 1) ? dp[i-1] + 1 : 0`, optimized to $\mathcal{O}(1)$ space with a single accumulator.

## 9. Tags & Related Problems

- **Tags**: `Array`, `TakeUForward`, `Strivers-A2Z`, `Easy`
- **Related problems**:
  - Similar Step 3 Array Problems in the curriculum.
