package fr.polytech.suuuuuuuuuuudoku.grid;

/**
 * Represents a listener for grid changes.
 */
@FunctionalInterface
public interface MultiGridListener {
    /**
     * Called when the grid changes.
     *
     * @param grids the new grids
     */
    void onGridChange(final InnerGrid[] grids);
}
