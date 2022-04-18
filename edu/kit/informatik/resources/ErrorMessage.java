package edu.kit.informatik.resources;

/**
 * Represents all the error-messages, is only useful when an error occurs and
 * not for output. The bracket-notation errors are from
 * {@link edu.kit.informatik.modelling.Network#Network(String)} and the
 * point-notation errors are from
 * {@link edu.kit.informatik.modelling.IP#IP(String)}.
 * 
 * @author uogok
 * @version 15
 */
public enum ErrorMessage {

    /**
     * If the given point notation were not separated to four 8-bit blocks, each
     * separated by a period.
     */
    POINT_NOTATION_IP_NOT_FOUR_BLOCKS(
            "the written 32-bit addresses were not separated to four 8-bit blocks, each separated by a period."),

    /**
     * If the given point notation contains non-numbers.
     */
    POINT_NOTATION_CONTAINS_NON_NUMBERS("the given input must only contain numbers and three period'.'"),

    /**
     * If the given 8-Bit_Block contains leading zeros, the range should be from 0
     * to 255 and mustn't contain leading zeros.
     */
    POINT_NOTATION_CONTAINS_LEADING_ZEROS(
            "the given 8-Bit_Block contains leading zeros, the range should be from 0 to 255 "
            + "and mustn't contain leading zeros"),

    /**
     * If the given decimals in IPv4 aren't in range between 0 and 255.
     */
    POINT_NOTATION_NOT_IN_RANGE(
            "each given 8-Bit-Blocks must be a decimal value ranging from 0 to 255, and mustn't contain leading zeros"),

    /**
     * The given point notation String has a null value.
     */
    POINT_NOTATION_NULL("the given point notation String has a null value."),

    /**
     * The given bracket notation String has a null value.
     */
    BRACKET_NOTATION_NULL("the given bracket notation String has a null value."),

    /**
     * If the given bracket notation doesn't have the same number of opening and
     * closing brackets.
     */
    BRACKET_NOTATION_BRACKETS("the given bracket notation doesn't have equal number of '(' and ')'."),

    /**
     * If the given bracket notation contains too many spaces.
     */
    BRACKET_NOTATION_SPACE_ERROR("the given bracket notation contains too many spaces."),

    /**
     * If the given bracket notation needs more elements.
     */
    BRACKET_NOTATION_NOT_ENOUGH_ELEMENTS("the given bracket notation needs more elements."),

    /**
     * If the given bracket notation has too many elements.
     */
    BRACKET_NOTATION_TOO_MANY_ELEMENTS("the given bracket notation has too many elements."),

    /**
     * If the given bracket notation has duplicate IPs.
     */
    BRACKET_NOTATION_IP_DUPLICATE("the given bracket notation has duplicate IPs."),

    /**
     * If the entered IP has a null value.
     */
    IP_NULL("the entered IP has a null value."),

    /**
     * If the entered Graph has a null value.
     */
    GRAPH_NULL("the entered Graph has a null value."),

    /**
     * If the entered Map has a null value.
     */
    MAP_NULL("the entered Map has a null value."),

    /**
     * If the entered node has a null value. Which indicates that the entered IP has
     * a null value.
     */
    NODE_NULL("the entered node has a null value. Which indicates that the entered IP has a null value."),

    /**
     * If the children list is empty, so no graph can be instantiated. As the root
     * network node must have at least one connection to another node. Used in
     * {@link edu.kit.informatik.modelling.Network#Network(edu.kit.informatik.modelling.IP, java.util.List)}.
     */
    CHILDREN_LIST_EMPTY(
            "the children list is empty, so no graph can be instantiated. As the root network node must have at "
            + "least one connection to another node."),

    /**
     * If a cycle has emerged due to duplicate IPs.
     */
    CYCLE_EMERGED_DUPLICATE_IP("cycle has emerged due to duplicate IPs."),

    /**
     * If cycle emerged because two parent nodes in a tree had a connection to the
     * same child node.
     */
    CYCLE_EMERGED_ADDING_ROOT(
            "cycle emerged because two parent nodes in a tree had a connection to the same child node."),

    /**
     * If graph not valid as number of connections are lower than 1.
     */
    GRAPH_NOT_VALID("graph not valid as number of connections are lower than 1."),

    /**
     * If the given graph is not a valid undirected graph as the parent node has a
     * connection to the child node but the child node does not have a connection to
     * the parent node.
     */
    NOT_VALID_UNDIRECTED_GRAPH_ONE_SIDED_CONNECTION(
            "the given graph is not a valid undirected graph as the parent node has a connection to the child "
            + "node but the child node does not have a connection to the parent node."),

    /**
     * If an unexpected token occurs in
     * {@link edu.kit.informatik.parser.Parser#bracketNotationToTree(String)}.
     */
    UNEXPECTED_TOKEN("unexpected token"),

    /**
     * This is occurs if unusual errors happen, which shouldn't normally occur, but
     * it's good to check during testing. Occurs in
     * {@link edu.kit.informatik.parser.Parser#bracketNotationToTree(String)}.
     */
    ERROR_SHOULD_NOT_OCCUR_BRACKET_NOTATION_ADDING_TREES(
            "it is not possible for this error to occur, but if it does then it could be because of "
            + "'BRACKET_NOTATION_IP_DUPLICATE'."),

    /**
     * This is a check if unusual errors happen, which shouldn't normally occur, but
     * it's good to check during testing. Occurs in
     * {@link edu.kit.informatik.modelling.IP#deepCopy()}.
     */
    ERROR_SHOULD_NOT_OCCUR_IP_DEEP_COPY(
            "it is not possible for this error to occur. As the point notation of one existing IP must "
            + "be valid henceforth it is not possible for a ParseException to occur.");

    private static final String PREFIX = "Error, ";

    private final String message;

    private ErrorMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return PREFIX + this.message;
    }

}
