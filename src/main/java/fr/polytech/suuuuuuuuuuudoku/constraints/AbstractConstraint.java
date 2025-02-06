package fr.polytech.suuuuuuuuuuudoku.constraints;

import fr.polytech.suuuuuuuuuuudoku.grid.InnerGrid;
import fr.polytech.suuuuuuuuuuudoku.utils.Box2D;
import fr.polytech.suuuuuuuuuuudoku.utils.Vec2i;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Interface representing a constraint for a Sudoku grid.
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
        var sizeConstraint = (int) Math.sqrt(size);
        return getRectConstraints(sizeConstraint, sizeConstraint, symbols);
    }

    /**
     * Generates the rectangular constraints for a Sudoku grid.
     * These are the line, column, block and not empty constraints.
     *
     * @param width:   the width of the block
     * @param height:  the height  of the block
     * @param symbols: the set of symbols used in the grid
     * @return a list of rectangular constraints
     */
    static List<AbstractConstraint> getRectConstraints(int width, int height, Set<Integer> symbols) {
        List<AbstractConstraint> constraintList = new ArrayList<>();
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
    boolean isSatisfied(InnerGrid grid);

    /**
     * Gets the possible values for a given position in the grid.
     *
     * @param grid the Sudoku grid
     * @param pos  the position in the grid
     * @return an Optional containing a list of possible values, or an empty Optional if this constraint does not
     * affect the position
     */
    Optional<Set<Integer>> getPossibilities(InnerGrid grid, Vec2i pos);

    /**
     * Checks if the two given positions have an effect on each other with respect to the constraint.
     *
     * @param pos1 the first position
     * @param pos2 the second position
     */
    boolean isAffectedBy(Vec2i pos1, Vec2i pos2);

    /**
     * Checks if the given position is affected by the constraint.
     *
     * @param pos the position to check
     * @return true if the position is affected by the constraint, false otherwise
     */
    boolean isPosAffected(Vec2i pos);
}
