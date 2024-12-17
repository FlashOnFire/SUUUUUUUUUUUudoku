package fr.polytech.suuuuuuuuuuudoku.constraints;

import fr.polytech.suuuuuuuuuuudoku.solver.Vec2i;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    public boolean isSatisfied(String[][] grid) {
        return Arrays.stream(grid).allMatch(line -> Arrays.stream(line).noneMatch(cell -> cell == " "));
    }

    /**
     * Returns the possible values for a given position in the grid.
     *
     * @param grid the Sudoku grid
     * @param pos  the position in the grid
     * @return an empty Optional as this constraint does not provide possibilities
     */
    @Override
    public Optional<List<String>> getPossibilities(String[][] grid, Vec2i pos) {
        return Optional.empty();
    }
}
