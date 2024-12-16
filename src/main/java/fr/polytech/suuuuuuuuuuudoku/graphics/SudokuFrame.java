package fr.polytech.suuuuuuuuuuudoku.graphics;
import fr.polytech.suuuuuuuuuuudoku.Grid;

import javax.swing.*;
import java.awt.*;


import fr.polytech.suuuuuuuuuuudoku.Grid;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class SudokuFrame extends JFrame {
    public SudokuFrame(Grid grid) {
        setTitle("Sudoku");
        setSize(1000, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        var value = grid.getGrid();

        String[] columnNames = new String[value[0].length];
        Arrays.fill(columnNames, "");

        Object[][] data = new Object[value.length][value[0].length];
        for (int i = 0; i < value.length; i++) {
            for (int j = 0; j < value[i].length; j++) {
                data[i][j] = value[i][j];
            }
        }

        JTable table = new JTable(data, columnNames);
        table.setRowHeight(50);
        for (int i = 0; i < value.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(50);
        }
        table.setFont(new Font("Arial", Font.PLAIN, 20));
//        table.setDefaultEditor(Object.class, null); // Make cells non-editable
        table.setCellSelectionEnabled(true);
        table.setGridColor(Color.BLACK);
        table.setShowGrid(true);
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }
}