package fr.polytech.suuuuuuuuuuudoku.grid;

/**
 * Represents a listener for grid changes.
 */
@FunctionalInterface
public interface MultiGridListener {
    /**
     * Called when the grid changes.
     *
     * @param grid the grid that changed
     * @param gridIndex the index of the grid that changed
     */
    void onGridChange(final int gridIndex, final InnerGrid grid);
}
