package fr.polytech.suuuuuuuuuuudoku.graphics.imgui;

import fr.polytech.suuuuuuuuuuudoku.algorithm.Generator;
import fr.polytech.suuuuuuuuuuudoku.algorithm.SolvingState;
import fr.polytech.suuuuuuuuuuudoku.algorithm.SudokuSolver;
import fr.polytech.suuuuuuuuuuudoku.constraints.BlockConstraint;
import fr.polytech.suuuuuuuuuuudoku.constraints.PositionSetConstraint;
import fr.polytech.suuuuuuuuuuudoku.graphics.Utils;
import fr.polytech.suuuuuuuuuuudoku.grid.*;
import fr.polytech.suuuuuuuuuuudoku.utils.CsvUtils;
import fr.polytech.suuuuuuuuuuudoku.utils.Difficulty;
import fr.polytech.suuuuuuuuuuudoku.utils.Vec2i;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.flag.*;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A class representing the main frame of the Sudoku game using ImGui.
 */
public class ImGUIFrame extends Application {
    /**
     * A boolean indicating if the solving thread is running.
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
     * Generator options
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
     * Private constructor to prevent instantiation of the Generator class.
     */
    private ImGUIFrame() {
    }

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
            generateBasic();
        }

        insertSeparator();

        ImGui.text("Random Blocks Grid Generator");

        ImGui.setNextItemWidth(100);
        ImGui.sliderInt("Grid Width##randomblocks", selectedRandomGeneratorWidth, 3, 6);
        ImGui.setNextItemWidth(100);
        ImGui.sameLine();
        ImGui.sliderInt("Grid Height##randomblocks", selectedRandomGeneratorHeight, 3, 6);

        if (ImGui.button("Generate##randomblocks")) {
            generateRandomBlocks();
        }

        insertSeparator();

        ImGui.text("MultiGrid Generator");
        if (ImGui.button("Generate MultiGrid")) {
            generateMultiGrid();
        }

        insertSeparator();

        ImGui.text("Test Grids");

        if (ImGui.button("Import 100x100 grid")) {
            importGrid("100x100");
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
            instantSolve();
        }
        if (ImGui.isItemHovered()) {
            ImGui.setTooltip("Instant solve the grid by showing the solution to the original generated grid (not " +
                    "taking into account the current state of the grid).");
        }

        ImGui.sameLine();

        if (ImGui.button("Visual Solve")) {
            visualSolve();
        }
        if (ImGui.isItemHovered()) {
            ImGui.setTooltip("Visually solve the grid by showing the solution step by step. The pace of the solving " +
                    "can be adjusted with the slider below.");
        }

        if (ImGui.button("Reset")) {
            reset();
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
            ImGui.setTooltip("When activated, the hint mode will reveal the correct answer when clicking cells.");
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
            ImGui.setTooltip("Adjust the pace of the solving. The lower the value, the slower the solving. (Only " +
                    "affects visual solving)");
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
            ImGui.setNextWindowSize(400, 600);
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
            gridPixelSize.x = Math.min(gridPixelSize.x, 70);
            gridPixelSize.y = Math.min(gridPixelSize.y, 70);

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

    /**
     * Draws the solvable grid in the ImGui window.
     *
     * @param solvable      the solvable to draw
     * @param gridSize      the size of the grid
     * @param gridPixelSize the size of a cell in the grid
     */
    private void drawSolvable(Solvable<?> solvable, Vec2i gridSize, ImVec2 gridPixelSize) {
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);

        for (int y = 0; y < gridSize.getY(); y++) {
            for (int x = 0; x < gridSize.getX(); x++) {
                var position = new Vec2i(x, y);

                // Add spacing when needed for multigrids
                if (!solvable.isInGrid(position)) {
                    ImGui.dummy(gridPixelSize.x, gridPixelSize.y);
                    ImGui.sameLine();
                    continue;
                }

                var isSelected = selectedPos != null && selectedPos.equals(x, y);

                Grid grid = null;
                Vec2i computedPosition;
                if (solvable instanceof Grid) {
                    grid = (Grid) solvable;
                    computedPosition = position;
                } else if (solvable instanceof MultiGrid) {
                    grid = ((MultiGrid) solvable).getGridFor(x, y).second();
                    computedPosition =
                            new Vec2i(position).subtract(((MultiGrid) solvable).getOffsets()[((MultiGrid) solvable).getGridFor(x, y).first()]);
                } else {
                    computedPosition = null;
                }

                assert computedPosition != null;

                AtomicInteger hashcode = new AtomicInteger(1);

                grid.getConstraints().stream()
                    .filter(c -> c instanceof BlockConstraint || c instanceof PositionSetConstraint)
                    .filter(c -> c.isPosAffected(computedPosition))
                    .forEach(c -> hashcode.updateAndGet(v -> 17 * v + c.hashCode()));

                // If the cell is prefilled, we will have a slightly different behavior :
                // - The color will be different
                // - The button which represents the cell will be disabled
                boolean prefilledCell = originalSolvable.getSymbolAt(position) != null;

                if (hashcode.get() != 1) {
                    int color = hashcode.get() % 360;

                    if (!prefilledCell) {
                        int[] rgbNormal;
                        int[] rgbHovered;
                        int[] rgbActive;

                        if (isSelected) {
                            rgbNormal = Utils.hslToRgb(color, 0.45f, 0.3f);
                            rgbHovered = Utils.hslToRgb(color, 0.45f, 0.25f);
                        } else {
                            rgbNormal = Utils.hslToRgb(color, 0.45f, 0.45f);
                            rgbHovered = Utils.hslToRgb(color, 0.45f, 0.35f);
                        }

                        rgbActive = Utils.hslToRgb(color, 0.45f, 0.2f);

                        ImGui.pushStyleColor(ImGuiCol.Button,
                                rgbNormal[0] / 255f,
                                rgbNormal[1] / 255f,
                                rgbNormal[2] / 255f,
                                1);
                        ImGui.pushStyleColor(
                                ImGuiCol.ButtonHovered,
                                rgbHovered[0] / 255f,
                                rgbHovered[1] / 255f,
                                rgbHovered[2] / 255f,
                                1
                        );

                        ImGui.pushStyleColor(
                                ImGuiCol.ButtonActive,
                                rgbActive[0] / 255f,
                                rgbActive[1] / 255f,
                                rgbActive[2] / 255f,
                                1
                        );
                    } else {
                        // if the cell was prefilled, we use a different darker color to indicate this cell is not
                        // editable
                        int[] rgbNormal = Utils.hslToRgb(color, 0.35f, 0.45f);

                        ImGui.pushStyleColor(ImGuiCol.Button,
                                rgbNormal[0] / 255f,
                                rgbNormal[1] / 255f,
                                rgbNormal[2] / 255f,
                                1);
                    }
                }

                float oldDisabledAlpha = ImGui.getStyle().getDisabledAlpha();
                if (prefilledCell) {
                    // We set the disabled alpha to 1.0f to avoid ImGUI to change button appearance
                    ImGui.getStyle().setDisabledAlpha(1.0f);
                    ImGui.beginDisabled();

                    ImGui.pushStyleColor(ImGuiCol.Text, 0.0f, 0.f, 0.0f, 1.0f);
                } else {
                    ImGui.pushStyleColor(ImGuiCol.Text, 1.0f, 1.f, 1.0f, 1.0f);
                }

                if (isSelected) {
                    ImGui.button(
                            (currentSymbol == null ? " " : currentSymbol) + "##" + y + ":" + x,
                            gridPixelSize
                    );
                } else {
                    if (ImGui.button(
                            (solvable.getSymbolAt(position) == null ? " " : solvable.getSymbolAt(position).toString()) + "##" + y + ":" + x,
                            gridPixelSize
                    ) && !solving.get()) {
                        if (hintMode) {
                            if (solvable.getSymbolAt(position) == null || !solvable.getSymbolAt(position).equals(solvedSolvable.getSymbolAt(position))) {
                                solvable.placeUnchecked(position, solvedSolvable.getSymbolAt(position), true, true);
                            }
                        } else {
                            setSelection(position);
                        }
                    }
                }

                if (hashcode.get() != 1) {
                    if (prefilledCell) {
                        ImGui.popStyleColor(1);
                    } else {
                        ImGui.popStyleColor(3);
                    }
                }

                if (prefilledCell) {
                    ImGui.endDisabled();
                    ImGui.getStyle().setDisabledAlpha(oldDisabledAlpha);
                }

                // Reset text color override
                ImGui.popStyleColor(1);

                ImGui.sameLine();
            }
            ImGui.newLine();
        }
        ImGui.popStyleVar();
    }

    /**
     * Handles the input of the ImGui window.
     * This method handles the input of the ImGui window
     * It handles the following keys:
     * - Delete: clears the current symbol
     * - Backspace: removes the last character of the current symbol
     * - Arrow keys: moves the selected position in the grid
     * - Keypad keys: adds the key pressed to the current symbol variable (which represents the symbol to place when
     * the changes will be applied)
     * - Enter or KeypadEnter: applies the current symbol to the selected position
     *
     * @see #setSelection(Vec2i)
     * @see #keyPress(int)
     * @see #applyEnteredSymbol()
     */
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
            if (selectedPos != null) {
                setSelection(new Vec2i(selectedPos).subtract(new Vec2i(0, 1)));
            }
        } else if (ImGui.isKeyPressed(ImGuiKey.DownArrow)) {
            if (selectedPos != null) {
                setSelection(new Vec2i(selectedPos).add(new Vec2i(0, 1)));
            }
        } else if (ImGui.isKeyPressed(ImGuiKey.LeftArrow)) {
            if (selectedPos != null) {
                setSelection(new Vec2i(selectedPos).subtract(new Vec2i(1, 0)));
            }
        } else if (ImGui.isKeyPressed(ImGuiKey.RightArrow)) {
            if (selectedPos != null) {
                setSelection(new Vec2i(selectedPos).add(new Vec2i(1, 0)));
            }
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

    /**
     * Sets or replace the current selected position in the grid.
     * If the position is not in the grid, the selection will not be set.
     * If another position is already selected, the changes will be applied.
     *
     * @param position the position
     * @see #applyEnteredSymbol()
     */
    private void setSelection(Vec2i position) {
        if (solvable.isInGrid(position)) {
            applyEnteredSymbol();

            selectedPos = position;
            currentSymbol = solvable.getSymbolAt(selectedPos) == null ? null :
                    solvable.getSymbolAt(selectedPos).toString();
        }
    }

    /**
     * Places the current symbol in the selected position in the grid.
     * This method also clears the current symbol and selected position.
     * If the current symbol is not valid (not in the symbols set), it will not be placed.
     */
    private void applyEnteredSymbol() {
        if (selectedPos != null) {
            var intCurrentSymbol = getCurrentSymbol();
            if ((solvable.getSymbols().contains(intCurrentSymbol) || intCurrentSymbol == null) && !Objects.equals(solvable.getSymbolAt(selectedPos), intCurrentSymbol)) {
                solvable.placeUnchecked(selectedPos, intCurrentSymbol, true, true);
            }

            selectedPos = null;
            currentSymbol = null;
        }
    }

    /**
     * Returns the current symbol as an integer.
     *
     * @return the current symbol as an integer (or null)
     */
    private Integer getCurrentSymbol() {
        return currentSymbol == null ? null : Integer.parseInt(currentSymbol);
    }

    /**
     * Handles the pressing of a number key.
     * This method updates the `currentSymbol` field with the pressed key.
     *
     * @param key the int value of the key pressed
     */
    private void keyPress(int key) {
        if (currentSymbol == null) {
            currentSymbol = String.valueOf(key);
        } else {
            currentSymbol += key;
        }
    }

    /**
     * Generates a basic Sudoku grid and update the original, solved, and current solvable fields.
     */
    private void generateBasic() {
        var oldPace = SudokuSolver.solvePace[0];
        SudokuSolver.solvePace[0] = 1.0f;

        var pair = Generator.generateSudokuWithBlockConstraints(
                selectedGeneratorGridSizeWidth[0],
                selectedGeneratorGridSizeHeight[0],
                selectedDifficulty
        );
        solvedSolvable = pair.first();
        solvable = pair.second();

        originalSolvable = ((Grid) solvable).shallowCopy();
        SudokuSolver.solvePace[0] = oldPace;
    }

    private void generateRandomBlocks() {
        var oldPace = SudokuSolver.solvePace[0];
        SudokuSolver.solvePace[0] = 1.0f;

        var pair = Generator.generateSudokuWithRandomBlockConstraint(
                selectedRandomGeneratorWidth[0],
                selectedRandomGeneratorHeight[0],
                selectedDifficulty
        );
        solvedSolvable = pair.first();
        solvable = pair.second();

        originalSolvable = ((Grid) solvable).shallowCopy();
        SudokuSolver.solvePace[0] = oldPace;
    }

    /**
     * Generates a MultiGrid Sudoku grid and update the original, solved, and current solvable fields.
     */
    private void generateMultiGrid() {
        var oldPace = SudokuSolver.solvePace[0];
        SudokuSolver.solvePace[0] = 1.0f;

        var pair = Generator.generateMultigridSudoku(selectedDifficulty);
        solvedSolvable = pair.first();
        solvable = pair.second();

        originalSolvable = ((MultiGrid) solvable).shallowCopy();
        SudokuSolver.solvePace[0] = oldPace;
    }

    /**
     * Imports a grid from a CSV file.
     * The full path of the file is "exemples/" + name + ".csv" (located in the project resources folder).
     *
     * @param name the name of the file to import
     * @see CsvUtils#importGrid(String)
     */
    private void importGrid(@SuppressWarnings("SameParameterValue") String name) {
        Integer[][] innerGrid;
        innerGrid = CsvUtils.importGrid("exemples/" + name + ".csv");
        var symbolSet = SymbolSets.generateSymbols(innerGrid.length);
        solvable = new Grid(innerGrid, symbolSet);
        originalSolvable = ((Grid) solvable).shallowCopy();

        Integer[][] solvedInnerGrid;
        solvedInnerGrid = CsvUtils.importGrid("exemples/" + name + "Solved.csv");
        solvedSolvable = new Grid(solvedInnerGrid, symbolSet);
    }

    /**
     * Instantly solves the current solvable by copying the stored solved solvable.
     */
    private void instantSolve() {
        solvable = (Solvable<?>) solvedSolvable.shallowCopy();
    }

    private void visualSolve() {
        Thread thread = new Thread(() -> {
            solving.set(true);

            if (solvable instanceof Grid) {
                var solve = SudokuSolver.solve(
                        new ObservableGrid(
                                (Grid) solvable,
                                innerGrid -> ((Grid) solvable).setInnerGrid(innerGrid)
                        ), solveDeducing, solveBacktracking, true
                );

                if (solve.first() != SolvingState.UNSOLVABLE) {
                    solvable = solve.second().getInner();
                } else {
                    unsolvablePopup.set(true);
                }
            } else if (solvable instanceof MultiGrid) {
                var solve = SudokuSolver.solve(
                        new ObservableMultiGrid(
                                (MultiGrid) solvable,
                                (grids) -> {
                                    for (int i = 0; i < grids.length; i++) {
                                        ((MultiGrid) solvable).getGrids()[i].setInnerGrid(grids[i]);
                                    }
                                }
                        ), solveDeducing, solveBacktracking, true
                );

                if (solve.first() != SolvingState.UNSOLVABLE) {
                    solvable = solve.second().getInner();
                } else {
                    unsolvablePopup.set(true);
                }
            }

            solving.set(false);
        });

        thread.setDaemon(true);
        thread.start();
    }

    private void reset() {
        solvable = (Solvable<?>) originalSolvable.shallowCopy();
    }
}
