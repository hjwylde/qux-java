package com.hjwylde.quxc.tests.valid;

import com.hjwylde.quxc.tests.Harness;

import org.junit.Test;

/**
 * Valid {@code real} tests.
 *
 * @author Henry J. Wylde
 * @since 0.2.0
 */
public final class ValidRealTests extends Harness {

    public ValidRealTests() {
        super("tests/valid/real_/");
    }

    @Test
    public void RealAdd() {
        run("RealAdd");
    }

    @Test
    public void RealDiv() {
        run("RealDiv");
    }

    @Test
    public void RealEq() {
        run("RealEq");
    }

    @Test
    public void RealGt() {
        run("RealGt");
    }

    @Test
    public void RealGte() {
        run("RealGte");
    }

    @Test
    public void RealLt() {
        run("RealLt");
    }

    @Test
    public void RealLte() {
        run("RealLte");
    }

    @Test
    public void RealMul() {
        run("RealMul");
    }

    @Test
    public void RealNeg() {
        run("RealNeg");
    }

    @Test
    public void RealNeq() {
        run("RealNeq");
    }

    @Test
    public void RealSub() {
        run("RealSub");
    }
}

