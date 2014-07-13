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
     * Final access modifier flag.
     */
    int ACC_FINAL = 0x10;

    /**
     * Binary operators.
     *
     * @author Henry J. Wylde
     */
    public static enum Binary implements Op {
        EQ, NEQ, LT, LTE, GT, GTE, AND, OR, XOR, IFF, IMP, IN, ACC, RNG, EXP, ADD, SUB, MUL, DIV,
        IDIV, REM;
    }

    /**
     * Unary operators
     *
     * @author Henry J. Wylde
     */
    public static enum Unary implements Op {
        DEC, INC, LEN, NEG, NOT;
    }
}
