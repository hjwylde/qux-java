package com.hjwylde.common.error;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 * @since 0.1.1
 */
public class SourceCompilerError extends CompilerError {

    private final String source;

    private final int line;
    private final int col;
    private final int length;

    public SourceCompilerError(String source, int line, int col, int length) {
        super();

        checkArgument(line >= 1, "line cannot be less than 1");
        checkArgument(col >= 0, "col cannot be less than 0");
        checkArgument(length >= 1, "length cannot be less than 1");

        this.source = checkNotNull(source, "source cannot be null");

        this.line = line;
        this.col = col;
        this.length = length;
    }

    public SourceCompilerError(String message, String source, int line, int col, int length) {
        super(message);

        checkArgument(line >= 1, "line cannot be less than 1");
        checkArgument(col >= 0, "col cannot be less than 0");
        checkArgument(length >= 1, "length cannot be less than 1");

        this.source = checkNotNull(source, "source cannot be null");

        this.line = line;
        this.col = col;
        this.length = length;
    }

    public SourceCompilerError(String message, Throwable cause, String source, int line, int col,
            int length) {
        super(message, cause);

        checkArgument(line >= 1, "line cannot be less than 1");
        checkArgument(col >= 0, "col cannot be less than 0");
        checkArgument(length >= 1, "length cannot be less than 1");

        this.source = checkNotNull(source, "source cannot be null");

        this.line = line;
        this.col = col;
        this.length = length;
    }

    public SourceCompilerError(Throwable cause, String source, int line, int col, int length) {
        super(cause);

        checkArgument(line >= 1, "line cannot be less than 1");
        checkArgument(col >= 0, "col cannot be less than 0");
        checkArgument(length >= 1, "length cannot be less than 1");

        this.source = checkNotNull(source, "source cannot be null");

        this.line = line;
        this.col = col;
        this.length = length;
    }

    public int getCol() {
        return col;
    }

    public int getLength() {
        return length;
    }

    public int getLine() {
        return line;
    }

    public String getSource() {
        return source;
    }
}
