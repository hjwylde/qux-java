package qux.lang;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static qux.lang.Bool.FALSE;
import static qux.lang.Bool.TRUE;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * TODO: Documentation
 * <p>
 * TODO: Implement reference counting
 *
 * @author Henry J. Wylde
 * @since 0.2.4
 */
public final class Record extends AbstractObj {

    private final Map<String, AbstractObj> fields;

    private Record(Record record) {
        fields = new TreeMap<>();
        for (Map.Entry<String, AbstractObj> field : record.fields.entrySet()) {
            fields.put(field.getKey(), field.getValue()._dup_());
        }
    }

    private Record(Map<String, AbstractObj> fields) {
        checkArgument(!fields.containsKey(null), "fields cannot contain null key");
        checkArgument(!fields.containsValue(null), "fields cannot contain null value");

        this.fields = new TreeMap<>(fields);
    }

    public void _assign_(String field, AbstractObj value) {
        put(field, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Int _comp_(AbstractObj obj) {
        if (!(obj instanceof Record)) {
            return meta()._comp_(obj.meta());
        }

        Record that = (Record) obj;

        Int comp = Int.valueOf(fields.size() - that.fields.size());
        if (!comp.equals(Int.ZERO)) {
            return comp;
        }

        Iterator<Map.Entry<String, AbstractObj>> thisIt = fields.entrySet().iterator();
        Iterator<Map.Entry<String, AbstractObj>> thatIt = that.fields.entrySet().iterator();

        while (thisIt.hasNext()) {
            Map.Entry<String, AbstractObj> thisField = thisIt.next();
            Map.Entry<String, AbstractObj> thatField = thatIt.next();

            comp = Int.valueOf(thisField.getKey().compareTo(thatField.getKey()));
            if (!comp.equals(Int.ZERO)) {
                return comp;
            }

            comp = thisField.getValue()._comp_(thatField.getValue());
            if (!comp.equals(Int.ZERO)) {
                return comp;
            }
        }

        return Int.ZERO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Str _desc_() {
        StringBuilder sb = new StringBuilder();

        sb.append("{");
        for (Iterator<String> it = fields.keySet().iterator(); it.hasNext(); ) {
            String key = it.next();

            sb.append(key);
            sb.append(": ");
            sb.append(fields.get(key));

            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append("}");

        return Str.valueOf(sb.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Record _dup_() {
        return new Record(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _eq_(AbstractObj obj) {
        if (super._eq_(obj) == FALSE) {
            return FALSE;
        }

        return fields.equals(((Record) obj).fields) ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Int _hash_() {
        return Int.valueOf(fields.hashCode());
    }

    public synchronized AbstractObj get(String field) {
        checkArgument(fields.containsKey(field), "record does not contain field '%s'", field);

        return fields.get(field);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Meta meta() {
        Map<String, Meta> fields = new HashMap<>();

        for (Map.Entry<String, AbstractObj> field : this.fields.entrySet()) {
            fields.put(field.getKey(), field.getValue().meta());
        }

        return Meta.forRecord(fields);
    }

    public static Record valueOf(Map<String, AbstractObj> fields) {
        return new Record(fields);
    }

    synchronized void put(String field, AbstractObj value) {
        checkArgument(fields.containsKey(field), "record does not contain field '%s'", field);

        fields.put(field, checkNotNull(value, "value cannot be null"));
    }
}
