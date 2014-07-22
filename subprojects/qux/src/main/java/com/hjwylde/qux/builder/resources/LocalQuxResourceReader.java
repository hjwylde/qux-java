package com.hjwylde.qux.builder.resources;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.hjwylde.qbs.builder.resources.Resource;
import com.hjwylde.qux.api.QuxReader;
import com.hjwylde.qux.tree.QuxNode;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since 0.2.1
 */
public final class LocalQuxResourceReader implements Resource.Reader<QuxResource> {

    private final Charset charset;

    public LocalQuxResourceReader(Charset charset) {
        this.charset = checkNotNull(charset, "charset cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuxResource read(InputStream in) throws IOException {
        QuxNode node = new QuxNode();
        new QuxReader(in, charset).accept(node);

        return new QuxResource(node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuxResource read(Path path) throws IOException {
        QuxNode node = new QuxNode();
        new QuxReader(path, charset).accept(node);

        return new QuxResource(node);
    }
}
