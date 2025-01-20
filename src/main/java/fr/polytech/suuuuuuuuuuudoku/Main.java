package fr.polytech.suuuuuuuuuuudoku;

import fr.polytech.suuuuuuuuuuudoku.algorithm.Generator;
import fr.polytech.suuuuuuuuuuudoku.graphics.SudokuFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        var grid = Generator.generateClassicNxN(16);

        SwingUtilities.invokeLater(() ->
        {
            SudokuFrame frame = new SudokuFrame(grid);
            frame.setVisible(true);
        });
    }
}