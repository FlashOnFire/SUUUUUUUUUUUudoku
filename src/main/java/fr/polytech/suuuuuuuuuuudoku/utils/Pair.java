package fr.polytech.suuuuuuuuuuudoku.utils;

/**
 * A generic class representing a pair of values.
 *
 * @param <K> the type of the first value
 * @param <V> the type of the second value
 */
public class Pair<K, V> {
    private final K first;
    private final V second;

    /**
     * Constructs a new Pair with the specified values.
     *
     * @param first  the first value
     * @param second the second value
     */
    public Pair(K first, V second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Returns the first value of the pair.
     *
     * @return the first value
     */
    public K getFirst() {
        return first;
    }

    /**
     * Returns the second value of the pair.
     *
     * @return the second value
     */
    public V getSecond() {
        return second;
    }
}