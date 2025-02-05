package fr.polytech.suuuuuuuuuuudoku.algorithm;

import fr.polytech.suuuuuuuuuuudoku.grid.Solvable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The SudokuSolver class provides methods to solve a Sudoku puzzle.
 */
public class SudokuSolver {
    public static final float[] solvePace = new float[]{1.0f};

    /**
     * Solves the given Sudoku grid.
     *
     * @param grid         the Sudoku grid to solve
     * @param backtracking whether to use backtracking if deduction fails
     * @return the solving state of the Sudoku grid
     */
    public static <T extends Solvable<T>> Pair<SolvingState, T> solve(T grid, boolean deducing,
                                                                      boolean backtracking,
                                                                      boolean store_moves) {
        assert deducing || backtracking : "At least one of deducing or backtracking must be enabled";

        if (!grid.areConstraintsSatisfied(true)) {
            return new Pair<>(SolvingState.UNSOLVABLE, null);
        }

        ArrayDeque<T> currentList = new ArrayDeque<>();
        currentList.add(grid.shallowCopy());

        while (!currentList.isEmpty()) {
            var currentGrid = currentList.removeLast();
            if (currentGrid.isSolved()) {
                return new Pair<>(SolvingState.SOLVED, currentGrid);
            }

            if (deducing) {
                SolvingState state = null;
                try {
                    state = solveDeduction(currentGrid, store_moves);
                } catch (InterruptedException e) {
                    System.out.println("Deduction interrupted");
                }

                if (state == SolvingState.SOLVED) {
                    return new Pair<>(SolvingState.SOLVED, currentGrid);
                } else if (state == SolvingState.UNSOLVABLE) {
                    continue;
                } else if (!backtracking) {
                    return new Pair<>(SolvingState.PARTIALLY_SOLVED, currentGrid);
                }
            }
            if (currentGrid.getEmptyCellsPossibilities().isEmpty()) {
                continue;
            }
            currentList.addAll(doBacktracking(currentGrid, store_moves));
        }

        return new Pair<>(SolvingState.UNSOLVABLE, null);
    }

    /**
     * Finds all solutions to the given Sudoku grid.
     *
     * @param grid:         the Sudoku grid to solve
     * @param deducing:     whether to use deduction
     * @param backtracking: whether to use backtracking
     * @param store_moves:  whether to store the moves
     * @return a list of all solutions to the Sudoku grid
     */
    public static <T extends Solvable<T>> List<T> findAllSolutions(T grid, boolean deducing,
                                                                   boolean backtracking,
                                                                   boolean store_moves) throws InterruptedException {
        assert deducing || backtracking : "At least one of deducing or backtracking must be enabled";

        ArrayDeque<T> currentList = new ArrayDeque<>();
        currentList.add(grid.shallowCopy());
        List<T> solutions = new ArrayList<>();

        while (!currentList.isEmpty()) {
            var currentGrid = currentList.removeLast();

            if (deducing) {
                var state = solveDeduction(currentGrid, store_moves);

                if (state == SolvingState.SOLVED) {
                    solutions.add(currentGrid);
                    continue;
                } else if (state == SolvingState.UNSOLVABLE) {
                    continue;
                } else if (!backtracking) {
                    continue;
                }
            }

            currentList.addAll(doBacktracking(currentGrid, store_moves));
        }

        return solutions;
    }

    /**
     * Checks if the given Sudoku grid has more than one solution.
     *
     * @param grid:         the Sudoku grid to solve
     * @param deducing:     whether to use deduction
     * @param backtracking: whether to use backtracking
     * @return true if the Sudoku grid has more than one solution, false otherwise
     */
    public static <T extends Solvable<T>> boolean hasMoreThanOneSolution(T grid,
                                                                         boolean deducing,
                                                                         boolean backtracking) throws InterruptedException {
        assert deducing || backtracking : "At least one of deducing or backtracking must be enabled";

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
                } else if (state == SolvingState.UNSOLVABLE) {
                    continue;
                } else if (!backtracking) {
                    continue;
                }
            }

            currentList.addAll(doBacktracking(currentGrid, false));
        }

        return false;
    }

    /**
     * Performs backtracking to solve the Sudoku grid.
     *
     * @param grid the Sudoku grid to solve
     * @return the solving state of the Sudoku grid
     */
    private static <T extends Solvable<T>> List<T> doBacktracking(T grid, boolean store_moves) {
        // System.out.println("Backtracking");

        assert !grid.getEmptyCellsPossibilities().isEmpty() : "No empty cells";

        var cell = grid.getEmptyCellsPossibilities().entrySet().stream()
                       .min(Comparator.comparingInt(e -> e.getValue().size()))
                       .orElseThrow();
        // System.out.println("Trying " + cell.getKey() + " with " + cell.getValue());

        // disable assert to allow solving with only backtracking
        //assert cell.getValue().size() > 1 : "Cell has less than two possibilities";
        if (cell.getValue().isEmpty()) {
            return List.of();
        }

        return cell.getValue().stream().map(c -> {
            T newGrid = grid.shallowCopy();
            newGrid.placeUnchecked(cell.getKey(), c, true, store_moves);
            return newGrid;
        }).toList();
    }

    /**
     * Solves the Sudoku grid using deduction.
     *
     * @param grid the Sudoku grid to solve
     * @return the solving state of the Sudoku grid
     */
    private static <T extends Solvable<T>> SolvingState solveDeduction(T grid, boolean store_moves) throws InterruptedException {
        // System.out.println("Deduction");

        boolean finished = false;

        while (!finished) {
            if (solvePace[0] != 1.0f) {
                Thread.sleep((long) (10 + (1 - solvePace[0]) * (300 - 10)));
            }
            finished = true;

            var emptyCells = new ArrayDeque<>(grid.getEmptyCellsPossibilities().keySet());
            if (emptyCells.isEmpty()) {
                break;
            }

            while (!emptyCells.isEmpty()) {
                var cell = emptyCells.pop();

                // Handle multigrid
                // We may have already filled this cell by placing a value in another grid
                if (grid.getEmptyCellsPossibilities().containsKey(cell)) {
                    var possibilities = grid.getEmptyCellsPossibilities().get(cell);

                    if (possibilities.size() == 1) {
                        grid.placeUnchecked(cell, possibilities.iterator().next(), true, store_moves);
                        finished = false;
                    } else if (possibilities.isEmpty()) {
                        // System.out.println("Empty cell has no possibilities");

                        return SolvingState.UNSOLVABLE;
                    }
                }
            }
        }

        return grid.isSolved() ? SolvingState.SOLVED : SolvingState.PARTIALLY_SOLVED;
    }
}
