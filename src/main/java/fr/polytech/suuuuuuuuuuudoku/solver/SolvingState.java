package fr.polytech.suuuuuuuuuuudoku.solver;

/**
 * Enum representing the state of the Sudoku solving process.
 */
public enum SolvingState {
    /**
     * Indicates that the Sudoku puzzle is completely solved.
     */
    SOLVED,

    /**
     * Indicates that the Sudoku puzzle is partially solved.
     */
    PARTIALLY_SOLVED,

    /**
     * Indicates that the Sudoku puzzle is unsolvable.
     */
    UNSOLVABLE,
}