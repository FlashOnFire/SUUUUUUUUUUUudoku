package fr.polytech.suuuuuuuuuuudoku.algorithm;

public class Box2D {
    private final int line, column, line2, column2;

    public Box2D(int line, int column, int width, int length) {
        this.line = line;
        this.column = column;
        this.line2 = line + width;
        this.column2 = column + length;
    }

    static public Box2D absolute(int line, int column, int line2, int column2) {
        return new Box2D(line, column, line2 - line, column2 - column);
    }

    public boolean contains(Vec2i vec2i) {
        return contains(vec2i.getLine(), vec2i.getColumn());
    }

    public boolean contains(int posColumn, int posLine) {
        return posColumn >= line && posColumn < line2 && posLine >= column && posLine < column2;
    }

    public Box2D overlap(Box2D other) {
        int newX = Math.max(this.line, other.line);
        int newY = Math.max(this.column, other.column);
        int newDx = Math.min(this.line2, other.line2);
        int newDy = Math.min(this.column2, other.column2);

        if (newX >= newDx || newY >= newDy) {
            return null; // No overlap
        }

        return new Box2D(newX, newY, newDx - newX, newDy - newY);
    }

    public int line() {
        return line;
    }

    public int column() {
        return column;
    }

    public int line2() {
        return line2;
    }

    public int column2() {
        return column2;
    }

    public int width() {
        return line2 - line;
    }

    public int height() {
        return column2 - column;
    }

    public Box2D substract(Box2D other) {
        return Box2D.absolute(line - other.line, column - other.column, line2 - other.line, column2 - other.column);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Box2D box2D = (Box2D) o;
        return line == box2D.line && column == box2D.column && line2 == box2D.line2 && column2 == box2D.column2;
    }

    @Override
    public int hashCode() {
        int result = line;
        result = 31 * result + column;
        result = 31 * result + line2;
        result = 31 * result + column2;
        return result;
    }
}
