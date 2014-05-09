package com.hjwylde.qux.util;

import com.google.common.collect.ImmutableList;

/**
 * Utility class for holding constants.
 *
 * @author Henry J. Wylde
 */
public final class Constants {

    public static final int QUX0_1_0 = 0 << 16 | 1 << 8 | 0;
    public static final int QUX0_1_1 = 0 << 16 | 1 << 8 | 1;
    public static final int QUX0_1_2 = 0 << 16 | 1 << 8 | 2;
    public static final int QUX0_1_3 = 0 << 16 | 1 << 8 | 3;

    /**
     * A list of supported Qux versions for this API.
     */
    public static final ImmutableList<Integer> SUPPORTED_VERSIONS = ImmutableList.of(QUX0_1_3);

    /**
     * This class cannot be instantiated.
     */
    private Constants() {}
}
