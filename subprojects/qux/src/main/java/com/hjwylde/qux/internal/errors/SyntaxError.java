package com.hjwylde.qux.internal.errors;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.common.error.CompilerError;

import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import javax.annotation.Nullable;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class SyntaxError extends CompilerError {

    private static final long serialVersionUID = 1L;

    private final Recognizer<?, ?> recognizer;
    private final Object offendingSymbol;
    private final int line;
    private final int charPositionInLine;
    private final String msg;
    private final RecognitionException recognitionException;

    public SyntaxError(Recognizer<?, ?> recognizer, @Nullable Object offendingSymbol, int line,
            int charPositionInLine, String msg, @Nullable RecognitionException e) {
        super(msg);

        this.recognizer = checkNotNull(recognizer, "recognizer cannot be null");
        this.offendingSymbol = offendingSymbol;
        this.line = line;
        this.charPositionInLine = charPositionInLine;
        this.msg = checkNotNull(msg, "msg cannot be null");
        recognitionException = e;
    }
}

