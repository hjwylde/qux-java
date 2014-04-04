package com.hjwylde.qux.util;

import static com.google.common.base.Preconditions.checkState;
import static com.hjwylde.qux.util.Type.ANY;
import static com.hjwylde.qux.util.Type.BOOL;
import static com.hjwylde.qux.util.Type.FUNCTION_PARAM_END;
import static com.hjwylde.qux.util.Type.FUNCTION_START;
import static com.hjwylde.qux.util.Type.INT;
import static com.hjwylde.qux.util.Type.LIST_START;
import static com.hjwylde.qux.util.Type.NULL;
import static com.hjwylde.qux.util.Type.REAL;
import static com.hjwylde.qux.util.Type.STR;
import static com.hjwylde.qux.util.Type.UNION_END;
import static com.hjwylde.qux.util.Type.UNION_START;
import static com.hjwylde.qux.util.Type.VOID;

import com.google.common.collect.ImmutableList;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class Types {

    /**
     * This class cannot be instantiated.
     */
    private Types() {}

    public static ImmutableList<Type> getFunctionParameterTypes(Type type) {
        return getFunctionParameterTypes(type.getDescriptor());
    }

    public static ImmutableList<Type> getFunctionParameterTypes(String desc) {
        checkState(isFunction(desc), "cannot get parameters on a non-function type");

        ImmutableList.Builder<Type> builder = ImmutableList.builder();
        int index = 1;
        while (!desc.substring(index, index + 1).equals(FUNCTION_PARAM_END)) {
            String param = parseFirstType(desc, index);
            builder.add(Type.of(param));

            index += param.length();
        }

        return builder.build();
    }

    public static Type getFunctionReturnType(Type type) {
        return getFunctionReturnType(type.getDescriptor());
    }

    public static Type getFunctionReturnType(String desc) {
        checkState(isFunction(desc), "cannot get return type on a non-function type");

        return Type.of(desc.substring(desc.indexOf(FUNCTION_PARAM_END) + 1));
    }

    public static Type getListInnerType(Type type) {
        return getListInnerType(type.getDescriptor());
    }

    public static Type getListInnerType(String desc) {
        checkState(isList(desc), "cannot get inner type on a non-list type");

        return Type.of(desc.substring(LIST_START.length()));
    }

    public static ImmutableList<Type> getUnionTypes(Type type) {
        return getUnionTypes(type.getDescriptor());
    }

    public static ImmutableList<Type> getUnionTypes(String desc) {
        checkState(isUnion(desc), "cannot get types on a non-union type");

        ImmutableList.Builder<Type> builder = ImmutableList.builder();
        int index = 1;
        while (!desc.substring(index, index + 1).equals(UNION_END)) {
            String param = parseFirstType(desc, index);
            builder.add(Type.of(param));

            index += param.length();
        }

        return builder.build();
    }

    public static boolean isAny(String type) {
        return type.equals(ANY);
    }

    public static boolean isAny(Type type) {
        return isAny(type.getDescriptor());
    }

    public static boolean isBool(String type) {
        return type.equals(BOOL);
    }

    public static boolean isBool(Type type) {
        return isBool(type.getDescriptor());
    }

    public static boolean isFunction(String type) {
        return type.startsWith(FUNCTION_START);
    }

    public static boolean isFunction(Type type) {
        return isFunction(type.getDescriptor());
    }

    public static boolean isInt(String type) {
        return type.equals(INT);
    }

    public static boolean isInt(Type type) {
        return isInt(type.getDescriptor());
    }

    public static boolean isList(String type) {
        return type.startsWith(LIST_START);
    }

    public static boolean isList(Type type) {
        return isList(type.getDescriptor());
    }

    public static boolean isNull(String type) {
        return type.equals(NULL);
    }

    public static boolean isNull(Type type) {
        return isNull(type.getDescriptor());
    }

    public static boolean isReal(String type) {
        return type.equals(REAL);
    }

    public static boolean isReal(Type type) {
        return isReal(type.getDescriptor());
    }

    public static boolean isStr(String type) {
        return type.equals(STR);
    }

    public static boolean isStr(Type type) {
        return isStr(type.getDescriptor());
    }

    public static boolean isUnion(String type) {
        return type.startsWith(UNION_START) && type.endsWith(UNION_END);
    }

    public static boolean isUnion(Type type) {
        return isUnion(type.getDescriptor());
    }

    public static boolean isVoid(String type) {
        return type.equals(VOID);
    }

    public static boolean isVoid(Type type) {
        return isVoid(type.getDescriptor());
    }

    static boolean isValidTypeDescriptor(String desc) {
        // TODO: Fixme, does not take into account functions properly
        boolean valid = false;

        valid |= isAny(desc);
        valid |= isBool(desc);
        valid |= isFunction(desc);
        valid |= isInt(desc);
        valid |= isList(desc);
        valid |= isNull(desc);
        valid |= isReal(desc);
        valid |= isStr(desc);
        valid |= isUnion(desc);
        valid |= isVoid(desc);

        return valid;
    }

    private static String parseFirstType(String desc, int offset) {
        switch (desc.substring(offset, offset + 1)) {
            case ANY:
            case BOOL:
            case INT:
            case REAL:
            case STR:
            case VOID:
                return desc.substring(offset, offset + 1);
            case FUNCTION_START:
                int index = offset + 1;
                while (!desc.substring(index, index + 1).equals(FUNCTION_PARAM_END)) {
                    index += parseFirstType(desc, index).length();
                }

                index += 1;
                index += parseFirstType(desc, index).length();

                return desc.substring(offset, index);
            case LIST_START:
                return LIST_START + parseFirstType(desc, offset + 1);
            case UNION_START:
                index = offset + 1;
                while (!desc.substring(index, index + 1).equals(UNION_END)) {
                    index += parseFirstType(desc, index).length();
                }

                return desc.substring(offset, index + 1);
            default:
                throw new IllegalArgumentException("illegal type start character: " + desc.charAt(
                        offset));
        }
    }
}
