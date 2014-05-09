package com.hjwylde.quxc.tests.valid;

import com.hjwylde.quxc.tests.Harness;

import org.junit.Test;

/**
 * Valid {@code list} tests.
 *
 * @author Henry J. Wylde
 * @since 0.1.2
 */
public final class ValidSetTests extends Harness {

    public ValidSetTests() {
        super("tests/valid/set_/");
    }

    @Test
    public void SetAccess() {
        run("SetAccess");
    }

    @Test
    public void SetAdd() {
        run("SetAdd");
    }

    @Test
    public void SetEq() {
        run("SetEq");
    }

    @Test
    public void SetLen() {
        run("SetLen");
    }

    @Test
    public void SetLoop() {
        run("SetLoop");
    }

    @Test
    public void SetNeq() {
        run("SetNeq");
    }

    @Test
    public void SetSlice() {
        run("SetSlice");
    }

    @Test
    public void SetSub() {
        run("SetSub");
    }
}

