package com.hjwylde.quxc.compiler;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.qbs.compiler.CompileOptions;
import com.hjwylde.quxc.util.QuxcProperties;

import java.nio.charset.Charset;

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

        private Charset charset;

        /**
         * Creates a new {@code Builder} and initialises the verbose and character set fields to the
         * defaults as found in the {@link com.hjwylde.quxc.util.QuxcProperties}.
         */
        protected Builder() {
            // Get the default properties
            QuxcProperties properties = QuxcProperties.loadDefaultProperties();

            verbose = Boolean.valueOf(properties.getVerbose());

            charset = Charset.forName(properties.getCharset());
        }

        /**
         * Creates a new {@code Builder} and initialises the verbose and character set fields to the
         * options found in the given {@link com.hjwylde.quxc.compiler.QuxCompileOptions}.
         *
         * @param options the options to load the defaults from.
         */
        protected Builder(QuxCompileOptions options) {
            this.verbose = options.verbose;

            this.charset = options.charset;
        }

        /**
         * Builds the {@link com.hjwylde.quxc.compiler.QuxCompileOptions} from the current fields.
         *
         * @return a new {@link com.hjwylde.quxc.compiler.QuxCompileOptions}.
         */
        public QuxCompileOptions build() {
            return new QuxCompileOptions(this);
        }

        /**
         * Sets the character set field.
         *
         * @param charset the character set.
         * @return this for method chaining.
         */
        public final Builder setCharset(Charset charset) {
            this.charset = checkNotNull(charset, "charset cannot be null");
            return this;
        }

        /**
         * Sets the verbose field.
         *
         * @param verbose the verbose field.
         * @return this for method chaining.
         */
        public final Builder setVerbose(boolean verbose) {
            this.verbose = verbose;
            return this;
        }
    }
}
