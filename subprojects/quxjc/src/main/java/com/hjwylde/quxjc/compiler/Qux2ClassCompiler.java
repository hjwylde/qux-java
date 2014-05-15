package com.hjwylde.quxjc.compiler;

import com.hjwylde.common.util.LoggerUtils;
import com.hjwylde.qbs.builder.BuildResult;
import com.hjwylde.qbs.builder.QuxContext;
import com.hjwylde.qbs.builder.QuxProject;
import com.hjwylde.qbs.compiler.Compiler;
import com.hjwylde.qbs.compiler.QuxCompileSpec;
import com.hjwylde.qux.pipelines.ControlFlowGraphPropagator;
import com.hjwylde.qux.pipelines.DeadCodeChecker;
import com.hjwylde.qux.pipelines.Pipeline;
import com.hjwylde.qux.pipelines.TypeChecker;
import com.hjwylde.qux.pipelines.TypePropagator;
import com.hjwylde.quxjc.builder.Qux2ClassBuilder;

import com.google.common.collect.ImmutableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 */
public final class Qux2ClassCompiler<T extends QuxCompileSpec> implements Compiler<T> {

    private static final Logger logger = LoggerFactory.getLogger(Qux2ClassCompiler.class);

    private static final ImmutableList<Class<? extends Pipeline>> DEFAULT_PIPELINES =
            ImmutableList.<Class<? extends Pipeline>>of(ControlFlowGraphPropagator.class,
                    //                    DefiniteAssignmentChecker.class,
                    DeadCodeChecker.class, TypePropagator.class, TypeChecker.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(T spec) {
        setLogLevel(spec.getOptions().isVerbose());

        QuxProject project = QuxProject.builder(spec).build();
        QuxContext context = new QuxContext(project);
        Qux2ClassBuilder builder = new Qux2ClassBuilder(context, DEFAULT_PIPELINES);

        Map<Path, BuildResult> results = builder.build(spec.getSource());

        for (Map.Entry<Path, BuildResult> result : results.entrySet()) {
            handleResult(spec, result.getKey(), result.getValue());
        }
    }

    private static void handleResult(QuxCompileSpec spec, Path path, BuildResult result) {
        if (result.isSuccess()) {
            return;
        }

        logger.error("{}: {}", path, result.getCode());

        try {
            List<String> source = Files.readAllLines(path, spec.getOptions().getCharset());

            LoggerUtils.logError(result.getCause(), source);
        } catch (IOException e) {
            LoggerUtils.logError(result.getCause());
        }
    }

    private static void setLogLevel(boolean verbose) {
        // TODO: Implement a solution for verbose mode
        // One idea is to have different loggers and to change which one is being used based on the
        // verbose mode
        // Another idea is to use markers, have a marker that turns verbose mode on
        if (verbose) {
            logger.warn("verbose mode is not currently supported");
        }
    }
}