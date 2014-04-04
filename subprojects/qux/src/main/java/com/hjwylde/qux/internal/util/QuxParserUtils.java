package com.hjwylde.qux.internal.util;

import com.hjwylde.qux.internal.antlr.QuxLexer;
import com.hjwylde.qux.internal.antlr.QuxParser;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.TokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Path;

/**
 * {@code Antlr} parser utilities for creating a {@link com.hjwylde.qux.internal.antlr.QuxParser}.
 *
 * @author Henry J. Wylde
 */
public final class QuxParserUtils {

    private static final Logger logger = LoggerFactory.getLogger(QuxParserUtils.class);

    /**
     * This class cannot be instantiated.
     */
    private QuxParserUtils() {}

    /**
     * Creates a new {@link com.hjwylde.qux.internal.antlr.QuxParser} using the given input stream
     * and character set.
     *
     * @param in the input stream.
     * @param charset the character set.
     * @return the Qux parser.
     * @throws IOException if the input stream cannot be read.
     */
    public static QuxParser createParser(InputStream in, Charset charset) throws IOException {
        return createParser(new InputStreamReader(in, charset));
    }

    /**
     * Creates a new {@link com.hjwylde.qux.internal.antlr.QuxParser} using the given reader.
     *
     * @param reader the reader.
     * @return the Qux parser.
     * @throws IOException if the reader cannot be read.
     */
    public static QuxParser createParser(Reader reader) throws IOException {
        return createParser(new ANTLRInputStream(reader));
    }

    /**
     * Creates a new {@link com.hjwylde.qux.internal.antlr.QuxParser} using the given character
     * stream.
     *
     * @param stream the character stream.
     * @return the Qux parser.
     */
    public static QuxParser createParser(CharStream stream) {
        QuxLexer lexer = new QuxLexer(stream);
        TokenStream tokenStream = new BufferedTokenStream(lexer);

        return new QuxParser(tokenStream);
    }

    /**
     * Creates a new {@link com.hjwylde.qux.internal.antlr.QuxParser} using the given file and
     * encoding. This method will attempt to open a new input stream to read the given file.
     *
     * @param fileName the file name to read.
     * @param encoding the character set encoding to read the file with.
     * @return the Qux parser.
     * @throws IOException if the file cannot be read.
     */
    public static QuxParser createParser(String fileName, String encoding) throws IOException {
        return createParser(new ANTLRFileStream(fileName, encoding));
    }

    /**
     * Creates a new {@link com.hjwylde.qux.internal.antlr.QuxParser} using the given input.
     *
     * @param input the input.
     * @return the Qux parser.
     */
    public static QuxParser createParser(String input) {
        try {
            return createParser(new StringReader(input));
        } catch (IOException e) {
            logger.error("should never occur", e);
            throw new InternalError(e.getMessage());
        }
    }

    /**
     * Creates a new {@link com.hjwylde.qux.internal.antlr.QuxParser} using the given file and
     * character set. This method will attempt to open a new input stream to read the given file.
     *
     * @param fileName the file name to read.
     * @param charset the character set to read the file with.
     * @return the Qux parser.
     * @throws IOException if the file cannot be read.
     */
    public static QuxParser createParser(String fileName, Charset charset) throws IOException {
        return createParser(fileName, charset.name());
    }

    /**
     * Creates a new {@link com.hjwylde.qux.internal.antlr.QuxParser} using the given file path and
     * character set. This method will attempt to open a new input stream to read the given file
     * path.
     *
     * @param path the file path to read.
     * @param encoding the character set encoding to read the file with.
     * @return the Qux parser.
     * @throws IOException if the file cannot be read.
     */
    public static QuxParser createParser(Path path, String encoding) throws IOException {
        return createParser(path.toString(), encoding);
    }

    /**
     * Creates a new {@link com.hjwylde.qux.internal.antlr.QuxParser} from the given file path and
     * character set.
     *
     * @param path the file path to read.
     * @param charset the character set to read the file with.
     * @return the Qux parser.
     * @throws IOException if the file cannot be read.
     */
    public static QuxParser createParser(Path path, Charset charset) throws IOException {
        return createParser(path.toString(), charset.name());
    }
}

