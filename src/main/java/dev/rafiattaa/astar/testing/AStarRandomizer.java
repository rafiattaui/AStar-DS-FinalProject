package dev.rafiattaa.astar.testing;

import dev.rafiattaa.astar.MinHeapAStar;
import dev.rafiattaa.astar.Point;
import dev.rafiattaa.astar.UnorderedAStar;

import java.util.List;
import java.util.Random;

public class AStarRandomizer {
    private final int rows;
    private final int cols;
    private final double obstacleChance;
    private final long seed;
    private final Random random;

    public AStarRandomizer(int rows, int cols, double obstacleChance) {
        this(rows, cols, obstacleChance, new Random().nextLong()); // generate random seed
    }

    public AStarRandomizer(int rows, int cols, double obstacleChance, long seed) {
        this.rows = rows;
        this.cols = cols;
        this.obstacleChance = obstacleChance;
        this.seed = seed;
        this.random = new Random(seed);
    }

    public int[][] generate() {
        int[][] grid = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = (random.nextDouble() < obstacleChance) ? 1 : 0;
            }
        }
        return grid;
    }

    public long getSeed() {
        return seed;
    }


    public static void main(String[] args) {
        int rows = 500;
        int cols = rows;
        int trials = 20;
        double obstacleChance = 0.40;

        Runtime rt = Runtime.getRuntime();

        double totalTimeMinHeap = 0;
        double totalTimeUnordered = 0;
        long totalMemMinHeap = 0;
        long totalMemUnordered = 0;

        int noPathMinHeap = 0;
        int noPathUnordered = 0;
        int UnorderedFaster = 0;
        int MinHeapFaster = 0;

        for (int t = 1; t <= trials; t++) {
            AStarRandomizer a = new AStarRandomizer(rows, cols, obstacleChance);
            int[][] grid = a.generate();
            Point goalPoint = new Point(rows - 1, cols - 1);
            System.out.println("\n=== Trial " + t + " | Seed: " + a.getSeed() + " ===");

            // ----- MinHeapAStar -----
            MinHeapAStar minHeap = new MinHeapAStar(grid);
            rt.gc();
            long beforeMemMinHeap = rt.totalMemory() - rt.freeMemory();
            long startTimeMinHeap = System.nanoTime();

            List<Point> minHeapPath = minHeap.findPath(0, 0, goalPoint.x, goalPoint.y);

            long endTimeMinHeap = System.nanoTime();
            long afterMemMinHeap = rt.totalMemory() - rt.freeMemory();

            double durationMsMinHeap = (endTimeMinHeap - startTimeMinHeap) / 1_000_000.0;
            long usedMemMinHeap = afterMemMinHeap - beforeMemMinHeap;

            totalTimeMinHeap += durationMsMinHeap;
            totalMemMinHeap += usedMemMinHeap;
            if (minHeapPath.isEmpty()) noPathMinHeap++;

            System.out.printf("MinHeapA* time: %.3f ms, memory: %.3f MB%n",
                    durationMsMinHeap, usedMemMinHeap / (1024.0 * 1024));

            // ----- UnorderedAStar -----
            UnorderedAStar unordered = new UnorderedAStar(grid);
            rt.gc();
            long beforeMemUnordered = rt.totalMemory() - rt.freeMemory();
            long startTimeUnordered = System.nanoTime();

            List<Point> unorderedPath = unordered.findPath(0, 0, goalPoint.x, goalPoint.y);

            long endTimeUnordered = System.nanoTime();
            long afterMemUnordered = rt.totalMemory() - rt.freeMemory();

            double durationMsUnordered = (endTimeUnordered - startTimeUnordered) / 1_000_000.0;
            long usedMemUnordered = afterMemUnordered - beforeMemUnordered;

            totalTimeUnordered += durationMsUnordered;
            totalMemUnordered += usedMemUnordered;
            if (unorderedPath.isEmpty()) noPathUnordered++;

            System.out.printf("UnorderedA* time: %.3f ms, memory: %.3f MB%n",
                    durationMsUnordered, usedMemUnordered / (1024.0 * 1024));

            if (durationMsUnordered > durationMsMinHeap){
                MinHeapFaster++;
            } else {
                UnorderedFaster++;
            }
        }

        // ===== Averages =====
        System.out.println("\n=== AVERAGE OVER " + trials + " TRIALS ===");
        System.out.printf("MinHeap A* - Avg Time: %.3f ms, Avg Memory: %.3f MB, No Paths: %d/%d%n",
                totalTimeMinHeap / trials,
                totalMemMinHeap / (trials * 1024.0 * 1024),
                noPathMinHeap, trials);

        System.out.printf("Unordered A* - Avg Time: %.3f ms, Avg Memory: %.3f MB, No Paths: %d/%d%n\n",
                totalTimeUnordered / trials,
                totalMemUnordered / (trials * 1024.0 * 1024),
                noPathUnordered, trials);
        System.out.printf("Times Unordered was faster: %d\n",  UnorderedFaster);
        System.out.printf("Times Min-Heap was faster: %d", MinHeapFaster);
    }


}
