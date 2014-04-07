package com.hjwylde.quxc.compiler;

import com.hjwylde.qbs.builder.BuildResult;
import com.hjwylde.qbs.builder.Context;
import com.hjwylde.qbs.compiler.Compiler;
import com.hjwylde.qux.internal.util.LoggerUtils;
import com.hjwylde.quxc.builder.Qux2ClassBuilder;
import com.hjwylde.quxc.builder.QuxProject;

import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 */
public final class QuxCompiler implements Compiler<QuxCompileSpec> {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(QuxCompiler.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(QuxCompileSpec spec) {
        setLogLevel(spec.getOptions().isVerbose());

        QuxProject project = QuxProject.builder(spec).build();
        // TODO: Make a QuxContext so we don't need to always cast for the QuxProject
        Context context = new Context(project);
        Qux2ClassBuilder builder = new Qux2ClassBuilder(context);

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
        Logger rootLogger = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

        if (verbose) {
            // Only set the log level if it hasn't been overwritten by a properties file
            if (rootLogger.getLevel().isGreaterOrEqual(Level.INFO)) {
                rootLogger.setLevel(Level.INFO);
            }
        }
    }
}
