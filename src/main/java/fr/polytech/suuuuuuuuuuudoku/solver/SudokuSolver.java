package fr.polytech.suuuuuuuuuuudoku.solver;

import fr.polytech.suuuuuuuuuuudoku.Grid;
import fr.polytech.suuuuuuuuuuudoku.constraints.AbstractConstraint;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SudokuSolver {
    public static Grid solve(Grid grid, boolean backtracking) {
        var constraints = grid.getConstraints();

        while (getEmptyCells(grid.getGrid()).stream().anyMatch(move -> {
            return solveDeduction(grid, move, constraints);
        })) {
        }

        return grid;
    }

    private static ArrayList<Vec2i> getEmptyCells(Character[][] grid) {
        var list = new ArrayList<Vec2i>();

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == ' ') {
                    list.add(new Vec2i(i, j));
                }
            }
        }

        return list;
    }

    private static boolean solveDeduction(Grid grid, Vec2i pos, List<AbstractConstraint> constraints) {
        var values = new HashSet<Character>();
        for (AbstractConstraint constraint : constraints) {
            constraint.tryDeduce(grid.getGrid(), pos).ifPresent(values::addAll);
        }

        if (values.size() == 1) {
            grid.tryPlace(pos, values.iterator().next());
            return true;
        }

        return false;
    }
}
