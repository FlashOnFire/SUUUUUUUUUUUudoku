package fr.polytech.suuuuuuuuuuudoku.utils;

/**
 * Represents a 2D box.
 */
public class Box2D {
    /**
     * The x and y coordinates of the box.
     * The box starts at (x, y) and ends at (dx, dy).
     */
    private final int x, y, dx, dy;

    /**
     * Constructs a Box2D with the specified coordinates.
     *
     * @param x      the x coordinate
     * @param y      the y coordinate
     * @param width  the width of the box
     * @param length the length of the box
     */
    public Box2D(int x, int y, int width, int length) {
        this.x = x;
        this.y = y;
        this.dx = x + width;
        this.dy = y + length;
    }

    /**
     * Constructs a Box2D with the specified position and size.
     *
     * @param pos  the position of the box
     * @param size the size of the box
     */
    public Box2D(Vec2i pos, Vec2i size) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.dx = x + size.getX();
        this.dy = y + size.getY();
    }

    /**
     * Checks if the box contains the specified position.
     *
     * @param vec2i the position to check
     * @return true if the box contains the position, false otherwise
     */
    public boolean contains(Vec2i vec2i) {
        return contains(vec2i.getX(), vec2i.getY());
    }

    /**
     * Checks if the box contains the specified position.
     *
     * @param posX the x coordinate of the position
     * @param posY the y coordinate of the position
     * @return true if the box contains the position, false otherwise
     */
    public boolean contains(int posX, int posY) {
        return posX >= x && posX < dx && posY >= y && posY < dy;
    }

    /**
     * Checks if the box overlaps with the specified box.
     *
     * @param other the box to check
     * @return the overlapping box if there is one, null otherwise
     */
    public Box2D overlap(Box2D other) {
        int newX = Math.max(this.x, other.x);
        int newY = Math.max(this.y, other.y);
        int newDx = Math.min(this.dx, other.dx);
        int newDy = Math.min(this.dy, other.dy);

        if (newX >= newDx || newY >= newDy) {
            return null; // No overlap
        }

        return new Box2D(newX, newY, newDx - newX, newDy - newY);
    }

    /**
     * Gets the x coordinate of the box.
     *
     * @return the x coordinate
     */
    public int x() {
        return x;
    }

    /**
     * Gets the y coordinate of the box.
     *
     * @return the y coordinate
     */
    public int y() {
        return y;
    }

    /**
     * Gets the dx coordinate of the box.
     *
     * @return the dx coordinate
     */
    public int dx() {
        return dx;
    }

    /**
     * Gets the dy coordinate of the box.
     *
     * @return the dy coordinate
     */
    public int dy() {
        return dy;
    }

    /**
     * Gets the width of the box.
     *
     * @return the position
     */
    public int width() {
        return dx - x;
    }

    /**
     * Gets the height of the box.
     *
     * @return the height
     */
    public int height() {
        return dy - y;
    }

    /**
     * Offsets the box by the specified amount.
     *
     * @param offsetX the x offset
     * @param offsetY the y offset
     * @return the offset box
     */
    public Box2D offset(int offsetX, int offsetY) {
        return new Box2D(x + offsetX, y + offsetY, dx + offsetX, dy + offsetY);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Box2D box2D = (Box2D) o;
        return x == box2D.x && y == box2D.y && dx == box2D.dx && dy == box2D.dy;
    }

    @Override
    public int hashCode() {
        int result = 113 * x + 129 * y;
        result = 31 * result + dx;
        result = 31 * result + dy;
        return result;
    }
}
