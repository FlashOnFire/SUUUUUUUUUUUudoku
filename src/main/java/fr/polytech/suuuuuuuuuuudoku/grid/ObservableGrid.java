package fr.polytech.suuuuuuuuuuudoku.grid;

import fr.polytech.suuuuuuuuuuudoku.utils.Move2i;
import fr.polytech.suuuuuuuuuuudoku.utils.Vec2i;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Represents an observable grid that notifies a listener on changes.
 */
public class ObservableGrid extends Solvable<ObservableGrid> {
    /**
     * The listener to notify on changes.
     */
    private final GridListener listener;
    /**
     * The grid to observe.
     */
    private Grid grid;

    /**
     * Constructs an ObservableGrid with the specified grid and listener.
     *
     * @param grid     the grid to observe
     * @param listener the listener to notify on changes
     */
    public ObservableGrid(Grid grid, GridListener listener) {
        super(Objects.requireNonNull(grid.symbols));
        assert listener != null;
        this.grid = grid;
        this.listener = listener;
    }

    /**
     * Gets the size of the grid.
     *
     * @return the size of the grid
     */
    @Override
    public Vec2i getSize() {
        return grid.getSize();
    }

    /**
     * Gets the observed grid.
     *
     * @return the observed grid
     */
    public Grid getInner() {
        return grid;
    }

    /**
     * Sets a new grid.
     *
     * @param grid the new grid to set
     */
    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    /**
     * Checks if the constraints of the grid are satisfied.
     *
     * @param skip_not_empty whether to skip non-empty cells
     * @return true if the constraints are satisfied, false otherwise
     */
    @Override
    public boolean areConstraintsSatisfied(boolean skip_not_empty) {
        return grid.areConstraintsSatisfied(skip_not_empty);
    }

    /**
     * Gets the symbol at the specified position.
     *
     * @param pos the position to get the symbol from
     * @return the symbol at the specified position
     */
    @Override
    public Integer getSymbolAt(Vec2i pos) {
        return grid.getSymbolAt(pos);
    }

    /**
     * Computes all possibilities for empty cells.
     */
    @Override
    public void computeAllEmptyCellsPossibilities() {
        grid.computeAllEmptyCellsPossibilities();
    }

    /**
     * Places a symbol at the specified position without checking constraints.
     *
     * @param pos                 the position to place the symbol
     * @param value               the symbol to place
     * @param updatePossibilities whether to update possibilities
     * @param store_move          whether to store the move
     */
    @Override
    public void placeUnchecked(Vec2i pos, Integer value, boolean updatePossibilities, boolean store_move) {
        grid.placeUnchecked(pos, value, updatePossibilities, store_move);
        listener.onGridChange(this.getInner().getInnerGrid());
    }

    /**
     * Gets the possibilities for empty cells.
     *
     * @return a map of empty cells and their possibilities
     */
    @Override
    public Map<Vec2i, Set<Integer>> getEmptyCellsPossibilities() {
        return grid.getEmptyCellsPossibilities();
    }

    /**
     * Creates a shallow copy of the observable grid.
     *
     * @return a shallow copy of the observable grid
     */
    @Override
    public ObservableGrid shallowCopy() {
        return new ObservableGrid(grid.shallowCopy(), listener);
    }

    /**
     * Gets the list of moves made on the grid.
     *
     * @return the list of moves
     */
    @Override
    public List<Move2i> getMoves() {
        return grid.getMoves();
    }

    /**
     * Cleans the list of moves.
     */
    @Override
    public void cleanMoves() {
        grid.cleanMoves();
    }

    /**
     * Checks if the specified position is in the grid.
     * @param pos the position to check
     * @return true if the position is in the grid, false otherwise
     */
    @Override
    public boolean isInGrid(Vec2i pos) {
        return grid.isInGrid(pos);
    }

    /**
     * Undoes the last move made on the grid.
     *
     * @param updatePossibilities whether to update possibilities
     */
    @Override
    public void undoLastMove(boolean updatePossibilities) {
        grid.undoLastMove(updatePossibilities);
        listener.onGridChange(this.getInner().getInnerGrid());
    }
}