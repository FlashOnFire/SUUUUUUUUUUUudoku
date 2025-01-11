package fr.polytech.suuuuuuuuuuudoku.constraints;

import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec2i;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Interface representing a constraint in the Sudoku solver.
 */
public interface AbstractConstraint {

    /**
     * Generates the classic constraints for a Sudoku grid.
     *
     * @param size    the size of the grid
     * @param symbols the set of symbols used in the grid
     * @return a list of classic constraints
     */
    static List<AbstractConstraint> getClassicConstraints(int size, Set<Integer> symbols) {
        List<AbstractConstraint> constraintList = new ArrayList<>();
        var blockSize = (int) Math.sqrt(size);

        for (int i = 0; i < size; i += blockSize) {
            for (int j = 0; j < size; j += blockSize) {
                constraintList.add(new BlockConstraint(symbols, i, j, blockSize, blockSize));
            }
        }

        constraintList.add(new LineConstraint(symbols));
        constraintList.add(new ColumnConstraint(symbols));
        constraintList.add(new NotEmptyConstraint());

        return constraintList;
    }

    /**
     * Checks if the constraint is satisfied for the given grid.
     *
     * @param grid the Sudoku grid
     * @return true if the constraint is satisfied, false otherwise
     */
    boolean isSatisfied(Integer[][] grid);

    /**
     * Gets the possible values for a given position in the grid.
     *
     * @param grid the Sudoku grid
     * @param pos  the position in the grid
     * @return an Optional containing a list of possible values, or an empty Optional if this constraint does not affect the position
     */
    Optional<Set<Integer>> getPossibilities(Integer[][] grid, Vec2i pos);

    /**
     * Checks if the two given positions have an effect on each other with respect to the constraint.
     * @param pos1 the first position
     * @param pos2 the second position
     */
    boolean isAffectedBy(Vec2i pos1, Vec2i pos2);

    /**
     * Checks if the given position is affected by the constraint.
     * @param pos the position to check
     * @return true if the position is affected by the constraint, false otherwise
     */
    boolean isPosAffected(Vec2i pos);
}
