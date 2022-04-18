package edu.kit.informatik.exceptions;

/**
 * This specially defined exception signals that an cyclic graph occurred(occurs
 * mostly while adding networks
 * {@link edu.kit.informatik.modelling.Network#add(edu.kit.informatik.modelling.Network)}).
 * 
 * @author uogok
 * @version 2.1
 */
public class CycleException extends IllegalArgumentException {

    private static final long serialVersionUID = 6095906026752183L;

    /**
     * Constructor for the CycleException Class.
     * 
     * @param message is the error message that is given while throwing this
     *                exception.
     */
    public CycleException(String message) {
        super(message);
    }

}
