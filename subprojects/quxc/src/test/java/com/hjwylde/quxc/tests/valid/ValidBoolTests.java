package com.hjwylde.quxc.tests.valid;

import com.hjwylde.quxc.tests.Harness;

import org.junit.Test;

/**
 * Valid {@code bool} tests.
 *
 * @author Henry J. Wylde
 * @since 0.2.0
 */
public final class ValidBoolTests extends Harness {

    public ValidBoolTests() {
        super("tests/valid/bool_/");
    }

    @Test
    public void BoolAnd() {
        run("BoolAnd");
    }

    @Test
    public void BoolEq() {
        run("BoolEq");
    }

    @Test
    public void BoolGt() {
        run("BoolGt");
    }

    @Test
    public void BoolGte() {
        run("BoolGte");
    }

    @Test
    public void BoolIff() {
        run("BoolIff");
    }

    @Test
    public void BoolImplies() {
        run("BoolImplies");
    }

    @Test
    public void BoolLt() {
        run("BoolLt");
    }

    @Test
    public void BoolLte() {
        run("BoolLte");
    }

    @Test
    public void BoolNeq() {
        run("BoolNeq");
    }

    @Test
    public void BoolNot() {
        run("BoolNot");
    }

    @Test
    public void BoolOr() {
        run("BoolOr");
    }

    @Test
    public void BoolXor() {
        run("BoolXor");
    }
}

