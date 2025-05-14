package dev.rafiattaa;

import java.util.*;

public class UnifiedDijkstra {

    // Edge class representing a connection between nodes
    static class Edge {
        int to;
        double weight;
        public Edge(int to, double weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    // Node class for Min-Heap Dijkstra
    static class MinHeapNode implements Comparable<MinHeapNode> {
        int vertex;
        double dist;

        MinHeapNode(int vertex, double dist) {
            this.vertex = vertex;
            this.dist = dist;
        }

        @Override
        public int compareTo(MinHeapNode other) {
            return Double.compare(this.dist, other.dist);
        }
    }

    // Node class for Fibonacci Heap Dijkstra
    static class FibHeapNode {
        int vertex;
        double distance;
        FibHeapNode parent, child, left, right;
        int degree;
        boolean mark;

        public FibHeapNode(int vertex, double distance) {
            this.vertex = vertex;
            this.distance = distance;
            this.left = this;
            this.right = this;
        }
    }

    // Fibonacci Heap class
    static class FibonacciHeap {
        private FibHeapNode min;

        public void insert(FibHeapNode node) {
            if (min == null) {
                min = node;
            } else {
                mergeLists(min, node);
                if (node.distance < min.distance) {
                    min = node;
                }
            }
        }

        public FibHeapNode extractMin() {
            FibHeapNode z = min;
            if (z != null) {
                if (z.child != null) {
                    FibHeapNode x = z.child;
                    do {
                        FibHeapNode next = x.right;
                        mergeLists(min, x);
                        x.parent = null;
                        x = next;
                    } while (x != z.child);
                }
                removeNode(z);
                if (z == z.right) {
                    min = null;
                } else {
                    min = z.right;
                }
            }
            return z;
        }

        private void mergeLists(FibHeapNode a, FibHeapNode b) {
            FibHeapNode aRight = a.right;
            a.right = b.right;
            b.right.left = a;
            b.right = aRight;
            aRight.left = b;
        }

        private void removeNode(FibHeapNode node) {
            node.left.right = node.right;
            node.right.left = node.left;
        }

        public boolean isEmpty() {
            return min == null;
        }
    }

    public static void minHeapDijkstra(List<List<Edge>> graph, int source) {
        int n = graph.size();
        PriorityQueue<MinHeapNode> pq = new PriorityQueue<>();
        double[] dist = new double[n];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        dist[source] = 0;
        pq.offer(new MinHeapNode(source, 0));

        while (!pq.isEmpty()) {
            MinHeapNode current = pq.poll();
            int u = current.vertex;

            for (Edge edge : graph.get(u)) {
                int v = edge.to;
                double weight = edge.weight;

                if (dist[u] + weight < dist[v]) {
                    dist[v] = dist[u] + weight;
                    pq.offer(new MinHeapNode(v, dist[v]));
                }
            }
        }

        //printDistances(dist);
    }

    public static void fibonacciHeapDijkstra(List<List<Edge>> graph, int source) {
        int n = graph.size();
        double[] dist = new double[n];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        dist[source] = 0;

        FibonacciHeap fh = new FibonacciHeap();
        FibHeapNode sourceNode = new FibHeapNode(source, 0);
        fh.insert(sourceNode);

        while (!fh.isEmpty()) {
            FibHeapNode minNode = fh.extractMin();
            int u = minNode.vertex;

            for (Edge edge : graph.get(u)) {
                int v = edge.to;
                double weight = edge.weight;

                if (dist[u] + weight < dist[v]) {
                    dist[v] = dist[u] + weight;
                    fh.insert(new FibHeapNode(v, dist[v]));
                }
            }

            // Consolidation logic for Fibonacci Heap (needed for efficiency)
            consolidate(fh);
        }
    }

    private static void consolidate(FibonacciHeap fh) {
        if (fh.min == null) return;

        Map<Integer, FibHeapNode> degreeMap = new HashMap<>();
        List<FibHeapNode> roots = new ArrayList<>();
        FibHeapNode current = fh.min;
        do {
            roots.add(current);
            current = current.right;
        } while (current != fh.min);

        for (FibHeapNode node : roots) {
            while (degreeMap.containsKey(node.degree)) {
                FibHeapNode other = degreeMap.get(node.degree);

                // Link the two nodes with the same degree
                if (node.distance > other.distance) {
                    FibHeapNode temp = node;
                    node = other;
                    other = temp;
                }

                link(node, other);
                degreeMap.remove(node.degree);
                node.degree++;
            }

            degreeMap.put(node.degree, node);
        }

        fh.min = null;
        for (FibHeapNode node : degreeMap.values()) {
            if (fh.min == null) {
                fh.min = node;
            } else {
                fh.insert(node);
            }
        }
    }

    private static void link(FibHeapNode minNode, FibHeapNode otherNode) {
        // Link smaller node as parent of the larger node
        otherNode.left.right = otherNode.right;
        otherNode.right.left = otherNode.left;

        otherNode.parent = minNode;
        if (minNode.child == null) {
            minNode.child = otherNode;
            otherNode.right = otherNode;
            otherNode.left = otherNode;
        } else {
            mergeLists(minNode.child, otherNode);
        }
        minNode.degree++;
    }

    private static void mergeLists(FibHeapNode a, FibHeapNode b) {
        FibHeapNode aRight = a.right;
        a.right = b.right;
        b.right.left = a;
        b.right = aRight;
        aRight.left = b;
    }


    public static void printDistances(double[] dist) {
        System.out.println("Shortest distances from source:");
        for (int i = 0; i < dist.length; i++) {
            System.out.println("Node " + i + " : " + dist[i]);
        }
    }

    public static void run(int V, int[][] edges, boolean useFibonacci) {

        // int V = 5;
        // int[][] edges = {{0, 1, 10}, {0, 4, 3}, {1, 2, 2}, {2, 3, 9}, {4, 1, 1}, {4, 2, 8}};

        List<List<Edge>> graph = new ArrayList<>();
        for (int i = 0; i < V; i++) graph.add(new ArrayList<>());
        for (int[] e : edges) graph.get(e[0]).add(new Edge(e[1], e[2]));

        if (useFibonacci) {
            fibonacciHeapDijkstra(graph, 0);
        } else {
            minHeapDijkstra(graph, 0);
        }
    }
}
