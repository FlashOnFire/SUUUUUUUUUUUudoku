package fr.polytech.suuuuuuuuuuudoku.graphics;

import fr.polytech.suuuuuuuuuuudoku.Grid;
import fr.polytech.suuuuuuuuuuudoku.constraints.BlockConstraint;

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

        JTable table = new JTable(data, columnNames);
        table.setFont(new Font("Arial", Font.PLAIN, 50));
//        table.setDefaultEditor(Object.class, null); // Make cells non-editable
        table.setCellSelectionEnabled(true);
        table.setGridColor(Color.BLACK);
        table.getTableHeader().setUI(null);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setBounds(50, 50, 900, 900);
        table.setRowHeight(900 / 9);

        for (int i = 0; i < value.length; i++) {
            table.getColumnModel().getColumn(i).setWidth(900 / 9);
            System.arraycopy(value[i], 0, data[i], 0, value[i].length);
            table.getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    setHorizontalAlignment(SwingConstants.CENTER);
                    var constraints = grid.getConstraints();
                    constraints.stream()
                            .filter(constraint -> constraint instanceof BlockConstraint)
                            .map(constraint -> (BlockConstraint) constraint)
                            .filter(blockConstraint -> row >= blockConstraint.getX() && row < blockConstraint.getDx() && column >= blockConstraint.getY() && column < blockConstraint.getDy())
                            .findFirst()
                            .ifPresent(blockConstraint -> c.setBackground(
                                    new Color(constraints.indexOf(blockConstraint) * 123456 % 0x888888 + 0x777777)) // in order to have different light colors
                            );
                    return c;
                }
            });
        }
        table.setShowGrid(true);

        add(table, BorderLayout.CENTER);
    }
}