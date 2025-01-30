package fr.polytech.suuuuuuuuuuudoku.grid;

import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec2i;

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
     * Displays the grid to the console.
     */
    public void display() {
        for (Integer[] lines : this.grid) {
            for (Integer cell : lines) {
                if (cell == null) {
                    System.out.print("  ");
                } else {
                    System.out.print(cell + " ");
                }
            }
            System.out.println();
        }
    }

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

    public Integer at(Vec2i pos) {
        return grid[pos.getColumn()][pos.getLine()];
    }

    public void set(Vec2i pos, Integer value) {
        grid[pos.getColumn()][pos.getLine()] = value;
    }

    /**
     * Returns the grid.
     *
     * @return the grid
     */
    public Integer[][] get() {
        return grid;
    }

    public int length() {
        return grid.length;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        InnerGrid innerGrid = (InnerGrid) o;
        return Arrays.deepEquals(grid, innerGrid.grid);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(grid);
    }
}
