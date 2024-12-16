package fr.polytech.constraints;

import java.nio.CharBuffer;
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
            return symbols.containsAll(Arrays.stream(line).toList())
                    && Arrays.stream(line).distinct().count() == line.length;
        });
    }
}
