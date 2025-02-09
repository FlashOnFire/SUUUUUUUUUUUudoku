package fr.polytech.suuuuuuuuuuudoku.algorithm;

import fr.polytech.suuuuuuuuuuudoku.constraints.*;
import fr.polytech.suuuuuuuuuuudoku.graphics.Utils;
import fr.polytech.suuuuuuuuuuudoku.grid.Grid;
import fr.polytech.suuuuuuuuuuudoku.grid.MultiGrid;
import fr.polytech.suuuuuuuuuuudoku.grid.Solvable;
import fr.polytech.suuuuuuuuuuudoku.grid.SymbolSets;
import fr.polytech.suuuuuuuuuuudoku.utils.CsvUtils;
import fr.polytech.suuuuuuuuuuudoku.utils.Difficulty;
import fr.polytech.suuuuuuuuuuudoku.utils.Pair;
import fr.polytech.suuuuuuuuuuudoku.utils.Vec2i;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A class to generate Sudoku grids
 */
public class Generator {

    /**
     * Private constructor to prevent instantiation of the Generator class.
     */
    private Generator() {
    }

    /**
     * Generates a multi-grid Sudoku with block constraints of size NxM.
     * This method creates a multi-grid Sudoku puzzle by first generating a fully solved grid
     * and then removing a certain number of values based on the specified difficulty level.
     * The resulting pair consists of:
     * - A solved multi-grid.
     * - An unsolved multi-grid with some cells removed to create the puzzle.
     *
     * @param difficulty: The difficulty of the generated grid
     * @return A pair of multigrids (solved and unsolved)
     */
    public static Pair<MultiGrid, MultiGrid> generateMultigridSudoku(Difficulty difficulty) {
        List<Pair<Vec2i, Grid>> gridList = new ArrayList<>();
        var blockSize = (int) Math.sqrt(9);
        var gridOffsets = MultiGrid.getRandomOffset();
        var symbolSet = SymbolSets.generateSymbols(9);

        var baseGrid = fastSolvedGridCreation(blockSize, blockSize);
        var emptyGrid = new Integer[9][9];
        for (var i = 0; i < blockSize; i++) {
            Arrays.fill(emptyGrid[i], null);
        }

        for (int i = 0; i < 5; i++) {
            if (i == 2) {
                gridList.add(new Pair<>(gridOffsets[i], baseGrid));
                continue;
            }
            gridList.add(new Pair<>(gridOffsets[i],
                    new Grid(Arrays.stream(emptyGrid).map(Integer[]::clone).toArray(Integer[][]::new),
                            new HashSet<>(symbolSet))));
        }

        var solvedMultiGrid = new MultiGrid(gridList);
        solvedMultiGrid = SudokuSolver.solve(solvedMultiGrid, true, true, false).second();
        var unsolvedMultiGrid = new MultiGrid(solvedMultiGrid);
        removeRandomCells(unsolvedMultiGrid, Math.max(solvedMultiGrid.getSize().getX(),
                solvedMultiGrid.getSize().getY()), difficulty);
        solvedMultiGrid.cleanMoves();
        return new Pair<>(solvedMultiGrid, unsolvedMultiGrid);
    }

    /**
     * Generates a random Sudoku grid with block constraints of size NxM.
     * This method creates a fully solved Sudoku grid that adheres to the specified block constraints
     * (where each block contains NxM cells). It then removes values from the grid based on the chosen
     * difficulty level to generate a playable puzzle.
     *
     * @param blockRows:    The number of rows in each block
     * @param blockColumns: The number of columns in each block
     * @param difficulty:   The difficulty of the generated grid
     * @return A pair of grid (solved and unsolved)
     */
    public static Pair<Grid, Grid> generateSudokuWithBlockConstraints(int blockRows, int blockColumns,
                                                                      Difficulty difficulty) {
        var solvedGrid = fastSolvedGridCreation(blockRows, blockColumns);
        var unsolvedGrid = new Grid(solvedGrid);
        removeRandomCells(unsolvedGrid, blockRows * blockColumns, difficulty);
        solvedGrid.cleanMoves();
        return new Pair<>(solvedGrid, unsolvedGrid);
    }

    /**
     * Generates a Sudoku grid with randomly shaped block constraints.
     * Unlike traditional Sudoku, where blocks are regular (NxM), this method allows blocks to have
     * arbitrary shapes, including non-contiguous regions. It first generates a fully solved grid
     * and applies random constraints to define unique block formations. Values are then removed
     * based on the chosen difficulty to create a playable puzzle.
     *
     * @param difficulty:   The difficulty of the generated grid
     * @param blockRows:    The number of rows in each block
     * @param blockColumns: The number of columns in each block
     * @return A pair of grid (solved and unsolved)
     */
    public static Pair<Grid, Grid> generateSudokuWithRandomBlockConstraint(int blockRows, int blockColumns,
                                                                           Difficulty difficulty) {
        var symbolSet = SymbolSets.generateSymbols(blockRows * blockColumns);
        Grid solvedGrid = fastSolvedGridCreation(blockRows, blockColumns);
        var constraints = createRandomConstraints(solvedGrid);

        solvedGrid = new Grid(solvedGrid.getInnerGrid().get(), constraints, symbolSet);
        assert solvedGrid.isSolved();
        var unsolvedGrid = new Grid(solvedGrid);
        removeRandomCells(unsolvedGrid, blockRows * blockColumns, difficulty);
        solvedGrid.cleanMoves();
        return new Pair<>(solvedGrid, unsolvedGrid);
    }

    /**
     * Removes random cells from a solved Sudoku grid to generate a puzzle with a unique solution.
     * This method starts with a fully solved grid and removes values randomly while ensuring that
     * the resulting grid remains solvable. It follows a dichotomic search strategy to maintain
     * difficulty consistency and guarantee a unique solution.
     *
     * @param solvedGrid  The solved Sudoku grid.
     * @param gridSize    The size of the grid (length of the inner grid).
     * @param difficulty  The difficulty level of the generated puzzle.
     */
    private static <T extends Solvable<T>> void removeRandomCells(T solvedGrid, int gridSize,
                                                                  Difficulty difficulty) {
        List<Vec2i> removableCells = new ArrayList<>();
        int difficultyValue = difficulty.getValue();
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                Vec2i position = new Vec2i(i, j);
                if (!(solvedGrid instanceof MultiGrid) || solvedGrid.isInGrid(position)) {
                    removableCells.add(new Vec2i(i, j));
                }
            }
        }
        Collections.shuffle(removableCells);
        //keep only gridSize * gridSize / difficultyValue cells
        removableCells = removableCells.subList(0,
                removableCells.size() / (Difficulty.getValues().length - difficultyValue));

        // The number of cell to remove at each iteration
        // This is divided by 2 at each iteration when the grid is not solvable to follow a dichotomy search strategy
        int nCellToRemove = Math.min((int) Math.sqrt(gridSize), removableCells.size());

        do {
            for (int i = 0; i < nCellToRemove && i < removableCells.size(); i++) {
                solvedGrid.placeUnchecked(removableCells.removeFirst(), null, false, true);
            }
            solvedGrid.computeAllEmptyCellsPossibilities();
            if (SudokuSolver.solve(solvedGrid, true, false, false).first() != SolvingState.SOLVED) {
                if (nCellToRemove > 1) {
                    for (int i = 0; i < nCellToRemove; i++) {
                        removableCells.add(solvedGrid.getMoves().getLast().position());
                        solvedGrid.undoLastMove(true);
                    }
                    nCellToRemove = Math.max(nCellToRemove / 2, 1);
                } else {
                    solvedGrid.undoLastMove(true);
                }
            }
        } while (!removableCells.isEmpty());

        // Undo the moves until there is only one solution
        do {
            solvedGrid.undoLastMove(true);
        } while (SudokuSolver.hasMoreThanOneSolution(solvedGrid, true, true));

        solvedGrid.cleanMoves();
    }

    /**
     * Generates a set of random constraints for a solved Sudoku grid.
     * This method takes a fully solved Sudoku grid and randomly rearranges the symbol positions
     * to generate constraints that break the traditional NxM block structure.
     *
     * @param solvedGrid  A fully solved Sudoku grid.
     * @return A list of randomly generated constraints.
     */
    private static List<AbstractConstraint> createRandomConstraints(Grid solvedGrid) {
        assert solvedGrid.isSolved();
        int gridSize = solvedGrid.length();

        // Create a list with associate Symbols with all the coordinates containing it
        List<List<Vec2i>> symbolPositions = IntStream.range(0, gridSize)
                                                     .mapToObj(_ -> new ArrayList<Vec2i>())
                                                     .collect(Collectors.toList());

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                Integer symbol = solvedGrid.getSymbolAt(row, col);
                // grid is solved, so we can use getSymbolAt without checking if it's null
                symbolPositions.get(symbol - 1).add(new Vec2i(row, col));
            }
        }

        // Swap the positions of the element with same values to create random constraints
        List<AbstractConstraint> constraints = new ArrayList<>();
        for (int i = 0; i < gridSize; i++) {
            HashSet<Vec2i> constraintPositions = new HashSet<>();
            for (int j = 0; j < gridSize; j++) {
                int randomIndex = (int) (Math.random() * symbolPositions.get(j).size());
                Vec2i position = symbolPositions.get(j).remove(randomIndex);
                constraintPositions.add(position);
            }

            constraints.add(new PositionSetConstraint(SymbolSets.generateSymbols(gridSize),
                    constraintPositions));
        }
        constraints.add(new LineConstraint(solvedGrid.getSymbols()));
        constraints.add(new ColumnConstraint(solvedGrid.getSymbols()));
        constraints.add(new NotEmptyConstraint());
        return constraints;
    }

    /**
     * Generates a fully solved Sudoku grid of size (NxM)².
     * This method creates an empty Sudoku grid, places random values along the diagonal to seed
     * the solving process, and then uses a backtracking solver to complete the grid.
     *
     * @param blockRows    The number of rows in each block.
     * @param blockColumns The number of columns in each block.
     * @return A fully solved Sudoku grid.
     */
    private static Grid createSolvedSudoku(int blockRows, int blockColumns) {
        var symbols = SymbolSets.generateSymbols(blockRows * blockColumns);
        var gridValues = new Integer[blockRows * blockColumns][blockRows * blockColumns];
        for (var row = 0; row < blockRows; row++) {
            Arrays.fill(gridValues[row], null);
        }

        Grid seedGrid = new Grid(gridValues, symbols, blockRows, blockColumns);
        var shuffledSymbols = new ArrayList<>(symbols);
        Collections.shuffle(shuffledSymbols);

        for (var i = 0; i < blockColumns * blockRows; i++) {
            seedGrid.placeUnchecked(new Vec2i(i, i), shuffledSymbols.get(i % shuffledSymbols.size()), false, false);
        }

        seedGrid.computeAllEmptyCellsPossibilities();
        var solvedPair = SudokuSolver.solve(seedGrid, true, true, false);
        assert solvedPair.first() == SolvingState.SOLVED;
        return solvedPair.second();
    }

    /**
     * Quickly generates a solved Sudoku grid of size (blockRows × blockColumns)².
     * This method loads a pre-solved grid from a CSV file, applies row and column shuffling
     * while preserving the solution, and then randomizes the symbol mapping. If the stored grid
     * is not correctly solved or unavailable, a new one is generated from scratch.
     *
     * @param blockRows    The number of rows in each block.
     * @param blockColumns The number of columns in each block.
     * @return A fully solved Sudoku grid.
     */
    public static Grid fastSolvedGridCreation(int blockRows, int blockColumns) {
        try {
            Integer[][] gridValues =
                    CsvUtils.importGrid("presolved/" + blockColumns * blockRows + "x" + blockColumns * blockRows +
                            "(constraint" + blockRows + "x" + blockColumns + ").csv");

            var symbols = SymbolSets.generateSymbols(blockRows * blockColumns);

            Grid grid = new Grid(gridValues, symbols, blockRows, blockColumns);
            if (!grid.isSolved()) {
                throw new Exception("The stored grid is not solved, we need to regenerate it");
            }
            // We shuffle the grid to make it more random, but we keep it solved
            shuffleGrid(blockRows, blockColumns, gridValues);

            // Shuffle symbols
            var symbolsList = new ArrayList<>(symbols);
            Collections.shuffle(symbolsList);
            Utils.applyMapping(gridValues, gridValues, symbolsList.stream().collect(HashMap::new, (m, v) -> m.put(v,
                    symbolsList.get(v - 1)), HashMap::putAll));

            // Check if the grid is solved to assure the grid is correct
            grid = new Grid(gridValues, symbols, blockRows, blockColumns);
            assert grid.isSolved();
            return grid;

        } catch (Exception e) {
            return createSolvedSudoku(blockRows, blockColumns);
        }
    }

    /**
     * Shuffles a solved grid while keeping it valid.
     * This method randomizes the grid by swapping:
     *  - Entire blocks within rows and columns.
     *  - Rows within block groups.
     *  - Columns within block groups.
     * These transformations maintain the validity of the Sudoku solution.
     *
     * @param blockRows    The number of rows in a block.
     * @param blockColumns The number of columns in a block.
     * @param gridValue   The grid values to shuffle.
     */
    private static void shuffleGrid(int blockRows, int blockColumns, Integer[][] gridValue) {
        var listOfIndexRows = new ArrayList<>(IntStream.rangeClosed(0, blockRows - 1).boxed().toList());
        var listOfIndexColumns = new ArrayList<>(IntStream.rangeClosed(0, blockColumns - 1).boxed().toList());

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
    }
}