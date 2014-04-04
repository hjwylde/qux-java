package com.hjwylde.qux.util;

import com.google.common.collect.ImmutableList;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 */
public final class Constants {

    // Qux file versions
    public static final int QUX0_1_0 = 0 << 16 | 1 << 8 | 0;

    public static final ImmutableList<Integer> SUPPORTED_VERSIONS = ImmutableList.of(QUX0_1_0);

    /**
     * This class cannot be instantiated.
     */
    private Constants() {}
}
