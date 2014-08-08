package com.hjwylde.qbs.compiler;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.common.error.IllegalTimeUnitNameException;
import com.hjwylde.qbs.util.QuxProperties;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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

    private final long timeout;
    private final TimeUnit timeoutUnit;

    protected QuxCompileOptions(Builder builder) {
        checkArgument(builder.timeout >= 0, "timeout cannot be negative");

        this.verbose = builder.verbose;

        this.charset = checkNotNull(builder.charset, "charset cannot be null");

        this.timeout = builder.timeout;
        this.timeoutUnit = checkNotNull(builder.timeoutUnit, "timeoutUnit cannot be null");
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

    /**
     * Gets the character set.
     *
     * @return the character set.
     */
    public final Charset getCharset() {
        return charset;
    }

    /**
     * Gets the timeout length.
     *
     * @return the timeout length.
     */
    public final Long getTimeout() {
        return timeout;
    }

    /**
     * Gets the timeout unit.
     *
     * @return the timeout unit.
     */
    public final TimeUnit getTimeoutUnit() {
        return timeoutUnit;
    }

    /**
     * Gets the verbose option.
     *
     * @return the verbose option.
     */
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

        private long timeout;
        private TimeUnit timeoutUnit;

        /**
         * Creates a new {@code Builder} and initialises the verbose, character set, timeout and
         * timeout unit fields to the defaults as found in the {@link com.hjwylde.qbs.util.QuxProperties}.
         */
        protected Builder() {
            // Get the default properties
            QuxProperties properties = QuxProperties.loadDefaultProperties();

            setVerbose(properties.getVerbose());

            setCharset(properties.getCharset());

            setTimeout(properties.getTimeout());
            setTimeoutUnit(properties.getTimeoutUnit());
        }

        /**
         * Creates a new {@code Builder} and initialises the verbose, character set, timeout and
         * timeout unit fields to the options found in the given {@link QuxCompileOptions}.
         *
         * @param options the options to load the defaults from.
         */
        protected Builder(QuxCompileOptions options) {
            this.verbose = options.verbose;

            this.charset = options.charset;

            this.timeout = options.timeout;
            this.timeoutUnit = options.timeoutUnit;
        }

        /**
         * Builds the {@link QuxCompileOptions} from the current fields.
         *
         * @return a new {@link QuxCompileOptions}.
         */
        public QuxCompileOptions build() {
            return new QuxCompileOptions(this);
        }

        /**
         * Sets the character set field.
         *
         * @param charset the character set.
         * @return this for method chaining.
         * @throws IllegalCharsetNameException if the character set name is illegal.
         * @throws UnsupportedCharsetException if the character set is not supported.
         */
        public final Builder setCharset(String charset)
                throws IllegalCharsetNameException, UnsupportedCharsetException {
            if (charset == null) {
                this.charset = null;
                return this;
            }

            return setCharset(Charset.forName(charset));
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
         * Sets the timeout field.
         *
         * @param timeout the timeout.
         * @return this for method chaining.
         * @throws NumberFormatException if the timeout value is illegal.
         */
        public final Builder setTimeout(String timeout) throws NumberFormatException {
            try {
                return setTimeout(Long.valueOf(timeout));
            } catch (NumberFormatException e) {
                throw new NumberFormatException(timeout);
            }
        }

        /**
         * Sets the timeout field.
         *
         * @param timeout the timeout.
         * @return this for method chaining.
         */
        public final Builder setTimeout(long timeout) {
            this.timeout = timeout;
            return this;
        }

        /**
         * Sets the timeout unit field.
         *
         * @param timeoutUnit the timeout unit.
         * @return this for method chaining.
         * @throws IllegalTimeUnitNameException if the timeout unit name is illegal.
         */
        public final Builder setTimeoutUnit(String timeoutUnit)
                throws IllegalTimeUnitNameException {
            if (timeoutUnit == null) {
                this.timeoutUnit = null;
                return this;
            }

            try {
                return setTimeoutUnit(TimeUnit.valueOf(timeoutUnit.toUpperCase(Locale.ENGLISH)));
            } catch (IllegalArgumentException e) {
                throw new IllegalTimeUnitNameException(timeoutUnit);
            }
        }

        /**
         * Sets the timeout unit field.
         *
         * @param timeoutUnit the timeout unit.
         * @return this for method chaining.
         */
        public final Builder setTimeoutUnit(TimeUnit timeoutUnit) {
            this.timeoutUnit = timeoutUnit;
            return this;
        }

        /**
         * Sets the verbose field.
         *
         * @param verbose the verbose field.
         * @return this for method chaining.
         */
        public final Builder setVerbose(String verbose) {
            return setVerbose(Boolean.valueOf(verbose));
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
