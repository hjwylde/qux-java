package com.hjwylde.quxc.tests.valid;

import com.hjwylde.quxc.tests.Harness;

import org.junit.Test;

/**
 * Valid {@code Print} tests.
 *
 * @author Henry J. Wylde
 * @since 0.2.0
 */
public final class ValidPrintTests extends Harness {

    public ValidPrintTests() {
        super("tests/valid/print_/");
    }

    @Test
    public void PrintBool() {
        run("PrintBool");
    }

    @Test
    public void PrintInt() {
        run("PrintInt");
    }

    @Test
    public void PrintList() {
        run("PrintList");
    }

    @Test
    public void PrintNull() {
        run("PrintNull");
    }

    @Test
    public void PrintReal() {
        run("PrintReal");
    }

    @Test
    public void PrintStr() {
        run("PrintStr");
    }
}
