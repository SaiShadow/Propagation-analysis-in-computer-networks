package edu.kit.informatik.modelling;

import edu.kit.informatik.exceptions.ParseException;
import edu.kit.informatik.parser.Parser;
import edu.kit.informatik.resources.ErrorMessage;

/**
 * The class models IPv4 addresses and sets an order for them. This class
 * contains the pointNotation and the IPv4 in a int representation(similar to
 * the RGBA conversion done in Assignment 4A).
 * 
 * @author uogok
 * @version 3.4
 */
public class IP implements Comparable<IP> {

    private static final int MAX_NUMBER_OF_BYTES = 4;
    private static final int BYTE_SIZE_TO_BIT = 8;
    private static final int VALUE_BYTE_AT_RIGHT = 0xFF;

    private final int ip;
    private final String pointNotation;

    /**
     * The constructor takes a textual representation of an IPv4 address and
     * instantiates a new object for that address. The character string passed must
     * correspond to the format of the decimal point notation. If the check is not
     * possible or the format is violated, a ParseException is thrown in the
     * constructor.
     * 
     * @param pointNotation The character string passed must correspond to the
     *                      format of the decimal point notation.
     * @throws ParseException if the pointNotation is not in format of the decimal
     *                        point notation or check is not possible then a
     *                        ParseException is thrown in the constructor.
     */
    public IP(final String pointNotation) throws ParseException {
        if (pointNotation == null || pointNotation.isEmpty()) {
            throw new ParseException(ErrorMessage.POINT_NOTATION_NULL.toString());
        }
        Parser parser = new Parser();
        this.ip = parser.pointNotationToInt(pointNotation);
        this.pointNotation = pointNotation;
    }

    /**
     * Returns the IPv4 address {@link #pointNotation}.
     * 
     * @return the IPv4 address in point notation for example '0.0.0.0'.
     */
    public String getPointNotation() {
        return this.pointNotation;
    }

    /**
     * Returns the int representation of the IPv4 address, as you can store the IPv4
     * values into 32-bit (similar to the RGBA conversion done in Assignment 4A).
     * {@link #ip}
     * 
     * @return the int representation of the IPv4 address.
     */
    public int getIP() {
        return this.ip;
    }

    @Override
    public String toString() {
        return getPointNotation();
    }

    @Override
    public int compareTo(IP o) {

        if (o == null) {
            throw new IllegalArgumentException(ErrorMessage.IP_NULL.toString());
        }

        int currentIP = getIP();
        int ipToCompare = o.getIP();

        if (currentIP == ipToCompare) {
            return 0;
        }

        for (int i = MAX_NUMBER_OF_BYTES - 1; i >= 0; i--) {

            int currentIPBitAtI = (currentIP >>> (BYTE_SIZE_TO_BIT * i)) & VALUE_BYTE_AT_RIGHT;
            int ipToCompareBitAtI = (ipToCompare >>> (BYTE_SIZE_TO_BIT * i)) & VALUE_BYTE_AT_RIGHT;

            if (currentIPBitAtI > ipToCompareBitAtI) {
                return 1;
            } else if (currentIPBitAtI < ipToCompareBitAtI) {
                return -1;
            }
        }

        return 0;
    }

    @Override
    public boolean equals(Object other) {

        if (this == other) {
            return true;
        }

        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        return compareTo((IP) other) == 0;
    }

    /**
     * Gets a deepCopy of the current IP i.e. return a new IP Object with the same
     * Point notation and int value.
     * 
     * @return a new IP object with same internal values.
     */
    public IP deepCopy() {
        IP copy = null;
        try {
            copy = new IP(this.getPointNotation());
        } catch (ParseException e) {
            System.out.println(ErrorMessage.ERROR_SHOULD_NOT_OCCUR_IP_DEEP_COPY.toString());
        }
        return copy;
    }
}
