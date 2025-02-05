package fr.polytech.suuuuuuuuuuudoku.grid;

import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec2i;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ObservableGrid extends Solvable<ObservableGrid> {
    private final GridListener listener;
    private Grid grid;

    public ObservableGrid(Grid grid, GridListener listener) {
        super(Objects.requireNonNull(grid.symbols));

        assert listener != null;

        this.grid = grid;
        this.listener = listener;
    }

    @Override
    public Vec2i getSize() {
        return grid.getSize();
    }

    public Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    @Override
    public boolean areConstraintsSatisfied(boolean skip_not_empty) {
        return grid.areConstraintsSatisfied(skip_not_empty);
    }

    @Override
    public Integer getSymbolAt(Vec2i pos) {
        return grid.getSymbolAt(pos);
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

    @Override
    public List<Move2i> getMoves() {
        return grid.getMoves();
    }

    @Override
    public void cleanMoves() {
        grid.cleanMoves();
    }

    @Override
    public void undoLastMove(boolean updatePossibilities) {
        grid.undoLastMove(updatePossibilities);
        listener.onGridChange(this.getGrid().getInnerGrid());
    }
}
