package fr.polytech.suuuuuuuuuuudoku.grid;

@FunctionalInterface
public interface GridListener {
    void onGridChange(final InnerGrid grid);
}
