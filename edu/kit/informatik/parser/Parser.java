package edu.kit.informatik.parser;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.Pattern;

import edu.kit.informatik.exceptions.ParseException;
import edu.kit.informatik.modelling.IP;
import edu.kit.informatik.modelling.structures.Tree;
import edu.kit.informatik.resources.ErrorMessage;

/**
 * This class represents a parser which is used for parsing the bracketNotation
 * and the pointNotation in
 * {@link edu.kit.informatik.modelling.Network#Network(String)} and
 * {@link edu.kit.informatik.modelling.IP#IP(String)}.
 * 
 * @author uogok
 * @version 14
 */
public class Parser {

    private static final String PERIOD = ".";
    private static final char PERIOD_AS_CHAR = '.';
    private static final String EMPTY_STRING = "";
    private static final String OPEN_BRACKET = "(";
    private static final String CLOSE_BRACKET = ")";
    private static final char CLOSE_BRACKET_AS_CHAR = ')';
    private static final String SPACE = " ";
    private static final int MAX_NUMBER_OF_BYTES = 4;
    private static final int BYTE_SIZE_TO_BIT = 8;
    private static final int MAX_VALUE_ONE_BYTE = 255;
    private static final int MIN_VALUE_ONE_BYTE = 0;

    private String lookahead;
    private StringTokenizer tokenizer;
    private List<Tree> listOfTrees;
    private int numberOfOpeningBracketsTillNow;
    private int numberOfClosingBracketsTillNow;
    private TreeSet<IP> ipsFoundInBracketNotation;

    /**
     * Converts the @param pointNotation into a IPv4 in an int representation
     * (similar to the RGBA conversion done in Assignment 4A). Look into
     * {@link edu.kit.informatik.modelling.IP#IP(String)} for detailed information
     * about the pointNotation.
     * 
     * @param pointNotation pointNotation The character string passed must
     *                      correspond to the format of the decimal point notation.
     * @return IPv4 in an int representation (similar to the RGBA conversion done in
     *         Assignment 4A).
     * @throws ParseException if the pointNotation is not in format of the decimal
     *                        point notation or check is not possible then a
     *                        ParseException is thrown in the constructor.
     */
    public int pointNotationToInt(String pointNotation) throws ParseException {

        if (pointNotation == null) {
            throw new ParseException(ErrorMessage.POINT_NOTATION_NULL.toString());
        }

        String[] byteArray = pointNotation.split(Pattern.quote(PERIOD));

        if (byteArray.length != MAX_NUMBER_OF_BYTES
                || pointNotation.charAt(pointNotation.length() - 1) == PERIOD_AS_CHAR) {
            throw new ParseException(ErrorMessage.POINT_NOTATION_IP_NOT_FOUR_BLOCKS.toString());
        }
        int ipAddress = 0;

        for (int i = 0; i < byteArray.length; i++) {
            int eightBits = getBitsFromString(byteArray[i]);
            containsLeadingZeros(eightBits, byteArray[i]);
            isByteInRange(eightBits);
            ipAddress += eightBits << (BYTE_SIZE_TO_BIT * ((MAX_NUMBER_OF_BYTES - 1) - i));
        }
        return ipAddress;
    }

    private boolean isByteInRange(int eightBits) throws ParseException {

        if (eightBits < MIN_VALUE_ONE_BYTE || eightBits > MAX_VALUE_ONE_BYTE) {
            throw new ParseException(ErrorMessage.POINT_NOTATION_NOT_IN_RANGE.toString());
        }
        return true;
    }

    private int getBitsFromString(String str) throws ParseException {
        int eightBits = 0;
        try {
            eightBits = Integer.parseInt(str);

        } catch (NumberFormatException errorForNumberFormat) {
            throw new ParseException(ErrorMessage.POINT_NOTATION_CONTAINS_NON_NUMBERS.toString());
        }
        return eightBits;
    }

    private boolean containsLeadingZeros(int eightBits, String bitString) throws ParseException {
        String numberInString = eightBits + EMPTY_STRING;
        if (numberInString.length() != bitString.length()) {
            throw new ParseException(ErrorMessage.POINT_NOTATION_CONTAINS_LEADING_ZEROS.toString());
        }
        return true;
    }

    /**
     * Converts the @param bracketNotation into a valid tree topology and @param
     * bracketNotation must correspond to the specified format of the brackets
     * introduced in the assignment. Look into
     * {@link edu.kit.informatik.modelling.Network#Network(String)} for detailed
     * information about the pointNotation.
     * 
     * This method first gets all sub-trees in each opening '(' and closing ')'
     * bracket using the methods {@link #parseConnection() #parseIP() #parseTree()}
     * and adds it into {@link #listOfTrees} which is then merged into one tree
     * using the method {@link #combineTrees()}, which returns the end resulting
     * tree topology which is to be stored in
     * {@link edu.kit.informatik.modelling.structures.Grove}
     * 
     * @param bracketNotation Enclosed by an opening ( and a closing ) parenthesis,
     *                        the network nodes of a single tree topology with a
     *                        height of 1 are combined. A hierarchical structure is
     *                        achieved by allowing bracketed tree topologies
     *                        (subtrees) within bracketed tree topologies.
     * @return the tree which has been converted from @param bracketNotation.
     * @throws ParseException if constructing a tree is not possible or the format
     *                        is violated.
     */
    public Tree bracketNotationToTree(String bracketNotation) throws ParseException {
        if (bracketNotation == null) {
            throw new ParseException(ErrorMessage.BRACKET_NOTATION_NULL.toString());
        }
        this.numberOfOpeningBracketsTillNow = 0;
        this.numberOfClosingBracketsTillNow = 0;
        this.listOfTrees = new LinkedList<>();
        this.tokenizer = new StringTokenizer(bracketNotation);
        this.ipsFoundInBracketNotation = new TreeSet<>();

        if (!checkForSpaces(bracketNotation, tokenizer.countTokens())) {
            throw new ParseException(ErrorMessage.BRACKET_NOTATION_SPACE_ERROR.toString());
        }
        next();
        parseTree();

        if (this.listOfTrees.size() > 1) {
            try {
                combineTrees();
            } catch (IllegalArgumentException message) {
                throw new ParseException(message.getMessage());
            }
        }
        return this.listOfTrees.get(0);
    }

    /**
     * Adds all the tree in {@link #listOfTrees} in slot 1 to all the trees in the
     * other slots.
     * 
     * @return a Tree which is the result of merging all the trees in
     *         {@link #listOfTrees}.
     * @throws ParseException if a cycle occurs while adding tree topologies.
     */
    private Tree combineTrees() throws ParseException {

        Tree mainTree = this.listOfTrees.get(0);

        for (int i = 1; i < this.listOfTrees.size(); i++) {
            Tree treeToAdd = this.listOfTrees.get(i);
            Tree copyOfMainTree = mainTree.getCopyOfTree();

            boolean addSuccessful = mainTree.addGraph(treeToAdd);
            if (addSuccessful) {
                treeToAdd.setJoined(true);
                mainTree.setJoined(true);
                if (copyOfMainTree.equals(mainTree)) {
                    throw new ParseException(ErrorMessage.BRACKET_NOTATION_IP_DUPLICATE.toString());
                }
            } else {
                throw new ParseException(ErrorMessage.ERROR_SHOULD_NOT_OCCUR_BRACKET_NOTATION_ADDING_TREES.toString());
            }
        }
        return mainTree;
    }

    private boolean checkForSpaces(String bracketNotation, int tokenSize) {
        String[] splitArray = bracketNotation.split(SPACE);
        return (splitArray.length == tokenSize);
    }

    private void next() throws ParseException {
        if (this.tokenizer.hasMoreTokens()) {
            lookahead = tokenizer.nextToken();
        } else {
            throw new ParseException(ErrorMessage.BRACKET_NOTATION_NOT_ENOUGH_ELEMENTS.toString());
        }
    }

    /**
     * Parses a tree.
     * 
     * @return the root of this sub-tree.
     * @throws ParseException if the token doesn't have the format required to parse
     *                        a tree.
     */
    private IP parseTree() throws ParseException {

        if (lookahead.startsWith(OPEN_BRACKET)) {

            ++this.numberOfOpeningBracketsTillNow;
            lookahead = lookahead.substring(1);

            IP root = parseIP();

            List<IP> children = parseConnection();

            listOfTrees.add(0, new Tree(root, children));

            return root;
        } else {
            throw new ParseException(ErrorMessage.UNEXPECTED_TOKEN.toString());
        }
    }

    /**
     * Parses an IP.
     * 
     * @return an IP Object with the IPv4 address that has been parsed.
     * @throws ParseException if this IP occurs more than once in the
     *                        bracketNotation.
     */
    private IP parseIP() throws ParseException {
        IP ip = new IP(lookahead);
        boolean containedIPBeforeAdd = !this.ipsFoundInBracketNotation.add(ip);
        if (containedIPBeforeAdd) {
            throw new ParseException(ErrorMessage.BRACKET_NOTATION_IP_DUPLICATE.toString());
        }
        return ip;
    }

    /**
     * Parses a connection which could be a tree or an IP, depending if there is an
     * opening bracket '('.
     * 
     * @return the children list needed in
     *         {@link edu.kit.informatik.modelling.structures.Tree#Tree(IP, List)}.
     * @throws ParseException
     */
    private List<IP> parseConnection() throws ParseException {
        List<IP> connectionList = new LinkedList<>();
        int localisedOpenBrackets = numberOfExtraOpenBrackets();
        do {
            next();
            if (lookahead.startsWith(OPEN_BRACKET)) {
                IP root = parseTree();
                connectionList.add(root);

            } else {
                boolean hasClosingBracket = checkClosingBracketIP();
                IP ip = parseIP();
                connectionList.add(ip);

                if (hasClosingBracket) {
                    return connectionList;
                }
            }
        } while (parsingCanContinue(localisedOpenBrackets));
        return connectionList;
    }

    private boolean parsingCanContinue(int localisedOpenBrackets) throws ParseException {
        if (bracketsEndedButTokensLeft()) {
            throw new ParseException(ErrorMessage.BRACKET_NOTATION_TOO_MANY_ELEMENTS.toString());
        }
        if (tooManyClosingBrackets()) {
            throw new ParseException(ErrorMessage.BRACKET_NOTATION_BRACKETS.toString());
        }
        return (!areThereEqualNumberOfBothBrackets()) && (localisedOpenBrackets <= numberOfExtraOpenBrackets());
    }

    private boolean checkClosingBracketIP() throws ParseException {
        if (lookahead.endsWith(CLOSE_BRACKET)) {

            int numberOfClosingBrackets = numberOfClosingBrackets(lookahead);
            this.numberOfClosingBracketsTillNow += numberOfClosingBrackets;

            if (tooManyClosingBrackets() || bracketsEndedButTokensLeft()) {
                throw new ParseException(ErrorMessage.BRACKET_NOTATION_BRACKETS.toString());
            }
            lookahead = lookahead.substring(0, lookahead.length() - numberOfClosingBrackets);
            return true;
        }
        return false;
    }

    private boolean tooManyClosingBrackets() {
        return this.numberOfClosingBracketsTillNow > this.numberOfOpeningBracketsTillNow;
    }

    private int numberOfExtraOpenBrackets() {
        return this.numberOfOpeningBracketsTillNow - this.numberOfClosingBracketsTillNow;
    }

    private boolean areThereEqualNumberOfBothBrackets() {
        return this.numberOfClosingBracketsTillNow == this.numberOfOpeningBracketsTillNow;
    }

    private boolean bracketsEndedButTokensLeft() {
        return areThereEqualNumberOfBothBrackets() && tokenizer.hasMoreTokens();
    }

    /**
     * Gets the number of closing brackets ')' at the end of the token, which is why
     * a for-each isn't suitable.
     * 
     * @param str
     * @return
     */
    private int numberOfClosingBrackets(String str) {
        int matches = 0;
        for (int i = str.length() - 1; i >= 0; i--) {
            if (str.charAt(i) == CLOSE_BRACKET_AS_CHAR) {
                ++matches;
            } else {
                return matches;
            }
        }
        return matches;
    }
}
