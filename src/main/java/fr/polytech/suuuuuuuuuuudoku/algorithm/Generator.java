package fr.polytech.suuuuuuuuuuudoku.algorithm;

import fr.polytech.suuuuuuuuuuudoku.constraints.*;
import fr.polytech.suuuuuuuuuuudoku.grid.Grid;
import fr.polytech.suuuuuuuuuuudoku.symbols.SymbolSets;

import java.util.*;
import java.util.stream.IntStream;

public class Generator {
    public static Grid generateClassicNxN(int n) {
        //assert n is perfect square
        assert Math.sqrt(n) == Math.floor(Math.sqrt(n));

        return generateNxM((int) Math.floor(Math.sqrt(n)), (int) Math.floor(Math.sqrt(n)));
    }

    public static Grid generateNxM(int n, int m) {
        var solvedGrid = generateSolvedGrid(n, m);

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
            solvedGrid.placeUnchecked(randomPos, null, true, false);
        } while (!SudokuSolver.hasMoreThanOneSolution(solvedGrid, true, true));

        solvedGrid.placeUnchecked(last_move_pos, last_move_symbol, true, false);
        return solvedGrid;
    }

    public static Grid generateRandomGridN(int length_innerGrid) {
        System.out.println("Generating random grid of size " + length_innerGrid + "x" + length_innerGrid);
        var symbols = SymbolSets.generateSymbols(length_innerGrid);
        var innerGrid = new Integer[length_innerGrid][length_innerGrid];
        for (var i = 0; i < length_innerGrid; i++) {
            Arrays.fill(innerGrid[i], null);
        }

        Vec2i dividers = findDividers(length_innerGrid);
        Grid solvedGrid = generateSolvedGrid(dividers.getX(), dividers.getY());

        List<AbstractConstraint> generalSymbolConstraints = new ArrayList<>();
        for (AbstractConstraint constraint : solvedGrid.getConstraints()) {
            if (constraint instanceof BlockConstraint) {
                var list = new ArrayList<Vec2i>();
                for (int pos_x = ((BlockConstraint) constraint).getBlock().x(); pos_x < ((BlockConstraint) constraint).getBlock().dx(); pos_x++) {
                    for (int pos_y = ((BlockConstraint) constraint).getBlock().y(); pos_y < ((BlockConstraint) constraint).getBlock().dy(); pos_y++) {
                        list.add(new Vec2i(pos_x, pos_y));
                    }
                }
                generalSymbolConstraints.add(new GeneralSymbolConstraint(symbols, list.toArray(Vec2i[]::new)));
            } else {
                generalSymbolConstraints.add(constraint);
            }
        }
        for (int i = 0; i < length_innerGrid * 4; i++) {
            int randomPos = (int) (Math.random() * symbols.size());
            int r1 = (int) (Math.random() * length_innerGrid);
            int r2 = (int) (Math.random() * length_innerGrid);
            Vec2i[] r1_value = ((GeneralSymbolConstraint) generalSymbolConstraints.get(r1)).getPositionList();
            Vec2i[] r2_value = ((GeneralSymbolConstraint) generalSymbolConstraints.get(r2)).getPositionList();

            int value = solvedGrid.getInnerGrid().at(r1_value[randomPos]);
            for (int o = 0; o < r2_value.length; o++) {
                if (solvedGrid.getInnerGrid().at(r2_value[o]) == value) {
                    swapConstraints(r1_value, r2_value, randomPos, o);
                    generalSymbolConstraints.set(r1, new GeneralSymbolConstraint(symbols, r1_value));
                    generalSymbolConstraints.set(r2, new GeneralSymbolConstraint(symbols, r2_value));
                    break;
                }
            }
        }

        solvedGrid = new Grid(solvedGrid.getInnerGrid().get(), generalSymbolConstraints, symbols);
        solvedGrid = SudokuSolver.solve(solvedGrid, true, true, false).getSecond();
        Vec2i last_move_pos;
        Integer last_move_symbol;
        Set<Vec2i> emptyCells = solvedGrid.getEmptyCellsPossibilities().keySet();
        Random random = new Random();
        do {
            Vec2i randomPos;
            do {
                randomPos = new Vec2i(random.nextInt(0, length_innerGrid), random.nextInt(0, length_innerGrid));
            } while (emptyCells.contains(randomPos));

            last_move_pos = randomPos;
            last_move_symbol = solvedGrid.getSymbolAt(randomPos);
            solvedGrid.placeUnchecked(randomPos, null, true, false);
        } while (!SudokuSolver.hasMoreThanOneSolution(solvedGrid, true, true));

        solvedGrid.placeUnchecked(last_move_pos, last_move_symbol, true, false);
        return solvedGrid;
    }

    private static Vec2i findDividers(int lengthInnerGrid) {
        int x = (int) Math.sqrt(lengthInnerGrid);
        int y = lengthInnerGrid / x;
        if (x * y == lengthInnerGrid) {
            return new Vec2i(x, y);
        }

        for (int i = x; i > 0; i--) {
            if (lengthInnerGrid % i == 0) {
                return new Vec2i(i, lengthInnerGrid / i);
            }
        }
        return new Vec2i(1, lengthInnerGrid);
    }

    private static void swapConstraints(Vec2i[] r1Value, Vec2i[] r2Value, int r1_index, int r2_index) {
        Vec2i temp = r1Value[r1_index];
        r1Value[r1_index] = r2Value[r2_index];
        r2Value[r2_index] = temp;
    }

    private static Grid generateSolvedGrid(int n, int m) {
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
            IntStream.range(0, n * m).forEach(i -> finalSeedGrid.placeUnchecked(posArray[i], symbolsArray[i], false, false));
            seedGrid.computeAllEmptyCellsPossibilities();

            var pair = SudokuSolver.solve(seedGrid, true, true, false);
            state = pair.getFirst();
            solvedGrid = pair.getSecond();
        } while (state != SolvingState.SOLVED);
        return solvedGrid;
    }
}
