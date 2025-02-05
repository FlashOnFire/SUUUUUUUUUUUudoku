package fr.polytech.suuuuuuuuuuudoku.algorithm;

import fr.polytech.suuuuuuuuuuudoku.CsvUtils;
import fr.polytech.suuuuuuuuuuudoku.constraints.*;
import fr.polytech.suuuuuuuuuuudoku.graphics.Utils;
import fr.polytech.suuuuuuuuuuudoku.grid.Grid;
import fr.polytech.suuuuuuuuuuudoku.grid.MultiGrid;
import fr.polytech.suuuuuuuuuuudoku.grid.Solvable;
import fr.polytech.suuuuuuuuuuudoku.symbols.SymbolSets;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Generator {
    /**
     * Generates a classic NxN grid
     *
     * @param n: The size of the grid
     * @return A playable grid
     */
    public static Grid generateClassicSudoku(int n) throws InterruptedException {
        //assert n is perfect square
        int sqrt = (int) Math.sqrt(n);
        assert sqrt * sqrt == n;

        return generateSudokuWithBlockConstraints((int) Math.sqrt(n), (int) Math.sqrt(n));
    }

    /**
     * Generates a multigrid with block constraints of size NxM
     *
     * @param n: The size of the sub grid
     * @param m: The number of sub grids
     * @return A playable multigrid
     */
    public static MultiGrid generateMultigridSudoku(int n, int m) throws InterruptedException {
        List<Pair<Vec2i, Grid>> grids = new ArrayList<>();
        var blockSize = (int) Math.sqrt(n);
        var paddings = MultiGrid.getRandomPadding();
        var symbols = SymbolSets.generateSymbols(n);

        var centeredGrid = fastSolvedGridCreation(blockSize, blockSize);
        var innerGrid = new Integer[n][n];
        for (var i = 0; i < blockSize; i++) {
            Arrays.fill(innerGrid[i], null);
        }

        for (int i = 0; i < m; i++) {
            if (i == 2) {
                grids.add(new Pair<>(paddings[i], centeredGrid));
                continue;
            }
            grids.add(new Pair<>(paddings[i],
                    new Grid(Arrays.stream(innerGrid).map(Integer[]::clone).toArray(Integer[][]::new),
                            new HashSet<>(symbols))));
        }

        var multigrid = new MultiGrid(grids);
        multigrid = SudokuSolver.solve(multigrid, true, true, false).getSecond();
        removeRandomCells(multigrid, Math.max(multigrid.getSize().getX(), multigrid.getSize().getY()));
        multigrid.cleanMoves();
        return multigrid;
    }

    /**
     * Generates a random grid with block constraints of size NxM
     *
     * @param blockRows:    The number of block rows
     * @param blockColumns: The number of block columns
     * @return A playable grid
     */
    public static Grid generateSudokuWithBlockConstraints(int blockRows, int blockColumns) throws InterruptedException {
        var solvedGrid = fastSolvedGridCreation(blockRows, blockColumns);
        removeRandomCells(solvedGrid, blockRows * blockColumns);
        solvedGrid.cleanMoves();
        return solvedGrid;
    }

    /**
     * Generates a grid with random block constraints
     *
     * @param lengthInnerGrid: The length of the inner grid
     * @return A playable grid
     */
    public static Grid generateSudokuWithRandomBlockConstraint(int lengthInnerGrid) throws InterruptedException {
        var symbols = SymbolSets.generateSymbols(lengthInnerGrid);
        Vec2i dividers = findDividers(lengthInnerGrid);
        Grid solvedGrid = fastSolvedGridCreation(dividers.getX(), dividers.getY());
        var generalSymbolConstraints = createRandomConstraints(solvedGrid);

        // We update the new constraints
        solvedGrid = new Grid(solvedGrid.getInnerGrid().get(), generalSymbolConstraints, symbols);
        assert solvedGrid.isSolved();
        removeRandomCells(solvedGrid, lengthInnerGrid);
        solvedGrid.cleanMoves();
        return solvedGrid;
    }

    /**
     * Removes random cells from a solved grid to generate a random Sudoku grid to solve
     *
     * @param solvedGrid:      The solved grid
     * @param lengthInnerGrid: The length of the inner grid
     * @return A playable grid
     */
    private static <T extends Solvable<T>> T removeRandomCells(T solvedGrid, int lengthInnerGrid) throws InterruptedException {
//        int nToRemove = (int) Math.sqrt(lengthInnerGrid);
        int nToRemove = lengthInnerGrid / 2;
        boolean canSolve = true;
        Set<Vec2i> emptyCells = new HashSet<>();
        do {
            // if we can't solve, undo n/2 moves and try again
            if (!canSolve) {
                nToRemove = nToRemove / 2;
                for (int i = 0; i < nToRemove; i++) {
                    solvedGrid.undoLastMove(true);
                }
            }

            for (int i = 0; i < nToRemove; i++) {
                Vec2i randomPos;
                do {
                    randomPos = Vec2i.random(lengthInnerGrid, lengthInnerGrid);
                } while (emptyCells.contains(randomPos)
                        && (!(solvedGrid instanceof MultiGrid) || ((MultiGrid) solvedGrid).isInGrid(randomPos)));

                solvedGrid.placeUnchecked(randomPos, null, false, true);
                emptyCells.add(randomPos);
            }
            solvedGrid.computeAllEmptyCellsPossibilities();
            canSolve = SudokuSolver.solve(solvedGrid, true, false, false).getFirst() == SolvingState.SOLVED;
        } while (canSolve || nToRemove > 1);

        //undo the moves until there's only one solution
        do {
            solvedGrid.undoLastMove(true);
        } while (SudokuSolver.hasMoreThanOneSolution(solvedGrid, true, true));

        solvedGrid.cleanMoves();
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

        // Create a list with associate Symbols with all the coordinates containing it
        List<List<Vec2i>> positionList = IntStream.range(0, length)
                                                  .mapToObj(_ -> new ArrayList<Vec2i>())
                                                  .collect(Collectors.toList());
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

        // Swap the positions of the element with same values to create random constraints
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

    public static Grid createExempleSolvedSudoku(int blockRow, int blockColumns) {
        var symbols = SymbolSets.generateSymbols(blockRow * blockColumns);
        var innerGrid = new Integer[blockRow * blockColumns][blockRow * blockColumns];
        for (var i = 0; i < blockRow; i++) {
            Arrays.fill(innerGrid[i], null);
        }
        Grid seedGrid = new Grid(innerGrid, symbols, blockRow, blockColumns);
        List<Integer> symbolsArray = new ArrayList<>(symbols);
        for (var i = 0; i < blockRow; i++) {
            // we place the value in the column i
            for (var j = 0; j < blockColumns * blockRow; j++) {
                seedGrid.placeUnchecked(new Vec2i(i, j), symbolsArray.get(j), false, false);
            }
            // we shift the values in the array
            for (var k = 0; k < blockColumns; k++) {
                symbolsArray.add(symbolsArray.getFirst());
                symbolsArray.removeFirst();
            }
        }

        for (var k = 1; k <= blockRow; k++) {
            symbolsArray.remove(((blockRow - k) * blockColumns));
        }

        for (var i = 0; i < blockRow; i++) {
            for (var j = 0; j < symbolsArray.size(); j++) {
                seedGrid.placeUnchecked(new Vec2i(j + blockRow, i * blockColumns), symbolsArray.get(j), false, false);
            }
            // we shift the values in the array once only (we don't have block constraint here)
            symbolsArray.add(symbolsArray.getFirst());
            symbolsArray.removeFirst();
        }

        seedGrid.computeAllEmptyCellsPossibilities();
        var pair = SudokuSolver.solve(seedGrid, true, true, false);
        var state = pair.getFirst();
        assert state == SolvingState.SOLVED;
        return pair.getSecond();
    }

    /**
     * Try to generate a solved grid faster using previous generated grid. We shuffled row and column, keeping block
     * constraints at the same time.
     *
     * @param blockRows    : the number of rows in a block constraint
     * @param blockColumns : the number of columns in a block constraint
     * @return a solved grid of size blockRows*blockColumns x blockRows*blockColumns
     */
    public static Grid fastSolvedGridCreation(int blockRows, int blockColumns) {
        Path path =
                Path.of("src/main/java/fr/polytech/suuuuuuuuuuudoku/ressources/" + blockColumns * blockRows + "x" + blockColumns * blockRows + "(constraint" + blockRows + "x" + blockColumns + ").csv");
        try {
            // Import the grid of length blockRows*blockColumns if it exists
            Integer[][] gridValue = CsvUtils.importGrid(path);
            var symbols = SymbolSets.generateSymbols(blockRows * blockColumns);
            var listOfIndexRows = new ArrayList<>(IntStream.rangeClosed(0, blockRows - 1).boxed().toList());
            var listOfIndexColumns = new ArrayList<>(IntStream.rangeClosed(0, blockColumns - 1).boxed().toList());
            var grid = new Grid(gridValue, symbols, blockRows, blockColumns);

            // The original grid is not solved, we need to regenerate it
            if (!grid.isSolved()) {
                throw new Exception("The stored grid is not solved, we need to regenerate it");
            }
            // Shuffle block in rows
            Collections.shuffle(listOfIndexRows);
            for (int i = 0; i < blockRows / 2; i++) {
                for (int j = 0; j < blockColumns; j++) {
                    var tmpValue = gridValue[blockColumns * i + j];
                    gridValue[blockColumns * i + j] = gridValue[blockColumns * listOfIndexRows.get(i) + j];
                    gridValue[blockColumns * listOfIndexRows.get(i) + j] = tmpValue;
                }
            }

            // shuffle block in columns
            Collections.shuffle(listOfIndexColumns);
            for (int i = 0; i < blockColumns; i++) {
                for (int j = 0; j < blockRows; j++) {
                    for (int k = 0; k < blockColumns * blockRows; k++) {
                        var tempValue = gridValue[k][blockRows * i + j];
                        gridValue[k][blockRows * i + j] = gridValue[k][blockRows * listOfIndexColumns.get(i) + j];
                        gridValue[k][blockRows * listOfIndexColumns.get(i) + j] = tempValue;
                    }
                }
            }

            // Shuffle rows
            for (int i = 0; i < blockRows; i++) {
                Collections.shuffle(listOfIndexColumns);
                for (int j = 0; j < blockColumns; j++) {
                    var tempValue = gridValue[blockColumns * i + j];
                    gridValue[blockColumns * i + j] = gridValue[blockColumns * i + listOfIndexColumns.get(j)];
                    gridValue[blockColumns * i + listOfIndexColumns.get(j)] = tempValue;
                }
            }

            // Shuffle columns
            for (int i = 0; i < blockColumns; i++) {
                Collections.shuffle(listOfIndexRows);
                for (int j = 0; j < blockRows; j++) {
                    for (int k = 0; k < blockColumns * blockRows; k++) {
                        var tempValue = gridValue[k][blockRows * i + j];
                        gridValue[k][blockRows * i + j] = gridValue[k][blockRows * i + listOfIndexRows.get(j)];
                        gridValue[k][blockRows * i + listOfIndexRows.get(j)] = tempValue;
                    }
                }
            }

            // Shuffle symbols
            var symbolsList = new ArrayList<>(symbols);
            Collections.shuffle(symbolsList);
            Utils.applyMapping(gridValue, gridValue, symbolsList.stream().collect(HashMap::new, (m, v) -> m.put(v,
                    symbolsList.get(v - 1)), HashMap::putAll));

            // Check if the grid is solved to assure the grid is correct
            grid = new Grid(gridValue, symbols, blockRows, blockColumns);
            assert grid.isSolved();
            return grid;
        } catch (Exception e) {
            var grid = createSolvedSudoku(blockRows, blockColumns);
            CsvUtils.exportGrid(path, grid);
            return grid;
        }
    }

    static public void printGrid(Integer[][] grid) {
        for (Integer[] integers : grid) {
            for (int j = 0; j < grid.length; j++) {
                System.out.print(integers[j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}