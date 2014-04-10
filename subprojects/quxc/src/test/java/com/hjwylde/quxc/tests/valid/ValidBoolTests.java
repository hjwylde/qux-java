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
    public void BoolEq1() {
        run("BoolEq1");
    }

    @Test
    public void BoolEq2() {
        run("BoolEq2");
    }

    @Test
    public void BoolGt1() {
        run("BoolGt1");
    }

    @Test
    public void BoolGt2() {
        run("BoolGt2");
    }

    @Test
    public void BoolGte1() {
        run("BoolGte1");
    }

    @Test
    public void BoolGte2() {
        run("BoolGte2");
    }

    @Test
    public void BoolLt1() {
        run("BoolLt1");
    }

    @Test
    public void BoolLt2() {
        run("BoolLt2");
    }

    @Test
    public void BoolLte1() {
        run("BoolLte1");
    }

    @Test
    public void BoolLte2() {
        run("BoolLte2");
    }

    @Test
    public void BoolNeq1() {
        run("BoolNeq1");
    }

    @Test
    public void BoolNeq2() {
        run("BoolNeq2");
    }
}

