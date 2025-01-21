package fr.polytech.suuuuuuuuuuudoku.constraints;

import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec3i;
import fr.polytech.suuuuuuuuuuudoku.grid.Grid;

import java.util.Optional;
import java.util.Set;

public interface MultiGridConstraint {
    /**
     * Checks if the constraint is satisfied for the given grid.
     *
     * @param grid the Sudoku grid
     * @return true if the constraint is satisfied, false otherwise
     */
    boolean isSatisfied(Grid[] grid);

    /**
     * Gets the possible values for a given position in the grid.
     *
     * @param grid the Sudoku grid
     * @param pos  the position in the grid
     * @return an Optional containing a list of possible values, or an empty Optional if this constraint does not affect the position
     */
    Optional<Set<Integer>> getPossibilities(Grid[] grid, Vec3i pos);

    /**
     * Checks if the two given positions have an effect on each other with respect to the constraint.
     * @param pos1 the first position
     * @param pos2 the second position
     */
    boolean isAffectedBy(Vec3i pos1, Vec3i pos2);

    /**
     * Checks if the given position is affected by the constraint.
     * @param pos the position to check
     * @return true if the position is affected by the constraint, false otherwise
     */
    boolean isPosAffected(Vec3i pos);
}
