package fr.polytech.suuuuuuuuuuudoku.graphics;

import fr.polytech.suuuuuuuuuuudoku.CsvUtils;
import fr.polytech.suuuuuuuuuuudoku.algorithm.Generator;
import fr.polytech.suuuuuuuuuuudoku.algorithm.SudokuSolver;
import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec2i;
import fr.polytech.suuuuuuuuuuudoku.grid.Grid;
import fr.polytech.suuuuuuuuuuudoku.grid.ObservableGrid;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiKey;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

public class ImGUIFrame extends Application {
    Grid originalGrid;
    Grid grid;
    Vec2i selected_pos = null;
    String current_symbol = null;
    AtomicBoolean solving = new AtomicBoolean(false);

    @Override
    protected void configure(Configuration config) {
        config.setTitle("Suuuuuuuuuuudoku");
    }

    @Override
    protected void preRun() {
    }

    @Override
    public void process() {
        ImGui.begin("Suuuuuuuuuuudoku");

        boolean disable = solving.get();
        if (disable) {
            ImGui.beginDisabled();
        }

        if (ImGui.button("Generate")) {
            grid = Generator.generateClassicNxN(16);
            originalGrid = grid.shallowCopy();
        }

        if (grid == null) {
            ImGui.beginDisabled();
        }
        ImGui.sameLine();
        if (ImGui.button("Solve")) {
            selected_pos = null;
            new Thread(() -> {
                solving.set(true);
                grid = SudokuSolver.solve(new ObservableGrid(grid, innerGrid -> grid.setInnerGrid(innerGrid)), true, true, true).getSecond().getGrid();
                solving.set(false);
            }).start();
        }

        ImGui.sameLine();
        if (ImGui.button("Reset")) {
            grid = originalGrid.shallowCopy();
        }

        if (grid == null) {
            ImGui.endDisabled();
        }

        ImGui.sameLine();
        if (ImGui.checkbox("Slow Solve", SudokuSolver.slowSolve)) {
            SudokuSolver.slowSolve = !SudokuSolver.slowSolve;
        }


        if (ImGui.button("MaxiGrid")) {
            try {
                grid = CsvUtils.importGrid(Path.of("src/test/java/fr/polytech/suuuuuuuuuuudoku/resources/100x100.csv"));
                originalGrid = grid.shallowCopy();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        if (disable) {
            ImGui.endDisabled();
        }

        ImGui.end();

        //little overlay window to show grid size
        ImGui.setNextWindowPos(10, 10);
        ImGui.setNextWindowSize(200, 20);
        ImGui.begin("Grid Size", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoTitleBar);
        ImGui.text("Size: " + ((grid != null) ? (grid.getSize().getX() + "x" + grid.getSize().getY()) : "No grid loaded"));
        ImGui.end();

        if (grid != null) {
            var size = ImGui.getIO().getDisplaySize();
            var gridSize = grid.getSize();
            var minSize = Math.min(size.x, size.y);

            var gridPixelSize = new ImVec2(minSize / gridSize.getX(), minSize / gridSize.getY());
            gridPixelSize.x = Math.min(gridPixelSize.x, 50);
            gridPixelSize.y = Math.min(gridPixelSize.y, 50);

            var fullGridPixelSize = new ImVec2(gridPixelSize.x * gridSize.getX(), gridPixelSize.y * gridSize.getY());

            ImGui.setNextWindowPos(size.x / 2 - fullGridPixelSize.x / 2, size.y / 2 - fullGridPixelSize.y / 2);
            ImGui.setNextWindowSize(fullGridPixelSize.x, fullGridPixelSize.y);


            ImGui.pushStyleColor(ImGuiCol.WindowBg, ImGui.getColorU32(0.6f, 0.6f, 0.6f, 1.0f));
            ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, new ImVec2(0, 0));
            ImGui.begin(
                    "Grid",
                    null,
                    ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoTitleBar
            );

            drawGrid(gridSize, gridPixelSize);

            ImGui.popStyleColor();
            ImGui.popStyleVar();

            handleInput();

            ImGui.end();
        }

        //ImGui.showDemoWindow();
    }

    private void drawGrid(Vec2i gridSize, ImVec2 gridPixelSize) {
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);
        for (int y = 0; y < gridSize.getY(); y++) {
            for (int x = 0; x < gridSize.getX(); x++) {
                boolean stylePushed = false;
                if (selected_pos != null && selected_pos.equals(x, y)) {
                    ImGui.pushStyleColor(ImGuiCol.Button, ImGui.getColorU32(0.2f, 0.2f, 0.8f, 1.0f));
                    ImGui.pushStyleColor(ImGuiCol.ButtonHovered, ImGui.getColorU32(0.3f, 0.3f, 0.8f, 1.0f));
                    stylePushed = true;
                    ImGui.button(
                            (current_symbol == null ? " " : current_symbol) + "##" + y + ":" + x,
                            gridPixelSize
                    );
                    ImGui.popStyleColor();
                    ImGui.popStyleColor();
                } else {
                    if (ImGui.button(
                            (grid.getSymbolAt(x, y) == null ? " " : grid.getSymbolAt(x, y).toString()) + "##" + y + ":" + x,
                            gridPixelSize
                    ) && !solving.get()) {
                        setSelection(x, y);
                        System.out.println("Selected: " + selected_pos);
                    }
                }
                ImGui.sameLine();
            }
            ImGui.newLine();
        }
        ImGui.popStyleVar();
    }

    private void handleInput() {
        if (ImGui.isKeyPressed(ImGuiKey.Delete)) {
            current_symbol = null;
        } else if (ImGui.isKeyPressed(ImGuiKey.Backspace)) {
            if (current_symbol != null && !current_symbol.isEmpty()) {
                current_symbol = current_symbol.substring(0, current_symbol.length() - 1);
            }
        }
        if (ImGui.isKeyPressed(ImGuiKey.UpArrow)) {
            setSelection(selected_pos.getX(), selected_pos.getY() - 1);
        } else if (ImGui.isKeyPressed(ImGuiKey.DownArrow)) {
            setSelection(selected_pos.getX(), selected_pos.getY() + 1);
        } else if (ImGui.isKeyPressed(ImGuiKey.LeftArrow)) {
            setSelection(selected_pos.getX() - 1, selected_pos.getY());
        } else if (ImGui.isKeyPressed(ImGuiKey.RightArrow)) {
            setSelection(selected_pos.getX() + 1, selected_pos.getY());
        } else if (ImGui.isKeyPressed(ImGuiKey.Keypad0)) {
            keyPress(0);
        } else if (ImGui.isKeyPressed(ImGuiKey.Keypad1)) {
            keyPress(1);
        } else if (ImGui.isKeyPressed(ImGuiKey.Keypad2)) {
            keyPress(2);
        } else if (ImGui.isKeyPressed(ImGuiKey.Keypad3)) {
            keyPress(3);
        } else if (ImGui.isKeyPressed(ImGuiKey.Keypad4)) {
            keyPress(4);
        } else if (ImGui.isKeyPressed(ImGuiKey.Keypad5)) {
            keyPress(5);
        } else if (ImGui.isKeyPressed(ImGuiKey.Keypad6)) {
            keyPress(6);
        } else if (ImGui.isKeyPressed(ImGuiKey.Keypad7)) {
            keyPress(7);
        } else if (ImGui.isKeyPressed(ImGuiKey.Keypad8)) {
            keyPress(8);
        } else if (ImGui.isKeyPressed(ImGuiKey.Keypad9)) {
            keyPress(9);
        } else if (ImGui.isKeyPressed(ImGuiKey.Enter)) {
            applyLastChanges();
            selected_pos = null;
        }
    }

    private void setSelection(int x, int y) {
        if (x >= 0 && x < grid.getSize().getX() && y >= 0 && y < grid.getSize().getY()) {
            applyLastChanges();
            selected_pos = new Vec2i(x, y);
            current_symbol = grid.getSymbolAt(x, y) == null ? null : grid.getSymbolAt(x, y).toString();
        }
    }

    private void applyLastChanges() {
        if (selected_pos != null) {
            grid.placeUnchecked(selected_pos, getCurrentSymbol(), true, false);
        }
    }

    private Integer getCurrentSymbol() {
        return current_symbol == null ? null : Integer.parseInt(current_symbol);
    }

    private void keyPress(int key) {
        if (current_symbol == null) {
            current_symbol = String.valueOf(key);
        } else {
            current_symbol += key;
        }
    }
}
