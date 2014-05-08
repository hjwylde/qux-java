package com.hjwylde.quxc.tests.valid;

import com.hjwylde.quxc.tests.Harness;

import org.junit.Test;

/**
 * Valid {@code null} tests.
 *
 * @author Henry J. Wylde
 * @since 0.1.2
 */
public final class ValidNullTests extends Harness {

    public ValidNullTests() {
        super("tests/valid/null_/");
    }

    @Test
    public void NullEq() {
        run("NullEq");
    }

    @Test
    public void NullFunction() {
        run("NullFunction");
    }

    @Test
    public void NullNeq() {
        run("NullNeq");
    }
}

