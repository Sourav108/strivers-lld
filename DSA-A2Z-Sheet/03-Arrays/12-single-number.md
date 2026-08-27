# Single Number (Step 3.1)

This is a complete, interview-ready note in C++ following the standard 9-section format.

- **Source**: https://takeuforward.org/arrays/find-the-number-that-appears-once-and-the-other-numbers-twice/
- **Difficulty**: Easy
- **Statement**: Every element appears twice except for one. Find that single one.

---

## 1. Problem, Restated

Every element appears twice except for one. Find that single one.

- **Input**: Vector of integers `nums`.
- **Output**: Result as specified by problem requirements.
- **Key Constraints**: $n$ up to $10^5$, elements can be negative/positive, time limit 1.0s.

---

## 2. Intuition & Pattern

Bitwise XOR. Since $a \oplus a = 0$ and $a \oplus 0 = a$, XORing all numbers isolates the unique element.

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

int singleNumberBrute(const vector<int>& nums) {
    for (int i = 0; i < (int)nums.size(); i++) {
        int cnt = 0;
        for (int j = 0; j < (int)nums.size(); j++) if (nums[j] == nums[i]) cnt++;
        if (cnt == 1) return nums[i];
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
Uses hash map consuming extra memory.

### C++17 Code
```cpp
#include <vector>
#include <algorithm>
#include <climits>
#include <unordered_map>
#include <unordered_set>
using namespace std;

int singleNumberBetter(const vector<int>& nums) {
    unordered_map<int, int> freq;
    for (int x : nums) freq[x]++;
    for (auto& [val, count] : freq) if (count == 1) return val;
    return -1;
}
```

### Complexity Derivation
- **Time Complexity**: O(n)
- **Space Complexity**: O(n)
- **Why it's still not optimal**: Uses hash map consuming extra memory.

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

int singleNumberOptimal(const vector<int>& nums) {
    int xorSum = 0;
    for (int x : nums) xorSum ^= x;
    return xorSum;
}
```

### Complexity Derivation
- **Time Complexity**: O(n)
- **Space Complexity**: O(1)
- **Why this is optimal**: Matches the theoretical information lower bound $\Omega(n)$ for unsorted array inspection.

---

## 6. Dry Run

**Trace**: nums = [4, 1, 2, 1, 2] -> 4 ^ (1^1) ^ (2^2) = 4 ^ 0 = 4

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

- **Tags**: `Array`, `TakeUForward`, `Strivers-A2Z`, `Easy`
- **Related problems**:
  - Similar Step 3 Array Problems in the curriculum.
