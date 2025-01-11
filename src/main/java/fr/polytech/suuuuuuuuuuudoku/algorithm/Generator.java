package fr.polytech.suuuuuuuuuuudoku.algorithm;

import fr.polytech.suuuuuuuuuuudoku.grid.Grid;
import fr.polytech.suuuuuuuuuuudoku.symbols.SymbolSets;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.IntStream;

public class Generator {
    public static Grid generate9x9() {
        var symbols = SymbolSets.generateSymbols(9);

        var innerGrid = new String[9][9];
        for (var i = 0; i < 9; i++) {
            Arrays.fill(innerGrid[i], " ");
        }

        var grid = new Grid(innerGrid, symbols);

        var pos = new HashSet<Vec2i>();
        while (pos.size() < symbols.size()) {
            var x = (int) (Math.random() * 9);
            var y = (int) (Math.random() * 9);
            pos.add(new Vec2i(x, y));
        }

        var posArray = pos.toArray(Vec2i[]::new);
        var symbolsArray = symbols.toArray(String[]::new);

        IntStream.range(0, 9).forEach(i -> grid.placeUnchecked(posArray[i], symbolsArray[i], false));
        grid.computeAllEmptyCellsPossibilities();

        SudokuSolver.solve(grid, true, true);
        assert grid.isSolved();

        grid.display();

        return grid;
    }
}
