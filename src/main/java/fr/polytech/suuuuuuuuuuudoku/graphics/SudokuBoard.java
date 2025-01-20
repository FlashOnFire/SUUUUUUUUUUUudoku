package fr.polytech.suuuuuuuuuuudoku.graphics;

import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec2i;
import fr.polytech.suuuuuuuuuuudoku.constraints.BlockConstraint;
import fr.polytech.suuuuuuuuuuudoku.grid.Grid;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Arrays;
import java.util.stream.IntStream;

public class SudokuBoard extends JPanel {
    Boolean alreadySolved = false;
    Grid solvedGrid;
    Grid grid;
    Grid previousGrid;
    boolean[][] trace;
    JTable table;

    SudokuBoard(Grid grid) {
        final Integer[][][] value = {grid.getGrid().getInner()};

        String[] columnNames = new String[value[0][0].length];
        Arrays.fill(columnNames, "");
        this.previousGrid = grid;
        this.grid = grid;
        Object[][] data = new Object[value[0].length][value[0][0].length];

        table = new JTable(data, columnNames) {
            public boolean isCellEditable(int row, int col) {
                return trace[row][col];
            }
        };
        table.setFont(new Font("Arial", Font.PLAIN, 50));
        table.setGridColor(Color.BLACK);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowHeight(900 / 9);

        trace = new boolean[value[0].length][value[0][0].length];
        for (int i = 0; i < value[0].length; i++) {
            for (int j = 0; j < value[0][i].length; j++) {
                // In order to change the color of the editable cells
                trace[i][j] = value[0][i][j] == null;
            }
        }

        for (int i = 0; i < value[0].length; i++) {
            table.getColumnModel().getColumn(i).setWidth(100);
            System.arraycopy(value[0][i], 0, data[i], 0, value[0][i].length);
            table.getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    setHorizontalAlignment(SwingConstants.CENTER);
                    grid.getConstraints().stream()
                            .filter(BlockConstraint.class::isInstance)
                            .map(BlockConstraint.class::cast)
                            .filter(blockConstraint -> row >= blockConstraint.getX() && row < blockConstraint.getDx() && column >= blockConstraint.getY() && column < blockConstraint.getDy())
                            .findFirst()
                            .ifPresent(blockConstraint -> c.setBackground(new Color((grid.getConstraints().indexOf(blockConstraint) * 123456) % 0x888888 + 0x777777)));
                    if (trace[row][column]) {
                        c.setForeground(new Color(50, 50, 200));
                    } else {
                        c.setForeground(Color.BLACK);
                    }
                    return c;
                }
            });
        }

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int newSize = Math.min(getHeight() / value[0].length, getWidth() / value[0][0].length);
                table.setRowHeight(newSize);
                IntStream.range(0, value[0][0].length).forEach(i -> table.getColumnModel().getColumn(i).setPreferredWidth(newSize));
                table.setFont(table.getFont().deriveFont((float) newSize / 2));
            }
        });

        table.setShowGrid(true);
        add(table);
    }

    public void update(Integer[][] newData, boolean isSolution) {
        for (int i = 0; i < newData.length; i++) {
            for (int j = 0; j < newData[i].length; j++) {
                table.setValueAt(newData[i][j], i, j);
            }
        }

        grid = new Grid(newData, grid.getSymbols());
        solvedGrid = new Grid(grid.getGrid().getInner(), grid.getSymbols());
        alreadySolved = isSolution;
        table.repaint();
    }

    public void recoverPreviousSudoku(Grid grid) {
        var value = grid.getGrid().getInner();
        IntStream.range(0, value.length).forEach(i -> IntStream.range(0, value[i].length).forEach(j -> {
            if (trace[i][j]) {
                value[i][j] = null;
                grid.placeUnchecked(new Vec2i(j, i), null, false);
            }
        }));
        update(value, false);
    }
}
