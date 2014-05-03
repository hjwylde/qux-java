package com.hjwylde.quxc.tests;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.google.common.base.Joiner;
import com.google.common.io.CharStreams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

    protected void compile(final String id) throws IOException, ExecutionException {
        String[] args =
                new String[] {QUXC_EXECUTABLE, "-cp", QUXC_CLASSPATH, "-od", root.toString(),
                        getTestPath(id, QUX_EXT).toString()};

        ProcessBuilder pb = new ProcessBuilder(args);
        FutureProcess quxc = new FutureProcess(pb.start());

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(quxc);

        try {
            quxc.get(QUXC_TIMEOUT, TIMEOUT_UNIT);
        } catch (InterruptedException e) {
            fail("qux compilation interrupted");
        } catch (TimeoutException e) {
            fail("qux compilation timed out after " + QUXC_TIMEOUT + " " + TIMEOUT_UNIT.toString()
                    .toLowerCase(Locale.ENGLISH));
        } finally {
            if (!quxc.isDone()) {
                quxc.cancel(true);
                quxc.destroy();
            }
        }

        if (quxc.isCancelled()) {
            fail("qux compilation cancelled");
        }

        List<String> output = new ArrayList<>();
        output.addAll(CharStreams.readLines(new InputStreamReader(quxc.getInputStream(),
                StandardCharsets.UTF_8)));
        output.addAll(CharStreams.readLines(new InputStreamReader(quxc.getErrorStream(),
                StandardCharsets.UTF_8)));

        if (!output.isEmpty()) {
            fail(Joiner.on("\n").join(output));
        }
    }

    protected List<String> execute(final String id) throws IOException, ExecutionException {
        String[] args = new String[] {JAVA_EXECUTABLE, "-cp", JAVA_CLASSPATH, getTestRelativePath(
                id).toString()};

        ProcessBuilder pb = new ProcessBuilder(args);
        pb.directory(root.toFile());
        FutureProcess java = new FutureProcess(pb.start());

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(java);

        try {
            java.get(JAVA_TIMEOUT, TIMEOUT_UNIT);
        } catch (InterruptedException e) {
            fail("java execution interrupted");
        } catch (TimeoutException e) {
            fail("java execution timed out after " + JAVA_TIMEOUT + " " + TIMEOUT_UNIT.toString()
                    .toLowerCase(Locale.ENGLISH));
        } finally {
            if (!java.isDone()) {
                java.cancel(true);
            }
        }

        if (java.isCancelled()) {
            fail("java execution cancelled");
        }

        List<String> err = CharStreams.readLines(new InputStreamReader(java.getErrorStream(),
                StandardCharsets.UTF_8));

        if (!err.isEmpty()) {
            fail(Joiner.on("\n").join(err));
        }

        return CharStreams.readLines(new InputStreamReader(java.getInputStream(),
                StandardCharsets.UTF_8));
    }

    protected String getTestDirectory(String id) {
        if (id.contains(".")) {
            return id.substring(0, id.lastIndexOf(".")).replace(".", File.separator);
        }

        return ".";
    }

    protected String getTestName(String id) {
        if (id.contains(".")) {
            return id.substring(id.lastIndexOf(".") + 1);
        }

        return id;
    }

    protected Path getTestPath(String id, String ext) {
        return root.resolve(getTestRelativePath(id, ext));
    }

    protected Path getTestPath(String id) {
        return root.resolve(getTestRelativePath(id));
    }

    protected Path getTestRelativePath(String id) {
        return Paths.get(getTestDirectory(id) + File.separatorChar + getTestName(id)).normalize();
    }

    protected Path getTestRelativePath(String id, String ext) {
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

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     * @since 0.1.2
     */
    private static final class FutureProcess extends FutureTask<Integer> {

        private final Process process;

        public FutureProcess(final Process process) {
            super(new Callable<Integer>() {
                @Override
                public Integer call() throws InterruptedException {
                    return process.waitFor();
                }
            });

            this.process = checkNotNull(process, "process cannot be null");
        }

        public void destroy() {
            process.destroy();
        }

        public InputStream getErrorStream() {
            return process.getErrorStream();
        }

        public InputStream getInputStream() {
            return process.getInputStream();
        }
    }
}
