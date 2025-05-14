package dev.rafiattaa;

import java.util.*;

class Node {
    int vertex;
    double distance;

    public Node(int vertex, double distance) {
        this.vertex = vertex;
        this.distance = distance;
    }
}

class FibonacciHeapNode {
    int vertex;
    double distance;
    FibonacciHeapNode parent, child, left, right;
    int degree;
    boolean mark;

    public FibonacciHeapNode(int vertex, double distance) {
        this.vertex = vertex;
        this.distance = distance;
        this.left = this;
        this.right = this;
    }
}

class FibonacciHeap {
    private FibonacciHeapNode min;
    private int n;

    public void insert(FibonacciHeapNode node) {
        if (min == null) {
            min = node;
        } else {
            mergeLists(min, node);
            if (node.distance < min.distance) {
                min = node;
            }
        }
        n++;
    }

    public FibonacciHeapNode extractMin() {
        FibonacciHeapNode z = min;
        if (z != null) {
            if (z.child != null) {
                FibonacciHeapNode x = z.child;
                do {
                    FibonacciHeapNode next = x.right;
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
                consolidate();
            }
            n--;
        }
        return z;
    }

    private void mergeLists(FibonacciHeapNode a, FibonacciHeapNode b) {
        FibonacciHeapNode aRight = a.right;
        a.right = b.right;
        b.right.left = a;
        b.right = aRight;
        aRight.left = b;
    }

    private void removeNode(FibonacciHeapNode node) {
        node.left.right = node.right;
        node.right.left = node.left;
    }

    private void consolidate() {
        // Consolidate trees of the same degree (not shown for brevity)
    }

    public boolean isEmpty() {
        return min == null;
    }
}

public class DijkstraFibonacciHeap {
    private int vertices;
    private List<List<Node>> adjList;

    public DijkstraFibonacciHeap(int vertices) {
        this.vertices = vertices;
        adjList = new ArrayList<>(vertices);
        for (int i = 0; i < vertices; i++) {
            adjList.add(new ArrayList<>());
        }
    }

    public void addEdge(int u, int v, double weight) {
        adjList.get(u).add(new Node(v, weight));
    }

    public void dijkstra(int src) {
        double[] dist = new double[vertices];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        dist[src] = 0;

        FibonacciHeap pq = new FibonacciHeap();
        pq.insert(new FibonacciHeapNode(src, 0));

        while (!pq.isEmpty()) {
            FibonacciHeapNode minNode = pq.extractMin();
            int u = minNode.vertex;

            for (Node neighbor : adjList.get(u)) {
                int v = neighbor.vertex;
                double weight = neighbor.distance;

                if (dist[u] + weight < dist[v]) {
                    dist[v] = dist[u] + weight;
                    pq.insert(new FibonacciHeapNode(v, dist[v]));
                }
            }
        }

        System.out.println("Vertex Distance from Source");
        for (int i = 0; i < vertices; i++) {
            System.out.println(i + " \t " + dist[i]);
        }
    }

    public static void main(String[] args) {
        DijkstraFibonacciHeap graph = new DijkstraFibonacciHeap(5);
        graph.addEdge(0, 1, 3);
        graph.addEdge(0, 2, 1);
        graph.addEdge(1, 2, 7);
        graph.addEdge(1, 3, 5);
        graph.addEdge(2, 3, 2);
        graph.addEdge(3, 4, 7);

        graph.dijkstra(0);
    }
}
