package com.hjwylde.quxjc.builder;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import com.hjwylde.common.error.BuildError;
import com.hjwylde.qbs.builder.BuildJob;
import com.hjwylde.qbs.builder.BuildResult;
import com.hjwylde.qbs.builder.QuxContext;
import com.hjwylde.qux.api.CheckQuxAdapter;
import com.hjwylde.qux.pipelines.Pipeline;
import com.hjwylde.qux.tree.QuxNode;
import com.hjwylde.quxjc.compiler.MainFunctionInjector;
import com.hjwylde.quxjc.compiler.Qux2ClassTranslater;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 */
public final class Qux2ClassBuildJob extends BuildJob {

    private static final Logger logger = LoggerFactory.getLogger(Qux2ClassBuildJob.class);

    private final Path source;

    private final QuxContext context;

    private final ImmutableList<Class<? extends Pipeline>> pipelines;

    private QuxNode node;

    /**
     * Creates a new {@code Qux2ClassBuildJob} with the given path, context and pipelines. The
     * pipelines are the different stages that the compilation goes through before the final
     * translation stage.
     *
     * @param source the source path.
     * @param context the context.
     * @param pipelines the pipelines.
     */
    public Qux2ClassBuildJob(Path source, QuxContext context,
            List<Class<? extends Pipeline>> pipelines) {
        this.source = checkNotNull(source, "source cannot be null");

        this.context = checkNotNull(context, "context cannot be null");

        this.pipelines = ImmutableList.copyOf(pipelines);
    }

    public void setQuxNode(QuxNode node) {
        this.node = checkNotNull(node, "node cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BuildResult build() {
        logger.debug("{}: building", source);

        try {
            Stopwatch stopwatch = Stopwatch.createStarted();

            // Apply the pipelines
            for (Class<? extends Pipeline> clazz : pipelines) {
                node = apply(clazz, node);
            }

            // Translate the file to the java bytecode format
            byte[] bytecode = translate(node);

            // Write out the Java bytecode
            write(node, bytecode);

            logger.debug("{}: building finished in {}", source, stopwatch);
        } catch (IOException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new BuildError(e.getMessage(), e);
        }

        return BuildResult.success();
    }

    private QuxNode apply(Class<? extends Pipeline> clazz, QuxNode node)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
            InstantiationException {
        logger.debug("{}: applying pipeline '{}'", source, clazz.getSimpleName());

        Stopwatch stopwatch = Stopwatch.createStarted();

        Pipeline pipeline = clazz.getConstructor(QuxContext.class).newInstance(context);

        // Apply the pipeline and update the node
        node = pipeline.apply(node);

        stopwatch.stop();

        logger.debug("{}: application finished in {}", source, stopwatch);

        return node;
    }

    /**
     * Generates a path based on the given output directory, id and extension. The id is treated as
     * a {@code .} separated sequence of identifiers. It is first translated by replacing all the
     * {@code .}s with {@code java.io.File#separator}s.
     *
     * @param outdir the output directory the path should be resolved from.
     * @param id the id of the output file.
     * @param extension the extension of the output file.
     * @return the generated path.
     */
    private static Path generatePath(Path outdir, String id, String extension) {
        return outdir.resolve(id.replace(".", File.separator) + "." + extension);
    }

    private byte[] translate(QuxNode node) {
        logger.debug("{}: translating to java bytecode", source);

        Stopwatch stopwatch = Stopwatch.createStarted();

        // Create the class writer, main function injector and check adapter
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        MainFunctionInjector mfi = new MainFunctionInjector(cw, source.toString());
        CheckClassAdapter cca = new CheckClassAdapter(mfi, false);

        // Create the translator and let it visit the node
        Qux2ClassTranslater q2ct = new Qux2ClassTranslater(source.toString(), cca);
        CheckQuxAdapter cqa = new CheckQuxAdapter(q2ct);
        node.accept(cqa);

        stopwatch.stop();

        logger.debug("{}: translation finished in {}", source, stopwatch);

        return cw.toByteArray();
    }

    private void write(QuxNode node, byte[] bytecode) throws IOException {
        logger.debug("{}: writing java bytecode out", source);

        Stopwatch stopwatch = Stopwatch.createStarted();

        Path outpath = generatePath(context.getProject().getOutdir(), node.getId(), "class");

        logger.debug("{}: writing to {}", source, outpath);

        Files.createDirectories(outpath.getParent());

        Files.write(outpath, bytecode, CREATE, WRITE, TRUNCATE_EXISTING);

        stopwatch.stop();

        logger.debug("{}: writing finished in {}", source, stopwatch);
    }
}
