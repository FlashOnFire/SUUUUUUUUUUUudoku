package fr.polytech.suuuuuuuuuuudoku.constraints;

import fr.polytech.suuuuuuuuuuudoku.grid.InnerGrid;
import fr.polytech.suuuuuuuuuuudoku.utils.Vec2i;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a constraint that ensures each line in the grid satisfies certain conditions.
 */
public class LineConstraint implements AbstractConstraint {
    private final Set<Integer> symbols;

    /**
     * Constructs a LineConstraint with the specified set of symbols.
     *
     * @param symbols the symbols that each line must contain
     */
    public LineConstraint(Set<Integer> symbols) {
        this.symbols = symbols;
    }

    /**
     * Checks if the constraint is satisfied for the given grid.
     *
     * @param grid the grid to check
     * @return true if the constraint is satisfied, false otherwise
     */
    @Override
    public boolean isSatisfied(InnerGrid grid) {
        assert grid.length() == symbols.size();

        return Arrays.stream(grid.get()).allMatch(line -> {
            var list = Arrays.stream(line).filter(Objects::nonNull).toList();

            return symbols.containsAll(list)
                    && list.stream().distinct().count() == list.size();
        });
    }

    /**
     * Gets the possible symbols that can be placed at the specified position in the grid.
     *
     * @param grid the grid to check
     * @param pos  the position to check
     * @return an Optional containing a list of possible symbols that can be placed at the specified position with respect to the constraint
     */
    @Override
    public Optional<Set<Integer>> getPossibilities(InnerGrid grid, Vec2i pos) {
        assert pos.getY() < grid.length();
        assert pos.getX() < grid.get().length;
        assert grid.at(pos) == null;

        var row = Arrays.stream(grid.get()[pos.getY()])
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

        var list = symbols
                .stream()
                .filter(c -> !row.contains(c))
                .collect(Collectors.toSet());

        return Optional.of(list);
    }

    /**
     * Checks if the two given positions have an effect on each other with respect to the constraint.
     *
     * @param pos1 the first position
     * @param pos2 the second position
     * @return true if the two positions have an effect on each other, false otherwise
     */
    public boolean isAffectedBy(Vec2i pos1, Vec2i pos2) {
        return pos1.getY() == pos2.getY();
    }

    /**
     * Checks if the given position is affected by the constraint.
     *
     * @param pos the position to check
     * @return true if the position is affected by the constraint, false otherwise
     */
    public boolean isPosAffected(Vec2i pos) {
        return true;
    }
}
