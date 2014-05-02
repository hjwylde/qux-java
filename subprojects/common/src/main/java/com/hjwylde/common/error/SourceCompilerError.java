package com.hjwylde.common.error;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents a compiler error with some attached source information. Contains source file
 * information pertaining to: the file that the error was caused in and the line number, column and
 * length of the token that caused the error.
 *
 * @author Henry J. Wylde
 * @since 0.1.1
 */
public class SourceCompilerError extends CompilerError {

    private final String source;

    private final int line;
    private final int col;
    private final int length;

    /**
     * Creates a new {@code SourceCompilerError} with the given source file name, line number,
     * column number and length.
     *
     * @param source the source file.
     * @param line the line number, starting from 1.
     * @param col the column number, starting from 0.
     * @param length the length, greater than or equal to 0.
     */
    public SourceCompilerError(String source, int line, int col, int length) {
        super();

        checkArgument(line >= 1, "line cannot be less than 1 (line=%s)", line);
        checkArgument(col >= 0, "col cannot be less than 0 (col=%s)", col);
        checkArgument(length >= 0, "length cannot be less than 0 (length=%s)", length);

        this.source = checkNotNull(source, "source cannot be null");

        this.line = line;
        this.col = col;
        this.length = length;
    }

    /**
     * Creates a new {@code SourceCompilerError} with the given error message, source file name,
     * line number, column number and length.
     *
     * @param message the error message.
     * @param source the source file.
     * @param line the line number, starting from 1.
     * @param col the column number, starting from 0.
     * @param length the length, greater than or equal to 0.
     */
    public SourceCompilerError(String message, String source, int line, int col, int length) {
        super(message);

        checkArgument(line >= 1, "line cannot be less than 1 (line=%s)", line);
        checkArgument(col >= 0, "col cannot be less than 0 (col=%s)", col);
        checkArgument(length >= 0, "length cannot be less than 0 (length=%s)", length);

        this.source = checkNotNull(source, "source cannot be null");

        this.line = line;
        this.col = col;
        this.length = length;
    }

    /**
     * Creates a new {@code SourceCompilerError} with the given error message, cause, source file
     * name, line number, column number and length.
     *
     * @param message the error message.
     * @param cause the cause of this error.
     * @param source the source file.
     * @param line the line number, starting from 1.
     * @param col the column number, starting from 0.
     * @param length the length, greater than or equal to 0.
     */
    public SourceCompilerError(String message, Throwable cause, String source, int line, int col,
            int length) {
        super(message, cause);

        checkArgument(line >= 1, "line cannot be less than 1 (line=%s)", line);
        checkArgument(col >= 0, "col cannot be less than 0 (col=%s)", col);
        checkArgument(length >= 0, "length cannot be less than 0 (length=%s)", length);

        this.source = checkNotNull(source, "source cannot be null");

        this.line = line;
        this.col = col;
        this.length = length;
    }

    /**
     * Creates a new {@code SourceCompilerError} with the given cause, source file name, line
     * number, column number and length.
     *
     * @param cause the cause of this error.
     * @param source the source file.
     * @param line the line number, starting from 1.
     * @param col the column number, starting from 0.
     * @param length the length, greater than or equal to 0.
     */
    public SourceCompilerError(Throwable cause, String source, int line, int col, int length) {
        super(cause);

        checkArgument(line >= 1, "line cannot be less than 1 (line=%s)", line);
        checkArgument(col >= 0, "col cannot be less than 0 (col=%s)", col);
        checkArgument(length >= 0, "length cannot be less than 0 (length=%s)", length);

        this.source = checkNotNull(source, "source cannot be null");

        this.line = line;
        this.col = col;
        this.length = length;
    }

    /**
     * Gets the column number in the line that this error occurred on. The column number starts from
     * 0.
     *
     * @return the column number.
     */
    public int getCol() {
        return col;
    }

    /**
     * Gets the length of the token that caused this error. The length is greater than or equal to
     * 0.
     *
     * @return the length.
     */
    public int getLength() {
        return length;
    }

    /**
     * Gets the line number of the token that caused this error. The line number starts from 1.
     *
     * @return the line number.
     */
    public int getLine() {
        return line;
    }

    /**
     * Gets the source file name that the error occurred in.
     *
     * @return the source file name.
     */
    public String getSource() {
        return source;
    }
}
