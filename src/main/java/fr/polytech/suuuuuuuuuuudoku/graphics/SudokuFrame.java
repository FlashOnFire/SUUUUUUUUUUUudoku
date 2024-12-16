package fr.polytech.suuuuuuuuuuudoku.graphics;

import fr.polytech.suuuuuuuuuuudoku.Grid;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Arrays;

public class SudokuFrame extends JFrame {
    public SudokuFrame(Grid grid) {
        setTitle("Sudoku");
        setSize(1000, 1000);
        setLayout(null);
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
        table.getTableHeader().setUI(null);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setBounds(50, 50, 900, 900);
        table.setRowHeight(900/9);
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < value.length; i++) {
            table.getColumnModel().getColumn(i).setWidth(900/9);
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
        table.setFont(new Font("Arial", Font.PLAIN, 50));
//        table.setDefaultEditor(Object.class, null); // Make cells non-editable
        table.setCellSelectionEnabled(true);
        table.setGridColor(Color.BLACK);
        table.setShowGrid(true);

        add(table, BorderLayout.CENTER);
    }
}