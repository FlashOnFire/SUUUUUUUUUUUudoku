package fr.polytech.suuuuuuuuuuudoku.constraints;

import fr.polytech.suuuuuuuuuuudoku.grid.InnerGrid;
import fr.polytech.suuuuuuuuuuudoku.utils.Vec2i;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * This class represents a constraint that ensures no cell in the grid is empty.
 */
public class NotEmptyConstraint implements AbstractConstraint {

    /**
     * Constructs a new NotEmptyConstraint.
     */
    public NotEmptyConstraint() {
    }

    /**
     * Checks if the constraint is satisfied for the given grid.
     *
     * @param grid the Sudoku grid to check
     * @return true if there are no empty cells in the grid, false otherwise
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
     *
     * @param pos1 the first position
     * @param pos2 the second position
     * @return false because changing one position does not affect the other
     */
    public boolean isAffectedBy(Vec2i pos1, Vec2i pos2) {
        return false;
    }

    /**
     * Checks if the given position is affected by the constraint.
     *
     * @param pos the position to check
     * @return true because all positions are affected by this constraint
     */
    public boolean isPosAffected(Vec2i pos) {
        return true;
    }
}
