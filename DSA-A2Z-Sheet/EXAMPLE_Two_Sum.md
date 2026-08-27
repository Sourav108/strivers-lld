# Two Sum (LeetCode #1)

> **Step**: Step 3.2: Medium Arrays  
> **Difficulty**: 🟢 `Easy` / 🟡 `Medium`  
> **Link**: [LeetCode #1 - Two Sum](https://leetcode.com/problems/two-sum/) | [TakeUForward Article](https://takeuforward.org/data-structure/two-sum-check-if-a-pair-with-given-sum-exists-in-array/)

---

## 💡 1. Intuition & Core Pattern

- **The Problem**: Given an array of integers `nums` and an integer `target`, return indices of the two numbers such that they add up to `target`. Each input has exactly one solution, and you may not use the same element twice.
- **Pattern Recognition**: 
  - For any element $x = 	ext{nums}[i]$, we are searching for its complement $y = 	ext{target} - x$.
  - Rather than re-scanning the array for $y$ in $O(N)$ time, we can store previously seen elements in an **Unordered Hash Map** (`std::unordered_map`) for $O(1)$ average-time lookups.

---

## 🛠️ 2. Approach Breakdown

### 🐢 Approach 1: Brute Force (Nested Loops)
Check every pair $(i, j)$ where $i < j$.

```cpp
#include <vector>

class Solution {
public:
    std::vector<int> twoSum(std::vector<int>& nums, int target) {
        int n = nums.size();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (nums[i] + nums[j] == target) {
                    return {i, j};
                }
            }
        }
        return {};
    }
};
```
- **Time Complexity**: $\mathcal{O}(N^2)$ — Two nested loops iterating over all pairs.
- **Space Complexity**: $\mathcal{O}(1)$ — No additional data structures used.

---

### 🏃 Approach 2: Better (Sort + Two Pointers - Variant for Exists/Values)
If only returning `true`/`false` or the values themselves, sort the array and use two pointers. (To return indices, we must sort index-value pairs).

```cpp
#include <vector>
#include <algorithm>

class Solution {
public:
    std::vector<int> twoSum(std::vector<int>& nums, int target) {
        int n = nums.size();
        std::vector<std::pair<int, int>> indexedNums(n);
        for (int i = 0; i < n; i++) {
            indexedNums[i] = {nums[i], i};
        }
        
        std::sort(indexedNums.begin(), indexedNums.end());
        
        int left = 0, right = n - 1;
        while (left < right) {
            int sum = indexedNums[left].first + indexedNums[right].first;
            if (sum == target) {
                return {indexedNums[left].second, indexedNums[right].second};
            } else if (sum < target) {
                left++;
            } else {
                right--;
            }
        }
        return {};
    }
};
```
- **Time Complexity**: $\mathcal{O}(N \log N)$ — Dominated by the sorting step.
- **Space Complexity**: $\mathcal{O}(N)$ — To store the `std::pair<value, original_index>`.

---

### 🚀 Approach 3: Optimal (Single-Pass Hash Map)
As we traverse `nums`, we check if `target - nums[i]` is already in the map. If yes, we found our pair. If no, we insert `nums[i] -> i` into the map.

```cpp
#include <vector>
#include <unordered_map>

class Solution {
public:
    std::vector<int> twoSum(std::vector<int>& nums, int target) {
        // Map stores: key = number value, value = array index
        std::unordered_map<int, int> seen;
        
        for (int i = 0; i < (int)nums.size(); i++) {
            int complement = target - nums[i];
            
            // Check if complement exists in our hash map
            if (seen.find(complement) != seen.end()) {
                return {seen[complement], i};
            }
            
            // Record current number and its index
            seen[nums[i]] = i;
        }
        
        return {}; // No solution found (problem guarantees one exists)
    }
};
```

- **Time Complexity**: $\mathcal{O}(N)$ average — Single pass through the array. Hash map lookups and inserts operate in $O(1)$ amortized time.
- **Space Complexity**: $\mathcal{O}(N)$ — Hash map stores at most $N$ key-value pairs.

---

## 📊 3. Visual Dry Run

**Input**: `nums = [2, 7, 11, 15]`, `target = 9`

| Step | Index `i` | Value `nums[i]` | Complement `target - nums[i]` | Map State Before Step | Map Contains Complement? | Action |
|---|---|---|---|---|---|---|
| 1 | 0 | 2 | 7 ($9 - 2$) | `{}` | ❌ No | Insert `seen[2] = 0` |
| 2 | 1 | 7 | 2 ($9 - 7$) | `{2: 0}` | ✅ **Yes** (`seen[2] == 0`) | **Return `{0, 1}`** 🎉 |

---

## ⚠️ 4. Corner Cases & Pitfalls

1. **Using Same Element Twice**:
   - If `nums = [3, 2, 4]`, `target = 6`. At index 0 (`nums[0] = 3`), complement is `3`. If we insert before checking, `seen.find(3)` would return index 0. By checking *before* inserting, we prevent using the same index twice.
2. **Duplicate Elements**:
   - `nums = [3, 3]`, `target = 6`. Handled cleanly: at index 1, complement `3` is found from index 0.
3. **Integer Overflow**:
   - If array values are close to `INT_MAX` or `INT_MIN`, ensure subtraction `target - nums[i]` does not overflow. In standard C++, `int` is sufficient when inputs are within $[-10^9, 10^9]$ and target is within $[-10^9, 10^9]$.

---

## 💬 5. Interview Follow-ups & Variations

- **Q1**: *What if the array is already sorted?*
  - **A**: Use the **Two-Pointer Approach** (Approach 2) in $\mathcal{O}(N)$ time and $\mathcal{O}(1)$ extra space without any hash map.
- **Q2**: *What if hash collisions make `std::unordered_map` slow in C++?*
  - **A**: In competitive programming (e.g. Codeforces anti-hash tests), `unordered_map` can degrade to $O(N^2)$ due to hash collisions. We can pass a custom splitmix64 hash functor to restore $O(1)$ performance.
- **Q3**: *How does this extend to 3Sum and 4Sum?*
  - **A**: 3Sum fixes one element and reduces to Two-Sum (Two-Pointer on sorted array in $O(N^2)$). 4Sum fixes two elements and reduces to Two-Sum ($O(N^3)$).
