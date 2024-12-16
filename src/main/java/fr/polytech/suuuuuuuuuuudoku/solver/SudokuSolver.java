package fr.polytech.suuuuuuuuuuudoku.solver;

import fr.polytech.suuuuuuuuuuudoku.Grid;
import fr.polytech.suuuuuuuuuuudoku.constraints.AbstractConstraint;
import fr.polytech.suuuuuuuuuuudoku.constraints.LineConstraint;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SudokuSolver {
    public static Optional<Grid> solve(Grid grid, Set<Character> symbols, boolean backtracking) {
        var constraints = grid.getConstraints();

        boolean finished = false;
        while (!finished) {
            var emptyCells = getEmptyCells(grid.getGrid());

            if (emptyCells.isEmpty()) {
                finished = true;
                break;
            }

            finished = true;
            for (var cell : emptyCells) {
                var list = solveDeduction(grid, symbols, cell, constraints);
                if (list.isEmpty()) {
                    System.out.println("Unsolvable grid");
                    return Optional.empty();
                } else if (list.size() == 1) {
                    grid.tryPlace(cell, list.getFirst());
                    finished = false;
                }
            }
        }

        return Optional.of(grid);
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

    private static List<Character> solveDeduction(Grid grid, Set<Character> symbols, Vec2i pos, List<AbstractConstraint> constraints) {
        var values = new ArrayList<>(symbols);
        for (AbstractConstraint constraint : constraints) {
            constraint.tryDeduce(grid.getGrid(), pos).ifPresent(values::retainAll);
        }

        return values;
    }
}
