# 🤖 AI Prompt Template for DSA Solutions

Use this standardized prompt to generate intuition-first, interview-ready DSA solutions in C++ (with Brute, Better, and Optimal approaches).

---

## 📋 Copy & Paste Prompt Block

```markdown
You are an expert Data Structures & Algorithms instructor and competitive programmer.
Generate a complete, high-quality, intuition-first solution in C++ for the following Striver A2Z DSA problem.

### 📌 Problem Information:
- **Problem Name**: [PROBLEM_NAME]
- **Sheet Step**: [STEP_NUMBER_AND_TOPIC] (e.g., Step 3.2: Medium Arrays)
- **Problem Link**: [LEETCODE_OR_TUF_LINK]
- **Difficulty**: [Easy / Medium / Hard]

### 📝 Problem Statement & Constraints:
[PASTE_PROBLEM_STATEMENT_AND_CONSTRAINTS_HERE]

---

### 🎯 Required Output Structure:

Please structure your response strictly using the following format:

1. **💡 Intuition & Core Pattern**:
   - What is the fundamental data structure / algorithmic pattern? (e.g. Monotonic Stack, Two Pointers, Floyd's Cycle, Greedy Interval Sort).
   - Why do naive approaches fail or underperform?

2. **🛠️ Approach Breakdown**:
   - **Approach 1: Brute Force**
     - Idea, C++ Code, Time Complexity derivation, Space Complexity derivation.
   - **Approach 2: Better / Sub-Optimal (if applicable)**
     - Idea, C++ Code, Time Complexity derivation, Space Complexity derivation.
   - **Approach 3: Optimal (Production / Interview-Ready)**
     - Complete, idiomatic, clean C++20 code with comments.
     - Mathematical Time Complexity ($O(...)$) with derivation.
     - Auxiliary Space Complexity ($O(...)$) with derivation.

3. **📊 Visual Dry Run / Trace**:
   - Step-by-step table or pointer diagram tracing an example input through the optimal algorithm.

4. **⚠️ Corner Cases & Pitfalls**:
   - Empty input, single element, negative numbers, duplicates, 32-bit integer overflow (`1e9` additions requiring `long long`), etc.

5. **💬 Interview Follow-ups & Variations**:
   - 2–3 common follow-up questions interviewers ask after seeing the optimal solution.
```

---

## 🌟 Reference Example

See [`EXAMPLE_Two_Sum.md`](./EXAMPLE_Two_Sum.md) to review the exact expected output formatting and depth.
