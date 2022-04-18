package edu.kit.informatik.modelling.structures.nodes;

import java.util.TreeSet;

import edu.kit.informatik.exceptions.CycleException;
import edu.kit.informatik.modelling.IP;
import edu.kit.informatik.resources.ErrorMessage;

/**
 * This class represents a specialisation of a node, which is a tree-node mostly
 * used in {@link edu.kit.informatik.modelling.structures.Tree}.
 * 
 * @author uogok
 * @version 3.2
 */
public class TreeNode extends Node {

    private TreeNode root;
    private final TreeSet<TreeNode> adjacentBranches;

    /**
     * Constructor for the tree-node, an IPv4 address is its identifier, which is
     * all it needs. {@link Node#Node(IP)}.
     * 
     * @param label is the node's unique identifier.
     */
    public TreeNode(IP label) {
        super(label);
        adjacentBranches = new TreeSet<TreeNode>();
        this.root = null;
    }

    /**
     * The second constructor but it directly sets @param root as this instance's
     * root and adds this instance into @param root's {@link #adjacentBranches}.
     * 
     * @param root  is to be set as this tree-node's {@link #root}.
     * @param label is the node's unique identifier.
     */
    public TreeNode(TreeNode root, IP label) {
        super(label);
        adjacentBranches = new TreeSet<TreeNode>();
        setRoot(root);
        root.addConnection(this);
    }

    /**
     * Returns if this tree-node is a root.
     * 
     * @return true, if this tree-node is a root.
     */
    public boolean isRoot() {
        return root == null;
    }

    /**
     * Checks if this tree-node has a {@link #root}.
     * 
     * @return true, if this tree-node has a root.
     */
    private boolean hasRoot() {
        return root != null;
    }

    /**
     * Checks if this tree-node is a leaf, it is if it's {@link #adjacentBranches}
     * is empty(the {@link #adjacentBranches} doesn't contain the root) i.e. if the
     * tree-nodes positive degree is 0.
     * 
     * @return true, if if this tree-node is a leaf.
     */
    public boolean isLeaf() {
        return adjacentBranches.isEmpty();
    }

    /**
     * Sets a tree-node @param root as this tree-node's root, executes only if this
     * instance doesn't have a root already.
     * 
     * @param root is to be set as new root.
     * @return true, if adding root was successful.
     */
    public boolean setRoot(TreeNode root) {
        if (root == null) {
            throw new IllegalArgumentException(ErrorMessage.NODE_NULL.toString());
        }
        if (hasRoot()) {
            throw new CycleException(ErrorMessage.CYCLE_EMERGED_ADDING_ROOT.toString());
        }
        this.root = root;
        return true;
    }

    /**
     * Gets the {@link #adjacentBranches} of this instance.
     * 
     * @return the {@link #adjacentBranches} of this instance.
     */
    public TreeSet<TreeNode> getAdjacentBranches() {
        return new TreeSet<>(adjacentBranches);
    }

    /**
     * Gets the {@link #root} of this instance.
     * 
     * @return the {@link #root} of this instance.
     */
    public TreeNode getRoot() {
        return root;
    }

    @Override
    public int getNumberOfEdges() {

        int size = adjacentBranches.size();
        return (root == null) ? size : (size + 1);
    }

    @Override
    public void addConnection(Node connect) {
        if (connect == null) {
            throw new IllegalArgumentException(ErrorMessage.NODE_NULL.toString());
        }
        boolean contains = this.adjacentBranches.contains((TreeNode) connect);
        if (contains) {
            throw new CycleException(ErrorMessage.CYCLE_EMERGED_DUPLICATE_IP.toString());
        }
        this.adjacentBranches.add((TreeNode) connect);
    }

    @Override
    public boolean removeConnection(Node disconnect) {
        if (disconnect == null) {
            return false;
        }
        return adjacentBranches.remove((TreeNode) disconnect);
    }

    @Override
    public Node deepCopy() {
        return new TreeNode(this.getLabel());
    }

}
