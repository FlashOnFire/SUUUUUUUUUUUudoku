package fr.polytech.suuuuuuuuuuudoku.constraints;

import fr.polytech.suuuuuuuuuuudoku.solver.Vec2i;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class NotEmptyConstraint implements AbstractConstraint {
    @Override
    public boolean isSatisfied(Character[][] grid) {
        return Arrays.stream(grid).allMatch(line -> Arrays.stream(line).noneMatch(cell -> cell == ' '));
    }

    @Override
    public Optional<List<Character>> getPossibilities(Character[][] grid, Vec2i pos) {
        return Optional.empty();
    }
}
