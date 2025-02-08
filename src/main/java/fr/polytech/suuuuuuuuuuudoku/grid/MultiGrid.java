package fr.polytech.suuuuuuuuuuudoku.grid;

import fr.polytech.suuuuuuuuuuudoku.utils.Box2D;
import fr.polytech.suuuuuuuuuuudoku.utils.Move2i;
import fr.polytech.suuuuuuuuuuudoku.utils.Pair;
import fr.polytech.suuuuuuuuuuudoku.utils.Vec2i;

import java.util.*;

/**
 * Represents a multi-grid Sudoku grid.
 */
public class MultiGrid extends Solvable<MultiGrid> {
    /**
     * The grids that make up the multi-grid Sudoku.
     */
    private final Grid[] grids;

    /**
     * The offsets for each grid in the multi-grid Sudoku.
     */
    private final Vec2i[] offsets;

    /**
     * The list of moves made on the multi-grid Sudoku.
     */
    private final List<Move2i> moves = new ArrayList<>();

    /**
     * The size of the multi-grid Sudoku.
     */
    private final Vec2i size;

    /**
     * The possibilities for empty cells in the multi-grid Sudoku.
     */
    private HashMap<Vec2i, Set<Integer>> emptyCellsPossibilities = new HashMap<>();

    /**
     * Constructs a MultiGrid with the specified grids and offset.
     *
     * @param grids the list of grids
     */
    public MultiGrid(List<Pair<Vec2i, Grid>> grids) {
        super(grids.getFirst().getSecond().getSymbols());

        // Sort the grids by their position to ensure correct display
        grids.sort(Comparator.comparing((Pair<Vec2i, Grid> pair) -> pair.getFirst().getY())
                             .thenComparing(pair -> pair.getFirst().getX()));
        this.grids = grids.stream().map(Pair::getSecond).toArray(Grid[]::new);
        this.offsets = grids.stream().map(Pair::getFirst).toArray(Vec2i[]::new);

        int maxX = grids.stream()
                        .mapToInt(pair -> pair.getFirst().getX() + pair.getSecond().length())
                        .max()
                        .orElse(0);

        int maxY = grids.stream()
                        .mapToInt(pair -> pair.getFirst().getY() + pair.getSecond().length())
                        .max()
                        .orElse(0);

        this.size = new Vec2i(maxX, maxY);
        fillOverlappingCells();
        computeAllEmptyCellsPossibilities();
    }

    /**
     * Constructs a MultiGrid from another MultiGrid.
     *
     * @param other the other MultiGrid
     */
    public MultiGrid(MultiGrid other) {
        super(other.symbols);
        this.grids = Arrays.stream(other.grids).map(Grid::new).toArray(Grid[]::new);
        this.emptyCellsPossibilities = new HashMap<>(other.emptyCellsPossibilities);
        this.moves.addAll(other.moves);
        this.size = other.size;
        this.offsets = Arrays.stream(other.offsets).map(Vec2i::new).toArray(Vec2i[]::new);
    }

    /**
     * Gets a random offset for the grids.
     * The offset is chosen randomly from a list of predefined offsets.
     *
     * @return the random offset
     */
    public static Vec2i[] getRandomOffset() {
        return new Vec2i[][]{
                new Vec2i[]{
                        new Vec2i(0, 0),
                        new Vec2i(12, 0),
                        new Vec2i(6, 6),
                        new Vec2i(0, 12),
                        new Vec2i(12, 12)
                },
                new Vec2i[]{
                        new Vec2i(6, 0),
                        new Vec2i(0, 6),
                        new Vec2i(6, 6),
                        new Vec2i(12, 6),
                        new Vec2i(6, 12)
                }
        }[(int) (Math.random() * 2)];
    }

    /**
     * Gets the size of the multi-grid Sudoku.
     *
     * @return the size of the multi-grid Sudoku
     */
    public Vec2i getSize() {
        return size;
    }

    /**
     * Checks if the position is not in the multi-grid Sudoku.
     *
     * @param pos the position to check
     * @return true if the position is not in the multi-grid Sudoku, false otherwise
     */
    public boolean isNotInGrid(Vec2i pos) {
        return !isInGrid(pos);
    }

    /**
     * Checks if the position is in the multi-grid Sudoku.
     *
     * @param pos the position to check
     * @return true if the position is in the multi-grid Sudoku, false otherwise
     */
    @Override
    public boolean isInGrid(Vec2i pos) {
        for (int i = 0; i < offsets.length; i++) {
            Vec2i offset = offsets[i];
            Vec2i size = grids[i].getSize();
            if (pos.getX() >= offset.getX() && pos.getX() < offset.getX() + size.getX()
                    && pos.getY() >= offset.getY() && pos.getY() < offset.getY() + size.getY()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the grids that make up the multi-grid Sudoku.
     *
     * @return the grids that make up the multi-grid Sudoku
     */
    public Grid[] getGrids() {
        return grids;
    }

    /**
     * Creates a shallow copy of this MultiGrid.
     *
     * @return a new MultiGrid instance that is a shallow copy of this MultiGrid
     */
    @Override
    public MultiGrid shallowCopy() {
        return new MultiGrid(this);
    }

    /**
     * Checks if all constraints are satisfied in the multi-grid Sudoku.
     *
     * @param skip_not_empty if true, skips checking non-empty cells
     * @return true if all constraints are satisfied, false otherwise
     */
    @Override
    public boolean areConstraintsSatisfied(boolean skip_not_empty) {
        return Arrays.stream(grids).allMatch(grid -> grid.areConstraintsSatisfied(skip_not_empty));
    }

    /**
     * Computes all the possibilities for empty cells in the multi-grid Sudoku.
     */
    @Override
    public void computeAllEmptyCellsPossibilities() {
        for (Grid grid : grids) {
            grid.computeAllEmptyCellsPossibilities();
        }
        gatherEmptyCellsPossibilities();
    }

    /**
     * Gathers all the possibilities for empty cells in the multi-grid Sudoku.
     * This method is used to gather the possibilities for the sub-grids.
     */
    public void gatherEmptyCellsPossibilities() {
        emptyCellsPossibilities.clear();
        for (int i = 0; i < grids.length; i++) {
            for (var entry : grids[i].getEmptyCellsPossibilities().entrySet()) {
                var pos = new Vec2i(entry.getKey()).add(offsets[i]);
                if (emptyCellsPossibilities.containsKey(pos)) {
                    emptyCellsPossibilities.get(pos).addAll(entry.getValue());
                } else {
                    emptyCellsPossibilities.put(pos, new HashSet<>(entry.getValue()));
                }
            }
        }
    }

    /**
     * Places a value at the specified position in the multi-grid Sudoku.
     * This take care of placing the value in the correct sub-grids.
     *
     * @param pos                 the position to place the value
     * @param value               the value to place
     * @param updatePossibilities if true, updates the possibilities for empty cells
     * @param store_move          if true, stores the move
     */
    @Override
    public void placeUnchecked(Vec2i pos, Integer value, boolean updatePossibilities, boolean store_move) {
        Integer oldValue = null;
        for (int i = 0; i < offsets.length; i++) {
            Vec2i offset = offsets[i];
            if (pos.getX() >= offset.getX() && pos.getX() < offset.getX() + grids[i].length()
                    && pos.getY() >= offset.getY() && pos.getY() < offset.getY() + grids[i].length()) {
                var subGridPosition = new Vec2i(pos).substract(offset);
                oldValue = grids[i].getSymbolAt(subGridPosition);
                grids[i].placeUnchecked(subGridPosition, value, updatePossibilities, store_move);
            }
        }

        if (updatePossibilities) {
            gatherEmptyCellsPossibilities();
        }

        if (store_move) {
            moves.add(new Move2i(pos, value, oldValue));
        }
    }

    /**
     * Gets the symbol at the specified position in the multi-grid Sudoku.
     *
     * @param pos the position to get the symbol
     * @return the symbol at the position
     */
    public Integer getSymbolAt(Vec2i pos) {
        for (int i = 0; i < offsets.length; i++) {
            Vec2i offset = offsets[i];
            Vec2i size = grids[i].getSize();
            if (pos.getX() >= offset.getX() && pos.getX() < offset.getX() + size.getX()
                    && pos.getY() >= offset.getY() && pos.getY() < offset.getY() + size.getY()) {
                return grids[i].getSymbolAt(pos.substract(offset));
            }
        }
        return null;
    }

    /**
     * Gets the grid for the specified position in the multi-grid Sudoku.
     *
     * @param x the x-coordinate of the position
     * @param y the y-coordinate of the position
     * @return a pair containing the index of the grid and the grid itself, or null if the position is not in the
     * multi-grid Sudoku
     */
    public Pair<Integer, Grid> getGridFor(int x, int y) {
        for (int i = 0; i < offsets.length; i++) {
            Vec2i offset = offsets[i];
            Vec2i size = grids[i].getSize();
            if (x >= offset.getX() && x < offset.getX() + size.getX()
                    && y >= offset.getY() && y < offset.getY() + size.getY()) {
                return new Pair<>(i, grids[i]);
            }
        }
        return null;
    }

    /**
     * Returns the possibilities for each empty cell in the grid.
     *
     * @return the possibilities for each empty cell in the grid
     */
    @Override
    public Map<Vec2i, Set<Integer>> getEmptyCellsPossibilities() {
        return emptyCellsPossibilities;
    }

    /**
     * Gets the list of moves made on the multi-grid Sudoku.
     *
     * @return the list of moves
     */
    public List<Move2i> getMoves() {
        return moves;
    }

    /**
     * Clears all the moves made on the multi-grid Sudoku.
     */
    @Override
    public void cleanMoves() {
        moves.clear();
    }

    /**
     * Undoes the last move made on the multi-grid Sudoku.
     *
     * @param updatePossibilities if true, updates the possibilities for empty cells
     */
    @Override
    public void undoLastMove(boolean updatePossibilities) {
        if (moves.isEmpty()) {
            return;
        }

        Move2i lastMove = moves.removeLast();
        placeUnchecked(lastMove.position(), lastMove.previous_value(), updatePossibilities, false);
    }

    /**
     * Gets the offsets for each grid in the multi-grid Sudoku.
     *
     * @return the offsets for each grid
     */
    public Vec2i[] getOffsets() {
        return offsets;
    }

    /**
     * Fills the overlapping cells of the grids with the same value if one of them has it.
     */
    public void fillOverlappingCells() {
        for (int i = 0; i < offsets.length; i++) {
            Box2D box = new Box2D(offsets[i], grids[i].getSize());
            for (int j = 0; j < offsets.length; j++) {
                if (i == j) {
                    continue;
                }
                Box2D otherBox = new Box2D(offsets[j], grids[j].getSize());
                Box2D overlap = box.overlap(otherBox);
                if (overlap != null) {
                    for (int x = overlap.x(); x < overlap.dx(); x++) {
                        for (int y = overlap.y(); y < overlap.dy(); y++) {
                            Vec2i pos = new Vec2i(x, y);
                            if (grids[i].getSymbolAt((new Vec2i(pos)).substract(offsets[i])) == null
                                    && grids[j].getSymbolAt((new Vec2i(pos)).substract(offsets[j])) != null) {
                                Integer symbol = grids[j].getSymbolAt((new Vec2i(pos)).substract(offsets[j]));
                                if (symbol != null) {
                                    placeUnchecked(pos, symbol, false, false);
                                }
                            } else if (grids[j].getSymbolAt((new Vec2i(pos)).substract(offsets[j])) == null
                                    && grids[i].getSymbolAt((new Vec2i(pos)).substract(offsets[i])) != null) {
                                Integer symbol = grids[i].getSymbolAt((new Vec2i(pos)).substract(offsets[i]));
                                if (symbol != null) {
                                    placeUnchecked(pos, symbol, false, false);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
