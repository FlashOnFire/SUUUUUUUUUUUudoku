package fr.polytech.suuuuuuuuuuudoku.constraints;

import fr.polytech.suuuuuuuuuuudoku.solver.Vec2i;

import java.util.List;
import java.util.Optional;

public interface AbstractConstraint {
    boolean isSatisfied(Character[][] grid);
    Optional<List<Character>> getPossibilities(Character[][] grid, Vec2i pos);
}
