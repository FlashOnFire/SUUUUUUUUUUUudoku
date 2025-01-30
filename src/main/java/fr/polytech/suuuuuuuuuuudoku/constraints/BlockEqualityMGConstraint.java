package fr.polytech.suuuuuuuuuuudoku.constraints;

import fr.polytech.suuuuuuuuuuudoku.algorithm.Box2D;
import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec2i;
import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec3i;
import fr.polytech.suuuuuuuuuuudoku.grid.Grid;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class BlockEqualityMGConstraint implements MultiGridConstraint {
    /**
     * The set of symbols to be checked within the block.
     */
    private final int gridIndex1, gridIndex2;
    private final Box2D block1, block2;

    public BlockEqualityMGConstraint(int gridIndex1, Box2D block1, int gridIndex2, Box2D block2) {
        this.gridIndex1 = gridIndex1;
        this.block1 = block1;
        this.gridIndex2 = gridIndex2;
        this.block2 = block2;
    }

    @Override
    public boolean isSatisfied(Grid[] grid) {
        for (int i = 0; i < block1.width(); i++) {
            for (int j = 0; j < block1.height(); j++) {
                if (!grid[gridIndex1].getSymbolAt(
                        block1.line() + i,
                        block1.column() + j
                ).equals(grid[gridIndex2].getSymbolAt(block2.line() + i, block2.column() + j))) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public Optional<Set<Integer>> getPossibilities(Grid[] grid, Vec3i pos) {
        if (pos.getDepth() != gridIndex1 && pos.getDepth() != gridIndex2) {
            return Optional.empty();
        }

        if (!isPosAffected(pos)) {
            return Optional.empty();
        }

        var possibilities = new HashSet<>(
                grid[pos.getDepth()].getEmptyCellsPossibilities().get(new Vec2i(pos.getLine(), pos.getColumn()))
        );
        var correspondingPos = getCorrespondingPosition(pos);
        possibilities.retainAll(grid[correspondingPos.getDepth()].getEmptyCellsPossibilities().get(new Vec2i(correspondingPos.getLine(), correspondingPos.getColumn())));

        return Optional.of(possibilities);
    }

    @Override
    public boolean isAffectedBy(Vec3i pos1, Vec3i pos2) {
        return pos1.getDepth() != pos2.getDepth() && isPosAffected(pos1) && isPosAffected(pos2);
    }

    @Override
    public boolean isPosAffected(Vec3i pos) {
        return (pos.getDepth() == gridIndex1 && block1.contains(pos.getLine(), pos.getColumn()))
                ||
                (pos.getDepth() == gridIndex2 && block2.contains(pos.getLine(), pos.getColumn()));
    }

    public Vec3i getCorrespondingPosition(Vec3i pos) {
        assert isPosAffected(pos);

        if (pos.getDepth() == gridIndex1) {
            return new Vec3i(
                    pos.getLine() - block1.line() + block2.line(),
                    pos.getColumn() - block1.column() + block2.column(),
                    gridIndex2
            );
        } else {
            return new Vec3i(
                    pos.getLine() - block2.line() + block1.line(),
                    pos.getColumn() - block2.column() + block1.column(),
                    gridIndex1
            );
        }
    }

    public Vec2i getPadding() {
        return new Vec2i(block1.line() - block2.line(), block1.column() - block2.column());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        BlockEqualityMGConstraint that = (BlockEqualityMGConstraint) o;
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
