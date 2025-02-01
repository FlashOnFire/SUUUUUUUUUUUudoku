package fr.polytech.suuuuuuuuuuudoku.constraints;

import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec2i;
import fr.polytech.suuuuuuuuuuudoku.grid.InnerGrid;

import java.util.*;
import java.util.stream.Collectors;

public class GeneralSymbolConstraint implements AbstractConstraint {
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
     * @param symbols the set of symbols to be checked within the constraints
     * @param positionList the list of position which define value to check
     * @throws IllegalArgumentException if the positionList is empty or the length of the positionList
     * is different from the length of the symbols
     */
    public GeneralSymbolConstraint(Set<Integer> symbols, Vec2i[] positionList) {
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

    @Override
    public boolean isAffectedBy(Vec2i pos1, Vec2i pos2) {
        return isInPositionList(pos1) && isInPositionList(pos2);
    }

    @Override
    public boolean isPosAffected(Vec2i pos) {
        return isInPositionList(pos);
    }

    private boolean isInPositionList(Vec2i pos) {
        return Arrays.asList(positionList).contains(pos);
    }

    private Set<Integer> extractValues(InnerGrid grid) {
        HashSet<Integer> set = new HashSet<>();
        for (Vec2i vec2i : positionList) {
            set.add(grid.at(vec2i));
        }
        set.removeIf(Objects::isNull);

        return set;
    }

    public Vec2i[] getPositionList() {
        return positionList;
    }
}
