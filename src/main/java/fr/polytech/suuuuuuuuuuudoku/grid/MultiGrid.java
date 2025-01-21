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

    public void computeChangedEmptyCellsPossibilities(Vec3i pos, boolean skip_not_empty) {
        grids[pos.getZ()].computeChangedEmptyCellsPossibilities(new Vec2i(pos.getX(), pos.getY()), skip_not_empty);

        // TODO : only apply on the cells affected by the change
        applyAllMultiGridConstraints();
    }

    public void applyAllMultiGridConstraints() {
        emptyCellsPossibilities.forEach(
                (pos, possibilities) -> constraints.stream()
                        .filter(constraint -> constraint.isPosAffected(pos))
                        .forEach(constraint -> {
                            constraint.getPossibilities(grids, pos).ifPresent(possibilities::retainAll);
                        }));
    }

    @Override
    public void placeUnchecked(Vec3i pos, Integer value, boolean updatePossibilities) {
        assert pos.getZ() < grids.length;
        Integer oldValue = grids[pos.getZ()].getSymbolAt(pos.getX(), pos.getY());
        grids[pos.getZ()].placeUnchecked(new Vec2i(pos.getX(), pos.getY()), value, updatePossibilities);

        if (oldValue == null && value != null) {
            emptyCellsPossibilities.remove(pos);
            constraints.stream()
                    .filter(c -> c instanceof BlockEqualityMGConstraint)
                    .map(c -> (BlockEqualityMGConstraint) c)
                    .forEach(constraint -> {
                        if (constraint.isPosAffected(pos)) {
                            emptyCellsPossibilities.remove(constraint.getCorrespondingPosition(pos));
                        }
                    });
        } else if (oldValue != null && value == null) {
            emptyCellsPossibilities.put(pos, new HashSet<>(symbols));
            constraints.stream()
                    .filter(c -> c instanceof BlockEqualityMGConstraint)
                    .map(c -> (BlockEqualityMGConstraint) c)
                    .forEach(constraint -> {
                        if (constraint.isPosAffected(pos)) {
                            emptyCellsPossibilities.put(
                                    constraint.getCorrespondingPosition(pos),
                                    new HashSet<>(symbols)
                            );
                        }
                    });
        }

        // remove from emptyCellsPossibilities if null and vice versa
        if (updatePossibilities) {
            computeChangedEmptyCellsPossibilities(pos, true);
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
