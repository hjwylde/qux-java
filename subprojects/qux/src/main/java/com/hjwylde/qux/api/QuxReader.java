package com.hjwylde.qux.api;

import com.hjwylde.common.error.CompilerError;
import com.hjwylde.common.error.CompilerErrorList;
import com.hjwylde.qux.internal.antlr.QuxParser;
import com.hjwylde.qux.internal.compiler.Antlr2QuxTranslater;
import com.hjwylde.qux.internal.compiler.SyntaxErrorListener;
import com.hjwylde.qux.internal.util.QuxParserUtils;
import com.hjwylde.qux.tree.QuxNode;

import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Path;

/**
 * A reader for {@link com.hjwylde.qux.api.QuxVisitor}s. The reader will parse some input (of
 * arbitrary format) and generate a {@link com.hjwylde.qux.tree.QuxNode}. When {@link
 * #accept(QuxVisitor)} is called, the call is delegated to the newly created {@link
 * com.hjwylde.qux.tree.QuxNode}.
 *
 * @author Henry J. Wylde
 */
public final class QuxReader {

    private final QuxNode node = new QuxNode();

    /**
     * Creates a new {@code QuxReader} with the given input path and character set. The character
     * set is used to read the path's content.
     *
     * @param path the input path.
     * @param charset the character set.
     * @throws IOException if the input cannot be read.
     * @throws CompilerError if the input is invalid.
     */
    public QuxReader(Path path, Charset charset) throws IOException, CompilerError {
        this(QuxParserUtils.createParser(path, charset));
    }

    /**
     * Creates a new {@code QuxReader} with the given input.
     *
     * @param input the input.
     * @throws CompilerError if the input is invalid.
     */
    public QuxReader(String input) throws CompilerError {
        this(QuxParserUtils.createParser(input));
    }

    /**
     * Creates a new {@code QuxReader} with the given input stream and character set. The character
     * set is used to read the input stream's content.
     *
     * @param in the input stream.
     * @param charset the character set.
     * @throws IOException if the input stream cannot be read.
     * @throws CompilerError if the input is invalid.
     */
    public QuxReader(InputStream in, Charset charset) throws IOException, CompilerError {
        this(QuxParserUtils.createParser(in, charset));
    }

    /**
     * Creates a new {@code QuxReader} with the given reader.
     *
     * @param reader the reader.
     * @throws IOException if the reader cannot be read.
     * @throws CompilerError if the input is invalid.
     */
    public QuxReader(Reader reader) throws IOException, CompilerError {
        this(QuxParserUtils.createParser(reader));
    }

    /**
     * Creates a new {@code QuxReader} with the given parser. The parser is read fully and if the
     * input is invalid then a {@link com.hjwylde.common.error.CompilerError} is thrown.
     *
     * @param parser the parser.
     * @throws CompilerError if the input is invalid.
     */
    private QuxReader(QuxParser parser) throws CompilerError {
        // TODO: Test what happens when the parser doesn't have a source name
        String source = parser.getSourceName();
        if (source == null) {
            source = "<empty>";
        }

        // Use two-step parsing
        // See https://theantlrguy.atlassian.net/wiki/pages/viewpage.action?pageId=1900591

        SyntaxErrorListener syntaxErrorListener = new SyntaxErrorListener(source);
        // DiagnosticErrorListener diagnosticErrorListener = new DiagnosticErrorListener(false);

        QuxParser.StartContext start;
        try {
            parser.removeErrorListeners();

            parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
            parser.setErrorHandler(new BailErrorStrategy());

            start = parser.start();
        } catch (ParseCancellationException e) {
            parser.addErrorListener(syntaxErrorListener);
            // parser.addErrorListener(diagnosticErrorListener);

            parser.getInterpreter().setPredictionMode(PredictionMode.LL);
            parser.setErrorHandler(new DefaultErrorStrategy());

            parser.reset();

            start = parser.start();
        }

        // TODO: Work with the diagnostic error listener

        if (parser.getNumberOfSyntaxErrors() > 0) {
            throw new CompilerErrorList(syntaxErrorListener.getSyntaxErrors());
        }

        start.accept(new Antlr2QuxTranslater(source, node));
    }

    /**
     * Accepts the given {@link com.hjwylde.qux.api.QuxVisitor}. The visitor has the appropriate
     * methods called representing the inpu tthat this reader was created with.
     *
     * @param qv the visitor.
     */
    public void accept(QuxVisitor qv) {
        node.accept(qv);
    }
}

