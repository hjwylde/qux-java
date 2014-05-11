package com.hjwylde.quxc.builder;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.common.error.BuildErrors;
import com.hjwylde.qbs.builder.BuildResult;
import com.hjwylde.qbs.builder.Builder;
import com.hjwylde.qux.pipelines.Pipeline;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Path, BuildResult> build(Set<Path> source) {
        logger.info("building {} source file(s)", source.size());

        Stopwatch stopwatch = Stopwatch.createStarted();

        // Build all the files concurrently
        int threads = Runtime.getRuntime().availableProcessors() * 4;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CompletionService<BuildResult> completion = new ExecutorCompletionService<BuildResult>(
                executor);

        logger.info("compiling {} source file(s) concurrently with {} worker(s)", source.size(),
                threads);

        Map<Path, Future<BuildResult>> jobs = new HashMap<>();

        for (Path path : source) {
            Qux2ClassBuildJob job = new Qux2ClassBuildJob(path, context, pipelines);

            Future<BuildResult> future = completion.submit(job);
            jobs.put(path, future);
        }

        Map<Path, BuildResult> results = new HashMap<>();
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
}
