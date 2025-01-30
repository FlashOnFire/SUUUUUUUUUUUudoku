package fr.polytech.suuuuuuuuuuudoku.algorithm;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A class representing a 2D vector with integer coordinates.
 */
public class Vec2i {
    private int line;
    private int column;

    /**
     * Constructs a Vec2i with the specified coordinates.
     *
     * @param line the x-coordinate
     * @param y    the y-coordinate
     */
    public Vec2i(int line, int y) {
        this.line = line;
        this.column = y;
    }

    /**
     * Generates a random Vec2i with coordinates between 0 and x_max and 0 and y_max.
     *
     * @param x_max the maximum x-coordinate
     * @param y_max the maximum y-coordinate
     * @return a random Vec2i
     */
    public static Vec2i random(int line_max, int column) {
        return new Vec2i(ThreadLocalRandom.current().nextInt(line_max), ThreadLocalRandom.current().nextInt(column));
    }

    public static Vec2i zero() {
        return new Vec2i(0, 0);
    }

    public Vec2i add(Vec2i other) {
        this.line += other.line;
        this.column += other.column;
        return this;
    }

    public Vec2i substract(Vec2i other) {
        this.line -= other.line;
        this.column -= other.column;
        return this;
    }

    public Vec2i absolute() {
        return new Vec2i(Math.abs(line), Math.abs(column));
    }

    /**
     * Returns the x-coordinate of this vector.
     *
     * @return the x-coordinate
     */
    public int getLine() {
        return line;
    }

    /**
     * Returns the y-coordinate of this vector.
     *
     * @return the y-coordinate
     */
    public int getColumn() {
        return column;
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
        return line == vec2i.line && column == vec2i.column;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(line, column);
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "Vec2i{" +
                "line=" + line +
                ", column=" + column +
                '}';
    }
}
