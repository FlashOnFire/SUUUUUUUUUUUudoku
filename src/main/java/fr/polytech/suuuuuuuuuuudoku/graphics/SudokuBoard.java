package fr.polytech.suuuuuuuuuuudoku.graphics;

import fr.polytech.suuuuuuuuuuudoku.Grid;
import fr.polytech.suuuuuuuuuuudoku.constraints.BlockConstraint;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Arrays;

public class SudokuBoard extends JPanel {
    SudokuBoard(Grid grid) {
        var value = grid.getGrid().getInner();

        String[] columnNames = new String[value[0].length];
        Arrays.fill(columnNames, "");

        Object[][] data = new Object[value.length][value[0].length];

        JTable table = new JTable(data, columnNames);
        table.setFont(new Font("Arial", Font.PLAIN, 50));
        table.setCellSelectionEnabled(true);
        table.setGridColor(Color.BLACK);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowHeight(900 / 9);

        for (int i = 0; i < value.length; i++) {
            table.getColumnModel().getColumn(i).setWidth(900 / 9);
            System.arraycopy(value[i], 0, data[i], 0, value[i].length);
            table.getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                               boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    setHorizontalAlignment(SwingConstants.CENTER);
                    grid.getConstraints().stream()
                        .filter(BlockConstraint.class::isInstance)
                        .map(BlockConstraint.class::cast)
                        .filter(blockConstraint -> row >= blockConstraint.getX() && row < blockConstraint.getDx() && column >= blockConstraint.getY() && column < blockConstraint.getDy())
                        .findFirst()
                        .ifPresent(blockConstraint -> c.setBackground(
                                new Color(grid.getConstraints().indexOf(blockConstraint) * 123456 % 0x888888 + 0x777777)
                        ));
                    return c;
                }
            });
        }

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int newSize = Math.min(getHeight() / value.length, getWidth() / value[0].length);
                table.setRowHeight(newSize);
                for (int i = 0; i < value[0].length; i++) {
                    table.getColumnModel().getColumn(i).setPreferredWidth(newSize);
                }
                table.setFont(table.getFont().deriveFont((float) newSize / 2));
            }
        });

        table.setShowGrid(true);
        add(table);
    }
}
