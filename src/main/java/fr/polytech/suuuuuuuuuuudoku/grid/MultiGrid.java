package fr.polytech.suuuuuuuuuuudoku.grid;

import fr.polytech.suuuuuuuuuuudoku.algorithm.*;
import fr.polytech.suuuuuuuuuuudoku.constraints.BlockEqualityMGConstraint;
import fr.polytech.suuuuuuuuuuudoku.constraints.MultiGridConstraint;

import java.util.*;

public class MultiGrid extends Solvable<Vec3i> implements ShallowCopyable<MultiGrid> {
    final List<MultiGridConstraint> constraints;
    private final Grid[] grids;
    private final Vec2i[] paddings;
    private final List<Move3i> moves = new ArrayList<>();
    private HashMap<Vec3i, Set<Integer>> emptyCellsPossibilities = new HashMap<>();

    public MultiGrid(List<Pair<Vec2i, Grid>> grids) {
        super(grids.getFirst().getSecond().getSymbols());

        grids.sort(Comparator.comparing((Pair<Vec2i, Grid> pair) -> pair.getFirst().getLine())
                             .thenComparing(pair -> pair.getFirst().getColumn()));
        this.grids = grids.stream().map(Pair::getSecond).toArray(Grid[]::new);
        this.paddings = grids.stream().map(Pair::getFirst).toArray(Vec2i[]::new);
        this.constraints = new ArrayList<>();

        int indexI = 0;
        for (Pair<Vec2i, Grid> i : grids) {
            int indexJ = 0;
            for (Pair<Vec2i, Grid> j : grids) {
                if (i != j) {
                    var usedI = new Box2D(i.getFirst().getLine(), i.getFirst().getColumn(), i.getSecond().length(),
                            i.getSecond().length());
                    var usedJ = new Box2D(j.getFirst().getLine(), j.getFirst().getColumn(), j.getSecond().length(),
                            j.getSecond().length());
                    var overlap = usedI.overlap(usedJ);
                    if (overlap != null) {
                        BlockEqualityMGConstraint constraint = new BlockEqualityMGConstraint(
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
        System.out.println("couco");


    }

    public MultiGrid(Grid[] grids, List<MultiGridConstraint> constraints, Set<Integer> symbols) {
        super(symbols);
        this.constraints = constraints;
        this.grids = grids;
        paddings = new Vec2i[0];
    }

    public MultiGrid(MultiGrid other) {
        super(other.symbols);
        this.constraints = other.constraints;
        this.grids = Arrays.stream(other.grids).map(Grid::new).toArray(Grid[]::new);
        this.emptyCellsPossibilities = new HashMap<>(other.emptyCellsPossibilities);
        this.moves.addAll(other.moves);
        paddings = new Vec2i[0];
    }

    public List<MultiGridConstraint> getConstraints() {
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
                            new Vec3i(pos.getLine(), pos.getColumn(), finalI),
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
                            new Vec3i(pos.getLine(), pos.getColumn(), finalI),
                            possibilities
                    ));
        }

        applyAllMultiGridConstraints();
    }

    public void applyAllMultiGridConstraints() {
        emptyCellsPossibilities.forEach(
                (pos, possibilities) -> constraints.stream()
                                                   .filter(constraint -> constraint.isPosAffected(pos))
                                                   .forEach(constraint -> constraint.getPossibilities(grids, pos).ifPresent(possibilities::retainAll)));
    }

    @Override
    public void placeUnchecked(Vec3i pos, Integer value, boolean updatePossibilities, boolean store_move) {
        assert pos.getDepth() < grids.length;
        System.out.println("MG: placing pos: " + pos + " value: " + value);

        Integer oldValue = grids[pos.getDepth()].getSymbolAt(pos.getLine(), pos.getColumn());
        grids[pos.getDepth()].placeUnchecked(new Vec2i(pos.getLine(), pos.getColumn()), value, updatePossibilities,
                false);

        constraints.stream()
                   .filter(c -> c instanceof BlockEqualityMGConstraint)
                   .map(c -> (BlockEqualityMGConstraint) c)
                   .filter(c -> c.isPosAffected(pos)).forEach(constraint -> {
                       Vec3i correspondingPos = constraint.getCorrespondingPosition(pos);
                       System.out.println("MG: Placing corresponding pos: " + correspondingPos + " value: " + value);
                       grids[correspondingPos.getDepth()].placeUnchecked(
                               new Vec2i(correspondingPos.getLine(), correspondingPos.getColumn()),
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
        assert pos.getDepth() < grids.length;
        return grids[pos.getDepth()].getSymbolAt(pos.getLine(), pos.getColumn());
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
