package fr.polytech.suuuuuuuuuuudoku.grid;

import fr.polytech.suuuuuuuuuuudoku.algorithm.*;
import fr.polytech.suuuuuuuuuuudoku.constraints.AbstractConstraint;
import fr.polytech.suuuuuuuuuuudoku.constraints.BlockEqualityMGConstraint;

import java.util.*;

public class MultiGrid extends Solvable<Vec3i> implements ShallowCopyable<MultiGrid> {
    final List<AbstractConstraint<Grid[], Vec3i>> constraints;
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

    public MultiGrid(Grid[] grids, List<AbstractConstraint<Grid[], Vec3i>> constraints, Set<Integer> symbols) {
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


//    public static MultiGrid getExemple() {
//        Grid[] grids = new Grid[5];
//        var symbolSet = SymbolSets.generateSymbols(9);
//        grids[0] = new Grid(
//                new Integer[][]
//                        {
//                                {7, 1, 2, 6, 3, 8, 4, 9, 5},
//                                {9, 5, 3, 4, 1, 2, 8, 7, 6},
//                                {4, 8, 6, 9, 5, 7, 2, 1, 3},
//                                {2, 3, 4, 1, 7, 6, 5, 8, 9},
//                                {5, 9, 1, 3, 8, 4, 7, 6, 2},
//                                {8, 6, 7, 2, 9, 5, 3, 4, 1},
//                                {1, 7, 8, 5, 2, 9, 6, 3, 4},
//                                {3, 4, 5, 8, 6, 1, 9, 2, 7},
//                                {6, 2, 9, 7, 4, 3, 1, 5, 8},
//                        }
//                ,
//                symbolSet
//        );
//
//        grids[1] = new Grid(
//                new Integer[][]
//                        {
//                                {6, 3, 4, 1, 7, 2, 9, 5, 8},
//                                {9, 2, 7, 8, 5, 3, 1, 6, 4},
//                                {1, 5, 8, 9, 4, 6, 7, 3, 2},
//                                {2, 6, 5, 7, 3, 8, 4, 9, 1},
//                                {3, 7, 9, 2, 1, 4, 6, 8, 5},
//                                {8, 4, 1, 6, 9, 5, 2, 7, 3},
//                                {7, 9, 3, 5, 2, 1, 8, 4, 6},
//                                {5, 1, 6, 4, 8, 7, 3, 2, 9},
//                                {4, 8, 2, 3, 6, 9, 5, 1, 7}
//                        }
//                ,
//                symbolSet
//        );
//        grids[2] = new Grid(
//                new Integer[][]
//                        {
//                                {5, 9, 1, 2, 4, 8, 3, 7, 6},
//                                {6, 2, 3, 9, 1, 7, 5, 8, 4},
//                                {8, 4, 7, 5, 6, 3, 2, 1, 9},
//                                {2, 1, 9, 7, 5, 4, 8, 6, 3},
//                                {4, 8, 6, 1, 3, 9, 7, 2, 5},
//                                {3, 7, 5, 8, 2, 6, 4, 9, 1},
//                                {9, 5, 8, 4, 7, 1, 6, 3, 2},
//                                {1, 6, 4, 3, 8, 2, 9, 5, 7},
//                                {7, 3, 2, 6, 9, 5, 1, 4, 8}
//                        }
//                ,
//                symbolSet
//        );
//
//        grids[3] = new Grid(
//                new Integer[][]
//                        {
//                                {6, 2, 5, 8, 1, 4, 7, 9, 3},
//                                {9, 8, 4, 2, 7, 3, 5, 1, 6},
//                                {7, 3, 1, 6, 5, 9, 4, 8, 2},
//                                {5, 6, 2, 9, 8, 7, 1, 3, 4},
//                                {3, 1, 7, 5, 4, 2, 9, 6, 8},
//                                {8, 4, 9, 3, 6, 1, 2, 5, 7},
//                                {2, 7, 6, 1, 3, 5, 8, 4, 9},
//                                {1, 9, 3, 4, 2, 8, 6, 7, 5},
//                                {4, 5, 8, 7, 9, 6, 3, 2, 1}
//                        }
//                ,
//                symbolSet
//        );
//        grids[4] = new Grid(
//                new Integer[][]
//                        {
//                                {8, 4, 6, 5, 3, 7, 1, 9, 2},
//                                {3, 2, 9, 6, 1, 8, 5, 7, 4},
//                                {5, 1, 7, 4, 9, 2, 6, 3, 8},
//                                {7, 5, 2, 1, 4, 9, 3, 8, 6},
//                                {4, 3, 8, 7, 6, 5, 2, 1, 9},
//                                {9, 6, 1, 2, 8, 3, 7, 4, 5},
//                                {1, 9, 3, 8, 2, 6, 4, 5, 7},
//                                {2, 7, 4, 9, 5, 1, 8, 6, 3},
//                                {6, 8, 5, 3, 7, 4, 9, 2, 1}
//                        }
//                ,
//                symbolSet
//        );
//
//        List<AbstractConstraint<Grid[], Vec3i>> constraints = List.of(new AbstractConstraint<Grid[], Vec3i>[]{
//                new BlockEqualityMGConstraint(0,
//                        new Box2D(6, 6, 3, 3),
//                        1,
//                        new Box2D(0, 0, 3, 3)),
//                new BlockEqualityMGConstraint(1,
//                        new Box2D(6, 0, 3, 3),
//                        2,
//                        new Box2D(0, 6, 3, 3)),
//                new BlockEqualityMGConstraint(1,
//                        new Box2D(0, 6, 3, 3),
//                        3,
//                        new Box2D(6, 0, 3, 3)),
//                new BlockEqualityMGConstraint(1,
//                        new Box2D(6, 6, 3, 3),
//                        4,
//                        new Box2D(0, 0, 3, 3)),
//        });
//
//        return new MultiGrid(grids, constraints, symbolSet);
//    }

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

        // TODO : only apply on the cells affected by the change
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
