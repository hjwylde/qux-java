package com.hjwylde.common.error;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

/**
 * Represents multiple build errors. This class may be used to combine multiple build errors for
 * return to give the user a better idea about all of the problems during a build.
 *
 * @author Henry J. Wylde
 */
public class BuildErrorList extends BuildError {

    /**
     * The list of build errors.
     */
    protected final ImmutableList<BuildError> errors;

    /**
     * Creates a new {@code BuildErrorList} with the given errors.
     *
     * @param errors the errors.
     */
    public BuildErrorList(Collection<? extends BuildError> errors) {
        super("build error list");

        this.errors = ImmutableList.copyOf(errors);
    }

    /**
     * Creates a new {@code BuildErrorList} with the given errors.
     *
     * @param errors the errors.
     */
    public BuildErrorList(BuildError... errors) {
        super("build error list");

        this.errors = ImmutableList.copyOf(errors);
    }

    /**
     * Gets the build errors.
     *
     * @return the errors.
     */
    public ImmutableList<BuildError> getErrors() {
        return errors;
    }
}
