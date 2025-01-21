package fr.polytech.suuuuuuuuuuudoku.grid;

import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec2i;
import fr.polytech.suuuuuuuuuuudoku.constraints.AbstractConstraint;

import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Solvable {
    final List<AbstractConstraint> constraints;
    final Set<Integer> symbols;

    protected Solvable(List<AbstractConstraint> constraints, Set<Integer> symbols) {
        this.constraints = constraints;
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

    public abstract void placeUnchecked(Vec2i pos, Integer value, boolean updatePossibilities);
    public abstract Integer getSymbolAt(Vec2i pos);

    public abstract Map<Vec2i, Set<Integer>> getEmptyCellsPossibilities();
}
