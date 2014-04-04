package com.hjwylde.qux.tree;

import com.hjwylde.qux.util.Attribute;

import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.Collection;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public abstract class Node {

    private ImmutableList<Attribute> attributes;

    public Node(Attribute... attributes) {
        this(Arrays.asList(attributes));
    }

    public Node(Collection<Attribute> attributes) {
        this.attributes = ImmutableList.copyOf(attributes);
    }

    public ImmutableList<Attribute> getAttributes() {
        return attributes;
    }
}

