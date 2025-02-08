package fr.polytech.suuuuuuuuuuudoku.graphics;

import fr.polytech.suuuuuuuuuuudoku.algorithm.Generator;
import fr.polytech.suuuuuuuuuuudoku.algorithm.SolvingState;
import fr.polytech.suuuuuuuuuuudoku.algorithm.SudokuSolver;
import fr.polytech.suuuuuuuuuuudoku.constraints.BlockConstraint;
import fr.polytech.suuuuuuuuuuudoku.constraints.PositionListConstraint;
import fr.polytech.suuuuuuuuuuudoku.grid.*;
import fr.polytech.suuuuuuuuuuudoku.utils.CsvUtils;
import fr.polytech.suuuuuuuuuuudoku.utils.Difficulty;
import fr.polytech.suuuuuuuuuuudoku.utils.Vec2i;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.flag.*;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A class representing the main frame of the Sudoku game using ImGui.
 */
public class ImGUIFrame extends Application {
    /**
     * A boolean indicating if the game is currently solving.
     */
    final AtomicBoolean solving = new AtomicBoolean(false);

    /**
     * A boolean set by the solving thread to signal we need to show the grid unsolvable popup.
     */
    final AtomicBoolean unsolvablePopup = new AtomicBoolean(false);

    /**
     * The width of the grid for the generator.
     */
    final int[] selectedGeneratorGridSizeWidth = {2};
    /**
     * The height of the grid for the generator.
     */
    final int[] selectedGeneratorGridSizeHeight = {2};
    final int[] selectedRandomGeneratorHeight = {3};
    final int[] selectedRandomGeneratorWidth = {3};

    /**
     * The difficulty of the grid to generate (affects all generators).
     */
    Difficulty selectedDifficulty = Difficulty.EXPERT;

    /**
     * A boolean indicating if the solver should use deducing.
     */
    boolean solveDeducing = true;

    /**
     * A boolean indicating if the solver should use backtracking.
     */
    boolean solveBacktracking = true;

    /**
     * A boolean indicating if the hint mode is enabled (next clicked cell will be filled with the correct value).
     */
    boolean hintMode = false;

    /**
     * The original solvable before any modifications.
     */
    Solvable<?> originalSolvable;

    /**
     * The current solvable.
     */
    Solvable<?> solvable;

    /**
     * The solved current solvable.
     */
    Solvable<?> solvedSolvable;

    /**
     * The currently selected position in the grid.
     */
    Vec2i selectedPos = null;

    /**
     * The current symbol selected for placement in the grid.
     */
    String currentSymbol = null;

    /**
     * The main method of the application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(new ImGUIFrame());
    }

    /**
     * Configures the application.
     *
     * @param config the configuration to set
     */
    @Override
    protected void configure(Configuration config) {
        config.setTitle("Suuuuuuuuuuudoku");
    }

    /**
     * The pre-run method of the application.
     */
    @Override
    protected void preRun() {
    }

    /**
     * The post-run method of the application.
     */
    @Override
    public void process() {
        ImGui.begin("Suuuuuuuuuuudoku", ImGuiWindowFlags.AlwaysAutoResize);

        boolean disable = solving.get();
        if (disable) {
            ImGui.beginDisabled();
        }

        ImGui.text("Global Generators Difficulty");
        ImGui.setNextItemWidth(100);
        if (ImGui.beginCombo("##difficulty", selectedDifficulty.toString())) {
            for (Difficulty difficulty : Difficulty.values()) {
                boolean isSelected = selectedDifficulty == difficulty;
                if (ImGui.selectable(difficulty.toString(), isSelected)) {
                    selectedDifficulty = difficulty;
                }
                if (isSelected) {
                    ImGui.setItemDefaultFocus();
                }
            }
            ImGui.endCombo();
        }

        insertSeparator();

        ImGui.text("Basic Grid Generator");

        ImGui.setNextItemWidth(100);
        ImGui.sliderInt("Block Width", selectedGeneratorGridSizeWidth, 2, 6, ImGuiSliderFlags.None);
        ImGui.sameLine();
        ImGui.setNextItemWidth(100);
        ImGui.sliderInt("Block Height", selectedGeneratorGridSizeHeight, 2, 6, ImGuiSliderFlags.None);

        if (ImGui.button("Generate##Basic")) {
            var oldPace = SudokuSolver.solvePace[0];
            SudokuSolver.solvePace[0] = 1.0f;

            var pair = Generator.generateSudokuWithBlockConstraints(
                    selectedGeneratorGridSizeWidth[0],
                    selectedGeneratorGridSizeHeight[0],
                    selectedDifficulty
            );
            solvedSolvable = pair.getFirst();
            solvable = pair.getSecond();

            originalSolvable = ((Grid) solvable).shallowCopy();
            SudokuSolver.solvePace[0] = oldPace;
        }

        insertSeparator();

        ImGui.text("Random Blocks Grid Generator");

        ImGui.setNextItemWidth(100);
        ImGui.sliderInt("Grid Width##randomblocks", selectedRandomGeneratorWidth, 3, 6);
        ImGui.setNextItemWidth(100);
        ImGui.sameLine();
        ImGui.sliderInt("Grid Height##randomblocks", selectedRandomGeneratorHeight, 3, 6);

        if (ImGui.button("Generate##randomblocks")) {
            var oldPace = SudokuSolver.solvePace[0];
            SudokuSolver.solvePace[0] = 1.0f;

            var pair = Generator.generateSudokuWithRandomBlockConstraint(
                    selectedRandomGeneratorWidth[0],
                    selectedRandomGeneratorHeight[0],
                    selectedDifficulty
            );
            solvedSolvable = pair.getFirst();
            solvable = pair.getSecond();

            originalSolvable = ((Grid) solvable).shallowCopy();
            SudokuSolver.solvePace[0] = oldPace;
        }

        insertSeparator();

        ImGui.text("MultiGrid Generator");
        if (ImGui.button("Generate MultiGrid")) {
            var oldPace = SudokuSolver.solvePace[0];
            SudokuSolver.solvePace[0] = 1.0f;

            var pair = Generator.generateMultigridSudoku(selectedDifficulty);
            solvedSolvable = pair.getFirst();
            solvable = pair.getSecond();

            originalSolvable = ((MultiGrid) solvable).shallowCopy();
            SudokuSolver.solvePace[0] = oldPace;
        }

        insertSeparator();

        ImGui.text("Test Grids");

        if (ImGui.button("Import 100x100 grid")) {
            Integer[][] innerGrid;
            innerGrid = CsvUtils.importGrid("exemples/100x100.csv");
            var symbolSet = SymbolSets.generateSymbols(innerGrid.length);
            solvable = new Grid(innerGrid, symbolSet);
            originalSolvable = ((Grid) solvable).shallowCopy();

            Integer[][] solvedInnerGrid;
            solvedInnerGrid = CsvUtils.importGrid("exemples/100x100Solved.csv");
            solvedSolvable = new Grid(solvedInnerGrid, symbolSet);
        }

        insertSeparator();

        ImGui.text("Solver");

        if (solvable == null) {
            ImGui.beginDisabled();
        }

        if (ImGui.checkbox("Deducing", solveDeducing)) {
            solveDeducing = !solveDeducing;
            if (!solveDeducing) {
                solveBacktracking = true;
            }
        }
        ImGui.sameLine();

        if (ImGui.checkbox("Backtracking", solveBacktracking)) {
            solveBacktracking = !solveBacktracking;
            if (!solveBacktracking) {
                solveDeducing = true;
            }
        }

        if (ImGui.button("Instant Solve")) {
            if (solvable instanceof Grid) {
                solvable = ((Grid) solvedSolvable).shallowCopy();
            } else if (solvable instanceof MultiGrid) {
                solvable = ((MultiGrid) solvedSolvable).shallowCopy();
            }
        }
        if (ImGui.isItemHovered()) {
            ImGui.setTooltip("Instant solve the grid by showing the solution to the original generated grid (not taking into account the current state of the grid).");
        }

        ImGui.sameLine();

        if (ImGui.button("Visual Solve")) {
            Thread thread = null;
            if (solvable instanceof Grid) {
                thread = new Thread(() -> {
                    solving.set(true);
                    var solve = SudokuSolver.solve(
                            new ObservableGrid(
                                    (Grid) solvable,
                                    innerGrid -> ((Grid) solvable).setInnerGrid(innerGrid)
                            ), solveDeducing, solveBacktracking, true
                    );

                    if (solve.getFirst() != SolvingState.UNSOLVABLE) {
                        solvable = solve.getSecond().getGrid();
                    } else {
                        unsolvablePopup.set(true);
                    }

                    solving.set(false);
                });
            } else if (solvable instanceof MultiGrid) {
                thread = new Thread(() -> {
                    solving.set(true);
                    var solve = SudokuSolver.solve(
                            new ObservableMultiGrid(
                                    (MultiGrid) solvable,
                                    (index, innerGrid) -> ((MultiGrid) solvable)
                                            .getGrids()[index].setInnerGrid(innerGrid)
                            ), solveDeducing, solveBacktracking, true
                    );

                    if (solve.getFirst() != SolvingState.UNSOLVABLE) {
                        solvable = solve.getSecond().getGrid();
                    } else {
                        unsolvablePopup.set(true);
                    }

                    solving.set(false);
                });
            }

            assert thread != null;
            thread.setDaemon(true);
            thread.start();
        }
        if (ImGui.isItemHovered()) {
            ImGui.setTooltip("Visually solve the grid by showing the solution step by step. The pace of the solving can be adjusted with the slider below.");
        }

        if (ImGui.button("Reset")) {
            if (originalSolvable instanceof Grid) {
                solvable = ((Grid) originalSolvable).shallowCopy();
            } else if (originalSolvable instanceof MultiGrid) {
                solvable = ((MultiGrid) originalSolvable).shallowCopy();
            }
        }

        if (hintMode) {
            ImGui.pushStyleColor(ImGuiCol.Button, 0.0f, 0.8f, 0.0f, 1.0f); // Lighter green color for active state
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.0f, 0.9f, 0.0f, 1.0f); // Even lighter green for hover state
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.0f, 1.0f, 0.0f, 1.0f); // Bright green for active state
        } else {
            ImGui.pushStyleColor(ImGuiCol.Button, 0.8f, 0.0f, 0.0f, 1.0f); // Lighter red color for inactive state
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.9f, 0.0f, 0.0f, 1.0f); // Even lighter red for hover state
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, 1.0f, 0.0f, 0.0f, 1.0f); // Bright red for active state
        }
        if (ImGui.button("Hint")) {
            hintMode = !hintMode;
            if (hintMode) {
                selectedPos = null;
            }
        }
        if (ImGui.isItemHovered()) {
            ImGui.setTooltip("When activated, the hint mode will reveal the correct answer for clicking cells.");
        }

        ImGui.popStyleColor(3);

        if (solvable == null) {
            ImGui.endDisabled();
        }

        if (disable) {
            ImGui.endDisabled();
        }

        ImGui.setNextItemWidth(100);
        ImGui.sliderFloat("Solve Pace", SudokuSolver.solvePace, 0.0f, 1.0f);
        if (ImGui.isItemHovered()) {
            ImGui.setTooltip("Adjust the pace of the solving. The lower the value, the slower the solving. (Only affects visual solving)");
        }

        ImGui.end();

        //little overlay window to show infos
        ImGui.setNextWindowPos(10, 10);
        ImGui.setNextWindowSize(200, 50);
        ImGui.begin(
                "Infos",
                ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoTitleBar
        );
        ImGui.text("Size: " + ((solvable != null) ? (solvable.getSize().getX() + "x" + solvable.getSize().getY()) :
                "No grid loaded"));
        ImGui.text("Solve pace : " + SudokuSolver.solvePace[0]);
        ImGui.end();

        if (solvable != null) {
            ImGui.setNextWindowSize(400, 800);
            ImGui.begin("Move History");

            var disabled = false;
            if (solving.get()) {
                ImGui.beginDisabled();
                disabled = true;
            }
            var empty = solvable.getMoves().isEmpty();
            if (empty) {
                ImGui.beginDisabled();
            }
            if (ImGui.button("Undo")) {
                solvable.undoLastMove(true);
            }

            if (empty) {
                ImGui.endDisabled();
            }

            ImGui.beginTable("Move History Table", 2, ImGuiTableFlags.Borders | ImGuiTableFlags.RowBg);
            ImGui.tableSetupColumn("Move");
            ImGui.tableSetupColumn("Action");
            ImGui.tableHeadersRow();

            for (int i = 0; i < solvable.getMoves().size(); i++) {
                var move = solvable.getMoves().get(i);
                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.text(move.toString());
                ImGui.tableNextColumn();
                if (ImGui.button("Return here##" + move)) {
                    for (int j = solvable.getMoves().size() - 1; j > i; j--) {
                        solvable.undoLastMove(false);
                    }
                    solvable.computeAllEmptyCellsPossibilities();
                }
            }
            ImGui.endTable();

            if (disabled) {
                ImGui.endDisabled();
            }
            ImGui.end();


            var size = ImGui.getIO().getDisplaySize();
            var gridSize = solvable.getSize();
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
                    ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoBringToFrontOnFocus
            );

            drawSolvable(solvable, gridSize, gridPixelSize);
            /*if (solvable instanceof Grid) {
                drawGrid((Grid) solvable, gridSize, gridPixelSize);
            } else if (solvable instanceof MultiGrid) {
                drawMultiGrid((MultiGrid) solvable, gridSize, gridPixelSize);
            }*/

            ImGui.popStyleColor();
            ImGui.popStyleVar();

            handleInput();

            ImGui.end();
        }

        if (unsolvablePopup.get()) {
            ImGui.openPopup("Unsolvable");
            unsolvablePopup.set(false);
        }

        if (ImGui.beginPopupModal("Unsolvable", null, ImGuiWindowFlags.AlwaysAutoResize)) {
            ImGui.text("This grid is unsolvable with the currently filled cells.");
            if (ImGui.button("OK")) {
                ImGui.closeCurrentPopup();
            }
            ImGui.endPopup();
        }
    }

    /**
     * Inserts a separator in the ImGui window.
     * This method adds padding before and after the separator to create space
     * between UI elements.
     */
    private void insertSeparator() {
        ImGui.dummy(new ImVec2(0.0f, 5.0f));
        ImGui.separator();
        ImGui.dummy(new ImVec2(0.0f, 5.0f));
    }

    private void drawSolvable(Solvable<?> solvable, Vec2i gridSize, ImVec2 gridPixelSize) {
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);
        for (int y = 0; y < gridSize.getY(); y++) {
            for (int x = 0; x < gridSize.getX(); x++) {
                // Add spacing when needed for multigrids
                if (!solvable.isInGrid(new Vec2i(x, y))) {
                    ImGui.dummy(gridPixelSize.x, gridPixelSize.y);
                    ImGui.sameLine();
                    continue;
                }

                var isSelected = selectedPos != null && selectedPos.equals(x, y);

                Grid grid = null;
                Vec2i computedPosition;
                if (solvable instanceof Grid) {
                    grid = (Grid) solvable;
                    computedPosition = new Vec2i(x, y);
                } else if (solvable instanceof MultiGrid) {
                    grid = ((MultiGrid) solvable).getGridFor(x, y).getSecond();
                    computedPosition = new Vec2i(x, y).substract(((MultiGrid) solvable).getOffsets()[((MultiGrid) solvable).getGridFor(x, y).getFirst()]);
                } else {
                    computedPosition = null;
                }

                assert computedPosition != null;

                AtomicInteger hashcode = new AtomicInteger(1);

                grid.getConstraints().stream()
                        .filter(c -> c instanceof BlockConstraint || c instanceof PositionListConstraint)
                        .filter(c -> c.isPosAffected(computedPosition))
                        .forEach(c -> hashcode.updateAndGet(v -> 17 * v + c.hashCode()));

                if (hashcode.get() != 1) {
                    int color = hashcode.get() % 360;

                    int[] rgb;
                    int[] rgbDarker;
                    if (isSelected) {
                        rgb = Utils.hslToRgb(color, 0.45f, 0.3f);
                        rgbDarker = Utils.hslToRgb(color, 0.45f, 0.25f);

                    } else {
                        rgb = Utils.hslToRgb(color, 0.45f, 0.45f);
                        rgbDarker = Utils.hslToRgb(color, 0.45f, 0.35f);
                    }

                    int[] rgbEvenDarker = Utils.hslToRgb(color, 0.45f, 0.2f);

                    ImGui.pushStyleColor(ImGuiCol.Button, rgb[0] / 255f, rgb[1] / 255f, rgb[2] / 255f, 1);
                    ImGui.pushStyleColor(
                            ImGuiCol.ButtonHovered,
                            rgbDarker[0] / 255f,
                            rgbDarker[1] / 255f,
                            rgbDarker[2] / 255f,
                            1
                    );

                    ImGui.pushStyleColor(
                            ImGuiCol.ButtonActive,
                            rgbEvenDarker[0] / 255f,
                            rgbEvenDarker[1] / 255f,
                            rgbEvenDarker[2] / 255f,
                            1
                    );

                }

                if (isSelected) {
                    ImGui.button(
                            (currentSymbol == null ? " " : currentSymbol) + "##" + y + ":" + x,
                            gridPixelSize
                    );
                } else {
                    if (ImGui.button(
                            (solvable.getSymbolAt(new Vec2i(x, y)) == null ? " " : solvable.getSymbolAt(new Vec2i(
                                    x,
                                    y
                            )).toString()) + "##" + y + ":" + x,
                            gridPixelSize
                    ) && !solving.get()) {
                        if (hintMode) {
                            if (solvable.getSymbolAt(new Vec2i(x, y)) == null || !solvable.getSymbolAt(new Vec2i(x, y)).equals(solvedSolvable.getSymbolAt(new Vec2i(x, y)))) {
                                solvable.placeUnchecked(new Vec2i(x, y), solvedSolvable.getSymbolAt(new Vec2i(x, y)), true, true);
                            }
                        } else {
                            setSelection(x, y);
                        }
                    }
                }

                if (hashcode.get() != 1) {
                    ImGui.popStyleColor(3);
                }

                ImGui.sameLine();
            }
            ImGui.newLine();
        }
        ImGui.popStyleVar();
    }

    private void handleInput() {
        if (ImGui.isKeyPressed(ImGuiKey.Delete)) {
            currentSymbol = null;
        } else if (ImGui.isKeyPressed(ImGuiKey.Backspace)) {
            if (currentSymbol != null && !currentSymbol.isEmpty()) {
                currentSymbol = currentSymbol.substring(0, currentSymbol.length() - 1);
                if (currentSymbol.isEmpty()) {
                    currentSymbol = null;
                }
            }
        }
        if (ImGui.isKeyPressed(ImGuiKey.UpArrow)) {
            setSelection(selectedPos.getX(), selectedPos.getY() - 1);
        } else if (ImGui.isKeyPressed(ImGuiKey.DownArrow)) {
            setSelection(selectedPos.getX(), selectedPos.getY() + 1);
        } else if (ImGui.isKeyPressed(ImGuiKey.LeftArrow)) {
            setSelection(selectedPos.getX() - 1, selectedPos.getY());
        } else if (ImGui.isKeyPressed(ImGuiKey.RightArrow)) {
            setSelection(selectedPos.getX() + 1, selectedPos.getY());
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
        } else if (ImGui.isKeyPressed(ImGuiKey.Enter) || ImGui.isKeyPressed(ImGuiKey.KeyPadEnter)) {
            applyEnteredSymbol();
        }
    }

    private void setSelection(int x, int y) {
        if (x >= 0 && x < solvable.getSize().getX() && y >= 0 && y < solvable.getSize().getY()) {
            applyEnteredSymbol();
            selectedPos = new Vec2i(x, y);
            currentSymbol = solvable.getSymbolAt(new Vec2i(x, y)) == null ? null : solvable.getSymbolAt(new Vec2i(
                    x,
                    y
            )).toString();
        }
    }

    private void applyEnteredSymbol() {
        if (selectedPos != null) {
            var int_current_symbol = getCurrentSymbol();
            if (solvable.getSymbols().contains(int_current_symbol) || int_current_symbol == null) {
                solvable.placeUnchecked(selectedPos, int_current_symbol, true, true);
                selectedPos = null;
            } else {
                currentSymbol = null;
            }
        }
    }

    private Integer getCurrentSymbol() {
        return currentSymbol == null ? null : Integer.parseInt(currentSymbol);
    }

    private void keyPress(int key) {
        if (currentSymbol == null) {
            currentSymbol = String.valueOf(key);
        } else {
            currentSymbol += key;
        }
    }

}
