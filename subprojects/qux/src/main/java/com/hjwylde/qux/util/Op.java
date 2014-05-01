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
        EQ, NEQ, LT, LTE, GT, GTE, ADD, SUB, MUL, DIV, REM;
    }

    /**
     * Unary operators
     *
     * @author Henry J. Wylde
     */
    public static enum Unary implements Op {
        NEG, NOT;
    }
}
