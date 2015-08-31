package qux.lang;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

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
            fields.put(field.getKey(), field.getValue().dup());
        }
    }

    private Record(Map<String, AbstractObj> fields) {
        checkArgument(!fields.containsKey(null), "fields cannot contain null key");
        checkArgument(!fields.containsValue(null), "fields cannot contain null value");

        this.fields = new TreeMap<>(fields);
    }

    public AbstractObj _get(String field) {
        return get(field);
    }

    public void _set(String field, AbstractObj value) {
        put(field, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(AbstractObj obj) {
        if (!(obj instanceof Record)) {
            return meta().compareTo(obj.meta());
        }

        Record that = (Record) obj;

        int comp = fields.size() - that.fields.size();
        if (comp != 0) {
            return comp;
        }

        Iterator<Map.Entry<String, AbstractObj>> thisIt = fields.entrySet().iterator();
        Iterator<Map.Entry<String, AbstractObj>> thatIt = that.fields.entrySet().iterator();

        while (thisIt.hasNext()) {
            Map.Entry<String, AbstractObj> thisField = thisIt.next();
            Map.Entry<String, AbstractObj> thatField = thatIt.next();

            comp = thisField.getKey().compareTo(thatField.getKey());
            if (comp != 0) {
                return comp;
            }

            comp = thisField.getValue().compareTo(thatField.getValue());
            if (comp != 0) {
                return comp;
            }
        }

        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Record dup() {
        return new Record(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        return fields.equals(((Record) obj).fields);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return fields.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
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

        return sb.toString();
    }

    public static Record valueOf(Map<String, AbstractObj> fields) {
        return new Record(fields);
    }

    synchronized AbstractObj get(String field) {
        checkArgument(fields.containsKey(field), "record does not contain field '%s'", field);

        return fields.get(field);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Meta meta() {
        Map<String, Meta> fields = new HashMap<>();

        for (Map.Entry<String, AbstractObj> field : this.fields.entrySet()) {
            fields.put(field.getKey(), field.getValue().meta());
        }

        return Meta.forRecord(fields);
    }

    synchronized void put(String field, AbstractObj value) {
        checkArgument(fields.containsKey(field), "record does not contain field '%s'", field);

        fields.put(field, checkNotNull(value, "value cannot be null"));
    }
}
