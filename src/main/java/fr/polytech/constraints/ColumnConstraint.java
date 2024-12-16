package fr.polytech.constraints;


import java.util.Arrays;
import java.util.Set;

public class ColumnConstraint implements AbstractConstraint {
    private final Set<Character> symbols;

    public ColumnConstraint(Set<Character> symbols) {
        this.symbols = symbols;
    }

    @Override
    public boolean isSatisfied(char[][] grille) {
        if (grille.length == 0) {
            return true;
        }

        if (grille[0].length != symbols.size()) {
            return false;
        }

        for (int i = 0; i < grille.length; i++) {
            // This is a workaround to use the variable i in the lambda
            int finalI = i;
            Character[] column = Arrays.stream(grille)
                    .parallel()
                    .map(line -> line[finalI])
                    .toArray(Character[]::new);

            if (!Arrays.stream(column)
                    .parallel()
                    .allMatch(c -> symbols.contains((char) c))
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
