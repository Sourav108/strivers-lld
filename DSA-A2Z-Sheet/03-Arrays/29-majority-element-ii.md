# Majority Element II (> n/3 times) (Step 3.3)

This is a complete, interview-ready note in C++ following the standard 9-section format.

- **Source**: https://takeuforward.org/data-structure/majority-elementsn-3-times-find-the-elements-that-appears-more-than-n-3-times-in-the-array/
- **Difficulty**: Medium
- **Statement**: Find all elements that appear more than $\lfloor n/3 \rfloor$ times (at most 2 candidates).

---

## 1. Problem, Restated

Find all elements that appear more than $\lfloor n/3 \rfloor$ times (at most 2 candidates).

- **Input**: Vector of integers `nums`.
- **Output**: Result as specified by problem requirements.
- **Key Constraints**: $n$ up to $10^5$, elements can be negative/positive, time limit 1.0s.

---

## 2. Intuition & Pattern

Extended Boyer-Moore Voting with 2 candidates and 2 counters.

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

vector<int> majority2Brute(const vector<int>& nums) {
    unordered_map<int, int> mp; vector<int> res;
    for (int x : nums) mp[x]++;
    for (auto& [v, c] : mp) if (c > (int)nums.size()/3) res.push_back(v);
    return res;
}
```

### Complexity Derivation
- **Time Complexity**: O(n)
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

vector<int> majorityElement2Optimal(const vector<int>& nums) {
    int c1 = 0, c2 = 0, el1 = 0, el2 = 1;
    for (int x : nums) {
        if (x == el1) c1++;
        else if (x == el2) c2++;
        else if (c1 == 0) { el1 = x; c1 = 1; }
        else if (c2 == 0) { el2 = x; c2 = 1; }
        else { c1--; c2--; }
    }
    c1 = c2 = 0;
    for (int x : nums) {
        if (x == el1) c1++;
        else if (x == el2) c2++;
    }
    vector<int> res;
    int thresh = nums.size() / 3;
    if (c1 > thresh) res.push_back(el1);
    if (c2 > thresh) res.push_back(el2);
    return res;
}
```

### Complexity Derivation
- **Time Complexity**: O(n)
- **Space Complexity**: O(1)
- **Why this is optimal**: Matches the theoretical information lower bound $\Omega(n)$ for unsorted array inspection.

---

## 6. Dry Run

**Trace**: nums = [3, 2, 3] -> el1=3 (count 2 > 3/3=1) -> [3]

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
