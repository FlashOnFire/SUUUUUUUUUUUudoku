package fr.polytech.suuuuuuuuuuudoku.graphics;

import javax.swing.*;
import java.awt.*;

public class SudokuOptions extends JPanel {
    public SudokuOptions(Color background_color) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(background_color);

        JButton button1 = new JButton("Generate");
        JButton button2 = new JButton("Solve");
        JButton button3 = new JButton("Reset");
        JButton button4 = new JButton("Help");

        applyMaterialDesign(button1);
        applyMaterialDesign(button2);
        applyMaterialDesign(button3);
        applyMaterialDesign(button4);

        buttonPanel.add(button1);
        buttonPanel.add(button2);
        buttonPanel.add(button3);
        buttonPanel.add(button4);

        add(buttonPanel);
    }

    private void applyMaterialDesign(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(new Color(144, 226, 226));
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
