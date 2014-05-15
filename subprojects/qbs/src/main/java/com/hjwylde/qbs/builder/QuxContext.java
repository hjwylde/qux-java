package com.hjwylde.qbs.builder;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since 0.1.3
 */
public class QuxContext extends Context {

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
