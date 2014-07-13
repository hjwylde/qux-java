package com.hjwylde.quxjc.tests.valid;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.quxjc.tests.Harness;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Valid {@code const} tests.
 *
 * @author Henry J. Wylde
 * @since 0.2.2
 */
@RunWith(Parameterized.class)
public final class ValidConstTests extends Harness {

    private static final Path ROOT = Paths.get("src/test/resources/tests/").toAbsolutePath();
    private static final String PKG = "valid.const_";

    private final String id;

    public ValidConstTests(String id) {
        super(ROOT);

        this.id = checkNotNull(id, "id cannot be null");
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() throws IOException {
        final List<Object[]> data = new ArrayList<>();
        for (Path path : Files.newDirectoryStream(ROOT.resolve(PKG.replace('.', '/')))) {
            if (path.toString().endsWith(".qux")) {
                data.add(new Object[] {getTestId(path.toAbsolutePath())});
            }
        }

        return data;
    }

    @Test
    public void run() {
        run(id);
    }

    private static String getTestId(Path file) {
        String id = ROOT.relativize(file).toString();

        // Remove the extension
        id = id.substring(0, id.lastIndexOf("."));
        // Change from the path syntax to id syntax ('/' -> '.')
        id = id.replace(File.separator, ".");

        return id;
    }
}

