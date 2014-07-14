package com.hjwylde.qbs.builder.resources;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

import java.nio.file.Path;
import java.util.Iterator;

import javax.annotation.Nullable;

/**
 * A directory resource is an inclusion of all resources that may be located from a root directory.
 * The resources in the iterator of this directory are lazily initialised. That is, they will only
 * be read from the file system when needed.
 * <p/>
 * This directory resource only includes resources that match its filter.
 *
 * @author Henry J. Wylde
 * @since 0.2.4
 */
public class FilteredDirectoryResource extends DirectoryResource {

    private final Predicate<? super Resource> filter;

    /**
     * Creates a new filtered directory resource with the given root directory and filter.
     *
     * @param root the root directory.
     * @param filter the filter.
     */
    public FilteredDirectoryResource(Path root, Predicate<? super Resource> filter) {
        super(root);

        this.filter = checkNotNull(filter, "filter cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }

        return filter.equals(((FilteredDirectoryResource) obj).filter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Resource> iterator() {
        return Iterators.filter(super.iterator(), filter);
    }
}
