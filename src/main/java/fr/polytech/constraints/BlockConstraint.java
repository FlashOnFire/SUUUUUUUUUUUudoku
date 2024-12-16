package fr.polytech.constraints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class BlockConstraint implements AbstractConstraint {
    private final Set<Character> symbols;
    private final int x, y, dx, dy;

    public BlockConstraint(Set<Character> symbols, int x, int y, int dx, int dy) {
        this.symbols = symbols;
        assert dx != 0 || dy != 0;
        assert dx > x && dy > y;

        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public boolean isSatisfied(Character[][] grid) {
        List<Character> list = new ArrayList<>();
        for (int i = y; i < dy; i++) {
            list.addAll(Arrays.asList(grid[i]).subList(x, dx));
        }

        return symbols.containsAll(list)
                && list.stream().distinct().count() == list.size();
    }
}
