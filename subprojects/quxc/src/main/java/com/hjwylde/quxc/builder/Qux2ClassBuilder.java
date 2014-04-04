package com.hjwylde.quxc.builder;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.qbs.builder.BuildResult;
import com.hjwylde.qbs.builder.Builder;
import com.hjwylde.qbs.builder.Context;

import com.google.common.base.Stopwatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class Qux2ClassBuilder implements Builder {

    private static final Logger logger = LoggerFactory.getLogger(Qux2ClassBuilder.class);

    private final Context context;

    public Qux2ClassBuilder(Context context) {
        this.context = checkNotNull(context, "context cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Path, BuildResult> build(Set<Path> source) {
        logger.info("building {} source file(s)", source.size());

        Stopwatch stopwatch = Stopwatch.createStarted();

        // Build all the files concurrently
        int threads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CompletionService completion = new ExecutorCompletionService(executor);

        logger.info("compiling {} source file(s) concurrently with {} worker(s)", source.size(),
                threads);


        Map<Path, Future<BuildResult>> jobs = new HashMap<>();

        for (Path path : source) {
            Qux2ClassBuildJob job = new Qux2ClassBuildJob(path, context);

            Future<BuildResult> future = completion.submit(job);
            jobs.put(path, future);
        }

        Map<Path, BuildResult> results = new HashMap<>();
        for (Map.Entry<Path, Future<BuildResult>> job : jobs.entrySet()) {
            BuildResult result = null;
            try {
                result = job.getValue().get();
            } catch (InterruptedException | ExecutionException | CancellationException e) {
                result = BuildResult.internalError(e);
            }
            results.put(job.getKey(), result);

            logger.debug("{}: build result ({})", job.getKey(), result.getCode());
        }

        stopwatch.stop();

        logger.info("build finished in {}", stopwatch);

        return results;
    }
}
