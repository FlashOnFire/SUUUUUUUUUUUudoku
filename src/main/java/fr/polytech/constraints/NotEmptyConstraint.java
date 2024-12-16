package fr.polytech.constraints;

import java.nio.CharBuffer;
import java.util.Arrays;

public class NotEmptyConstraint implements AbstractConstraint {
    @Override
    public boolean isSatisfied(char[][] grid) {
        return Arrays.stream(grid).allMatch(line -> CharBuffer.wrap(line).chars().noneMatch(cell -> cell == ' '));
    }
}
