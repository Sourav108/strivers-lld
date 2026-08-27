# Subarrays with Given XOR (Advanced) (Step 3.3)

This is a complete, interview-ready note in C++ following the standard 9-section format.

- **Source**: https://takeuforward.org/data-structure/count-the-number-of-subarrays-with-given-xor-k/
- **Difficulty**: Hard
- **Statement**: Count subarrays having XOR equal to target integer $k$.

---

## 1. Problem, Restated

Count subarrays having XOR equal to target integer $k$.

- **Input**: Vector of integers `nums`.
- **Output**: Result as specified by problem requirements.
- **Key Constraints**: $n$ up to $10^5$, elements can be negative/positive, time limit 1.0s.

---

## 2. Intuition & Pattern

Prefix XOR Hashing (Mathematical invariant $Y = XR \oplus K$).

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

int countXORSubarraysBrute(const vector<int>& nums, int k) {
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

int countXORSubarraysOptimal(const vector<int>& nums, int k) {
    unordered_map<int, int> xorFreq;
    xorFreq[0] = 1;
    int xr = 0, count = 0;
    for (int x : nums) {
        xr ^= x;
        int rem = xr ^ k;
        if (xorFreq.count(rem)) count += xorFreq[rem];
        xorFreq[xr]++;
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

**Trace**: nums = [4, 2, 2, 6, 4], k = 6 -> count = 4

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

- **Q1: What is the mathematical proof of the prefix XOR frequency relation?**  
  **A**: Let $XR_i$ be prefix XOR from index 0 to $i$. Subarray $nums[j..i]$ has XOR $k \iff XR_i \oplus XR_{j-1} = k$. XORing both sides with $k \oplus XR_{j-1}$ yields $XR_{j-1} = XR_i \oplus k$. Thus, every past occurrence of prefix XOR $(XR_i \oplus k)$ forms a valid subarray.

- **Q2: Why is `xorFreq[0] = 1` initialized in the frequency map?**  
  **A**: If the prefix XOR from index 0 to $i$ equals $k$ ($XR_i = k$), then $XR_i \oplus k = 0$. The base count `xorFreq[0] = 1` accounts for subarrays starting at index 0.

- **Q3: How does this compare with Count Subarrays with Sum Equals K?**  
  **A**: They are mathematically isomorphic: Subarray Sum uses subtraction ($S - k$), Subarray XOR uses bitwise XOR ($XR \oplus k$).

- **Q4: How to find the LONGEST subarray with XOR equal to K?**  
  **A**: Instead of storing frequency count, store the *first seen index* in `unordered_map<int, int> firstSeen`. Update `maxLen = max(maxLen, i - firstSeen[XR ^ k])`.

- **Q5: How to solve Maximum XOR Subarray in O(n) using a Bitwise Trie?**  
  **A**: Insert each prefix XOR into a binary Trie (depth 32). For each prefix XOR, query the Trie for the path that maximizes opposite bits at each position from MSB to LSB in $\mathcal{O}(32 \cdot n) = \mathcal{O}(n)$ time.

## 9. Tags & Related Problems

- **Tags**: `Array`, `TakeUForward`, `Strivers-A2Z`, `Hard`
- **Related problems**:
  - Similar Step 3 Array Problems in the curriculum.
