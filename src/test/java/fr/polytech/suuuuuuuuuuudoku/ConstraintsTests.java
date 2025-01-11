package fr.polytech.suuuuuuuuuuudoku;

import fr.polytech.suuuuuuuuuuudoku.constraints.BlockConstraint;
import fr.polytech.suuuuuuuuuuudoku.constraints.ColumnConstraint;
import fr.polytech.suuuuuuuuuuudoku.constraints.LineConstraint;
import fr.polytech.suuuuuuuuuuudoku.grid.Grid;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConstraintsTests {

    @Test
    public void testBlockConstraint() {
        var grid = new Grid(new String[][]{
                {"4", "2", "3"},
                {"3", "1", "2"},
                {"2", "1", "3"}
        }, List.of(
                new BlockConstraint(Set.of("1", "2", "3", "4"), 0, 0, 2, 2)
        ), Set.of("1", "2", "3", "4"));
        assertTrue(grid.areConstraintsSatisfied(false));
    }

    @Test
    public void testBlockConstraintEmpty() {
        var grid = new Grid(new String[][]{
                {" ", " ", " "},
                {" ", " ", " "},
                {" ", " ", " "}
        }, List.of(
                new BlockConstraint(Set.of("1", "2", "3", "4"), 0, 0, 2, 2)
        ), Set.of("1", "2", "3", "4"));
        assertTrue(grid.areConstraintsSatisfied(false));
    }

    @Test
    public void testBlockConstraintFail() {
        var grid = new Grid(new String[][]{
                {"4", "2", "3"},
                {"3", "1", "2"},
                {"2", "1", "3"}
        }, List.of(
                new BlockConstraint(Set.of("1", "2", "3"), 0, 0, 2, 2)
        ), Set.of("1", "2", "3"));
        assertFalse(grid.areConstraintsSatisfied(false));
    }

    @Test
    public void testColumnConstraint() {
        var grid = new Grid(new String[][]{
                {"1", "2", "3"},
                {"3", "3", "2"},
                {"2", "1", "1"}
        }, List.of(
                new ColumnConstraint(Set.of("1", "2", "3"))
        ), Set.of("1", "2", "3"));
        assertTrue(grid.areConstraintsSatisfied(false));
    }

    @Test
    public void testColumnConstraintEmpty() {
        var grid = new Grid(new String[][]{
                {" ", " ", " "},
                {" ", " ", " "},
                {" ", " ", " "}
        }, List.of(
                new ColumnConstraint(Set.of("1", "2", "3"))
        ), Set.of("1", "2", "3"));
        assertTrue(grid.areConstraintsSatisfied(false));
    }

    @Test
    public void testColumnConstraintFail() {
        var grid = new Grid(new String[][]{
                {"4", "2", "3"},
                {"3", "1", "2"},
                {"2", "1", "3"}
        }, List.of(
                new ColumnConstraint(Set.of("1", "2", "3"))
        ), Set.of("1", "2", "3"));
        assertFalse(grid.areConstraintsSatisfied(false));
    }

    @Test
    public void testLineConstraint() {
        var grid = new Grid(new String[][]{
                {"1", "2", "3"},
                {"3", "1", "2"},
                {"2", "1", "3"}
        }, List.of(
                new LineConstraint(Set.of("1", "2", "3"))
        ), Set.of("1", "2", "3"));
        assertTrue(grid.areConstraintsSatisfied(false));
    }

    @Test
    public void testLineConstraintEmpty() {
        var grid = new Grid(new String[][]{
                {" ", " ", " "},
                {" ", " ", " "},
                {" ", " ", " "}
        }, List.of(
                new LineConstraint(Set.of("1", "2", "3"))
        ), Set.of("1", "2", "3"));
        assertTrue(grid.areConstraintsSatisfied(false));
    }

    @Test
    public void testLineConstraintFail() {
        var grid = new Grid(new String[][]{
                {"4", "2", "3"},
                {"3", "1", "2"},
                {"2", "1", "3"}
        }, List.of(
                new LineConstraint(Set.of("1", "2", "3"))
        ), Set.of("1", "2", "3"));
        assertFalse(grid.areConstraintsSatisfied(false));
    }
}
