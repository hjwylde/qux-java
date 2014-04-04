package com.hjwylde.qux.internal.compiler;

import com.hjwylde.qux.internal.errors.SyntaxError;

import com.google.common.collect.ImmutableList;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class SyntaxErrorListener extends BaseErrorListener {

    private final List<SyntaxError> errors = new ArrayList<>();

    public ImmutableList<SyntaxError> getSyntaxErrors() {
        return ImmutableList.copyOf(errors);
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, @Nullable Object offendingSymbol, int line,
            int charPositionInLine, String msg, @Nullable RecognitionException e) {
        errors.add(new SyntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e));
    }
}
