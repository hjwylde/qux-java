package com.hjwylde.qux.util;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 * @since 0.1.1
 */
public final class Types {

    /**
     * This class cannot be instantiated.
     */
    private Types() {}

    public static Type.Union flatten(Type.Union type) {
        List<Type> types = new ArrayList<>();

        for (Type inner : type.getTypes()) {
            if (inner instanceof Type.Union) {
                types.addAll(((Type.Union) inner).getTypes());
            } else {
                types.add(inner);
            }
        }

        // Create a new union using the constructor to avoid an infinite recursive call to normalise
        return new Type.Union(types);
    }

    public static boolean isEquivalent(Type lhs, Type rhs) {
        // Because union types are represented with sets, we can just use standard equality
        return lhs.equals(rhs);
    }

    public static boolean isSubtype(Type lhs, Type rhs) {
        lhs = normalise(lhs);
        rhs = normalise(rhs);

        if (rhs instanceof Type.Any) {
            return true;
        } else if (rhs instanceof Type.Union) {
            return isSubtype(lhs, (Type.Union) rhs);
        }

        if (lhs instanceof Type.Function) {
            if (!(rhs instanceof Type.Function)) {
                return false;
            }

            return isSubtype((Type.Function) lhs, (Type.Function) rhs);
        } else if (lhs instanceof Type.List) {
            if (!(rhs instanceof Type.List)) {
                return false;
            }

            return isSubtype(((Type.List) lhs).getInnerType(), ((Type.List) rhs).getInnerType());
        } else if (lhs instanceof Type.Set) {
            if (!(rhs instanceof Type.Set)) {
                return false;
            }

            return isSubtype(((Type.Set) lhs).getInnerType(), ((Type.Set) rhs).getInnerType());
        } else if (lhs instanceof Type.Union) {
            return isSubtype((Type.Union) lhs, rhs);
        }

        // Rest of the types only subtype themselves
        return lhs.equals(rhs);
    }

    public static Type.Function normalise(Type.Function type) {
        List<Type> parameterTypes = new ArrayList<>();

        for (Type parameterType : type.getParameterTypes()) {
            parameterTypes.add(normalise(parameterType));
        }

        // Create a new function using the constructor to avoid an infinite recursive call to normalise
        return new Type.Function(normalise(type.getReturnType()), parameterTypes);
    }

    public static Type.Set normalise(Type.Set type) {
        // Create a new set using the constructor to avoid an infinite recursive call to normalise
        return new Type.Set(normalise(type.getInnerType()));
    }

    public static Type.List normalise(Type.List type) {
        // Create a new list using the constructor to avoid an infinite recursive call to normalise
        return new Type.List(normalise(type.getInnerType()));
    }

    public static Type normalise(Type type) {
        if (type instanceof Type.Function) {
            return normalise((Type.Function) type);
        } else if (type instanceof Type.List) {
            return normalise((Type.List) type);
        } else if (type instanceof Type.Set) {
            return normalise((Type.Set) type);
        } else if (type instanceof Type.Union) {
            return normalise((Type.Union) type);
        }

        // Rest of the types are already normalised
        return type;
    }

    public static Type normalise(Type.Union type) {
        List<Type> types = new ArrayList<>();

        // Flatten out any inner unions
        type = flatten(type);

        OUTER:
        for (Type inner : type.getTypes()) {
            // If it contains an any type, then just return that
            if (inner.equals(Type.TYPE_ANY)) {
                return Type.TYPE_ANY;
            }

            INNER:
            for (int i = 0; i < types.size(); i++) {
                // Ignore subtypes
                if (isSubtype(inner, types.get(i))) {
                    continue OUTER;
                } else if (isSubtype(types.get(i), inner)) {
                    // Supertypes should override the redundent type
                    types.set(i, inner);

                    // Continue on the inner, as it may supertype more than one inner type
                    // If it does, then duplicates will be removed when we create a set of the types
                    continue INNER;
                }
            }

            types.add(inner);
        }

        Set<Type> union = ImmutableSet.copyOf(types);

        checkState(!union.isEmpty(), "normalisation of union resulted in union of size 0: %s",
                type);

        if (union.size() == 1) {
            return union.iterator().next();
        }

        // Create a new union using the constructor to avoid an infinite recursive call to normalise
        return new Type.Union(union);
    }

    private static boolean isSubtype(Type.Function lhs, Type.Function rhs) {
        if (!isSubtype(lhs.getReturnType(), rhs.getReturnType())) {
            return false;
        }

        if (lhs.getParameterTypes().size() < rhs.getParameterTypes().size()) {
            return false;
        }

        for (int i = 0; i < lhs.getParameterTypes().size(); i++) {
            if (!isSubtype(lhs.getParameterTypes().get(i), rhs.getParameterTypes().get(i))) {
                return false;
            }
        }

        return true;
    }

    private static boolean isSubtype(Type lhs, Type.Union rhs) {
        for (Type type : rhs.getTypes()) {
            if (isSubtype(lhs, type)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isSubtype(Type.Union lhs, Type rhs) {
        for (Type type : lhs.getTypes()) {
            if (!isSubtype(type, rhs)) {
                return false;
            }
        }

        return true;
    }
}
