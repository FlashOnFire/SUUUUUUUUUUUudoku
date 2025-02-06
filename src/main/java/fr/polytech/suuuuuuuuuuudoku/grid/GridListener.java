package fr.polytech.suuuuuuuuuuudoku.grid;

/**
 * Represents a listener for grid changes.
 */
@FunctionalInterface
public interface GridListener {
    /**
     * Called when the grid changes.
     *
     * @param grid the grid that changed
     */
    void onGridChange(final InnerGrid grid);
}
