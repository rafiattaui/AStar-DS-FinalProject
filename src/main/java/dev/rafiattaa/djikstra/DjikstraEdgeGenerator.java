package dev.rafiattaa.djikstra;
import java.util.*;

public class DjikstraEdgeGenerator {

    /**
     * Generates a list of edges for a directed weighted graph.
     * @param V Number of vertices
     * @param E Number of edges to generate
     * @param maxWeight Maximum edge weight
     * @return int[][] edges where each edge is {from, to, weight}
     */
    public static int[][] generateEdges(int V, int E, int maxWeight) {
        Random random = new Random();
        Set<String> existingEdges = new HashSet<>();
        List<int[]> edgesList = new ArrayList<>();

        // Optional: Make sure the graph is at least weakly connected by linking vertices in a chain
        for (int i = 0; i < V - 1; i++) {
            int weight = 1 + random.nextInt(maxWeight);
            edgesList.add(new int[]{i, i + 1, weight});
            existingEdges.add(i + "->" + (i + 1));
        }

        // Now add random edges until total edges count reaches E
        while (edgesList.size() < E) {
            int from = random.nextInt(V);
            int to = random.nextInt(V);

            if (from == to) continue; // No self-loop
            String edgeKey = from + "->" + to;
            if (existingEdges.contains(edgeKey)) continue; // no duplicate edges

            int weight = 1 + random.nextInt(maxWeight);
            edgesList.add(new int[]{from, to, weight});
            existingEdges.add(edgeKey);
        }

        return edgesList.toArray(new int[0][]);
    }
}
