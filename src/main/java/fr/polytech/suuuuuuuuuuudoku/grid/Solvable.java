package fr.polytech.suuuuuuuuuuudoku.grid;

import fr.polytech.suuuuuuuuuuudoku.utils.Move2i;
import fr.polytech.suuuuuuuuuuudoku.utils.Vec2i;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Abstract class representing a solvable grid.
 *
 * @param <T> the type of the solvable grid
 */
public abstract class Solvable<T> {
    final Set<Integer> symbols;

    /**
     * Constructor for Solvable.
     *
     * @param symbols the set of symbols used in the grid
     */
    protected Solvable(Set<Integer> symbols) {
        this.symbols = symbols;
    }

    /**
     * Gets the size of the grid.
     *
     * @return the size of the grid as a Vec2i object
     */
    public abstract Vec2i getSize();

    /**
     * Checks if all constraints are satisfied.
     *
     * @param skip_not_empty whether to skip the NotEmptyConstraint
     * @return true if all constraints are satisfied, false otherwise
     */
    public abstract boolean areConstraintsSatisfied(boolean skip_not_empty);

    /**
     * Gets the symbol at the specified position.
     *
     * @param pos the position in the grid
     * @return the symbol at the specified position
     */
    public abstract Integer getSymbolAt(Vec2i pos);

    /**
     * Checks if the grid is solved.
     *
     * @return true if the grid is solved, false otherwise
     */
    public boolean isSolved() {
        return this.areConstraintsSatisfied(false);
    }

    /**
     * Computes all possibilities for empty cells.
     */
    public abstract void computeAllEmptyCellsPossibilities();

    /**
     * Places a symbol at the specified position without checking constraints.
     *
     * @param pos                 the position in the grid
     * @param value               the symbol to place
     * @param updatePossibilities whether to update possibilities
     * @param store_move          whether to store the move
     */
    public abstract void placeUnchecked(Vec2i pos, Integer value, boolean updatePossibilities, boolean store_move);

    /**
     * Gets the possibilities for empty cells.
     *
     * @return a map of empty cells and their possible symbols
     */
    public abstract Map<Vec2i, Set<Integer>> getEmptyCellsPossibilities();

    /**
     * Creates a shallow copy of the grid.
     *
     * @return a shallow copy of the grid
     */
    public abstract T shallowCopy();

    /**
     * Gets the set of symbols used in the grid.
     *
     * @return the set of symbols
     */
    public Set<Integer> getSymbols() {
        return symbols;
    }

    /**
     * Gets the list of moves made on the grid.
     *
     * @return the list of moves
     */
    public abstract List<Move2i> getMoves();

    /**
     * Undoes the last move made on the grid.
     *
     * @param updatePossibilities whether to update possibilities
     */
    public abstract void undoLastMove(boolean updatePossibilities);

    /**
     * Cleans the list of moves.
     */
    public abstract void cleanMoves();

    /**
     * Checks if the specified position is in the grid.
     *
     * @param pos the position to check
     * @return true if the position is in the grid, false otherwise
     */
    public abstract boolean isInGrid(Vec2i pos);
}