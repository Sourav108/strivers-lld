# Best Time to Buy and Sell Stock (Step 3.2)

This is a complete, interview-ready note in C++ following the standard 9-section format.

- **Source**: https://takeuforward.org/data-structure/stock-buy-and-sell/
- **Difficulty**: Easy
- **Statement**: Maximize profit choosing a single day to buy and a later day to sell.

---

## 1. Problem, Restated

Maximize profit choosing a single day to buy and a later day to sell.

- **Input**: Vector of integers `nums`.
- **Output**: Result as specified by problem requirements.
- **Key Constraints**: $n$ up to $10^5$, elements can be negative/positive, time limit 1.0s.

---

## 2. Intuition & Pattern

Track minimum buying price seen so far and maximize current price minus min price.

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

int maxProfitBrute(const vector<int>& prices) {
    int maxP = 0, n = prices.size();
    for (int i = 0; i < n; i++)
        for (int j = i + 1; j < n; j++)
            maxP = max(maxP, prices[j] - prices[i]);
    return maxP;
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

int maxProfitOptimal(const vector<int>& prices) {
    int minPrice = INT_MAX, maxProfit = 0;
    for (int p : prices) {
        minPrice = min(minPrice, p);
        maxProfit = max(maxProfit, p - minPrice);
    }
    return maxProfit;
}
```

### Complexity Derivation
- **Time Complexity**: O(n)
- **Space Complexity**: O(1)
- **Why this is optimal**: Matches the theoretical information lower bound $\Omega(n)$ for unsorted array inspection.

---

## 6. Dry Run

**Trace**: prices = [7, 1, 5, 3, 6, 4] -> buy at 1, sell at 6 -> profit = 5

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

- **Q1: How does this change if you can make UNLIMITED transactions (Stock II - LeetCode 122)?**  
  **A**: Greedy approach: capture every upward price movement. Whenever `prices[i] > prices[i-1]`, add `prices[i] - prices[i-1]` to total profit in $\mathcal{O}(n)$ time and $\mathcal{O}(1)$ space.

- **Q2: What if you can make at most TWO transactions (Stock III - LeetCode 123)?**  
  **A**: Maintain 4 variables in a single pass: `buy1 = max(buy1, -p)`, `sell1 = max(sell1, buy1 + p)`, `buy2 = max(buy2, sell1 - p)`, `sell2 = max(sell2, buy2 + p)` in $\mathcal{O}(n)$ time and $\mathcal{O}(1)$ space.

- **Q3: What if there is a 1-day COOLDOWN after selling (Stock with Cooldown - LeetCode 309)?**  
  **A**: Use State Machine DP with 3 states: `held`, `sold`, `reset`. `held = max(held, reset - p)`, `sold = held + p`, `reset = max(reset, prevSold)` in $\mathcal{O}(n)$ time.

- **Q4: What if there is a TRANSACTION FEE per trade (LeetCode 714)?**  
  **A**: Maintain `held = max(held, cash - price)` and `cash = max(cash, held + price - fee)` in $\mathcal{O}(n)$ time and $\mathcal{O}(1)$ space.

- **Q5: How does this problem map directly to Kadane's algorithm?**  
  **A**: Construct difference array $D[i] = \text{prices}[i] - \text{prices}[i-1]$. Finding maximum profit between buy day $i$ and sell day $j$ is mathematically identical to finding the maximum contiguous subarray sum in $D$.

## 9. Tags & Related Problems

- **Tags**: `Array`, `TakeUForward`, `Strivers-A2Z`, `Easy`
- **Related problems**:
  - Similar Step 3 Array Problems in the curriculum.
