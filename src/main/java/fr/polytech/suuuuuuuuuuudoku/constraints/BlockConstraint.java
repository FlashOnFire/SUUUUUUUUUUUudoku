package fr.polytech.suuuuuuuuuuudoku.constraints;

import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec2i;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a constraint that checks for the presence of specific symbols within a defined block in a grid.
 */
public class BlockConstraint implements AbstractConstraint {
    /**
     * The set of symbols to be checked within the block.
     */
    private final Set<Integer> symbols;

    /**
     * The starting and ending coordinates of the block.
     */
    private final int x, y, dx, dy;

    /**
     * Constructs a BlockConstraint with the specified symbols and coordinates.
     *
     * @param symbols the set of symbols to be checked within the block
     * @param x       the starting x-coordinate of the block
     * @param y       the starting y-coordinate of the block
     * @param width   the width of the block
     * @param height  the height of the block
     * @throws IllegalArgumentException if dx is less than or equal to x or dy is less than or equal to y
     */
    public BlockConstraint(Set<Integer> symbols, int x, int y, int width, int height) {
        this.symbols = symbols;
        assert width != 0 || height != 0;

        this.x = x;
        this.y = y;
        this.dx = x + width;
        this.dy = y + height;
    }

    /**
     * Checks if the constraint is satisfied for the given grid.
     *
     * @param grid the grid to check the constraint against
     * @return true if the constraint is satisfied, false otherwise
     */
    @Override
    public boolean isSatisfied(Integer[][] grid) {
        // Extract the block from the grid
        var set = extractBlock(grid);

        // Check if the block contains all the symbols and has no duplicates
        return symbols.containsAll(set) && set.size() == symbols.size();
    }

    /**
     * Returns the possible symbols that can be placed at the given position in the grid.
     *
     * @param grid the grid to check the possibilities against
     * @param pos  the position to check the possibilities for
     * @return an Optional containing a list of possible symbols, or an empty Optional if the position is not within the block
     * @throws AssertionError if the position is out of the grid bounds or the grid cell is empty
     */
    @Override
    public Optional<Set<Integer>> getPossibilities(Integer[][] grid, Vec2i pos) {
        assert pos.getX() < grid[0].length;
        assert pos.getY() < grid.length;
        assert grid[pos.getY()][pos.getX()] == null;

        // Check if the position is within the block
        if (pos.getX() < x || pos.getX() >= dx || pos.getY() < y || pos.getY() >= dy) {
            return Optional.empty();
        }

        // Extract the block from the grid
        Set<Integer> set = extractBlock(grid);

        // Return the symbols that are not present in the block
        var possibilities = symbols.stream()
                .filter(c -> !set.contains(c)).
                collect(Collectors.toSet());

        return Optional.of(possibilities);
    }

    /**
     * Checks if the two given positions have an effect on each other with respect to the constraint.
     */
    public boolean isAffectedBy(Vec2i pos1, Vec2i pos2) {
        return pos1.getX() >= x && pos1.getX() < dx && pos1.getY() >= y && pos1.getY() < dy
                && pos2.getX() >= x && pos2.getX() < dx && pos2.getY() >= y && pos2.getY() < dy;
    }

    /**
     * Checks if the given position is affected by the constraint.
     */
    public boolean isPosAffected(Vec2i pos) {
        return pos.getX() >= x && pos.getX() < dx && pos.getY() >= y && pos.getY() < dy;
    }

    /**
     * Checks if the given position is within the block.
     *
     * @param pos the position to check
     * @return true if the position is within the block, false otherwise
     */
    public boolean isInBlock(Vec2i pos) {
        return pos.getX() >= x && pos.getX() < dx && pos.getY() >= y && pos.getY() < dy;
    }

    /**
     * Extracts the block of characters from the grid based on the defined coordinates.
     *
     * @param grid the grid from which to extract the block
     * @return a set of characters within the block, excluding empty cells
     */
    private Set<Integer> extractBlock(Integer[][] grid) {
        HashSet<Integer> set = new HashSet<>();
        for (int i = y; i < dy; i++) {
            set.addAll(Arrays.asList(grid[i]).subList(x, dx));
        }
        set.removeIf(Objects::isNull);

        return set;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }
}
