package com.hjwylde.quxc.compiler;

import static com.google.common.base.Preconditions.checkArgument;

import com.hjwylde.qbs.compiler.CompileOptions;
import com.hjwylde.qbs.compiler.QuxCompileSpec;

/**
 * Compile specification for a Qux unit. This specification adds in the ability to set the {@link
 * com.hjwylde.quxc.compiler.QuxcCompileOptions}.
 * <p/>
 * The default contains the default Quxc compile options.
 *
 * @author Henry J. Wylde
 * @since TODO: SINCE
 */
public class QuxcCompileSpec extends QuxCompileSpec {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new {@code QuxcCompileSpec} and initialises defaults as found in the {@link
     * com.hjwylde.quxc.util.QuxcProperties}.
     */
    public QuxcCompileSpec() {
        this(QuxcCompileOptions.DEFAULT_OPTIONS);
    }

    protected QuxcCompileSpec(QuxcCompileOptions options) {
        super(options);
    }

    /**
     * Gets the Quxc compile options.
     *
     * @return the Quxc compile options.
     */
    @Override
    public QuxcCompileOptions getOptions() {
        return (QuxcCompileOptions) super.getOptions();
    }

    /**
     * Sets the Quxc compile options. The options must be an instance of {@link
     * com.hjwylde.quxc.compiler.QuxcCompileOptions}, otherwise an {@link
     * java.lang.IllegalArgumentException} is thrown.
     *
     * @param options the new options.
     * @throws java.lang.IllegalArgumentException if the options aren't an instance of {@link
     * com.hjwylde.quxc.compiler.QuxcCompileOptions}.
     */
    @Override
    public void setOptions(CompileOptions options) {
        checkArgument(options instanceof QuxcCompileOptions,
                "options must be an instanceof QuxcCompileOptions");

        super.setOptions(options);
    }
}
