package fr.polytech.suuuuuuuuuuudoku.constraints;

import fr.polytech.suuuuuuuuuuudoku.grid.InnerGrid;
import fr.polytech.suuuuuuuuuuudoku.utils.Vec2i;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a constraint that checks for the presence of specific symbols at specific positions in a grid.
 * Like a block constraint but destructured.
 */
public class PositionListConstraint implements AbstractConstraint {
    /**
     * The set of symbols to be checked within the constraints.
     */
    private final Set<Integer> symbols;

    /**
     * The list of position which define value to check.
     */
    private final Vec2i[] positionList;

    /**
     * Constructs a GeneralSymbolConstraint with the specified symbols and positions.
     *
     * @param symbols      the set of symbols to be checked within the constraints
     * @param positionList the list of position which define value to check
     * @throws IllegalArgumentException if the positionList is empty or the length of the positionList
     *                                  is different from the length of the symbols
     */
    public PositionListConstraint(Set<Integer> symbols, Vec2i[] positionList) {
        assert positionList.length != 0;
        assert positionList.length == symbols.size();

        this.symbols = symbols;
        this.positionList = positionList;
    }

    /**
     * Checks if the constraint is satisfied for the given grid.
     *
     * @param grid the grid to check the constraint against
     * @return true if the constraint is satisfied, false otherwise
     */
    @Override
    public boolean isSatisfied(InnerGrid grid) {
        // Extract all the values from the grid
        Set<Integer> set = extractValues(grid);

        // Check if the block contains all the symbols and has no duplicates
        return symbols.containsAll(set) && set.size() == symbols.size();
    }

    /**
     * Gets the possible values for a given position in the grid.
     *
     * @param grid the Sudoku grid
     * @param pos  the position in the grid
     * @return the possible values for the given position in the grid
     */
    @Override
    public Optional<Set<Integer>> getPossibilities(InnerGrid grid, Vec2i pos) {
        assert pos.getX() < grid.get()[0].length;
        assert pos.getY() < grid.length();
        assert grid.get()[pos.getY()][pos.getX()] == null;

        if (!isInPositionList(pos)) {
            return Optional.empty();
        }

        // Extract the block from the grid
        Set<Integer> set = extractValues(grid);

        // Return the symbols that are not present in the block
        var possibilities = symbols.stream()
                                   .filter(c -> !set.contains(c))
                                   .collect(Collectors.toSet());

        return Optional.of(possibilities);
    }

    /**
     * Checks if the two given positions have an effect on each other with respect to the constraint.
     *
     * @param pos1 the first position
     * @param pos2 the second position
     * @return true if the two positions have an effect on each other, false otherwise
     */
    @Override
    public boolean isAffectedBy(Vec2i pos1, Vec2i pos2) {
        return isInPositionList(pos1) && isInPositionList(pos2);
    }

    /**
     * Checks if the given position is affected by the constraint.
     *
     * @param pos the position to check
     * @return true if the position is affected by the constraint, false otherwise
     */
    @Override
    public boolean isPosAffected(Vec2i pos) {
        return isInPositionList(pos);
    }

    /**
     * Checks if the given position is within the block.
     *
     * @param pos the position to check
     * @return true if the position is within the block, false otherwise
     */
    private boolean isInPositionList(Vec2i pos) {
        return Arrays.asList(positionList).contains(pos);
    }

    /**
     * Extracts the block of characters from the grid based on the defined coordinates.
     *
     * @param grid the grid from which to extract the block
     * @return a set of characters within the block, excluding empty cells
     */
    private Set<Integer> extractValues(InnerGrid grid) {
        HashSet<Integer> set = new HashSet<>();
        for (Vec2i vec2i : positionList) {
            set.add(grid.at(vec2i));
        }
        set.removeIf(Objects::isNull);

        return set;
    }

    /**
     * Returns the list of positions which define the values to check.
     *
     * @return the array of positions which define the values to check
     */
    public Vec2i[] getPositionList() {
        return positionList;
    }
}
