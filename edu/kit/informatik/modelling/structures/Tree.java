package edu.kit.informatik.modelling.structures;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import edu.kit.informatik.exceptions.CycleException;
import edu.kit.informatik.modelling.IP;
import edu.kit.informatik.modelling.structures.nodes.TreeNode;
import edu.kit.informatik.resources.ErrorMessage;

/**
 * This class represents an undirected tree which is a child class of
 * {@link UndirectedGraph}. This class mostly transforms acyclic undirected
 * graphs into trees and executes operations.
 * 
 * @author uogok
 * @version 13
 */
public class Tree extends UndirectedGraph {

    private static final String OPEN_BRACKET = "(";
    private static final String CLOSE_BRACKET = ")";
    private static final String SPACE = " ";
    private static final String DEFAULT_RETURN_STRING = "";
    private static final int DEFAULT_RETURN_INT = 0;
    private static final int MINIMUM_NUMBER_OF_NODES = 2;

    private final TreeSet<TreeNode> allNodes;
    private TreeNode root;
    private TreeMap<IP, TreeSet<IP>> cacheOfGraphMappings;

    /**
     * Constructor in which a tree with height value of 1 is created. Look into
     * {@link edu.kit.informatik.modelling.Network#Network(IP, List)} for a detailed
     * explanation and {@link UndirectedGraph#UndirectedGraph(IP, List)} for the
     * constructor operations.
     * 
     * @param root     the root of the tree with height 1.
     * @param children the root is connected to each one of the IPs in this
     *                 list(must contain at least one IP).
     */
    public Tree(IP root, List<IP> children) {
        super(root, children);
        this.allNodes = new TreeSet<TreeNode>();
        this.cacheOfGraphMappings = new TreeMap<>();
        this.root = null;
    }

    /**
     * Constructor in which mappings of an undirected graph is used to construct a
     * tree. Look into {@link UndirectedGraph#UndirectedGraph(TreeMap)}.
     * 
     * @param mapToCopy mappings of an undirected graph which is used to construct a
     *                  tree.
     */
    Tree(TreeMap<IP, TreeSet<IP>> mapToCopy) {
        super(mapToCopy);
        this.allNodes = new TreeSet<TreeNode>();
        this.cacheOfGraphMappings = new TreeMap<>();
        this.root = null;
    }

    /**
     * Checks if the tree is up to date. By comparing {@link #cacheOfGraphMappings}
     * with the mappings of {@link UndirectedGraph#getAdjacentEdgesMap()} and if
     * the @param checkRoot has the IPv4 address as {@link #root}.
     * 
     * @param checkRoot is to be checked if this IPv4 address is the root of the
     *                  current tree.
     * @return true, if {@link #cacheOfGraphMappings} has the same mappings as
     *         {@link UndirectedGraph#getAdjacentEdgesMap()} and if the @param
     *         checkRoot has the IPv4 address as {@link #root}. Otherwise return
     *         false.
     */
    private boolean isTreeUpToDate(IP checkRoot) {
        if (this.cacheOfGraphMappings == null || this.root == null) {
            return false;
        }
        return (this.root.getLabel().equals(checkRoot) && cacheOfGraphMappings.equals(getAdjacentEdgesMap()));
    }

    /**
     * Returns a copy of {@link #cacheOfGraphMappings}.
     * 
     * @return copy of {@link #cacheOfGraphMappings}.
     */
    public TreeMap<IP, TreeSet<IP>> getCacheOfGraphMappings() {
        return new TreeMap<>(this.cacheOfGraphMappings);
    }

    /**
     * Sets {@link #cacheOfGraphMappings} to the values in @param
     * cacheOfGraphMappings.
     * 
     * @param cacheOfGraphMappings a copy of this Map is to be made and set as
     *                             {@link #cacheOfGraphMappings}.
     */
    public void setCacheOfGraphMappings(TreeMap<IP, TreeSet<IP>> cacheOfGraphMappings) {
        if (cacheOfGraphMappings == null) {
            throw new IllegalArgumentException(ErrorMessage.GRAPH_NULL.toString());
        }
        this.cacheOfGraphMappings = new TreeMap<>(cacheOfGraphMappings);
    }

    /**
     * Gets {@link #root} IPv4-Address.
     * 
     * @return {@link #root} IPv4-Address.
     */
    public IP getIPofRoot() {
        return root.getLabel();
    }

    /**
     * Sets {@link #root} to the value of @param root.
     * 
     * @param root
     */
    public void setRoot(TreeNode root) {
        this.root = root;
    }

    /**
     * Builds the tree topology as @param newRoot as the new root {@link #root}.
     * 
     * @param newRoot is to be set as {@link #root}, a tree topology is build with
     *                this as root.
     * @return true, if build was successful.
     */
    private boolean buildTree(IP newRoot) {

        if (newRoot == null) {
            throw new IllegalArgumentException(ErrorMessage.IP_NULL.toString());
        }

        if (getAdjacentEdgesMap().size() < MINIMUM_NUMBER_OF_NODES || !containsIP(newRoot)) {
            return false;
        }

        if (!isTreeUpToDate(newRoot)) {

            setCacheOfGraphMappings(getAdjacentEdgesMap());
            this.allNodes.clear();
            TreeNode rootOfTree = new TreeNode(newRoot);

            this.allNodes.add(rootOfTree);

            addConnectionsToNodes(rootOfTree, getAdjacentEdgesMap());

            setRoot(rootOfTree);
        }
        return true;
    }

    /**
     * Is used to check if a given tree is acyclic, by building a tree
     * {@link #buildTree(IP)}.
     * 
     * @return true, if it is a undirected acyclic tree, else false.
     */
    public boolean isTree() {
        return buildTree(getAdjacentEdgesMap().firstKey());
    }

    /**
     * Creates a tree recursively with the help of @param graphMappings and @param
     * root as its root. It creates a new
     * {@link edu.kit.informatik.modelling.structures.nodes TreeNode} to every IP
     * that is connected to @param root, and runs the method with the new TreeNode
     * xyz and adds connections to xyz.
     * 
     * @param root
     * @param graphMappings
     */
    private void addConnectionsToNodes(TreeNode root, TreeMap<IP, TreeSet<IP>> graphMappings) {

        if (root == null) {
            throw new IllegalArgumentException(ErrorMessage.NODE_NULL.toString());
        }

        IP ipOfRoot = root.getLabel();

        if (graphMappings.get(ipOfRoot).isEmpty()) {
            return;
        }

        for (IP ipConnectionToRoot : graphMappings.get(ipOfRoot)) {

            TreeNode newNode = new TreeNode(root, ipConnectionToRoot);

            boolean contains = this.allNodes.contains(newNode);
            if (contains) {
                throw new CycleException(ErrorMessage.CYCLE_EMERGED_ADDING_ROOT.toString());
            }

            this.allNodes.add(newNode);

            boolean validConnection = graphMappings.get(ipConnectionToRoot).contains(ipOfRoot);
            if (!validConnection) {
                throw new CycleException(ErrorMessage.NOT_VALID_UNDIRECTED_GRAPH_ONE_SIDED_CONNECTION.toString());
            }

            graphMappings.get(ipConnectionToRoot).remove(ipOfRoot);

            addConnectionsToNodes(newNode, graphMappings);
        }
    }

    /**
     * Gets height of tree with @param rootas its root. Look into
     * {@link edu.kit.informatik.modelling.Network#getHeight(IP)} for a detailed
     * explanation
     * 
     * @param root is to be set as its root.
     * @return 0, if a problem occurred, else the height of the tree.
     */
    public int getHeight(IP root) {
        if (root == null) {
            return DEFAULT_RETURN_INT;
        }
        buildTree(root);
        return calculateHeight(getNodeWithIP(root));
    }

    /**
     * Calculates the height by recursively going through the nodes connected
     * to @param root and choosing the maximum height from each connection.
     * 
     * @param root
     * @return
     */
    private int calculateHeight(TreeNode root) {
        if (root.isLeaf()) {
            return DEFAULT_RETURN_INT;
        }
        int maxLevels = 0;
        for (TreeNode treeNodeConnection : root.getAdjacentBranches()) {
            int heightOfSubTree = calculateHeight(treeNodeConnection);
            if (heightOfSubTree >= maxLevels) {
                maxLevels = 1 + heightOfSubTree;
            }
        }
        return maxLevels;
    }

    /**
     * Gets the shortest path between @param start and @param end. Look into
     * {@link edu.kit.informatik.modelling.Network#getRoute(IP, IP)} for a detailed
     * explanation. Gets route between @param start and @param end by building a
     * tree with @param start as root and recursively getting the root from @param
     * end till you reach @param start.
     * 
     * @param start is the start IP to which the shortest path must be found to the
     *              end IP.
     * @param end   is the end IP to which the shortest path must be found from
     *              start IP.
     * @return a list of the individual IP addresses of the network nodes of the
     *         shortest path between the @param start and @param end nodes specified
     *         by the respective parameter.
     */
    public List<IP> getRoute(IP start, IP end) {
        if (!(containsIP(start) && containsIP(end))) {
            return new LinkedList<IP>();
        }
        buildTree(start);
        List<IP> route = new LinkedList<IP>();

        return getRouteBetweenNodes(getNodeWithIP(end), route);
    }

    /**
     * Gets route between {@link #getRoute(IP, IP)} IP start and IP end. This method
     * gets route by going to end node and searching upwards to the root, adding all
     * the nodes visited to a list.
     * 
     * @param end   this node keeps searching upwards till it reaches the root.
     * @param route the visited IPs of nodes must be added into this list.
     * @return List of IPs visited from end till root, whereas root is the first
     *         element in the list and end is the last element of the list.
     */
    private List<IP> getRouteBetweenNodes(TreeNode end, List<IP> route) {
        route.add(0, end.getLabel());
        if (end.isRoot()) {
            return route;
        }
        return getRouteBetweenNodes(end.getRoot(), route);
    }

    private TreeNode getNodeWithIP(IP label) {
        for (TreeNode treeNodeToIterate : allNodes) {
            if (treeNodeToIterate.getLabel().equals(label)) {
                return treeNodeToIterate;
            }
        }
        return null;
    }

    /**
     * Look into {@link edu.kit.informatik.modelling.Network#getLevels(IP)} for a
     * detailed explanation.
     * 
     * @param root a tree is to be build with @param root as root.
     * @return the level structure of a tree topology in list form.
     */
    public List<List<IP>> getLevels(IP root) {
        if (!containsIP(root)) {
            return new LinkedList<List<IP>>();
        }
        buildTree(root);
        List<List<IP>> listOfLevels = new LinkedList<List<IP>>();
        listOfLevels.add(0, new LinkedList<IP>());
        getEachLevel(this.root, 0, listOfLevels);

        for (List<IP> levelsOfTree : listOfLevels) {
            levelsOfTree.sort(null);
        }

        return new LinkedList<List<IP>>(listOfLevels);
    }

    /**
     * Recursively adds all the IPs in this instance into its respective levels.
     * 
     * @param root         is the root of a subtree.
     * @param level        level of the tree topology.
     * @param listOfLevels add the IPs into this list.
     */
    private void getEachLevel(TreeNode root, int level, List<List<IP>> listOfLevels) {

        if (level == listOfLevels.size()) {
            listOfLevels.add(level, new LinkedList<IP>());
        }

        listOfLevels.get(level).add(root.getLabel());

        if (root.isLeaf()) {
            return;
        }
        TreeSet<TreeNode> adjacentBranches = root.getAdjacentBranches();

        for (TreeNode nodeConnections : adjacentBranches) {
            getEachLevel(nodeConnections, level + 1, listOfLevels);
        }
        return;
    }

    /**
     * This method returns the bracket notation as a string for a tree topology.
     * Look into {@link edu.kit.informatik.modelling.Network#toString(IP)} for a
     * detailed explanation.
     * 
     * @param root is considered its top node.
     * @return the bracket notation as a string for a tree topology. If the given
     *         address is not assigned within the instance, only an instantiated
     *         empty string is returned.
     */
    public String toString(IP root) {
        if (!containsIP(root)) {
            return DEFAULT_RETURN_STRING;
        }
        buildTree(root);
        return toString(getNodeWithIP(root));
    }

    private String toString(TreeNode root) {

        String toReturn = root.getLabel().toString();

        if (root.isLeaf()) {
            return toReturn;
        }

        TreeSet<TreeNode> adjacentBranches = root.getAdjacentBranches();
        for (TreeNode treeNodeChild : adjacentBranches) {

            toReturn = toReturn + SPACE + toString(treeNodeChild);
        }

        return OPEN_BRACKET + toReturn + CLOSE_BRACKET;
    }

    /**
     * Gets deep-copy of this Tree, by using {@link #Tree(TreeMap)}.
     * 
     * @return deep-copy of this Tree.
     */
    public Tree getCopyOfTree() {
        return new Tree(this.getAdjacentEdgesMap());
    }

}
