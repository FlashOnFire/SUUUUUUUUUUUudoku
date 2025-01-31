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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ResolveTest {
    static String RESSOURCES_PATH = "src/test/java/fr/polytech/suuuuuuuuuuudoku/resources/";

    @Test
    public void testSolveDeduce() throws FileNotFoundException {
        var grid = CsvUtils.importGrid(Path.of(RESSOURCES_PATH + "deduce.csv"));
        var res = CsvUtils.importGrid(Path.of(RESSOURCES_PATH + "deduceSolved.csv"));

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
        var grid = CsvUtils.importGrid(Path.of(RESSOURCES_PATH + "backtrack.csv"));
        var res = CsvUtils.importGrid(Path.of(RESSOURCES_PATH + "backtrackSolved.csv"));

        assertFalse(grid.areConstraintsSatisfied(false));
        var solve = SudokuSolver.solve(grid, false, true, false);
        assertEquals(SolvingState.SOLVED, solve.getFirst());
        assertTrue(solve.getSecond().areConstraintsSatisfied(false));
        assertEquals(solve.getSecond().getInnerGrid(), res.getInnerGrid());
    }

    @Test
    public void testSolveWithBackTrackAndDeduce() throws FileNotFoundException {
        var grid = CsvUtils.importGrid(Path.of(RESSOURCES_PATH + "backtrackAndDeduce.csv"));
        var res = CsvUtils.importGrid(Path.of(RESSOURCES_PATH + "backtrackAndDeduceSolved.csv"));

        assertFalse(grid.areConstraintsSatisfied(false));
        var solve = SudokuSolver.solve(grid, true, true, false);
        assertEquals(SolvingState.SOLVED, solve.getFirst());
        assertTrue(solve.getSecond().areConstraintsSatisfied(true));
        assertEquals(solve.getSecond().getInnerGrid(), res.getInnerGrid());
    }

    @Test
    public void testMultiSolve() throws FileNotFoundException {
        var grid = CsvUtils.importGrid(Path.of(RESSOURCES_PATH + "multisolutions.csv"));

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
        var grid = CsvUtils.importGrid(Path.of(RESSOURCES_PATH + "100x100.csv"));
        var res = CsvUtils.importGrid(Path.of(RESSOURCES_PATH + "100x100Solved.csv"));

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
        List<Pair<Vec2i, Grid>> grids = new ArrayList<>();
        Vec2i[] positions = {
                Vec2i.zero(),
                new Vec2i(12, 0),
                new Vec2i(6, 6),
                new Vec2i(0, 12),
                new Vec2i(12, 12),
        };
        for (int i = 0; i < 5; i++) {
            var grid = CsvUtils.importGrid(Path.of(RESSOURCES_PATH + "/multigrid_1/" + i + ".csv"));
            grids.add(new Pair<>(positions[i], grid));
        }
        var grid = new MultiGrid(grids);

        for (var g : grid.getGrids()) {
            assertTrue(g.isSolved());
        }
        assertTrue(grid.isSolved());

        grid.placeUnchecked(new Vec3i(0, 0, 0), null, false, false);
        grid.placeUnchecked(new Vec3i(0, 0, 1), null, false, false);
        grid.placeUnchecked(new Vec3i(0, 0, 2), null, false, false);
        grid.placeUnchecked(new Vec3i(6, 6, 0), null, false, false);
        grid.placeUnchecked(new Vec3i(6, 6, 0), null, false, false);
        grid.placeUnchecked(new Vec3i(0, 0, 3), null, false, false);
        grid.placeUnchecked(new Vec3i(0, 0, 4), null, false, false);
        grid.computeAllEmptyCellsPossibilities();
        assertFalse(grid.isSolved());
        var solve = SudokuSolver.solve(grid, true, true, false);
        assertEquals(SolvingState.SOLVED, solve.getFirst());
    }


    @Test
    public void testMultiDokuSolve2() throws FileNotFoundException {
        List<Pair<Vec2i, Grid>> grids = new ArrayList<>();
        Vec2i[] positions = {
                new Vec2i(6, 0),
                new Vec2i(0, 6),
                new Vec2i(6, 6),
                new Vec2i(12, 6),
                new Vec2i(6, 12),
        };
        for (int i = 0; i < 5; i++) {
            var grid = CsvUtils.importGrid(Path.of(RESSOURCES_PATH + "/multigrid_2/" + i + ".csv"));
            grids.add(new Pair<>(positions[i], grid));
        }
        var grid = new MultiGrid(grids);

        for (var g : grid.getGrids()) {
            assertTrue(g.isSolved());
        }
        assertTrue(grid.isSolved());

        grid.placeUnchecked(new Vec3i(0, 0, 0), null, false, false);
        grid.placeUnchecked(new Vec3i(0, 0, 1), null, false, false);
        grid.placeUnchecked(new Vec3i(0, 0, 2), null, false, false);
        grid.placeUnchecked(new Vec3i(6, 6, 0), null, false, false);
        grid.placeUnchecked(new Vec3i(6, 6, 0), null, false, false);
        grid.placeUnchecked(new Vec3i(0, 0, 3), null, false, false);
        grid.placeUnchecked(new Vec3i(0, 0, 4), null, false, false);
        grid.computeAllEmptyCellsPossibilities();
        assertFalse(grid.isSolved());
        var solve = SudokuSolver.solve(grid, true, true, false);
        assertEquals(SolvingState.SOLVED, solve.getFirst());
    }
}
