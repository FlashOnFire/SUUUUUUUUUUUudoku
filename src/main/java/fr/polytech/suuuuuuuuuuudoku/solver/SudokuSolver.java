package fr.polytech.suuuuuuuuuuudoku.solver;

import fr.polytech.suuuuuuuuuuudoku.Grid;
import fr.polytech.suuuuuuuuuuudoku.constraints.AbstractConstraint;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * The SudokuSolver class provides methods to solve a Sudoku puzzle.
 */
public class SudokuSolver {

    /**
     * Solves the given Sudoku grid.
     *
     * @param grid         the Sudoku grid to solve
     * @param symbols      the set of symbols used in the Sudoku grid
     * @param backtracking whether to use backtracking if deduction fails
     * @return the solving state of the Sudoku grid
     */
    public static SolvingState solve(Grid grid, Set<Character> symbols, boolean backtracking) {
        var constraints = grid.getConstraints();

        if (!grid.areConstraintsSatisfied()) {
            System.out.println("Initial constraints not satisfied");
            return SolvingState.UNSOLVABLE;
        }

        var state = solveDeduction(grid, symbols);
        if (state == SolvingState.UNSOLVABLE) {
            return SolvingState.UNSOLVABLE;
        } else if (state == SolvingState.SOLVED) {
            return SolvingState.SOLVED;
        } else if (backtracking) {
            return doBacktracking(grid, symbols, constraints);
        }

        return SolvingState.PARTIALLY_SOLVED;
    }

    /**
     * Performs backtracking to solve the Sudoku grid.
     *
     * @param grid        the Sudoku grid to solve
     * @param symbols     the set of symbols used in the Sudoku grid
     * @param constraints the list of constraints to satisfy
     * @return the solving state of the Sudoku grid
     */
    private static SolvingState doBacktracking(Grid grid, Set<Character> symbols, List<AbstractConstraint> constraints) {
        var emptyCells = grid.getEmptyCells();
        if (emptyCells.isEmpty()) {
            return SolvingState.SOLVED;
        }

        var cell = emptyCells.getFirst();
        var possibilities = tryDeduce(grid, symbols, cell, constraints);
        for (var possibility : possibilities) {
            var newGrid = grid.clone();
            newGrid.tryPlace(cell, possibility);

            var status = solve(newGrid, symbols, true);
            if (status == SolvingState.SOLVED) {
                grid.setGrid(newGrid.getGrid());
                return SolvingState.SOLVED;
            }
        }

        return SolvingState.UNSOLVABLE;
    }

    /**
     * Solves the Sudoku grid using deduction.
     *
     * @param grid    the Sudoku grid to solve
     * @param symbols the set of symbols used in the Sudoku grid
     * @return the solving state of the Sudoku grid
     */
    private static SolvingState solveDeduction(Grid grid, Set<Character> symbols) {
        boolean finished = false;
        while (!finished) {
            var emptyCells = grid.getEmptyCells();

            if (emptyCells.isEmpty()) {
                break;
            }

            finished = true;
            for (var cell : emptyCells) {
                var list = tryDeduce(grid, symbols, cell, grid.getConstraints());
                if (list.isEmpty()) {
                    return SolvingState.UNSOLVABLE;
                } else if (list.size() == 1) {
                    grid.tryPlace(cell, list.getFirst());
                    finished = false;
                }
            }
        }

        return grid.isSolved() ? SolvingState.SOLVED : SolvingState.PARTIALLY_SOLVED;
    }

    /**
     * Tries to deduce the possible values for a cell in the Sudoku grid.
     *
     * @param grid        the Sudoku grid
     * @param symbols     the set of symbols used in the Sudoku grid
     * @param pos         the position of the cell
     * @param constraints the list of constraints to satisfy
     * @return the list of possible values for the cell
     */
    private static List<Character> tryDeduce(Grid grid, Set<Character> symbols, Vec2i pos, List<AbstractConstraint> constraints) {
        var values = new ArrayList<>(symbols);
        for (AbstractConstraint constraint : constraints) {
            constraint.getPossibilities(grid.getGrid(), pos).ifPresent(values::retainAll);
        }

        return values;
    }
}
