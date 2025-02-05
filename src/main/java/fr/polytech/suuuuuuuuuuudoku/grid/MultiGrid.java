package fr.polytech.suuuuuuuuuuudoku.grid;

import fr.polytech.suuuuuuuuuuudoku.algorithm.Pair;
import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec2i;

import java.util.*;

public class MultiGrid extends Solvable<MultiGrid> {
    private final Grid[] grids;
    private final Vec2i[] paddings;
    private final List<Move2i> moves = new ArrayList<>();
    private final Vec2i size;
    private HashMap<Vec2i, Set<Integer>> emptyCellsPossibilities = new HashMap<>();

    public MultiGrid(List<Pair<Vec2i, Grid>> grids) {
        super(grids.getFirst().getSecond().getSymbols());

        grids.sort(Comparator.comparing((Pair<Vec2i, Grid> pair) -> pair.getFirst().getY())
                             .thenComparing(pair -> pair.getFirst().getX()));
        this.grids = grids.stream().map(Pair::getSecond).toArray(Grid[]::new);
        this.paddings = grids.stream().map(Pair::getFirst).toArray(Vec2i[]::new);

        int maxX = grids.stream()
                        .mapToInt(pair -> pair.getFirst().getX() + pair.getSecond().length())
                        .max()
                        .orElse(0);

        int maxY = grids.stream()
                        .mapToInt(pair -> pair.getFirst().getY() + pair.getSecond().length())
                        .max()
                        .orElse(0);

        this.size = new Vec2i(maxX, maxY);
    }

    public MultiGrid(MultiGrid other) {
        super(other.symbols);
        this.grids = Arrays.stream(other.grids).map(Grid::new).toArray(Grid[]::new);
        this.emptyCellsPossibilities = new HashMap<>(other.emptyCellsPossibilities);
        this.moves.addAll(other.moves);
        this.size = other.size;
        this.paddings = Arrays.stream(other.paddings).map(Vec2i::new).toArray(Vec2i[]::new);
    }

    public Vec2i getSize() {
        return size;
    }

    public boolean isNotInGrid(Vec2i pos) {
        return !isInGrid(pos);
    }

    public boolean isInGrid(Vec2i pos) {
        // Check if the position is in one of the grids
        for (int i = 0; i < paddings.length; i++) {
            Vec2i padding = paddings[i];
            Vec2i size = grids[i].getSize();
            if (pos.getX() >= padding.getX() && pos.getX() < padding.getX() + size.getX()
                    && pos.getY() >= padding.getY() && pos.getY() < padding.getY() + size.getY()) {
                return true;
            }
        }
        return false;
    }

    public Grid[] getGrids() {
        return grids;
    }

    @Override
    public MultiGrid shallowCopy() {
        return new MultiGrid(this);
    }

    @Override
    public boolean areConstraintsSatisfied(boolean skip_not_empty) {
        return Arrays.stream(grids).allMatch(grid -> grid.areConstraintsSatisfied(skip_not_empty));
    }

    @Override
    public void computeAllEmptyCellsPossibilities() {
        for (Grid grid : grids) {
            grid.computeAllEmptyCellsPossibilities();
        }
        gatherEmptyCellsPossibilities();
    }

    public void gatherEmptyCellsPossibilities() {
        emptyCellsPossibilities.clear();
        for (int i = 0; i < grids.length; i++) {
            for (var entry : grids[i].getEmptyCellsPossibilities().entrySet()) {
                var pos = new Vec2i(entry.getKey()).add(paddings[i]);
                if (emptyCellsPossibilities.containsKey(pos)) {
                    emptyCellsPossibilities.get(pos).addAll(entry.getValue());
                } else {
                    emptyCellsPossibilities.put(pos, new HashSet<>(entry.getValue()));
                }
            }
        }
    }

    @Override
    public void placeUnchecked(Vec2i pos, Integer value, boolean updatePossibilities, boolean store_move) {
        Integer oldValue = null;
        for (int i = 0; i < paddings.length; i++) {
            Vec2i padding = paddings[i];
            if (pos.getX() >= padding.getX() && pos.getX() < padding.getX() + grids[i].length()
                    && pos.getY() >= padding.getY() && pos.getY() < padding.getY() + grids[i].length()) {
                var subGridPosition = new Vec2i(pos).substract(padding);
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


    public Integer getSymbolAt(Vec2i pos) {
        for (int i = 0; i < paddings.length; i++) {
            Vec2i padding = paddings[i];
            Vec2i size = grids[i].getSize();
            if (pos.getX() >= padding.getX() && pos.getX() < padding.getX() + size.getX()
                    && pos.getY() >= padding.getY() && pos.getY() < padding.getY() + size.getY()) {
                return grids[i].getSymbolAt(pos.substract(padding));
            }
        }
        return null;
    }

    public Pair<Integer, Grid> getGridFor(int x, int y) {
        for (int i = 0; i < paddings.length; i++) {
            Vec2i padding = paddings[i];
            Vec2i size = grids[i].getSize();
            if (x >= padding.getX() && x < padding.getX() + size.getX()
                    && y >= padding.getY() && y < padding.getY() + size.getY()) {
                return new Pair<>(i, grids[i]);
            }
        }
        return null;
    }

    @Override
    public Map<Vec2i, Set<Integer>> getEmptyCellsPossibilities() {
        return emptyCellsPossibilities;
    }

    public List<Move2i> getMoves() {
        return moves;
    }

    @Override
    public void undoLastMove(boolean updatePossibilities) {
        if (moves.isEmpty()) {
            return;
        }

        Move2i lastMove = moves.removeLast();
        placeUnchecked(lastMove.position(), lastMove.previous_value(), updatePossibilities, false);
    }

    public Vec2i[] getPaddings() {
        return paddings;
    }

    public void display() {
        for (int y = 0; y < size.getY(); y++) {
            for (int x = 0; x < size.getX(); x++) {
                Pair<Integer, Grid> grid = getGridFor(x, y);
                if (grid != null) {
                    Vec2i subGridPosition = new Vec2i(x, y).substract(paddings[grid.getFirst()]);
                    System.out.print(grid.getSecond().getSymbolAt(subGridPosition) != null ?
                            grid.getSecond().getSymbolAt(subGridPosition) : ".");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }
}
