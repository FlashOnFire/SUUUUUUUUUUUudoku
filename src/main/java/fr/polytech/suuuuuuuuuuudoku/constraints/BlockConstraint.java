package fr.polytech.suuuuuuuuuuudoku.constraints;

import fr.polytech.suuuuuuuuuuudoku.solver.Vec2i;

import java.util.*;

/**
 * Represents a constraint that checks for the presence of specific symbols within a defined block in a grid.
 */
public class BlockConstraint implements AbstractConstraint {
    /**
     * The set of symbols to be checked within the block.
     */
    private final Set<String> symbols;

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
     * @param dx      the ending x-coordinate of the block
     * @param dy      the ending y-coordinate of the block
     * @throws IllegalArgumentException if dx is less than or equal to x or dy is less than or equal to y
     */
    public BlockConstraint(Set<String> symbols, int x, int y, int dx, int dy) {
        this.symbols = symbols;
        assert dx != 0 || dy != 0;
        assert dx > x && dy > y;

        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Checks if the constraint is satisfied for the given grid.
     *
     * @param grid the grid to check the constraint against
     * @return true if the constraint is satisfied, false otherwise
     */
    @Override
    public boolean isSatisfied(String[][] grid) {
        // Extract the block from the grid
        var list = extractBlock(grid);

        // Check if the block contains all the symbols and has no duplicates
        return symbols.containsAll(list)
                && list.stream().distinct().count() == list.size();
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
    public Optional<List<String>> getPossibilities(String[][] grid, Vec2i pos) {
        assert pos.getX() < grid[0].length;
        assert pos.getY() < grid.length;
        assert grid[pos.getY()][pos.getX()] == " ";

        // Check if the position is within the block
        if (pos.getX() < x || pos.getX() >= dx || pos.getY() < y || pos.getY() >= dy) {
            return Optional.empty();
        }

        // Extract the block from the grid
        List<String> list = extractBlock(grid);

        // Return the symbols that are not present in the block
        var possibilities = symbols.stream().filter(c -> !list.contains(c)).toList();
        return possibilities.isEmpty() ? Optional.empty() : Optional.of(possibilities);
    }

    /**
     * Extracts the block of characters from the grid based on the defined coordinates.
     *
     * @param grid the grid from which to extract the block
     * @return a list of characters within the block, excluding empty cells
     */
    private List<String> extractBlock(String[][] grid) {
        List<String> list = new ArrayList<>();
        for (int i = y; i < dy; i++) {
            list.addAll(Arrays.asList(grid[i]).subList(x, dx));
        }
        list = list.stream().filter(c -> c != " ").toList();

        return list;
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
