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
    public boolean isSatisfied(char[][] grille) {
        assert grille.length == symbols.size();

        return Arrays.stream(grille).allMatch(line -> {
            return CharBuffer.wrap(line)
                    .chars()
                    .allMatch(c -> symbols.contains((char) c))
                    && CharBuffer
                    .wrap(line)
                    .chars()
                    .distinct()
                    .count() == line.length;
        });
    }
}
