package fr.polytech.constraints;

import java.nio.CharBuffer;
import java.util.Arrays;

public class NotEmptyConstraint implements AbstractConstraint {
    @Override
    public boolean isSatisfied(Character[][] grid) {
        return Arrays.stream(grid).allMatch(line -> Arrays.stream(line).noneMatch(cell -> cell == ' '));
    }
}
