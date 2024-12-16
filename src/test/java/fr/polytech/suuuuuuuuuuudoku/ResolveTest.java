package fr.polytech.suuuuuuuuuuudoku;

import fr.polytech.suuuuuuuuuuudoku.constraints.AbstractConstraint;
import fr.polytech.suuuuuuuuuuudoku.constraints.BlockConstraint;
import fr.polytech.suuuuuuuuuuudoku.constraints.ColumnConstraint;
import fr.polytech.suuuuuuuuuuudoku.constraints.LineConstraint;
import fr.polytech.suuuuuuuuuuudoku.solver.SolvingState;
import fr.polytech.suuuuuuuuuuudoku.solver.SudokuSolver;
import fr.polytech.suuuuuuuuuuudoku.symbols.SymbolSets;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ResolveTest {
    @Test
    public void testSolveWithoutBackTrack() {

        List<AbstractConstraint> constraintsTests = new ArrayList<>();
        for (int i = 0; i < 9; i += 3) {
            for (int j = 0; j < 9; j += 3) {
                constraintsTests.add(new BlockConstraint(SymbolSets.DIGITS, i, j, i + 3, j + 3));
            }
        }
        constraintsTests.add(new LineConstraint(SymbolSets.DIGITS));
        constraintsTests.add(new ColumnConstraint(SymbolSets.DIGITS));


        var grid = new Grid(new Character[][]{
                {' ', '9', ' ', ' ', ' ', '2', ' ', '1', ' '},
                {'2', ' ', '8', ' ', '4', ' ', '9', '3', ' '},
                {'7', ' ', '3', '1', ' ', '6', '8', ' ', ' '},
                {' ', ' ', ' ', '3', ' ', ' ', '1', '4', '5'},
                {'1', '8', '5', ' ', '2', '9', '6', ' ', ' '},
                {' ', '7', '4', ' ', ' ', '1', '2', ' ', '8'},
                {' ', ' ', ' ', '2', ' ', ' ', ' ', '8', ' '},
                {'5', ' ', ' ', '9', ' ', ' ', '7', '6', '2'},
                {'8', ' ', ' ', '6', ' ', '3', ' ', ' ', ' '}
        }, constraintsTests);

        var res = new Character[][]{
                {'4', '9', '6', '8', '3', '2', '5', '1', '7'},
                {'2', '1', '8', '7', '4', '5', '9', '3', '6'},
                {'7', '5', '3', '1', '9', '6', '8', '2', '4'},
                {'9', '6', '2', '3', '7', '8', '1', '4', '5'},
                {'1', '8', '5', '4', '2', '9', '6', '7', '3'},
                {'3', '7', '4', '5', '6', '1', '2', '9', '8'},
                {'6', '4', '9', '2', '5', '7', '3', '8', '1'},
                {'5', '3', '1', '9', '8', '4', '7', '6', '2'},
                {'8', '2', '7', '6', '1', '3', '4', '5', '9'}
        };

        assertTrue(grid.areConstraintsSatisfied());

        var isSolved = SudokuSolver.solve(grid, SymbolSets.DIGITS, false);
        assertEquals(SolvingState.SOLVED, isSolved);
        assertTrue(grid.areConstraintsSatisfied());

        var solvedGrid = grid.getGrid();
        for (int i = 0; i < solvedGrid.length; i++) {
            for (int j = 0; j < solvedGrid[i].length; j++) {
                assertEquals(res[i][j], solvedGrid[i][j]);
            }
        }
    }

    @Test
    public void testSolveWithBackTrack() {

        List<AbstractConstraint> constraintsTests = new ArrayList<>();
        for (int i = 0; i < 9; i += 3) {
            for (int j = 0; j < 9; j += 3) {
                constraintsTests.add(new BlockConstraint(SymbolSets.DIGITS, i, j, i + 3, j + 3));
            }
        }
        constraintsTests.add(new LineConstraint(SymbolSets.DIGITS));
        constraintsTests.add(new ColumnConstraint(SymbolSets.DIGITS));


        var grid = new Grid(new Character[][]{
                {'5', ' ', ' ', '9', '2', ' ', ' ', '7', ' '},
                {' ', ' ', ' ', ' ', ' ', '1', ' ', ' ', ' '},
                {'1', ' ', ' ', ' ', ' ', ' ', ' ', '5', '6'},
                {'4', '5', '2', ' ', '9', ' ', ' ', ' ', ' '},
                {'7', ' ', ' ', '3', ' ', '2', ' ', ' ', ' '},
                {' ', ' ', '9', '5', '6', ' ', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', '8', ' ', '1', ' ', '2'},
                {' ', '6', ' ', '7', ' ', ' ', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '4'}
        }, constraintsTests);

        var res = new Character[][]{
                {'5', '4', '3', '9', '2', '6', '8', '7', '1',},
                {'6', '2', '7', '8', '5', '1', '3', '4', '9',},
                {'1', '9', '8', '4', '7', '3', '2', '5', '6',},
                {'4', '5', '2', '1', '9', '7', '6', '8', '3',},
                {'7', '8', '6', '3', '4', '2', '9', '1', '5',},
                {'3', '1', '9', '5', '6', '8', '4', '2', '7',},
                {'9', '7', '4', '6', '8', '5', '1', '3', '2',},
                {'2', '6', '1', '7', '3', '4', '5', '9', '8',},
                {'8', '3', '5', '2', '1', '9', '7', '6', '4',},

        };

        assertTrue(grid.areConstraintsSatisfied());

        var pastialSolve = SudokuSolver.solve(grid, SymbolSets.DIGITS, false);
        assertEquals(SolvingState.PARTIALLY_SOLVED, pastialSolve);
        var isSolved = SudokuSolver.solve(grid, SymbolSets.DIGITS, true);
        assertEquals(SolvingState.SOLVED, isSolved);
        assertTrue(grid.areConstraintsSatisfied());


        var solvedGrid = grid.getGrid();
        for (int i = 0; i < solvedGrid.length; i++) {
            for (int j = 0; j < solvedGrid[i].length; j++) {
                assertEquals(res[i][j], solvedGrid[i][j]);
            }
        }

    }

    @Test
    public void testSolveFail() {
        var grid = new Grid(new Character[][]{
                {'4', '2', '3'},
                {'3', '1', '2'},
                {'2', '1', '3'}
        }, List.of(
                new BlockConstraint(Set.of('1', '2', '3'), 0, 0, 2, 2)
        ));
        assertFalse(grid.areConstraintsSatisfied());
    }
}
