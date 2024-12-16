package fr.polytech;

import fr.polytech.constraints.AbstractConstraint;

import java.util.List;

public class Grid {
    private Character[][] grid;
    private List<AbstractConstraint> constraints;

    public Grid(Character[][] grid, List<AbstractConstraint> constraints) {
        this.grid = grid;
        this.constraints = constraints;
    }

    public void display() {
        for (Character[] lines : this.grid) {
            for (Character cell: lines) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }

    public boolean areConstraintsSatisfied() {
        return this.constraints.stream().allMatch(c -> c.isSatisfied(this.grid));
    }
}
