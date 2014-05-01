package com.hjwylde.quxc.tests.valid;

import com.hjwylde.quxc.tests.Harness;

import org.junit.Test;

/**
 * Valid {@code str} tests.
 *
 * @author Henry J. Wylde
 * @since 0.2.0
 */
public final class ValidStrTests extends Harness {

    public ValidStrTests() {
        super("tests/valid/str_/");
    }

    @Test
    public void StrAdd1() {
        run("StrAdd1");
    }

    @Test
    public void StrAdd2() {
        run("StrAdd2");
    }

    @Test
    public void StrEq1() {
        run("StrEq1");
    }

    @Test
    public void StrEq2() {
        run("StrEq2");
    }

    @Test
    public void StrGt1() {
        run("StrGt1");
    }

    @Test
    public void StrGt2() {
        run("StrGt2");
    }

    @Test
    public void StrGte1() {
        run("StrGte1");
    }

    @Test
    public void StrGte2() {
        run("StrGte2");
    }

    @Test
    public void StrLt1() {
        run("StrLt1");
    }

    @Test
    public void StrLt2() {
        run("StrLt2");
    }

    @Test
    public void StrLte1() {
        run("StrLte1");
    }

    @Test
    public void StrLte2() {
        run("StrLte2");
    }

    @Test
    public void StrMul1() {
        run("StrMul1");
    }

    @Test
    public void StrMul2() {
        run("StrMul2");
    }

    @Test
    public void StrNeq1() {
        run("StrNeq1");
    }

    @Test
    public void StrNeq2() {
        run("StrNeq2");
    }

    @Test
    public void StrSub1() {
        run("StrSub1");
    }

    @Test
    public void StrSub2() {
        run("StrSub2");
    }
}

