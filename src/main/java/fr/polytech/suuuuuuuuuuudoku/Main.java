package fr.polytech.suuuuuuuuuuudoku;

import fr.polytech.suuuuuuuuuuudoku.algorithm.Generator;
import fr.polytech.suuuuuuuuuuudoku.graphics.SudokuFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        /*var grid = new Grid(new String[][]{
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
                new BlockConstraint(SymbolSets.DIGITS, 3, 0, 3, 3),
                new BlockConstraint(SymbolSets.DIGITS, 6, 0, 3, 3),

                new BlockConstraint(SymbolSets.DIGITS, 0, 3, 3, 3),
                new BlockConstraint(SymbolSets.DIGITS, 3, 3, 3, 3),
                new BlockConstraint(SymbolSets.DIGITS, 6, 3, 3, 3),

                new BlockConstraint(SymbolSets.DIGITS, 0, 6, 3, 3),
                new BlockConstraint(SymbolSets.DIGITS, 3, 6, 3, 3),
                new BlockConstraint(SymbolSets.DIGITS, 6, 6, 3, 3),

                new NotEmptyConstraint()
        ), SymbolSets.DIGITS);*/

        var grid = Generator.generateClassicNxN(9);

        SwingUtilities.invokeLater(() ->
        {
            SudokuFrame frame = new SudokuFrame(grid);
            frame.setVisible(true);
        });


    }
}