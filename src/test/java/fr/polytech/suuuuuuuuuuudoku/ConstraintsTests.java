package fr.polytech.suuuuuuuuuuudoku;

import fr.polytech.suuuuuuuuuuudoku.algorithm.Box2D;
import fr.polytech.suuuuuuuuuuudoku.constraints.BlockConstraint;
import fr.polytech.suuuuuuuuuuudoku.constraints.ColumnConstraint;
import fr.polytech.suuuuuuuuuuudoku.constraints.LineConstraint;
import fr.polytech.suuuuuuuuuuudoku.grid.Grid;
import fr.polytech.suuuuuuuuuuudoku.symbols.SymbolSets;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConstraintsTests {

    @Test
    public void testBlockConstraint() {
        var symbolSet = SymbolSets.generateSymbols(4);

        var grid = new Grid(new Integer[][]{
                {4, 2, 3},
                {3, 1, 2},
                {2, 1, 3}
        }, List.of(
                new BlockConstraint(symbolSet, new Box2D(0, 0, 2, 2))
        ), symbolSet);
        assertTrue(grid.areConstraintsSatisfied(false));
    }

    @Test
    public void testBlockConstraintEmpty() {
        var symbolSet = SymbolSets.generateSymbols(4);

        var grid = new Grid(new Integer[][]{
                {null, null, null},
                {null, null, null},
                {null, null, null}
        }, List.of(
                new BlockConstraint(symbolSet, new Box2D(0, 0, 2, 2))
        ), symbolSet);
        assertTrue(grid.areConstraintsSatisfied(false));
    }

    @Test
    public void testBlockConstraintFail() {
        var symbolSet = SymbolSets.generateSymbols(3);

        var grid = new Grid(new Integer[][]{
                {4, 2, 3},
                {3, 1, 2},
                {2, 1, 3}
        }, List.of(
                new BlockConstraint(symbolSet, new Box2D(0, 0, 2, 2))
        ), symbolSet);
        assertFalse(grid.areConstraintsSatisfied(false));
    }

    @Test
    public void testColumnConstraint() {
        var symbolSet = SymbolSets.generateSymbols(3);

        var grid = new Grid(new Integer[][]{
                {1, 2, 3},
                {3, 3, 2},
                {2, 1, 1}
        }, List.of(
                new ColumnConstraint(symbolSet)
        ), symbolSet);
        assertTrue(grid.areConstraintsSatisfied(false));
    }

    @Test
    public void testColumnConstraintEmpty() {
        var symbolSet = SymbolSets.generateSymbols(3);

        var grid = new Grid(new Integer[][]{
                {null, null, null},
                {null, null, null},
                {null, null, null},
        }, List.of(
                new ColumnConstraint(symbolSet)
        ), symbolSet);
        assertTrue(grid.areConstraintsSatisfied(false));
    }

    @Test
    public void testColumnConstraintFail() {
        var symbolSet = SymbolSets.generateSymbols(3);

        var grid = new Grid(new Integer[][]{
                {4, 2, 3},
                {3, 1, 2},
                {2, 1, 3}
        }, List.of(
                new ColumnConstraint(symbolSet)
        ), symbolSet);
        assertFalse(grid.areConstraintsSatisfied(false));
    }

    @Test
    public void testLineConstraint() {
        var symbolSet = SymbolSets.generateSymbols(3);

        var grid = new Grid(new Integer[][]{
                {1, 2, 3},
                {3, 1, 2},
                {2, 1, 3}
        }, List.of(
                new LineConstraint(symbolSet)
        ), symbolSet);
        assertTrue(grid.areConstraintsSatisfied(false));
    }

    @Test
    public void testLineConstraintEmpty() {
        var symbolSet = SymbolSets.generateSymbols(3);

        var grid = new Grid(new Integer[][]{
                {null, null, null},
                {null, null, null},
                {null, null, null},
        }, List.of(
                new LineConstraint(symbolSet)
        ), symbolSet);
        assertTrue(grid.areConstraintsSatisfied(false));
    }

    @Test
    public void testLineConstraintFail() {
        var symbolSet = SymbolSets.generateSymbols(3);

        var grid = new Grid(new Integer[][]{
                {4, 2, 3},
                {3, 1, 2},
                {2, 1, 3}
        }, List.of(
                new LineConstraint(symbolSet)
        ), symbolSet);
        assertFalse(grid.areConstraintsSatisfied(false));
    }
}
