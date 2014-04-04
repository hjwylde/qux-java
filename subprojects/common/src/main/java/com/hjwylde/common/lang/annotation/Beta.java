package com.hjwylde.common.lang.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to indicate that a feature is currently in beta testing stage. A beta feature is one
 * that has been decided on, but is subject to change for reasons such as design.
 *
 * @author Henry J. Wylde
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD,
        ElementType.FIELD})
public @interface Beta {}
