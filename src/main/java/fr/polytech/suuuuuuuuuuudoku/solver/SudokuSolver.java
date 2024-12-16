package fr.polytech.suuuuuuuuuuudoku.solver;

import fr.polytech.suuuuuuuuuuudoku.Grid;
import fr.polytech.suuuuuuuuuuudoku.constraints.AbstractConstraint;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SudokuSolver {
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

    private static SolvingState solveDeduction(Grid grid, Set<Character> symbols) {
        boolean finished = false;
        while (!finished) {
            var emptyCells = grid.getEmptyCells();

            if (emptyCells.isEmpty()) {
                finished = true;
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

    private static List<Character> tryDeduce(Grid grid, Set<Character> symbols, Vec2i pos, List<AbstractConstraint> constraints) {
        var values = new ArrayList<>(symbols);
        for (AbstractConstraint constraint : constraints) {
            constraint.getPossibilities(grid.getGrid(), pos).ifPresent(values::retainAll);
        }

        return values;
    }
}
