package fr.polytech.suuuuuuuuuuudoku.graphics;

import fr.polytech.suuuuuuuuuuudoku.algorithm.Generator;
import fr.polytech.suuuuuuuuuuudoku.grid.Grid;
import fr.polytech.suuuuuuuuuuudoku.algorithm.SudokuSolver;

import javax.swing.*;
import java.awt.*;

public class SudokuFrame extends JFrame {
    private SudokuBoard board;
    private final Color background_color;
    private Grid grid;
    private JPanel buttonPanel;

    private final Runnable solve = () -> {
        if (board.grid.isSolved()) {
            JOptionPane.showMessageDialog(null, "The grid is already solved", "Sudoku", JOptionPane.INFORMATION_MESSAGE);
        } else {
            if (board.alreadySolved) {
                board.grid.setGrid(board.solvedGrid.getGrid());
                board.update(board.solvedGrid.getGrid().getInner(), true);
            } else {
                board.grid = SudokuSolver.solve(board.grid, true, true).getSecond();
                board.update(board.grid.getGrid().getInner(), true);
            }
        }
        System.out.println("Solved !");
    };

    private final Runnable reset = () -> {
        board.recoverPreviousSudoku(grid);
        System.out.println("Reset!");
    };

    private final Runnable generate = () -> {

        this.grid = new Grid(Generator.generateClassicNxN(9));
        board = new SudokuBoard(grid);
        updateJpanel();

        getContentPane().remove(board);
        getContentPane().remove(buttonPanel);
        getContentPane().add(board, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.EAST);

        getContentPane().revalidate();
        System.out.println("Generated!");
    };

    public SudokuFrame(Grid grid) {
        background_color = getBackground();
        setTitle("Sudoku");
        setSize(1300, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.grid = grid;
        board = new SudokuBoard(grid);
        updateJpanel();
        getContentPane().add(board, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.EAST);
        getContentPane().setBackground(background_color);
    }

    private void updateJpanel() {
        getContentPane().removeAll();
        getContentPane().setLayout(new BorderLayout());

        buttonPanel = new SudokuOptions(background_color, solve, reset, generate);
        getContentPane().add(buttonPanel, BorderLayout.EAST);
    }
}
