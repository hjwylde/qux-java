package com.hjwylde.qux.util;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Iterator;

import javax.annotation.Nullable;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 */
public final class Type {

    // TODO: Convert these to each have a class per type

    /**
     * String representation of the <code>any</code> type.
     */
    public static final String ANY = "A";
    public static final Type TYPE_ANY = Type.of(ANY);

    /**
     * String representation of the <code>bool</code> type.
     */
    public static final String BOOL = "B";
    public static final Type TYPE_BOOL = Type.of(BOOL);

    /**
     * String representation of the <code>int</code> type.
     */
    public static final String INT = "Z";
    public static final Type TYPE_INT = Type.of(INT);

    /**
     * String representation of the {@code int} type.
     */
    public static final String NULL = "N";
    public static final Type TYPE_NULL = Type.of(NULL);

    /**
     * String representation of the <code>real</code> type.
     */
    public static final String REAL = "R";
    public static final Type TYPE_REAL = Type.of(REAL);

    /**
     * String representation of the <code>str</code> type.
     */
    public static final String STR = "S";
    public static final Type TYPE_STR = Type.of(STR);

    /**
     * String representation of the <code>void</code> type.
     */
    public static final String VOID = "V";
    public static final Type TYPE_VOID = Type.of(VOID);

    public static final String FUNCTION_START = "(";
    public static final String FUNCTION_PARAM_END = ")";

    public static final String LIST_START = "[";

    public static final String UNION_START = "U";
    public static final String UNION_END = ";";

    private static final Logger logger = LoggerFactory.getLogger(Type.class);

    private final String desc;

    private Type(String... concat) {
        this.desc = Joiner.on("").join(concat);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Type)) {
            return false;
        }

        return desc.equals(((Type) obj).desc);
    }

    public static Type forFunction(Type returnType, Type... parameters) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (Type parameter : parameters) {
            sb.append(parameter.getDescriptor());
        }
        sb.append(")");
        sb.append(returnType.getDescriptor());

        return new Type(sb.toString());
    }

    public static Type forFunction(String returnType, String... parameters) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (String parameter : parameters) {
            sb.append(parameter);
        }
        sb.append(")");
        sb.append(returnType);

        return new Type(sb.toString());
    }

    public static Type forList(String innerType) {
        return new Type(LIST_START + innerType);
    }

    public static Type forList(Type innerType) {
        return forList(innerType.getDescriptor());
    }

    public static Type forUnion(String... types) {
        return Type.of(UNION_START + Joiner.on("").join(types) + UNION_END);
    }

    public static Type forUnion(Type... types) {
        return forUnion(Lists.transform(Arrays.asList(types), new Function<Type, String>() {
            @Nullable
            @Override
            public String apply(@Nullable Type input) {
                return input.getDescriptor();
            }
        }).toArray(new String[0]));
    }

    public String getDescriptor() {
        return desc;
    }

    @Override
    public int hashCode() {
        return desc.hashCode();
    }

    public static Type of(String desc) {
        checkArgument(Types.isValidTypeDescriptor(desc), "invalid type descriptor: " + desc);

        return new Type(desc);
    }

    @Override
    public String toString() {
        if (Types.isAny(this)) {
            return "any";
        } else if (Types.isBool(this)) {
            return "bool";
        } else if (Types.isList(this)) {
            return "[" + Types.getListInnerType(this) + "]";
        } else if (Types.isReal(this)) {
            return "real";
        } else if (Types.isFunction(this)) {
            StringBuilder sb = new StringBuilder();

            sb.append("(");
            for (Iterator<Type> it = Types.getFunctionParameterTypes(this).iterator();
                    it.hasNext(); ) {
                sb.append(it.next());
                if (it.hasNext()) {
                    sb.append(", ");
                }
            }
            sb.append(") => ");
            sb.append(Types.getFunctionReturnType(this));

            return sb.toString();
        } else if (Types.isInt(this)) {
            return "int";
        } else if (Types.isNull(this)) {
            return "null";
        } else if (Types.isStr(this)) {
            return "str";
        } else if (Types.isUnion(this)) {
            StringBuilder sb = new StringBuilder();

            for (Iterator<Type> it = Types.getUnionTypes(this).iterator(); it.hasNext(); ) {
                sb.append(it.next());

                if (it.hasNext()) {
                    sb.append("|");
                }
            }

            return sb.toString();
        } else if (Types.isVoid(this)) {
            return "void";
        } else {
            logger.warn("Type.toString() not fully implemented: " + desc);

            return getDescriptor();
        }
    }
}
