package com.hjwylde.quxc.tests.valid;

import com.hjwylde.quxc.tests.Harness;

import org.junit.Test;

/**
 * Valid function tests.
 *
 * @author Henry J. Wylde
 * @since 0.2.0
 */
public final class ValidFunctionTests extends Harness {

    public ValidFunctionTests() {
        super("tests/valid/function/");
    }

    @Test
    public void Average() {
        run("Average");
    }

    @Test
    public void Fibonacci() {
        run("Fibonacci");
    }

    @Test
    public void Gcd() {
        run("Gcd");
    }
}
