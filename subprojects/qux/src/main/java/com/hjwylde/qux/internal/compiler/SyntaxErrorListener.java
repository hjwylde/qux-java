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
 * A listener that records all reported errors and allows retrieval of them. Upon recording, the
 * error is translated into a {@link com.hjwylde.common.error.SourceCompilerError}.
 *
 * @author Henry J. Wylde
 */
public final class SyntaxErrorListener extends BaseErrorListener {

    private final String source;

    private final List<SourceCompilerError> errors = new ArrayList<>();

    /**
     * Creates a new {@code SyntaxErrorListener} with the given source file name. The source file
     * name should include the extension (if applicable).
     *
     * @param source the source file name (inclusive of extension).
     */
    public SyntaxErrorListener(String source) {
        this.source = checkNotNull(source, "source cannot be null");
    }

    /**
     * Gets the recorded errors.
     *
     * @return the recorded errors.
     */
    public ImmutableList<SourceCompilerError> getSyntaxErrors() {
        return ImmutableList.copyOf(errors);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, @Nullable Object offendingSymbol, int line,
            int charPositionInLine, String msg, @Nullable RecognitionException e) {
        // Unsure on when this case occurs
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
