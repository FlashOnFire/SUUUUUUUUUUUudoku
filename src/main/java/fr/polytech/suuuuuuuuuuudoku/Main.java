package fr.polytech.suuuuuuuuuuudoku;

import fr.polytech.suuuuuuuuuuudoku.constraints.BlockConstraint;
import fr.polytech.suuuuuuuuuuudoku.constraints.ColumnConstraint;
import fr.polytech.suuuuuuuuuuudoku.constraints.LineConstraint;
import fr.polytech.suuuuuuuuuuudoku.solver.SudokuSolver;
import fr.polytech.suuuuuuuuuuudoku.symbols.SymbolSets;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        var grid = new Grid(new Character[][]{
                {' ', '9', ' ', ' ', ' ', '2', ' ', '1', ' '},
                {'2', ' ', '8', ' ', '4', ' ', '9', '3', ' '},
                {'7', ' ', '3', '1', ' ', '6', '8', ' ', ' '},
                {' ', ' ', ' ', '3', ' ', ' ', '1', '4', '5'},
                {'1', '8', '5', ' ', '2', '9', '6', ' ', ' '},
                {' ', '7', '4', ' ', ' ', '1', '2', ' ', '8'},
                {' ', ' ', ' ', '2', ' ', ' ', ' ', '8', ' '},
                {'5', ' ', ' ', '9', ' ', ' ', '7', '6', '2'},
                {'8', ' ', ' ', '6', ' ', '3', ' ', ' ', ' '}
        }, List.of(
                new LineConstraint(SymbolSets.DIGITS),
                new ColumnConstraint(SymbolSets.DIGITS),

                new BlockConstraint(SymbolSets.DIGITS, 0, 0, 3, 3),
                new BlockConstraint(SymbolSets.DIGITS, 3, 0, 6, 3),
                new BlockConstraint(SymbolSets.DIGITS, 6, 0, 9, 3),

                new BlockConstraint(SymbolSets.DIGITS, 0, 3, 3, 3),
                new BlockConstraint(SymbolSets.DIGITS, 3, 3, 6, 3),
                new BlockConstraint(SymbolSets.DIGITS, 6, 3, 9, 3),

                new BlockConstraint(SymbolSets.DIGITS, 0, 6, 3, 9),
                new BlockConstraint(SymbolSets.DIGITS, 3, 6, 6, 9),
                new BlockConstraint(SymbolSets.DIGITS, 6, 6, 9, 9)
        ));

        grid.display();
        System.out.println(grid.areConstraintsSatisfied());
        SudokuSolver.solve(grid, SymbolSets.DIGITS, true);
        var res = new Character[][]{
                {'4', '9', '6', '8', '3', '2', '5', '1', '7'},
                {'2', '1', '8', '7', '4', '5', '9', '3', '6'},
                {'7', '5', '3', '1', '9', '6', '8', '2', '4'},
                {'9', '6', '2', '3', '7', '8', '1', '4', '5'},
                {'1', '8', '5', '4', '2', '9', '6', '7', '3'},
                {'3', '7', '4', '5', '6', '1', '2', '9', '8'},
                {'6', '4', '9', '2', '5', '7', '3', '8', '1'},
                {'5', '3', '1', '9', '8', '4', '7', '6', '2'},
                {'8', '2', '7', '6', '1', '3', '4', '5', '9'}
        };


        grid.display();
        System.out.println(Arrays.deepEquals(grid.getGrid(), res));
    }
}