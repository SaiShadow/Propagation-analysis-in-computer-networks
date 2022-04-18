package edu.kit.informatik.modelling;

import java.util.ArrayList;
import java.util.List;

import edu.kit.informatik.exceptions.ParseException;
import edu.kit.informatik.modelling.structures.Grove;
import edu.kit.informatik.modelling.structures.Tree;
import edu.kit.informatik.parser.Parser;
import edu.kit.informatik.resources.ErrorMessage;

/**
 * This class represents a network which contains at least one tree topology at
 * all times. The class is the core element of this task and contains the most
 * important methods for attack propagation analysis. In addition to creating,
 * adding, and modifying tree topologies, it also provides other methods for
 * retrieving information about these tree topologies.
 * 
 * @author uogok
 * @version 3.4 `
 */
public class Network {

    private static final String DEFAULT_RETURN_STRING = "";
    private static final int DEFAULT_RETURN_INT = 0;

    private final Grove grove;

    /**
     * This constructor creates a new object instance and adds the given non-empty
     * and valid tree topology to the object instance. The tree topology has height
     * 1 with the root @param as the root and the network nodes (at least one) in
     * the children list as directly connected network nodes.
     * 
     * @param root     the root of the tree with height 1.
     * @param children the root is connected to each one of the IPs in this
     *                 list(must contain at least one IP).
     */
    public Network(final IP root, final List<IP> children) {
        if (root == null || children == null) {
            throw new IllegalArgumentException(ErrorMessage.IP_NULL.toString());
        }
        this.grove = new Grove(root.deepCopy(), deepCopy(children));
    }

    /**
     * This constructor creates a new object instance and adds the given non-empty
     * and valid tree topology to it. The tree topology is specified as a character
     * string in brackets and must correspond to the specified format of the
     * brackets introduced in the assignment. If this is not possible or the format
     * is violated, a ParseException is thrown in the constructor.
     * 
     * @param bracketNotation Enclosed by an opening ( and a closing ) parenthesis,
     *                        the network nodes of a single tree topology with a
     *                        height of 1 are combined. A hierarchical structure is
     *                        achieved by allowing bracketed tree topologies
     *                        (subtrees) within bracketed tree topologies.
     * @throws ParseException If constructing a tree is not possible or the format
     *                        is violated.
     */
    public Network(final String bracketNotation) throws ParseException {
        if (bracketNotation == null) {
            throw new ParseException(ErrorMessage.BRACKET_NOTATION_NULL.toString());
        }
        this.grove = new Grove(new Parser().bracketNotationToTree(bracketNotation));
    }

    /**
     * This method copies the tree topologies of the passed object instance into its
     * own instance. If connections or network nodes from the two instances are the
     * same due to their IP addresses, they are merged. Look into
     * {@link edu.kit.informatik.modelling.structures.Grove#add(Grove)} for the
     * detailed execution/calculation.
     * 
     * @param subnet the trees contained in the subnet are to be copied to this
     *               network.
     * @return If the tree topologies could be successfully copied and thus the own
     *         instance has changed internally, true is returned, otherwise false is
     *         always returned.
     */
    public boolean add(final Network subnet) {
        if (subnet == null) {
            return false;
        }
        return this.grove.add(subnet.getGrove());
    }

    /**
     * This method returns the IP addresses of all network nodes currently present
     * in the object instance in a new and independent list. The addresses in this
     * list are sorted in ascending order according to their overall natural order.
     * Look into {@link edu.kit.informatik.modelling.structures.Grove#list()} for
     * the detailed execution/calculation.
     * 
     * @return the IP addresses of all network nodes currently present in the object
     *         instance in natural order.
     */
    public List<IP> list() {
        return this.grove.list();
    }

    /**
     * This method adds a new connection between two existing network nodes. These
     * are determined by the two parameters for their respective IP addresses.Look
     * into {@link edu.kit.informatik.modelling.structures.Grove#connect(IP, IP)}
     * for the detailed execution/calculation.
     * 
     * @param ip1 is to be connected to ip2.
     * @param ip2 is to be connected to ip1.
     * @return If a new connection could be successfully added, true is returned,
     *         otherwise false is always returned.
     */
    public boolean connect(final IP ip1, final IP ip2) {
        if (ip1 == null || ip2 == null || ip1.equals(ip2)) {
            return false;
        }
        return this.grove.connect(ip1, ip2);
    }

    /**
     * This method removes an existing connection between two network nodes. These
     * two network nodes are determined based on the transferred IP addresses. If a
     * node then has degree 0, it is removed from the object instance so that its IP
     * address can be reassigned. If there is only one connection left in the
     * network, it must not be removed, otherwise there would be no more network
     * nodes. Look into
     * {@link edu.kit.informatik.modelling.structures.Grove#disconnect(IP, IP)} for
     * the detailed execution/calculation.
     * 
     * @param ip1 is to be disconnected from ip2.
     * @param ip2 is to be disconnected from ip1.
     * @return If a connection was successfully removed, true is returned, otherwise
     *         false is always returned.
     */
    public boolean disconnect(final IP ip1, final IP ip2) {
        if (ip1 == null || ip2 == null || ip1.equals(ip2)) {
            return false;
        }
        return this.grove.disconnect(ip1, ip2);
    }

    /**
     * This method returns true if the object instance contains a node with the
     * specified IP address. Otherwise false is always returned. Look into
     * {@link edu.kit.informatik.modelling.structures.Grove#contains(IP)} for the
     * detailed execution/calculation.
     * 
     * @param ip check if this IP is within this network.
     * @return This method returns true if the object instance contains a node with
     *         the specified IP address. Otherwise false is always returned.
     */
    public boolean contains(final IP ip) {
        if (ip == null) {
            return false;
        }
        return this.grove.contains(ip);
    }

    /**
     * This method returns the integer height of a tree topology. This tree topology
     * is picked up at the existing node specified by the so that it is considered
     * the top node. If the specified IP address is not assigned internally, 0 is
     * always returned. Look into
     * {@link edu.kit.informatik.modelling.structures.Tree#getHeight(IP)} for the
     * detailed execution/calculation.
     * 
     * @param root this IP is to be set as root to calculate the height of the tree.
     * @return height of the tree topology. If the specified IP address is not
     *         assigned internally, 0 is always returned.
     */
    public int getHeight(final IP root) {
        if (root == null || !contains(root)) {
            return DEFAULT_RETURN_INT;
        }
        return getTreeWithIP(root).getHeight(root);
    }

    /**
     * This method outputs the level structure of a tree topology in list form. The
     * addresses of the network nodes of a level are inserted into a sorted list.
     * The total level structure returned is, in turn, an ascending sorted list of
     * these individual level lists. This tree topology is picked up at the existing
     * node specified by the parameter, so this is considered to be its top node.
     * This given node is assigned the first level and its IP address is inserted as
     * the only element of the first inner list. The IP addresses of the next level
     * are then inserted in the list below. The addresses in the inner lists for the
     * levels are sorted in ascending order of their overall natural order. Look
     * into {@link edu.kit.informatik.modelling.structures.Tree#getLevels(IP)} for
     * the detailed execution/calculation.
     * 
     * @param root is considered to be the tree topology's top node.
     * @return the level structure of a tree topology in list form. If there is no
     *         node with the specified IP address, only an instantiated empty list
     *         is returned.
     */
    public List<List<IP>> getLevels(final IP root) {
        if (root == null || !contains(root)) {
            return new ArrayList<>();
        }
        return getTreeWithIP(root).getLevels(root);
    }

    /**
     * This method returns a list of the individual IP addresses of the network
     * nodes of the shortest path between the start and end nodes specified by the
     * respective parameter. The IP address of the starting node is the first item
     * in this list and the IP address of the ending node is the last item. The
     * consecutive network nodes in the list must always be connected by a
     * connection in the tree topology. If one of the two specified network nodes
     * does not exist or if there is no path between the two, only an instantiated
     * empty list is returned. Look into
     * {@link edu.kit.informatik.modelling.structures.Tree#getRoute(IP, IP)} for the
     * detailed execution/calculation.
     * 
     * @param start is the start IP to which the shortest path must be found to the
     *              end IP.
     * @param end   is the end IP to which the shortest path must be found from
     *              start IP.
     * @return a list of the individual IP addresses of the network nodes of the
     *         shortest path between the start and end nodes specified by the
     *         respective parameter.
     */
    public List<IP> getRoute(final IP start, final IP end) {
        if (start == null || end == null || start.equals(end) || !contains(start) || !contains(end)) {
            return new ArrayList<>();
        }
        return getTreeWithIP(start).getRoute(start, end);
    }

    /**
     * This method returns the bracket notation as a string for a tree topology. At
     * this point, the IP addresses for each level must be sorted in ascending order
     * according to their 32-bit value within this bracket notation. Consequently,
     * the IP addresses with the lowest bit value are listed first in a
     * left-to-right level. This tree topology is picked up at the existing node
     * specified by the parameter, so this is considered its top node. Look into
     * {@link edu.kit.informatik.modelling.structures.Tree#toString(IP)} for the
     * detailed execution/calculation.
     * 
     * @param root is considered its top node.
     * @return the bracket notation as a string for a tree topology. If the given
     *         address is not assigned within the instance, only an instantiated
     *         empty string is returned.
     */
    public String toString(IP root) {
        if (root == null || !contains(root)) {
            return DEFAULT_RETURN_STRING;
        }
        return getTreeWithIP(root).toString(root);
    }

    @Override
    public boolean equals(Object other) {

        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        Network subnet = (Network) other;

        return this.grove.equals(subnet.getGrove());
    }

    /**
     * Returns a deep-copy of Grove i.e. a copy of all the trees contained in this
     * network. Look into
     * {@link edu.kit.informatik.modelling.structures.Grove#getCopyOfGrove()} for
     * the detailed execution/calculation.
     * 
     * @return a deep-copy of Grove.
     */
    public Grove getGrove() {
        return this.grove.getCopyOfGrove();
    }

    private Tree getTreeWithIP(IP ip) {
        return this.grove.getTreeWithIP(ip);
    }

    private List<IP> deepCopy(List<IP> toCopy) {
        List<IP> copy = new ArrayList<>();
        for (IP value : toCopy) {
            if (value == null) {
                throw new IllegalArgumentException(ErrorMessage.IP_NULL.toString());
            }
            copy.add(value.deepCopy());
        }
        return copy;
    }
}
