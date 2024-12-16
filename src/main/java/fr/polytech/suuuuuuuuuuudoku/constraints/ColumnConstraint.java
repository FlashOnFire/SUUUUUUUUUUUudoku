package fr.polytech.suuuuuuuuuuudoku.constraints;


import java.util.Arrays;
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
}
