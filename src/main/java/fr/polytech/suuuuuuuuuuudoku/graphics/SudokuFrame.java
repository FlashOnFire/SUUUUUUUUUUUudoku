package fr.polytech.suuuuuuuuuuudoku.graphics;

import fr.polytech.suuuuuuuuuuudoku.grid.Grid;
import fr.polytech.suuuuuuuuuuudoku.solver.SudokuSolver;

import javax.swing.*;
import java.awt.*;

public class SudokuFrame extends JFrame {
    public SudokuFrame(Grid grid) {
        var background_color = getBackground();
        setTitle("Sudoku");
        setSize(1300, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SudokuBoard board = new SudokuBoard(grid);

        JPanel buttonPanel = new SudokuOptions(background_color, () -> {
            System.out.println("Solving...");

            grid.getGrid().display();

            if (grid.isSolved()) {
                JOptionPane.showMessageDialog(null, "The grid is already solved", "Sudoku", JOptionPane.INFORMATION_MESSAGE);
            } else {
                SudokuSolver.solve(grid, true, true);
                board.update(grid.getGrid().getInner());
            }
        },
                () -> {
                    System.out.println("Resetting...");
                    board.recoverPreviousSudoku(grid);
                    grid.display();
                });

        getContentPane().add(board, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.EAST);
        getContentPane().setBackground(background_color);
    }

}