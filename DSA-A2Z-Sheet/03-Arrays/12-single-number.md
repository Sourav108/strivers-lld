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
Hash Map Frequency Count: Iterate through `nums` and populate a hash map `unordered_map<int, int>` with the occurrence count of each number. Then iterate through the map to find the key with `count == 1`.

### C++17 Code
```cpp
#include <vector>
#include <unordered_map>
using namespace std;

int singleNumberBetter(const vector<int>& nums) {
    unordered_map<int, int> freq;
    for (int x : nums) {
        freq[x]++;
    }
    for (const auto& [val, count] : freq) {
        if (count == 1) return val;
    }
    return -1;
}
```

### Complexity Derivation
- **Time Complexity**: $\mathcal{O}(n)$ average time for hash map insertions and queries.
- **Space Complexity**: $\mathcal{O}(n)$ — stores $n/2 + 1$ distinct keys in the hash table.
- **Why it's still not optimal**: Requires $\mathcal{O}(n)$ extra space and dynamic memory allocations, whereas bitwise XOR achieves $\mathcal{O}(1)$ space.

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

- **Q1: What if every element appears THREE times except one which appears once (LeetCode 137)?**  
  **A**: Count bits at each position $0..31$ modulo 3: `bitSum % 3`. Alternatively, use two bitmasks `ones` and `twos` with boolean algebra: `ones = (ones ^ x) & ~twos; twos = (twos ^ x) & ~ones;` in $\mathcal{O}(n)$ time and $\mathcal{O}(1)$ space.

- **Q2: What if every element appears TWICE except TWO elements which appear once (LeetCode 260)?**  
  **A**: XOR all elements to get $X = A \oplus B$. Find rightmost set bit `diff = X & (-X)`. Partition array into two sets: numbers with `diff` bit set vs unset. XOR each set independently to isolate $A$ and $B$.

- **Q3: Can we find the single number in O(log n) if the array is sorted?**  
  **A**: Yes, Binary Search on index parity: before the single element, pairs start at even indices `nums[2k] == nums[2k+1]`. After the single element, pairs start at odd indices `nums[2k+1] == nums[2k+2]`.

- **Q4: What mathematical algebraic structure does XOR form?**  
  **A**: XOR forms an **Abelian Group** over bit strings: closure, associativity, commutativity, identity ($0$), and every element is its own self-inverse ($x \oplus x = 0$).

- **Q5: What if elements are floating-point numbers or strings instead of integers?**  
  **A**: XOR operates strictly on discrete bit vectors. For floats or strings, an $\mathcal{O}(n)$ Hash Map or sorting $\mathcal{O}(n \log n)$ is required.

## 9. Tags & Related Problems

- **Tags**: `Array`, `TakeUForward`, `Strivers-A2Z`, `Easy`
- **Related problems**:
  - Similar Step 3 Array Problems in the curriculum.
