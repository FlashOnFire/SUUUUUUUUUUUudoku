package fr.polytech.suuuuuuuuuuudoku;

import fr.polytech.suuuuuuuuuuudoku.graphics.SudokuFrame;

import javax.swing.*;

import static fr.polytech.suuuuuuuuuuudoku.algorithm.Generator.generateRandomGridN;

public class Main {
    public static void main(String[] args) {

        var grid = generateRandomGridN(6);

        SwingUtilities.invokeLater(() ->
        {
            SudokuFrame frame = new SudokuFrame(grid);
            frame.setVisible(true);
        });
    }
}