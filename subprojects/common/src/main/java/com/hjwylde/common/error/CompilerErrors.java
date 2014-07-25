package com.hjwylde.common.error;

/**
 * Utility class for creating {@link com.hjwylde.common.error.CompilerError}s with consistent error
 * messages.
 *
 * @author Henry J. Wylde
 */
public final class CompilerErrors {

    /**
     * This class cannot be instantiated.
     */
    private CompilerErrors() {}

    /**
     * Creates a new {@link com.hjwylde.common.error.CompilerError} representing a duplicate
     * constant definition found.
     *
     * @param name the name of the constant.
     * @return the created {@link com.hjwylde.common.error.CompilerError}.
     */
    public static CompilerError duplicateConstant(String name) {
        String message = String.format("duplicate constant definition found for '%s'", name);

        return new CompilerError(message);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.SourceCompilerError} representing a duplicate
     * constant definition found with the provided source information.
     *
     * @param name the name of the constant.
     * @param source the source file name.
     * @param line the line number.
     * @param col the column number.
     * @param length the length.
     * @return the created {@link com.hjwylde.common.error.SourceCompilerError}.
     */
    public static SourceCompilerError duplicateConstant(String name, String source, int line,
            int col, int length) {
        String message = String.format("duplicate constant definition found for '%s'", name);

        return new SourceCompilerError(message, source, line, col, length);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.CompilerError} representing a duplicate
     * function definition found.
     *
     * @param name the name of the function.
     * @return the created {@link com.hjwylde.common.error.CompilerError}.
     */
    public static CompilerError duplicateFunction(String name) {
        String message = String.format("duplicate function definition found for '%s'", name);

        return new CompilerError(message);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.SourceCompilerError} representing a duplicate
     * function definition found with the provided source information.
     *
     * @param name the name of the function.
     * @param source the source file name.
     * @param line the line number.
     * @param col the column number.
     * @param length the length.
     * @return the created {@link com.hjwylde.common.error.SourceCompilerError}.
     */
    public static SourceCompilerError duplicateFunction(String name, String source, int line,
            int col, int length) {
        String message = String.format("duplicate function definition found for '%s'", name);

        return new SourceCompilerError(message, source, line, col, length);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.CompilerError} representing a duplicate
     * function parameter definition found.
     *
     * @param name the name of the function parameter.
     * @return the created {@link com.hjwylde.common.error.CompilerError}.
     */
    public static CompilerError duplicateFunctionParameter(String name) {
        String message = String.format("duplicate function parameter definition found for '%s'",
                name);

        return new CompilerError(message);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.SourceCompilerError} representing a duplicate
     * function parameter definition found with the provided source information.
     *
     * @param name the name of the function parameter.
     * @param source the source file name.
     * @param line the line number.
     * @param col the column number.
     * @param length the length.
     * @return the created {@link com.hjwylde.common.error.SourceCompilerError}.
     */
    public static SourceCompilerError duplicateFunctionParameter(String name, String source,
            int line, int col, int length) {
        String message = String.format("duplicate function parameter definition found for '%s'",
                name);

        return new SourceCompilerError(message, source, line, col, length);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.CompilerError} representing a duplicate import
     * definition found.
     *
     * @param id the id of the import.
     * @return the created {@link com.hjwylde.common.error.CompilerError}.
     */
    public static CompilerError duplicateImport(String id) {
        String message = String.format("duplicate import definition found for '%s'", id);

        return new CompilerError(message);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.SourceCompilerError} representing a duplicate
     * import definition found with the provided source information.
     *
     * @param id the id of the import.
     * @param source the source file name.
     * @param line the line number.
     * @param col the column number.
     * @param length the length.
     * @return the created {@link com.hjwylde.common.error.SourceCompilerError}.
     */
    public static SourceCompilerError duplicateImport(String id, String source, int line, int col,
            int length) {
        String message = String.format("duplicate import definition found for '%s'", id);

        return new SourceCompilerError(message, source, line, col, length);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.CompilerError} representing a duplicate record
     * field definition found.
     *
     * @param name the name of the record field.
     * @return the created {@link com.hjwylde.common.error.CompilerError}.
     */
    public static CompilerError duplicateRecordField(String name) {
        String message = String.format("duplicate record field definition found for '%s'", name);

        return new CompilerError(message);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.SourceCompilerError} representing a duplicate
     * record field definition found with the provided source information.
     *
     * @param name the name of the record field.
     * @param source the source file name.
     * @param line the line number.
     * @param col the column number.
     * @param length the length.
     * @return the created {@link com.hjwylde.common.error.SourceCompilerError}.
     */
    public static SourceCompilerError duplicateRecordField(String name, String source, int line,
            int col, int length) {
        String message = String.format("duplicate record field definition found for '%s'", name);

        return new SourceCompilerError(message, source, line, col, length);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.CompilerError} representing a duplicate type
     * definition found.
     *
     * @param name the name of the type definition.
     * @return the created {@link com.hjwylde.common.error.CompilerError}.
     */
    public static CompilerError duplicateType(String name) {
        String message = String.format("duplicate type definition found for '%s'", name);

        return new CompilerError(message);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.SourceCompilerError} representing a duplicate
     * type definition found with the provided source information.
     *
     * @param name the name of the type.
     * @param source the source file name.
     * @param line the line number.
     * @param col the column number.
     * @param length the length.
     * @return the created {@link com.hjwylde.common.error.SourceCompilerError}.
     */
    public static SourceCompilerError duplicateType(String name, String source, int line, int col,
            int length) {
        String message = String.format("duplicate type definition found for '%s'", name);

        return new SourceCompilerError(message, source, line, col, length);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.CompilerError} representing an invalid
     * assignment.
     *
     * @return the created {@link com.hjwylde.common.error.CompilerError}.
     */
    public static CompilerError invalidAssignment() {
        return new CompilerError("invalid assignment");
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.SourceCompilerError} representing an invalid
     * assignment with the provided source information.
     *
     * @param source the source file name.
     * @param line the line number.
     * @param col the column number.
     * @param length the length.
     * @return the created {@link com.hjwylde.common.error.SourceCompilerError}.
     */
    public static SourceCompilerError invalidAssignment(String source, int line, int col,
            int length) {
        return new SourceCompilerError("invalid assignment", source, line, col, length);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.CompilerError} representing an invalid constant
     * identifier.
     *
     * @param id the constant identifier.
     * @param regex the expected regular expression.
     * @return the created {@link com.hjwylde.common.error.CompilerError}.
     */
    public static CompilerError invalidConstantIdentifier(String id, String regex) {
        String message = String.format(
                "invalid constant identifier, received '%s' but expected '%s'", id, regex);

        return new CompilerError(message);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.SourceCompilerError} representing an invalid
     * constant identifier with the provided source information.
     *
     * @param id the constant identifier.
     * @param regex the expected regular expression.
     * @param source the source file name.
     * @param line the line number.
     * @param col the column number.
     * @param length the length.
     * @return the created {@link com.hjwylde.common.error.SourceCompilerError}.
     */
    public static SourceCompilerError invalidConstantIdentifier(String id, String regex,
            String source, int line, int col, int length) {
        String message = String.format(
                "invalid constant identifier, received '%s' but expected '%s'", id, regex);

        return new SourceCompilerError(message, source, line, col, length);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.CompilerError} representing an invalid dedent.
     *
     * @return the created {@link com.hjwylde.common.error.CompilerError}.
     */
    public static CompilerError invalidDedent() {
        return new CompilerError("invalid dedent");
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.SourceCompilerError} representing an invalid
     * dedent with the provided source information.
     *
     * @param source the source file name.
     * @param line the line number.
     * @param col the column number.
     * @param length the length.
     * @return the created {@link com.hjwylde.common.error.SourceCompilerError}.
     */
    public static SourceCompilerError invalidDedent(String source, int line, int col, int length) {
        return new SourceCompilerError("invalid dedent", source, line, col, length);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.CompilerError} representing an invalid generic
     * identifier.
     *
     * @param id the generic identifier.
     * @param regex the expected regular expression.
     * @return the created {@link com.hjwylde.common.error.CompilerError}.
     */
    public static CompilerError invalidGenericIdentifier(String id, String regex) {
        String message = String.format(
                "invalid generic identifier, received '%s' but expected '%s'", id, regex);

        return new CompilerError(message);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.SourceCompilerError} representing an invalid
     * generic identifier with the provided source information.
     *
     * @param id the generic identifier.
     * @param regex the expected regular expression.
     * @param source the source file name.
     * @param line the line number.
     * @param col the column number.
     * @param length the length.
     * @return the created {@link com.hjwylde.common.error.SourceCompilerError}.
     */
    public static SourceCompilerError invalidGenericIdentifier(String id, String regex,
            String source, int line, int col, int length) {
        String message = String.format(
                "invalid generic identifier, received '%s' but expected '%s'", id, regex);

        return new SourceCompilerError(message, source, line, col, length);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.CompilerError} representing an invalid
     * identifier.
     *
     * @param id the identifier.
     * @param regex the expected regular expression.
     * @return the created {@link com.hjwylde.common.error.CompilerError}.
     */
    public static CompilerError invalidIdentifier(String id, String regex) {
        String message = String.format("invalid identifier, received '%s' but expected '%s'", id);

        return new CompilerError(message);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.SourceCompilerError} representing an invalid
     * identifier with the provided source information.
     *
     * @param id the identifier.
     * @param regex the expected regular expression.
     * @param source the source file name.
     * @param line the line number.
     * @param col the column number.
     * @param length the length.
     * @return the created {@link com.hjwylde.common.error.SourceCompilerError}.
     */
    public static SourceCompilerError invalidIdentifier(String id, String regex, String source,
            int line, int col, int length) {
        String message = String.format("invalid identifier, received '%s' but expected '%s'", id,
                regex);

        return new SourceCompilerError(message, source, line, col, length);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.SourceCompilerError} representing an invalid
     * import with the provided source information.
     *
     * @param id the import id.
     * @param regex the expected regular expression.
     * @param source the source file name.
     * @param line the line number.
     * @param col the column number.
     * @param length the length.
     * @return the created {@link com.hjwylde.common.error.SourceCompilerError}.
     */
    public static SourceCompilerError invalidImport(String id, String regex, String source,
            int line, int col, int length) {
        String message = String.format("invalid import received '%s' but expected '%s'", id, regex);

        return new SourceCompilerError(message, source, line, col, length);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.CompilerError} representing an invalid import.
     *
     * @param id the import id.
     * @return the created {@link com.hjwylde.common.error.CompilerError}.
     */
    public static CompilerError invalidInstance(String id) {
        String message = String.format("invalid import '%s'", id);

        return new CompilerError(message);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.CompilerError} representing an invalid
     * instance.
     *
     * @param received the received instance.
     * @param expected the expected instance.
     * @return the created {@link com.hjwylde.common.error.CompilerError}.
     */
    public static CompilerError invalidInstance(String received, String expected) {
        String message = String.format("invalid instance, received '%s' but expected '%s'",
                received, expected);

        return new CompilerError(message);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.SourceCompilerError} representing an invalid
     * instance with the provided source information.
     *
     * @param received the received instance.
     * @param expected the expected instance.
     * @param source the source file name.
     * @param line the line number.
     * @param col the column number.
     * @param length the length.
     * @return the created {@link com.hjwylde.common.error.SourceCompilerError}.
     */
    public static SourceCompilerError invalidInstance(String received, String expected,
            String source, int line, int col, int length) {
        String message = String.format("invalid instance, received '%s' but expected '%s'",
                received, expected);

        return new SourceCompilerError(message, source, line, col, length);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.CompilerError} representing an invalid meta.
     *
     * @param id the meta id.
     * @return the created {@link com.hjwylde.common.error.CompilerError}.
     */
    public static CompilerError invalidMeta(String id) {
        String message = String.format("invalid meta '%s'", id);

        return new CompilerError(message);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.SourceCompilerError} representing an invalid
     * meta with the provided source information.
     *
     * @param id the meta id.
     * @param source the source file name.
     * @param line the line number.
     * @param col the column number.
     * @param length the length.
     * @return the created {@link com.hjwylde.common.error.SourceCompilerError}.
     */
    public static SourceCompilerError invalidMeta(String id, String source, int line, int col,
            int length) {
        String message = String.format("invalid meta '%s'", id);

        return new SourceCompilerError(message, source, line, col, length);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.CompilerError} representing an invalid
     * namespace identifier.
     *
     * @param id the namespace identifier.
     * @param regex the expected regular expression.
     * @return the created {@link com.hjwylde.common.error.CompilerError}.
     */
    public static CompilerError invalidNamespaceIdentifier(String id, String regex) {
        String message = String.format(
                "invalid namespace identifier, received '%s' but expected '%s'", id, regex);

        return new CompilerError(message);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.SourceCompilerError} representing an invalid
     * namespace identifier with the provided source information.
     *
     * @param id the namespace identifier.
     * @param regex the expected regular expression.
     * @param source the source file name.
     * @param line the line number.
     * @param col the column number.
     * @param length the length.
     * @return the created {@link com.hjwylde.common.error.SourceCompilerError}.
     */
    public static SourceCompilerError invalidNamespaceIdentifier(String id, String regex,
            String source, int line, int col, int length) {
        String message = String.format(
                "invalid namespace identifier, received '%s' but expected '%s'", id, regex);

        return new SourceCompilerError(message, source, line, col, length);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.CompilerError} representing an invalid type.
     *
     * @param received the received type.
     * @param expected the expected type.
     * @return the created {@link com.hjwylde.common.error.CompilerError}.
     */
    public static CompilerError invalidType(String received, String expected) {
        String message = String.format("invalid type, received '%s' but expected '%s'", received,
                expected);

        return new CompilerError(message);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.SourceCompilerError} representing an invalid
     * type with the provided source information.
     *
     * @param received the received type.
     * @param expected the expected type.
     * @param source the source file name.
     * @param line the line number.
     * @param col the column number.
     * @param length the length.
     * @return the created {@link com.hjwylde.common.error.SourceCompilerError}.
     */
    public static SourceCompilerError invalidType(String received, String expected, String source,
            int line, int col, int length) {
        String message = String.format("invalid type, received '%s' but expected '%s'", received,
                expected);

        return new SourceCompilerError(message, source, line, col, length);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.CompilerError} representing an invalid type
     * identifier.
     *
     * @param id the type identifier.
     * @param regex the expected regular expression.
     * @return the created {@link com.hjwylde.common.error.CompilerError}.
     */
    public static CompilerError invalidTypeIdentifier(String id, String regex) {
        String message = String.format("invalid type identifier, received '%s' but expected '%s'",
                id, regex);

        return new CompilerError(message);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.SourceCompilerError} representing an invalid
     * type identifier with the provided source information.
     *
     * @param id the type identifier.
     * @param regex the expected regular expression.
     * @param source the source file name.
     * @param line the line number.
     * @param col the column number.
     * @param length the length.
     * @return the created {@link com.hjwylde.common.error.SourceCompilerError}.
     */
    public static SourceCompilerError invalidTypeIdentifier(String id, String regex, String source,
            int line, int col, int length) {
        String message = String.format("invalid type identifier, received '%s' but expected '%s'",
                id, regex);

        return new SourceCompilerError(message, source, line, col, length);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.CompilerError} representing a no constant
     * definition found.
     *
     * @param owner the owner of the constant.
     * @param name the name of the constant.
     * @return the created {@link com.hjwylde.common.error.CompilerError}.
     */
    public static CompilerError noConstantFound(String owner, String name) {
        String message = String.format("no constant definition found for '%s$%s'", owner, name);

        return new CompilerError(message);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.SourceCompilerError} representing a no constant
     * definition found with the provided source information.
     *
     * @param owner the owner of the constant.
     * @param name the name of the constant.
     * @param source the source file name.
     * @param line the line number.
     * @param col the column number.
     * @param length the length.
     * @return the created {@link com.hjwylde.common.error.SourceCompilerError}.
     */
    public static SourceCompilerError noConstantFound(String owner, String name, String source,
            int line, int col, int length) {
        String message = String.format("no constant definition found for '%s$%s'", owner, name);

        return new SourceCompilerError(message, source, line, col, length);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.CompilerError} representing a no function
     * definition found.
     *
     * @param owner the owner of the function.
     * @param name the name of the function.
     * @return the created {@link com.hjwylde.common.error.CompilerError}.
     */
    public static CompilerError noFunctionFound(String owner, String name) {
        String message = String.format("no function definition found for '%s$%s'", owner, name);

        return new CompilerError(message);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.SourceCompilerError} representing a no function
     * definition found with the provided source information.
     *
     * @param owner the owner of the function.
     * @param name the name of the function.
     * @param source the source file name.
     * @param line the line number.
     * @param col the column number.
     * @param length the length.
     * @return the created {@link com.hjwylde.common.error.SourceCompilerError}.
     */
    public static SourceCompilerError noFunctionFound(String owner, String name, String source,
            int line, int col, int length) {
        String message = String.format("no function definition found for '%s$%s'", owner, name);

        return new SourceCompilerError(message, source, line, col, length);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.CompilerError} representing a no resource
     * definition found.
     *
     * @param id the resource id.
     * @return the created {@link com.hjwylde.common.error.CompilerError}.
     */
    public static CompilerError noResourceFound(String id) {
        String message = String.format("no resource definition found for '%s'", id);

        return new CompilerError(message);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.SourceCompilerError} representing a no resource
     * definition found with the provided source information.
     *
     * @param id the resource id.
     * @param source the source file name.
     * @param line the line number.
     * @param col the column number.
     * @param length the length.
     * @return the created {@link com.hjwylde.common.error.SourceCompilerError}.
     */
    public static SourceCompilerError noResourceFound(String id, String source, int line, int col,
            int length) {
        String message = String.format("no resource definition found for '%s'", id);

        return new SourceCompilerError(message, source, line, col, length);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.CompilerError} representing a no type
     * definition found.
     *
     * @param owner the owner of the type.
     * @param name the name of the type.
     * @return the created {@link com.hjwylde.common.error.CompilerError}.
     */
    public static CompilerError noTypeFound(String owner, String name) {
        String message = String.format("no type definition found for '%s$%s'", owner, name);

        return new CompilerError(message);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.SourceCompilerError} representing a no type
     * definition found with the provided source information.
     *
     * @param owner the owner of the type.
     * @param name the name of the type.
     * @param source the source file name.
     * @param line the line number.
     * @param col the column number.
     * @param length the length.
     * @return the created {@link com.hjwylde.common.error.SourceCompilerError}.
     */
    public static SourceCompilerError noTypeFound(String owner, String name, String source,
            int line, int col, int length) {
        String message = String.format("no type definition found for '%s$%s'", owner, name);

        return new SourceCompilerError(message, source, line, col, length);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.CompilerError} representing an access to an
     * unassigned variable.
     *
     * @param var the unassigned variable.
     * @return the created {@link com.hjwylde.common.error.CompilerError}.
     */
    public static CompilerError unassignedVariableAccess(String var) {
        String message = String.format("access to unassigned variable '%s'", var);

        return new CompilerError(message);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.SourceCompilerError} representing an access to
     * an unassigned variable with the provided source information.
     *
     * @param var the unassigned variable.
     * @param source the source file name.
     * @param line the line number.
     * @param col the column number.
     * @param length the length.
     * @return the created {@link com.hjwylde.common.error.SourceCompilerError}.
     */
    public static SourceCompilerError unassignedVariableAccess(String var, String source, int line,
            int col, int length) {
        String message = String.format("access to unassigned variable '%s'", var);

        return new SourceCompilerError(message, source, line, col, length);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.CompilerError} representing an unreachable
     * statement (dead code).
     *
     * @param statement the unreachable statement.
     * @return the created {@link com.hjwylde.common.error.CompilerError}.
     */
    public static CompilerError unreachableStatement(String statement) {
        String message = String.format("unreachable statement detected, '%s'", statement);

        return new CompilerError(message);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.SourceCompilerError} representing an
     * unreachable statement (dead code) with the provided source information.
     *
     * @param statement the unreachable statement.
     * @param source the source file name.
     * @param line the line number.
     * @param col the column number.
     * @param length the length.
     * @return the created {@link com.hjwylde.common.error.SourceCompilerError}.
     */
    public static SourceCompilerError unreachableStatement(String statement, String source,
            int line, int col, int length) {
        String message = String.format("unreachable statement detected, '%s'", statement);

        return new SourceCompilerError(message, source, line, col, length);
    }
}
