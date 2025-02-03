package fr.polytech.suuuuuuuuuuudoku.algorithm;

public class Box2D {
    private final int x, y, dx, dy;

    public Box2D(int x, int y, int width, int length) {
        this.x = x;
        this.y = y;
        this.dx = x + width;
        this.dy = y + length;
    }

    static public Box2D absolute(int x, int y, int dx, int dy) {
        return new Box2D(x, y, dx - x, dy - y);
    }

    public Box2D absolute() {
        return new Box2D(x, y, dx - x, dy - y);
    }

    public boolean contains(Vec2i vec2i) {
        return contains(vec2i.getX(), vec2i.getY());
    }

    public boolean contains(int posX, int posY) {
        return posX >= x && posX < dx && posY >= y && posY < dy;
    }

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

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int dx() {
        return dx;
    }

    public int dy() {
        return dy;
    }

    public int width() {
        return dx - x;
    }

    public int height() {
        return dy - y;
    }

    public Box2D offset(int offsetX, int offsetY) {
        return new Box2D(x + offsetX, y + offsetY, dx + offsetX, dy + offsetY);
    }

    public Box2D substract(Box2D other) {
      return Box2D.absolute(x - other.x, y - other.y, dx - other.x, dy - other.y);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Box2D box2D = (Box2D) o;
        return x == box2D.x && y == box2D.y && dx == box2D.dx && dy == box2D.dy;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + dx;
        result = 31 * result + dy;
        return result;
    }
}
