package fr.polytech.suuuuuuuuuuudoku;

import fr.polytech.suuuuuuuuuuudoku.constraints.BlockConstraint;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResolveTest {
    @Test
    public void testSolve() {
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
        }, List.of(
                new BlockConstraint(Set.of('1', '2', '3', '4', '5', '6', '7', '8', '9'), 0, 0, 9, 9)

        ));

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
    }

    @Test
    public void testSolveFail() {
        /*
        var grid = new Grid(new Character[][]{
                {'4', '2', '3'},
                {'3', '1', '2'},
                {'2', '1', '3'}
        }, List.of(
                new BlockConstraint(Set.of('1', '2', '3'), 0, 0, 2, 2)
        ));
        assertFalse(grid.areConstraintsSatisfied());
        */
    }
}
