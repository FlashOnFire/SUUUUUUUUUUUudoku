package fr.polytech;

import fr.polytech.constraints.AbstractConstraint;

import java.util.List;

public class Grid {
    private int[][] grid;
    private List<AbstractConstraint> constraints;

    public Grid(int[][] grid, List<AbstractConstraint> constraints) {
        this.grid = grid;
        this.constraints = constraints;
    }

    public int[][] getGrid() {
        return this.grid;
    }

    public void setGrid(int[][] grid) {
        this.grid = grid;
    }

    public void display() {
        for (int[] lines : this.grid) {
            for (int n : lines) {
                System.out.print(n + " ");
            }
            System.out.println();
        }
    }
}
