package com.hjwylde.quxc.tests.valid;

import com.hjwylde.quxc.tests.Harness;

import org.junit.Test;

/**
 * Valid {@code str} tests.
 *
 * @author Henry J. Wylde
 * @since 0.1.2
 */
public final class ValidStrTests extends Harness {

    public ValidStrTests() {
        super("tests/valid/str_/");
    }

    @Test
    public void StrAccess() {
        run("StrAccess");
    }

    @Test
    public void StrAdd() {
        run("StrAdd");
    }

    @Test
    public void StrAssign() {
        run("StrAssign");
    }

    @Test
    public void StrEq() {
        run("StrEq");
    }

    @Test
    public void StrGt() {
        run("StrGt");
    }

    @Test
    public void StrGte() {
        run("StrGte");
    }

    @Test
    public void StrLt() {
        run("StrLt");
    }

    @Test
    public void StrLte() {
        run("StrLte");
    }

    @Test
    public void StrMul() {
        run("StrMul");
    }

    @Test
    public void StrNeq() {
        run("StrNeq");
    }

    @Test
    public void StrSub() {
        run("StrSub");
    }
}

