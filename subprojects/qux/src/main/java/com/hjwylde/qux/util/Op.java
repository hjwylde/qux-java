package com.hjwylde.qux.util;

/**
 * Operator enumerations and arbitrary constants.
 *
 * @author Henry J. Wylde
 */
public interface Op {

    /**
     * Public access modifier flag.
     */
    int ACC_PUBLIC = 0x1;
    /**
     * Static access modifier flag.
     */
    int ACC_STATIC = 0x8;

    /**
     * Binary operators.
     *
     * @author Henry J. Wylde
     */
    public static enum Binary implements Op {
        // TODO: CONSIDER: You could add Access as a binary operator
        EQ, NEQ, LT, LTE, GT, GTE, AND, OR, XOR, IFF, IMPLIES, IN, RANGE, ADD, SUB, MUL, DIV, REM;
    }

    /**
     * Unary operators
     *
     * @author Henry J. Wylde
     */
    public static enum Unary implements Op {
        INC, LEN, NEG, NOT;
    }
}
