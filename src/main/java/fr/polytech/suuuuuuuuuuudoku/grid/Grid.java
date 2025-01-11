package fr.polytech.suuuuuuuuuuudoku.grid;

import fr.polytech.suuuuuuuuuuudoku.constraints.AbstractConstraint;
import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec2i;
import fr.polytech.suuuuuuuuuuudoku.constraints.BlockConstraint;
import fr.polytech.suuuuuuuuuuudoku.constraints.NotEmptyConstraint;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents a Sudoku grid with constraints.
 */
public class Grid {
    /**
     * The list of constraints applied to the Sudoku grid.
     */
    private final List<AbstractConstraint> constraints;
    /**
     * The set of symbols used in the grid.
     */
    private final Set<Integer> symbols;
    /**
     * The Sudoku grid represented as a 2D array of Strings.
     */
    private InnerGrid grid;

    private HashMap<Vec2i, Set<Integer>> emptyCellsPossibilities = new HashMap<>();

    /**
     * Constructs a Grid with the specified grid, constraints, and symbols.
     *
     * @param grid        the initial grid
     * @param constraints the list of constraints
     */
    public Grid(Integer[][] grid, List<AbstractConstraint> constraints, Set<Integer> symbols) {
        this.grid = new InnerGrid(grid);
        this.constraints = constraints;
        this.symbols = symbols;
        this.grid.computeEmptyCells();
        this.computeAllEmptyCellsPossibilities();
    }

    /**
     * Constructs a Grid with the specified grid and symbols.
     * The constraints are generated automatically.
     *
     * @param grid    the initial grid
     * @param symbols the set of symbols
     */
    public Grid(Integer[][] grid, Set<Integer> symbols) {
        this(grid, AbstractConstraint.getClassicConstraints(grid.length, symbols), symbols);
    }

    public Grid(Grid otherGrid) {
        this.constraints = otherGrid.constraints;
        this.symbols = otherGrid.symbols;
        this.emptyCellsPossibilities = new HashMap<>(otherGrid.emptyCellsPossibilities);
        this.grid = new InnerGrid(otherGrid.grid);
    }


    /**
     * Displays the grid to the console.
     */
    public void display() {
        this.grid.display();
    }

    /**
     * Checks if all constraints are satisfied.
     *
     * @return true if all constraints are satisfied, false otherwise
     */
    public boolean areConstraintsSatisfied(boolean skip_not_empty) {
        return this.constraints.stream()
                .filter(c -> !(skip_not_empty && c instanceof NotEmptyConstraint))    // Skip NotEmptyConstraint
                .allMatch(c -> c.isSatisfied(this.grid.getInner()));
    }

    /**
     * Computes the possibilities for each empty cell in the grid.
     */
    public void computeAllEmptyCellsPossibilities() {
        this.emptyCellsPossibilities.clear();

        for (var pos : this.grid.computeEmptyCells()) {
            var list = this.constraints.stream()
                    .filter(c -> c.isPosAffected(pos))
                    .map(c -> c.getPossibilities(this.grid.getInner(), pos))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .reduce((acc, set) -> {
                        acc.retainAll(set);
                        return acc;
                    })
                    .orElse(Set.of());

            this.emptyCellsPossibilities.put(pos, list);
        }
    }

    /**
     * Computes the possibilities for each empty cell in the grid that is affected by the changed position.
     */
    public void computeChangedEmptyCellsPossibilities(Vec2i changedPos, boolean skip_not_empty) {
        Set<Vec2i> affectedCells = new HashSet<>();

        // Collect all affected cells
        for (var constraint : this.constraints) {
            if (skip_not_empty && constraint instanceof NotEmptyConstraint) {
                continue;
            }

            if (constraint.isPosAffected(changedPos)) {
                for (var pos : this.emptyCellsPossibilities.keySet()) {
                    if (constraint.isAffectedBy(changedPos, pos)) {
                        affectedCells.add(pos);
                    }
                }
            }
        }

        // Recompute possibilities for affected cells
        for (var pos : affectedCells) {
            var list = this.constraints.stream()
                    .filter(c -> !(skip_not_empty && c instanceof NotEmptyConstraint) && c.isPosAffected(pos))
                    .map(c -> c.getPossibilities(this.grid.getInner(), pos))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .reduce((acc, set) -> {
                        acc.retainAll(set);
                        return acc;
                    })
                    .orElse(new HashSet<>(this.symbols));

            this.emptyCellsPossibilities.put(pos, list);
        }
    }

    public boolean applyNakedPairs() {
        AtomicBoolean changed = new AtomicBoolean(false);

        // Precompute blocks only once to avoid unnecessary filtering
        // Create a mapping of each block to its related empty cells
        Map<BlockConstraint, List<Vec2i>> blockToEmptyCells = new HashMap<>();
        constraints.stream()
                .filter(c -> c instanceof BlockConstraint)
                .map(c -> (BlockConstraint) c)
                .forEach(c -> blockToEmptyCells.put(c,
                        this.emptyCellsPossibilities.keySet().stream()
                                .filter(c::isInBlock) // precompute cells in the block
                                .toList()));

        // Iterate over blocks
        blockToEmptyCells.forEach((constraint, cells) -> {
            // Create a map of possibilities to cells within this block
            Map<Set<Integer>, List<Vec2i>> possibilitiesToCells = new HashMap<>();
            for (Vec2i cell : cells) {
                var possibilities = this.emptyCellsPossibilities.get(cell);
                if (possibilities.size() == 2) { // Only consider cells with size 2
                    possibilitiesToCells.computeIfAbsent(possibilities, _ -> new ArrayList<>()).add(cell);
                }
            }

            // Check for Naked Pairs in the block
            possibilitiesToCells.forEach((possibilities, matchingCells) -> {
                if (matchingCells.size() == 2) { // Found a pair of matching possibilities
                    System.out.println("Naked pair found at " + matchingCells + " with possibilities " + possibilities);

                    // Remove these possibilities from all other cells in the block
                    for (Vec2i cell : cells) {
                        if (!matchingCells.contains(cell)) { // Skip the pair itself
                            var cellPossibilities = this.emptyCellsPossibilities.get(cell);
                            if (cellPossibilities.removeAll(possibilities)) {
                                changed.set(true); // Mark as changed if any possibility was removed
                            }
                        }
                    }
                }
            });
        });

        return changed.get();
    }

    public HashMap<Vec2i, Set<Integer>> getEmptyCellsPossibilities() {
        return this.emptyCellsPossibilities;
    }

    /**
     * Returns the grid.
     *
     * @return the grid
     */
    public InnerGrid getGrid() {
        return grid;
    }

    /**
     * Sets the grid.
     *
     * @param grid the new grid
     */
    public void setGrid(InnerGrid grid) {
        this.grid = grid;
        this.grid.computeEmptyCells();
    }

    /**
     * Tries to place a value at the specified position.
     *
     * @param pos   the position to place the value
     * @param value the value to place
     * @return true if the placement is valid, false otherwise
     */
    public boolean tryPlace(Vec2i pos, Integer value, boolean updatePossibilities) {
        var oldValue = this.grid.getInner()[pos.getY()][pos.getX()];
        this.grid.getInner()[pos.getY()][pos.getX()] = value;
        if (!this.areConstraintsSatisfied(true)) {
            // revert
            System.out.println("Invalid placement (" + value + ") at " + pos + ", reverting");
            this.grid.getInner()[pos.getY()][pos.getX()] = oldValue;

            return false;
        }

        if (oldValue == null && value != null) {
            this.emptyCellsPossibilities.remove(pos);
        }

        if (updatePossibilities) {
            computeChangedEmptyCellsPossibilities(pos, true);
        }

        // System.out.println("Placed " + value + " at " + pos);
        return true;
    }

    public void placeUnchecked(Vec2i pos, Integer value, boolean updatePossibilities) {
        if (this.grid.getInner()[pos.getY()][pos.getX()] == null) {
            this.emptyCellsPossibilities.remove(pos);
        }

        this.grid.getInner()[pos.getY()][pos.getX()] = value;

        if (updatePossibilities) {
            computeChangedEmptyCellsPossibilities(pos, true);
        }
    }

    /**
     * Returns the list of constraints.
     *
     * @return the list of constraints
     */
    public List<AbstractConstraint> getConstraints() {
        return constraints;
    }

    /**
     * Checks if the grid is solved.
     *
     * @return true if the grid is solved, false otherwise
     */
    public boolean isSolved() {
        return this.areConstraintsSatisfied(false);
    }

    /**
     * Returns the set of symbols used in the grid.
     *
     * @return the set of symbols
     */
    public Set<Integer> getSymbols() {
        return this.symbols;
    }

    public int length() {
        return this.grid.getInner().length;
    }
}
