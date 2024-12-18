package fr.polytech.suuuuuuuuuuudoku.graphics;

import fr.polytech.suuuuuuuuuuudoku.Grid;

import javax.swing.*;
import java.awt.*;

public class SudokuFrame extends JFrame {
    public SudokuFrame(Grid grid) {
        var background_color = Color.WHITE;
        setTitle("Sudoku");
        setSize(1300, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel board = new SudokuBoard(grid);
        JPanel buttonPanel = new SudokuOptions(background_color);

        getContentPane().add(board, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.EAST);
        getContentPane().setBackground(background_color);
    }

}