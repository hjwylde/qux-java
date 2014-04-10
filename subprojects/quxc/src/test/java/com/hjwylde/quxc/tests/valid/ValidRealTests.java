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
    public void RealAdd1() {
        run("RealAdd1");
    }

    @Test
    public void RealAdd2() {
        run("RealAdd2");
    }

    @Test
    public void RealDiv1() {
        run("RealDiv1");
    }

    @Test
    public void RealEq1() {
        run("RealEq1");
    }

    @Test
    public void RealEq2() {
        run("RealEq2");
    }

    @Test
    public void RealGt1() {
        run("RealGt1");
    }

    @Test
    public void RealGt2() {
        run("RealGt2");
    }

    @Test
    public void RealGte1() {
        run("RealGte1");
    }

    @Test
    public void RealGte2() {
        run("RealGte2");
    }

    @Test
    public void RealLt1() {
        run("RealLt1");
    }

    @Test
    public void RealLt2() {
        run("RealLt2");
    }

    @Test
    public void RealLte1() {
        run("RealLte1");
    }

    @Test
    public void RealLte2() {
        run("RealLte2");
    }

    @Test
    public void RealMul1() {
        run("RealMul1");
    }

    @Test
    public void RealMul2() {
        run("RealMul2");
    }

    @Test
    public void RealNeg1() {
        run("RealNeg1");
    }

    @Test
    public void RealNeq1() {
        run("RealNeq1");
    }

    @Test
    public void RealNeq2() {
        run("RealNeq2");
    }

    @Test
    public void RealSub1() {
        run("RealSub1");
    }

    @Test
    public void RealSub2() {
        run("RealSub2");
    }
}

