package fr.polytech.suuuuuuuuuuudoku.graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class SudokuOptions extends JPanel {
    public SudokuOptions(Color background_color, Runnable solve, Runnable reset) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(background_color);

        JButton button1 = new JButton("Generate");
        JButton button2 = new JButton("Solve");
        JButton button3 = new JButton("Reset");
        JButton button4 = new JButton("Hint");

        button2.addActionListener(_ -> solve.run());
        button3.addActionListener(_ -> reset.run());

        applyMaterialDesign(button1);
        applyMaterialDesign(button2);
        applyMaterialDesign(button3);
        applyMaterialDesign(button4);

        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(button1);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(button2);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(button3);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(button4);
        buttonPanel.add(Box.createVerticalStrut(10));

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                //TODO : fix problem of resizing height which resize the width of the buttons
                int newSize = getHeight() / 5;
                buttonPanel.setPreferredSize(new Dimension(newSize, getHeight()));
                for (Component component : buttonPanel.getComponents()) {
                    if (component instanceof Box.Filler filler) {
                        filler.changeShape(filler.getMinimumSize(), new Dimension(component.getWidth(), newSize), filler.getMaximumSize());
                    }
                }
                revalidate();
                repaint();
            }
        });
        add(buttonPanel, BorderLayout.CENTER);


    }

    private void applyMaterialDesign(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(new Color(144, 226, 226));
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height));

    }
}
