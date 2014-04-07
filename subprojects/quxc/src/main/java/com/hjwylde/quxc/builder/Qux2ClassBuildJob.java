package com.hjwylde.quxc.builder;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import com.hjwylde.common.error.BuildError;
import com.hjwylde.qbs.builder.BuildJob;
import com.hjwylde.qbs.builder.BuildResult;
import com.hjwylde.qbs.builder.Context;
import com.hjwylde.qux.api.CheckQuxAdapter;
import com.hjwylde.qux.api.DefiniteAssignmentChecker;
import com.hjwylde.qux.api.QuxReader;
import com.hjwylde.qux.api.QuxVisitor;
import com.hjwylde.qux.api.TypeChecker;
import com.hjwylde.qux.tree.QuxNode;
import com.hjwylde.quxc.compiler.MainFunctionInjector;
import com.hjwylde.quxc.compiler.Qux2ClassTranslater;

import com.google.common.base.Stopwatch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 */
public final class Qux2ClassBuildJob extends BuildJob {

    private static final Logger logger = LoggerFactory.getLogger(Qux2ClassBuildJob.class);

    private final Path source;

    private final Context context;

    public Qux2ClassBuildJob(Path source, Context context) {
        this.source = checkNotNull(source, "source cannot be null");

        this.context = checkNotNull(context, "context cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BuildResult build() {
        logger.debug("{}: building", source);

        try {
            Stopwatch stopwatch = Stopwatch.createStarted();

            QuxNode node = new QuxNode();

            // Check that the input is a valid representation of the qux grammar
            parse(node);

            // Verify the semantics of the qux file
            verify(node);

            // Translate the file to the java bytecode format
            byte[] bytecode = translate(node);

            // Write out the Java bytecode
            write(bytecode);

            logger.debug("{}: building finished in {}", source, stopwatch);
        } catch (IOException e) {
            throw new BuildError(e);
        }

        return BuildResult.success();
    }

    protected final String getFileName() {
        return com.google.common.io.Files.getNameWithoutExtension(source.toString());
    }

    private static Path generateOutpath(Path outdir, String name, String extension) {
        return outdir.resolve(name + "." + extension);
    }

    private void parse(QuxVisitor qv) throws IOException {
        logger.debug("{}: parsing", source);

        Stopwatch stopwatch = Stopwatch.createStarted();

        Charset charset = ((QuxProject) context.getProject()).getOptions().getCharset();
        new QuxReader(source, charset).accept(qv);

        stopwatch.stop();

        logger.debug("{}: parsing finished in {}", source, stopwatch);
    }

    private byte[] translate(QuxNode node) {
        // TODO: Make a consistent name of "fileName" or "name" or "identifier" for stuffs
        logger.debug("{}: translating to java bytecode", source);

        Stopwatch stopwatch = Stopwatch.createStarted();

        // Create the class writer, main function injector and check adapter
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        MainFunctionInjector mfi = new MainFunctionInjector(cw, node.getName());
        CheckClassAdapter cca = new CheckClassAdapter(mfi, false);

        // Create the translator and let it visit the node
        Qux2ClassTranslater q2ct = new Qux2ClassTranslater(node.getName(), cca);
        CheckQuxAdapter cqa = new CheckQuxAdapter(q2ct);
        node.accept(cqa);

        stopwatch.stop();

        logger.debug("{}: translation finished in {}", source, stopwatch);

        return cw.toByteArray();
    }

    private void verify(QuxNode node) {
        logger.debug("{}: verifying", source);

        Stopwatch stopwatch = Stopwatch.createStarted();

        TypeChecker tc = new TypeChecker();
        DefiniteAssignmentChecker dac = new DefiniteAssignmentChecker(tc);

        node.accept(dac);

        stopwatch.stop();

        logger.debug("{}: verification finished in {}", source, stopwatch);
    }

    private void write(byte[] bytecode) throws IOException {
        logger.debug("{}: writing java bytecode out", source);

        Stopwatch stopwatch = Stopwatch.createStarted();

        Path outpath = generateOutpath(context.getProject().getOutdir(), getFileName().toString(),
                "class");

        logger.debug("{}: writing to {}", source, outpath);

        Files.createDirectories(outpath.getParent());

        Files.write(outpath, bytecode, CREATE, WRITE, TRUNCATE_EXISTING);

        stopwatch.stop();

        logger.debug("{}: writing finished in {}", source, stopwatch);
    }
}
