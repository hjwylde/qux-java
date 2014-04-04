package com.hjwylde.common.error;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

/**
 * Represents multiple compiler errors. This class may be used to combine multiple compiler errors
 * for return to give the user a better idea about all of the problems during compilation.
 *
 * @author Henry J. Wylde
 */
public class CompilerErrorList extends CompilerError {

    /**
     * The list of compiler errors.
     */
    protected final ImmutableList<CompilerError> errors;

    /**
     * Creates a new {@code CompilerErrorList} with the given errors.
     *
     * @param errors the errors.
     */
    public CompilerErrorList(Collection<? extends CompilerError> errors) {
        super("compiler error list");

        this.errors = ImmutableList.copyOf(errors);
    }

    /**
     * Creates a new {@code CompilerErrorList} with the given errors.
     *
     * @param errors the errors.
     */
    public CompilerErrorList(CompilerError... errors) {
        super("compiler error list");

        this.errors = ImmutableList.copyOf(errors);
    }

    /**
     * Gets the compiler errors.
     *
     * @return the errors.
     */
    public ImmutableList<CompilerError> getErrors() {
        return errors;
    }
}
