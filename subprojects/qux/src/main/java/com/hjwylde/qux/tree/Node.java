package com.hjwylde.qux.tree;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Arrays.asList;

import com.hjwylde.qux.util.Attribute;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public abstract class Node {

    private List<Attribute> attributes = new ArrayList<>();

    public Node(Attribute... attributes) {
        this(asList(attributes));
    }

    public Node(Collection<? extends Attribute> attributes) {
        addAttributes(attributes);
    }

    public void addAttributes(Attribute... attributes) {
        addAttributes(asList(attributes));
    }

    public void addAttributes(Collection<? extends Attribute> attributes) {
        for (Attribute attribute : attributes) {
            this.attributes.add(checkNotNull(attribute, "attribute cannot be null"));
        }
    }

    public ImmutableList<Attribute> getAttributes() {
        return ImmutableList.copyOf(attributes);
    }
}

