package fr.polytech.suuuuuuuuuuudoku.grid;

import fr.polytech.suuuuuuuuuuudoku.algorithm.ShallowCopyable;
import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec2i;
import fr.polytech.suuuuuuuuuuudoku.constraints.AbstractConstraint;
import fr.polytech.suuuuuuuuuuudoku.constraints.BlockConstraint;
import fr.polytech.suuuuuuuuuuudoku.constraints.NotEmptyConstraint;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents a Sudoku grid with constraints.
 */
public class Grid extends Solvable<Vec2i> implements ShallowCopyable<Grid> {
    final List<AbstractConstraint<InnerGrid, Vec2i>> constraints;
    private final ArrayList<Move2i> moves = new ArrayList<>();
    /**
     * The Sudoku grid represented as a 2D array of Strings.
     */
    private InnerGrid innerGrid;
    private HashMap<Vec2i, Set<Integer>> emptyCellsPossibilities = new HashMap<>();

    /**
     * Constructs a Grid with the specified grid, constraints, and symbols.
     *
     * @param grid        the initial grid
     * @param constraints the list of constraints
     */
    public Grid(Integer[][] grid, List<AbstractConstraint<InnerGrid, Vec2i>> constraints, Set<Integer> symbols) {
        super(symbols);
        this.constraints = constraints;
        this.innerGrid = new InnerGrid(grid);
        this.innerGrid.computeEmptyCells();
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

    public Grid(Integer[][] grid, Set<Integer> symbols, int width, int height) {
        this(grid, AbstractConstraint.getRectConstraints(width, height, symbols), symbols);
    }

    public Grid(Grid otherGrid) {
        super(otherGrid.symbols);
        this.constraints = otherGrid.constraints;
        this.emptyCellsPossibilities = new HashMap<>(otherGrid.emptyCellsPossibilities);
        this.innerGrid = new InnerGrid(otherGrid.innerGrid);
        this.moves.addAll(otherGrid.moves);
    }

    /**
     * Displays the grid to the console.
     */
    public void display() {
        this.innerGrid.display();
    }

    /**
     * Checks if all constraints are satisfied.
     *
     * @return true if all constraints are satisfied, false otherwise
     */
    @Override
    public boolean areConstraintsSatisfied(boolean skip_not_empty) {
        return this.constraints.stream()
                               .filter(c -> !(skip_not_empty && c instanceof NotEmptyConstraint))    // Skip
                               // NotEmptyConstraint
                               .allMatch(c -> c.isSatisfied(this.innerGrid));
    }

    /**
     * Computes the possibilities for each empty cell in the grid.
     */
    @Override
    public void computeAllEmptyCellsPossibilities() {
        this.emptyCellsPossibilities.clear();

        for (var pos : this.innerGrid.computeEmptyCells()) {
            var list = this.constraints.stream()
                                       .filter(c -> c.isPosAffected(pos))
                                       .map(c -> c.getPossibilities(this.innerGrid, pos))
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
                                       .map(c -> c.getPossibilities(this.innerGrid, pos))
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
                   .forEach(c -> blockToEmptyCells.put(
                           c,
                           this.emptyCellsPossibilities.keySet().stream()
                                                       .filter(c::isInBlock) // precompute cells in the block
                                                       .toList()
                   ));

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

    @Override
    public HashMap<Vec2i, Set<Integer>> getEmptyCellsPossibilities() {
        return this.emptyCellsPossibilities;
    }

    /**
     * Returns the grid.
     *
     * @return the grid
     */
    public InnerGrid getInnerGrid() {
        return innerGrid;
    }

    /**
     * Sets the grid.
     *
     * @param innerGrid the new grid
     */
    public void setInnerGrid(InnerGrid innerGrid) {
        this.innerGrid = innerGrid;
        this.innerGrid.computeEmptyCells();
    }

    /**
     * Tries to place a value at the specified position.
     *
     * @param pos   the position to place the value
     * @param value the value to place
     * @return true if the placement is valid, false otherwise
     */
    public boolean tryPlace(Vec2i pos, Integer value, boolean updatePossibilities, boolean store_move) {
        var oldValue = this.innerGrid.at(pos);
        this.innerGrid.set(pos, value);
        if (!this.areConstraintsSatisfied(true)) {
            // revert
            System.out.println("Invalid placement (" + value + ") at " + pos + ", reverting");
            this.innerGrid.set(pos, value);

            return false;
        }

        if (oldValue == null && value != null) {
            this.emptyCellsPossibilities.remove(pos);
        } else if (oldValue != null && value == null) {
            System.out.println("Placing empty cell at " + pos);
            this.emptyCellsPossibilities.put(pos, new HashSet<>(this.symbols));
        }

        if (updatePossibilities) {
            computeChangedEmptyCellsPossibilities(pos, true);
        }

        // System.out.println("Placed " + value + " at " + pos);
        return true;
    }

    @Override
    public void placeUnchecked(Vec2i pos, Integer value, boolean updatePossibilities, boolean store_move) {
        Integer oldValue = getSymbolAt(pos);

        if (oldValue == null && value != null) {
            //System.out.println("Suppress empty cell at " + pos);
            this.emptyCellsPossibilities.remove(pos);
        } else if (oldValue != null && value == null) {
            //System.out.println("Placing empty cell at " + pos);
            this.emptyCellsPossibilities.put(pos, new HashSet<>(this.symbols));
        }

        this.innerGrid.set(pos, value);

        if (updatePossibilities) {
            computeChangedEmptyCellsPossibilities(pos, true);
        }

        if (store_move) {
            this.moves.add(new Move2i(pos, value, oldValue));
        }
    }

    public Integer getSymbolAt(Vec2i pos) {
        return this.innerGrid.get()[pos.getColumn()][pos.getLine()];
    }

    public Integer getSymbolAt(int x, int y) {
        return this.innerGrid.get()[y][x];
    }

    /**
     * Returns the list of constraints.
     *
     * @return the list of constraints
     */
    public List<AbstractConstraint<InnerGrid, Vec2i>> getConstraints() {
        return constraints;
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
        return this.innerGrid.get().length;
    }

    public Vec2i size() {
        return new Vec2i(this.innerGrid.get().length, this.innerGrid.length() == 0 ? 0 :
                this.innerGrid.get()[0].length);
    }

    @Override
    public Grid shallowCopy() {
        return new Grid(this);
    }

    public List<Move2i> getMoves() {
        return moves;
    }
}
