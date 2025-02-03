package fr.polytech.suuuuuuuuuuudoku;

import fr.polytech.suuuuuuuuuuudoku.graphics.ImGUIFrame;

import java.io.IOException;

import static fr.polytech.suuuuuuuuuuudoku.algorithm.Generator.fastSolvedGridCreation;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
//        SudokuFrame.main(new String[0]);
//        Tui.main(new String[0]);
    //    ImGUIFrame.main(new String[0])
        var grid = fastSolvedGridCreation(2, 4);
        var grid2 = fastSolvedGridCreation(3, 2);
        var grid3 = fastSolvedGridCreation(4, 2);
        var grid4 = fastSolvedGridCreation(2, 3);
        var grid5 = fastSolvedGridCreation(3, 3);
    }
}