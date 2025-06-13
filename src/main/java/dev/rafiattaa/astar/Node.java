package dev.rafiattaa.astar;

import java.util.Objects;

public class Node implements Comparable<Node>{
    int x, y; // position
    double gCost; // cost from start
    double hCost; // heuristic cost to goal

    public Node(int x, int y, double gCost, double hCost) {
        this.x = x;
        this.y = y;
        this.gCost = gCost;
        this.hCost = hCost;
    }

    public double getFCost(){
        return gCost + hCost;
    }

    @Override
    public int compareTo(Node other) {
        return Double.compare(this.gCost, other.gCost);
    }

    @Override
    public int hashCode(){
        return Objects.hash(x, y);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Node otherNode = (Node) other;
        return this.x == otherNode.x && this.y == otherNode.y;
    }
}
