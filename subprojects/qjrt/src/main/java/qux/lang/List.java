package qux.lang;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Arrays;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class List extends Obj {

    private Obj[] data;
    private int count;

    private List(Obj[] data) {
        checkArgument(!Arrays.asList(data).contains(null), "data cannot contain null");

        this.data = data.clone();
        this.count = data.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Str _desc_() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < count; i++) {
            sb.append(data[i]._desc_());

            if (i < count - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");

        return Str.valueOf(sb.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Meta meta() {
        // TODO: Implement meta()
        throw new InternalError("meta() not implemented");
    }

    public static List valueOf(Obj... data) {
        return new List(data);
    }
}
