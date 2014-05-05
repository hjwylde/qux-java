package com.hjwylde.qbs.builder;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.qbs.builder.resources.Resource;
import com.hjwylde.qbs.builder.resources.ResourceSet;

import com.google.common.base.Optional;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;

/**
 * A context is a wrapper for a project that saves related resources during compilation. These
 * resources are used at later stages during compilation for checks such as name resolution and type
 * checking.
 *
 * @author Henry J. Wylde
 */
public class Context {

    private final Project project;

    private final Set<Resource> resources = Collections.synchronizedSet(new HashSet<Resource>());

    /**
     * Creates a new <code>Context</code> for the given project.
     *
     * @param project the project for this context.
     */
    public Context(Project project) {
        this.project = checkNotNull(project, "project cannot be null");
    }

    /**
     * Adds the given resources to this context.
     *
     * @param resources the resources.
     */
    public void addResources(Collection<? extends Resource> resources) {
        this.resources.add(new ResourceSet(resources));
    }

    /**
     * Adds the given resources to this context.
     *
     * @param resources the resources to add.
     */
    public void addResources(Resource... resources) {
        this.resources.add(new ResourceSet(resources));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Context)) {
            return false;
        }

        Context context = (Context) obj;

        return Objects.equals(project, context.project) && Objects.equals(resources,
                context.resources);
    }

    /**
     * Gets the project this context is for.
     *
     * @return the project.
     */
    public Project getProject() {
        return project;
    }

    /**
     * Gets the resource with the given identifier.
     *
     * @param id the resource identifier.
     * @return the resource with the given identifier or null.
     */
    public Optional<Resource.Single> getResourceById(String id) {
        for (Resource resource : resources) {
            if (resource.containsId(id)) {
                return resource.getById(id);
            }
        }

        return Optional.absent();
    }

    /**
     * Gets the resources this context contains.
     *
     * @return an immutable view of the resources.
     */
    public Set<Resource> getResources() {
        return Collections.unmodifiableSet(resources);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(project, resources);
    }
}
