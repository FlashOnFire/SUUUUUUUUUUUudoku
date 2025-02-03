package fr.polytech.suuuuuuuuuuudoku.grid;

import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec2i;

import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Solvable<T> {
    final Set<Integer> symbols;

    protected Solvable(Set<Integer> symbols) {
        this.symbols = symbols;
    }

    public abstract Vec2i getSize();

    /**
     * Checks if all constraints are satisfied.
     *
     * @param skip_not_empty whether to skip the NotEmptyConstraint
     * @return true if all constraints are satisfied, false otherwise
     */
    public abstract boolean areConstraintsSatisfied(boolean skip_not_empty);

    public abstract Integer getSymbolAt(Vec2i pos);

    public boolean isSolved() {
        return this.areConstraintsSatisfied(false);
    }

    public abstract void computeAllEmptyCellsPossibilities();

    public abstract void placeUnchecked(Vec2i pos, Integer value, boolean updatePossibilities, boolean store_move);

    public abstract Map<Vec2i, Set<Integer>> getEmptyCellsPossibilities();

    public abstract T shallowCopy();

    public Set<Integer> getSymbols() {
        return symbols;
    }

    public abstract List<Move2i> getMoves();
    public abstract void undoLastMove(boolean updatePossibilities);
}
