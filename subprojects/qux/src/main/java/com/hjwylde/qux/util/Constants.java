package com.hjwylde.qux.util;

import com.google.common.collect.ImmutableList;

/**
 * Utility class for holding constants.
 *
 * @author Henry J. Wylde
 */
public final class Constants {

    // Qux file versions

    /**
     * Qux v0.1.0.
     */
    public static final int QUX0_1_0 = 0 << 16 | 1 << 8 | 0;
    /**
     * Qux v0.1.1.
     */
    public static final int QUX0_1_1 = 0 << 16 | 1 << 8 | 1;

    /**
     * A list of supported Qux versions for this API.
     */
    public static final ImmutableList<Integer> SUPPORTED_VERSIONS = ImmutableList.of(QUX0_1_1);

    /**
     * This class cannot be instantiated.
     */
    private Constants() {}
}
