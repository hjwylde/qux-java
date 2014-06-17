package com.hjwylde.quxjc.compiler;

import com.hjwylde.common.util.LoggerUtils;
import com.hjwylde.qbs.builder.BuildResult;
import com.hjwylde.qbs.builder.QuxContext;
import com.hjwylde.qbs.builder.QuxProject;
import com.hjwylde.qbs.builder.resources.Resource;
import com.hjwylde.qbs.builder.resources.ResourceManager;
import com.hjwylde.qbs.compiler.Compiler;
import com.hjwylde.qbs.compiler.QuxCompileSpec;
import com.hjwylde.qux.pipelines.Pipeline;
import com.hjwylde.quxjc.builder.Qux2ClassBuilder;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import org.apache.log4j.Level;
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
            Pipeline.DEFAULT_PIPELINES;

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(T spec) {
        setVerbose(spec.getOptions().isVerbose());

        QuxProject project = QuxProject.builder(spec).build();
        QuxContext context = initialiseContext(project);
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

    private QuxContext initialiseContext(QuxProject project) {
        QuxContext context = new QuxContext(project);

        for (Path path : project.getClasspath()) {
            Optional<Resource> resource = ResourceManager.loadResource(path);

            if (resource.isPresent()) {
                context.addResources(resource.get());
            } else {
                logger.warn("classpath entry '{}' unable to be loaded", path);
            }
        }

        return context;
    }

    /**
     * Sets the compiler to be verbose or not.
     *
     * @param verbose true if the compiler should be verbose.
     */
    private static void setVerbose(boolean verbose) {
        if (verbose) {
            LoggerUtils.setLogLevel(Level.DEBUG);
        } else {
            LoggerUtils.setLogLevel(Level.WARN);
        }
    }
}
