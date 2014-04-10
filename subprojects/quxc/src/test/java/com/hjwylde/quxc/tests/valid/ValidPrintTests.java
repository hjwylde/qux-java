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
    public void PrintBool1() {
        run("PrintBool1");
    }

    @Test
    public void PrintBool2() {
        run("PrintBool2");
    }

    @Test
    public void PrintInt1() {
        run("PrintInt1");
    }

    @Test
    public void PrintList1() {
        run("PrintList1");
    }

    @Test
    public void PrintList2() {
        run("PrintList2");
    }

    @Test
    public void PrintList3() {
        run("PrintList3");
    }

    @Test
    public void PrintNull1() {
        run("PrintNull1");
    }

    @Test
    public void PrintReal1() {
        run("PrintReal1");
    }

    @Test
    public void PrintStr1() {
        run("PrintStr1");
    }
}
