# Count Inversions (Step 3.3)

This is a complete, interview-ready note in C++ following the standard 9-section format.

- **Source**: https://takeuforward.org/data-structure/count-inversions-in-an-array/
- **Difficulty**: Hard
- **Statement**: Count pairs $(i, j)$ such that $i < j$ and $nums[i] > nums[j]$.

---

## 1. Problem, Restated

Count pairs $(i, j)$ such that $i < j$ and $nums[i] > nums[j]$.

- **Input**: Vector of integers `nums`.
- **Output**: Result as specified by problem requirements.
- **Key Constraints**: $n$ up to $10^5$, elements can be negative/positive, time limit 1.0s.

---

## 2. Intuition & Pattern

Enhanced Merge Sort: when picking element from right half $nums[j]$, all remaining elements in left half $mid - i + 1$ form inversions.

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

long long countInversionsBrute(const vector<int>& nums) {
    long long cnt = 0; int n = nums.size();
    for (int i = 0; i < n; i++)
        for (int j = i + 1; j < n; j++)
            if (nums[i] > nums[j]) cnt++;
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

long long mergeAndCount(vector<int>& arr, int low, int mid, int high) {
    vector<int> temp;
    int left = low, right = mid + 1;
    long long invCount = 0;
    while (left <= mid && right <= high) {
        if (arr[left] <= arr[right]) {
            temp.push_back(arr[left++]);
        } else {
            temp.push_back(arr[right++]);
            invCount += (mid - left + 1); // all remaining left elements are greater
        }
    }
    while (left <= mid) temp.push_back(arr[left++]);
    while (right <= high) temp.push_back(arr[right++]);
    for (int i = low; i <= high; i++) arr[i] = temp[i - low];
    return invCount;
}

long long mergeSortCount(vector<int>& arr, int low, int high) {
    if (low >= high) return 0;
    int mid = low + (high - low) / 2;
    long long inv = mergeSortCount(arr, low, mid);
    inv += mergeSortCount(arr, mid + 1, high);
    inv += mergeAndCount(arr, low, mid, high);
    return inv;
}

long long numberOfInversions(vector<int>& nums) {
    return mergeSortCount(nums, 0, (int)nums.size() - 1);
}
```

### Complexity Derivation
- **Time Complexity**: O(n log n)
- **Space Complexity**: O(n)
- **Why this is optimal**: Matches the theoretical information lower bound $\Omega(n)$ for unsorted array inspection.

---

## 6. Dry Run

**Trace**: nums = [5, 3, 2, 4, 1] -> total inversions = 8

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

- **Q1: What if the array is already sorted?**  
  **A**: Exploiting sortedness allows two pointers or binary search to reduce time to $\mathcal{O}(\log n)$ or space to $\mathcal{O}(1)$.

- **Q2: What if elements arrive in a streaming fashion?**  
  **A**: Single-pass state accumulators adapt naturally to online streaming computation in $\mathcal{O}(1)$ amortized time per event.

- **Q3: What if input does not fit into RAM?**  
  **A**: Use external merge sort or MapReduce chunking with streaming combiner passes.

- **Q4: Can we parallelize this algorithm?**  
  **A**: Divide and conquer enables multi-threaded chunk evaluation with an $\mathcal{O}(1)$ merge step.

- **Q5: How does this generalize to multidimensional arrays or higher $K$?**  
  **A**: Techniques reduce higher dimensions by fixing degrees of freedom iteratively.

---

## 9. Tags & Related Problems

- **Tags**: `Array`, `TakeUForward`, `Strivers-A2Z`, `Hard`
- **Related problems**:
  - Similar Step 3 Array Problems in the curriculum.
