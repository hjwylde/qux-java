package com.hjwylde.qbs.builder;

/**
 * A context specific to a {@code qux} compilation process. This context uses a {@link
 * com.hjwylde.qbs.builder.QuxProject} rather than a standard one.
 *
 * @author Henry J. Wylde
 * @since 0.1.3
 */
public class QuxContext extends Context {

    /**
     * Creates a new {@code QuxContext} with the given project.
     *
     * @param project the project.
     */
    public QuxContext(QuxProject project) {
        super(project);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final QuxProject getProject() {
        return (QuxProject) super.getProject();
    }
}
