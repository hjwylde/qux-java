package com.hjwylde.qux.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An attribute can hold any type of extra information. It may be attached to a {@link
 * com.hjwylde.qux.tree.Node}.
 *
 * @author Henry J. Wylde
 */
public interface Attribute {

    /**
     * A control flow graph attribute. This attribute holds information regarding the control flow
     * graph of a particular function.<p/>May only be attached to a {@link
     * com.hjwylde.qux.tree.FunctionNode}.
     *
     * @author Henry J. Wylde
     * @since TODO: SINCE
     */
    public static final class ControlFlowGraph implements Attribute {

        private final com.hjwylde.qux.builder.ControlFlowGraph cfg;

        public ControlFlowGraph(com.hjwylde.qux.builder.ControlFlowGraph cfg) {
            this.cfg = checkNotNull(cfg);
        }

        /**
         * Gets the control flow graph.
         *
         * @return the control flow graph.
         */
        public com.hjwylde.qux.builder.ControlFlowGraph getControlFlowGraph() {
            return cfg;
        }
    }

    /**
     * A source attribute. This attribute holds information regarding the source file that this
     * {@link com.hjwylde.qux.tree.Node} originated from. It includes the following information: the
     * source file name, the line number, the column number and the length.
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
     * A type attribute. This attribute holds information regarding the type of the {@link
     * com.hjwylde.qux.tree.Node}.
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
