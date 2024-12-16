package fr.polytech.suuuuuuuuuuudoku;

import fr.polytech.suuuuuuuuuuudoku.constraints.AbstractConstraint;
import fr.polytech.suuuuuuuuuuudoku.solver.Vec2i;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a Sudoku grid with constraints.
 */
public class Grid implements Cloneable {
    /**
     * The list of constraints applied to the Sudoku grid.
     */
    private final List<AbstractConstraint> constraints;
    /**
     * The Sudoku grid represented as a 2D array of Characters.
     */
    private Character[][] grid;

    /**
     * Constructs a Grid with the specified grid and constraints.
     *
     * @param grid        the initial grid
     * @param constraints the list of constraints
     */
    public Grid(Character[][] grid, List<AbstractConstraint> constraints) {
        this.grid = grid;
        this.constraints = constraints;
    }

    /**
     * Displays the grid to the console.
     */
    public void display() {
        for (Character[] lines : this.grid) {
            for (Character cell : lines) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }

    /**
     * Checks if all constraints are satisfied.
     *
     * @return true if all constraints are satisfied, false otherwise
     */
    public boolean areConstraintsSatisfied() {
        return this.constraints.stream().allMatch(c -> {
            if (!c.isSatisfied(this.grid)) {
                System.out.println("Constraint not satisfied: " + c);
                return false;
            }
            return true;
        });
    }

    /**
     * Returns the grid.
     *
     * @return the grid
     */
    public Character[][] getGrid() {
        return grid;
    }

    /**
     * Sets the grid.
     *
     * @param grid the new grid
     */
    public void setGrid(Character[][] grid) {
        this.grid = grid;
    }

    /**
     * Tries to place a value at the specified position.
     *
     * @param pos   the position to place the value
     * @param value the value to place
     * @return true if the placement is valid, false otherwise
     */
    public boolean tryPlace(Vec2i pos, char value) {
        var oldValue = this.grid[pos.getY()][pos.getX()];
        this.grid[pos.getY()][pos.getX()] = value;
        if (!this.areConstraintsSatisfied()) {
            // revert
            this.grid[pos.getY()][pos.getX()] = oldValue;

            System.out.println("Invalid placement (" + value + ") at " + pos + ", reverting");
            return false;
        }

        // System.out.println("Placed " + value + " at " + pos);
        return true;
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
        var list = new ArrayList<Vec2i>();

        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
                if (grid[y][x] == ' ') {
                    list.add(new Vec2i(x, y));
                }
            }
        }

        return list;
    }

    /**
     * Creates a clone of the grid.
     *
     * @return a new Grid object that is a clone of this grid
     */
    @Override
    public Grid clone() {
        try {
            Grid cloned = (Grid) super.clone();
            var newGrid = new Character[this.grid.length][this.grid[0].length];
            for (int y = 0; y < this.grid.length; y++) {
                newGrid[y] = Arrays.copyOf(this.grid[y], this.grid[y].length);
            }
            cloned.grid = newGrid;
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
