package fr.polytech.suuuuuuuuuuudoku;

import fr.polytech.suuuuuuuuuuudoku.constraints.AbstractConstraint;
import fr.polytech.suuuuuuuuuuudoku.solver.Vec2i;

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
            for (Character cell : lines) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }

    public boolean areConstraintsSatisfied() {
        return this.constraints.stream().allMatch(c -> c.isSatisfied(this.grid));
    }

    public Character[][] getGrid() {
        return grid;
    }

    public boolean tryPlace(Vec2i pos, char value) {
        var oldValue = this.grid[pos.x()][pos.y()];
        this.grid[pos.x()][pos.y()] = value;
        if (!this.areConstraintsSatisfied()) {
            //revert
            this.grid[pos.x()][pos.y()] = oldValue;
            return false;
        }

        return true;
    }

    public List<AbstractConstraint> getConstraints() {
        return constraints;
    }
}
