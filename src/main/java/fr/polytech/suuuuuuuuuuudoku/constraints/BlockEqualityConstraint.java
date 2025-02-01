package fr.polytech.suuuuuuuuuuudoku.constraints;

import fr.polytech.suuuuuuuuuuudoku.algorithm.Box2D;
import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec2i;
import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec3i;
import fr.polytech.suuuuuuuuuuudoku.grid.Grid;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @param gridIndex1 The set of symbols to be checked within the block.
 */
public record BlockEqualityConstraint(int gridIndex1, Box2D block1, int gridIndex2,
                                      Box2D block2) implements AbstractConstraint<Grid[], Vec3i> {

    @Override
    public boolean isSatisfied(Grid[] grid) {
        for (int i = 0; i < block1.width(); i++) {
            for (int j = 0; j < block1.height(); j++) {
                if (!grid[gridIndex1].getSymbolAt(
                        block1.x() + i,
                        block1.y() + j
                ).equals(grid[gridIndex2].getSymbolAt(block2.x() + i, block2.y() + j))) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public Optional<Set<Integer>> getPossibilities(Grid[] grid, Vec3i pos) {
        if (pos.getZ() != gridIndex1 && pos.getZ() != gridIndex2) {
            return Optional.empty();
        }

        if (!isPosAffected(pos)) {
            return Optional.empty();
        }

        var possibilities = new HashSet<>(
                grid[pos.getZ()].getEmptyCellsPossibilities().get(new Vec2i(pos.getX(), pos.getY()))
        );
        var correspondingPos = getCorrespondingPosition(pos);
        possibilities.retainAll(grid[correspondingPos.getZ()].getEmptyCellsPossibilities().get(new Vec2i(correspondingPos.getX(), correspondingPos.getY())));

        return Optional.of(possibilities);
    }

    @Override
    public boolean isAffectedBy(Vec3i pos1, Vec3i pos2) {
        return pos1.getZ() != pos2.getZ() && isPosAffected(pos1) && isPosAffected(pos2);
    }

    @Override
    public boolean isPosAffected(Vec3i pos) {
        return (pos.getZ() == gridIndex1 && block1.contains(pos.getX(), pos.getY()))
                ||
                (pos.getZ() == gridIndex2 && block2.contains(pos.getX(), pos.getY()));
    }

    public Vec3i getCorrespondingPosition(Vec3i pos) {
        assert isPosAffected(pos);

        if (pos.getZ() == gridIndex1) {
            return new Vec3i(
                    pos.getX() - block1.x() + block2.x(),
                    pos.getY() - block1.y() + block2.y(),
                    gridIndex2
            );
        } else {
            return new Vec3i(
                    pos.getX() - block2.x() + block1.x(),
                    pos.getY() - block2.y() + block1.y(),
                    gridIndex1
            );
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        BlockEqualityConstraint that = (BlockEqualityConstraint) o;
        return (gridIndex1 == that.gridIndex1 && gridIndex2 == that.gridIndex2 && block1.equals(that.block1) && block2.equals(that.block2))
                || (gridIndex1 == that.gridIndex2 && gridIndex2 == that.gridIndex1 && block1.equals(that.block2) && block2.equals(that.block1));
    }

}
