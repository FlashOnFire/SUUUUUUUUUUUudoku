package fr.polytech.suuuuuuuuuuudoku.utils;

public record Move2i(Vec2i position, Integer value, Integer previous_value) {
    @Override
    public String toString() {
        return "(" + position.getX() + ", " + position.getY() + ") : " + ((previous_value == null) ? "empty" :
                previous_value) + " -> " + ((value == null) ? "empty" : value);
    }
}

