package fr.polytech.suuuuuuuuuuudoku.constraints;

import fr.polytech.suuuuuuuuuuudoku.solver.Vec2i;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a constraint that ensures each line in the grid satisfies certain conditions.
 */
public class LineConstraint implements AbstractConstraint {
    private final Set<String> symbols;

    /**
     * Constructs a LineConstraint with the specified set of symbols.
     *
     * @param symbols the set of symbols that must be present in each line
     */
    public LineConstraint(Set<String> symbols) {
        this.symbols = symbols;
    }

    /**
     * Checks if the constraint is satisfied for the given grid.
     *
     * @param grid the grid to check
     * @return true if the constraint is satisfied, false otherwise
     */
    @Override
    public boolean isSatisfied(String[][] grid) {
        assert grid.length == symbols.size();

        return Arrays.stream(grid).allMatch(line -> {
            var list = Arrays.stream(line).filter(c -> !c.equals(" ")).toList();

            return symbols.containsAll(list)
                    && list.stream().distinct().count() == list.size();
        });
    }

    /**
     * Gets the possible symbols that can be placed at the specified position in the grid.
     *
     * @param grid the grid to check
     * @param pos  the position to check
     * @return an Optional containing a list of possible symbols, or an empty Optional if no symbols are possible
     */
    @Override
    public Optional<Set<String>> getPossibilities(String[][] grid, Vec2i pos) {
        assert pos.getY() < grid.length;
        assert pos.getX() < grid[0].length;
        assert grid[pos.getY()][pos.getX()].equals(" ");

        var row = Arrays.stream(grid[pos.getY()]).filter(c -> !c.equals(" ")).toList();
        var list = symbols.stream().filter(c -> !row.contains(c)).collect(Collectors.toSet());

        return list.isEmpty() ? Optional.empty() : Optional.of(list);
    }
}
