package dev.rafiattaa;

import java.text.DecimalFormat;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Benchmark {

    // Measure performance of a run
    public static double measurePerformance(int V, int[][] edges, String mode) {
        long start = System.nanoTime();
        UnifiedDijkstra.run(V, edges, mode);
        long end = System.nanoTime();
        return (end - start) / 1_000_000.0;  // ms
    }

    // Run benchmarks asynchronously
    public static void benchmarkOnce(int V, int[][] edges) {
        System.out.println("\nBenchmarking with V = " + V + ", Edges = " + edges.length);

        CompletableFuture<Double> fib = CompletableFuture.supplyAsync(() -> {
            double time = measurePerformance(V, edges, "fibonacci");
            System.out.println("Fibonacci Heap Runtime: " + format(time) + " ms");
            return time;
        });

        CompletableFuture<Double> min = CompletableFuture.supplyAsync(() -> {
            double time = measurePerformance(V, edges, "minheap");
            System.out.println("Min Heap Runtime: " + format(time) + " ms");
            return time;
        });

        CompletableFuture<Double> unord = CompletableFuture.supplyAsync(() -> {
            double time = measurePerformance(V, edges, "unordered");
            System.out.println("Unordered Queue Runtime: " + format(time) + " ms");
            return time;
        });

        CompletableFuture.allOf(fib, min, unord).join();
        System.out.println("All runs completed.\n");
    }

    // Repeat benchmark to get average times
    public static void test(int V, int numEdges, int maxWeight, int repeats) {
        double totalFib = 0, totalMin = 0, totalUnord = 0;

        for (int i = 0; i < repeats; i++) {
            System.out.println("------ Test Run " + (i + 1) + " ------");
            int[][] edges = DjikstraEdgeGenerator.generateEdges(V, numEdges, maxWeight);

            try {
                CompletableFuture<Double> fib = CompletableFuture.supplyAsync(() -> measurePerformance(V, edges, "fibonacci"));
                CompletableFuture<Double> min = CompletableFuture.supplyAsync(() -> measurePerformance(V, edges, "minheap"));
                CompletableFuture<Double> unord = CompletableFuture.supplyAsync(() -> measurePerformance(V, edges, "unordered"));

                CompletableFuture.allOf(fib, min, unord).join();

                totalFib += fib.get();
                totalMin += min.get();
                totalUnord += unord.get();

                System.out.println("Fibonacci Heap Runtime: " + format(fib.get()) + " ms");
                System.out.println("Min Heap Runtime: " + format(min.get()) + " ms");
                System.out.println("Unordered Queue Runtime: " + format(unord.get()) + " ms\n");

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        System.out.println("====== Average Runtimes ======");
        System.out.println("Fibonacci Heap: " + format(totalFib / repeats) + " ms");
        System.out.println("Min Heap: " + format(totalMin / repeats) + " ms");
        System.out.println("Unordered Queue: " + format(totalUnord / repeats) + " ms");
    }

    private static String format(double value) {
        return new DecimalFormat("#.##").format(value);
    }

    public static void main(String[] args) {
        int V = 5000;
        int numEdges = 50000;
        int maxWeight = 2;
        int repeats = 3;

        test(V, numEdges, maxWeight, repeats);
    }
}
