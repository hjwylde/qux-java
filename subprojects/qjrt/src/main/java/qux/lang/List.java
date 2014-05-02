package qux.lang;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import qux.lang.operators.Len;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class List extends Obj implements Len {

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
    public Int _len_() {
        return Int.valueOf(count);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Meta meta() {
        Set<Meta> types = new HashSet<>();

        for (Obj datum : data) {
            types.add(datum.meta());
        }

        // TODO: Normalise the list before the checks

        if (types.isEmpty()) {
            return Meta.forList(Meta.META_ANY);
        }

        if (types.size() == 1) {
            return Meta.forList(types.iterator().next());
        }

        return Meta.forList(Meta.forUnion(types));
    }

    public static List valueOf(Obj... data) {
        return new List(data);
    }
}
