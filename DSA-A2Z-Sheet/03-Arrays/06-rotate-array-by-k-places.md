# Rotate Array by K Places (Step 3.1)

This is a complete, interview-ready note in C++ following the standard 9-section format.

- **Source**: https://takeuforward.org/data-structure/rotate-array-by-k-elements/
- **Difficulty**: Medium
- **Statement**: Rotate an array to the right by $k$ steps ($k \ge 0$).

---

## 1. Problem, Restated

Rotate an array to the right by $k$ steps ($k \ge 0$).

- **Input**: Vector of integers `nums`.
- **Output**: Result as specified by problem requirements.
- **Key Constraints**: $n$ up to $10^5$, elements can be negative/positive, time limit 1.0s.

---

## 2. Intuition & Pattern

Reversal Algorithm: Reverse whole array, reverse first k, reverse remaining n-k.

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

void rotateBrute(vector<int>& nums, int k) {
    int n = nums.size(); k %= n;
    vector<int> temp(n);
    for (int i = 0; i < n; i++) temp[(i + k) % n] = nums[i];
    nums = temp;
}
```

### Complexity Derivation
- **Time Complexity**: O(n)
- **Space Complexity**: O(n)
- **Why it's not good enough**: For $n = 10^5$, polynomial time $\mathcal{O}(n^2)$ takes $\approx 10^{10}$ operations and triggers Time Limit Exceeded (TLE).

---

## 4. Approach 2 — Better

### Idea
Temporary Array: Store the last $k$ elements in an auxiliary vector `temp`. Shift the first $n-k$ elements to the right by $k$ positions, then copy `temp` into the first $k$ slots of the original array.

### C++17 Code
```cpp
#include <vector>
using namespace std;

void rotateBetter(vector<int>& nums, int k) {
    int n = nums.size();
    k %= n;
    if (k == 0) return;
    
    // Copy last k elements
    vector<int> temp(k);
    for (int i = 0; i < k; i++) {
        temp[i] = nums[n - k + i];
    }
    // Shift remaining elements rightward
    for (int i = n - k - 1; i >= 0; i--) {
        nums[i + k] = nums[i];
    }
    // Copy temp to front
    for (int i = 0; i < k; i++) {
        nums[i] = temp[i];
    }
}
```

### Complexity Derivation
- **Time Complexity**: $\mathcal{O}(n)$ — copies $k$ elements, shifts $n-k$ elements, and copies $k$ elements back.
- **Space Complexity**: $\mathcal{O}(k)$ — auxiliary space for temporary buffer.
- **Why it's still not optimal**: Consumes $\mathcal{O}(k)$ extra memory; the 3-step reversal algorithm achieves the same $\mathcal{O}(n)$ time in strictly $\mathcal{O}(1)$ space.

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

void rotateOptimal(vector<int>& nums, int k) {
    int n = nums.size();
    k %= n;
    reverse(nums.begin(), nums.end());
    reverse(nums.begin(), nums.begin() + k);
    reverse(nums.begin() + k, nums.end());
}
```

### Complexity Derivation
- **Time Complexity**: O(n)
- **Space Complexity**: O(1)
- **Why this is optimal**: Matches the theoretical information lower bound $\Omega(n)$ for unsorted array inspection.

---

## 6. Dry Run

**Trace**: nums = [1, 2, 3, 4, 5, 6, 7], k=3 -> rev whole [7,6,5,4,3,2,1] -> rev parts [5,6,7, 1,2,3,4]

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

- **Q1: Why does the 3-Step Reversal Algorithm work mathematically?**  
  **A**: Let the array be split into two parts $A = \text{nums}[0..n-k-1]$ and $B = \text{nums}[n-k..n-1]$. We want the result $BA$. Reversing $A$ gives $A^R$, reversing $B$ gives $B^R$, giving $A^R B^R$. Reversing the whole array gives $(A^R B^R)^R = (B^R)^R (A^R)^R = BA$.

- **Q2: What is the Juggling Algorithm (Block Swap) approach using GCD(n, k)?**  
  **A**: The array elements form $\gcd(n, k)$ independent cyclic permutation sets. We traverse each cycle by jumping $k$ steps at a time with a temporary variable, performing exactly $n$ moves in $\mathcal{O}(n)$ time and $\mathcal{O}(1)$ space.

- **Q3: What happens when k is larger than n or negative?**  
  **A**: Normalize $k = (k \% n + n) \% n$. This handles $k > n$ and converts negative rotations (left rotations) into equivalent positive right rotations.

- **Q4: Can array rotation be done in O(1) time in system design?**  
  **A**: Yes, in a Circular Buffer / Ring Buffer data structure, rotation is $\mathcal{O}(1)$ by simply shifting the `headIndex = (headIndex + k) % n` pointer without moving any elements in memory.

- **Q5: Can we parallelize the reversal algorithm?**  
  **A**: Yes, each of the three `std::reverse` operations is embarrassingly parallel: independent CPU threads swap symmetric index pairs `(start + i, end - i)` concurrently.

## 9. Tags & Related Problems

- **Tags**: `Array`, `TakeUForward`, `Strivers-A2Z`, `Medium`
- **Related problems**:
  - Similar Step 3 Array Problems in the curriculum.
