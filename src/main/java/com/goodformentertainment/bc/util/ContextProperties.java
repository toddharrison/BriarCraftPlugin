package com.goodformentertainment.bc.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class ContextProperties<C, P> {
    private final SortedMap<C, P> contexts;
    private final Map<C, SortedMap<String, P>> properties;

    public ContextProperties() {
        contexts = new TreeMap<C, P>();
        properties = new HashMap<C, SortedMap<String, P>>();
    }

    public ContextProperties(final Comparator<C> comparator) {
        contexts = new TreeMap<C, P>(comparator);
        properties = new HashMap<C, SortedMap<String, P>>();
    }

    public P set(final C context, final P property) {
        return contexts.put(context, property);
    }

    public P set(final C context, final String name, final P property) {
        SortedMap<String, P> contextProperties = properties.get(context);
        if (contextProperties == null) {
            contextProperties = new TreeMap<String, P>();
            properties.put(context, contextProperties);
        }
        return contextProperties.put(name, property);
    }

    public Set<C> contexts() {
        return contexts.keySet();
    }

    public Set<String> names(final C context) {
        Set<String> set;
        final SortedMap<String, P> contextProperties = properties.get(context);
        if (contextProperties == null) {
            set = Collections.emptySet();
        } else {
            set = contextProperties.keySet();
        }
        return set;
    }

    public P get(final C context) {
        return contexts.get(context);
    }

    public P get(final C context, final String name) {
        P property = null;
        final SortedMap<String, P> contextProperties = properties.get(context);
        if (contextProperties != null) {
            property = contextProperties.get(name);
        }
        return property;
    }
}
