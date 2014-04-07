package com.hjwylde.qux.api;

import com.hjwylde.common.error.CompilerError;
import com.hjwylde.common.error.CompilerErrorList;
import com.hjwylde.qux.internal.antlr.QuxParser;
import com.hjwylde.qux.internal.compiler.Antlr2QuxTranslater;
import com.hjwylde.qux.internal.compiler.SyntaxErrorListener;
import com.hjwylde.qux.internal.util.QuxParserUtils;
import com.hjwylde.qux.tree.QuxNode;

import com.google.common.io.Files;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Path;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 */
public final class QuxReader {

    private final QuxNode node = new QuxNode();

    public QuxReader(Path path, Charset charset) throws IOException, CompilerError {
        this(QuxParserUtils.createParser(path, charset));
    }

    public QuxReader(String input) throws IOException, CompilerError {
        this(QuxParserUtils.createParser(input));
    }

    public QuxReader(InputStream in, Charset charset) throws IOException, CompilerError {
        this(QuxParserUtils.createParser(in, charset));
    }

    public QuxReader(Reader reader) throws IOException, CompilerError {
        this(QuxParserUtils.createParser(reader));
    }

    private QuxReader(QuxParser parser) throws CompilerError {
        // TODO: Test what happens when the parser doesn't have a source name
        String source = Files.getNameWithoutExtension(parser.getSourceName());

        SyntaxErrorListener listener = new SyntaxErrorListener(source);
        parser.removeErrorListeners();
        parser.addErrorListener(listener);

        QuxParser.StartContext start = parser.start();

        if (parser.getNumberOfSyntaxErrors() > 0) {
            throw new CompilerErrorList(listener.getSyntaxErrors());
        }

        start.accept(new Antlr2QuxTranslater(source, node));
    }

    public void accept(QuxVisitor qv) {
        node.accept(qv);
    }
}

