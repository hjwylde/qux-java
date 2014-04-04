package com.hjwylde.quxc.compiler;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.qbs.compiler.CompileOptions;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 */
public class QuxCompileOptions extends CompileOptions {

    public static final QuxCompileOptions DEFAULT_OPTIONS = builder().build();

    private static final long serialVersionUID = 1L;

    private final boolean verbose;

    private final Charset charset;

    protected QuxCompileOptions(Builder builder) {
        this.verbose = builder.verbose;

        this.charset = checkNotNull(builder.charset, "charset cannot be null");
    }

    /**
     * Gets a new builder.
     *
     * @return the builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Gets a new builder, initialising it with the given compile options.
     *
     * @param options the options.
     * @return the initialised builder.
     */
    public static Builder builder(QuxCompileOptions options) {
        return new Builder(options);
    }

    public final Charset getCharset() {
        return charset;
    }

    public final boolean isVerbose() {
        return verbose;
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     */
    public static class Builder {

        private boolean verbose;

        private Charset charset = StandardCharsets.UTF_8;

        protected Builder() {}

        protected Builder(QuxCompileOptions options) {
            this.verbose = options.verbose;

            this.charset = options.charset;
        }

        public QuxCompileOptions build() {
            return new QuxCompileOptions(this);
        }

        public final Builder setCharset(Charset charset) {
            this.charset = checkNotNull(charset, "charset cannot be null");
            return this;
        }

        public final Builder setVerbose(boolean verbose) {
            this.verbose = verbose;
            return this;
        }
    }
}
