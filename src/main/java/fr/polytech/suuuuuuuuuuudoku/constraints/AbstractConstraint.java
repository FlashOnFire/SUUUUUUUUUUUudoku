package fr.polytech.suuuuuuuuuuudoku.constraints;

import fr.polytech.suuuuuuuuuuudoku.solver.Vec2i;

import java.util.Optional;
import java.util.Set;

/**
 * Interface representing a constraint in the Sudoku solver.
 */
public interface AbstractConstraint {

    /**
     * Checks if the constraint is satisfied for the given grid.
     *
     * @param grid the Sudoku grid
     * @return true if the constraint is satisfied, false otherwise
     */
    boolean isSatisfied(String[][] grid);

    /**
     * Gets the possible values for a given position in the grid.
     *
     * @param grid the Sudoku grid
     * @param pos  the position in the grid
     * @return an Optional containing a list of possible values, or an empty Optional if no possibilities
     */
    Optional<Set<String>> getPossibilities(String[][] grid, Vec2i pos);
}
