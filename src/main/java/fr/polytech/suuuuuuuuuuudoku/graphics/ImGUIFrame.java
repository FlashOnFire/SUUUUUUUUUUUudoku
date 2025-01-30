package fr.polytech.suuuuuuuuuuudoku.graphics;

import fr.polytech.suuuuuuuuuuudoku.algorithm.Generator;
import fr.polytech.suuuuuuuuuuudoku.algorithm.SudokuSolver;
import fr.polytech.suuuuuuuuuuudoku.grid.Grid;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;

public class ImGUIFrame extends Application {
    Grid originalGrid;
    Grid grid;

    @Override
    protected void configure(Configuration config) {
        config.setTitle("Suuuuuuuuuuudoku");
    }

    @Override
    protected void preRun() {
        grid = Generator.generateRandomGridN(6);
        originalGrid = grid.shallowCopy();
    }

    @Override
    public void process() {
        if (ImGui.begin("Suuuuuuuuuuudoku")) {
            if (ImGui.button("Generate")) {
                grid = Generator.generateRandomGridN(6);
                originalGrid = grid.shallowCopy();
            }
            ImGui.sameLine();
            if (ImGui.button("Solve")) {
                grid = SudokuSolver.solve(grid, true, true, true).getSecond();
            }
            ImGui.sameLine();
            if (ImGui.button("Reset")) {
                grid = originalGrid.shallowCopy();
            }
        }
        ImGui.end();

        var size = ImGui.getIO().getDisplaySize();
        var gridSize = grid.size();
        var minSize = Math.min(size.x, size.y);

        var gridPixelSize = new ImVec2(minSize / gridSize.getX(), minSize / gridSize.getY());
        gridPixelSize.x = Math.min(gridPixelSize.x, 50);
        gridPixelSize.y = Math.min(gridPixelSize.y, 50);

        var fullGridPixelSize = new ImVec2(gridPixelSize.x * gridSize.getX(), gridPixelSize.y * gridSize.getY());

        ImGui.setNextWindowPos(size.x /2 - fullGridPixelSize.x / 2, size.y / 2 - fullGridPixelSize.y / 2);
        ImGui.setNextWindowSize(fullGridPixelSize.x, fullGridPixelSize.y);
        
        
        ImGui.pushStyleColor(ImGuiCol.WindowBg, ImGui.getColorU32(0.6f, 0.6f, 0.6f, 1.0f));
        ImGui.begin("Grid", null, ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoCollapse);

        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);
        for (int i = 0; i < gridSize.getX(); i++) {
            for (int j = 0; j < gridSize.getY(); j++) {
                ImGui.button(grid.getSymbolAt(i, j) == null ? " " : grid.getSymbolAt(i, j).toString(), gridPixelSize);
                ImGui.sameLine();
            }
            ImGui.newLine();
        }
        ImGui.popStyleVar();

        ImGui.end();
        ImGui.popStyleColor();

        ImGui.showDemoWindow();
    }
}
