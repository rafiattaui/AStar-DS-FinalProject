package dev.rafiattaa;

import java.text.DecimalFormat;
import java.util.*;

public class Benchmark {

    // Generate random edge list with a fixed number of edges for benchmarking Dijkstra's algorithm
    public static int[][] GenerateRandomEdge(int v, int numEdges) {
        Random random = new Random();
        Set<String> edgeSet = new HashSet<>();
        List<int[]> edgeList = new ArrayList<>();

        // Ensure that the number of edges is feasible (<= V*(V-1))
        if (numEdges > v * (v - 1)) {
            throw new IllegalArgumentException("Number of edges cannot be greater than V*(V-1) for a directed graph.");
        }

        // Generate random edges
        while (edgeList.size() < numEdges) {
            int u = random.nextInt(v);  // Random vertex u (0 to v-1)
            int ve = random.nextInt(v);  // Random vertex v (0 to v-1)

            // Ensure no self-loop (u != ve)
            if (u != ve) {
                // Ensure no duplicate edges (u -> ve and ve -> u are considered same)
                String edgeKey = u < ve ? u + "-" + ve : ve + "-" + u;

                if (!edgeSet.contains(edgeKey)) {
                    int weight = random.nextInt(10) + 1;  // Random weight between 1 and 10
                    edgeList.add(new int[] {u, ve, weight});
                    edgeSet.add(edgeKey);  // Mark this edge as used
                }
            }
        }

        // Convert List to 2D array for easy use in Dijkstra's algorithm
        int[][] edgeArray = new int[edgeList.size()][3];
        for (int i = 0; i < edgeList.size(); i++) {
            edgeArray[i] = edgeList.get(i);
        }

        return edgeArray;
    }

    public static double measurePerformance(int V, int[][] edges, boolean useFibonacci){
        // Capture the start time in nanoseconds
        long startTime = System.nanoTime();

        // Run the Dijkstra algorithm using the given parameters
        UnifiedDijkstra.run(V, edges, useFibonacci);

        // Capture the end time in nanoseconds
        long endTime = System.nanoTime();

        // Calculate the time taken and return it
        double timeTaken = (endTime - startTime) / 1_000_000.0;
        DecimalFormat df = new DecimalFormat("#.##");
        return Double.parseDouble(df.format(timeTaken));
        }

    // Simple benchmark test with fixed number of edges
    public static void test(int V, int numEdges, int repeats) {
        for (int i = 0; i < repeats; i++) {
            int[][] edges = GenerateRandomEdge(V, numEdges);

            // Print the generated edges for verification
            System.out.println("Run " + (i + 1) + ":");
            //for (int[] edge : edges) {
            //System.out.println(Arrays.toString(edge));
            //}
                System.out.println();
                double fibTime = measurePerformance(V, edges, true);
                System.out.println("Fibonacci Heap Runtime: " + fibTime + "ms\n");

                double minTime = measurePerformance(V, edges, false);
                System.out.println("Min Heap Runtime: " + minTime +"ms\n");

        }
    }

    public static void main(String[] args) {
        int V = 40;  // Number of vertices
        int numEdges = 50;  // Number of edges
        int repeats = 1;  // Number of times to repeat the test

        test(V, numEdges, repeats);  // Run the benchmark test
    }
}
