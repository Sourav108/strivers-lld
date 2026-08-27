# Majority Element (> n/2 times) (Step 3.2)

This is a complete, interview-ready note in C++ following the standard 9-section format.

- **Source**: https://takeuforward.org/data-structure/find-the-majority-element-that-occurs-more-than-n-2-times/
- **Difficulty**: Easy
- **Statement**: Find the element that appears more than $\lfloor n/2 \rfloor$ times.

---

## 1. Problem, Restated

Find the element that appears more than $\lfloor n/2 \rfloor$ times.

- **Input**: Vector of integers `nums`.
- **Output**: Result as specified by problem requirements.
- **Key Constraints**: $n$ up to $10^5$, elements can be negative/positive, time limit 1.0s.

---

## 2. Intuition & Pattern

Moore's Voting Algorithm. Cancel out distinct element pairs.

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

int majorityBrute(const vector<int>& nums) {
    int n = nums.size();
    for (int i = 0; i < n; i++) {
        int cnt = 0;
        for (int j = 0; j < n; j++) if (nums[j] == nums[i]) cnt++;
        if (cnt > n / 2) return nums[i];
    }
    return -1;
}
```

### Complexity Derivation
- **Time Complexity**: O(n^2)
- **Space Complexity**: O(1)
- **Why it's not good enough**: For $n = 10^5$, polynomial time $\mathcal{O}(n^2)$ takes $\approx 10^{10}$ operations and triggers Time Limit Exceeded (TLE).

---

## 4. Approach 2 — Better

### Idea
Uses hash map for frequencies.

### C++17 Code
```cpp
#include <vector>
#include <algorithm>
#include <climits>
#include <unordered_map>
#include <unordered_set>
using namespace std;

int majorityBetter(const vector<int>& nums) {
    unordered_map<int, int> mp;
    for (int x : nums) {
        mp[x]++;
        if (mp[x] > (int)nums.size() / 2) return x;
    }
    return -1;
}
```

### Complexity Derivation
- **Time Complexity**: O(n)
- **Space Complexity**: O(n)
- **Why it's still not optimal**: Uses hash map for frequencies.

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

int majorityOptimal(const vector<int>& nums) {
    int candidate = 0, count = 0;
    for (int x : nums) {
        if (count == 0) candidate = x;
        count += (x == candidate) ? 1 : -1;
    }
    return candidate;
}
```

### Complexity Derivation
- **Time Complexity**: O(n)
- **Space Complexity**: O(1)
- **Why this is optimal**: Matches the theoretical information lower bound $\Omega(n)$ for unsorted array inspection.

---

## 6. Dry Run

**Trace**: nums = [2, 2, 1, 1, 1, 2, 2] -> candidate 2 survives with count > 0

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

- **Q1: Why is Moore's Voting Algorithm guaranteed to find the majority element if it exists?**  
  **A**: Because the majority element occurs $> \lfloor n/2 \rfloor$ times. Even if every other non-majority element cancels out one majority element instance, the majority element will still have a remaining count $\ge 1$.

- **Q2: What if the problem statement does NOT guarantee that a majority element exists?**  
  **A**: Run a second $\mathcal{O}(n)$ confirmation pass: count the occurrences of the elected `candidate`. If `actualCount > n / 2`, return `candidate`; otherwise return `-1`.

- **Q3: Can Boyer-Moore voting be parallelized across multiple machines in MapReduce?**  
  **A**: Yes, each worker computes a local `(candidate, count)`. A reducer merges two pairs: if candidates match, add counts `count1 + count2`; if different, keep candidate with larger count and set count to `abs(count1 - count2)`. Then run a global verification pass.

- **Q4: What is the Randomized Monte Carlo approach for Majority Element?**  
  **A**: Pick a random index and verify if `nums[randIdx]` is the majority element in $\mathcal{O}(n)$. Since probability of picking majority element is $> 1/2$, expected number of iterations is $\le 2$, giving expected $\mathcal{O}(n)$ time and $\mathcal{O}(1)$ space.

- **Q5: How does this generalize to finding elements appearing > n/K times?**  
  **A**: Maintain at most $K - 1$ candidate-counter pairs (Tetris cancellation algorithm) in $\mathcal{O}(n \log K)$ time and $\mathcal{O}(K)$ space.

## 9. Tags & Related Problems

- **Tags**: `Array`, `TakeUForward`, `Strivers-A2Z`, `Easy`
- **Related problems**:
  - Similar Step 3 Array Problems in the curriculum.
