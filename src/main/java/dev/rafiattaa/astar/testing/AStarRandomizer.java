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
        int rows = 250;
        int cols = 250;
        Point goalPoint = new Point(rows - 1, cols - 1);

        AStarRandomizer a = new AStarRandomizer(rows, cols, 0.25);
        int[][] grid = a.generate();
        System.out.println("Generating with seed: " + a.getSeed());

        Runtime rt = Runtime.getRuntime();

        // =======================
        // MinHeapAStar Benchmark
        // =======================
        MinHeapAStar minHeap = new MinHeapAStar(grid);
        rt.gc();
        long beforeMemMinHeap = rt.totalMemory() - rt.freeMemory();
        long startTimeMinHeap = System.nanoTime();

        List<Point> minHeapPath = minHeap.findPath(0, 0, goalPoint.x, goalPoint.y);

        long endTimeMinHeap = System.nanoTime();
        long afterMemMinHeap = rt.totalMemory() - rt.freeMemory();

        double durationMsMinHeap = (endTimeMinHeap - startTimeMinHeap) / 1_000_000.0;
        long usedMemMinHeap = afterMemMinHeap - beforeMemMinHeap;

        System.out.println("\n--- MinHeap A* Results ---");
        MinHeapAStar.printPath(grid, minHeapPath, new Point(0, 0), goalPoint, false);
        System.out.printf("Execution time: %.3f ms%n", durationMsMinHeap);
        System.out.printf("Memory used: %.3f MB%n", usedMemMinHeap / (1024.0 * 1024));

        // =========================
        // UnorderedAStar Benchmark
        // =========================
        UnorderedAStar unordered = new UnorderedAStar(grid);
        rt.gc();
        long beforeMemUnordered = rt.totalMemory() - rt.freeMemory();
        long startTimeUnordered = System.nanoTime();

        List<Point> unorderedPath = unordered.findPath(0, 0, goalPoint.x, goalPoint.y);

        long endTimeUnordered = System.nanoTime();
        long afterMemUnordered = rt.totalMemory() - rt.freeMemory();

        double durationMsUnordered = (endTimeUnordered - startTimeUnordered) / 1_000_000.0;
        long usedMemUnordered = afterMemUnordered - beforeMemUnordered;

        System.out.println("\n--- Unordered List A* Results ---");
        UnorderedAStar.printPath(grid, unorderedPath, new Point(0, 0), goalPoint, false);
        System.out.printf("Execution time: %.3f ms%n", durationMsUnordered);
        System.out.printf("Memory used: %.3f MB%n", usedMemUnordered / (1024.0 * 1024));
    }

}
