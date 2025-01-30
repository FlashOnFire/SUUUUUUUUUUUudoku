package fr.polytech.suuuuuuuuuuudoku.constraints;

import fr.polytech.suuuuuuuuuuudoku.algorithm.Box2D;
import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec2i;
import fr.polytech.suuuuuuuuuuudoku.grid.InnerGrid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Interface representing a constraint in the Sudoku solver.
 */
public interface AbstractConstraint<T, O> {

    /**
     * Generates the classic constraints for a Sudoku grid.
     *
     * @param size    the size of the grid
     * @param symbols the set of symbols used in the grid
     * @return a list of classic constraints
     */
    static List<AbstractConstraint<InnerGrid, Vec2i>> getClassicConstraints(int size, Set<Integer> symbols) {
        return getRectConstraints(size, size, symbols);
    }

    /**
     * Generates the rectangular constraints for a Sudoku grid.
     *
     * @param width: the width of the grid
     * @param height: the height of the grid
     * @param symbols: the set of symbols used in the grid
     * @return a list of rectangular constraints
     */
    static List<AbstractConstraint<InnerGrid, Vec2i>> getRectConstraints(int width, int height, Set<Integer> symbols) {
        List<AbstractConstraint<InnerGrid, Vec2i>> constraintList = new ArrayList<>();
        var length = width * height;
        for (int i = 0; i < length; i += width) {
            for (int j = 0; j < length; j += height) {
                constraintList.add(new BlockConstraint(symbols, new Box2D(i, j, width, height)));
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
    boolean isSatisfied(T grid);

    /**
     * Gets the possible values for a given position in the grid.
     *
     * @param grid the Sudoku grid
     * @param pos  the position in the grid
     * @return an Optional containing a list of possible values, or an empty Optional if this constraint does not
     * affect the position
     */
    Optional<Set<Integer>> getPossibilities(T grid, O pos);

    /**
     * Checks if the two given positions have an effect on each other with respect to the constraint.
     *
     * @param pos1 the first position
     * @param pos2 the second position
     */
    boolean isAffectedBy(O pos1, O pos2);

    /**
     * Checks if the given position is affected by the constraint.
     *
     * @param pos the position to check
     * @return true if the position is affected by the constraint, false otherwise
     */
    boolean isPosAffected(O pos);
}
