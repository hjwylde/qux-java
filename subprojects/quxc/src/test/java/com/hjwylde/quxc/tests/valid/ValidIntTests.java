package com.hjwylde.quxc.tests.valid;

import com.hjwylde.quxc.tests.Harness;

import org.junit.Test;

/**
 * Valid {@code int} tests.
 *
 * @author Henry J. Wylde
 * @since 0.1.2
 */
public final class ValidIntTests extends Harness {

    public ValidIntTests() {
        super("tests/valid/int_/");
    }

    @Test
    public void IntAdd() {
        run("IntAdd");
    }

    @Test
    public void IntDec() {
        run("IntDec");
    }

    @Test
    public void IntDiv() {
        run("IntDiv");
    }

    @Test
    public void IntEq() {
        run("IntEq");
    }

    @Test
    public void IntExp() {
        run("IntExp");
    }

    @Test
    public void IntGt() {
        run("IntGt");
    }

    @Test
    public void IntGte() {
        run("IntGte");
    }

    @Test
    public void IntIDiv() {
        run("IntIDiv");
    }

    @Test
    public void IntInc() {
        run("IntInc");
    }

    @Test
    public void IntLt() {
        run("IntLt");
    }

    @Test
    public void IntLte() {
        run("IntLte");
    }

    @Test
    public void IntMul() {
        run("IntMul");
    }

    @Test
    public void IntNeg() {
        run("IntNeg");
    }

    @Test
    public void IntNeq() {
        run("IntNeq");
    }

    @Test
    public void IntRange() {
        run("IntRange");
    }

    @Test
    public void IntRem() {
        run("IntRem");
    }

    @Test
    public void IntSub() {
        run("IntSub");
    }
}

