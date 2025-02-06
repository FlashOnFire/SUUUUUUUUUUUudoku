package fr.polytech.suuuuuuuuuuudoku.graphics;

import fr.polytech.suuuuuuuuuuudoku.constraints.BlockConstraint;
import fr.polytech.suuuuuuuuuuudoku.constraints.PositionListConstraint;
import fr.polytech.suuuuuuuuuuudoku.grid.Grid;
import fr.polytech.suuuuuuuuuuudoku.utils.Vec2i;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * This class represents a graphical Sudoku board using Swing components.
 */
public class SudokuBoard extends JPanel {
    /**
     * The previous grid.
     */
    final Grid previousGrid;
    /**
     * The table representing the Sudoku board.
     */
    final JTable table;
    /**
     * Whether the Sudoku board has already been solved.
     */
    Boolean alreadySolved = false;
    /**
     * The solved grid.
     */
    Grid solvedGrid;
    /**
     * The current grid.
     */
    Grid grid;
    /**
     * The trace of the Sudoku board.
     */
    boolean[][] trace;

    /**
     * Constructs a SudokuBoard with the given grid.
     *
     * @param grid the initial grid to display on the board
     */
    SudokuBoard(Grid grid) {
        final Integer[][][] value = {grid.getInnerGrid().get()};

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
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                               boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    setHorizontalAlignment(SwingConstants.CENTER);
                    grid.getConstraints().stream()
                        .filter(BlockConstraint.class::isInstance)
                        .map(BlockConstraint.class::cast)
                        .filter(blockConstraint -> row >= blockConstraint.getBlock().x() && row < blockConstraint.getBlock().dx() && column >= blockConstraint.getBlock().y() && column < blockConstraint.getBlock().dy())
                        .findFirst()
                        .ifPresent(blockConstraint -> c.setBackground(new Color((grid.getConstraints().indexOf(blockConstraint) * 1234567) % 0x888888 + 0x777777)));

                    grid.getConstraints().stream()
                        .filter(PositionListConstraint.class::isInstance)
                        .map(PositionListConstraint.class::cast)
                        .filter(positionListConstraint -> positionListConstraint.getPositionSet().stream().anyMatch(pos -> pos.getX() == column && pos.getY() == row))
                        .findFirst()
                        .ifPresent(blockConstraint -> c.setBackground(new Color((grid.getConstraints().indexOf(blockConstraint) * 1234567) % 0x888888 + 0x777777)));

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

    /**
     * Updates the Sudoku board with new data.
     *
     * @param newData    the new data to update the board with
     * @param isSolution whether the new data represents a solved Sudoku
     */
    public void update(Integer[][] newData, boolean isSolution) {
        for (int row = 0; row < newData.length; row++) {
            for (int column = 0; column < newData[row].length; column++) {
                table.setValueAt(newData[row][column], row, column);
            }
        }

        grid = new Grid(newData, grid.getConstraints(), grid.getSymbols());
        solvedGrid = new Grid(grid.getInnerGrid().get(), grid.getConstraints(), grid.getSymbols());
        alreadySolved = isSolution;
        table.repaint();
    }

    /**
     * Recovers the previous Sudoku state from the given grid.
     *
     * @param currentGrid the current grid to recover the previous state from
     */
    public void recoverPreviousSudoku(Grid currentGrid) {
        var integerTab = currentGrid.getInnerGrid().get();
        IntStream.range(0, integerTab.length).forEach(row -> IntStream.range(0, integerTab[row].length).forEach(column -> {
            if (trace[row][column]) {
                integerTab[row][column] = null;
                grid.placeUnchecked(new Vec2i(column, row), null, false, false);
            }
        }));

        update(integerTab, false);
    }
}
