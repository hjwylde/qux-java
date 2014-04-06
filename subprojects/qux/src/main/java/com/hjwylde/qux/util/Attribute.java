package com.hjwylde.qux.util;

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

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     * @since 0.1.1
     */
    public static final class Type implements Attribute {

        private final com.hjwylde.qux.util.Type type;

        public Type(com.hjwylde.qux.util.Type type) {
            this.type = checkNotNull(type, "type cannot be null");
        }

        public com.hjwylde.qux.util.Type getType() {
            return type;
        }
    }
}
