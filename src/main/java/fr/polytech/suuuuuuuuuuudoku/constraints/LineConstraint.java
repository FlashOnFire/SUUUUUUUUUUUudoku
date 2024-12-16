package fr.polytech.suuuuuuuuuuudoku.constraints;

import java.util.Arrays;
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
                    && list.stream().distinct().count() == line.length;
        });
    }
}
