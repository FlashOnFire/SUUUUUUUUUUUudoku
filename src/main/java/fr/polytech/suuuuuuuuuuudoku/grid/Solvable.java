package fr.polytech.suuuuuuuuuuudoku.grid;

import java.util.Map;
import java.util.Set;

public abstract class Solvable<T> {
    final Set<Integer> symbols;

    protected Solvable(Set<Integer> symbols) {
        this.symbols = symbols;
    }

    /**
     * Checks if all constraints are satisfied.
     *
     * @param skip_not_empty whether to skip the NotEmptyConstraint
     * @return true if all constraints are satisfied, false otherwise
     */
    public abstract boolean areConstraintsSatisfied(boolean skip_not_empty);

    public boolean isSolved() {
        return this.areConstraintsSatisfied(false);
    }

    public abstract void computeAllEmptyCellsPossibilities();

    public abstract void placeUnchecked(T pos, Integer value, boolean updatePossibilities, boolean store_move);

    public abstract Map<T, Set<Integer>> getEmptyCellsPossibilities();
}
