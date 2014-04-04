package com.hjwylde.common.lang.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to indicate that a feature is currently in alpha testing stage. An alpha feature may
 * be removed at any point in time, without warning.
 *
 * @author Henry J. Wylde
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD,
        ElementType.FIELD})
public @interface Alpha {}
