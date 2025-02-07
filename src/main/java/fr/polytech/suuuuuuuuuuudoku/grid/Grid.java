package fr.polytech.suuuuuuuuuuudoku.grid;

import fr.polytech.suuuuuuuuuuudoku.constraints.AbstractConstraint;
import fr.polytech.suuuuuuuuuuudoku.constraints.NotEmptyConstraint;
import fr.polytech.suuuuuuuuuuudoku.utils.Move2i;
import fr.polytech.suuuuuuuuuuudoku.utils.Vec2i;

import java.util.*;

/**
 * Represents a Sudoku grid with constraints.
 */
public class Grid extends Solvable<Grid> {
    /**
     * The list of constraints.
     */
    final List<AbstractConstraint> constraints;
    /**
     * The list of moves.
     */
    private final ArrayList<Move2i> moves = new ArrayList<>();
    /**
     * The Sudoku grid represented as a 2D array of Strings.
     */
    private InnerGrid innerGrid;
    /**
     * The possibilities for each empty cell in the grid.
     */
    private HashMap<Vec2i, Set<Integer>> emptyCellsPossibilities = new HashMap<>();

    /**
     * Constructs a Grid with the specified grid, constraints, and symbols.
     *
     * @param grid        the initial grid
     * @param constraints the list of constraints
     * @param symbols     the set of symbols
     */
    public Grid(Integer[][] grid, List<AbstractConstraint> constraints, Set<Integer> symbols) {
        super(symbols);
        this.constraints = constraints;
        this.innerGrid = new InnerGrid(grid);
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

    /**
     * Constructs a Grid with the specified grid, width, height, and symbols.
     * The constraints are generated automatically.
     *
     * @param grid    the initial grid
     * @param symbols the set of symbols
     * @param width   the width of the block
     * @param height  the height of the block
     */
    public Grid(Integer[][] grid, Set<Integer> symbols, int width, int height) {
        this(grid, AbstractConstraint.getRectConstraints(width, height, symbols), symbols);
    }

    /**
     * Constructs a Grid by copying another grid.
     *
     * @param otherGrid the grid to copy
     */
    public Grid(Grid otherGrid) {
        super(otherGrid.symbols);
        this.constraints = otherGrid.constraints;
        this.emptyCellsPossibilities = new HashMap<>(otherGrid.emptyCellsPossibilities);
        this.innerGrid = new InnerGrid(otherGrid.innerGrid);
        this.moves.addAll(otherGrid.moves);
    }

    /**
     * Checks if all constraints are satisfied.
     *
     * @param skip_not_empty: whether to skip the NotEmptyConstraint
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
     *
     * @param changedPos:     the position that has changed
     * @param skip_not_empty: whether to skip the NotEmptyConstraint
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

    /**
     * Returns the possibilities for each empty cell in the grid.
     *
     * @return the possibilities for each empty cell in the grid
     */
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
    }

    /**
     * Places a value at the specified position without checking constraints.
     *
     * @param pos:                 the position to place the value
     * @param value:               the value to place
     * @param updatePossibilities: whether to update the possibilities
     * @param store_move:          whether to store the move
     */
    @Override
    public void placeUnchecked(Vec2i pos, Integer value, boolean updatePossibilities, boolean store_move) {
        Integer oldValue = getSymbolAt(pos);

        if (oldValue == null && value != null) {
            this.emptyCellsPossibilities.remove(pos);
        } else if (oldValue != null && value == null) {
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

    /**
     * Returns the symbol at the specified position.
     *
     * @param pos: the position to get the symbol
     * @return the symbol at the position
     */
    public Integer getSymbolAt(Vec2i pos) {
        return this.innerGrid.get()[pos.getY()][pos.getX()];
    }

    /**
     * Returns the symbol at the specified position.
     *
     * @param x: the x-coordinate of the position
     * @param y: the y-coordinate of the position
     * @return the symbol at the position
     */
    public Integer getSymbolAt(int x, int y) {
        return this.innerGrid.get()[y][x];
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
     * Returns the set of symbols used in the grid.
     *
     * @return the set of symbols
     */
    public Set<Integer> getSymbols() {
        return this.symbols;
    }


    /**
     * Clears all moves.
     */
    @Override
    public void cleanMoves() {
        this.moves.clear();
    }

    /**
     * Returns the length of the grid.
     *
     * @return the length of the grid
     */
    public int length() {
        return this.innerGrid.get().length;
    }

    /**
     * Returns the size of the grid.
     *
     * @return the size of the grid
     */
    public Vec2i getSize() {
        return new Vec2i(this.innerGrid.get().length, this.innerGrid.length() == 0 ? 0 :
                this.innerGrid.get()[0].length);
    }

    /**
     * Returns a copied version of the grid.
     *
     * @return a copied version of the grid
     */
    @Override
    public Grid shallowCopy() {
        return new Grid(this);
    }

    /**
     * Returns the list of moves.
     *
     * @return the list of moves
     */
    public List<Move2i> getMoves() {
        return moves;
    }

    /**
     * Undoes the last move made by the users
     *
     * @param updatePossibilities: whether to update the possibilities
     */
    @Override
    public void undoLastMove(boolean updatePossibilities) {
        if (this.moves.isEmpty()) {
            return;
        }

        var lastMove = this.moves.removeLast();
        this.placeUnchecked(lastMove.position(), lastMove.previous_value(), updatePossibilities, false);
    }
}
