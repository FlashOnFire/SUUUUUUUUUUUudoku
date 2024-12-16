package fr.polytech.suuuuuuuuuuudoku.graphics;
import javax.swing.*;
import java.awt.*;

public class SudokuFrame extends JFrame {
    public SudokuFrame(Character[][] grid) {
        setTitle("Sudoku");
        setSize(1000, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField[][] textFields = new JTextField[grid.length][grid[0].length];
        JPanel panel = new JPanel(new GridLayout(grid.length, grid[0].length));

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                textFields[i][j] = new JTextField(String.valueOf(grid[i][j]));
                textFields[i][j].setHorizontalAlignment(JTextField.CENTER);
                textFields[i][j].setFont(new Font("Arial", Font.PLAIN, 20));
                panel.add(textFields[i][j]);
            }
        }

        add(panel, BorderLayout.CENTER);
    }
}