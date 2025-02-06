package fr.polytech.suuuuuuuuuuudoku.constraints;

import fr.polytech.suuuuuuuuuuudoku.grid.InnerGrid;
import fr.polytech.suuuuuuuuuuudoku.utils.Vec2i;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a constraint that ensures each column in the grid satisfies the given symbols.
 */
public class ColumnConstraint implements AbstractConstraint {
    private final Set<Integer> symbols;

    /**
     * Constructs a ColumnConstraint with the specified set of symbols.
     *
     * @param symbols the set of symbols that each column must contain
     */
    public ColumnConstraint(Set<Integer> symbols) {
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
        if (grid.length() == 0) {
            return true;
        }

        if (grid.get()[0].length != symbols.size()) {
            return false;
        }

        for (int i = 0; i < grid.length(); i++) {
            // This is a workaround to use the variable i in the lambda
            int finalI = i;
            Integer[] column = Arrays.stream(grid.get())
                                     .map(line -> line[finalI])
                                     .filter(Objects::nonNull)
                                     .toArray(Integer[]::new);

            if (!symbols.containsAll(Arrays.asList(column))
                    || Arrays.stream(column)
                             .distinct()
                             .count() != column.length) {
                return false;
            }
        }

        return true;
    }

    /**
     * Gets the possible symbols that can be placed at the specified position in the grid.
     *
     * @param grid the grid to check
     * @param pos  the position to check
     * @return an Optional containing a list of possible symbols, or an empty Optional if no symbols are possible
     */
    @Override
    public Optional<Set<Integer>> getPossibilities(InnerGrid grid, Vec2i pos) {
        assert pos.getY() < grid.length();
        assert pos.getX() < grid.get()[0].length;
        assert grid.at(pos) == null;

        var column = Arrays.stream(grid.get())
                           .map(line -> line[pos.getX()])
                           .filter(Objects::nonNull)
                           .collect(Collectors.toSet());

        var list = symbols.stream().filter(c -> !column.contains(c)).collect(Collectors.toSet());

        return Optional.of(list);
    }

    /**
     * Checks if the two given positions have an effect on each other with respect to the constraint.
     */
    public boolean isAffectedBy(Vec2i pos1, Vec2i pos2) {
        return pos1.getX() == pos2.getX();
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
