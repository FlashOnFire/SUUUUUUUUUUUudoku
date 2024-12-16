package fr.polytech.suuuuuuuuuuudoku;

import fr.polytech.suuuuuuuuuuudoku.constraints.BlockConstraint;
import fr.polytech.suuuuuuuuuuudoku.constraints.ColumnConstraint;
import fr.polytech.suuuuuuuuuuudoku.constraints.LineConstraint;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConstraintsTests {

    @Test
    public void testBlockConstraint() {
        var grid = new Grid(new Character[][]{
                {'4', '2', '3'},
                {'3', '1', '2'},
                {'2', '1', '3'}
        }, List.of(
                new BlockConstraint(Set.of('1', '2', '3', '4'), 0, 0, 2, 2)
        ));
        assertTrue(grid.areConstraintsSatisfied());
    }

    @Test
    public void testBlockConstraintFail() {
        var grid = new Grid(new Character[][]{
                {'4', '2', '3'},
                {'3', '1', '2'},
                {'2', '1', '3'}
        }, List.of(
                new BlockConstraint(Set.of('1', '2', '3'), 0, 0, 2, 2)
        ));
        assertFalse(grid.areConstraintsSatisfied());
    }

    @Test
    public void testColumnConstraint() {
        var grid = new Grid(new Character[][]{
                {'4', '2', '3'},
                {'3', '1', '2'},
                {'2', '1', '3'}
        }, List.of(
                new ColumnConstraint(Set.of('1', '2', '3', '4'))
        ));
        assertTrue(grid.areConstraintsSatisfied());
    }

    @Test
    public void testColumnConstraintFail() {
        var grid = new Grid(new Character[][]{
                {'4', '2', '3'},
                {'3', '1', '2'},
                {'2', '1', '3'}
        }, List.of(
                new ColumnConstraint(Set.of('1', '2', '3'))
        ));
        assertFalse(grid.areConstraintsSatisfied());
    }

    @Test
    public void testLineConstraint() {
        var grid = new Grid(new Character[][]{
                {'4', '2', '3'},
                {'3', '1', '2'},
                {'2', '1', '3'}
        }, List.of(
                new LineConstraint(Set.of('1', '2', '3', '4'))
        ));
        assertTrue(grid.areConstraintsSatisfied());
    }

    @Test
    public void testLineConstraintFail() {
        var grid = new Grid(new Character[][]{
                {'4', '2', '3'},
                {'3', '1', '2'},
                {'2', '1', '3'}
        }, List.of(
                new LineConstraint(Set.of('1', '2', '3'))
        ));
        assertFalse(grid.areConstraintsSatisfied());
    }
}
