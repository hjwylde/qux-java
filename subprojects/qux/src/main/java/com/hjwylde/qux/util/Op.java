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
     * Private access modifier flag.
     */
    int ACC_PRIVATE = 0x2;
    /**
     * Protected access modifier flag.
     */
    int ACC_PROTECTED = 0x4;
    /**
     * Static access modifier flag.
     */
    int ACC_STATIC = 0x8;
    /**
     * Final access modifier flag.
     */
    int ACC_FINAL = 0x10;

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
        LEN, NEG, NOT;
    }
}
