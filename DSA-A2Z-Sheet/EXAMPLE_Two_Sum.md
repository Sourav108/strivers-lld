# Two Sum (LeetCode #1)

> **Step / Topic**: Step 03 — Arrays (Medium)  
> **Source Link**: [LeetCode #1 - Two Sum](https://leetcode.com/problems/two-sum/) | [TakeUForward Article](https://takeuforward.org/data-structure/two-sum-check-if-a-pair-with-given-sum-exists-in-array/)  
> **Difficulty**: 🟢 `Easy` / 🟡 `Medium`

---

## 1. Problem, Restated

You are given an array of integers `nums` and a single integer `target`. Your task is to find the indices of two distinct numbers in `nums` that add up to `target`.

- **Input**: An integer array `nums` of size $n$ and an integer `target`.
- **Output**: A vector containing two indices `[i, j]` such that $i \neq j$ and $\text{nums}[i] + \text{nums}[j] == \text{target}$.
- **Key Constraints**:
  - $2 \le n \le 10^4$
  - $-10^9 \le \text{nums}[i] \le 10^9$
  - $-10^9 \le \text{target} \le 10^9$
  - **Exactly one valid answer exists**.
  - You cannot use the same element twice (i.e. $i \neq j$).
  - The array is **unsorted**.

---

## 2. Intuition & Pattern

- **Underlying Pattern**: **Hash Map Lookup (Complement Search)**.
- **The "Aha!" Moment**:
  - In algebra, if $x + y = \text{target}$, then $y = \text{target} - x$.
  - When inspecting any number $x = \text{nums}[i]$, we do not need to rescan the entire array for $y$. Instead, we ask: *"Have we already seen $y = \text{target} - x$ in our past traversal?"*
  - By remembering numbers and their indices in an unordered hash map (`std::unordered_map`), we turn an $O(n)$ search into an $O(1)$ average-time lookup.
- **Recognition Clue**:
  - Whenever a problem asks to find a pair $(x, y)$ that satisfies a fixed arithmetic condition on an unsorted array, storing historical elements in a hash map as you stream through is the primary pattern.

---

## 3. Approach 1 — Brute Force

### Idea
Iterate through all possible pairs $(i, j)$ where $0 \le i < j < n$. Check whether $\text{nums}[i] + \text{nums}[j] == \text{target}$.

### C++17 Code
```cpp
#include <vector>

class Solution {
public:
    std::vector<int> twoSum(const std::vector<int>& nums, int target) {
        int n = static_cast<int>(nums.size());
        
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (nums[i] + nums[j] == target) {
                    return {i, j};
                }
            }
        }
        
        return {}; // Guarantee: problem statement ensures a solution always exists
    }
};
```

### Complexity Derivation
- **Time Complexity**: $\mathcal{O}(n^2)$ — The outer loop runs $n$ times and the inner loop runs $(n - 1 - i)$ times. Total iterations $= \frac{n(n-1)}{2} = \mathcal{O}(n^2)$.
- **Space Complexity**: $\mathcal{O}(1)$ — Only a few primitive integer counters (`i`, `j`, `n`) are allocated on the stack.

### Why It's Not Good Enough
For $n = 10^4$, $n^2 = 10^8$ operations. While $10^8$ operations barely passes within 1.0s on some online judges, if $n$ increases to $10^5$, $n^2 = 10^{10}$ operations, causing an immediate **Time Limit Exceeded (TLE)**.

---

## 4. Approach 2 — Better

### Idea
If the problem only asked whether a pair exists (returning `bool`) or to return the values themselves, we could sort `nums` and use **Two Pointers** (`left = 0`, `right = n - 1`). 

Because we must return original indices, we store each element along with its original index as a pair `std::pair<int, int> {value, original_index}`, sort the list of pairs by value, and then shrink the window using two pointers.

### C++17 Code
```cpp
#include <vector>
#include <algorithm>

class Solution {
public:
    std::vector<int> twoSum(const std::vector<int>& nums, int target) {
        int n = static_cast<int>(nums.size());
        std::vector<std::pair<int, int>> indexedNums(n);
        
        for (int i = 0; i < n; i++) {
            indexedNums[i] = {nums[i], i};
        }
        
        // Sort ascending by value
        std::sort(indexedNums.begin(), indexedNums.end());
        
        int left = 0, right = n - 1;
        while (left < right) {
            int currentSum = indexedNums[left].first + indexedNums[right].first;
            
            if (currentSum == target) {
                return {indexedNums[left].second, indexedNums[right].second};
            } else if (currentSum < target) {
                left++;  // Need a larger sum -> move left pointer rightward
            } else {
                right--; // Need a smaller sum -> move right pointer leftward
            }
        }
        
        return {};
    }
};
```

### Complexity Derivation
- **Time Complexity**: $\mathcal{O}(n \log n)$ — Creating the pair array takes $\mathcal{O}(n)$. Sorting $n$ elements takes $\mathcal{O}(n \log n)$. The two-pointer traversal takes $\mathcal{O}(n)$. The overall time is dominated by sorting: $\mathcal{O}(n \log n)$.
- **Space Complexity**: $\mathcal{O}(n)$ — Required to store the $n$ element-index pairs.

### Why It's Still Not Optimal
While $\mathcal{O}(n \log n)$ is significantly faster than $\mathcal{O}(n^2)$, we can eliminate the sorting overhead entirely and achieve $\mathcal{O}(n)$ linear time using a hash map.

---

## 5. Approach 3 — Optimal

### Idea
Maintain a hash table `seen` mapping `value -> index`.
As we iterate through `nums` with index `i`:
1. Calculate the required complement: $\text{complement} = \text{target} - \text{nums}[i]$.
2. Query `seen` for `complement`.
3. If `complement` exists in `seen`, return `{seen[complement], i}`.
4. Otherwise, record the current element `seen[nums[i]] = i` and continue.

Checking the map *before* inserting `nums[i]` automatically ensures an element is never matched with itself ($i \neq j$).

### C++17 Code
```cpp
#include <vector>
#include <unordered_map>

class Solution {
public:
    std::vector<int> twoSum(const std::vector<int>& nums, int target) {
        // Maps: number_value -> original_index
        std::unordered_map<int, int> seen;
        int n = static_cast<int>(nums.size());
        
        // Reserve bucket space to minimize rehashing overhead
        seen.reserve(n);
        
        for (int i = 0; i < n; i++) {
            int complement = target - nums[i];
            
            auto it = seen.find(complement);
            if (it != seen.end()) {
                return {it->second, i};
            }
            
            seen[nums[i]] = i;
        }
        
        return {};
    }
};
```

### Complexity Derivation
- **Time Complexity**: $\mathcal{O}(n)$ average — We perform a single pass over the $n$ elements. Each `find` and insertion in `std::unordered_map` operates in $\mathcal{O}(1)$ average time.
- **Space Complexity**: $\mathcal{O}(n)$ — In the worst case (pair found at the very last step), the hash table stores $n - 1$ entries. This cannot be reduced below $\mathcal{O}(n)$ auxiliary space if single-pass $\mathcal{O}(n)$ time is required on unsorted data without mutating the input array.

### Why This Is Optimal
Finding two arbitrary elements in an unsorted array has an information-theoretic lower bound of $\Omega(n)$ because every element may need to be inspected at least once. This approach matches the $\mathcal{O}(n)$ lower bound.

---

## 6. Dry Run

**Sample Input**: `nums = [2, 7, 11, 15]`, `target = 9`

| Step `i` | Current `nums[i]` | Complement (`9 - nums[i]`) | State of `seen` map before check | `complement` in `seen`? | Action / Return |
|:---:|:---:|:---:|:---:|:---:|---|
| `0` | `2` | `7` | `{}` | ❌ No | Insert `seen[2] = 0` |
| `1` | `7` | `2` | `{2: 0}` | ✅ **Yes** (`seen[2] == 0`) | **Return `{0, 1}`** 🎉 |

---

## 7. Edge Cases & Common Bugs

### Edge Cases Handled
1. **Duplicates in Input** (`nums = [3, 3]`, `target = 6`):
   - At $i = 0$, `seen[3] = 0`.
   - At $i = 1$, complement is $6 - 3 = 3$. `seen` contains $3$ at index $0$. Returns `{0, 1}` correctly without collision bugs.
2. **Negative Numbers** (`nums = [-3, 4, 3, 90]`, `target = 0`):
   - Handled naturally: at $i = 2$ (`nums[2] = 3`), complement is $0 - 3 = -3$. Map contains `-3` at index 0. Returns `{0, 2}`.
3. **Large Integers** ($\text{nums}[i] = 10^9, \text{target} = 2 \times 10^9$):
   - Standard 32-bit signed integer (`int`) in C++ covers up to $\approx 2.14 \times 10^9$. Subtraction $2 \cdot 10^9 - 10^9 = 10^9$ does not overflow `int`.

### Common Bugs to Avoid
- **Inserting Before Checking**: If you write `seen[nums[i]] = i` before calling `seen.find(complement)`, then for `nums = [3, 2, 4]`, `target = 6`, at $i = 0$, complement is $3$. It will find itself and erroneously return `{0, 0}`.
- **Rehashing Overhead**: In competitive programming, repeatedly inserting into `std::unordered_map` without `reserve()` causes multiple internal table reallocations. Call `seen.reserve(n)` for maximum speed.

---

## 8. Follow-Up Questions (Interview Style)

### Q1: What if the input array is guaranteed to be already sorted?
- **Answer**: If `nums` is sorted, we can avoid the $\mathcal{O}(n)$ hash table entirely. Using the **Two-Pointer technique** on `nums` directly gives $\mathcal{O}(n)$ Time Complexity and $\mathcal{O}(1)$ Auxiliary Space Complexity.

### Q2: What if we need to return ALL unique pairs that sum up to `target`, not just one?
- **Answer**: Sort the array in $\mathcal{O}(n \log n)$. Use two pointers (`left = 0`, `right = n - 1`). Whenever a valid sum is found, record `{nums[left], nums[right]}`, then advance `left++` while skipping duplicate elements (`while (left < right && nums[left] == nums[left-1]) left++`) and decrement `right--` similarly to guarantee unique pairs.

### Q3: What if the array is too large to fit in memory (External / Streaming data)?
- **Answer**: If $n = 10^{11}$ (distributed / streaming):
  1. If streaming from a single source, pass elements through a distributed key-value store (e.g. Redis) partitioned by `hash(complement)`.
  2. In MapReduce / Spark: Partition data into buckets based on ranges or hash partitions such that if $x$ is in bucket $A$, its complement $y$ maps to bucket $B$, then join corresponding buckets.

### Q4: In C++, how do you protect `std::unordered_map` against adversarial worst-case $\mathcal{O}(n^2)$ hash collision attacks?
- **Answer**: `std::unordered_map` uses `std::hash<int>`, which is deterministic (identity hash). An adversary can craft inputs with identical modulo bucket values to force $\mathcal{O}(n)$ collisions per lookup ($\mathcal{O}(n^2)$ overall). We fix this by supplying a custom hash functor using a random seed (e.g. `custom_hash` with `splitmix64` and `chrono::steady_clock`).

### Q5: How does this pattern extend to 3Sum and 4Sum?
- **Answer**:
  - **3Sum** ($a + b + c = 0$): Sort array $\mathcal{O}(n \log n)$, fix the first element $a = \text{nums}[i]$ via a loop, and solve Two Sum for target $-a$ on the remaining sub-array using two pointers in $\mathcal{O}(n)$, yielding $\mathcal{O}(n^2)$ total time.
  - **4Sum** ($a + b + c + d = \text{target}$): Fix two elements via nested loops and run Two Sum on the remainder, yielding $\mathcal{O}(n^3)$ time.

---

## 9. Tags & Related Problems

- **Tags**: `[hash-map]`, `[two-pointers]`, `[array]`, `[striver-a2z-step3]`
- **Related Problems to Practice Next**:
  1. **Two Sum II - Input Array Is Sorted** (Step 3.2 / LC 167) — Reinforces two-pointer search with $\mathcal{O}(1)$ space.
  2. **3Sum** (Step 3.3 / LC 15) — Extends Two Sum to triplets with duplicate avoidance.
  3. **4Sum** (Step 3.3 / LC 18) — Generalizes $k$-sum reduction via recursion/two-pointers.
  4. **Subarray Sum Equals K** (Step 3.2 / LC 560) — Extends hash map complement search to continuous prefix sums.
