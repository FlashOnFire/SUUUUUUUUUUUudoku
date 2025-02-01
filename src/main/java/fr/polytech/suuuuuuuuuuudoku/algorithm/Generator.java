package fr.polytech.suuuuuuuuuuudoku.algorithm;

import fr.polytech.suuuuuuuuuuudoku.constraints.*;
import fr.polytech.suuuuuuuuuuudoku.grid.Grid;
import fr.polytech.suuuuuuuuuuudoku.symbols.SymbolSets;

import java.util.*;

public class Generator {
    /**
     * Generates a classic NxN grid
     *
     * @param n: The size of the grid
     * @return A playable grid
     */
    public static Grid generateClassicSudoku(int n) {
        //assert n is perfect square
        int sqrt = (int) Math.sqrt(n);
        assert sqrt * sqrt == n;

        return generateSudokuWithBlockConstraints((int) Math.sqrt(n), (int) Math.sqrt(n));
    }

    /**
     * Generates a random grid with block constraints of size NxM
     *
     * @param blockRows:    The number of block rows
     * @param blockColumns: The number of block columns
     * @return A playable grid
     */
    public static Grid generateSudokuWithBlockConstraints(int blockRows, int blockColumns) {
        var solvedGrid = createSolvedSudoku(blockRows, blockColumns);
        return removeRandomCells(solvedGrid, blockRows * blockColumns);
    }

    /**
     * Generates a grid with random block constraints
     *
     * @param lengthInnerGrid: The length of the inner grid
     * @return A playable grid
     */
    public static Grid generateSudokuWithRandomBlockConstraint(int lengthInnerGrid) {
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
     * Removes random cells from a solved grid to generate a random Sudoku grid to solve
     *
     * @param solvedGrid:      The solved grid
     * @param lengthInnerGrid: The length of the inner grid
     * @return A playable grid
     */
    private static Grid removeRandomCells(Grid solvedGrid, int lengthInnerGrid) {
        Vec2i lastMovePos;
        Integer lastMoveSymbol;
        Set<Vec2i> emptyCells = solvedGrid.getEmptyCellsPossibilities().keySet();
        Random random = new Random();
        do {
            Vec2i randomPos;
            do {
                randomPos = Vec2i.random(lengthInnerGrid, lengthInnerGrid);
            } while (emptyCells.contains(randomPos));

            lastMovePos = randomPos;
            lastMoveSymbol = solvedGrid.getSymbolAt(randomPos);
            solvedGrid.placeUnchecked(randomPos, null, true, false);
        } while (!SudokuSolver.hasMoreThanOneSolution(solvedGrid, true, true));

        solvedGrid.placeUnchecked(lastMovePos, lastMoveSymbol, true, false);
        return solvedGrid;
    }

    /**
     * Finds 2 dividers of a number
     *
     * @param n: The number to find dividers for
     * @return A position with the dividers inside
     */
    private static Vec2i findDividers(int n) {
        for (int i = (int) Math.sqrt(n); i > 0; i--) {
            if (n % i == 0) {
                return new Vec2i(i, n / i);
            }
        }
        return new Vec2i(1, n);
    }

    /**
     * Generates random constraints for a solved grid
     * Uses a solved NxM block grid to generate random constraints (shuffles the positions of the constraints)
     *
     * @param grid: A solved grid
     * @return A list of constraints with random positions for each GeneralSymbolConstraint
     */
    static List<AbstractConstraint> createRandomConstraints(Grid grid) {
        int length = grid.length();

        List<List<Vec2i>> positionList = new ArrayList<>(Collections.nCopies(length, new ArrayList<>()));
        for (int x = 0; x < length; x++) {
            for (int y = 0; y < length; y++) {
                Integer symbol = grid.getSymbolAt(x, y);
                if (symbol != null) {
                    positionList.get(symbol - 1).add(new Vec2i(x, y));
                } else {
                    System.out.println("Error: symbol is null");
                }
            }
        }
        List<AbstractConstraint> constraints = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            List<Vec2i> listInConstraint = new ArrayList<>();
            for (int j = 0; j < length; j++) {
                int selectedPos = (int) (Math.random() * positionList.get(j).size());
                Vec2i pos = positionList.get(j).get(selectedPos);
                listInConstraint.add(pos);
                positionList.get(i).remove(pos);
            }
            constraints.add(new GeneralSymbolConstraint(SymbolSets.generateSymbols(length),
                    listInConstraint.toArray(Vec2i[]::new)));
        }
        constraints.add(new LineConstraint(grid.getSymbols()));
        constraints.add(new ColumnConstraint(grid.getSymbols()));
        constraints.add(new NotEmptyConstraint());
        return constraints;
    }

    /**
     * Generates a random solved grid of size NxM * NxM
     *
     * @param blockRows:    The number of block rows
     * @param blockColumns: The number of block columns
     * @return A solved grid
     */
    private static Grid createSolvedSudoku(int blockRows, int blockColumns) {
        var symbols = SymbolSets.generateSymbols(blockRows * blockColumns);
        var innerGrid = new Integer[blockRows * blockColumns][blockRows * blockColumns];
        for (var i = 0; i < blockRows; i++) {
            Arrays.fill(innerGrid[i], null);
        }

        Grid seedGrid;
        SolvingState state;
        Grid solvedGrid;
        do {
            seedGrid = new Grid(innerGrid, symbols, blockRows, blockColumns);

            var symbolsArray = new ArrayList<>(symbols);
            Collections.shuffle(symbolsArray);
            for (var i = 0; i < blockColumns * blockRows; i++) {
                seedGrid.placeUnchecked(new Vec2i(i, i), symbolsArray.get(i % symbolsArray.size()), false, false);
            }

            seedGrid.computeAllEmptyCellsPossibilities();

            var pair = SudokuSolver.solve(seedGrid, true, true, false);

            state = pair.getFirst();
            solvedGrid = pair.getSecond();
        } while (state != SolvingState.SOLVED);
        return solvedGrid;
    }
}