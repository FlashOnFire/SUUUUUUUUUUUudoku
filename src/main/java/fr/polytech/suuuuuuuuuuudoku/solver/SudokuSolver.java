package fr.polytech.suuuuuuuuuuudoku.solver;

import fr.polytech.suuuuuuuuuuudoku.Grid;
import fr.polytech.suuuuuuuuuuudoku.constraints.AbstractConstraint;

import java.util.*;

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
    public static SolvingState solve(Grid grid, Set<String> symbols, boolean deducing, boolean backtracking) {
        assert deducing || backtracking : "At least one of deducing or backtracking must be enabled";

        ArrayDeque<String[][]> currentList = new ArrayDeque<>();
        currentList.add(grid.getGrid());

        var constraints = grid.getConstraints();
        while (!currentList.isEmpty()) {
            var currentGrid = currentList.removeLast();
            if (isSolved(currentGrid, constraints)) {
                grid.setGrid(currentGrid);
                return SolvingState.SOLVED;
            }

            if (deducing) {
                var state = solveDeduction(currentGrid, symbols, constraints);

                if (state == SolvingState.SOLVED) {
                    grid.setGrid(currentGrid);
                    return SolvingState.SOLVED;
                } else if (!backtracking) {
                    if (state == SolvingState.UNSOLVABLE) {
                        grid.setGrid(currentGrid);
                        return SolvingState.UNSOLVABLE;
                    }

                    grid.setGrid(currentGrid);
                    return SolvingState.PARTIALLY_SOLVED;
                }
            }

            currentList.addAll(doBacktracking(currentGrid, symbols, constraints));
        }

        return SolvingState.UNSOLVABLE;
    }

    /**
     * Performs backtracking to solve the Sudoku grid.
     *
     * @param grid        the Sudoku grid to solve
     * @param symbols     the set of symbols used in the Sudoku grid
     * @param constraints the list of constraints to satisfy
     * @return the solving state of the Sudoku grid
     */
    private static List<String[][]> doBacktracking(String[][] grid, Set<String> symbols, List<AbstractConstraint> constraints) {
        var emptyCells = getEmptyCells(grid);

        if (emptyCells.isEmpty()) {
            List<String[][]> lst = new ArrayList<>();
            lst.add(grid);
            return lst;
        }

        HashMap<Vec2i, List<String>> emptyCellMap = new HashMap<>();
        for (Vec2i vec2i : emptyCells) {
            emptyCellMap.put(vec2i, tryDeduce(grid, symbols, vec2i, constraints));
        }

        var cell = emptyCellMap.entrySet().stream()
                .min(Comparator.comparingInt(e -> e.getValue().size()))
                .map(Map.Entry::getKey)
                .orElseThrow();
        return tryDeduce(grid, symbols, cell, constraints).stream().map(c -> {
            String[][] newgrid = cloneGrid(grid);
            newgrid[cell.getY()][cell.getX()] = c;
            return newgrid;
        }).toList();
    }

    /**
     * Solves the Sudoku grid using deduction.
     *
     * @param grid    the Sudoku grid to solve
     * @param symbols the set of symbols used in the Sudoku grid
     * @return the solving state of the Sudoku grid
     */
    private static SolvingState solveDeduction(String[][] grid, Set<String> symbols, List<AbstractConstraint> constraints) {
        boolean finished = false;

        List<Vec2i> emptyCells = getEmptyCells(grid);
        while (!finished) {
            if (emptyCells.isEmpty()) {
                break;
            }
            ArrayDeque<Vec2i> emptyCellsQueue = new ArrayDeque<>(emptyCells);


            finished = true;
            while (!emptyCellsQueue.isEmpty()) {
                var cell = emptyCellsQueue.pop();

                var list = tryDeduce(grid, symbols, cell, constraints);
                if (list.isEmpty()) {
                    return SolvingState.UNSOLVABLE;
                } else if (list.size() == 1) {
                    grid[cell.getY()][cell.getX()] = list.getFirst();
                    emptyCells.remove(cell);
                    finished = false;
                }
            }
        }

        return isSolved(grid, constraints) ? SolvingState.SOLVED : SolvingState.PARTIALLY_SOLVED;
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
    private static List<String> tryDeduce(String[][] grid, Set<String> symbols, Vec2i pos, List<AbstractConstraint> constraints) {
        var values = new ArrayList<>(symbols);
        for (AbstractConstraint constraint : constraints) {
            constraint.getPossibilities(grid, pos).ifPresent(values::retainAll);
        }

        return values;
    }


    private static boolean isSolved(String[][] grid, List<AbstractConstraint> constraints) {
        for (var constraint : constraints) {
            if (!constraint.isSatisfied(grid)) {
                return false;
            }
        }

        return true;
    }

    private static String[][] cloneGrid(String[][] grid) {
        if (grid.length == 0) {
            return new String[0][0];
        }

        String[][] newGrid = new String[grid.length][grid[0].length];

        for (int i = 0; i < grid.length; i++) {
            newGrid[i] = (Arrays.copyOf(grid[i], grid[i].length));
        }

        return newGrid;
    }

    private static List<Vec2i> getEmptyCells(String[][] grid) {
        List<Vec2i> emptyCells = new ArrayList<>();
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                if (Objects.equals(grid[y][x], " ")) {
                    emptyCells.add(new Vec2i(x, y));
                }
            }
        }

        return emptyCells;
    }
}
