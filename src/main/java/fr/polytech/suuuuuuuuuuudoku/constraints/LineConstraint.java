package fr.polytech.suuuuuuuuuuudoku.constraints;

import fr.polytech.suuuuuuuuuuudoku.solver.Vec2i;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class LineConstraint implements AbstractConstraint {
    private final Set<Character> symbols;

    public LineConstraint(Set<Character> symbols) {
        this.symbols = symbols;
    }

    @Override
    public boolean isSatisfied(Character[][] grid) {
        assert grid.length == symbols.size();

        return Arrays.stream(grid).allMatch(line -> {
            var list = Arrays.stream(line).filter(c -> c != ' ').toList();

            return symbols.containsAll(list)
                    && list.stream().distinct().count() == list.size();
        });
    }

    @Override
    public Optional<List<Character>> getPossibilities(Character[][] grid, Vec2i pos) {
        assert pos.getY() < grid.length;
        assert pos.getX() < grid[0].length;
        assert grid[pos.getX()][pos.getY()] != ' ';

        var row = Arrays.stream(grid[pos.getY()]).filter(c -> c != ' ').toList();
        var list = symbols.stream().filter(c -> !row.contains(c)).toList();

        return list.isEmpty() ? Optional.empty() : Optional.of(list);
    }
}
