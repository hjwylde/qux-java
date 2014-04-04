package com.hjwylde.qbs.builder;

import static com.google.common.base.Preconditions.checkState;

import com.hjwylde.common.error.BuildError;
import com.hjwylde.common.error.CompilerError;

import java.util.concurrent.Callable;

import javax.annotation.Nullable;

/**
 * A build job that may be executed on a thread pool to speed up the compilation process. The build
 * job will return a result which is one of {@link BuildResult.Code#SUCCESS}, {@link
 * BuildResult.Code#FAIL} and {@link BuildResult.Code#INTERNAL_ERROR} depending on how the build
 * went.
 *
 * @author Henry J. Wylde
 */
public abstract class BuildJob implements Callable<BuildResult> {

    /**
     * The build result of this job. Is null until the job has been completed.
     */
    protected volatile BuildResult result;

    /**
     * {@inheritDoc}
     */
    @Override
    public final BuildResult call() {
        checkState(result == null, "build job has already been run");

        try {
            result = build();
        } catch (BuildError | CompilerError e) {
            result = BuildResult.fail(e);
        } catch (InternalError | RuntimeException e) {
            result = BuildResult.internalError(e);
        }

        return result;
    }

    /**
     * Gets the result of this build job. If the job has not completed yet, then this method will
     * return {@code null}.
     *
     * @return the build result.
     */
    @Nullable
    public final BuildResult getResult() {
        return result;
    }

    public final boolean isComplete() {
        return result != null;
    }

    /**
     * Attempts to build this job, returning the build result. The build result should wrap any
     * checked errors that occurred during the build.
     *
     * @return the build result.
     */
    protected abstract BuildResult build();
}
