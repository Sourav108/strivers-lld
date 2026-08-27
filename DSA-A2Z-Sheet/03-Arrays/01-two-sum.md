# Two Sum (LeetCode #1)

> **Step / Topic**: Step 03 — Arrays (Medium)  
> **Source Link**: [LeetCode 1](https://leetcode.com/problems/two-sum/) | [TakeUForward Article](https://takeuforward.org/data-structure/two-sum-check-if-a-pair-with-given-sum-exists-in-array/)  
> **Difficulty**: Easy

---

## 1. Problem, Restated

You get a list of numbers and a target sum. Find two numbers in the list that add up to the target, and return their positions (indices), not the values themselves. Constraints that matter: $n$ can be up to $\sim 10^4 - 10^5$ depending on platform, values can be negative, and there's guaranteed to be exactly one valid pair.

---

## 2. Intuition & Pattern

This is the classic **complement search pattern** using hashing. For every number $x$ you visit, the only thing you need to know is: *"have I already seen $\text{target} - x$ earlier in the array?"* If yes, you're done — no need to look at every pair.

The naive instinct is to check every pair of numbers ($\mathcal{O}(n^2)$), but the moment you realize the question is really *"does a specific value exist among the numbers I've already passed,"* it becomes a lookup problem — and a hash map gives $\mathcal{O}(1)$ average lookup. The clue that should trigger this pattern in future problems: *"find a pair/subset satisfying a sum condition"* + *"return indices, not sorted values."*

---

## 3. Approach 1 — Brute Force

Check every pair $(i, j)$ and see if they sum to `target`.

```cpp
#include <vector>
using namespace std;

vector<int> twoSumBrute(vector<int>& nums, int target) {
    int n = nums.size();
    for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
            if (nums[i] + nums[j] == target) {
                return {i, j};
            }
        }
    }
    return {-1, -1}; // no valid pair (shouldn't happen per constraints)
}
```

- **Time Complexity**: $\mathcal{O}(n^2)$ — for each of the $n$ elements, we scan up to $n$ more elements ahead of it.
- **Space Complexity**: $\mathcal{O}(1)$ — no extra data structure beyond the output.
- **Why it's not good enough**: at $n = 10^5$, $n^2$ is $10^{10}$ operations — will TLE well before that, typically anything past $n \approx 10^4$ on a 1–2 second limit.

---

## 4. Approach 2 — Better

No meaningful intermediate step — the optimal approach below removes the brute force's bottleneck directly.

---

## 5. Approach 3 — Optimal

Walk the array once. At each element $x$, first check whether $\text{target} - x$ is already in a hash map you've built from elements seen before this one. If it is, you've found your pair immediately — return the stored index and the current index. If not, add $x$ (and its index) to the map and move on.

The key insight: you never need to look ahead. Every pair gets checked exactly once, at the moment its second element is reached, by asking the map instead of re-scanning the array.

```cpp
#include <vector>
#include <unordered_map>
using namespace std;

vector<int> twoSumOptimal(vector<int>& nums, int target) {
    unordered_map<int, int> seen; // value -> index
    for (int i = 0; i < (int)nums.size(); i++) {
        int complement = target - nums[i];
        auto it = seen.find(complement);
        if (it != seen.end()) {
            return {it->second, i};
        }
        seen[nums[i]] = i;
    }
    return {-1, -1};
}
```

- **Time Complexity**: $\mathcal{O}(n)$ — one pass, and each hash map insert/lookup is $\mathcal{O}(1)$ on average.
- **Space Complexity**: $\mathcal{O}(n)$ — worst case you store almost every element in the map before finding the pair.
- **Can it be reduced further?** Not in the general (unsorted) case — you fundamentally need either a full pass with $\mathcal{O}(n)$ extra memory, or a sort first (which costs $\mathcal{O}(n \log n)$ time to then get $\mathcal{O}(1)$ extra space with two pointers, trading time for space).
- **Why this is optimal**: you must inspect every element at least once ($\Omega(n)$ lower bound, since the answer could be at the very end), and this approach does exactly $\mathcal{O}(n)$ work — no better asymptotic complexity is possible for the unsorted case.

---

## 6. Dry Run

`nums = [2, 7, 11, 15]`, `target = 9`

| `i` | `nums[i]` | `complement` | `seen` (before this step) | action |
|:---:|:---:|:---:|:---:|:---:|
| `0` | `2` | `7` | `{}` | not found $\rightarrow$ insert `2:0` |
| `1` | `7` | `2` | `{2:0}` | found `2` at index `0` $\rightarrow$ return `{0, 1}` |

**Output**: `[0, 1]` ($2 + 7 = 9$). ✅

---

## 7. Edge Cases & Common Bugs

- **No valid pair exists**: shouldn't happen per LeetCode's constraints, but guard against it if adapting this for a variant.
- **Duplicate values**, e.g. `nums = [3, 3]`, `target = 6` — works correctly here since you check the map before inserting the current element, so you never match an element with itself.
- **Negative numbers**, e.g. `nums = [-3, 4, 1]`, `target = 1` — works fine, hashing doesn't care about sign.
- **Common bug #1**: inserting `nums[i]` into the map before checking the complement — this lets an element pair with itself when $2 \times \text{nums}[i] == \text{target}$, producing a wrong `{i, i}` answer.
- **Common bug #2**: overwriting the map value if a number repeats — if you always store the latest index and the earlier occurrence was the correct pair, you'd get a wrong index (rare, but be aware; storing latest vs earliest changes which valid answer you return, though either is usually accepted since the problem states any valid pair works).

---

## 8. Follow-Up Questions (Interview Style)

- **Q1: What if the array were already sorted?**  
  **A**: Use two pointers instead of a hash map — one at the start, one at the end. If the sum is too small, move the left pointer right; too big, move the right pointer left. This gives $\mathcal{O}(n)$ time and $\mathcal{O}(1)$ extra space (better than the hash map's $\mathcal{O}(n)$ space), but only works because sortedness lets you decide a direction to move without a lookup.

- **Q2: What if you need to return all pairs that sum to target, not just one?**  
  **A**: Still use a hash map/set, but instead of returning immediately on a match, collect all pairs. Watch out for duplicate pairs if the array has repeated values — dedupe by sorting each pair or using a set of pairs.

- **Q3: What if the input is a stream of numbers, not a fixed array?**  
  **A**: The hash map approach adapts naturally — it's already a single left-to-right pass. Maintain the map across incoming elements; check the complement as each new number arrives. This makes it a genuinely online algorithm, $\mathcal{O}(1)$ amortized work per incoming number.

- **Q4: Can you do it with $\mathcal{O}(1)$ extra space on unsorted input?**  
  **A**: Not without changing the time complexity — you'd need to sort first ($\mathcal{O}(n \log n)$) which then allows $\mathcal{O}(1)$-space two pointers, but you lose the original indices unless you store them alongside the values before sorting (which itself costs $\mathcal{O}(n)$ space, defeating the purpose). So on unsorted input, there's a genuine time-space tradeoff, not a free win.

- **Q5: What if there could be more than one valid answer and you must return the lexicographically smallest index pair?**  
  **A**: The hash map approach as written already returns the pair with the smallest second index (since it returns the moment a match is found while scanning left to right) with the corresponding earliest-seen first index — which naturally satisfies *"smallest second index,"* and among ties for the first index, it's whichever occurred first in the map, so no extra work is needed for this specific tie-break.

---

## 9. Tags & Related Problems

- **Tags**: `Array`, `Hashing`, `Complement Search`
- **Related problems (same pattern, practice next)**:
  - **Two Sum II — Input Array is Sorted**: same idea but with two pointers since sortedness is given.
  - **3Sum**: fix one element, reduce to Two Sum on the rest.
  - **4Sum**: fix two elements, reduce to Two Sum on the rest.
  - **Subarray Sum Equals K**: same *"complement in a hash map"* idea, but on prefix sums instead of raw values.
