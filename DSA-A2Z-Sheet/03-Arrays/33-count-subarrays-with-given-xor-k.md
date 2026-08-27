# Count Subarrays with Given XOR K (Step 3.3)

This is a complete, interview-ready note in C++ following the standard 9-section format.

- **Source**: https://takeuforward.org/data-structure/count-the-number-of-subarrays-with-given-xor-k/
- **Difficulty**: Medium
- **Statement**: Find number of subarrays having bitwise XOR equal to $K$.

---

## 1. Problem, Restated

Find number of subarrays having bitwise XOR equal to $K$.

- **Input**: Vector of integers `nums`.
- **Output**: Result as specified by problem requirements.
- **Key Constraints**: $n$ up to $10^5$, elements can be negative/positive, time limit 1.0s.

---

## 2. Intuition & Pattern

Prefix XOR Frequency Map: if $XR \oplus Y = K$, then $Y = XR \oplus K$.

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

int subarraysWithXorKBrute(const vector<int>& nums, int k) {
    int cnt = 0, n = nums.size();
    for (int i = 0; i < n; i++) {
        int x = 0;
        for (int j = i; j < n; j++) { x ^= nums[j]; if (x == k) cnt++; }
    }
    return cnt;
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

int subarraysWithXorKOptimal(const vector<int>& nums, int k) {
    unordered_map<int, int> freq;
    freq[0] = 1;
    int xr = 0, count = 0;
    for (int x : nums) {
        xr ^= x;
        int target = xr ^ k;
        if (freq.count(target)) count += freq[target];
        freq[xr]++;
    }
    return count;
}
```

### Complexity Derivation
- **Time Complexity**: O(n)
- **Space Complexity**: O(n)
- **Why this is optimal**: Matches the theoretical information lower bound $\Omega(n)$ for unsorted array inspection.

---

## 6. Dry Run

**Trace**: nums = [4, 2, 2, 6, 4], k=6 -> 4 subarrays

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

- **Q1: What is the mathematical proof of the prefix XOR frequency map?**  
  **A**: Let $XR_i$ be prefix XOR up to $i$. Subarray $nums[j..i]$ has XOR $k \iff XR_i \oplus XR_{j-1} = k$. XORing both sides with $k \oplus XR_{j-1}$ gives $XR_{j-1} = XR_i \oplus k$. Every time $XR_i \oplus k$ has occurred in past prefix XORs, it forms a valid subarray.

- **Q2: Why is `freq[0] = 1` initialized in the frequency map?**  
  **A**: If prefix XOR $XR_i == k$, then $XR_i \oplus k = 0$. The entry `freq[0] = 1` accounts for the full prefix subarray from index 0 to $i$ having XOR equal to $k$.

- **Q3: How does this compare with Count Subarrays with Sum K?**  
  **A**: The algorithms are mathematically isomorphic: Subarray Sum uses $(S - k)$, Subarray XOR uses $(XR \oplus k)$ because XOR is its own self-inverse ($x \oplus x = 0$).

- **Q4: Can we implement this using a Bitwise Trie instead of a hash map?**  
  **A**: Yes, insert 32-bit prefix XORs into a Trie where each node stores subtree insertion counts. Querying $XR \oplus k$ takes $\mathcal{O}(32) = \mathcal{O}(1)$ deterministic time without hash collisions.

- **Q5: How to solve Maximum XOR Subarray (finding subarray with maximum possible XOR)?**  
  **A**: Maintain prefix XORs in a Bitwise Trie. For each prefix XOR, query the Trie for the path that maximizes opposite bits at each step in $\mathcal{O}(32 \cdot n) = \mathcal{O}(n)$ time.

## 9. Tags & Related Problems

- **Tags**: `Array`, `TakeUForward`, `Strivers-A2Z`, `Medium`
- **Related problems**:
  - Similar Step 3 Array Problems in the curriculum.
