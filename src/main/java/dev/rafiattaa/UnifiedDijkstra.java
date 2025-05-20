package dev.rafiattaa;

import java.util.*;

public class UnifiedDijkstra {

    // Edge class representing a directed edge with weight
    static class Edge {
        int to;
        double weight;
        public Edge(int to, double weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    // MinHeap node for PriorityQueue
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

    // FibHeap node
    static class FibHeapNode {
        int vertex;
        double distance;
        FibHeapNode parent, child, left, right;
        int degree;
        boolean mark;

        FibHeapNode(int vertex, double distance) {
            this.vertex = vertex;
            this.distance = distance;
            this.left = this;
            this.right = this;
        }
    }

    // Fibonacci Heap Implementation
    static class FibonacciHeap {
        FibHeapNode min;
        int n = 0;

        void insert(FibHeapNode node) {
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

        FibHeapNode extractMin() {
            FibHeapNode z = min;
            if (z != null) {
                if (z.child != null) {
                    FibHeapNode child = z.child;
                    List<FibHeapNode> children = new ArrayList<>();
                    FibHeapNode current = child;
                    do {
                        children.add(current);
                        current = current.right;
                    } while (current != child);

                    for (FibHeapNode x : children) {
                        x.parent = null;
                        mergeLists(min, x);
                    }
                }
                removeNode(z);
                n--;
                if (z == z.right) {
                    min = null;
                } else {
                    min = z.right;
                    consolidate();
                }
            }
            return z;
        }

        boolean isEmpty() {
            return min == null;
        }

        // Decrease key operation
        void decreaseKey(FibHeapNode node, double newDist) {
            if (newDist >= node.distance) return;
            node.distance = newDist;
            FibHeapNode parent = node.parent;
            if (parent != null && node.distance < parent.distance) {
                cut(node, parent);
                cascadingCut(parent);
            }
            if (node.distance < min.distance) {
                min = node;
            }
        }

        private void cut(FibHeapNode node, FibHeapNode parent) {
            // Remove node from child list of parent
            if (parent.child == node) {
                if (node.right != node) {
                    parent.child = node.right;
                } else {
                    parent.child = null;
                }
            }
            removeNode(node);
            parent.degree--;

            // Add node to root list
            node.parent = null;
            node.mark = false;
            mergeLists(min, node);
        }

        private void cascadingCut(FibHeapNode node) {
            FibHeapNode parent = node.parent;
            if (parent != null) {
                if (!node.mark) {
                    node.mark = true;
                } else {
                    cut(node, parent);
                    cascadingCut(parent);
                }
            }
        }

        private void consolidate() {
            if (min == null) return;

            int maxDegree = (int) Math.floor(Math.log(n) / Math.log(2)) + 1;
            FibHeapNode[] degreeTable = new FibHeapNode[maxDegree];

            List<FibHeapNode> rootNodes = new ArrayList<>();
            FibHeapNode current = min;
            do {
                rootNodes.add(current);
                current = current.right;
            } while (current != min);

            for (FibHeapNode node : rootNodes) {
                int d = node.degree;
                while (degreeTable[d] != null) {
                    FibHeapNode other = degreeTable[d];
                    if (node.distance > other.distance) {
                        FibHeapNode temp = node;
                        node = other;
                        other = temp;
                    }
                    link(node, other);
                    degreeTable[d] = null;
                    d++;
                }
                degreeTable[d] = node;
            }

            min = null;
            for (FibHeapNode node : degreeTable) {
                if (node != null) {
                    node.left = node;
                    node.right = node;
                    if (min == null) {
                        min = node;
                    } else {
                        mergeLists(min, node);
                        if (node.distance < min.distance) {
                            min = node;
                        }
                    }
                }
            }
        }

        private void link(FibHeapNode parent, FibHeapNode child) {
            // Remove child from root list
            removeNode(child);
            child.parent = parent;

            if (parent.child == null) {
                parent.child = child;
                child.left = child;
                child.right = child;
            } else {
                mergeLists(parent.child, child);
            }
            parent.degree++;
            child.mark = false;
        }

        private static void mergeLists(FibHeapNode a, FibHeapNode b) {
            if (a == null || b == null) return;
            FibHeapNode aRight = a.right;
            FibHeapNode bLeft = b.left;

            a.right = b;
            b.left = a;

            aRight.left = bLeft;
            bLeft.right = aRight;
        }

        private void removeNode(FibHeapNode node) {
            node.left.right = node.right;
            node.right.left = node.left;
            node.left = node;
            node.right = node;
        }
    }

    // Min-Heap Dijkstra using Java PriorityQueue
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
            if (current.dist > dist[u]) continue;

            for (Edge edge : graph.get(u)) {
                int v = edge.to;
                double weight = edge.weight;
                if (dist[u] + weight < dist[v]) {
                    dist[v] = dist[u] + weight;
                    pq.offer(new MinHeapNode(v, dist[v]));
                }
            }
        }
    }

    // Fibonacci Heap Dijkstra with decrease-key and visited check
    public static void fibonacciHeapDijkstra(List<List<Edge>> graph, int source) {
        int n = graph.size();
        double[] dist = new double[n];
        boolean[] processed = new boolean[n];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        dist[source] = 0;

        FibonacciHeap fh = new FibonacciHeap();
        FibHeapNode[] nodes = new FibHeapNode[n];

        FibHeapNode sourceNode = new FibHeapNode(source, 0);
        nodes[source] = sourceNode;
        fh.insert(sourceNode);

        while (!fh.isEmpty()) {
            FibHeapNode minNode = fh.extractMin();
            int u = minNode.vertex;

            if (processed[u]) continue;
            processed[u] = true;

            for (Edge edge : graph.get(u)) {
                int v = edge.to;
                double weight = edge.weight;

                if (dist[u] + weight < dist[v]) {
                    dist[v] = dist[u] + weight;
                    if (nodes[v] == null) {
                        nodes[v] = new FibHeapNode(v, dist[v]);
                        fh.insert(nodes[v]);
                    } else {
                        fh.decreaseKey(nodes[v], dist[v]);
                    }
                }
            }
        }
    }

    // Unordered list Dijkstra O(V^2)
    public static void unorderedListDijkstra(List<List<Edge>> graph, int source) {
        int n = graph.size();
        double[] dist = new double[n];
        boolean[] visited = new boolean[n];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        dist[source] = 0;

        for (int i = 0; i < n; i++) {
            int u = -1;
            double minDist = Double.POSITIVE_INFINITY;

            for (int j = 0; j < n; j++) {
                if (!visited[j] && dist[j] < minDist) {
                    minDist = dist[j];
                    u = j;
                }
            }

            if (u == -1) break;
            visited[u] = true;

            for (Edge edge : graph.get(u)) {
                int v = edge.to;
                double weight = edge.weight;
                if (dist[u] + weight < dist[v]) {
                    dist[v] = dist[u] + weight;
                }
            }
        }
    }

    // Run selected mode: "fibonacci", "minheap", "unordered"
    public static void run(int V, int[][] edges, String mode) {
        List<List<Edge>> graph = new ArrayList<>();
        for (int i = 0; i < V; i++) graph.add(new ArrayList<>());
        for (int[] e : edges) {
            graph.get(e[0]).add(new Edge(e[1], e[2]));
        }

        switch (mode.toLowerCase()) {
            case "fibonacci":
                fibonacciHeapDijkstra(graph, 0);
                break;
            case "minheap":
                minHeapDijkstra(graph, 0);
                break;
            case "unordered":
                unorderedListDijkstra(graph, 0);
                break;
            default:
                throw new IllegalArgumentException("Unknown mode: " + mode);
        }
    }
}
