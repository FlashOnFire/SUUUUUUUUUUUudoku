package fr.polytech.suuuuuuuuuuudoku.constraints;

import fr.polytech.suuuuuuuuuuudoku.algorithm.Box2D;
import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec2i;
import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec3i;
import fr.polytech.suuuuuuuuuuudoku.grid.Grid;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class BlockEqualityConstraint implements AbstractConstraint<Grid[], Vec3i> {
    /**
     * The set of symbols to be checked within the block.
     */
    private final int gridIndex1, gridIndex2;
    private final Box2D block1, block2;

    public BlockEqualityConstraint(int gridIndex1, Box2D block1, int gridIndex2, Box2D block2) {
        this.gridIndex1 = gridIndex1;
        this.block1 = block1;
        this.gridIndex2 = gridIndex2;
        this.block2 = block2;
    }

    public int getGridIndex1() {
        return gridIndex1;
    }

    public int getGridIndex2() {
        return gridIndex2;
    }

    public Box2D getBlock1() {
        return block1;
    }

    public Box2D getBlock2() {
        return block2;
    }

    @Override
    public boolean isSatisfied(Grid[] grid) {
        for (int i = 0; i < block1.width(); i++) {
            for (int j = 0; j < block1.height(); j++) {
                if (!grid[gridIndex1].getSymbolAt(
                        block1.y() + i,
                        block1.x() + j
                ).equals(grid[gridIndex2].getSymbolAt(block2.y() + i, block2.x() + j))) {
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

    public Vec2i getPadding() {
        return new Vec2i(block1.x() - block2.x(), block1.y() - block2.y());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        BlockEqualityConstraint that = (BlockEqualityConstraint) o;
        return (gridIndex1 == that.gridIndex1 && gridIndex2 == that.gridIndex2 && block1.equals(that.block1) && block2.equals(that.block2))
                || (gridIndex1 == that.gridIndex2 && gridIndex2 == that.gridIndex1 && block1.equals(that.block2) && block2.equals(that.block1));
    }

    @Override
    public int hashCode() {
        int result = gridIndex1;
        result = 31 * result + gridIndex2;
        result = 31 * result + block1.hashCode();
        result = 31 * result + block2.hashCode();
        return result;
    }
}
