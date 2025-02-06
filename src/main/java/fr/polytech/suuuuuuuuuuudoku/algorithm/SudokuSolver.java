package fr.polytech.suuuuuuuuuuudoku.algorithm;

import fr.polytech.suuuuuuuuuuudoku.grid.Solvable;
import fr.polytech.suuuuuuuuuuudoku.utils.Pair;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.List;

/**
 * The SudokuSolver class provides methods to solve a Sudoku puzzle.
 */
public class SudokuSolver {
    /**
     * The pace at which the solver should solve the Sudoku puzzle.
     * Used to slow down the solving process for better visualization.
     */
    public static final float[] solvePace = new float[]{1.0f};

    /**
     * Solves the given Sudoku grid.
     *
     * @param <T>          the type of the Sudoku grid should implement the Solvable interface
     * @param grid         the Sudoku grid to solve
     * @param deducing     whether to use deduction
     * @param backtracking whether to use backtracking if deduction fails
     * @param store_moves  whether to store the moves
     * @return the solving state of the Sudoku grid
     */
    public static <T extends Solvable<T>> Pair<SolvingState, T> solve(T grid, boolean deducing,
                                                                      boolean backtracking,
                                                                      boolean store_moves) {
        assert deducing || backtracking : "At least one of deducing or backtracking must be enabled";

        if (!grid.areConstraintsSatisfied(true)) {
            return new Pair<>(SolvingState.UNSOLVABLE, null);
        }

        // Create the queue of grids to solve
        ArrayDeque<T> currentList = new ArrayDeque<>();
        currentList.add(grid.shallowCopy());

        while (!currentList.isEmpty()) {
            var currentGrid = currentList.removeLast();
            if (currentGrid.isSolved()) {
                return new Pair<>(SolvingState.SOLVED, currentGrid);
            }

            if (deducing) {
                SolvingState state = solveDeduction(currentGrid, store_moves);
                if (state == SolvingState.SOLVED) {
                    return new Pair<>(SolvingState.SOLVED, currentGrid);
                } else if (state == SolvingState.UNSOLVABLE) {
                    continue;
                } else if (state == SolvingState.PARTIALLY_SOLVED && !backtracking) {
                    return new Pair<>(SolvingState.PARTIALLY_SOLVED, currentGrid);
                }
            }

            // Avoid backtracking if the grid is already completely filled
            if (!currentGrid.getEmptyCellsPossibilities().isEmpty()) {
                // Add all the possible grids to the queue
                currentList.addAll(doBacktracking(currentGrid, store_moves));
            }

        }

        return new Pair<>(SolvingState.UNSOLVABLE, null);
    }

    /**
     * Checks if the given Sudoku grid has more than one solution.
     *
     * @param <T>           the type of the Sudoku grid should implement the Solvable interface
     * @param grid:         the Sudoku grid to solve
     * @param deducing:     whether to use deduction
     * @param backtracking: whether to use backtracking
     * @return true if the Sudoku grid has more than one solution, false otherwise
     */
    public static <T extends Solvable<T>> boolean hasMoreThanOneSolution(T grid,
                                                                         boolean deducing,
                                                                         boolean backtracking) {
        assert deducing || backtracking : "At least one of deducing or backtracking must be enabled";

        // Create the queue of grids to solve
        ArrayDeque<T> currentList = new ArrayDeque<>();
        currentList.add(grid.shallowCopy());
        boolean foundOne = false;

        while (!currentList.isEmpty()) {
            var currentGrid = currentList.removeLast();

            if (deducing) {
                var state = solveDeduction(currentGrid, false);

                if (state == SolvingState.SOLVED) {
                    if (foundOne) {
                        return true;
                    }
                    foundOne = true;
                    continue;
                } else if (state == SolvingState.UNSOLVABLE
                        || (foundOne && state == SolvingState.PARTIALLY_SOLVED)) {
                    continue;
                }
            }

            // Avoid backtracking if the grid is already completely filled
            if (!currentGrid.getEmptyCellsPossibilities().isEmpty()) {
                // Add all the possible grids to the queue
                currentList.addAll(doBacktracking(currentGrid, false));
            }
        }

        return false;
    }

    /**
     * Performs backtracking to solve the Sudoku grid.
     *
     * @param <T>         the type of the Sudoku grid should implement the Solvable interface
     * @param grid        the Sudoku grid to solve
     * @param store_moves whether to store the moves
     * @return the solving state of the Sudoku grid
     */
    private static <T extends Solvable<T>> List<T> doBacktracking(T grid, boolean store_moves) {
        assert !grid.getEmptyCellsPossibilities().isEmpty() : "No empty cells";

        // Find the cell with the fewest possibilities
        var cell = grid.getEmptyCellsPossibilities().entrySet().stream()
                       .min(Comparator.comparingInt(e -> e.getValue().size()))
                       .orElseThrow();

        if (cell.getValue().isEmpty()) {
            return List.of();
        }

        // Create a new grid for each possibility
        return cell.getValue().stream().map(c -> {
            T newGrid = grid.shallowCopy();
            newGrid.placeUnchecked(cell.getKey(), c, true, store_moves);
            return newGrid;
        }).toList();
    }

    /**
     * Solves the Sudoku grid using deduction.
     *
     * @param <T>         the type of the Sudoku grid should implement the Solvable interface
     * @param grid        the Sudoku grid to solve
     * @param store_moves whether to store the moves
     * @return the solving state of the Sudoku grid
     */
    private static <T extends Solvable<T>> SolvingState solveDeduction(T grid, boolean store_moves) {
        boolean finished = false;

        while (!finished) {
            // Adjust the solving pace for visualization purposes
            if (solvePace[0] != 1.0f) {
                try {
                    Thread.sleep((long) (10 + (1 - solvePace[0]) * (300 - 10)));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            finished = true;

            // Get all empty cells in the grid
            var emptyCells = new ArrayDeque<>(grid.getEmptyCellsPossibilities().keySet());
            if (emptyCells.isEmpty()) {
                break;
            }

            while (!emptyCells.isEmpty()) {
                var cell = emptyCells.pop();

                // Handle multigrid: check if the cell is still empty
                if (grid.getEmptyCellsPossibilities().containsKey(cell)) {
                    var possibilities = grid.getEmptyCellsPossibilities().get(cell);

                    // If only one possibility, place it and mark as unfinished
                    if (possibilities.size() == 1) {
                        grid.placeUnchecked(cell, possibilities.iterator().next(), true, store_moves);
                        finished = false;
                    } else if (possibilities.isEmpty()) {
                        return SolvingState.UNSOLVABLE;
                    }
                }
            }
        }

        // Return the solving state based on whether the grid is solved
        return grid.isSolved() ? SolvingState.SOLVED : SolvingState.PARTIALLY_SOLVED;
    }
}
