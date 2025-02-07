package fr.polytech.suuuuuuuuuuudoku.utils;

import java.util.Objects;

/**
 * A class representing a 2D vector with integer coordinates.
 */
public class Vec2i {
    /**
     * The x and y coordinates of the vector.
     */
    private int x;
    private int y;

    /**
     * Constructs a Vec2i with the specified coordinates.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public Vec2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructs a Vec2i  cloning another Vec2i.
     *
     * @param other the Vec2i to clone
     */
    public Vec2i(Vec2i other) {
        this.x = other.x;
        this.y = other.y;
    }

    /**
     * Returns a new Vec2i with 0, 0 coordinates.
     *
     * @return a new Vec2i with 0, 0 coordinates
     */
    public static Vec2i zero() {
        return new Vec2i(0, 0);
    }


    /**
     * Adds the coordinates of another Vec2i to this vector.
     *
     * @param other the Vec2i to add
     * @return this vector after addition
     */
    public Vec2i add(Vec2i other) {
        this.x += other.x;
        this.y += other.y;
        return this;
    }

    /**
     * Subtracts the coordinates of another Vec2i from this vector.
     *
     * @param other the Vec2i to subtract
     * @return this vector after subtraction
     */
    public Vec2i substract(Vec2i other) {
        this.x -= other.x;
        this.y -= other.y;
        return this;
    }

    /**
     * Returns the x-coordinate of this vector.
     *
     * @return the x-coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of this vector.
     *
     * @return the y-coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param o the reference object with which to compare
     * @return true if this object is the same as the obj argument; false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vec2i vec2i = (Vec2i) o;
        return x == vec2i.x && y == vec2i.y;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "Vec2i{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    /**
     * Checks if the coordinates are equal to the given ones.
     *
     * @param i: the x-coordinate to compare
     * @param j: the y-coordinate to compare
     * @return true if the coordinates are equal, false otherwise
     */
    public boolean equals(int i, int j) {
        return x == i && y == j;
    }
}
