package fr.polytech.suuuuuuuuuuudoku.grid;

import fr.polytech.suuuuuuuuuuudoku.constraints.AbstractConstraint;
import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec2i;

import java.util.List;
import java.util.Set;

/**
 * Represents a Sudoku grid with constraints.
 */
public class Grid {
    /**
     * The list of constraints applied to the Sudoku grid.
     */
    private final List<AbstractConstraint> constraints;
    /**
     * The set of symbols used in the grid.
     */
    private final Set<String> symbols;
    /**
     * The Sudoku grid represented as a 2D array of Strings.
     */
    private InnerGrid grid;

    /**
     * Constructs a Grid with the specified grid and constraints.
     *
     * @param grid        the initial grid
     * @param constraints the list of constraints
     */
    public Grid(String[][] grid, List<AbstractConstraint> constraints, Set<String> symbols) {
        this.grid = new InnerGrid(grid);
        this.constraints = constraints;
        this.symbols = symbols;
        this.grid.computeEmptyCells();
    }

    /**
     * Constructs a Grid with the specified grid and symbols.
     * The constraints are generated automatically.
     *
     * @param grid    the initial grid
     * @param symbols the set of symbols
     */
    public Grid(String[][] grid, Set<String> symbols) {
        this(grid, AbstractConstraint.getClassicConstraints(grid.length, symbols), symbols);
    }

    public Grid(Grid otherGrid) {
        this.constraints = otherGrid.constraints;
        this.symbols = otherGrid.symbols;
        this.grid = new InnerGrid(otherGrid.grid);
    }


    /**
     * Displays the grid to the console.
     */
    public void display() {
        this.grid.display();
    }

    /**
     * Checks if all constraints are satisfied.
     *
     * @return true if all constraints are satisfied, false otherwise
     */
    public boolean areConstraintsSatisfied() {
        return this.constraints.stream()
                               .parallel()
                               .allMatch(c -> c.isSatisfied(this.grid.getInner()));
    }

    /**
     * Returns the grid.
     *
     * @return the grid
     */
    public InnerGrid getGrid() {
        return grid;
    }

    /**
     * Sets the grid.
     *
     * @param grid the new grid
     */
    public void setGrid(InnerGrid grid) {
        this.grid = grid;
        this.grid.computeEmptyCells();
    }

    /**
     * Tries to place a value at the specified position.
     *
     * @param pos   the position to place the value
     * @param value the value to place
     * @return true if the placement is valid, false otherwise
     */
    public boolean tryPlace(Vec2i pos, String value) {
        var oldValue = this.grid.getInner()[pos.getY()][pos.getX()];
        this.grid.getInner()[pos.getY()][pos.getX()] = value;
        if (!this.areConstraintsSatisfied()) {
            // revert
            this.grid.getInner()[pos.getY()][pos.getX()] = oldValue;

            System.out.println("Invalid placement (" + value + ") at " + pos + ", reverting");
            return false;
        }

        if (this.grid.getInner()[pos.getY()][pos.getX()].equals(" ") && !value.equals(" ")) {
            this.grid.emptyCells.remove(pos);
        } else if (!this.grid.getInner()[pos.getY()][pos.getX()].equals(" ") && value.equals(" ")) {
            this.grid.emptyCells.add(pos);
        }

        // System.out.println("Placed " + value + " at " + pos);
        return true;
    }

    public void placeUnchecked(Vec2i pos, String value) {
        if (this.grid.getInner()[pos.getY()][pos.getX()].equals(" ")) {
            this.grid.emptyCells.remove(pos);
        }

        this.grid.getInner()[pos.getY()][pos.getX()] = value;

        if (value.equals(" ")) {
            this.grid.emptyCells.add(pos);
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
        return this.areConstraintsSatisfied();
    }

    /**
     * Returns the list of empty cells.
     *
     * @return the list of empty cells
     */
    public List<Vec2i> getEmptyCells() {
        return this.grid.emptyCells;
    }

    /**
     * Returns the set of symbols used in the grid.
     *
     * @return the set of symbols
     */
    public Set<String> getSymbols() {
        return this.symbols;
    }

    public int length() {
        return this.grid.getInner().length;
    }
}
