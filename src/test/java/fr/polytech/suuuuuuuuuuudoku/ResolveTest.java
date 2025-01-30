package fr.polytech.suuuuuuuuuuudoku;

import fr.polytech.suuuuuuuuuuudoku.algorithm.*;
import fr.polytech.suuuuuuuuuuudoku.constraints.BlockConstraint;
import fr.polytech.suuuuuuuuuuudoku.grid.Grid;
import fr.polytech.suuuuuuuuuuudoku.grid.MultiGrid;
import fr.polytech.suuuuuuuuuuudoku.symbols.SymbolSets;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ResolveTest {
    static String resoucesPath = "src/test/java/fr/polytech/suuuuuuuuuuudoku/resources/";

    @Test
    public void testSolveDeduce() throws FileNotFoundException {
        var grid = CsvUtils.importGrid(Path.of(resoucesPath + "deduce.csv"));
        var res = CsvUtils.importGrid(Path.of(resoucesPath + "deduceSolved.csv"));

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
        var grid = CsvUtils.importGrid(Path.of(resoucesPath + "backtrack.csv"));
        var res = CsvUtils.importGrid(Path.of(resoucesPath + "backtrackSolved.csv"));

        assertFalse(grid.areConstraintsSatisfied(false));
        var solve = SudokuSolver.solve(grid, false, true, false);
        assertEquals(SolvingState.SOLVED, solve.getFirst());
        assertTrue(solve.getSecond().areConstraintsSatisfied(false));
        assertEquals(solve.getSecond().getInnerGrid(), res.getInnerGrid());
    }

    @Test
    public void testSolveWithBackTrackAndDeduce() throws FileNotFoundException {
        var grid = CsvUtils.importGrid(Path.of(resoucesPath + "backtrackAndDeduce.csv"));
        var res = CsvUtils.importGrid(Path.of(resoucesPath + "backtrackAndDeduceSolved.csv"));

        assertFalse(grid.areConstraintsSatisfied(false));
        var solve = SudokuSolver.solve(grid, true, true, false);
        assertEquals(SolvingState.SOLVED, solve.getFirst());
        assertTrue(solve.getSecond().areConstraintsSatisfied(true));
        assertEquals(solve.getSecond().getInnerGrid(), res.getInnerGrid());
    }

    @Test
    public void testMultiSolve() throws FileNotFoundException {
        var grid = CsvUtils.importGrid(Path.of(resoucesPath + "multisolutions.csv"));

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
        var grid = CsvUtils.importGrid(Path.of(resoucesPath + "100x100.csv"));
        var res = CsvUtils.importGrid(Path.of(resoucesPath + "100x100Solved.csv"));

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
    public void testMultiDokuSolve() {
        List<Pair<Vec2i, Grid>> grids = new ArrayList<>();
        var symbolSet = SymbolSets.generateSymbols(9);
        grids.add(new Pair<>(
                Vec2i.zero(),
                new Grid(
                        new Integer[][]
                                {
                                        {7, 1, 2, 6, 3, 8, 4, 9, 5},
                                        {9, 5, 3, 4, 1, 2, 8, 7, 6},
                                        {4, 8, 6, 9, 5, 7, 2, 1, 3},
                                        {2, 3, 4, 1, 7, 6, 5, 8, 9},
                                        {5, 9, 1, 3, 8, 4, 7, 6, 2},
                                        {8, 6, 7, 2, 9, 5, 3, 4, 1},
                                        {1, 7, 8, 5, 2, 9, 6, 3, 4},
                                        {3, 4, 5, 8, 6, 1, 9, 2, 7},
                                        {6, 2, 9, 7, 4, 3, 1, 5, 8},
                                }
                        ,
                        symbolSet
                )));

        grids.add(new Pair<>(
                new Vec2i(0, 12), new Grid(
                new Integer[][]
                        {
                                {5, 9, 1, 2, 4, 8, 3, 7, 6},
                                {6, 2, 3, 9, 1, 7, 5, 8, 4},
                                {8, 4, 7, 5, 6, 3, 2, 1, 9},
                                {2, 1, 9, 7, 5, 4, 8, 6, 3},
                                {4, 8, 6, 1, 3, 9, 7, 2, 5},
                                {3, 7, 5, 8, 2, 6, 4, 9, 1},
                                {9, 5, 8, 4, 7, 1, 6, 3, 2},
                                {1, 6, 4, 3, 8, 2, 9, 5, 7},
                                {7, 3, 2, 6, 9, 5, 1, 4, 8}
                        }
                ,
                symbolSet
        )));

        grids.add(new Pair<>(
                new Vec2i(6, 6),
                new Grid(
                        new Integer[][]
                                {
                                        {6, 3, 4, 1, 7, 2, 9, 5, 8},
                                        {9, 2, 7, 8, 5, 3, 1, 6, 4},
                                        {1, 5, 8, 9, 4, 6, 7, 3, 2},
                                        {2, 6, 5, 7, 3, 8, 4, 9, 1},
                                        {3, 7, 9, 2, 1, 4, 6, 8, 5},
                                        {8, 4, 1, 6, 9, 5, 2, 7, 3},
                                        {7, 9, 3, 5, 2, 1, 8, 4, 6},
                                        {5, 1, 6, 4, 8, 7, 3, 2, 9},
                                        {4, 8, 2, 3, 6, 9, 5, 1, 7}
                                }
                        ,
                        symbolSet
                )));

        grids.add(new Pair<>(
                new Vec2i(12, 0), new Grid(
                new Integer[][]
                        {
                                {6, 2, 5, 8, 1, 4, 7, 9, 3},
                                {9, 8, 4, 2, 7, 3, 5, 1, 6},
                                {7, 3, 1, 6, 5, 9, 4, 8, 2},
                                {5, 6, 2, 9, 8, 7, 1, 3, 4},
                                {3, 1, 7, 5, 4, 2, 9, 6, 8},
                                {8, 4, 9, 3, 6, 1, 2, 5, 7},
                                {2, 7, 6, 1, 3, 5, 8, 4, 9},
                                {1, 9, 3, 4, 2, 8, 6, 7, 5},
                                {4, 5, 8, 7, 9, 6, 3, 2, 1}
                        }
                ,
                symbolSet)
        ));
        grids.add(new Pair<>(
                new Vec2i(12, 12), new Grid(
                new Integer[][]
                        {
                                {8, 4, 6, 5, 3, 7, 1, 9, 2},
                                {3, 2, 9, 6, 1, 8, 5, 7, 4},
                                {5, 1, 7, 4, 9, 2, 6, 3, 8},
                                {7, 5, 2, 1, 4, 9, 3, 8, 6},
                                {4, 3, 8, 7, 6, 5, 2, 1, 9},
                                {9, 6, 1, 2, 8, 3, 7, 4, 5},
                                {1, 9, 3, 8, 2, 6, 4, 5, 7},
                                {2, 7, 4, 9, 5, 1, 8, 6, 3},
                                {6, 8, 5, 3, 7, 4, 9, 2, 1}
                        }
                ,
                symbolSet
        )));

        Collections.shuffle(grids);

        var grid = new MultiGrid(grids);

        for (var g : grid.getGrids()) {
            assertTrue(g.isSolved());
        }
        assertTrue(grid.isSolved());

//        grid.placeUnchecked(new Vec3i(0, 0, 0), null, false);
//        grid.placeUnchecked(new Vec3i(0, 0, 1), null, false);
        grid.placeUnchecked(new Vec3i(0, 0, 2), null, false, false);
        grid.placeUnchecked(new Vec3i(6, 6, 0), null, false, false);
        grid.getGrids()[2].display();
        System.out.println();
        grid.getGrids()[0].display();
//        grid.placeUnchecked(new Vec3i(6, 6, 0), null, false);
//        grid.placeUnchecked(new Vec3i(0, 0, 3), null, false);
//        grid.placeUnchecked(new Vec3i(0, 0, 4), null, false);
        grid.computeAllEmptyCellsPossibilities();
        assertFalse(grid.isSolved());
        var solve = SudokuSolver.solve(grid, true, true, false);
        assertEquals(SolvingState.SOLVED, solve.getFirst());
    }
}
