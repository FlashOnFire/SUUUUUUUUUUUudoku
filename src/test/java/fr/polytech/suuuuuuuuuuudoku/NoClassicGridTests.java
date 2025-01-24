package fr.polytech.suuuuuuuuuuudoku;

import fr.polytech.suuuuuuuuuuudoku.algorithm.Box2D;
import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec2i;
import fr.polytech.suuuuuuuuuuudoku.constraints.*;
import fr.polytech.suuuuuuuuuuudoku.graphics.SudokuFrame;
import fr.polytech.suuuuuuuuuuudoku.grid.Grid;
import fr.polytech.suuuuuuuuuuudoku.symbols.SymbolSets;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class NoClassicGridTests {
    @Test
    public void testNoSquareGridConstraint() {
        var symbolSet = SymbolSets.generateSymbols(6);
        var grid = new Grid(new Integer[][]{
                {1, 2, 6, 4, 5, 3},
                {3, 4, 5, 1, 6, 2},
                {6, 5, 3, 2, 4, 1},
                {4, 6, 1, 3, 2, 5},
                {5, 1, 2, 6, 3, 4},
                {2, 3, 4, 5, 1, 6}
        }, List.of(
                new BlockConstraint(symbolSet, new Box2D(0, 0, 2, 3)),
                new BlockConstraint(symbolSet, new Box2D(2, 0, 2, 3)),
                new BlockConstraint(symbolSet, new Box2D(4, 0, 2, 3)),
                new BlockConstraint(symbolSet, new Box2D(0, 3, 2, 3)),
                new BlockConstraint(symbolSet, new Box2D(2, 3, 2, 3)),
                new BlockConstraint(symbolSet, new Box2D(4, 3, 2, 3)),
                new LineConstraint(symbolSet),
                new ColumnConstraint(symbolSet),
                new NotEmptyConstraint()
        ), symbolSet);
        assertTrue(grid.areConstraintsSatisfied(false));
    }

    @Test
    public void testGridWithConstraintNoRect() {
        var symbolSet = SymbolSets.generateSymbols(9);

        var grid = new Grid(new Integer[][]{
                {8, 5, 6, 2, 9, 4, 1, 7, 3},
                {2, 7, 8, 5, 6, 3, 9, 4, 1},
                {9, 1, 3, 4, 2, 5, 8, 6, 7},
                {4, 2, 7, 1, 3, 8, 6, 9, 5},
                {5, 3, 4, 6, 1, 7, 2, 8, 9},
                {7, 8, 9, 3, 5, 1, 4, 2, 6},
                {6, 9, 1, 7, 4, 2, 3, 5, 8},
                {1, 4, 5, 9, 8, 6, 7, 3, 2},
                {3, 6, 2, 8, 7, 9, 5, 1, 4}
        }, List.of(
                new GeneralSymbolConstraint(symbolSet, new Vec2i[]{
                        new Vec2i(0, 0),
                        new Vec2i(0, 1),
                        new Vec2i(0, 2),
                        new Vec2i(1, 0),
                        new Vec2i(1, 1),
                        new Vec2i(2, 0),
                        new Vec2i(2, 2),
                        new Vec2i(3, 3),
                        new Vec2i(2, 4),
                }),
                new GeneralSymbolConstraint(symbolSet, new Vec2i[]{
                        new Vec2i(0, 0),
                        new Vec2i(0, 1),
                        new Vec2i(0, 2),
                        new Vec2i(1, 0),
                        new Vec2i(1, 1),
                        new Vec2i(2, 0),
                        new Vec2i(2, 2),
                        new Vec2i(3, 3),
                        new Vec2i(2, 4),
                }),
                new GeneralSymbolConstraint(symbolSet, new Vec2i[]{
                        new Vec2i(3, 0),
                        new Vec2i(4, 0),
                        new Vec2i(2, 1),
                        new Vec2i(3, 1),
                        new Vec2i(2, 3),
                        new Vec2i(3, 2),
                        new Vec2i(3, 4),
                        new Vec2i(4, 3),
                        new Vec2i(4, 4),
                }),
                new GeneralSymbolConstraint(symbolSet, new Vec2i[]{
                        new Vec2i(5, 0),
                        new Vec2i(6, 0),
                        new Vec2i(7, 0),
                        new Vec2i(4, 1),
                        new Vec2i(5, 1),
                        new Vec2i(6, 1),
                        new Vec2i(4, 2),
                        new Vec2i(5, 2),
                        new Vec2i(6, 2),
                }),
                new GeneralSymbolConstraint(symbolSet, new Vec2i[]{
                        new Vec2i(8, 0),
                        new Vec2i(7, 1),
                        new Vec2i(8, 1),
                        new Vec2i(7, 2),
                        new Vec2i(8, 2),
                        new Vec2i(5, 3),
                        new Vec2i(7, 3),
                        new Vec2i(8, 3),
                        new Vec2i(6, 4),
                }),
                new GeneralSymbolConstraint(symbolSet, new Vec2i[]{
                        new Vec2i(1, 2),
                        new Vec2i(0, 3),
                        new Vec2i(1, 3),
                        new Vec2i(0, 4),
                        new Vec2i(1, 4),
                        new Vec2i(0, 5),
                        new Vec2i(1, 5),
                        new Vec2i(0, 6),
                        new Vec2i(1, 6),
                }),
                new GeneralSymbolConstraint(symbolSet, new Vec2i[]{
                        new Vec2i(6, 3),
                        new Vec2i(5, 4),
                        new Vec2i(7, 4),
                        new Vec2i(2, 5),
                        new Vec2i(3, 5),
                        new Vec2i(4, 5),
                        new Vec2i(6, 5),
                        new Vec2i(2, 6),
                        new Vec2i(5, 6),
                }),
                new GeneralSymbolConstraint(symbolSet, new Vec2i[]{
                        new Vec2i(0, 8),
                        new Vec2i(1, 8),
                        new Vec2i(2, 8),
                        new Vec2i(3, 8),
                        new Vec2i(0, 7),
                        new Vec2i(1, 7),
                        new Vec2i(2, 7),
                        new Vec2i(3, 7),
                        new Vec2i(3, 6),
                }),
                new GeneralSymbolConstraint(symbolSet, new Vec2i[]{
                        new Vec2i(5, 5),
                        new Vec2i(7, 5),
                        new Vec2i(4, 6),
                        new Vec2i(6, 6),
                        new Vec2i(7, 6),
                        new Vec2i(4, 7),
                        new Vec2i(5, 7),
                        new Vec2i(4, 8),
                        new Vec2i(5, 8),
                }),
                new GeneralSymbolConstraint(symbolSet, new Vec2i[]{
                        new Vec2i(8, 4),
                        new Vec2i(8, 5),
                        new Vec2i(8, 6),
                        new Vec2i(6, 7),
                        new Vec2i(7, 7),
                        new Vec2i(8, 7),
                        new Vec2i(6, 8),
                        new Vec2i(7, 8),
                        new Vec2i(8, 8),
                }),
                new LineConstraint(symbolSet),
                new ColumnConstraint(symbolSet),
                new NotEmptyConstraint()
        ), symbolSet);
        assertTrue(grid.areConstraintsSatisfied(false));
    }
}
