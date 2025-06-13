package dev.rafiattaa.astar;

import java.util.*;

public class MinHeapAStar {
    private final int[][] grid;
    private final int rows, cols;
    private static final int[] dx = {-1, 1, 0, 0, -1, -1, 1, 1}; // 8-directional movement
    private static final int[] dy = {0, 0, -1, 1, -1, 1, -1, 1};
    private static final double DIAGONAL_COST = Math.sqrt(2);
    private static final double STRAIGHT_COST = 1.0;

    public MinHeapAStar(int[][] grid) {
        this.grid = grid;
        this.rows = grid.length;
        this.cols = grid[0].length;
    }

    public List<Point> findPath(int startX, int startY, int goalX, int goalY) {
        // Validate start and goal positions
        if (!isValid(startX, startY) || !isValid(goalX, goalY) ||
                grid[startX][startY] == 1 || grid[goalX][goalY] == 1) {
            return new ArrayList<>(); // Return empty path if invalid
        }

        PriorityQueue<Node> openSet = new PriorityQueue<>((a, b) ->
                Double.compare(a.getFCost(), b.getFCost())); // List of all existing nodes discovered, but not checked
        Set<String> closedSet = new HashSet<>(); // already checked nodes
        Map<String, Node> allNodes = new HashMap<>();
        Map<String, Point> cameFrom = new HashMap<>();

        // Initialize start node
        double StartHCost = calculateHeuristic(startX, startY, goalX, goalY);
        Node startNode = new Node(startX, startY, 0, StartHCost);

        openSet.add(startNode);
        allNodes.put(getKey(startX, startY), startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            String currentKey = getKey(current.x, current.y);

            // Check if we reached the goal
            if (current.x == goalX && current.y == goalY) {
                return reconstructPath(cameFrom, current.x, current.y, startX, startY);
            }

            closedSet.add(currentKey);

            // Explore neighbors
            for (int i = 0; i < dx.length; i++) {
                int nx = current.x + dx[i];
                int ny = current.y + dy[i];
                String neighborKey = getKey(nx, ny);

                // Skip if out of bounds, obstacle, or already processed
                if (!isValid(nx, ny) || grid[nx][ny] == 1 || closedSet.contains(neighborKey)) {
                    continue;
                }

                // Calculate movement cost (diagonal vs straight)
                double moveCost = (Math.abs(dx[i]) + Math.abs(dy[i]) == 2) ? DIAGONAL_COST : STRAIGHT_COST;
                double tentativeGCost = current.gCost + moveCost;

                Node neighbor = allNodes.get(neighborKey);
                boolean isNewNode = (neighbor == null);

                if (isNewNode || tentativeGCost < neighbor.gCost) {
                    double NeighborHCost = calculateHeuristic(nx, ny, goalX, goalY);

                    if (isNewNode) {
                        neighbor = new Node(nx, ny, tentativeGCost, NeighborHCost);
                        allNodes.put(neighborKey, neighbor);
                        openSet.add(neighbor);
                    } else {
                        // Update existing node
                        neighbor.gCost = tentativeGCost;
                        // Remove and re-add to update priority queue ordering
                        openSet.remove(neighbor);
                        openSet.add(neighbor);
                    }

                    cameFrom.put(neighborKey, new Point(current.x, current.y));
                }
            }
        }

        return new ArrayList<>(); // No path found
    }

    private boolean isValid(int x, int y) {
        return x >= 0 && x < rows && y >= 0 && y < cols;
    }

    private double calculateHeuristic(int x1, int y1, int x2, int y2) {
        // Euclidean distance
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    private String getKey(int x, int y) {
        return x + "," + y;
    }

    private List<Point> reconstructPath(Map<String, Point> cameFrom, int goalX, int goalY, int startX, int startY) {
        List<Point> path = new ArrayList<>();
        int currentX = goalX, currentY = goalY;

        while (currentX != startX || currentY != startY) {
            path.add(new Point(currentX, currentY));
            Point parent = cameFrom.get(getKey(currentX, currentY));
            currentX = parent.x;
            currentY = parent.y;
        }

        path.add(new Point(startX, startY)); // Add start point
        Collections.reverse(path); // Reverse to get path from start to goal
        return path;
    }


    public static void printPath(int[][] grid, List<Point> finishedPath, Point start, Point goal, boolean drawGraph) {
        if (finishedPath.isEmpty()) {
            System.out.println("No path found!");
        } else {
            System.out.println("Path found:");
            if (drawGraph) {
                for (Point p : finishedPath) {
                    System.out.println(p);
                }

                // Visualize the path
                System.out.println("\nGrid with path (P = path, X = obstacle, . = free):");
                char[][] visual = new char[grid.length][grid[0].length];

                for (int i = 0; i < grid.length; i++) {
                    for (int j = 0; j < grid[0].length; j++) {
                        visual[i][j] = (grid[i][j] == 1) ? 'X' : '.';
                    }
                }

                for (Point p : finishedPath) {
                    visual[p.x][p.y] = 'P';
                }
                visual[start.x][start.y] = 'S';
                visual[goal.x][goal.y] = 'G';

                for (char[] row : visual) {
                    System.out.println(new String(row));
                }
            }
        }
    }

    // Example usage and testing
    public static void main(String[] args) {
        // Create a sample grid (0 = free, 1 = obstacle)
        int[][] grid = {
                {0, 0, 0, 1, 0},
                {0, 1, 0, 1, 0},
                {0, 1, 0, 0, 0},
                {0, 0, 0, 1, 0},
                {0, 0, 0, 0, 0}
        };

        MinHeapAStar aStar = new MinHeapAStar(grid);
        List<Point> path = aStar.findPath(0, 0, 4, 4);
        printPath(grid, path, new Point(0,0), new Point(4,4), true);
    }
}