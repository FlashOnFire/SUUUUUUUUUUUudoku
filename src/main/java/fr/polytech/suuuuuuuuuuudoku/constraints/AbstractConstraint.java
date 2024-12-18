package fr.polytech.suuuuuuuuuuudoku.constraints;

import fr.polytech.suuuuuuuuuuudoku.solver.Vec2i;

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
    static List<AbstractConstraint> getClassicConstraints(int size, Set<String> symbols) {
        List<AbstractConstraint> constraintList = new ArrayList<>();
        var blockSize = (int) Math.sqrt(size);

        for (int i = 0; i < size; i += blockSize) {
            for (int j = 0; j < size; j += blockSize) {
                constraintList.add(new BlockConstraint(symbols, i, j, i + blockSize, j + blockSize));
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
