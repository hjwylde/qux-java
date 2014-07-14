package com.hjwylde.qux.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.hjwylde.qux.util.Type.TYPE_ANY;

import com.hjwylde.common.lang.Pair;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility class for methods pertaining to a {@link com.hjwylde.qux.util.Type}.
 *
 * @author Henry J. Wylde
 * @since 0.1.1
 */
public final class Types {

    /**
     * This class cannot be instantiated.
     */
    private Types() {}

    /**
     * Flattens the given union type. Flattening a union type will check all of the inner types, if
     * any one of them is a union type, then its inner types are added to the flattened list. The
     * end result is a single union of types.
     *
     * @param type the union type to flatten.
     * @return the flattened union type.
     */
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
        return new Type.Union(types, type.getAttributes());
    }

    public static boolean isEquivalent(Type lhs, Type rhs) {
        // Because union types are represented with sets, we can just use standard equality
        return lhs.equals(checkNotNull(rhs, "rhs cannot be null"));
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
        } else if (lhs instanceof Type.Record) {
            if (!(rhs instanceof Type.Record)) {
                return false;
            }

            return isSubtype((Type.Record) lhs, (Type.Record) rhs);
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
        return new Type.Function(normalise(type.getReturnType()), parameterTypes,
                type.getAttributes());
    }

    public static Type normalise(Type type) {
        if (type instanceof Type.Function) {
            return normalise((Type.Function) type);
        } else if (type instanceof Type.List) {
            return Type.forList(((Type.List) type).getInnerType(), type.getAttributes());
        } else if (type instanceof Type.Record) {
            return normalise((Type.Record) type);
        } else if (type instanceof Type.Set) {
            return Type.forSet(((Type.Set) type).getInnerType(), type.getAttributes());
        } else if (type instanceof Type.Union) {
            return normalise((Type.Union) type);
        }

        // Rest of the types are already normalised
        return checkNotNull(type, "type cannot be null");
    }

    public static Type normalise(Type.Union type) {
        List<Type> types = new ArrayList<>();

        // Flatten out any inner unions
        type = flatten(type);

        OUTER:
        for (Type inner : type.getTypes()) {
            // If it contains an any type, then just return that
            if (isEquivalent(inner, TYPE_ANY)) {
                return TYPE_ANY;
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
        return new Type.Union(union, type.getAttributes());
    }

    /**
     * Normalises the given record type. The purpose of this is to have a consistent form for
     * comparing different types (specifically, subtype relations). A normalised record will have
     * all element types normalised, along with any union types brought out to the upper most level.
     * For example, {@code {int|null a, real b}} would be normalised to {@code {int a, real b}|{null
     * a, real b}}. This maintains all properties of the type while making it easy to do subtype
     * comparisons between records.
     *
     * @param type the record type to normalise.
     * @return the normalised record type.
     */
    public static Type normalise(Type.Record type) {
        // We're going to split up all the inner unions of the record
        // An example of how it will be represented:
        // {int|null a, real b} = [{("a", int), ("a", null)}, {("b", real)}]
        // That way when we do a cartesian product on all of the sets, we will get all possible
        // record types
        // The final result will be: {int a, real b}|{null a, real b}

        // Split up the record as per the example above
        List<Set<Pair<Identifier, Type>>> split = new ArrayList<>();
        for (Map.Entry<Identifier, Type> entry : type.getFields().entrySet()) {
            Set<Pair<Identifier, Type>> pairs = new HashSet<>();
            Type normalised = normalise(entry.getValue());

            if (normalised instanceof Type.Union) {
                for (Type inner : ((Type.Union) normalised).getTypes()) {
                    pairs.add(new Pair<>(entry.getKey(), normalise(inner)));
                }
            } else {
                pairs.add(new Pair<>(entry.getKey(), normalised));
            }

            split.add(pairs);
        }

        // Cartesian product time!
        Set<List<Pair<Identifier, Type>>> product = Sets.cartesianProduct(split);

        // Recreate the union of records now, so we'll have {int a, real b}|{null a, real b}
        List<Type.Record> union = new ArrayList<>();
        for (List<Pair<Identifier, Type>> inner : product) {
            Map<Identifier, Type> record = new HashMap<>();
            for (Pair<Identifier, Type> pair : inner) {
                record.put(pair.getFirst(), pair.getSecond());
            }

            union.add(new Type.Record(record));
        }

        checkState(!union.isEmpty(), "normalisation of record resulted in union of size 0: %s",
                type);

        if (union.size() == 1) {
            return new Type.Record(union.get(0).getFields(), type.getAttributes());
        }

        // Create a new union using the constructor to avoid an infinite recursive call to normalise
        return new Type.Union(union, type.getAttributes());
    }

    private static boolean isSubtype(Type.Record lhs, Type.Record rhs) {
        if (!lhs.getFields().keySet().containsAll(rhs.getFields().keySet())) {
            return false;
        }

        for (Identifier key : rhs.getFields().keySet()) {
            if (!isSubtype(lhs.getFields().get(key), rhs.getFields().get(key))) {
                return false;
            }
        }

        return true;
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
