package com.hjwylde.qux.util;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public interface Op {

    int ACC_PUBLIC = 0x1;
    int ACC_PRIVATE = 0x2;
    int ACC_PROTECTED = 0x4;
    int ACC_STATIC = 0x8;
    int ACC_FINAL = 0x10;

    public static enum Binary implements Op {
        EQ, NEQ, LT, LTE, GT, GTE, ADD, SUB, MUL, DIV;
    }

    public static enum Unary implements Op {
        NEG, NOT;
    }
}
