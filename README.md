# Closest Pair of Points – Custom Algorithm vs Brute Force vs Divide & Conquer

## Task Description

This project was part of a small competition held during the *Algorithms and Data Structures* course in Klagenfurt University. The challenge:  
**Find the closest pair of points in a randomly generated array** — as efficiently as possible.  
The only restriction: **the classical Divide & Conquer algorithm was not allowed** for the custom solution.
This algorithm took the first place among all solutions.

### Algorithm Logic

1. **Input**: An array of 2D points with integer coordinates.
2. **Sort points by X-coordinate** using an in-place quicksort.
3. **Initialize closest pair**: Start with the distance between the first two points as the current minimum.
4. **Iterate through all pairs**:
   - For each point `i`, only consider points `j > i` as long as the X-distance between them is less than the current minimum.
   - As soon as `dx² >= minDist²`, break the inner loop.
   - Otherwise, compute full Euclidean distance and update the closest pair if it's smaller.

### Why It Works

- After sorting, most point pairs are **quickly ruled out** using only X-coordinate distance.
- The **early break condition** avoids unnecessary distance calculations.
- All valid pairs are still checked, preserving correctness.
- In practice, this yields better performance than Divide & Conquer due to lower overhead and better cache locality.

## Correctness Check

All algorithms (Brute Force, Divide & Conquer, and Mine) return the **same correct result**.

## Final Performance Table

| Size    | My Algorithm (ms) | Brute Force (ms) | Divide & Conquer (ms) |
|---------|-------------------|------------------|------------------------|
| 128     | 0                 | 0                | 2                      |
| 256     | 0                 | 0                | 0                      |
| 512     | 0                 | 1                | 0                      |
| 1024    | 0                 | 1                | 1                      |
| 2048    | 0                 | 2                | 2                      |
| 4096    | 1                 | 9                | 4                      |
| 8192    | 2                 | 41               | 19                     |
| 16384   | 3                 | 138              | 43                     |
| 32768   | 5                 | 483              | 54                     |
| 65536   | 8                 | 1951             | 79                     |
| 131072  | 15                | 7667             | 121                    |
| 262144  | 32                | 31337            | 221                    |
