package fr.polytech.suuuuuuuuuuudoku.grid;

public class MultiGrid {
    private final InnerGrid[] grids;

    public MultiGrid(InnerGrid[] grids) {
        this.grids = grids;
    }

    public InnerGrid[] getGrids() {
        return grids;
    }
}
