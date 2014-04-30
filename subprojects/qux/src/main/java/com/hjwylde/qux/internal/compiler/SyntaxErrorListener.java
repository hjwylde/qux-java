package com.hjwylde.qux.internal.compiler;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.common.error.SourceCompilerError;
import com.hjwylde.common.util.LoggerUtils;

import com.google.common.collect.ImmutableList;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class SyntaxErrorListener extends BaseErrorListener {

    private final String source;

    private final List<SourceCompilerError> errors = new ArrayList<>();

    public SyntaxErrorListener(String source) {
        this.source = checkNotNull(source, "source cannot be null");
    }

    public ImmutableList<SourceCompilerError> getSyntaxErrors() {
        return ImmutableList.copyOf(errors);
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, @Nullable Object offendingSymbol, int line,
            int charPositionInLine, String msg, @Nullable RecognitionException e) {
        if (offendingSymbol == null) {
            LoggerUtils.logAssert("offendingSymbol is null", e);
        }

        Token token = (Token) offendingSymbol;

        int length = (token.getStopIndex() + 1) - token.getStartIndex();

        if (e != null) {
            errors.add(new SourceCompilerError(msg, e, source, line, charPositionInLine, length));
        } else {
            errors.add(new SourceCompilerError(msg, source, line, charPositionInLine, length));
        }
    }
}
