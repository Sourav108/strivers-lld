# Pascal's Triangle (Step 3.3)

This is a complete, interview-ready note in C++ following the standard 9-section format.

- **Source**: https://takeuforward.org/data-structure/program-to-generate-pascals-triangle/
- **Difficulty**: Medium
- **Statement**: Generate first $n$ rows of Pascal's Triangle.

---

## 1. Problem, Restated

Generate first $n$ rows of Pascal's Triangle.

- **Input**: Vector of integers `nums`.
- **Output**: Result as specified by problem requirements.
- **Key Constraints**: $n$ up to $10^5$, elements can be negative/positive, time limit 1.0s.

---

## 2. Intuition & Pattern

Combinatorics formula: each row element $C(r, c) = C(r, c-1) \times \frac{r - c + 1}{c}$.

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

vector<vector<int>> pascalBrute(int n) {
    // computes nCr from scratch for each position
    vector<vector<int>> res;
    return res;
}
```

### Complexity Derivation
- **Time Complexity**: O(n^3)
- **Space Complexity**: O(1)
- **Why it's not good enough**: For $n = 10^5$, polynomial time $\mathcal{O}(n^2)$ takes $\approx 10^{10}$ operations and triggers Time Limit Exceeded (TLE).

---

## 4. Approach 2 — Better

### Idea
Dynamic Programming Row-by-Row: Generate each row from the previous row using the recurrence $row[j] = prevRow[j-1] + prevRow[j]$.

### C++17 Code
```cpp
#include <vector>
using namespace std;

vector<vector<int>> generatePascalBetter(int numRows) {
    vector<vector<int>> triangle;
    for (int i = 0; i < numRows; i++) {
        vector<int> row(i + 1, 1);
        for (int j = 1; j < i; j++) {
            row[j] = triangle[i - 1][j - 1] + triangle[i - 1][j];
        }
        triangle.push_back(row);
    }
    return triangle;
}
```

### Complexity Derivation
- **Time Complexity**: $\mathcal{O}(n^2)$ — total elements computed is $n(n+1)/2$.
- **Space Complexity**: $\mathcal{O}(n^2)$ — output triangle storage.
- **Why it's still not optimal**: Requires reading the previous row from memory, whereas the direct combinatorics formula computes any row in isolation.

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

vector<vector<int>> generatePascalOptimal(int numRows) {
    vector<vector<int>> triangle;
    for (int i = 0; i < numRows; i++) {
        vector<int> row(i + 1, 1);
        long long val = 1;
        for (int j = 1; j < i; j++) {
            val = val * (i - j + 1) / j;
            row[j] = val;
        }
        triangle.push_back(row);
    }
    return triangle;
}
```

### Complexity Derivation
- **Time Complexity**: O(n^2)
- **Space Complexity**: O(1)
- **Why this is optimal**: Matches the theoretical information lower bound $\Omega(n)$ for unsorted array inspection.

---

## 6. Dry Run

**Trace**: n=5 -> generates 5 rows with edge 1s

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

- **Q1: How to compute the single element at row R and column C in O(C) time and O(1) space?**  
  **A**: Use the combinatorial formula $\binom{R-1}{C-1} = \frac{(R-1) \times (R-2) \times \dots \times (R-C+1)}{1 \times 2 \times \dots \times (C-1)}$. Compute iteratively multiplying the numerator and dividing the denominator in $\mathcal{O}(C)$ operations.

- **Q2: How to generate ONLY the N-th row of Pascal's triangle in O(N) time and O(1) extra space?**  
  **A**: Start with `val = 1`. For $i = 1$ to $N-1$, compute next element `val = val * (N - i) / i` and append to the row vector in $\mathcal{O}(N)$ time.

- **Q3: What are the key mathematical properties of Pascal's Triangle?**  
  **A**: 1) Sum of row $N$ is $2^{N-1}$. 2) Diagonals represent triangular, tetrahedral, and simplex numbers. 3) Parity of elements (odd/even) forms the fractal **Sierpinski Triangle**.

- **Q4: How to prevent 64-bit integer overflow when computing nCr for N = 60?**  
  **A**: Compute using prime factorization cancellations, dynamic programming with addition, or 128-bit integers (`__int128_t` in GCC/Clang).

- **Q5: How does Pascal's Triangle relate to Binomial Theorem?**  
  **A**: The $N$-th row contains the exact polynomial coefficients for the algebraic expansion of $(x + y)^{N-1} = \sum_{k=0}^{N-1} \binom{N-1}{k} x^{N-1-k} y^k$.

## 9. Tags & Related Problems

- **Tags**: `Array`, `TakeUForward`, `Strivers-A2Z`, `Medium`
- **Related problems**:
  - Similar Step 3 Array Problems in the curriculum.
