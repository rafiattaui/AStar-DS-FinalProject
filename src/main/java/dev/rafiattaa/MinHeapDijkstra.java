package dev.rafiattaa;

import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.List;

class MinHeapDijkstra {
    static class Edge{
        int to, weight;
        public Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    static class Node implements Comparable<Node>{
        int vertex, dist;
        Node(int vertex, int dist) {
            this.vertex = vertex;
            this.dist = dist;
        }

        public int compareTo(Node other) {
            return Integer.compare(this.dist, other.dist);
        }
    }

    public static int[] dijkstra (List<List<Edge>> graph,int source){
        int n = graph.size();

        PriorityQueue<Node> pq = new PriorityQueue<>();

        boolean[] visited = new boolean[n];
        int[] dist = new int[n];

        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[source] = 0;

        pq.offer(new Node(source, 0));

        while (!pq.isEmpty()){
            Node current = pq.poll();
            int u = current.vertex;
            if (visited[u]) continue;
            visited[u] = true;

            for (Edge edge : graph.get(u)) {
                int v = edge.to;
                int weight = edge.weight;

                if (!visited[v] && dist[u] + weight < dist[v]) {
                    dist[v] = dist[u] + weight;
                    pq.offer(new Node(v, dist[v]));
                }
            }
        }
        return dist;
    }

//    public static void printAdjMatrix(List<List<Edge>> graph) {
//        int n = graph.size();
//        int[][] matrix = new int[n][n];
//
//        // Initialize with -1 to indicate no edge
//        for (int[] row : matrix)
//            Arrays.fill(row, -1);
//
//        for (int u = 0; u < n; u++) {
//            for (Edge edge : graph.get(u)) {
//                matrix[u][edge.to] = edge.weight;
//                matrix[edge.to][u] = edge.weight;
//            }
//        }
//
//        System.out.println("Adjacency Matrix:");
//        for (int[] row : matrix) {
//            for (int val : row) {
//                System.out.print((val == -1 ? "∞" : val) + "\t");
//            }
//            System.out.println();
//        }
//    }

    public static void main(String[] args) {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc(); // suggest garbage collection

        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        long start = System.nanoTime();

        int V = 5;
        List<List<Edge>> graph = new ArrayList<>();
        for (int i = 0; i < V; i++) {
            graph.add(new ArrayList<>());
        }

        // Graph edges: u -> v with weight
        graph.get(0).add(new Edge(1, 10));
        graph.get(0).add(new Edge(4, 3));
        graph.get(1).add(new Edge(2, 2));
        graph.get(2).add(new Edge(3, 9));
        graph.get(4).add(new Edge(1, 1));
        graph.get(4).add(new Edge(2, 8));

//        printAdjMatrix(graph);

        int[] distances = dijkstra(graph, 0);

        System.out.println("\nShortest distances from node 0:");
        for (int i = 0; i < distances.length; i++) {
            System.out.println("To node " + i + " = " + distances[i]);
        }
    }
}

// time complexity (approx. 25 ms): O((V+E) log V)
// space complexity (approx 1 mb): O(V+E)
//

// ADDITIONAL INFO
//time and space complexity for printing matrix: O(V^2)