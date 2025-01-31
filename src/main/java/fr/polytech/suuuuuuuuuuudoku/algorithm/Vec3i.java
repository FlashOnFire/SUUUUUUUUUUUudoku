package fr.polytech.suuuuuuuuuuudoku.algorithm;

import java.util.Objects;

/**
 * A class representing a 3D vector with integer coordinates.
 */
public class Vec3i {
    private final int x;
    private final int y;
    private final int z;

    /**
     * Constructs a Vec3i with the specified coordinates.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param z the z-coordinate
     */
    public Vec3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
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
     * Returns the z-coordinate of this vector.
     *
     * @return the z-coordinate
     */
    public int getZ() {
        return z;
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
        return x == vec3i.x && y == vec3i.y && z == vec3i.z;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "Vec3i{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
