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

    /**
     * Performs backtracking to solve the Sudoku grid.
     *
     * @param grid the Sudoku grid to solve
     * @return the solving state of the Sudoku grid
     */
    private static List<Grid> doBacktracking(Grid grid) {
        var emptyCells = grid.getEmptyCells();

        if (emptyCells.isEmpty()) {
            List<Grid> lst = new ArrayList<>();
            lst.add(grid);
            return lst;
        }

        HashMap<Vec2i, Set<String>> emptyCellMap = new HashMap<>();
        emptyCells.stream().parallel().forEach(cell -> emptyCellMap.put(cell, tryDeduce(grid, cell)));

        var cell = emptyCellMap.entrySet().stream()
                               .min(Comparator.comparingInt(e -> e.getValue().size()))
                               .map(Map.Entry::getKey)
                               .orElseThrow();
        return tryDeduce(grid, cell).stream().map(c -> {
            Grid newgrid = new Grid(grid);
            newgrid.placeUnchecked(cell, c);
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
        boolean finished = false;

        List<Vec2i> emptyCells = grid.getEmptyCells();
        while (!finished) {
            if (emptyCells.isEmpty()) {
                break;
            }
            ArrayDeque<Vec2i> emptyCellsQueue = new ArrayDeque<>(emptyCells);

            finished = true;
            while (!emptyCellsQueue.isEmpty()) {
                var cell = emptyCellsQueue.pop();

                var set = tryDeduce(grid, cell);
                if (set.isEmpty()) {
                    return SolvingState.UNSOLVABLE;
                } else if (set.size() == 1) {
                    grid.placeUnchecked(cell, set.stream().findFirst().orElseThrow());
                    finished = false;
                }
            }
        }

        return grid.isSolved() ? SolvingState.SOLVED : SolvingState.PARTIALLY_SOLVED;
    }

    /**
     * Tries to deduce the possible values for a cell in the Sudoku grid.
     *
     * @param grid the Sudoku grid
     * @param pos  the position of the cell
     * @return the set of possible values for the cell
     */
    private static Set<String> tryDeduce(Grid grid, Vec2i pos) {
        return grid.getConstraints()
                   .stream()
                   .parallel()
                   .map(constraint -> constraint.getPossibilities(grid.getGrid().getInner(), pos))
                   .filter(Optional::isPresent)
                   .map(Optional::get)
                   .reduce((acc, set) -> {
                       acc.retainAll(set);
                       return acc;
                   }).orElse(new HashSet<>(grid.getSymbols()));
    }
}
