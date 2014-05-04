package com.hjwylde.quxc.tests.valid;

import com.hjwylde.quxc.tests.Harness;

import org.junit.Test;

/**
 * Valid {@code list} tests.
 *
 * @author Henry J. Wylde
 * @since 0.1.2
 */
public final class ValidListTests extends Harness {

    public ValidListTests() {
        super("tests/valid/list_/");
    }

    @Test
    public void ListAccess() {
        run("ListAccess");
    }

    @Test
    public void ListAdd() {
        run("ListAdd");
    }

    @Test
    public void ListAssign() {
        run("ListAssign");
    }

    @Test
    public void ListEq() {
        run("ListEq");
    }

    @Test
    public void ListLen() {
        run("ListLen");
    }

    @Test
    public void ListLoop() {
        run("ListLoop");
    }

    @Test
    public void ListMul() {
        run("ListMul");
    }

    @Test
    public void ListNeq() {
        run("ListNeq");
    }

    @Test
    public void ListSub() {
        run("ListSub");
    }
}

