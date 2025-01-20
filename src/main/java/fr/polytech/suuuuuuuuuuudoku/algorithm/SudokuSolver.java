package fr.polytech.suuuuuuuuuuudoku.algorithm;

import fr.polytech.suuuuuuuuuuudoku.grid.Grid;

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
    public static SolvingState solve(Grid grid, boolean deducing, boolean backtracking) {
        assert deducing || backtracking : "At least one of deducing or backtracking must be enabled";

        ArrayDeque<Grid> currentList = new ArrayDeque<>();
        currentList.add(grid);

        while (!currentList.isEmpty()) {
            var currentGrid = currentList.removeLast();
            if (currentGrid.isSolved()) {
                grid.setGrid(currentGrid.getGrid());
                return SolvingState.SOLVED;
            }

            if (deducing) {
                var state = solveDeduction(currentGrid);

                if (state == SolvingState.SOLVED) {
                    grid.setGrid(currentGrid.getGrid());
                    return SolvingState.SOLVED;
                } else if (!backtracking) {
                    if (state == SolvingState.UNSOLVABLE) {
                        grid.setGrid(currentGrid.getGrid());
                        return SolvingState.UNSOLVABLE;
                    }

                    grid.setGrid(currentGrid.getGrid());
                    return SolvingState.PARTIALLY_SOLVED;
                }
            }

            currentList.addAll(doBacktracking(currentGrid));
        }

        return SolvingState.UNSOLVABLE;
    }

    private static List<Grid> findAllSolutions(Grid grid, boolean deducing, boolean backtracking) {
        assert deducing || backtracking : "At least one of deducing or backtracking must be enabled";

        ArrayDeque<Grid> currentList = new ArrayDeque<>();
        currentList.add(grid);
        List<Grid> solutions = new ArrayList<>();

        while (!currentList.isEmpty()) {
            var currentGrid = currentList.removeLast();

            if (deducing) {
                var state = solveDeduction(currentGrid);

                if (state == SolvingState.SOLVED) {
                    solutions.add(currentGrid);
                } else if (!backtracking) {
                    if (state == SolvingState.UNSOLVABLE) {
                        continue;
                    }
                }
            }

            currentList.addAll(doBacktracking(currentGrid));
        }

        return solutions;
    }

    /**
     * Performs backtracking to solve the Sudoku grid.
     *
     * @param grid the Sudoku grid to solve
     * @return the solving state of the Sudoku grid
     */
    private static List<Grid> doBacktracking(Grid grid) {
        System.out.println("Backtracking");

        assert !grid.getEmptyCellsPossibilities().isEmpty() : "No empty cells";

        var cell = grid.getEmptyCellsPossibilities().entrySet().stream()
                .min(Comparator.comparingInt(e -> e.getValue().size()))
                .orElseThrow();
        System.out.println("Trying " + cell.getKey() + " with " + cell.getValue());
        if (cell.getValue().isEmpty()) {
            return List.of();
        }

        //assert cell.getValue().stream().count() > 1 : "Cell has less than two possibilities";

        return cell.getValue().stream().map(c -> {
            Grid newgrid = new Grid(grid);
            newgrid.placeUnchecked(cell.getKey(), c, true);
            return newgrid;
        }).toList();
    }

    /**
     * Solves the Sudoku grid using deduction.
     *
     * @param grid the Sudoku grid to solve
     * @return the solving state of the Sudoku grid
     */
    private static SolvingState solveDeduction(Grid grid) {
        System.out.println("Deduction");

        boolean finished = false;

        while (!finished) {
            finished = true;

            var emptyCells = new ArrayDeque<>(grid.getEmptyCellsPossibilities().keySet());
            if (emptyCells.isEmpty()) {
                break;
            }

            while (!emptyCells.isEmpty()) {
                var cell = emptyCells.pop();

                var possibilities = grid.getEmptyCellsPossibilities().get(cell);

                if (possibilities.size() == 1) {
                    grid.placeUnchecked(cell, possibilities.iterator().next(), true);
                    finished = false;
                } else if (possibilities.isEmpty()) {
                    return SolvingState.UNSOLVABLE;
                }
            }
        }

        return grid.isSolved() ? SolvingState.SOLVED : SolvingState.PARTIALLY_SOLVED;
    }
}
