package fr.polytech.suuuuuuuuuuudoku.algorithm;

import java.util.Objects;

/**
 * A class representing a 3D vector with integer coordinates.
 */
public class Vec3i {
    private final int line;
    private final int column;
    private final int depth;

    /**
     * Constructs a Vec3i with the specified coordinates.
     *
     * @param line   the x-coordinate
     * @param column the y-coordinate
     * @param depth  the z-coordinate
     */
    public Vec3i(int line, int column, int depth) {
        this.line = line;
        this.column = column;
        this.depth = depth;
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
     * Returns the z-coordinate of this vector.
     *
     * @return the z-coordinate
     */
    public int getDepth() {
        return depth;
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
        Vec3i vec3i = (Vec3i) o;
        return line == vec3i.line && column == vec3i.column && depth == vec3i.depth;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(line, column, depth);
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "Vec3i{" +
                "x=" + line +
                ", y=" + column +
                ", z=" + depth +
                '}';
    }
}
