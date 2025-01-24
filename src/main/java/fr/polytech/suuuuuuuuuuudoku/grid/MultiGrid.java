package fr.polytech.suuuuuuuuuuudoku.grid;

import fr.polytech.suuuuuuuuuuudoku.algorithm.ShallowCopyable;
import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec2i;
import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec3i;
import fr.polytech.suuuuuuuuuuudoku.constraints.BlockEqualityMGConstraint;
import fr.polytech.suuuuuuuuuuudoku.constraints.MultiGridConstraint;

import java.util.*;

public class MultiGrid extends Solvable<Vec3i> implements ShallowCopyable<MultiGrid> {
    final List<MultiGridConstraint> constraints;
    private final Grid[] grids;

    private HashMap<Vec3i, Set<Integer>> emptyCellsPossibilities = new HashMap<>();

    public MultiGrid(Grid[] grids, List<MultiGridConstraint> constraints, Set<Integer> symbols) {
        super(symbols);
        this.constraints = constraints;
        this.grids = grids;
    }

    public MultiGrid(MultiGrid other) {
        super(other.symbols);
        this.constraints = other.constraints;
        this.grids = Arrays.stream(other.grids).map(Grid::new).toArray(Grid[]::new);
        this.emptyCellsPossibilities = new HashMap<>(other.emptyCellsPossibilities);
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

        applyAllMultiGridConstraints();
    }

    public void applyAllMultiGridConstraints() {
        emptyCellsPossibilities.forEach(
                (pos, possibilities) -> constraints.stream()
                        .filter(constraint -> constraint.isPosAffected(pos))
                        .forEach(constraint -> constraint.getPossibilities(grids, pos).ifPresent(possibilities::retainAll)));
    }

    @Override
    public void placeUnchecked(Vec3i pos, Integer value, boolean updatePossibilities) {
        assert pos.getZ() < grids.length;
        System.out.println("MG: placing pos: " + pos + " value: " + value);

        grids[pos.getZ()].placeUnchecked(new Vec2i(pos.getX(), pos.getY()), value, updatePossibilities);

        constraints.stream()
                .filter(c -> c instanceof BlockEqualityMGConstraint)
                .map(c -> (BlockEqualityMGConstraint) c)
                .filter(c -> c.isPosAffected(pos)).forEach(constraint -> {
                    Vec3i correspondingPos = constraint.getCorrespondingPosition(pos);
                    System.out.println("MG: Placing corresponding pos: " + correspondingPos + " value: " + value);
                    grids[correspondingPos.getZ()].placeUnchecked(
                            new Vec2i(correspondingPos.getX(), correspondingPos.getY()),
                            value,
                            updatePossibilities
                    );
                });


        // The possibilities of the individual affected grids are already updated by the calls to placeUnchecked()
        // So we don't have to recompute EVERYTHING
        // But, we can't just check the new possibilities of the affected grids, because if we place an empty cell,
        // new possibilities will be added, and we cannot assume they are correct without checking possibilities of overlapping grids.
        // So we need to gather all possibilities from all grids and apply all constraints.
        if (updatePossibilities) {
            gatherEmptyCellsPossibilitiesFromGrids();
            applyAllMultiGridConstraints();
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
}
