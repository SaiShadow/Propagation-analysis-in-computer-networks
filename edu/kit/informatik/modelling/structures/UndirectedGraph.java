package edu.kit.informatik.modelling.structures;

import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import edu.kit.informatik.exceptions.CycleException;
import edu.kit.informatik.modelling.IP;
import edu.kit.informatik.resources.ErrorMessage;

/**
 * This class represents an undirected graph.
 * 
 * @author uogok
 * @version 14.4
 */
public class UndirectedGraph {

    private boolean joined;
    private final TreeMap<IP, TreeSet<IP>> adjacentEdgesMap;

    /**
     * Constructor for this class look into
     * {@link edu.kit.informatik.modelling.Network#Network(IP, List)} for a detailed
     * explanation.
     * 
     * @param root     the root of the tree with height 1.
     * @param children the root is connected to each one of the IPs in this
     *                 list(must contain at least one IP).
     */
    UndirectedGraph(final IP root, final List<IP> children) {

        if (root == null) {
            throw new IllegalArgumentException(ErrorMessage.IP_NULL.toString());
        }

        if (children.isEmpty()) {
            throw new IllegalArgumentException(ErrorMessage.CHILDREN_LIST_EMPTY.toString());
        }

        adjacentEdgesMap = new TreeMap<>();
        constructGraph(root, children);

    }

    /**
     * Constructor in which the mappings are already in @param.
     * 
     * @param mapToCopy sets {@link #adjacentEdgesMap} to the mappings of this map.
     */
    UndirectedGraph(TreeMap<IP, TreeSet<IP>> mapToCopy) {

        if (mapToCopy == null) {
            throw new IllegalArgumentException(ErrorMessage.MAP_NULL.toString());
        }
        this.adjacentEdgesMap = new TreeMap<>(getCopyOfMap(mapToCopy));
    }

    private void constructGraph(final IP root, final List<IP> children) {
        addNode(root);
        addConnections(root, children);
    }

    private void addConnections(final IP root, final List<IP> children) {
        for (IP ipToConnectToRoot : children) {
            addNode(ipToConnectToRoot);
            connect(root, ipToConnectToRoot);
        }
    }

    /**
     * This method adds another graph to this graph while avoiding cycle as much as
     * possible but it isn't guaranteed, to perfectly avoid cycles you must check
     * with {@link Tree#isTree()}.
     * 
     * @param graphToAdd
     * @return true, if you could successfully merge @param graphToAdd into this
     *         instance, otherwise return false.
     */
    public boolean addGraph(UndirectedGraph graphToAdd) {

        if (graphToAdd == null) {
            throw new IllegalArgumentException(ErrorMessage.GRAPH_NULL.toString());
        }

        if (this.equals(graphToAdd)) {
            return true;
        }

        UndirectedGraph copyOfOriginalGraph = this.getCopyOfGraph();
        UndirectedGraph copyOfGraphToAdd = graphToAdd.getCopyOfGraph();

        TreeMap<IP, TreeSet<IP>> mergedMap = mergeMaps(copyOfOriginalGraph.getAdjacentEdgesMap(),
                copyOfGraphToAdd.getAdjacentEdgesMap());

        if (mergedMap == null) {
            return false;
        }
        if (copyOfOriginalGraph.getAdjacentEdgesMap().equals(mergedMap)) {
            return true;
        }

        this.adjacentEdgesMap.clear();
        this.adjacentEdgesMap.putAll(mergedMap);

        return true;
    }

    /**
     * Merges the maps in @param into one Map. Is used in
     * {@link #addGraph(UndirectedGraph)}.
     * 
     * @param mapGiven            is to be merged with @param mapToMergeIntoGiven.
     * @param mapToMergeIntoGiven is to be merged with @param mapGiven.
     * @return null if merge unsuccessful, that is if the maps to merge do not have
     *         any common IPs, else return a merged map.
     */
    private TreeMap<IP, TreeSet<IP>> mergeMaps(TreeMap<IP, TreeSet<IP>> mapGiven,
            TreeMap<IP, TreeSet<IP>> mapToMergeIntoGiven) {

        TreeMap<IP, TreeSet<IP>> copyOfMapGiven = getCopyOfMap(mapGiven);
        TreeMap<IP, TreeSet<IP>> copyOfMapToMergeIntoGiven = getCopyOfMap(mapToMergeIntoGiven);

        TreeSet<IP> commonKeys = getCommonKeys(copyOfMapGiven, copyOfMapToMergeIntoGiven);

        boolean hasNoCommonIP = commonKeys == null || commonKeys.isEmpty();
        if (hasNoCommonIP) {
            return null;
        }

        /**
         * Copy the set of connections of all common nodes to the map you want to merge.
         * After that put all the mappings for the mapToMergeIntoGiven to the given map.
         * This does not check if the map contains cycles.
         */
        for (IP commonIP : commonKeys) {
            copyOfMapToMergeIntoGiven.get(commonIP).addAll(copyOfMapGiven.get(commonIP));
        }
        copyOfMapGiven.putAll(copyOfMapToMergeIntoGiven);
        return copyOfMapGiven;
    }

    private TreeSet<IP> getCommonKeys(TreeMap<IP, TreeSet<IP>> map1, TreeMap<IP, TreeSet<IP>> map2) {

        if (map1 == null || map2 == null) {
            throw new IllegalArgumentException(ErrorMessage.MAP_NULL.toString());
        }

        /**
         * You need a copy here as you change the values of the map.
         */
        TreeMap<IP, TreeSet<IP>> map1Copy = getCopyOfMap(map1);
        TreeMap<IP, TreeSet<IP>> map2Copy = getCopyOfMap(map2);

        map1Copy.keySet().retainAll(map2Copy.keySet());

        return new TreeSet<IP>(map1Copy.keySet());
    }

    private void connect(IP ip1, IP ip2) {

        boolean addedFirstIP = adjacentEdgesMap.get(ip2).add(ip1);
        boolean addedSecondIP = adjacentEdgesMap.get(ip1).add(ip2);

        if (!(addedFirstIP && addedSecondIP)) {
            throw new CycleException(ErrorMessage.CYCLE_EMERGED_DUPLICATE_IP.toString());
        }
    }

    private void addNode(IP ipToAdd) {
        if (containsIP(ipToAdd)) {
            throw new CycleException(ErrorMessage.CYCLE_EMERGED_DUPLICATE_IP.toString());
        }
        adjacentEdgesMap.put(ipToAdd, new TreeSet<IP>());
    }

    private TreeSet<IP> getCopyOfTreeSetOfIPs(TreeSet<IP> treeSetToCopy) {
        return new TreeSet<IP>(treeSetToCopy);
    }

    /**
     * Gets the value of {@link #joined}. Look into {@link Grove#add(Grove)} as this
     * is used in there(in the helper methods).
     * 
     * @return true if this Graph was merged by another graph.
     */
    public boolean isJoined() {
        return joined;
    }

    /**
     * Sets the value of {@link #joined} to the value of @param joined. Look into
     * {@link Grove#add(Grove)} as this is used in there(in the helper methods).
     * 
     * @param joined this value is to be set to {@link #joined} .
     */
    public void setJoined(boolean joined) {
        this.joined = joined;
    }

    private TreeMap<IP, TreeSet<IP>> getCopyOfMap(TreeMap<IP, TreeSet<IP>> mapToCopy) {

        if (mapToCopy == null) {
            throw new IllegalArgumentException(ErrorMessage.MAP_NULL.toString());
        }
        TreeMap<IP, TreeSet<IP>> copyMap = new TreeMap<>();

        for (Entry<IP, TreeSet<IP>> keyIterator : mapToCopy.entrySet()) {
            copyMap.put(keyIterator.getKey(), getCopyOfTreeSetOfIPs(keyIterator.getValue()));
        }
        return copyMap;
    }

    /**
     * Gets a copy of the mappings of the {@link #adjacentEdgesMap}.
     * 
     * @return copy of the mappings of {@link #adjacentEdgesMap}.
     */
    public TreeMap<IP, TreeSet<IP>> getAdjacentEdgesMap() {
        return getCopyOfMap(this.adjacentEdgesMap);
    }

    /**
     * Gets a copy of graph by getting a copy of {@link #adjacentEdgesMap} by using
     * {@link #getAdjacentEdgesMap()} into {@link #UndirectedGraph(TreeMap)} to get
     * a new graph with the same mappings.
     * 
     * @return copy of this graph.
     */
    public UndirectedGraph getCopyOfGraph() {
        return new UndirectedGraph(this.getAdjacentEdgesMap());
    }

    /**
     * Checks if @param ipToSearch exists as a node in {@link #adjacentEdgesMap}.
     * 
     * @param ipToSearch search if this IPv4-Address exists as a node in this graph.
     * @return true, if @param ipToSearch exists in {@link #adjacentEdgesMap},
     *         otherwise false.
     */
    public boolean containsIP(IP ipToSearch) {
        return this.adjacentEdgesMap.containsKey(ipToSearch);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        UndirectedGraph otherGraph = (UndirectedGraph) other;

        return this.getAdjacentEdgesMap().equals(otherGraph.getAdjacentEdgesMap());
    }

}
