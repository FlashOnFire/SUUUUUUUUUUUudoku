package fr.polytech.suuuuuuuuuuudoku.solver;

import fr.polytech.suuuuuuuuuuudoku.Grid;
import fr.polytech.suuuuuuuuuuudoku.constraints.AbstractConstraint;
import fr.polytech.suuuuuuuuuuudoku.constraints.LineConstraint;

import java.util.*;

public class SudokuSolver {
    public static Grid solve(Grid grid, Set<Character> symbols, boolean backtracking) {
        var constraints = grid.getConstraints();

        System.out.println(new LineConstraint(symbols).tryDeduce(grid.getGrid(), new Vec2i(0, 2)));


        boolean finished = false;
        while (!finished) {
            var emptyCells = getEmptyCells(grid.getGrid());

            if (emptyCells.isEmpty()) {
                finished = true;
                break;
            }

            finished = true;
            for (var cell: emptyCells) {
                var opt = solveDeduction(grid, symbols, cell, constraints);
                if (opt.isPresent()) {
                    grid.tryPlace(cell, opt.get());
                    finished = false;
                }
            }
        }

        return grid;
    }

    private static ArrayList<Vec2i> getEmptyCells(Character[][] grid) {
        var list = new ArrayList<Vec2i>();

        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
                if (grid[y][x] == ' ') {
                    list.add(new Vec2i(x, y));
                }
            }
        }

        return list;
    }

    private static Optional<Character> solveDeduction(Grid grid, Set<Character> symbols, Vec2i pos, List<AbstractConstraint> constraints) {
        var values = new ArrayList<>(symbols);
        for (AbstractConstraint constraint : constraints) {
            constraint.tryDeduce(grid.getGrid(), pos).ifPresent(values::retainAll);
        }

        if (values.size() == 1) {
            return Optional.of(values.getFirst());
        }

        return Optional.empty();
    }
}
