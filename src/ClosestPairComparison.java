import java.util.*;

class Point {
    private double x, y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() { return x; }
    public double getY() { return y; }

    @Override
    public String toString() {
        return String.format("(%.2f, %.2f)", x, y);
    }
}

class LogAP {
    private int comparisons = 0;

    public void enter() { comparisons = 0; }
    public void count() { comparisons++; }
    public void exit() {}
    public int getComparisons() { return comparisons; }
}

public class ClosestPairComparison {
    private static LogAP logAP = new LogAP();

    // Your original algorithm (optimized brute-force)
    private static Point[] findCpp(Point[] points) {
        Point[] cpp = new Point[2];
        cpp[0] = points[0];
        cpp[1] = points[1];
        double minDist = dist(cpp[0], cpp[1]);
        logAP.enter();
        quickSort(points, 0, points.length - 1);
        for (int i = 0; i < points.length; i++) {
            for (int j = i + 1; j < points.length; j++) {
                double dx = points[j].getX() - points[i].getX();
                if (dx * dx >= minDist * minDist) break;
                double d = dist(points[i], points[j]);
                logAP.count();
                if (d < minDist) {
                    minDist = d;
                    cpp[0] = points[i];
                    cpp[1] = points[j];
                }
            }
        }
        logAP.exit();
        return cpp;
    }

    // Classical brute-force O(n²)
    private static Point[] bruteForceCpp(Point[] points) {
        Point[] cpp = new Point[2];
        cpp[0] = points[0];
        cpp[1] = points[1];
        double minDist = dist(cpp[0], cpp[1]);

        for (int i = 0; i < points.length; i++) {
            for (int j = i + 1; j < points.length; j++) {
                double d = dist(points[i], points[j]);
                if (d < minDist) {
                    minDist = d;
                    cpp[0] = points[i];
                    cpp[1] = points[j];
                }
            }
        }
        return cpp;
    }

    // Divide and conquer O(n log n)
    private static Point[] divideAndConquerCpp(Point[] points) {
        Point[] sortedByX = points.clone();
        Point[] sortedByY = points.clone();

        Arrays.sort(sortedByX, Comparator.comparingDouble(Point::getX));
        Arrays.sort(sortedByY, Comparator.comparingDouble(Point::getY));

        return closestPairRec(sortedByX, sortedByY, 0, points.length - 1);
    }

    private static Point[] closestPairRec(Point[] sortedByX, Point[] sortedByY, int left, int right) {
        if (right - left <= 3) {
            Point[] subset = Arrays.copyOfRange(sortedByX, left, right + 1);
            return bruteForceCpp(subset);
        }

        int mid = (left + right) / 2;
        Point midPoint = sortedByX[mid];

        List<Point> leftY = new ArrayList<>();
        List<Point> rightY = new ArrayList<>();

        for (Point p : sortedByY) {
            if (p.getX() <= midPoint.getX()) {
                leftY.add(p);
            } else {
                rightY.add(p);
            }
        }

        Point[] leftResult = closestPairRec(sortedByX, leftY.toArray(new Point[0]), left, mid);
        Point[] rightResult = closestPairRec(sortedByX, rightY.toArray(new Point[0]), mid + 1, right);

        double leftDist = dist(leftResult[0], leftResult[1]);
        double rightDist = dist(rightResult[0], rightResult[1]);

        Point[] minResult;
        double minDist;
        if (leftDist < rightDist) {
            minResult = leftResult;
            minDist = leftDist;
        } else {
            minResult = rightResult;
            minDist = rightDist;
        }

        List<Point> strip = new ArrayList<>();
        for (Point p : sortedByY) {
            if (Math.abs(p.getX() - midPoint.getX()) < minDist) {
                strip.add(p);
            }
        }

        for (int i = 0; i < strip.size(); i++) {
            for (int j = i + 1; j < strip.size() &&
                    (strip.get(j).getY() - strip.get(i).getY()) < minDist; j++) {
                double d = dist(strip.get(i), strip.get(j));
                if (d < minDist) {
                    minDist = d;
                    minResult[0] = strip.get(i);
                    minResult[1] = strip.get(j);
                }
            }
        }

        return minResult;
    }

    private static double dist(Point p1, Point p2) {
        double dx = p1.getX() - p2.getX();
        double dy = p1.getY() - p2.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private static void quickSort(Point[] points, int low, int high) {
        if (low < high) {
            int pi = partition(points, low, high);
            quickSort(points, low, pi - 1);
            quickSort(points, pi + 1, high);
        }
    }

    private static int partition(Point[] points, int low, int high) {
        double pivot = points[high].getX();
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (points[j].getX() <= pivot) {
                i++;
                Point temp = points[i];
                points[i] = points[j];
                points[j] = temp;
            }
        }
        Point temp = points[i + 1];
        points[i + 1] = points[high];
        points[high] = temp;
        return i + 1;
    }

    private static Point[] generateRandomPoints(int n) {
        Random rand = new Random(42);
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            points[i] = new Point(rand.nextDouble() * 1000, rand.nextDouble() * 1000);
        }
        return points;
    }

    private static long measureTime(Runnable algorithm) {
        long start = System.nanoTime();
        algorithm.run();
        long end = System.nanoTime();
        return (end - start) / 1_000_000;
    }

    public static void main(String[] args) {
        int[] sizes = {128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768, 65536, 131072, 262144};

        System.out.println("=== COMPARISON OF CLOSEST PAIR ALGORITHMS ===\n");
        System.out.printf("%-8s %-15s %-15s %-15s\n", "Size", "My Algorithm", "Brute Force", "Divide & Conquer");
        System.out.printf("%-8s %-15s %-15s %-15s\n", "", "(ms)", "(ms)", "(ms)");
        System.out.println("-".repeat(60));

        List<TestResult> results = new ArrayList<>();

        for (int n : sizes) {
            Point[] points = generateRandomPoints(n);

            Point[] pointsCopy1 = points.clone();
            long timeOptimized = measureTime(() -> findCpp(pointsCopy1));

            Point[] pointsCopy2 = points.clone();
            long timeBrute = measureTime(() -> bruteForceCpp(pointsCopy2));

            Point[] pointsCopy3 = points.clone();
            long timeDivideConquer = measureTime(() -> divideAndConquerCpp(pointsCopy3));

            System.out.printf("%-8d %-15d %-15s %-15d\n",
                    n, timeOptimized,
                    (timeBrute == -1 ? "---" : String.valueOf(timeBrute)),
                    timeDivideConquer);

            results.add(new TestResult(n, timeOptimized, timeBrute, timeDivideConquer));
        }

        System.out.println("\n=== CORRECTNESS CHECK ===");
        Point[] testPoints = generateRandomPoints(100);

        Point[] result1 = findCpp(testPoints.clone());
        Point[] result2 = bruteForceCpp(testPoints.clone());
        Point[] result3 = divideAndConquerCpp(testPoints.clone());

        double dist1 = dist(result1[0], result1[1]);
        double dist2 = dist(result2[0], result2[1]);
        double dist3 = dist(result3[0], result3[1]);

        System.out.printf("My Algorithm: %.6f between %s and %s\n", dist1, result1[0], result1[1]);
        System.out.printf("Brute Force: %.6f between %s and %s\n", dist2, result2[0], result2[1]);
        System.out.printf("Divide & Conquer: %.6f between %s and %s\n", dist3, result3[0], result3[1]);

        boolean correct = Math.abs(dist1 - dist2) < 1e-9 && Math.abs(dist2 - dist3) < 1e-9;
        System.out.println("All algorithms produce the same result: " + (correct ? "YES" : "NO"));
    }

    static class TestResult {
        int size;
        long timeOptimized;
        long timeBrute;
        long timeDivideConquer;

        TestResult(int size, long timeOptimized, long timeBrute, long timeDivideConquer) {
            this.size = size;
            this.timeOptimized = timeOptimized;
            this.timeBrute = timeBrute;
            this.timeDivideConquer = timeDivideConquer;
        }
    }
}