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

        return generateNxM((int) Math.floor(Math.sqrt(n)), (int) Math.floor(Math.sqrt(n)));
    }

    public static Grid generateNxM(int n, int m) {
        var symbols = SymbolSets.generateSymbols(n * m);

        var innerGrid = new Integer[n*m][n*m];
        for (var i = 0; i < n; i++) {
            Arrays.fill(innerGrid[i], null);
        }

        Grid seedGrid;
        SolvingState state;
        Grid solvedGrid;
        do {
            seedGrid = new Grid(innerGrid, symbols, n, m);

            var pos = new HashSet<Vec2i>();
            while (pos.size() < n * m) {
                var x = (int) (Math.random() * n);
                var y = (int) (Math.random() * m);
                pos.add(new Vec2i(x, y));
            }

            var posArray = pos.toArray(Vec2i[]::new);
            var symbolsArray = symbols.toArray(Integer[]::new);

            Grid finalSeedGrid = seedGrid;
            IntStream.range(0, n * m).forEach(i -> finalSeedGrid.placeUnchecked(posArray[i], symbolsArray[i], false));
            seedGrid.computeAllEmptyCellsPossibilities();

            var pair = SudokuSolver.solve(seedGrid, true, true);
            state = pair.getFirst();
            solvedGrid = pair.getSecond();
        } while (state != SolvingState.SOLVED);

        Vec2i last_move_pos;
        Integer last_move_symbol;
        do {
            var emptyCells = solvedGrid.getEmptyCellsPossibilities().keySet();
            Vec2i randomPos;
            do {
                //random among empty cells
                randomPos = Vec2i.random(n * m, n * m);
            } while (emptyCells.contains(randomPos));

            last_move_pos = randomPos;
            last_move_symbol = solvedGrid.getSymbolAt(randomPos);
            solvedGrid.placeUnchecked(randomPos, null, true);
        } while (!SudokuSolver.hasMoreThanOneSolution(solvedGrid, true, true));

        solvedGrid.placeUnchecked(last_move_pos, last_move_symbol, true);
        return solvedGrid;
    }
}
