package fr.polytech.suuuuuuuuuuudoku.algorithm;

import fr.polytech.suuuuuuuuuuudoku.grid.Grid;
import fr.polytech.suuuuuuuuuuudoku.symbols.SymbolSets;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.IntStream;

public class Generator {
    public static Grid generateClassicNxN(int n) {
        //assert n is perfect square
        assert Math.sqrt(n) == Math.floor(Math.sqrt(n));

        var symbols = SymbolSets.generateSymbols(n);

        var innerGrid = new Integer[n][n];
        for (var i = 0; i < n; i++) {
            Arrays.fill(innerGrid[i], null);
        }

        var grid = new Grid(innerGrid, symbols);

        var pos = new HashSet<Vec2i>();
        while (pos.size() < n) {
            var x = (int) (Math.random() * n);
            var y = (int) (Math.random() * n);
            pos.add(new Vec2i(x, y));
        }

        var posArray = pos.toArray(Vec2i[]::new);
        var symbolsArray = symbols.toArray(Integer[]::new);

        IntStream.range(0, 9).forEach(i -> grid.placeUnchecked(posArray[i], symbolsArray[i], false));
        grid.computeAllEmptyCellsPossibilities();

        SudokuSolver.solve(grid, true, true);
        assert grid.isSolved();

        var emptyCells = grid.getEmptyCellsPossibilities().keySet();

        Vec2i last_move_pos;
        Integer last_move_symbol;
        do {
            Vec2i randomPos;
            do {
                randomPos = Vec2i.random(n, n);
            } while (emptyCells.contains(randomPos));

            last_move_pos = randomPos;
            last_move_symbol = grid.getSymbolAt(randomPos);
            grid.placeUnchecked(randomPos, null, false);
        } while (grid.isSolved());

        grid.placeUnchecked(last_move_pos, last_move_symbol, false);

        grid.computeAllEmptyCellsPossibilities();

        return grid;
    }
}
