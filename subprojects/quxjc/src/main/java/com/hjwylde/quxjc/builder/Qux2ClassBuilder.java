package com.hjwylde.quxjc.builder;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.common.error.BuildError;
import com.hjwylde.common.error.BuildErrors;
import com.hjwylde.common.error.CompilerError;
import com.hjwylde.qbs.builder.BuildResult;
import com.hjwylde.qbs.builder.Builder;
import com.hjwylde.qbs.builder.QuxContext;
import com.hjwylde.qbs.builder.resources.Resource;
import com.hjwylde.qbs.builder.resources.ResourceManager;
import com.hjwylde.qux.api.QuxReader;
import com.hjwylde.qux.builder.resources.LocalQuxResourceReader;
import com.hjwylde.qux.builder.resources.QuxResource;
import com.hjwylde.qux.pipelines.Pipeline;
import com.hjwylde.qux.tree.QuxNode;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class Qux2ClassBuilder implements Builder {

    private static final Logger logger = LoggerFactory.getLogger(Qux2ClassBuilder.class);

    private final QuxContext context;

    private final ImmutableList<Class<? extends Pipeline>> pipelines;

    /**
     * Creates a new {@code Qux2ClassBuilder} with the given context and pipelines. The pipelines
     * are the different stages that the compilation goes through before the final translation
     * stage.
     *
     * @param context the context.
     * @param pipelines the pipelines.
     */
    public Qux2ClassBuilder(QuxContext context, List<Class<? extends Pipeline>> pipelines) {
        this.context = checkNotNull(context, "context cannot be null");

        this.pipelines = ImmutableList.copyOf(pipelines);

        // Register the resource reader to use the charset from the project options
        Charset charset = context.getProject().getOptions().getCharset();
        Resource.Reader<QuxResource> reader = new LocalQuxResourceReader(charset);
        ResourceManager.register(QuxResource.EXTENSION, reader);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Path, BuildResult> build(Set<Path> source) {
        logger.info("building {} source file(s)", source.size());

        Stopwatch stopwatch = Stopwatch.createStarted();

        // Build all the files concurrently
        int threads = Runtime.getRuntime().availableProcessors() * 2;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CompletionService<BuildResult> completion = new ExecutorCompletionService<BuildResult>(
                executor);

        logger.info("compiling {} source file(s) concurrently with {} worker(s)", source.size(),
                threads);

        Map<Path, BuildResult> results = new HashMap<>();

        // Need to parse all source files and add them as resources to the context for use in the build process
        Map<Path, Qux2ClassBuildJob> intermediate = new HashMap<>();
        for (Path path : source) {
            Qux2ClassBuildJob job = new Qux2ClassBuildJob(path, context, pipelines);

            // Try to parse the source file and add the result to the context
            // If it can't be parsed, then ignore it and put the result as a failed job
            try {
                QuxNode node = parse(path);
                job.setQuxNode(node);

                intermediate.put(path, job);

                context.addResources(new QuxResource(node));
            } catch (IOException e) {
                results.put(path, BuildResult.fail(new BuildError(e.getMessage(), e)));
            } catch (BuildError | CompilerError e) {
                results.put(path, BuildResult.fail(e));
            } catch (InternalError | RuntimeException e) {
                results.put(path, BuildResult.internalError(e));
            }
        }

        // All the files have been added as resources, now we can actually start each build process concurrently
        Map<Path, Future<BuildResult>> jobs = new HashMap<>();
        for (Map.Entry<Path, Qux2ClassBuildJob> job : intermediate.entrySet()) {
            jobs.put(job.getKey(), completion.submit(job.getValue()));
        }

        // Wait for each job to finish and gather up the results
        for (Map.Entry<Path, Future<BuildResult>> job : jobs.entrySet()) {
            BuildResult result = null;
            try {
                if (getTimeout() > 0) {
                    result = job.getValue().get(getTimeout(), getTimeoutUnit());
                } else {
                    result = job.getValue().get();
                }
            } catch (InterruptedException | ExecutionException | CancellationException e) {
                result = BuildResult.internalError(e);
            } catch (TimeoutException e) {
                result = BuildResult.fail(BuildErrors.buildTimeout(getTimeout(), getTimeoutUnit()));
            }
            results.put(job.getKey(), result);

            logger.debug("{}: build result ({})", job.getKey(), result.getCode());
        }

        stopwatch.stop();

        logger.info("build finished in {}", stopwatch);

        return results;
    }

    private Long getTimeout() {
        return context.getProject().getOptions().getTimeout();
    }

    private TimeUnit getTimeoutUnit() {
        return context.getProject().getOptions().getTimeoutUnit();
    }

    /**
     * Parses the source file.
     *
     * @param source the source file parse.
     * @throws java.io.IOException if the source file cannot be read.
     */
    private QuxNode parse(Path source) throws IOException {
        logger.debug("{}: parsing", source);

        Stopwatch stopwatch = Stopwatch.createStarted();

        QuxNode node = new QuxNode();

        Charset charset = context.getProject().getOptions().getCharset();
        new QuxReader(source, charset).accept(node);

        stopwatch.stop();

        logger.debug("{}: parsing finished in {}", source, stopwatch);

        return node;
    }
}
