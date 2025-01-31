package fr.polytech.suuuuuuuuuuudoku;

import fr.polytech.suuuuuuuuuuudoku.graphics.ImGUIFrame;

import java.io.IOException;

import static imgui.app.Application.launch;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
//        var grid = Generator.generateClassicNxN(16);
        /*Tui tui = new Tui();
        tui.start();*/
//        SwingUtilities.invokeLater(() ->
//        {
//            SudokuFrame frame = new SudokuFrame(grid);
//            frame.setVisible(true);
//        });

        launch(new ImGUIFrame());
    }
}