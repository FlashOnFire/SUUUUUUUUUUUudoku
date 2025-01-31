package fr.polytech.suuuuuuuuuuudoku.grid;

import fr.polytech.suuuuuuuuuuudoku.algorithm.*;
import fr.polytech.suuuuuuuuuuudoku.constraints.AbstractConstraint;
import fr.polytech.suuuuuuuuuuudoku.constraints.BlockEqualityConstraint;

import java.util.*;

public class MultiGrid extends Solvable<Vec3i> implements ShallowCopyable<MultiGrid> {
    final List<AbstractConstraint<Grid[], Vec3i>> constraints;
    private final Grid[] grids;
    private final Vec2i[] paddings;
    private final List<Move3i> moves = new ArrayList<>();
    private final Vec2i size;
    private HashMap<Vec3i, Set<Integer>> emptyCellsPossibilities = new HashMap<>();

    public MultiGrid(List<Pair<Vec2i, Grid>> grids) {
        super(grids.getFirst().getSecond().getSymbols());

        grids.sort(Comparator.comparing((Pair<Vec2i, Grid> pair) -> pair.getFirst().getY())
                             .thenComparing(pair -> pair.getFirst().getX()));
        this.grids = grids.stream().map(Pair::getSecond).toArray(Grid[]::new);
        this.paddings = grids.stream().map(Pair::getFirst).toArray(Vec2i[]::new);
        this.constraints = new ArrayList<>();

        int indexI = 0;
        for (Pair<Vec2i, Grid> i : grids) {
            int indexJ = 0;
            for (Pair<Vec2i, Grid> j : grids) {
                if (i != j) {
                    var usedI = new Box2D(i.getFirst().getX(), i.getFirst().getY(), i.getSecond().length(),
                            i.getSecond().length());
                    var usedJ = new Box2D(j.getFirst().getX(), j.getFirst().getY(), j.getSecond().length(),
                            j.getSecond().length());
                    var overlap = usedI.overlap(usedJ);
                    if (overlap != null) {
                        BlockEqualityConstraint constraint = new BlockEqualityConstraint(
                                indexI,
                                overlap.substract(usedI),
                                indexJ,
                                overlap.substract(usedJ)
                        );
                        if (!constraints.contains(constraint)) {
                            constraints.add(constraint);
                        }
                    }
                }
                indexJ++;
            }
            indexI++;
        }

        // Compute the size of the MultiGrid
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
        this.constraints = other.constraints;
        this.grids = Arrays.stream(other.grids).map(Grid::new).toArray(Grid[]::new);
        this.emptyCellsPossibilities = new HashMap<>(other.emptyCellsPossibilities);
        this.moves.addAll(other.moves);
        this.size = other.size;
        paddings = new Vec2i[0];
    }

    public Vec2i getSize() {
        return size;
    }

    public List<AbstractConstraint<Grid[], Vec3i>> getConstraints() {
        return constraints;
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
        return Arrays.stream(grids).allMatch(grid -> grid.areConstraintsSatisfied(skip_not_empty))
                && constraints.stream().allMatch(constraint -> constraint.isSatisfied(grids));
    }

    @Override
    public void computeAllEmptyCellsPossibilities() {
        emptyCellsPossibilities.clear();
        for (int i = 0; i < grids.length; i++) {
            grids[i].computeAllEmptyCellsPossibilities();
            int finalI = i;
            grids[i].getEmptyCellsPossibilities().forEach(
                    (pos, possibilities) -> emptyCellsPossibilities.put(
                            new Vec3i(pos.getX(), pos.getY(), finalI),
                            possibilities
                    ));
        }

        applyAllMultiGridConstraints();
    }

    public void gatherEmptyCellsPossibilitiesFromGrids() {
        emptyCellsPossibilities.clear();
        for (int i = 0; i < grids.length; i++) {
            int finalI = i;
            grids[i].getEmptyCellsPossibilities().forEach(
                    (pos, possibilities) -> emptyCellsPossibilities.put(
                            new Vec3i(pos.getX(), pos.getY(), finalI),
                            possibilities
                    ));
        }

        // TODO : only apply on the cells affected by the change
        applyAllMultiGridConstraints();
    }

    public void applyAllMultiGridConstraints() {
        emptyCellsPossibilities.forEach(
                (pos, possibilities) -> constraints.stream()
                                                   .filter(constraint -> constraint.isPosAffected(pos))
                                                   .forEach(constraint -> constraint.getPossibilities(grids, pos).ifPresent(possibilities::retainAll)));
    }

    public void placeUncheckedPaddingBased(Vec2i pos, Integer value, boolean updatePossibilities, boolean store_move) {
        for (int i = 0; i < paddings.length; i++) {
            Vec2i padding = paddings[i];
            if (pos.getX() >= padding.getX() && pos.getX() < padding.getX() + grids[i].length()
                    && pos.getY() >= padding.getY() && pos.getY() < padding.getY() + grids[i].length()) {
                placeUnchecked(new Vec3i(pos.getX() - padding.getX(), pos.getY() - padding.getY(), i), value,
                        updatePossibilities, store_move);
                return;
            }
        }
    }

    @Override
    public void placeUnchecked(Vec3i pos, Integer value, boolean updatePossibilities, boolean store_move) {
        assert pos.getZ() < grids.length;
        System.out.println("MG: placing pos: " + pos + " value: " + value);

        Integer oldValue = grids[pos.getZ()].getSymbolAt(pos.getX(), pos.getY());
        grids[pos.getZ()].placeUnchecked(new Vec2i(pos.getX(), pos.getY()), value, updatePossibilities, false);

        constraints.stream()
                   .filter(c -> c instanceof BlockEqualityConstraint)
                   .map(c -> (BlockEqualityConstraint) c)
                   .filter(c -> c.isPosAffected(pos)).forEach(constraint -> {
                       Vec3i correspondingPos = constraint.getCorrespondingPosition(pos);
                       System.out.println("MG: Placing corresponding pos: " + correspondingPos + " value: " + value);
                       grids[correspondingPos.getZ()].placeUnchecked(
                               new Vec2i(correspondingPos.getX(), correspondingPos.getY()),
                               value,
                               updatePossibilities,
                               false
                       );
                   });


        // The possibilities of the individual affected grids are already updated by the calls to placeUnchecked()
        // So we don't have to recompute EVERYTHING
        // But, we can't just check the new possibilities of the affected grids, because if we place an empty cell,
        // new possibilities will be added, and we cannot assume they are correct without checking possibilities of
        // overlapping grids.
        // So we need to gather all possibilities from all grids and apply all constraints.
        if (updatePossibilities) {
            gatherEmptyCellsPossibilitiesFromGrids();
            applyAllMultiGridConstraints();
        }

        if (store_move) {
            moves.add(new Move3i(pos, value, oldValue));
        }
    }

    public Integer getSymbolAt(Vec3i pos) {
        assert pos.getZ() < grids.length;
        return grids[pos.getZ()].getSymbolAt(pos.getX(), pos.getY());
    }

    @Override
    public Map<Vec3i, Set<Integer>> getEmptyCellsPossibilities() {
        return emptyCellsPossibilities;
    }

    public List<Move3i> getMoves() {
        return moves;
    }

    public Vec2i[] getPaddings() {
        return paddings;
    }
}
