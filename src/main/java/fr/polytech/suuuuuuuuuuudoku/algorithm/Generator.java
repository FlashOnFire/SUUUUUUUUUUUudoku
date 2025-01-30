package fr.polytech.suuuuuuuuuuudoku.algorithm;

import fr.polytech.suuuuuuuuuuudoku.constraints.AbstractConstraint;
import fr.polytech.suuuuuuuuuuudoku.constraints.BlockConstraint;
import fr.polytech.suuuuuuuuuuudoku.constraints.GeneralSymbolConstraint;
import fr.polytech.suuuuuuuuuuudoku.grid.Grid;
import fr.polytech.suuuuuuuuuuudoku.grid.InnerGrid;
import fr.polytech.suuuuuuuuuuudoku.symbols.SymbolSets;

import java.util.*;
import java.util.stream.IntStream;

public class Generator {
    /**
     * Generate a classic grid of size NxN
     * @param n: The size of the grid
     * @return A grid to be played
     */
    public static Grid generateClassicNxN(int n) {
        //assert n is perfect square
        assert Math.sqrt(n) == Math.floor(Math.sqrt(n));

        return generateNxM((int) Math.floor(Math.sqrt(n)), (int) Math.floor(Math.sqrt(n)));
    }

    /**
     * Generate a random grid with blocks constraints of size NxM
     * @param n: The number of rows
     * @param m: The number of columns
     * @return A grid to be played
     */
    public static Grid generateNxM(int n, int m) {
        var solvedGrid = generateSolvedNxMGrid(n, m);
        return unpopulateGrid(solvedGrid, n*m);
    }

    /**
     * Generate a random grid of size NxN
     * @param lengthInnerGrid: The length of the inner grid
     * @return A grid to be played
     */
    public static Grid generateRandomGridN(int lengthInnerGrid) {
        System.out.println("Generating random grid of size " + lengthInnerGrid + "x" + lengthInnerGrid);
        var symbols = SymbolSets.generateSymbols(lengthInnerGrid);
        var innerGrid = new Integer[lengthInnerGrid][lengthInnerGrid];
        for (var i = 0; i < lengthInnerGrid; i++) {
            Arrays.fill(innerGrid[i], null);
        }

        Vec2i dividers = findDividers(lengthInnerGrid);
        Grid solvedGrid = generateSolvedNxMGrid(dividers.getLine(), dividers.getColumn());

        var generalSymbolConstraints = generateRandomConstraint(lengthInnerGrid, solvedGrid, symbols);

        // We update the new constraints
        solvedGrid = new Grid(solvedGrid.getInnerGrid().get(), generalSymbolConstraints, symbols);

        // We solve the grid
        solvedGrid = SudokuSolver.solve(solvedGrid, true, true, false).getSecond();

        return unpopulateGrid(solvedGrid, lengthInnerGrid);
    }

    /**
     * Suppress randoms cells from a solved grid to generate a random sudoku grid to solve
     * @param solvedGrid: The solved grid
     * @param lengthInnerGrid: The length of the inner grid
     * @return A grid to be played
     */
    private static Grid unpopulateGrid(Grid solvedGrid, int lengthInnerGrid) {
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
     * @param length_innerGrid: The length of the inner grid
     * @param grid: A solved grid with NxM BlocksConstraint
     * @param symbols: The set of symbols
     * @return A list of constraints with random positions for each GeneralSymbolConstraint
     */
    private static List<AbstractConstraint<InnerGrid, Vec2i>> generateRandomConstraint(int length_innerGrid, Grid grid, Set<Integer> symbols) {
        List<AbstractConstraint<InnerGrid, Vec2i>> generalSymbolConstraints = new ArrayList<>();
        for (AbstractConstraint<InnerGrid, Vec2i> constraint : grid.getConstraints()) {
            if (constraint instanceof BlockConstraint) {
                var list = new ArrayList<Vec2i>();
                for (int posX = ((BlockConstraint) constraint).getBlock().line(); posX < ((BlockConstraint) constraint).getBlock().line2(); posX++) {
                    for (int posY = ((BlockConstraint) constraint).getBlock().column(); posY < ((BlockConstraint) constraint).getBlock().column2(); posY++) {
                        list.add(new Vec2i(posX, posY));
                    }
                }
                generalSymbolConstraints.add(new GeneralSymbolConstraint(symbols, list.toArray(Vec2i[]::new)));
            } else {
                generalSymbolConstraints.add(constraint);
            }
        }
        for (int i = 0; i < length_innerGrid * 4; i++) {
            int randomId = (int) (Math.random() * symbols.size());
            int constraintId1 = (int) (Math.random() * length_innerGrid);
            int constraintId2 = (int) (Math.random() * length_innerGrid);
            Vec2i[] constraint1 = ((GeneralSymbolConstraint) generalSymbolConstraints.get(constraintId1)).getPositionList();
            Vec2i[] constraint2 = ((GeneralSymbolConstraint) generalSymbolConstraints.get(constraintId2)).getPositionList();

            int value = grid.getInnerGrid().at(constraint1[randomId]);
            for (int otherValId = 0; otherValId < constraint2.length; otherValId++) {
                if (grid.getInnerGrid().at(constraint2[otherValId]) == value) {
                    swapConstraints(constraint1, constraint2, randomId, otherValId);
                    generalSymbolConstraints.set(constraintId1, new GeneralSymbolConstraint(symbols, constraint1));
                    generalSymbolConstraints.set(constraintId2, new GeneralSymbolConstraint(symbols, constraint2));
                    break;
                }
            }
        }
        return generalSymbolConstraints;
    }

    /**
     * Swap one position of the constraints one with the other
     * @param constraint1: The first constraint position list
     * @param constraint2: The second constraint position list
     * @param constraintId1: The index of the first constraint
     * @param constraintId2: The index of the second constraint
     */
    private static void swapConstraints(Vec2i[] constraint1, Vec2i[] constraint2, int constraintId1, int constraintId2) {
        Vec2i temp = constraint1[constraintId1];
        constraint1[constraintId1] = constraint2[constraintId2];
        constraint2[constraintId2] = temp;
    }

    /**
     * Generate a random solved grid of size NxM * NxM
     * @param n: The number of rows
     * @param m: The number of columns
     * @return A solved grid
     */
    private static Grid generateSolvedNxMGrid(int n, int m) {
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
