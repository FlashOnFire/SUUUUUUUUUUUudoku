package fr.polytech.suuuuuuuuuuudoku;

import fr.polytech.suuuuuuuuuuudoku.constraints.BlockConstraint;
import fr.polytech.suuuuuuuuuuudoku.constraints.ColumnConstraint;
import fr.polytech.suuuuuuuuuuudoku.constraints.LineConstraint;
import fr.polytech.suuuuuuuuuuudoku.constraints.NotEmptyConstraint;
import fr.polytech.suuuuuuuuuuudoku.graphics.SudokuFrame;
import fr.polytech.suuuuuuuuuuudoku.grid.Grid;
import fr.polytech.suuuuuuuuuuudoku.solver.SudokuSolver;
import fr.polytech.suuuuuuuuuuudoku.symbols.SymbolSets;

import javax.swing.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        var grid = new Grid(new String[][]{
                {"5", " ", " ", "9", "2", " ", " ", "7", " "},
                {" ", " ", " ", " ", " ", "1", " ", " ", " "},
                {"1", " ", " ", " ", " ", " ", " ", "5", "6"},
                {"4", "5", "2", " ", "9", " ", " ", " ", " "},
                {"7", " ", " ", "3", " ", "2", " ", " ", " "},
                {" ", " ", "9", "5", "6", " ", " ", " ", " "},
                {" ", " ", " ", " ", "8", " ", "1", " ", "2"},
                {" ", "6", " ", "7", " ", " ", " ", " ", " "},
                {" ", " ", " ", " ", " ", " ", " ", " ", "4"}
        }, List.of(
                new LineConstraint(SymbolSets.DIGITS),
                new ColumnConstraint(SymbolSets.DIGITS),

                new BlockConstraint(SymbolSets.DIGITS, 0, 0, 3, 3),
                new BlockConstraint(SymbolSets.DIGITS, 3, 0, 6, 3),
                new BlockConstraint(SymbolSets.DIGITS, 6, 0, 9, 3),

                new BlockConstraint(SymbolSets.DIGITS, 0, 3, 3, 6),
                new BlockConstraint(SymbolSets.DIGITS, 3, 3, 6, 6),
                new BlockConstraint(SymbolSets.DIGITS, 6, 3, 9, 6),

                new BlockConstraint(SymbolSets.DIGITS, 0, 6, 3, 9),
                new BlockConstraint(SymbolSets.DIGITS, 3, 6, 6, 9),
                new BlockConstraint(SymbolSets.DIGITS, 6, 6, 9, 9),

                new NotEmptyConstraint()
        ), SymbolSets.DIGITS);

        SwingUtilities.invokeLater(() ->
        {
            SudokuFrame frame = new SudokuFrame(grid);
            frame.setVisible(true);
        });

        /*
        var start = System.currentTimeMillis();
        System.out.println(SudokuSolver.solve(grid, true, true));
        var end = System.currentTimeMillis();

        System.out.println("Time: " + (end - start) + "ms");
        grid.display();
        */
    }
}