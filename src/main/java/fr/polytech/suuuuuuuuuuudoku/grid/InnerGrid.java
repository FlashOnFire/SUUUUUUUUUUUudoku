package fr.polytech.suuuuuuuuuuudoku.grid;

import fr.polytech.suuuuuuuuuuudoku.utils.Vec2i;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Represents a Sudoku grid with constraints.
 */
public class InnerGrid {
    /**
     * The Sudoku grid represented as a 2D array of Strings.
     */
    private final Integer[][] grid;

    /**
     * Constructs a Grid with the specified grid and constraints.
     *
     * @param grid the initial grid
     */
    public InnerGrid(Integer[][] grid) {
        this.grid = grid;
    }

    /**
     * Constructs a Grid with the specified inner grid.
     *
     * @param otherGrid the inner grid
     */
    public InnerGrid(InnerGrid otherGrid) {
        if (otherGrid.grid.length == 0) {
            this.grid = new Integer[0][0];
            return;
        }

        this.grid = new Integer[otherGrid.grid.length][otherGrid.grid[0].length];
        for (int y = 0; y < otherGrid.grid.length; y++) {
            this.grid[y] = Arrays.copyOf(otherGrid.grid[y], otherGrid.grid[y].length);
        }
    }

    /**
     * Computes the empty cells in the grid.
     *
     * @return the set of empty cells
     */
    public HashSet<Vec2i> computeEmptyCells() {
        var emptyCells = new HashSet<Vec2i>();

        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                if (grid[y][x] == null) {
                    emptyCells.add(new Vec2i(x, y));
                }
            }
        }

        return emptyCells;
    }

    /**
     * Returns the value at the specified position.
     *
     * @param pos the position
     * @return the value at the specified position
     */
    public Integer at(Vec2i pos) {
        return grid[pos.getY()][pos.getX()];
    }

    /**
     * Sets the value at the specified position.
     *
     * @param pos   the position
     * @param value the value
     */
    public void set(Vec2i pos, Integer value) {
        grid[pos.getY()][pos.getX()] = value;
    }

    /**
     * Returns the grid.
     *
     * @return the grid
     */
    public Integer[][] get() {
        return grid;
    }

    /**
     * Returns the length of the grid.
     *
     * @return the length of the grid
     */
    public int length() {
        return grid.length;
    }


    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param o the reference object with which to compare
     * @return true if this object is the same as the obj argument; false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        InnerGrid innerGrid = (InnerGrid) o;
        return Arrays.deepEquals(grid, innerGrid.grid);
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(grid);
    }
}
