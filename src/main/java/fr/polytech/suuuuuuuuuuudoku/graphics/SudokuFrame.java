package fr.polytech.suuuuuuuuuuudoku.graphics;
import fr.polytech.suuuuuuuuuuudoku.Grid;

import javax.swing.*;
import java.awt.*;

public class SudokuFrame extends JFrame {
    public SudokuFrame(Grid grid) {
        setTitle("Sudoku");
        setSize(1000, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        var value = grid.getGrid();

        JTextField[][] textFields = new JTextField[value.length][value[0].length];
        JPanel panel = new JPanel(new GridLayout(value.length, value[0].length));

        for (int i = 0; i < value.length; i++) {
            for (int j = 0; j < value[i].length; j++) {
                textFields[i][j] = new JTextField(String.valueOf(value[i][j]));
                textFields[i][j].setHorizontalAlignment(JTextField.CENTER);
                textFields[i][j].setFont(new Font("Arial", Font.PLAIN, 20));
                panel.add(textFields[i][j]);
            }
        }

        add(panel, BorderLayout.CENTER);
    }
}