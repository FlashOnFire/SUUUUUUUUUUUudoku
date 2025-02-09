package fr.polytech.suuuuuuuuuuudoku.graphics.swing;

import fr.polytech.suuuuuuuuuuudoku.algorithm.Generator;
import fr.polytech.suuuuuuuuuuudoku.algorithm.SudokuSolver;
import fr.polytech.suuuuuuuuuuudoku.grid.Grid;
import fr.polytech.suuuuuuuuuuudoku.utils.Difficulty;

import javax.swing.*;
import java.awt.*;

/**
 * The SudokuFrame class represents the main frame for the Sudoku game.
 */
public class SudokuFrame extends JFrame {
    /**
     * The background color of the frame.
     */
    private final Color background_color;

    /**
     * The Sudoku board displayed in the frame.
     */
    private SudokuBoard board;

    /**
     * The solve action for solving the Sudoku puzzle.
     */
    private final Runnable solve = () -> {
        if (board.grid.isSolved()) {
            JOptionPane.showMessageDialog(null, "The grid is already solved", "Sudoku",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            if (board.alreadySolved) {
                board.grid.setInnerGrid(board.solvedGrid.getInnerGrid());
                board.update(board.solvedGrid.getInnerGrid().get(), true);
            } else {
                board.grid = SudokuSolver.solve(board.grid, true, true, true).second();
                board.update(board.grid.getInnerGrid().get(), true);
            }
        }
    };

    /**
     * The hint action for providing a hint for the Sudoku puzzle.
     */
    final Runnable hint = () -> {
        if (board.grid.isSolved()) {
            JOptionPane.showMessageDialog(null, "The grid is already solved", "Sudoku",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            if (board.alreadySolved) {
                board.grid.setInnerGrid(board.solvedGrid.getInnerGrid());
                board.update(board.solvedGrid.getInnerGrid().get(), true);
            } else {
                board.grid = SudokuSolver.solve(board.grid, true, true, true).second();
                board.update(board.grid.getInnerGrid().get(), true);
            }
        }
    };

    /**
     * The initial grid for the Sudoku puzzle.
     */
    private Grid grid;

    /**
     * The reset action for resetting the Sudoku puzzle.
     */
    private final Runnable reset = () -> board.recoverPreviousSudoku(grid);

    /**
     * The panel containing the buttons for Sudoku options.
     */
    private JPanel buttonPanel;

    /**
     * The length of the classic Sudoku.
     */
    private int lengthClassicSudoku = 3;

    /**
     * Constructs a new SudokuFrame with the specified grid.
     *
     * @param grid the initial grid for the Sudoku puzzle
     */
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

    /**
     * The main method to start the Sudoku game.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Grid grid = Generator.generateSudokuWithBlockConstraints(4, 4, Difficulty.EXPERT).second();

        SwingUtilities.invokeLater(() ->
        {
            SudokuFrame frame = new SudokuFrame(grid);
            frame.setVisible(true);
        });
    }

    /**
     * Updates the JPanel with the current Sudoku options.
     */
    private void updateJpanel() {
        getContentPane().removeAll();
        getContentPane().setLayout(new BorderLayout());

        buttonPanel = new SudokuOptions(background_color, solve, reset, generate, generateRandom, generateNxM, hint);
        getContentPane().add(buttonPanel, BorderLayout.EAST);
    }

    /**
     * The generate action for generating a new Sudoku puzzle.
     */
    private final Runnable generate = () -> {
        this.grid = new Grid(Generator.generateSudokuWithBlockConstraints(lengthClassicSudoku, lengthClassicSudoku,
                Difficulty.EXPERT).second());
        lengthClassicSudoku = (lengthClassicSudoku) % 4 + 1;
        if (lengthClassicSudoku < 2) lengthClassicSudoku = 2;
        board = new SudokuBoard(grid);
        updateJpanel();

        getContentPane().remove(board);
        getContentPane().remove(buttonPanel);
        getContentPane().add(board, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.EAST);

        getContentPane().revalidate();
    };

    /**
     * The generateRandom action for generating a new Sudoku puzzle with random block constraints.
     */
    private final Runnable generateRandom = () -> {
        var length = (int) (Math.random() * 3) + 2;
        var length2 = (int) (Math.random() * 3) + 2;
        this.grid =
                new Grid(Generator.generateSudokuWithRandomBlockConstraint(length, length2, Difficulty.EXPERT).second());
        board = new SudokuBoard(grid);
        updateJpanel();

        getContentPane().remove(board);
        getContentPane().remove(buttonPanel);
        getContentPane().add(board, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.EAST);

        getContentPane().revalidate();
    };

    /**
     * The generateNxM action for generating a new Sudoku puzzle with NxM block constraints.
     */
    private final Runnable generateNxM = () -> {
        var n = (int) (Math.random() * 3) + 2;
        var m = (int) (Math.random() * 3) + 2;

        this.grid = new Grid(Generator.generateSudokuWithBlockConstraints(n, m, Difficulty.EXPERT).second());
        board = new SudokuBoard(grid);
        updateJpanel();

        getContentPane().remove(board);
        getContentPane().remove(buttonPanel);
        getContentPane().add(board, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.EAST);

        getContentPane().revalidate();
    };
}
