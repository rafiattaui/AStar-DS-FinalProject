package dev.rafiattaa;

import java.util.*;

public class UnifiedAStar {

    // Edge class representing a directed edge with weight
    static class Edge {
        int to;
        double weight;
        public Edge(int to, double weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    // Heuristic interface for A* algorithm
    interface Heuristic {
        double estimate(int from, int to);
    }

    // MinHeap node for PriorityQueue (now includes f-score for A*)
    static class MinHeapNode implements Comparable<MinHeapNode> {
        int vertex;
        double gScore;  // Actual cost from start
        double fScore;  // gScore + heuristic

        MinHeapNode(int vertex, double gScore, double fScore) {
            this.vertex = vertex;
            this.gScore = gScore;
            this.fScore = fScore;
        }

        @Override
        public int compareTo(MinHeapNode other) {
            return Double.compare(this.fScore, other.fScore);
        }
    }

    // FibHeap node (modified for A*)
    static class FibHeapNode {
        int vertex;
        double gScore;
        double fScore;
        FibHeapNode parent, child, left, right;
        int degree;
        boolean mark;

        FibHeapNode(int vertex, double gScore, double fScore) {
            this.vertex = vertex;
            this.gScore = gScore;
            this.fScore = fScore;
            this.left = this;
            this.right = this;
        }
    }

    // Fibonacci Heap Implementation (modified for A*)
    static class FibonacciHeap {
        FibHeapNode min;
        int n = 0;

        void insert(FibHeapNode node) {
            if (min == null) {
                min = node;
            } else {
                mergeLists(min, node);
                if (node.fScore < min.fScore) {
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

        void decreaseKey(FibHeapNode node, double newGScore, double newFScore) {
            if (newFScore >= node.fScore) return;
            node.gScore = newGScore;
            node.fScore = newFScore;
            FibHeapNode parent = node.parent;
            if (parent != null && node.fScore < parent.fScore) {
                cut(node, parent);
                cascadingCut(parent);
            }
            if (node.fScore < min.fScore) {
                min = node;
            }
        }

        private void cut(FibHeapNode node, FibHeapNode parent) {
            if (parent.child == node) {
                if (node.right != node) {
                    parent.child = node.right;
                } else {
                    parent.child = null;
                }
            }
            removeNode(node);
            parent.degree--;

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
                    if (node.fScore > other.fScore) {
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
                        if (node.fScore < min.fScore) {
                            min = node;
                        }
                    }
                }
            }
        }

        private void link(FibHeapNode parent, FibHeapNode child) {
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

    // Min-Heap A* using Java PriorityQueue
    public static List<Integer> minHeapAStar(List<List<Edge>> graph, int source, int target, Heuristic heuristic) {
        int n = graph.size();
        PriorityQueue<MinHeapNode> pq = new PriorityQueue<>();
        double[] gScore = new double[n];
        int[] parent = new int[n];
        boolean[] visited = new boolean[n];

        Arrays.fill(gScore, Double.POSITIVE_INFINITY);
        Arrays.fill(parent, -1);
        gScore[source] = 0;

        double fScore = heuristic.estimate(source, target);
        pq.offer(new MinHeapNode(source, 0, fScore));

        while (!pq.isEmpty()) {
            MinHeapNode current = pq.poll();
            int u = current.vertex;

            if (visited[u]) continue;
            visited[u] = true;

            if (u == target) {
                return reconstructPath(parent, source, target);
            }

            for (Edge edge : graph.get(u)) {
                int v = edge.to;
                double tentativeGScore = gScore[u] + edge.weight;

                if (tentativeGScore < gScore[v]) {
                    gScore[v] = tentativeGScore;
                    parent[v] = u;
                    double fScoreV = gScore[v] + heuristic.estimate(v, target);
                    pq.offer(new MinHeapNode(v, gScore[v], fScoreV));
                }
            }
        }

        return new ArrayList<>(); // No path found
    }

    // Fibonacci Heap A*
    public static List<Integer> fibonacciHeapAStar(List<List<Edge>> graph, int source, int target, Heuristic heuristic) {
        int n = graph.size();
        double[] gScore = new double[n];
        int[] parent = new int[n];
        boolean[] processed = new boolean[n];

        Arrays.fill(gScore, Double.POSITIVE_INFINITY);
        Arrays.fill(parent, -1);
        gScore[source] = 0;

        FibonacciHeap fh = new FibonacciHeap();
        FibHeapNode[] nodes = new FibHeapNode[n];

        double fScore = heuristic.estimate(source, target);
        FibHeapNode sourceNode = new FibHeapNode(source, 0, fScore);
        nodes[source] = sourceNode;
        fh.insert(sourceNode);

        while (!fh.isEmpty()) {
            FibHeapNode minNode = fh.extractMin();
            int u = minNode.vertex;

            if (processed[u]) continue;
            processed[u] = true;

            if (u == target) {
                return reconstructPath(parent, source, target);
            }

            for (Edge edge : graph.get(u)) {
                int v = edge.to;
                double tentativeGScore = gScore[u] + edge.weight;

                if (tentativeGScore < gScore[v]) {
                    gScore[v] = tentativeGScore;
                    parent[v] = u;
                    double fScoreV = gScore[v] + heuristic.estimate(v, target);

                    if (nodes[v] == null) {
                        nodes[v] = new FibHeapNode(v, gScore[v], fScoreV);
                        fh.insert(nodes[v]);
                    } else {
                        fh.decreaseKey(nodes[v], gScore[v], fScoreV);
                    }
                }
            }
        }

        return new ArrayList<>(); // No path found
    }

    // Unordered list A* O(V^2)
    public static List<Integer> unorderedListAStar(List<List<Edge>> graph, int source, int target, Heuristic heuristic) {
        int n = graph.size();
        double[] gScore = new double[n];
        double[] fScore = new double[n];
        int[] parent = new int[n];
        boolean[] visited = new boolean[n];

        Arrays.fill(gScore, Double.POSITIVE_INFINITY);
        Arrays.fill(fScore, Double.POSITIVE_INFINITY);
        Arrays.fill(parent, -1);

        gScore[source] = 0;
        fScore[source] = heuristic.estimate(source, target);

        while (true) {
            int u = -1;
            double minFScore = Double.POSITIVE_INFINITY;

            for (int j = 0; j < n; j++) {
                if (!visited[j] && fScore[j] < minFScore) {
                    minFScore = fScore[j];
                    u = j;
                }
            }

            if (u == -1 || u == target) break;
            visited[u] = true;

            for (Edge edge : graph.get(u)) {
                int v = edge.to;
                double tentativeGScore = gScore[u] + edge.weight;

                if (tentativeGScore < gScore[v]) {
                    gScore[v] = tentativeGScore;
                    fScore[v] = gScore[v] + heuristic.estimate(v, target);
                    parent[v] = u;
                }
            }
        }

        return reconstructPath(parent, source, target);
    }

    // Helper method to reconstruct the path
    private static List<Integer> reconstructPath(int[] parent, int source, int target) {
        List<Integer> path = new ArrayList<>();
        int current = target;

        while (current != -1) {
            path.add(Integer.valueOf(current));
            current = parent[current];
        }

        if (path.get(path.size() - 1) != source) {
            return new ArrayList<>(); // No path found
        }

        Collections.reverse(path);
        return path;
    }

    // Run selected mode: "fibonacci", "minheap", "unordered"
    public static List<Integer> run(int V, int[][] edges, String mode, int source, int target, Heuristic heuristic) {
        List<List<Edge>> graph = new ArrayList<>();
        for (int i = 0; i < V; i++) graph.add(new ArrayList<>());
        for (int[] e : edges) {
            graph.get(e[0]).add(new Edge(e[1], e[2]));
        }

        switch (mode.toLowerCase()) {
            case "fibonacci":
                return fibonacciHeapAStar(graph, source, target, heuristic);
            case "minheap":
                return minHeapAStar(graph, source, target, heuristic);
            case "unordered":
                return unorderedListAStar(graph, source, target, heuristic);
            default:
                throw new IllegalArgumentException("Unknown mode: " + mode);
        }
    }
}

// Edge Generator Class
class EdgeGenerator {
    private Random random;

    public EdgeGenerator() {
        this.random = new Random();
    }

    public EdgeGenerator(long seed) {
        this.random = new Random(seed);
    }

    // Generate a complete graph with random weights
    public int[][] generateCompleteGraph(int vertices, double minWeight, double maxWeight) {
        List<int[]> edges = new ArrayList<>();

        for (int i = 0; i < vertices; i++) {
            for (int j = 0; j < vertices; j++) {
                if (i != j) {
                    double weight = minWeight + random.nextDouble() * (maxWeight - minWeight);
                    edges.add(new int[]{i, j, (int) Math.round(weight)});
                }
            }
        }

        return edges.toArray(new int[0][]);
    }

    // Generate a sparse random graph
    public int[][] generateSparseGraph(int vertices, int edgeCount, double minWeight, double maxWeight) {
        Set<String> edgeSet = new HashSet<>();
        List<int[]> edges = new ArrayList<>();

        while (edges.size() < edgeCount) {
            int from = random.nextInt(vertices);
            int to = random.nextInt(vertices);

            if (from != to) {
                String edgeKey = from + "->" + to;
                if (!edgeSet.contains(edgeKey)) {
                    edgeSet.add(edgeKey);
                    double weight = minWeight + random.nextDouble() * (maxWeight - minWeight);
                    edges.add(new int[]{from, to, (int) Math.round(weight)});
                }
            }
        }

        return edges.toArray(new int[0][]);
    }

    // Generate a grid graph (useful for pathfinding)
    public int[][] generateGridGraph(int width, int height, double minWeight, double maxWeight) {
        List<int[]> edges = new ArrayList<>();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int nodeId = i * width + j;

                // Right neighbor
                if (j < width - 1) {
                    int rightId = i * width + (j + 1);
                    double weight = minWeight + random.nextDouble() * (maxWeight - minWeight);
                    edges.add(new int[]{nodeId, rightId, (int) Math.round(weight)});
                    edges.add(new int[]{rightId, nodeId, (int) Math.round(weight)}); // Bidirectional
                }

                // Bottom neighbor
                if (i < height - 1) {
                    int bottomId = (i + 1) * width + j;
                    double weight = minWeight + random.nextDouble() * (maxWeight - minWeight);
                    edges.add(new int[]{nodeId, bottomId, (int) Math.round(weight)});
                    edges.add(new int[]{bottomId, nodeId, (int) Math.round(weight)}); // Bidirectional
                }
            }
        }

        return edges.toArray(new int[0][]);
    }

    // Generate Manhattan distance heuristic for grid graphs
    public static UnifiedAStar.Heuristic createManhattanHeuristic(int width) {
        return (from, to) -> {
            int fromX = from % width;
            int fromY = from / width;
            int toX = to % width;
            int toY = to / width;
            return Math.abs(fromX - toX) + Math.abs(fromY - toY);
        };
    }

    // Generate Euclidean distance heuristic for grid graphs
    public static UnifiedAStar.Heuristic createEuclideanHeuristic(int width) {
        return (from, to) -> {
            int fromX = from % width;
            int fromY = from / width;
            int toX = to % width;
            int toY = to / width;
            return Math.sqrt(Math.pow(fromX - toX, 2) + Math.pow(fromY - toY, 2));
        };
    }

    // Generate zero heuristic (turns A* into Dijkstra)
    public static UnifiedAStar.Heuristic createZeroHeuristic() {
        return (from, to) -> 0.0;
    }
}