package fr.polytech.suuuuuuuuuuudoku.grid;

import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec2i;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a Sudoku grid with constraints.
 */
public class InnerGrid {
    /**
     * The list of empty cells in the grid.
     */
    protected List<Vec2i> emptyCells = new ArrayList<>();
    /**
     * The Sudoku grid represented as a 2D array of Strings.
     */
    private String[][] grid;

    /**
     * Constructs a Grid with the specified grid and constraints.
     *
     * @param grid the initial grid
     */
    public InnerGrid(String[][] grid) {
        this.grid = grid;
        this.computeEmptyCells();
    }


    public InnerGrid(InnerGrid otherGrid) {
        if (otherGrid.grid.length == 0) {
            this.grid = new String[0][0];
            return;
        }

        this.grid = new String[otherGrid.grid.length][otherGrid.grid[0].length];
        for (int y = 0; y < otherGrid.grid.length; y++) {
            this.grid[y] = Arrays.copyOf(otherGrid.grid[y], otherGrid.grid[y].length);
        }

        this.emptyCells = new ArrayList<>(otherGrid.emptyCells);
    }

    /**
     * Displays the grid to the console.
     */
    public void display() {
        for (String[] lines : this.grid) {
            for (String cell : lines) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }

    protected void computeEmptyCells() {
        this.emptyCells.clear();
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                if (grid[y][x].equals(" ")) {
                    this.emptyCells.add(new Vec2i(x, y));
                }
            }
        }
    }

    /**
     * Returns the grid.
     *
     * @return the grid
     */
    public String[][] getInner() {
        return grid;
    }

    /**
     * Sets the grid.
     *
     * @param grid the new grid
     */
    public void setGrid(String[][] grid) {
        this.grid = grid;
        this.computeEmptyCells();
    }


    /**
     * Returns the list of empty cells.
     *
     * @return the list of empty cells
     */
    public List<Vec2i> getEmptyCells() {
        return this.emptyCells;
    }

    public int length() {
        return grid.length;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        InnerGrid innerGrid = (InnerGrid) o;
        return emptyCells.equals(innerGrid.emptyCells) && Arrays.deepEquals(grid, innerGrid.grid);
    }

    @Override
    public int hashCode() {
        int result = emptyCells.hashCode();
        result = 31 * result + Arrays.deepHashCode(grid);
        return result;
    }
}
