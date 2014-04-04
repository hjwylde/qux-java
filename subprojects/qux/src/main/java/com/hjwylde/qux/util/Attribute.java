package com.hjwylde.qux.util;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public abstract class Attribute {

    public static final class Source {

        // TODO: Make the tree use the source attributes

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
}
