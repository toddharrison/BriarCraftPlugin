package com.goodformentertainment.bc.util.wrap;

public class Named<T> implements Comparable<Named<T>> {
    public static <T> Named<T> wrap(final String name, final T object) {
        return new Named<T>(name, object);
    }

    private final String name;
    private final T object;

    public Named(final String name, final T object) {
        this.name = name;
        this.object = object;
    }

    public String getName() {
        return name;
    }

    public T get() {
        return object;
    }

    @Override
    public int compareTo(final Named<T> named) {
        return name.compareTo(named.name);
    }
}
