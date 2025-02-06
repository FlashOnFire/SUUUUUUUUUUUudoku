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
     * Generates a multigrid with block constraints of size NxM
     *
     * @param n: The size of the sub grid
     * @param m: The number of sub grids
     * @return A playable multigrid
     */
    public static MultiGrid generateMultigridSudoku(int n, int m, Difficulty difficulty) {
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
        removeRandomCells(multigrid, Math.max(multigrid.getSize().getX(), multigrid.getSize().getY()), difficulty);
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
    public static Grid generateSudokuWithBlockConstraints(int blockRows, int blockColumns, Difficulty difficulty) {
        var solvedGrid = fastSolvedGridCreation(blockRows, blockColumns);
        removeRandomCells(solvedGrid, blockRows * blockColumns, difficulty);
        solvedGrid.cleanMoves();
        return solvedGrid;
    }

    /**
     * Generates a grid with random block constraints
     *
     * @param lengthInnerGrid: The length of the inner grid
     * @return A playable grid
     */
    public static Grid generateSudokuWithRandomBlockConstraint(int lengthInnerGrid, Difficulty difficulty) {
        var symbols = SymbolSets.generateSymbols(lengthInnerGrid);
        Vec2i dividers = findDividers(lengthInnerGrid);
        Grid solvedGrid = fastSolvedGridCreation(dividers.getX(), dividers.getY());
        var generalSymbolConstraints = createRandomConstraints(solvedGrid);

        // We update the new constraints
        solvedGrid = new Grid(solvedGrid.getInnerGrid().get(), generalSymbolConstraints, symbols);
        assert solvedGrid.isSolved();
        removeRandomCells(solvedGrid, lengthInnerGrid, difficulty);
        solvedGrid.cleanMoves();
        return solvedGrid;
    }

    /**
     * Removes random cells from a solved grid to generate a random Sudoku grid to solve
     *
     * @param solvedGrid:      The solved grid
     * @param lengthInnerGrid: The length of the inner grid
     */
    private static <T extends Solvable<T>> void removeRandomCells(T solvedGrid, int lengthInnerGrid,
                                                                  Difficulty difficulty) {
        List<Vec2i> toTestRemove = new ArrayList<>();
        int difficultyValue = difficulty.getValue();
        for (int i = 0; i < lengthInnerGrid; i++) {
            for (int j = 0; j < lengthInnerGrid; j++) {
                Vec2i pos = new Vec2i(i, j);
                if (!(solvedGrid instanceof MultiGrid) || ((MultiGrid) solvedGrid).isInGrid(pos)) {
                    toTestRemove.add(new Vec2i(i, j));
                }
            }
        }
        Collections.shuffle(toTestRemove);
        //keep only lengthInnerGrid * lengthInnerGrid / difficultyValue cells
        toTestRemove = toTestRemove.subList(0, toTestRemove.size() / (Difficulty.getValues().length - difficultyValue));

        do {
            solvedGrid.placeUnchecked(toTestRemove.removeFirst(), null, false, true);
            solvedGrid.computeAllEmptyCellsPossibilities();
            if (SudokuSolver.solve(solvedGrid, true, false, false).getFirst() != SolvingState.SOLVED) {
                solvedGrid.undoLastMove(true);
            }
        } while (!toTestRemove.isEmpty());

        //undo the moves until there's only one solution
        do {
            solvedGrid.undoLastMove(true);
        } while (SudokuSolver.hasMoreThanOneSolution(solvedGrid, true, true));

        solvedGrid.cleanMoves();
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
                Path.of(ClassLoader.getSystemResource("presolved/" + blockColumns * blockRows + "x" + blockColumns * blockRows + "(constraint" + blockRows + "x" + blockColumns + ").csv").getFile());
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
}