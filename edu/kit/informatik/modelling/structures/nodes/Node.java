package edu.kit.informatik.modelling.structures.nodes;

import edu.kit.informatik.modelling.IP;
import edu.kit.informatik.resources.ErrorMessage;

/**
 * Represents a node, which can be used in
 * {@link edu.kit.informatik.modelling.structures.Tree}.
 * 
 * @author uogok
 * @version 3
 */
public abstract class Node implements Comparable<Node> {

    private final IP label;

    /**
     * Constructor for the node, an IPv4 address is its identifier, which is all it
     * needs.
     * 
     * @param label is the node's unique identifier.
     */
    Node(IP label) {
        if (label == null) {
            throw new IllegalArgumentException(ErrorMessage.IP_NULL.toString());
        }
        this.label = label.deepCopy();
    }

    /**
     * Gets the number of edges connected to this node.
     * 
     * @return number of edges connected to this node.
     */
    public abstract int getNumberOfEdges();

    /**
     * Connects @param connect node to this instance.
     * 
     * @param connect is to be connected to this instance.
     */
    public abstract void addConnection(Node connect);

    /**
     * Removes the connection
     * 
     * @param disconnect
     * @return true, if disconnection was successful, otherwise false.
     */
    public abstract boolean removeConnection(Node disconnect);

    /**
     * Gets a deep-copy of this Node.
     * 
     * @return a deep-copy of this Node.
     */
    public abstract Node deepCopy();

    /**
     * Gets the IPv4-address of this node.
     * 
     * @return IPv4-address of this node.
     */
    public IP getLabel() {
        return this.label.deepCopy();
    }

    @Override
    public int compareTo(Node nodeToCompare) {
        if (nodeToCompare == null) {
            throw new IllegalArgumentException(ErrorMessage.NODE_NULL.toString());
        }
        return this.label.compareTo(nodeToCompare.label);
    }

    @Override
    public boolean equals(Object other) {

        if (this == other) {
            return true;
        }

        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        return compareTo((Node) other) == 0;
    }
}
