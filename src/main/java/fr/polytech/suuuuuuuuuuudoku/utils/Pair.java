package fr.polytech.suuuuuuuuuuudoku.utils;

/**
 * A generic class representing a pair of values.
 *
 * @param <K> the type of the first value
 * @param <V> the type of the second value
 */
public record Pair<K, V>(K first, V second) {
    /**
     * Constructs a new Pair with the specified values.
     *
     * @param first  the first value
     * @param second the second value
     */
    public Pair {
    }

    /**
     * Returns the first value of the pair.
     *
     * @return the first value
     */
    @Override
    public K first() {
        return first;
    }

    /**
     * Returns the second value of the pair.
     *
     * @return the second value
     */
    @Override
    public V second() {
        return second;
    }
}