package fr.polytech.suuuuuuuuuuudoku.algorithm;

import fr.polytech.suuuuuuuuuuudoku.CsvUtils;
import fr.polytech.suuuuuuuuuuudoku.constraints.*;
import fr.polytech.suuuuuuuuuuudoku.graphics.Utils;
import fr.polytech.suuuuuuuuuuudoku.grid.Grid;
import fr.polytech.suuuuuuuuuuudoku.grid.MultiGrid;
import fr.polytech.suuuuuuuuuuudoku.grid.Solvable;
import fr.polytech.suuuuuuuuuuudoku.symbols.SymbolSets;
import fr.polytech.suuuuuuuuuuudoku.utils.Difficulty;
import fr.polytech.suuuuuuuuuuudoku.utils.Pair;
import fr.polytech.suuuuuuuuuuudoku.utils.Vec2i;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A class to generate Sudoku grids
 */
public class Generator {
    /**
     * Generates a multigrid with block constraints of size NxM
     *
     * @param difficulty: The difficulty of the generated grid
     * @return A pair of multigrids, the first one is solved and the second one is unsolved
     */
    public static Pair<MultiGrid, MultiGrid> generateMultigridSudoku(Difficulty difficulty) {
        List<Pair<Vec2i, Grid>> grids = new ArrayList<>();
        var blockSize = (int) Math.sqrt(9);
        var offsets = MultiGrid.getRandomOffset();
        var symbols = SymbolSets.generateSymbols(9);

        var centeredGrid = fastSolvedGridCreation(blockSize, blockSize);
        var innerGrid = new Integer[9][9];
        for (var i = 0; i < blockSize; i++) {
            Arrays.fill(innerGrid[i], null);
        }

        for (int i = 0; i < 5; i++) {
            if (i == 2) {
                grids.add(new Pair<>(offsets[i], centeredGrid));
                continue;
            }
            grids.add(new Pair<>(offsets[i],
                    new Grid(Arrays.stream(innerGrid).map(Integer[]::clone).toArray(Integer[][]::new),
                            new HashSet<>(symbols))));
        }

        var multigrid = new MultiGrid(grids);
        multigrid = SudokuSolver.solve(multigrid, true, true, false).getSecond();
        var unsolvedGrid = new MultiGrid(multigrid);
        removeRandomCells(unsolvedGrid, Math.max(multigrid.getSize().getX(), multigrid.getSize().getY()), difficulty);
        multigrid.cleanMoves();
        return new Pair<>(multigrid, unsolvedGrid);
    }

    /**
     * Generates a random grid with block constraints of size NxM
     *
     * @param blockRows:    The number of block rows
     * @param blockColumns: The number of block columns
     * @param difficulty:   The difficulty of the generated grid
     * @return A pair of grid, the first one is solved and the second one is unsolved
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
     * Generates a grid with random block constraints (block can be of any size and any form, even not contiguous)
     *
     * @param difficulty:   The difficulty of the generated grid
     * @param blockRows:    The number of block rows
     * @param blockColumns: The number of block columns
     * @return A pair of grid, the first one is solved and the second one is unsolved
     */
    public static Pair<Grid, Grid> generateSudokuWithRandomBlockConstraint(int blockRows, int blockColumns,
                                                                           Difficulty difficulty) {
        var symbols = SymbolSets.generateSymbols(blockRows * blockColumns);
        Grid solvedGrid = fastSolvedGridCreation(blockRows, blockColumns);
        var generalSymbolConstraints = createRandomConstraints(solvedGrid);

        // We update the new constraints
        solvedGrid = new Grid(solvedGrid.getInnerGrid().get(), generalSymbolConstraints, symbols);
        assert solvedGrid.isSolved();
        var unsolvedGrid = new Grid(solvedGrid);
        removeRandomCells(unsolvedGrid, blockRows * blockColumns, difficulty);
        solvedGrid.cleanMoves();
        return new Pair<>(solvedGrid, unsolvedGrid);
    }

    /**
     * Removes random cells from a solved grid to generate a random Sudoku grid to solve
     *
     * @param solvedGrid:      The solved grid
     * @param lengthInnerGrid: The length of the inner grid
     * @param difficulty:      The difficulty of the generated grid
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
            HashSet<Vec2i> listInConstraint = new HashSet<>();
            for (int j = 0; j < length; j++) {
                int selectedPos = (int) (Math.random() * positionList.get(j).size());
                Vec2i pos = positionList.get(j).get(selectedPos);
                listInConstraint.add(pos);
                positionList.get(i).remove(pos);
            }

            constraints.add(new PositionListConstraint(SymbolSets.generateSymbols(length),
                    listInConstraint));
        }
        constraints.add(new LineConstraint(grid.getSymbols()));
        constraints.add(new ColumnConstraint(grid.getSymbols()));
        constraints.add(new NotEmptyConstraint());
        return constraints;
    }

    /**
     * Generates a random solved grid of size NxM * NxM
     * The grid is generated by solving a grid with random values in the diagonal
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
        assert state == SolvingState.SOLVED;
        return solvedGrid;
    }

    /**
     * Fastly generates a solved grid of size blockRows*blockColumns x blockRows*blockColumns
     * It uses a pre-solved grid to generate and shuffle the row and columns while keeping it solved
     *
     * @param blockRows    : the number of rows in a block constraint
     * @param blockColumns : the number of columns in a block constraint
     * @return a solved grid of size blockRows*blockColumns x blockRows*blockColumns
     */
    public static Grid fastSolvedGridCreation(int blockRows, int blockColumns) {

        try {
            Path path =
                    Path.of(ClassLoader.getSystemResource("presolved/" + blockColumns * blockRows + "x" + blockColumns * blockRows + "(constraint" + blockRows + "x" + blockColumns + ").csv").getFile());
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

            if (!ClassLoader.getSystemResource("exemples").getProtocol().equals("jar")) {
                Path path =
                        Path.of("src/main/resources/presolved/" + blockColumns * blockRows + "x" + blockColumns * blockRows + "(constraint" + blockRows + "x" + blockColumns + ").csv");
                CsvUtils.exportGrid(path, grid);
            }
            return grid;
        }
    }
}