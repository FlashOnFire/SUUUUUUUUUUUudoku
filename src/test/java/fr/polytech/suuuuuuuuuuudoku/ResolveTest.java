package fr.polytech.suuuuuuuuuuudoku;

import fr.polytech.suuuuuuuuuuudoku.constraints.BlockConstraint;
import fr.polytech.suuuuuuuuuuudoku.grid.Grid;
import fr.polytech.suuuuuuuuuuudoku.algorithm.SolvingState;
import fr.polytech.suuuuuuuuuuudoku.algorithm.SudokuSolver;
import fr.polytech.suuuuuuuuuuudoku.symbols.SymbolSets;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ResolveTest {
    static String resoucesPath = "src/test/java/fr/polytech/suuuuuuuuuuudoku/resources/";

    @Test
    public void testSolveDeduce() throws FileNotFoundException {
        var grid = CsvUtils.importGrid(Path.of(resoucesPath + "deduce.csv"));
        var res = CsvUtils.importGrid(Path.of(resoucesPath + "deduceSolved.csv"));

        assertFalse(grid.areConstraintsSatisfied());
        var isSolved = SudokuSolver.solve(grid, true, false);
        assertEquals(SolvingState.SOLVED, isSolved);
        assertTrue(grid.areConstraintsSatisfied());
        assertEquals(grid.getGrid(), res.getGrid());
    }

    @Test
    public void testSolveWithBackTrack() throws FileNotFoundException {
        var grid = CsvUtils.importGrid(Path.of(resoucesPath + "backtrack.csv"));
        var res = CsvUtils.importGrid(Path.of(resoucesPath + "backtrackSolved.csv"));

        assertFalse(grid.areConstraintsSatisfied());
        var isSolved = SudokuSolver.solve(grid, false, true);
        assertEquals(SolvingState.SOLVED, isSolved);
        assertTrue(grid.areConstraintsSatisfied());
        assertEquals(grid.getGrid(), res.getGrid());
    }

    @Test
    public void testSolveWithBackTrackAndDeduce() throws FileNotFoundException {
        var grid = CsvUtils.importGrid(Path.of(resoucesPath + "backtrackAndDeduce.csv"));
        var res = CsvUtils.importGrid(Path.of(resoucesPath + "backtrackAndDeduceSolved.csv"));

        assertFalse(grid.areConstraintsSatisfied());
        var isSolved = SudokuSolver.solve(grid, true, true);
        assertEquals(SolvingState.SOLVED, isSolved);
        assertTrue(grid.areConstraintsSatisfied());
        assertEquals(grid.getGrid(), res.getGrid());
    }

    @Test
    public void testSolveFail() {
        var grid = new Grid(new String[][]{
                {"4", "2", "3"},
                {"3", "1", "2"},
                {"2", "1", "3"}
        }, List.of(
                new BlockConstraint(Set.of("1", "2", "3"), 0, 0, 2, 2)
        ), SymbolSets.DIGITS);
        assertFalse(grid.areConstraintsSatisfied());
    }

    @Test
    public void testSolveWithoutStrategy() {
        var grid = new Grid(new String[][]{
                {"4", "2", "3"},
                {"3", "1", "2"},
                {"2", "1", "3"}
        }, List.of(
                new BlockConstraint(Set.of("1", "2", "3"), 0, 0, 2, 2)
        ), SymbolSets.DIGITS);
        assertFalse(grid.areConstraintsSatisfied());
        assertThrows(AssertionError.class, () -> SudokuSolver.solve(grid, false, false));
        assertFalse(grid.areConstraintsSatisfied());
    }

    @Test
    public void testSolveBig() throws FileNotFoundException {
        var grid = CsvUtils.importGrid(Path.of(resoucesPath + "100x100.csv"));
        var res = CsvUtils.importGrid(Path.of(resoucesPath + "100x100Solved.csv"));

        assertFalse(grid.areConstraintsSatisfied());
        var isSolved = SudokuSolver.solve(grid, true, true);
        assertEquals(SolvingState.SOLVED, isSolved);
        assertTrue(grid.areConstraintsSatisfied());
        assertEquals(grid.getGrid(), res.getGrid());
    }
}
