# Find Missing and Repeating Number (Step 3.3)

This is a complete, interview-ready note in C++ following the standard 9-section format.

- **Source**: https://takeuforward.org/data-structure/find-the-repeating-and-missing-numbers/
- **Difficulty**: Hard
- **Statement**: Given array of size $n$ containing numbers $1..n$, one appears twice ($A$) and one is missing ($B$). Find $[A, B]$.

---

## 1. Problem, Restated

Given array of size $n$ containing numbers $1..n$, one appears twice ($A$) and one is missing ($B$). Find $[A, B]$.

- **Input**: Vector of integers `nums`.
- **Output**: Result as specified by problem requirements.
- **Key Constraints**: $n$ up to $10^5$, elements can be negative/positive, time limit 1.0s.

---

## 2. Intuition & Pattern

Maths Equation System ($S - S_N = A - B$, $S^2 - S^2_N = A^2 - B^2$) or Bitwise XOR Partitioning.

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

vector<int> findMissingRepeatingBrute(const vector<int>& nums) {
    int n = nums.size(), rep = -1, mis = -1;
    for (int i = 1; i <= n; i++) {
        int cnt = 0;
        for (int x : nums) if (x == i) cnt++;
        if (cnt == 2) rep = i;
        else if (cnt == 0) mis = i;
    }
    return {rep, mis};
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

vector<int> findMissingRepeatingOptimal(const vector<int>& nums) {
    long long n = nums.size();
    long long SN = n * (n + 1) / 2;
    long long S2N = n * (n + 1) * (2 * n + 1) / 6;
    long long S = 0, S2 = 0;
    for (int x : nums) {
        S += x;
        S2 += (long long)x * x;
    }
    long long val1 = S - SN; // A - B
    long long val2 = (S2 - S2N) / val1; // A + B
    long long A = (val1 + val2) / 2;
    long long B = val2 - A;
    return {(int)A, (int)B};
}
```

### Complexity Derivation
- **Time Complexity**: O(n)
- **Space Complexity**: O(1)
- **Why this is optimal**: Matches the theoretical information lower bound $\Omega(n)$ for unsorted array inspection.

---

## 6. Dry Run

**Trace**: nums = [3, 1, 2, 5, 3] -> Repeating A=3, Missing B=4

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

- **Q1: How do the algebraic equations derive A (repeating) and B (missing)?**  
  **A**: 1) $S - S_N = A - B$. 2) $S^2 - S^2_N = A^2 - B^2 = (A - B)(A + B)$. Dividing (2) by (1) gives $A + B = \frac{S^2 - S^2_N}{S - S_N}$. Solving the linear system gives $A = \frac{(A-B) + (A+B)}{2}$ and $B = (A+B) - A$ in $\mathcal{O}(n)$ time and $\mathcal{O}(1)$ space.

- **Q2: How does the Bitwise XOR method work without integer overflow risks?**  
  **A**: Compute $X = (\bigoplus_{i=1}^n i) \oplus (\bigoplus x \in \text{nums})$. $X = A \oplus B$. Find rightmost set bit $D = X \& (-X)$. Partition numbers $1..n$ and array elements into two groups based on bit $D$. XORing each group isolates $A$ and $B$, verified by a single scan.

- **Q3: What is the Array Index Negation method?**  
  **A**: For each $x \in \text{nums}$, negate `nums[abs(x) - 1]`. If `nums[abs(x) - 1]` is already negative, $x$ is the repeating number. Then, the only index $i$ with a positive value corresponds to missing number $i + 1$.

- **Q4: Why is the Index Negation method not always permitted in interviews?**  
  **A**: Because it mutates the input array. If the array is read-only or in const memory, the Maths or XOR approaches must be used.

- **Q5: What if the numbers are in range [0, n-1] instead of [1, n]?**  
  **A**: Adjust formulas: $S_N = \frac{(n-1)n}{2}$ and $S^2_N = \frac{(n-1)n(2n-1)}{6}$, or map indices $x \to x+1$.

## 9. Tags & Related Problems

- **Tags**: `Array`, `TakeUForward`, `Strivers-A2Z`, `Hard`
- **Related problems**:
  - Similar Step 3 Array Problems in the curriculum.
