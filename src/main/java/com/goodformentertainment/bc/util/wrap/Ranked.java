package com.goodformentertainment.bc.util.wrap;

public class Ranked<T extends Comparable<T>> implements Comparable<Ranked<T>> {
    public static <T extends Comparable<T>> Ranked<T> wrap(final int rank, final T object) {
        return new Ranked<T>(rank, object);
    }

    private final int rank;
    private final T object;

    public Ranked(final int rank, final T object) {
        this.rank = rank;
        this.object = object;
    }

    public int getRank() {
        return rank;
    }

    public T get() {
        return object;
    }

    @Override
    public int compareTo(final Ranked<T> ranked) {
        int comp = rank - ranked.rank;
        if (comp == 0) {
            comp = object.compareTo(ranked.object);
        }
        return comp;
    }
}
