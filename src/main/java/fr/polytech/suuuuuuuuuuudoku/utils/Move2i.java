package fr.polytech.suuuuuuuuuuudoku.utils;

/**
 * Represents a move in the Sudoku game.
 *
 * @param position       The position of the move on the Sudoku grid.
 * @param value          The new value to be placed at the position.
 * @param previous_value The previous value at the position.
 */
public record Move2i(Vec2i position, Integer value, Integer previous_value) {

    /**
     * Returns a string representation of the move.
     *
     * @return A string in the format "(x, y) : previous_value -> value".
     */
    @Override
    public String toString() {
        return "(" + position.getX() + ", " + position.getY() + ") : " + ((previous_value == null) ? "empty" :
                previous_value) + " -> " + ((value == null) ? "empty" : value);
    }
}