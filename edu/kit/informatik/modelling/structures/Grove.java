
package edu.kit.informatik.modelling.structures;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import edu.kit.informatik.exceptions.CycleException;
import edu.kit.informatik.modelling.IP;
import edu.kit.informatik.resources.ErrorMessage;

/**
 * This class represents a collection of Trees{@link Tree}. Serves as connection
 * between {@link edu.kit.informatik.modelling.Network} and {@link Tree}.
 * 
 * @author uogok
 * @version 4.5
 */
public class Grove {

    private static final int MINIMUM_NUMBER_OF_NODES_REQUIRED_IN_TREE = 2;
    private static final int MINIMUM_NUMBER_OF_TREES_REQUIRED_IN_GROVE = 1;
    private final List<Tree> forest;

    /**
     * Constructor in which a tree with height value of 1 is created and added to
     * the list. Look into
     * {@link edu.kit.informatik.modelling.Network#Network(IP, List)} for a detailed
     * explanation.
     * 
     * @param root     the root of the tree with height 1.
     * @param children the root is connected to each one of the IPs in this
     *                 list(must contain at least one IP).
     */
    public Grove(final IP root, final List<IP> children) {
        this.forest = new LinkedList<>();
        Tree tree = new Tree(root, children);
        this.forest.add(tree);
    }

    /**
     * Constructor in which a new tree with the same mappings as the
     * undirected-graph is to be added to the forest.
     * 
     * @param graph the mappings in the graph are to be copied and a new tree is to
     *              be instantiated to be added to the forest.
     */
    public Grove(UndirectedGraph graph) {
        if (graph == null) {
            throw new IllegalArgumentException(ErrorMessage.GRAPH_NULL.toString());
        }
        this.forest = new LinkedList<>();
        Tree tree = new Tree(graph.getAdjacentEdgesMap());
        this.forest.add(tree);
    }

    /**
     * Constructor to make deep copying a grove easier.
     * 
     * @param forest a new Grove is to instantiated by copying the list of trees in
     *               the forest from the parameter and adding it to this.forest.
     */
    Grove(List<Tree> forest) {
        this.forest = new LinkedList<>(getCopyOfList(forest));
    }

    /**
     * Look into {@link edu.kit.informatik.modelling.Network#contains(IP)} for a
     * detailed explanation.
     * 
     * @param ipToSearch check if this IP exists in the Grove.
     * @return true, if IP exists in this Grove i.e. in one of the Trees contained
     *         in the Grove.
     */
    public boolean contains(IP ipToSearch) {
        return (getTreeWithIP(ipToSearch) != null);
    }

    /**
     * Look into {@link edu.kit.informatik.modelling.Network#list()} for a detailed
     * explanation.
     * 
     * @return a sorted list of all IPs that exist in the Grove.
     */
    public List<IP> list() {
        List<IP> listOfIPs = new LinkedList<IP>();
        for (Tree treeInForest : forest) {
            listOfIPs.addAll(treeInForest.getAdjacentEdgesMap().keySet());
        }
        listOfIPs.sort(null);
        return listOfIPs;
    }

    /**
     * Look into {@link edu.kit.informatik.modelling.Network#connect(IP, IP)} for a
     * detailed explanation.
     * 
     * @param ip1 is to be connected to ip2.
     * @param ip2 is to be connected to ip1.
     * @return true, if successfully connected both IPs specified in the parameter.
     */
    public boolean connect(final IP ip1, final IP ip2) {

        if (ip1.equals(ip2)) {
            return false;
        }
        Tree treeWithIP1 = getTreeWithIP(ip1);
        Tree treeWithIP2 = getTreeWithIP(ip2);

        if (treeWithIP1 == null || treeWithIP2 == null || treeWithIP1.equals(treeWithIP2)) {
            return false;
        }

        TreeMap<IP, TreeSet<IP>> mappingsOfTree1 = treeWithIP1.getAdjacentEdgesMap();
        TreeMap<IP, TreeSet<IP>> mappingsOfTree2 = treeWithIP2.getAdjacentEdgesMap();

        mappingsOfTree1.get(ip1).add(ip2);
        mappingsOfTree2.get(ip2).add(ip1);
        mappingsOfTree1.putAll(mappingsOfTree2);

        this.forest.add(new Tree(mappingsOfTree1));

        this.forest.remove(treeWithIP1);
        this.forest.remove(treeWithIP2);
        return true;
    }

    /**
     * Look into {@link edu.kit.informatik.modelling.Network#disconnect(IP, IP)} for
     * a detailed explanation. This method first removes ip1 from ip2's
     * connection-list by removing it from the mappings from the
     * {@link UndirectedGraph#getAdjacentEdgesMap()} and vice-versa. And then I get
     * the independent mappings by using
     * {@link #getIndividualGraphs(IP, TreeMap, TreeMap)}. With the help of the two
     * new independent Maps I add Trees {@link Tree#Tree(TreeMap)} and add it into
     * {@link #forest} and remove the original tree which contained @param ip1
     * and @param ip2 from {@link #forest}.
     * 
     * @param ip1 is to be disconnected from ip2.
     * @param ip2 is to be disconnected from ip1.
     * @return true, if a connection was successfully removed, otherwise false is
     *         always returned.
     */
    public boolean disconnect(final IP ip1, final IP ip2) {

        if (ip1.equals(ip2)) {
            return false;
        }
        Tree treeWithIP = getTreeWithIP(ip1);

        if (treeWithIP == null || !treeWithIP.containsIP(ip2)) {
            return false;
        }
        TreeMap<IP, TreeSet<IP>> mappingsOfTree = treeWithIP.getAdjacentEdgesMap();

        if (mappingsOfTree.size() == MINIMUM_NUMBER_OF_NODES_REQUIRED_IN_TREE) {
            if (forest.size() == MINIMUM_NUMBER_OF_TREES_REQUIRED_IN_GROVE) {
                return false;
            } else {
                this.forest.remove(treeWithIP);
                return true;
            }
        }

        if (!mappingsOfTree.get(ip1).contains(ip2)) {
            return false;
        }

        mappingsOfTree.get(ip1).remove(ip2);
        mappingsOfTree.get(ip2).remove(ip1);

        boolean ip1DoesNotHaveConnections = mappingsOfTree.get(ip1).isEmpty();
        boolean ip2DoesNotHaveConnections = mappingsOfTree.get(ip2).isEmpty();

        if (ip2DoesNotHaveConnections || ip1DoesNotHaveConnections) {
            if (ip1DoesNotHaveConnections) {
                mappingsOfTree.remove(ip1);
            } else {
                mappingsOfTree.remove(ip2);
            }
            this.forest.add(new Tree(mappingsOfTree));
            this.forest.remove(treeWithIP);
            return true;
        }

        TreeMap<IP, TreeSet<IP>> mapToCopyInto = new TreeMap<>();

        getIndividualGraphs(ip1, mappingsOfTree, mapToCopyInto);

        this.forest.add(new Tree(mappingsOfTree));
        this.forest.add(new Tree(mapToCopyInto));
        this.forest.remove(treeWithIP);
        return true;
    }

    /**
     * This method recursively adds all the nodes connected(here a node x is
     * connected to @param ipToAdd when a path exists from ipToAdd to x) to @param
     * ipToAdd from @param mappingsOfTree to @param mapToCopyInto. Look into
     * {@link #disconnect(IP, IP)} as this method is called in from
     * {@link #disconnect(IP, IP)}, as this method separates two independent tree
     * topologies which are in @param mappingsOfTree and adds the independent tree
     * topology which contains @param ipToAdd into @param mapToCopyInto.
     * 
     * @param ipToAdd        all the nodes connected to this node is to be removed
     *                       from @param mappingsOfTree and added into @param
     *                       mapToCopyInto.
     * @param mappingsOfTree removes all the nodes connected to @param
     *                       ipToAdd(itself too).
     * @param mapToCopyInto  adds all the nodes connected to @param ipToAdd(counting
     *                       itself too).
     */
    private void getIndividualGraphs(final IP ipToAdd, TreeMap<IP, TreeSet<IP>> mappingsOfTree,
            TreeMap<IP, TreeSet<IP>> mapToCopyInto) {

        if (!mappingsOfTree.containsKey(ipToAdd)) {
            return;
        }

        TreeSet<IP> ipsConnectedToIPtoAdd = new TreeSet<>(mappingsOfTree.get(ipToAdd));

        mapToCopyInto.putIfAbsent(ipToAdd, ipsConnectedToIPtoAdd);
        mappingsOfTree.remove(ipToAdd);

        for (IP ipConnection : ipsConnectedToIPtoAdd) {
            getIndividualGraphs(ipConnection, mappingsOfTree, mapToCopyInto);
        }
    }

    /**
     * Gets the tree that contains @param ipToSearch.
     * 
     * @param ipToSearch gets tree which contains this IP.
     * @return Tree which contains @param ipToSearch, if no Tree exists with @param
     *         ipToSearch then returns null.
     */
    public Tree getTreeWithIP(IP ipToSearch) {
        for (Tree treeInForest : forest) {
            if (treeInForest.containsIP(ipToSearch)) {
                return treeInForest;
            }
        }
        return null;
    }

    /**
     * Look into
     * {@link edu.kit.informatik.modelling.Network#add(edu.kit.informatik.modelling.Network)}
     * for a detailed explanation.
     * 
     * @param groveToAdd adds all the trees existing in @param groveToAdd into this
     *                   instance. If two trees have the same IPs then they are
     *                   merged, unless a cycle emerges then the procedure ends
     *                   while not changing the own instance(i.e. if the add fails
     *                   then this instance has the same Grove as before executing
     *                   the add).
     * @return true, if the tree topologies could be successfully copied and thus
     *         the own instance has changed internally, otherwise false is returned.
     */
    public boolean add(Grove groveToAdd) {

        if (this.equals(groveToAdd)) {
            return false;
        }

        List<Tree> treesInOriginalGrove = this.getCopyOfAllTreesInGrove();
        List<Tree> treesInGroveToAdd = groveToAdd.getCopyOfAllTreesInGrove();

        boolean addSuccessful = true;
        try {
            addSuccessful = addGraphsFromTwoLists(treesInOriginalGrove, treesInGroveToAdd);
        } catch (IllegalArgumentException errorMessage) {
            return false;
        }
        return addSuccessful;
    }

    /**
     * This method adds every tree topology from @param setOfTreesInOriginalGrove to
     * every tree topology from @param setOfTreesInGroveToAdd. Then sends the
     * setOfTreesInOriginalGrove to {@link #addGraphsFromOneList(List)} to add the
     * tree topologies that have already been joined once, as there might be other
     * tree topologies in setOfTreesInOriginalGrove that have common IPs.
     * 
     * @param setOfTreesInOriginalGrove add all the tree topologies existing within
     *                                  this list with all the tree
     *                                  topologies @param setOfTreesInGroveToAdd.
     * @param setOfTreesInGroveToAdd    add all the tree topologies existing within
     *                                  this list with all the tree
     *                                  topologies @param setOfTreesInOriginalGrove.
     * @return true, if the tree topologies could be successfully copied and thus
     *         the own instance has changed internally, otherwise false is returned.
     */
    private boolean addGraphsFromTwoLists(List<Tree> setOfTreesInOriginalGrove, List<Tree> setOfTreesInGroveToAdd) {

        for (Tree treeInOriginalGrove : setOfTreesInOriginalGrove) {
            for (Tree treeInGroveToCopy : setOfTreesInGroveToAdd) {
                boolean addSuccessful = addTwoTrees(treeInOriginalGrove, treeInGroveToCopy);
                if (addSuccessful) {
                    treeInGroveToCopy.setJoined(true);
                }
            }
        }

        for (Tree treeInGroveToCopy : setOfTreesInGroveToAdd) {
            if (!treeInGroveToCopy.isJoined()) {
                setOfTreesInOriginalGrove.add(treeInGroveToCopy);
            }
        }

        addGraphsFromOneList(setOfTreesInOriginalGrove);

        if (this.getCopyOfAllTreesInGrove().equals(setOfTreesInOriginalGrove)) {
            return false;
        }

        this.forest.clear();
        boolean changedTheForest = this.forest.addAll(setOfTreesInOriginalGrove);
        return changedTheForest;
    }

    /**
     * Recursively adds all tree topologies that have been merged at least once in
     * {@link #addGraphsFromTwoLists(List, List)} or
     * {@link #addGraphsFromOneList(List)}. If @param setOfTrees only has a maximum
     * of one tree topology that has previously been merged then do nothing and
     * return, else continue recursively.
     * 
     * @param setOfTrees add all the tree topologies in this list, if the tree
     *                   topologies have previously been merged.
     */
    private void addGraphsFromOneList(List<Tree> setOfTrees) {

        List<Tree> joinedTrees = new LinkedList<>();

        for (Tree treeIterator : setOfTrees) {
            if (treeIterator.isJoined()) {
                treeIterator.setJoined(false);
                joinedTrees.add(treeIterator);
            }
        }

        if (joinedTrees.size() <= 1) {
            return;
        }
        setOfTrees.removeAll(joinedTrees);

        List<Tree> treesToRemove = new LinkedList<>();

        for (int i = 0; i < joinedTrees.size(); i++) {
            for (int j = i + 1; j < joinedTrees.size(); j++) {
                Tree original = joinedTrees.get(i);
                Tree treeToAdd = joinedTrees.get(j);
                boolean addSuccessful = addTwoTrees(original, treeToAdd);
                if (addSuccessful) {
                    treesToRemove.add(treeToAdd);
                }
            }
        }
        joinedTrees.removeAll(treesToRemove);
        addGraphsFromOneList(joinedTrees);
        setOfTrees.addAll(joinedTrees);
    }

    /**
     * Adds two tree topologies, if the resulting tree is not acyclic then a
     * CycleException is thrown.
     * 
     * @param treeOriginal
     * @param treeToAdd
     * @return returns value of {@link UndirectedGraph#addGraph(UndirectedGraph)}.
     */
    private boolean addTwoTrees(Tree treeOriginal, Tree treeToAdd) {

        boolean addSuccessful = treeOriginal.addGraph(treeToAdd);
        if (addSuccessful) {
            treeOriginal.setJoined(true);
            if (!treeOriginal.isTree()) {
                throw new CycleException(ErrorMessage.GRAPH_NOT_VALID.toString());
            }
        }
        return addSuccessful;
    }

    private List<Tree> getCopyOfList(List<Tree> setToCopy) {
        if (setToCopy == null) {
            throw new IllegalArgumentException(ErrorMessage.MAP_NULL.toString());
        }

        LinkedList<Tree> setToReturn = new LinkedList<>();
        for (Tree treeInForest : setToCopy) {

            setToReturn.add(treeInForest.getCopyOfTree());
        }

        return setToReturn;
    }

    /**
     * Returns a deep-copy(Check out{@link #getCopyOfList(List)}) of all tree
     * topologies existing in this instance as a List<Tree>. Is the getter for
     * {@link #forest}.
     * 
     * @return a deep-copy of all tree topologies existing in this instance as a
     *         List<Tree>.
     */
    public List<Tree> getCopyOfAllTreesInGrove() {
        return getCopyOfList(this.forest);
    }

    /**
     * Returns a deep-copy of the current Grove by using {@link #Grove(List)}.
     * 
     * @return deep-copy of the current Grove.
     */
    public Grove getCopyOfGrove() {
        return new Grove(getCopyOfAllTreesInGrove());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }

        return this.getCopyOfAllTreesInGrove().equals(((Grove) other).getCopyOfAllTreesInGrove());
    }

}
