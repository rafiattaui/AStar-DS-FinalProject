# Fibonacci Heap

A Fibonacci heap is a specific implementation of the heap data structure that makes use of Fibonacci numbers. Fibonacci heaps are used to implement the priority queue element in Dijkstra’s algorithm, giving the algorithm a very efficient running time.
Structure

Fibonacci heaps are a collection of heap-ordered trees. They are designed to provide efficient support for operations like insertion, extract-min, and decrease-key, which are essential for algorithms like Dijkstra’s and Prim’s.

## Each node in a Fibonacci heap contains the following:

    Value: The key stored at the node.

    Parent pointer: A pointer to the parent node (if any).

    Child pointer: A pointer to the first child node (if any).

    Degree: The number of children the node has.

    Mark: A boolean indicating whether the node has lost a child in a previous operation. A node is marked if it has lost a child, and it is unmarked if it has not.

    Left and Right pointers: Pointers to the previous and next nodes in the doubly linked list of the root.

The min pointer of the heap maintains a reference to the root node with the smallest key.

## Key Operations

    Insertion:

        Inserting a node into a Fibonacci heap involves placing the node into the root list, which can be done in constant time O(1)O(1).

    Extract-Min:

        This operation removes the minimum element from the root list. The child nodes of the minimum node are added to the root list, and the heap is consolidated to ensure the structure remains efficient. The consolidation step is O(log⁡n)O(logn), but the overall time complexity for this operation is amortized O(log⁡n)O(logn).

    Decrease-Key:

        Decreasing the key of a node involves cutting the node from its parent and adding it to the root list. This operation is done in constant time O(1)O(1).

    Merging Heaps (Union):

        Two Fibonacci heaps can be merged in constant time O(1)O(1). This is done by linking the two root lists together.

# Why Fibonacci Heap is Faster than a Min-Heap

While both binary min-heaps and Fibonacci heaps are designed to efficiently implement priority queues, Fibonacci heaps offer better amortized performance for certain operations. The key differences lie in the time complexity of frequently used operations, especially decrease-key and insert.

1.  Decrease-Key Operation (O(1) Amortized vs O(log n))

    Binary Min-Heap:

        Decrease-key in a binary min-heap takes O(log⁡n)O(logn) because, after modifying the key, the heap must percolate up (up-heapify) to restore the heap property. This requires traversing up the tree, potentially all the way to the root.

    Fibonacci Heap:

        In a Fibonacci heap, decrease-key can be performed in constant time O(1)O(1) (amortized). This is because, when a key is decreased, the node is cut from its parent and added to the root list. The node becomes marked to indicate it has lost a child. If a marked node loses another child, it is cascaded upwards. Since no reheapification (like in binary heaps) is needed, the operation is more efficient.

2.  Insert Operation (O(1) vs O(log n))

    Binary Min-Heap:

        Insertion in a binary heap involves placing the new element at the end of the heap and then bubbling it up to restore the heap property, which takes O(log⁡n)O(logn) time.

    Fibonacci Heap:

        In contrast, insertion in a Fibonacci heap is done in constant time O(1)O(1) because the node is simply added to the root list, and no reorganization of the heap is required. The only operation is updating the min pointer if the new node has a smaller key.

3.  Extract-Min Operation (O(log n) vs O(log n))

    Binary Min-Heap:

        The extract-min operation in both heaps is O(log⁡n)O(logn). This operation involves removing the minimum element (root), moving the last element to the root position, and then bubbling down to restore the heap property.

    Fibonacci Heap:

        The extract-min operation is similarly O(log⁡n)O(logn) in both heaps, but Fibonacci heaps perform better in practice due to their lazy consolidation. The consolidation step (where trees of the same degree are merged) is delayed until after the extract-min operation, which helps reduce the overall cost when multiple operations are performed in sequence.

4.  Merging Heaps (O(log n) vs O(1))

    Binary Min-Heap:

        Merging two binary heaps requires copying all elements from both heaps and rebuilding the heap, which takes O(n)O(n) time.

    Fibonacci Heap:

        Merging two Fibonacci heaps is done in constant time O(1)O(1). This is possible because the heap structure is based on a collection of trees, and merging two root lists is a simple operation.

5.  Practical Impact on Dijkstra's Algorithm

    Fibonacci Heaps improve Dijkstra’s algorithm significantly. In Dijkstra’s algorithm, the most time-consuming operations are decrease-key and extract-min.

        With a binary heap, decrease-key takes O(log⁡n)O(logn) and extract-min also takes O(log⁡n)O(logn). For a graph with VV vertices and EE edges, the total time complexity is O((V+E)log⁡V)O((V+E)logV).

        With a Fibonacci heap, decrease-key takes O(1)O(1), and extract-min still takes O(log⁡n)O(logn), reducing the overall time complexity to O(E+Vlog⁡V)O(E+VlogV), which is faster when the graph is dense.

Conclusion:

A Fibonacci heap is faster than a binary min-heap due to its amortized constant-time decrease-key and insertion operations. This makes it particularly efficient for algorithms like Dijkstra’s and Prim’s, where the decrease-key operation is performed frequently. While the extract-min operation is similar in both heaps, the Fibonacci heap’s lazy consolidation and efficient merging of heaps give it an edge in situations with many operations. However, for smaller or simpler graphs, the constant overhead of Fibonacci heaps may make binary heaps more practical in practice.

## Source:

- https://brilliant.org/wiki/fibonacci-heap/#citation-6
- https://www.youtube.com/watch?v=0vsX3ZQFREM
