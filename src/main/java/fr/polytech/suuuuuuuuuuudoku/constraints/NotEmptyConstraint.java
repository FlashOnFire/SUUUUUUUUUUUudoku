package fr.polytech.suuuuuuuuuuudoku.constraints;

import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec2i;
import fr.polytech.suuuuuuuuuuudoku.grid.InnerGrid;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * This class represents a constraint that ensures no cell in the grid is empty.
 */
public class NotEmptyConstraint implements AbstractConstraint {

    /**
     * Checks if the constraint is satisfied for the given grid.
     *
     * @param grid the Sudoku grid to check
     * @return true if no cell in the grid is empty, false otherwise
     */
    @Override
    public boolean isSatisfied(InnerGrid grid) {
        return Arrays.stream(grid.get()).allMatch(line -> Arrays.stream(line).noneMatch(Objects::isNull));
    }

    /**
     * Returns the possible values for a given position in the grid.
     *
     * @param grid the Sudoku grid
     * @param pos  the position in the grid
     * @return an empty Optional as this constraint does not provide possibilities
     */
    @Override
    public Optional<Set<Integer>> getPossibilities(InnerGrid grid, Vec2i pos) {
        return Optional.empty();
    }

    /**
     * Checks if the two given positions have an effect on each other with respect to the constraint.
     */
    public boolean isAffectedBy(Vec2i pos1, Vec2i pos2) {
        return false;
    }

    /**
     * Checks if the given position is affected by the constraint.
     *
     * @param pos the position to check
     * @return true if the position is affected by the constraint, false otherwise
     */
    public boolean isPosAffected(Vec2i pos) {
        return true;
    }
}
