package fr.polytech.suuuuuuuuuuudoku.algorithm;

public class Box2D {
    private final int x,y,dx,dy;

    public Box2D(int x, int y, int width, int length) {
        this.x = x;
        this.y = y;
        this.dx = x + width;
        this.dy = y + length;
    }

    public boolean contains(Vec2i vec2i) {
        return contains(vec2i.getX(), vec2i.getY());
    }

    public boolean contains(int posX, int posY) {
        return posX >= x && posX < dx && posY >= y && posY < dy;
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
}
