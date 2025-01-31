package fr.polytech.suuuuuuuuuuudoku.algorithm;

import fr.polytech.suuuuuuuuuuudoku.constraints.*;
import fr.polytech.suuuuuuuuuuudoku.grid.Grid;
import fr.polytech.suuuuuuuuuuudoku.grid.InnerGrid;
import fr.polytech.suuuuuuuuuuudoku.symbols.SymbolSets;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Generator {
    /**
     * Generate a classic grid of size NxN
     *
     * @param n: The size of the grid
     * @return A grid to be played
     */
    public static Grid generateClassicSudoku(int n) {
        //assert n is perfect square
        assert Math.sqrt(n) == Math.floor(Math.sqrt(n));

        return generateSudokuWithNxMConstraintBlock((int) Math.floor(Math.sqrt(n)), (int) Math.floor(Math.sqrt(n)));
    }

    /**
     * Generate a random grid with blocks constraints of size NxM
     *
     * @param n: The number of rows
     * @param m: The number of columns
     * @return A grid to be played
     */
    public static Grid generateSudokuWithNxMConstraintBlock(int n, int m) {
        var solvedGrid = createSolvedSudoku(n, m);
        return removeRandomCells(solvedGrid, n * m);
    }

    /**
     * Generate a grid with random block constraints
     *
     * @param lengthInnerGrid: The length of the inner grid
     * @return A grid to be played
     */
    public static Grid generateSudokuWithRandomBlockConstraint(int lengthInnerGrid) {
        System.out.println("Generating random grid of size " + lengthInnerGrid + "x" + lengthInnerGrid);
        var symbols = SymbolSets.generateSymbols(lengthInnerGrid);
        Vec2i dividers = findDividers(lengthInnerGrid);
        Grid solvedGrid = createSolvedSudoku(dividers.getX(), dividers.getY());
        var generalSymbolConstraints = createRandomConstraints(solvedGrid);

        // We update the new constraints
        solvedGrid = new Grid(solvedGrid.getInnerGrid().get(), generalSymbolConstraints, symbols);
        assert solvedGrid.isSolved();
        return removeRandomCells(solvedGrid, lengthInnerGrid);
    }

    /**
     * Suppress randoms cells from a solved grid to generate a random sudoku grid to solve
     *
     * @param solvedGrid:      The solved grid
     * @param lengthInnerGrid: The length of the inner grid
     * @return A grid to be played
     */
    private static Grid removeRandomCells(Grid solvedGrid, int lengthInnerGrid) {
        Vec2i lastMovePos;
        Integer lastMoveSymbol;
        Set<Vec2i> emptyCells = solvedGrid.getEmptyCellsPossibilities().keySet();
        Random random = new Random();
        do {
            Vec2i randomPos;
            do {
                randomPos = new Vec2i(random.nextInt(0, lengthInnerGrid), random.nextInt(0, lengthInnerGrid));
            } while (emptyCells.contains(randomPos));

            lastMovePos = randomPos;
            lastMoveSymbol = solvedGrid.getSymbolAt(randomPos);
            solvedGrid.placeUnchecked(randomPos, null, true, false);
        } while (!SudokuSolver.hasMoreThanOneSolution(solvedGrid, true, true));

        solvedGrid.placeUnchecked(lastMovePos, lastMoveSymbol, true, false);
        return solvedGrid;
    }

    /**
     * Find 2 dividers of a number
     *
     * @param n: The number to find the dividers
     * @return A position with the dividers inside
     */
    private static Vec2i findDividers(int n) {
        int x = (int) Math.sqrt(n);
        int y = n / x;
        if (x * y == n) {
            return new Vec2i(x, y);
        }

        for (int i = x; i > 0; i--) {
            if (n % i == 0) {
                return new Vec2i(i, n / i);
            }
        }
        return new Vec2i(1, n);
    }

    /**
     * Generate random constraints for a solved grid
     * Use a solved grid of NxM blocks to generate random constraints (shuffle the constraints positions)
     *
     * @param grid:             A solved grid
     * @return A list of constraints with random positions for each GeneralSymbolConstraint
     */
    static List<AbstractConstraint<InnerGrid, Vec2i>> createRandomConstraints(Grid grid) {
        int length = grid.length();
        List<List<Vec2i>> positionList = IntStream.range(0, length)
                .mapToObj(_ -> new ArrayList<Vec2i>())
                .collect(Collectors.toList());
        for (int x = 0; x < length; x++) {
            for (int y = 0; y < length; y++) {
                Integer symbol = grid.getSymbolAt(x, y);
                if (symbol != null) {
                    positionList.get(symbol-1).add(new Vec2i(x, y));
                } else {
                    System.out.println("Error: symbol is null");
                }
            }
        }
        List<AbstractConstraint<InnerGrid, Vec2i>> constraints = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            List<Vec2i> listInConstraint = new ArrayList<>();
            for (int j = 0; j < length; j++) {
                int selectedPos = (int) (Math.random() * positionList.get(j).size());
                Vec2i pos = positionList.get(j).get(selectedPos);
                listInConstraint.add(pos);
                positionList.get(i).remove(pos);
            }
            constraints.add(new GeneralSymbolConstraint(SymbolSets.generateSymbols(length), listInConstraint.toArray(Vec2i[]::new)));
        }
        constraints.add(new LineConstraint(grid.getSymbols()));
        constraints.add(new ColumnConstraint(grid.getSymbols()));
        constraints.add(new NotEmptyConstraint());
        return constraints;
    }

    /**
     * Generate a random solved grid of size NxM * NxM
     *
     * @param n: The number of rows
     * @param m: The number of columns
     * @return A solved grid
     */
    private static Grid createSolvedSudoku(int n, int m) {
        var symbols = SymbolSets.generateSymbols(n * m);
        var innerGrid = new Integer[n * m][n * m];
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
            IntStream.range(0, n * m).forEach(i -> finalSeedGrid.placeUnchecked(posArray[i], symbolsArray[i], false,
                    false));
            seedGrid.computeAllEmptyCellsPossibilities();

            var pair = SudokuSolver.solve(seedGrid, true, true, false);
            state = pair.getFirst();
            solvedGrid = pair.getSecond();
        } while (state != SolvingState.SOLVED);
        return solvedGrid;
    }
}
