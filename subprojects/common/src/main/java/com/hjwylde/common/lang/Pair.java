package com.hjwylde.common.lang;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import javax.annotation.Nullable;

/**
 * A pair is an immutable tuple of 2 elements. The elements may have different types.
 *
 * @param <F> the type of the first element.
 * @param <S> the type of the second element.
 * @author Henry J. Wylde
 */
public class Pair<F, S> {

    /**
     * The first element.
     */
    protected final F first;
    /**
     * The second element.
     */
    protected final S second;

    /**
     * Creates a new ordered pair of the given two elements.
     *
     * @param first the first element.
     * @param second the second element.
     */
    public Pair(F first, S second) {
        this.first = checkNotNull(first, "first cannot be null");
        this.second = checkNotNull(second, "second cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Pair<?, ?> pair = (Pair<?, ?>) obj;

        return first.equals(pair.first) && second.equals(pair.second);
    }

    /**
     * Gets the first element.
     *
     * @return the first element.
     */
    public F getFirst() {
        return first;
    }

    /**
     * Gets the second element.
     *
     * @return the second element.
     */
    public S getSecond() {
        return second;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}

