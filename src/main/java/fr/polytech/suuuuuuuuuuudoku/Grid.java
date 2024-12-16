package fr.polytech.suuuuuuuuuuudoku;

import fr.polytech.suuuuuuuuuuudoku.constraints.AbstractConstraint;
import fr.polytech.suuuuuuuuuuudoku.solver.Vec2i;

import java.util.ArrayList;
import java.util.Arrays;
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
        return this.constraints.stream().allMatch(c -> {
            if (!c.isSatisfied(this.grid)) {
                System.out.println("Constraint not satisfied: " + c);
                return false;
            }
            return true;
        });
    }

    public Character[][] getGrid() {
        return grid;
    }

    public void setGrid(Character[][] grid) {
        this.grid = grid;
    }

    public boolean tryPlace(Vec2i pos, char value) {
        var oldValue = this.grid[pos.getY()][pos.getX()];
        this.grid[pos.getY()][pos.getX()] = value;
        if (!this.areConstraintsSatisfied()) {
            //revert
            this.grid[pos.getY()][pos.getX()] = oldValue;

            System.out.println("Invalid placement (" + value + ") at " + pos + ", reverting");
            return false;
        }

        //System.out.println("Placed " + value + " at " + pos);
        return true;
    }

    public List<AbstractConstraint> getConstraints() {
        return constraints;
    }

    public boolean isSolved() {
        return this.getEmptyCells().isEmpty() && this.areConstraintsSatisfied();
    }

    public List<Vec2i> getEmptyCells() {
        var list = new ArrayList<Vec2i>();

        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
                if (grid[y][x] == ' ') {
                    list.add(new Vec2i(x, y));
                }
            }
        }

        return list;
    }

    public Grid clone() {
        var newGrid = new Character[this.grid.length][this.grid[0].length];
        for (int y = 0; y < this.grid.length; y++) {
            newGrid[y] = Arrays.copyOf(this.grid[y], this.grid[y].length);
        }
        return new Grid(newGrid, this.constraints);
    }
}
