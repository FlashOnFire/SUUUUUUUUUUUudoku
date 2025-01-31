package fr.polytech.suuuuuuuuuuudoku.grid;

import fr.polytech.suuuuuuuuuuudoku.algorithm.ShallowCopyable;
import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec2i;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ObservableGrid extends Solvable<Vec2i> implements ShallowCopyable<ObservableGrid> {
    private final GridListener listener;
    private Grid grid;

    public ObservableGrid(Grid grid, GridListener listener) {
        super(Objects.requireNonNull(grid.symbols));

        assert listener != null;

        this.grid = grid;
        this.listener = listener;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public Grid getGrid() {
        return grid;
    }

    @Override
    public boolean areConstraintsSatisfied(boolean skip_not_empty) {
        return grid.areConstraintsSatisfied(skip_not_empty);
    }

    @Override
    public void computeAllEmptyCellsPossibilities() {
        grid.computeAllEmptyCellsPossibilities();
    }

    @Override
    public void placeUnchecked(Vec2i pos, Integer value, boolean updatePossibilities, boolean store_move) {
        grid.placeUnchecked(pos, value, updatePossibilities, store_move);
        listener.onGridChange(this.getGrid().getInnerGrid());
    }

    @Override
    public Map<Vec2i, Set<Integer>> getEmptyCellsPossibilities() {
        return grid.getEmptyCellsPossibilities();
    }

    @Override
    public ObservableGrid shallowCopy() {
        return new ObservableGrid(grid.shallowCopy(), listener);
    }
}
