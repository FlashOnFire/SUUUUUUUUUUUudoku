package fr.polytech.suuuuuuuuuuudoku;

import fr.polytech.suuuuuuuuuuudoku.constraints.AbstractConstraint;
import fr.polytech.suuuuuuuuuuudoku.solver.Vec2i;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Represents a Sudoku grid with constraints.
 */
public class Grid {
    /**
     * The list of constraints applied to the Sudoku grid.
     */
    private final List<AbstractConstraint> constraints;
    /**
     * The Sudoku grid represented as a 2D array of Strings.
     */
    private String[][] grid;

    private List<Vec2i> emptyCells = new ArrayList<>();

    /**
     * Constructs a Grid with the specified grid and constraints.
     *
     * @param grid        the initial grid
     * @param constraints the list of constraints
     */
    public Grid(String[][] grid, List<AbstractConstraint> constraints) {
        this.grid = grid;
        this.constraints = constraints;
        this.computeEmptyCells();
    }

    public Grid(Grid otherGrid) {
        this.constraints = otherGrid.constraints;

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

    private void computeEmptyCells() {
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
     * Checks if all constraints are satisfied.
     *
     * @return true if all constraints are satisfied, false otherwise
     */
    public boolean areConstraintsSatisfied() {
        return this.constraints.stream()
                .parallel()
                .allMatch(c -> c.isSatisfied(this.grid));
    }

    /**
     * Returns the grid.
     *
     * @return the grid
     */
    public String[][] getGrid() {
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
     * Tries to place a value at the specified position.
     *
     * @param pos   the position to place the value
     * @param value the value to place
     * @return true if the placement is valid, false otherwise
     */
    public boolean tryPlace(Vec2i pos, String value) {
        var oldValue = this.grid[pos.getY()][pos.getX()];
        this.grid[pos.getY()][pos.getX()] = value;
        if (!this.areConstraintsSatisfied()) {
            // revert
            this.grid[pos.getY()][pos.getX()] = oldValue;

            System.out.println("Invalid placement (" + value + ") at " + pos + ", reverting");
            return false;
        }

        if (this.grid[pos.getY()][pos.getX()].equals(" ") && !value.equals(" ")) {
            this.emptyCells.remove(pos);
        } else if (!this.grid[pos.getY()][pos.getX()].equals(" ") && value.equals(" ")) {
            this.emptyCells.add(pos);
        }

        // System.out.println("Placed " + value + " at " + pos);
        return true;
    }

    public void placeUnchecked(Vec2i pos, String value) {
        if (this.grid[pos.getY()][pos.getX()].equals(" ")) {
            this.emptyCells.remove(pos);
        }

        this.grid[pos.getY()][pos.getX()] = value;

        if (value.equals(" ")) {
            this.emptyCells.add(pos);
        }
    }

    /**
     * Returns the list of constraints.
     *
     * @return the list of constraints
     */
    public List<AbstractConstraint> getConstraints() {
        return constraints;
    }

    /**
     * Checks if the grid is solved.
     *
     * @return true if the grid is solved, false otherwise
     */
    public boolean isSolved() {
        return this.getEmptyCells().isEmpty() && this.areConstraintsSatisfied();
    }

    /**
     * Returns the list of empty cells.
     *
     * @return the list of empty cells
     */
    public List<Vec2i> getEmptyCells() {
        return this.emptyCells;
    }
}
