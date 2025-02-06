package fr.polytech.suuuuuuuuuuudoku.constraints;

import fr.polytech.suuuuuuuuuuudoku.grid.InnerGrid;
import fr.polytech.suuuuuuuuuuudoku.utils.Box2D;
import fr.polytech.suuuuuuuuuuudoku.utils.Vec2i;

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
     * The box that defines the block in the grid.
     */
    private final Box2D box;

    /**
     * Constructs a BlockConstraint with the specified symbols and coordinates.
     *
     * @param symbols the set of symbols to be checked within the block
     * @param box     the box that defines the block in the grid
     * @throws IllegalArgumentException if dx is less than or equal to x or dy is less than or equal to y
     */
    public BlockConstraint(Set<Integer> symbols, Box2D box) {
        this.symbols = symbols;
        assert box.width() != 0 || box.height() != 0;

        this.box = box;
    }

    /**
     * Checks if the constraint is satisfied for the given grid.
     *
     * @param grid the grid to check the constraint against
     * @return true if the constraint is satisfied, false otherwise
     */
    @Override
    public boolean isSatisfied(InnerGrid grid) {
        HashSet<Integer> set = new HashSet<>();

        for (int y = box.y(); y < box.dy(); y++) {
            for (int x = box.x(); x < box.dx(); x++) {
                // Ignore empty cells
                if (grid.get()[y][x] != null) {
                    // Check for duplicates
                    if (set.contains(grid.get()[y][x])) {
                        return false;
                    }

                    set.add(grid.get()[y][x]);
                }
            }
        }

        // Check if all values in the block are present in the symbols
        // (not the other way around to allow empty cells to pass the constraint satisfaction)
        return symbols.containsAll(set);
    }

    /**
     * Returns the possible symbols that can be placed at the given position in the grid.
     *
     * @param grid the grid to check the possibilities against
     * @param pos  the position to check the possibilities for
     * @return an Optional containing a list of possible symbols, or an empty Optional if the position is not within
     * the block
     * @throws AssertionError if the position is out of the grid bounds or the grid cell is empty
     */
    @Override
    public Optional<Set<Integer>> getPossibilities(InnerGrid grid, Vec2i pos) {
        assert pos.getX() < grid.get()[0].length;
        assert pos.getY() < grid.length();
        assert grid.get()[pos.getY()][pos.getX()] == null;

        if (!isInBlock(pos)) {
            return Optional.empty();
        }

        // Extract the block from the grid
        Set<Integer> values = extractBlock(grid);

        // Return the symbols that are not present in the block
        var possibilities = symbols.stream()
                                   .filter(c -> !values.contains(c))
                                   .collect(Collectors.toSet());

        return Optional.of(possibilities);
    }

    /**
     * Checks if the two given positions have an effect on each other with respect to the constraint.
     */
    public boolean isAffectedBy(Vec2i pos1, Vec2i pos2) {
        return isInBlock(pos1) && isInBlock(pos2);
    }

    /**
     * Checks if the given position is affected by the constraint.
     */
    public boolean isPosAffected(Vec2i pos) {
        return isInBlock(pos);
    }

    /**
     * Checks if the given position is within the block.
     *
     * @param pos the position to check
     * @return true if the position is within the block, false otherwise
     */
    public boolean isInBlock(Vec2i pos) {
        return box.contains(pos);
    }

    /**
     * Extracts the block of characters from the grid based on the defined coordinates.
     *
     * @param grid the grid from which to extract the block
     * @return a set of characters within the block, excluding empty cells
     */
    private Set<Integer> extractBlock(InnerGrid grid) {
        Set<Integer> set = new HashSet<>();

        for (int i = box.y(); i < box.dy(); i++) {
            set.addAll(Arrays.asList(grid.get()[i]).subList(box.x(), box.dx()));
        }

        set.remove(null);

        return set;
    }

    public Box2D getBlock() {
        return box;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BlockConstraint that = (BlockConstraint) o;
        return Objects.equals(symbols, that.symbols) && Objects.equals(box, that.box);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbols, box);
    }
}
