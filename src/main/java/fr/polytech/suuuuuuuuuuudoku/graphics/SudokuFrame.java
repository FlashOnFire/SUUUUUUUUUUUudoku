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
    private int lengthClassicSudoku = 3;

    private final Runnable solve = () -> {
        if (board.grid.isSolved()) {
            JOptionPane.showMessageDialog(null, "The grid is already solved", "Sudoku", JOptionPane.INFORMATION_MESSAGE);
        } else {
            if (board.alreadySolved) {
                board.grid.setInnerGrid(board.solvedGrid.getInnerGrid());
                board.update(board.solvedGrid.getInnerGrid().get(), true);
            } else {
                board.grid = SudokuSolver.solve(board.grid, true, true).getSecond();
                board.update(board.grid.getInnerGrid().get(), true);
            }
        }
        System.out.println("Solved !");
    };

    private final Runnable reset = () -> {
        board.recoverPreviousSudoku(grid);
        System.out.println("Reset!");
    };

    private final Runnable generate = () -> {
        this.grid = new Grid(Generator.generateClassicNxN(lengthClassicSudoku*lengthClassicSudoku));
        lengthClassicSudoku = (lengthClassicSudoku) % 4 + 1;
        if (lengthClassicSudoku < 2) lengthClassicSudoku = 2;
        board = new SudokuBoard(grid);
        updateJpanel();

        getContentPane().remove(board);
        getContentPane().remove(buttonPanel);
        getContentPane().add(board, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.EAST);

        getContentPane().revalidate();
        System.out.println("Generated!");
    };

    private final Runnable generateRandom = () -> {
        var length = (int) (Math.random() * 6) + 4;
        this.grid = new Grid(Generator.generateRandomGridN(length));
        board = new SudokuBoard(grid);
        updateJpanel();

        getContentPane().remove(board);
        getContentPane().remove(buttonPanel);
        getContentPane().add(board, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.EAST);

        getContentPane().revalidate();
        System.out.println("Generated!");
    };

    private final Runnable generateNxM = () -> {
        var n = (int) (Math.random() * 3) + 2;
        var m = (int) (Math.random() * 3) + 2;
        this.grid = new Grid(Generator.generateNxM(n, m));
        board = new SudokuBoard(grid);
        updateJpanel();

        getContentPane().remove(board);
        getContentPane().remove(buttonPanel);
        getContentPane().add(board, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.EAST);

        getContentPane().revalidate();
        System.out.println("Generated!");
    };

    Runnable hint = () -> {
        if (board.grid.isSolved()) {
            JOptionPane.showMessageDialog(null, "The grid is already solved", "Sudoku", JOptionPane.INFORMATION_MESSAGE);
        } else {
            if (board.alreadySolved) {
                board.grid.setInnerGrid(board.solvedGrid.getInnerGrid());
                board.update(board.solvedGrid.getInnerGrid().get(), true);
            } else {
                board.grid = SudokuSolver.solve(board.grid, true, true).getSecond();
                board.update(board.grid.getInnerGrid().get(), true);
            }
        }
        System.out.println("Hint !");
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

        buttonPanel = new SudokuOptions(background_color, solve, reset, generate, generateRandom, generateNxM, hint);
        getContentPane().add(buttonPanel, BorderLayout.EAST);
    }
}
