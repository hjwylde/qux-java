package com.hjwylde.quxc.tests.valid;

import com.hjwylde.quxc.tests.Harness;

import org.junit.Test;

/**
 * Valid {@code list} tests.
 *
 * @author Henry J. Wylde
 * @since 0.2.0
 */
public final class ValidListTests extends Harness {

    public ValidListTests() {
        super("tests/valid/list_/");
    }

    @Test
    public void ListAdd1() {
        run("ListAdd1");
    }

    @Test
    public void ListAdd2() {
        run("ListAdd2");
    }

    @Test
    public void ListEq1() {
        run("ListEq1");
    }

    @Test
    public void ListEq2() {
        run("ListEq2");
    }

    @Test
    public void ListEq3() {
        run("ListEq3");
    }

    @Test
    public void ListMul1() {
        run("ListMul1");
    }

    @Test
    public void ListMul2() {
        run("ListMul2");
    }

    @Test
    public void ListNeq1() {
        run("ListNeq1");
    }

    @Test
    public void ListNeq2() {
        run("ListNeq2");
    }

    @Test
    public void ListSub1() {
        run("ListSub1");
    }

    @Test
    public void ListSub2() {
        run("ListSub2");
    }
}

