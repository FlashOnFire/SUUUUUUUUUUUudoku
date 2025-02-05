package fr.polytech.suuuuuuuuuuudoku;

import fr.polytech.suuuuuuuuuuudoku.algorithm.Box2D;
import fr.polytech.suuuuuuuuuuudoku.algorithm.SolvingState;
import fr.polytech.suuuuuuuuuuudoku.algorithm.SudokuSolver;
import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec2i;
import fr.polytech.suuuuuuuuuuudoku.constraints.BlockConstraint;
import fr.polytech.suuuuuuuuuuudoku.grid.Grid;
import fr.polytech.suuuuuuuuuuudoku.symbols.SymbolSets;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ResolveTest {

    @Test
    public void testSolveDeduce() throws FileNotFoundException {
        var gridValue = CsvUtils.importGrid(Path.of(ClassLoader.getSystemResource("exemples/deduce.csv").getFile()));
        var resGrid =
                CsvUtils.importGrid(Path.of(ClassLoader.getSystemResource("exemples/deduceSolved.csv").getFile()));
        var symbol = SymbolSets.generateSymbols(gridValue.length);
        Grid grid = new Grid(gridValue, symbol);
        Grid res = new Grid(resGrid, symbol);

        assertFalse(grid.areConstraintsSatisfied(false));

        var start = System.nanoTime();
        var solve = SudokuSolver.solve(grid, true, false, false);
        var end = System.nanoTime();

        assertEquals(SolvingState.SOLVED, solve.getFirst());
        assertTrue(solve.getSecond().areConstraintsSatisfied(false));
        assertEquals(solve.getSecond().getInnerGrid(), res.getInnerGrid());

        System.out.println("Time: " + (end - start) / 1_000_000 + "ms");
    }

    @Test
    public void testSolveWithBackTrack() throws FileNotFoundException {
        var gridValue = CsvUtils.importGrid(Path.of(ClassLoader.getSystemResource("exemples/backtrack.csv").getFile()));
        var resGrid =
                CsvUtils.importGrid(Path.of(ClassLoader.getSystemResource("exemples/backtrackSolved.csv").getFile()));
        var symbol = SymbolSets.generateSymbols(gridValue.length);
        Grid grid = new Grid(gridValue, symbol);
        Grid res = new Grid(resGrid, symbol);

        assertFalse(grid.areConstraintsSatisfied(false));
        var solve = SudokuSolver.solve(grid, false, true, false);
        assertEquals(SolvingState.SOLVED, solve.getFirst());
        assertTrue(solve.getSecond().areConstraintsSatisfied(false));
        assertEquals(solve.getSecond().getInnerGrid(), res.getInnerGrid());
    }

    @Test
    public void testSolveWithBackTrackAndDeduce() throws FileNotFoundException {
        var gridValue =
                CsvUtils.importGrid(Path.of(ClassLoader.getSystemResource("exemples/backtrackAndDeduce.csv").getFile()));
        var resGrid = CsvUtils.importGrid(Path.of(ClassLoader.getSystemResource("exemples/backtrackAndDeduceSolved" +
                ".csv").getFile()));
        var symbol = SymbolSets.generateSymbols(gridValue.length);
        Grid grid = new Grid(gridValue, symbol);
        Grid res = new Grid(resGrid, symbol);


        assertFalse(grid.areConstraintsSatisfied(false));
        var solve = SudokuSolver.solve(grid, true, true, false);
        assertEquals(SolvingState.SOLVED, solve.getFirst());
        assertTrue(solve.getSecond().areConstraintsSatisfied(true));
        assertEquals(solve.getSecond().getInnerGrid(), res.getInnerGrid());
    }

    @Test
    public void testMultiSolve() throws FileNotFoundException, InterruptedException {
        var gridValue =
                CsvUtils.importGrid(Path.of(ClassLoader.getSystemResource("exemples/multisolutions.csv").getFile()));
        var symbol = SymbolSets.generateSymbols(gridValue.length);
        Grid grid = new Grid(gridValue, symbol);

        assertFalse(grid.areConstraintsSatisfied(false));

        var solvedList = SudokuSolver.findAllSolutions(grid, true, true, false);
        assertEquals(11, solvedList.size());
        for (var solved : solvedList) {
            assertTrue(solved.areConstraintsSatisfied(true));
        }
    }

    @Test
    public void testSolveFail() {
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
    public void testSolveWithoutStrategy() {
        var symbolSet = SymbolSets.generateSymbols(3);

        var grid = new Grid(new Integer[][]{
                {4, 2, 3},
                {3, 1, 2},
                {2, 1, 3}
        }, List.of(
                new BlockConstraint(symbolSet, new Box2D(0, 0, 2, 2))
        ), symbolSet);
        assertFalse(grid.areConstraintsSatisfied(false));
        assertThrows(AssertionError.class, () -> SudokuSolver.solve(grid, false, false, false));
        assertFalse(grid.areConstraintsSatisfied(false));
    }

    @Test
    public void testSolveBig() throws FileNotFoundException {
        var gridValue = CsvUtils.importGrid(Path.of(ClassLoader.getSystemResource("exemples/100x100.csv").getFile()));
        var resGrid =
                CsvUtils.importGrid(Path.of(ClassLoader.getSystemResource("exemples/100x100Solved.csv").getFile()));
        var symbol = SymbolSets.generateSymbols(gridValue.length);
        Grid grid = new Grid(gridValue, symbol);
        Grid res = new Grid(resGrid, symbol);

        assertFalse(grid.areConstraintsSatisfied(false));

        var start = System.nanoTime();
        var solve = SudokuSolver.solve(grid, true, true, false);
        var end = System.nanoTime();
        assertEquals(SolvingState.SOLVED, solve.getFirst());
        assertTrue(solve.getSecond().areConstraintsSatisfied(false));
        assertEquals(solve.getSecond().getInnerGrid(), res.getInnerGrid());
        System.out.println("Time: " + (end - start) / 1_000_000 + "ms");
    }

    @Test
    public void testMultiDokuSolve() throws FileNotFoundException {
        var grid = CsvUtils.importMultiGrid(Path.of(ClassLoader.getSystemResource("exemples/multigrid_1").getFile()));

        for (var g : grid.getGrids()) {
            assertTrue(g.isSolved());
        }
        assertTrue(grid.isSolved());

        grid.placeUnchecked(Vec2i.zero(), null, false, false);
        grid.placeUnchecked(new Vec2i(12, 0), null, false, false);
        grid.placeUnchecked(new Vec2i(6, 6), null, false, false);
        grid.placeUnchecked(new Vec2i(0, 12), null, false, false);
        grid.placeUnchecked(new Vec2i(12, 12), null, false, false);
        grid.computeAllEmptyCellsPossibilities();
        assertFalse(grid.isSolved());
        var solve = SudokuSolver.solve(grid, true, true, false);
        assertEquals(SolvingState.SOLVED, solve.getFirst());
    }


    @Test
    public void testMultiDokuSolve2() throws FileNotFoundException {
        var grid = CsvUtils.importMultiGrid(Path.of(ClassLoader.getSystemResource("exemples/multigrid_2").getFile()));

        for (var g : grid.getGrids()) {
            assertTrue(g.isSolved());
        }
        assertTrue(grid.isSolved());

        grid.placeUnchecked(new Vec2i(6, 0), null, false, false);
        grid.placeUnchecked(new Vec2i(0, 6), null, false, false);
        grid.placeUnchecked(new Vec2i(6, 6), null, false, false);
        grid.placeUnchecked(new Vec2i(12, 6), null, false, false);
        grid.placeUnchecked(new Vec2i(12, 6), null, false, false);
        grid.placeUnchecked(new Vec2i(6, 12), null, false, false);
        grid.computeAllEmptyCellsPossibilities();
        assertFalse(grid.isSolved());
        var solve = SudokuSolver.solve(grid, true, true, false);
        assertEquals(SolvingState.SOLVED, solve.getFirst());
    }
}
