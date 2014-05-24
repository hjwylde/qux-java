package com.hjwylde.quxc.compiler;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.common.error.IllegalTargetNameException;
import com.hjwylde.qbs.compiler.QuxCompileOptions;
import com.hjwylde.quxc.util.QuxcProperties;
import com.hjwylde.quxc.util.Target;

import java.util.Locale;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since 0.2.1
 */
public class QuxcCompileOptions extends QuxCompileOptions {

    public static final QuxcCompileOptions DEFAULT_OPTIONS = builder().build();

    private static final long serialVersionUID = 1L;

    private final Target target;

    protected QuxcCompileOptions(Builder builder) {
        super(builder);

        this.target = checkNotNull(builder.target, "target cannot be null");
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
    public static Builder builder(QuxcCompileOptions options) {
        return new Builder(options);
    }

    public final Target getTarget() {
        return target;
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     * @since 0.2.1
     */
    public static class Builder extends QuxCompileOptions.Builder {

        private Target target;

        /**
         * Creates a new {@code Builder} and initialises the target field to the defaults as found
         * in the {@link com.hjwylde.quxc.util.QuxcProperties}.
         */
        protected Builder() {
            // Get the default properties
            QuxcProperties properties = QuxcProperties.loadDefaultProperties();

            setTarget(properties.getTarget());
        }

        /**
         * Creates a new {@code Builder} and initialises the target field to the options found in
         * the given {@link com.hjwylde.quxc.compiler.QuxcCompileOptions}.
         *
         * @param options the options to load the defaults from.
         */
        protected Builder(QuxcCompileOptions options) {
            super(options);

            this.target = options.target;
        }

        /**
         * Builds the {@link com.hjwylde.quxc.compiler.QuxcCompileOptions} from the current fields.
         *
         * @return a new {@link com.hjwylde.quxc.compiler.QuxcCompileOptions}.
         */
        public QuxcCompileOptions build() {
            return new QuxcCompileOptions(this);
        }

        /**
         * Sets the target field.
         *
         * @param target the target.
         * @return this for method chaining.
         * @throws com.hjwylde.common.error.IllegalTargetNameException if the target name is
         * illegal.
         */
        public final Builder setTarget(String target) throws IllegalTargetNameException {
            if (target == null) {
                this.target = null;
                return this;
            }

            try {
                return setTarget(Target.valueOf(target.toUpperCase(Locale.ENGLISH)));
            } catch (IllegalArgumentException e) {
                throw new IllegalTargetNameException(target);
            }
        }

        /**
         * Sets the target field.
         *
         * @param target the target.
         * @return this for method chaining.
         */
        public final Builder setTarget(Target target) {
            this.target = target;
            return this;
        }
    }
}
