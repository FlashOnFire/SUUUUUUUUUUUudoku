package fr.polytech.suuuuuuuuuuudoku.grid;

import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec2i;

public record Move2i(Vec2i position, Integer value, Integer previous_value) {
    @Override
    public String toString() {
        return "(" + position.getX() + ", " + position.getY() + ") : " + ((value == null) ? "empty" : value) + " -> " + ((previous_value == null) ? "empty" : previous_value);
    }
}

