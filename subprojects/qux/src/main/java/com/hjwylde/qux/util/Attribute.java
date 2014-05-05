package com.hjwylde.qux.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public interface Attribute {

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     */
    public static final class Source implements Attribute {

        private final String source;

        private final int line;
        private final int col;
        private final int length;

        public Source(String source, int line, int col, int length) {
            checkArgument(line >= 1, "line cannot be less than 1");
            checkArgument(col >= 0, "col cannot be less than 0");
            checkArgument(length >= 0, "length cannot be less than 0");

            this.source = checkNotNull(source, "source cannot be null");

            this.line = line;
            this.col = col;
            this.length = length;
        }

        /**
         * Gets the column number of this source attribute. Starts from {@code 0}.
         *
         * @return the column number.
         */
        public int getCol() {
            return col;
        }

        /**
         * Gets the length of text this source attribute spans. Greater or equal to {@code 0}.
         *
         * @return the source length.
         */
        public int getLength() {
            return length;
        }

        /**
         * Gets the line number of this source attribute. Starts from {@code 1}.
         */
        public int getLine() {
            return line;
        }

        /**
         * Gets the source file name.
         *
         * @return the source file name.
         */
        public String getSource() {
            return source;
        }
    }

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     * @since 0.1.1
     */
    public static final class Type implements Attribute {

        private com.hjwylde.qux.util.Type type;

        public Type(com.hjwylde.qux.util.Type type) {
            setType(type);
        }

        /**
         * Gets the type.
         *
         * @return the type.
         */
        public com.hjwylde.qux.util.Type getType() {
            return type;
        }

        public void setType(com.hjwylde.qux.util.Type type) {
            this.type = checkNotNull(type, "type cannot be null");
        }
    }
}
