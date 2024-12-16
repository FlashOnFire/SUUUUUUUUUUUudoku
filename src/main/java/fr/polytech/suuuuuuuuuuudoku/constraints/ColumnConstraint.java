package fr.polytech.suuuuuuuuuuudoku.constraints;

import fr.polytech.suuuuuuuuuuudoku.solver.Vec2i;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ColumnConstraint implements AbstractConstraint {
    private final Set<Character> symbols;

    public ColumnConstraint(Set<Character> symbols) {
        this.symbols = symbols;
    }

    @Override
    public boolean isSatisfied(Character[][] grid) {
        if (grid.length == 0) {
            return true;
        }

        if (grid[0].length != symbols.size()) {
            return false;
        }

        for (int i = 0; i < grid.length; i++) {
            // This is a workaround to use the variable i in the lambda
            int finalI = i;
            Character[] column = Arrays.stream(grid)
                    .parallel()
                    .map(line -> line[finalI])
                    .filter(c -> c != ' ')
                    .toArray(Character[]::new);

            if (!symbols.containsAll(Arrays.asList(column))
                    || Arrays.stream(column)
                    .parallel()
                    .distinct()
                    .count() != column.length) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Optional<List<Character>> tryDeduce(Character[][] grid, Vec2i pos) {
        assert pos.getX() < grid[0].length;
        assert pos.getY() < grid.length;
        assert grid[pos.getY()][pos.getX()] != ' ';

        var column = Arrays.stream(grid)
                .parallel()
                .map(line -> line[pos.getX()])
                .filter(c -> c != ' ')
                .toList();

        var list = symbols.stream().filter(c -> !column.contains(c)).toList();

        return list.isEmpty() ? Optional.empty() : Optional.of(list);
    }
}
