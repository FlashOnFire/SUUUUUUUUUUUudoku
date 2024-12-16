package fr.polytech;

import fr.polytech.constraints.AbstractConstraint;

import java.util.List;

public class Grid {
    private char[][] grid;
    private List<AbstractConstraint> constraints;

    public Grid(char[][] grid, List<AbstractConstraint> constraints) {
        this.grid = grid;
        this.constraints = constraints;
    }

    public char[][] getGrid() {
        return this.grid;
    }

    public void setGrid(char[][] grid) {
        this.grid = grid;
    }

    public void display() {
        for (char[] lines : this.grid) {
            for (char cell: lines) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }

    public boolean areConstraintsSatisfied() {
        return this.constraints.stream().allMatch(c -> c.isSatisfied(this.grid));
    }
}
