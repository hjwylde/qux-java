package com.hjwylde.quxc.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.google.common.base.Joiner;
import com.google.common.io.CharStreams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 * @since 0.1.2
 */
public abstract class Harness {

    private static final Logger logger = LoggerFactory.getLogger(Harness.class);

    private static final String QUXC_EXECUTABLE = "build/install/quxc/bin/quxc";
    private static final String JAVA_EXECUTABLE = "java";
    private static final String TIMEOUT_EXECUTABLE = "timeout";

    private static final int TIMEOUT_EXIT_CODE = 124;

    private static final long QUXC_TIMEOUT = 4;
    private static final long JAVA_TIMEOUT = 4;
    private static final TimeUnit TIMEOUT_UNIT = TimeUnit.SECONDS;

    private static final String QUX_EXT = "qux";
    private static final String CLASS_EXT = "class";
    private static final String OUT_EXT = "out";

    private static final String QUXC_CLASSPATH;
    private static final String JAVA_CLASSPATH;

    private final Path root;

    static {
        // Grab all of the library files in our libraries directory
        final List<String> files = new ArrayList<>();
        SimpleFileVisitor sfv = new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                files.add(file.toAbsolutePath().toString());
                return FileVisitResult.CONTINUE;
            }
        };

        Path libsPath = Paths.get("build/install/quxc/lib/");

        try {
            Files.walkFileTree(libsPath, sfv);
        } catch (IOException e) {
            logger.error("error", e);

            throw new InternalError(e.getMessage());
        }

        StringBuilder sb = new StringBuilder();
        sb.append(".").append(File.pathSeparator);
        sb.append(Joiner.on(File.pathSeparator).join(files));

        logger.debug("setting test classpath to '{}'", sb);

        QUXC_CLASSPATH = "";
        JAVA_CLASSPATH = sb.toString();
    }

    public Harness(String root) {
        this(Paths.get(root));
    }

    public Harness(Path root) {
        this.root = Paths.get("src/test/resources/").resolve(root).toAbsolutePath().normalize();
    }

    protected final void compile(final String id) throws IOException {
        long timeout = TimeUnit.SECONDS.convert(QUXC_TIMEOUT, TIMEOUT_UNIT);
        String[] args = new String[] {TIMEOUT_EXECUTABLE, String.valueOf(timeout), QUXC_EXECUTABLE,
                "-cp", QUXC_CLASSPATH, "-od", root.toString(), getTestPath(id, QUX_EXT).toString()};

        ProcessBuilder pb = new ProcessBuilder(args);
        final Process quxc = pb.start();

        try {
            int result = quxc.waitFor();

            if (result == TIMEOUT_EXIT_CODE) {
                fail("qux compilation timed out after " + QUXC_TIMEOUT + " " + TIMEOUT_UNIT
                        .toString().toLowerCase(Locale.ENGLISH));
            }

            List<String> output = new ArrayList<>();
            output.addAll(CharStreams.readLines(new InputStreamReader(quxc.getInputStream(),
                    StandardCharsets.UTF_8)));
            output.addAll(CharStreams.readLines(new InputStreamReader(quxc.getErrorStream(),
                    StandardCharsets.UTF_8)));

            if (!output.isEmpty()) {
                fail(Joiner.on("\n").join(output));
            }
        } catch (InterruptedException e) {
            fail("qux compilation interrupted");
        } finally {
            quxc.destroy();
        }
    }

    protected final List<String> execute(final String id) throws IOException, ExecutionException {
        long timeout = TimeUnit.SECONDS.convert(JAVA_TIMEOUT, TIMEOUT_UNIT);
        String[] args = new String[] {TIMEOUT_EXECUTABLE, String.valueOf(timeout), JAVA_EXECUTABLE,
                "-cp", JAVA_CLASSPATH, getTestRelativePath(id).toString()};

        ProcessBuilder pb = new ProcessBuilder(args);
        pb.directory(root.toFile());
        final Process java = pb.start();

        try {
            int result = java.waitFor();

            if (result == TIMEOUT_EXIT_CODE) {
                fail("java execution timed out after " + JAVA_TIMEOUT + " " + TIMEOUT_UNIT
                        .toString().toLowerCase(Locale.ENGLISH));
            }

            List<String> err = CharStreams.readLines(new InputStreamReader(java.getErrorStream(),
                    StandardCharsets.UTF_8));

            if (!err.isEmpty()) {
                fail(Joiner.on("\n").join(err));
            }

            return CharStreams.readLines(new InputStreamReader(java.getInputStream(),
                    StandardCharsets.UTF_8));
        } catch (InterruptedException e) {
            fail("java execution interrupted");
            throw new InternalError("DEADCODE");
        } finally {
            java.destroy();
        }
    }

    protected final String getTestDirectory(String id) {
        if (id.contains(".")) {
            return id.substring(0, id.lastIndexOf(".")).replace(".", File.separator);
        }

        return ".";
    }

    protected final String getTestName(String id) {
        if (id.contains(".")) {
            return id.substring(id.lastIndexOf(".") + 1);
        }

        return id;
    }

    protected final Path getTestPath(String id, String ext) {
        return root.resolve(getTestRelativePath(id, ext));
    }

    protected final Path getTestPath(String id) {
        return root.resolve(getTestRelativePath(id));
    }

    protected final Path getTestRelativePath(String id) {
        return Paths.get(getTestDirectory(id) + File.separatorChar + getTestName(id)).normalize();
    }

    protected final Path getTestRelativePath(String id, String ext) {
        return Paths.get(getTestDirectory(id) + File.separatorChar + getTestName(id) + "." + ext)
                .normalize();
    }

    protected void run(String id) {
        try {
            compile(id);
            List<String> received = execute(id);
            List<String> expected = Files.readAllLines(getTestPath(id, OUT_EXT),
                    StandardCharsets.UTF_8);

            assertEquals("mismatch in test output, ", expected, received);
        } catch (ExecutionException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
