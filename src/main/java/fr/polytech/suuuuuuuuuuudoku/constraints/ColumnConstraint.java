package fr.polytech.suuuuuuuuuuudoku.constraints;

import fr.polytech.suuuuuuuuuuudoku.solver.Vec2i;

import java.util.*;

/**
 * Represents a constraint that ensures each column in the grid satisfies the given symbols.
 */
public class ColumnConstraint implements AbstractConstraint {
    private final Set<String> symbols;

    /**
     * Constructs a ColumnConstraint with the specified set of symbols.
     *
     * @param symbols the set of symbols that each column must contain
     */
    public ColumnConstraint(Set<String> symbols) {
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
        if (grid.length == 0) {
            return true;
        }

        if (grid[0].length != symbols.size()) {
            return false;
        }

        for (int i = 0; i < grid.length; i++) {
            // This is a workaround to use the variable i in the lambda
            int finalI = i;
            String[] column = Arrays.stream(grid)
                    .parallel()
                    .map(line -> line[finalI])
                    .filter(c -> c != " ")
                    .toArray(String[]::new);

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

    /**
     * Gets the possible symbols that can be placed at the specified position in the grid.
     *
     * @param grid the grid to check
     * @param pos  the position to check
     * @return an Optional containing a list of possible symbols, or an empty Optional if no symbols are possible
     */
    @Override
    public Optional<List<String>> getPossibilities(String[][] grid, Vec2i pos) {
        assert pos.getX() < grid[0].length;
        assert pos.getY() < grid.length;
        assert Objects.equals(grid[pos.getY()][pos.getX()], " ");

        var column = Arrays.stream(grid)
                .parallel()
                .map(line -> line[pos.getX()])
                .filter(c -> !Objects.equals(c, " "))
                .toList();

        var list = symbols.stream().filter(c -> !column.contains(c)).toList();

        return list.isEmpty() ? Optional.empty() : Optional.of(list);
    }
}
