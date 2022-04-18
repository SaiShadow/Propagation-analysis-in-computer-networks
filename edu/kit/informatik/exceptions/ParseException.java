package edu.kit.informatik.exceptions;

/**
 * This specially defined exception signals that an unexpected error occurred
 * while parsing the textual bracket notation or decimal point notation. This
 * class represents the exception that is thrown when semantic and syntactic
 * errors occur while parsing. Is thrown in
 * {@link edu.kit.informatik.modelling.IP#IP(String)} and in
 * {@link edu.kit.informatik.modelling.Network#Network(String)}.
 * 
 * @author uogok
 * @version 2
 */
public class ParseException extends Exception {

    private static final long serialVersionUID = -1023986737949218483L;

    /**
     * Constructor for the ParseException.
     * 
     * @param message The error message thrown while instantiating a new
     *                ParseException.
     */
    public ParseException(String message) {
        super(message);
    }
}
