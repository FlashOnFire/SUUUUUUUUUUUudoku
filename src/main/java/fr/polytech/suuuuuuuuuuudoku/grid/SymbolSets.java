package fr.polytech.suuuuuuuuuuudoku.grid;

import java.util.Set;
import java.util.stream.IntStream;

/**
 * A utility class that provides predefined sets of symbols for Sudoku.
 */
public class SymbolSets {
    /**
     * Constructs a new SymbolSets.
     */
    public SymbolSets() {
    }

    /**
     * Generates a set of symbols from 1 to the specified length.
     *
     * @param length the maximum number to include in the set
     * @return a set of Integer representing the numbers from 1 to the specified length
     */
    public static Set<Integer> generateSymbols(int length) {
        return IntStream.rangeClosed(1, length).boxed().collect(java.util.stream.Collectors.toSet());
    }
}
