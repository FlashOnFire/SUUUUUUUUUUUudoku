package fr.polytech.suuuuuuuuuuudoku.algorithm;

import fr.polytech.suuuuuuuuuuudoku.grid.Solvable;

import java.util.*;

/**
 * The SudokuSolver class provides methods to solve a Sudoku puzzle.
 */
public class SudokuSolver {

    /**
     * Solves the given Sudoku grid.
     *
     * @param grid         the Sudoku grid to solve
     * @param backtracking whether to use backtracking if deduction fails
     * @return the solving state of the Sudoku grid
     */
    public static <C, T extends Solvable<C> & ShallowCopyable<T>> Pair<SolvingState, T> solve(T grid, boolean deducing, boolean backtracking) {
        assert deducing || backtracking : "At least one of deducing or backtracking must be enabled";

        ArrayDeque<T> currentList = new ArrayDeque<>();
        currentList.add(grid.shallowCopy());

        while (!currentList.isEmpty()) {
            var currentGrid = currentList.removeLast();
            if (currentGrid.isSolved()) {
                return new Pair<>(SolvingState.SOLVED, currentGrid);
            }

            if (deducing) {
                var state = solveDeduction(currentGrid);

                if (state == SolvingState.SOLVED) {
                    return new Pair<>(SolvingState.SOLVED, currentGrid);
                } else if (state == SolvingState.UNSOLVABLE) {
                    continue;
                } else if (!backtracking) {
                    return new Pair<>(SolvingState.PARTIALLY_SOLVED, currentGrid);
                }
            }

            currentList.addAll(doBacktracking(currentGrid));
        }

        return new Pair<>(SolvingState.UNSOLVABLE, null);
    }

    public static <C, T extends Solvable<C> & ShallowCopyable<T>> List<T> findAllSolutions(T grid, boolean deducing, boolean backtracking) {
        assert deducing || backtracking : "At least one of deducing or backtracking must be enabled";

        ArrayDeque<T> currentList = new ArrayDeque<>();
        currentList.add(grid.shallowCopy());
        List<T> solutions = new ArrayList<>();

        while (!currentList.isEmpty()) {
            var currentGrid = currentList.removeLast();

            if (deducing) {
                var state = solveDeduction(currentGrid);

                if (state == SolvingState.SOLVED) {
                    solutions.add(currentGrid);
                    continue;
                } else if (state == SolvingState.UNSOLVABLE) {
                    continue;
                } else if (!backtracking) {
                    continue;
                }
            }

            currentList.addAll(doBacktracking(currentGrid));
        }

        return solutions;
    }

    public static <C, T extends Solvable<C> & ShallowCopyable<T>> boolean hasMoreThanOneSolution(T grid, boolean deducing, boolean backtracking) {
        assert deducing || backtracking : "At least one of deducing or backtracking must be enabled";

        ArrayDeque<T> currentList = new ArrayDeque<>();
        currentList.add(grid.shallowCopy());
        boolean foundOne = false;

        while (!currentList.isEmpty()) {
            var currentGrid = currentList.removeLast();

            if (deducing) {
                var state = solveDeduction(currentGrid);

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

            currentList.addAll(doBacktracking(currentGrid));
        }

        return false;
    }

    /**
     * Performs backtracking to solve the Sudoku grid.
     *
     * @param grid the Sudoku grid to solve
     * @return the solving state of the Sudoku grid
     */
    private static <C, T extends Solvable<C> & ShallowCopyable<T>> List<T> doBacktracking(T grid) {
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
            T new_grid = grid.shallowCopy();
            new_grid.placeUnchecked(cell.getKey(), c, true);
            return new_grid;
        }).toList();
    }

    /**
     * Solves the Sudoku grid using deduction.
     *
     * @param grid the Sudoku grid to solve
     * @return the solving state of the Sudoku grid
     */
    private static <C, T extends Solvable<C>> SolvingState solveDeduction(T grid) {
        // System.out.println("Deduction");

        boolean finished = false;

        while (!finished) {
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
                        grid.placeUnchecked(cell, possibilities.iterator().next(), true);
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
