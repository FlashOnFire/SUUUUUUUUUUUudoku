package fr.polytech.suuuuuuuuuuudoku;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
//        var grid = Generator.generateClassicNxN(16);
        Tui tui = new Tui();
        tui.start();
//        SwingUtilities.invokeLater(() ->
//        {
//            SudokuFrame frame = new SudokuFrame(grid);
//            frame.setVisible(true);
//        });
    }
}