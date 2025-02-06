package fr.polytech.suuuuuuuuuuudoku.graphics;

import fr.polytech.suuuuuuuuuuudoku.CsvUtils;
import fr.polytech.suuuuuuuuuuudoku.algorithm.Generator;
import fr.polytech.suuuuuuuuuuudoku.algorithm.SudokuSolver;
import fr.polytech.suuuuuuuuuuudoku.constraints.BlockConstraint;
import fr.polytech.suuuuuuuuuuudoku.constraints.PositionListConstraint;
import fr.polytech.suuuuuuuuuuudoku.grid.Grid;
import fr.polytech.suuuuuuuuuuudoku.grid.MultiGrid;
import fr.polytech.suuuuuuuuuuudoku.grid.ObservableGrid;
import fr.polytech.suuuuuuuuuuudoku.grid.Solvable;
import fr.polytech.suuuuuuuuuuudoku.symbols.SymbolSets;
import fr.polytech.suuuuuuuuuuudoku.utils.Difficulty;
import fr.polytech.suuuuuuuuuuudoku.utils.Vec2i;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.flag.*;

import java.io.FileNotFoundException;
import java.nio.file.Path;
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
     * The width of the grid for the generator.
     */
    final int[] selectedGeneratorGridSizeWidth = {2};

    /**
     * The height of the grid for the generator.
     */
    final int[] selectedGeneratorGridSizeHeight = {2};

    final int[] selectedRandomGeneratorHeight = {4};
    final int[] selectedRandomGeneratorWidth = {4};

    /**
     * The original solvable grid before any modifications.
     */
    Solvable<?> originalSolvable;

    /**
     * The current solvable grid.
     */
    Solvable<?> solvable;

    /**
     * The currently selected position in the grid.
     */
    Vec2i selected_pos = null;

    /**
     * The current symbol selected for placement in the grid.
     */
    String current_symbol = null;

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
        ImGui.begin("Suuuuuuuuuuudoku");

        boolean disable = solving.get();
        if (disable) {
            ImGui.beginDisabled();
        }

        ImGui.sameLine();
        ImGui.setNextItemWidth(100);
        ImGui.sliderInt("Block Width", selectedGeneratorGridSizeWidth, 2, 6, ImGuiSliderFlags.None);
        ImGui.sameLine();
        ImGui.setNextItemWidth(100);
        ImGui.sliderInt("Block Height", selectedGeneratorGridSizeHeight, 2, 6, ImGuiSliderFlags.None);

        ImGui.sameLine();
        if (ImGui.button("Generate")) {
            var oldPace = SudokuSolver.solvePace[0];
            SudokuSolver.solvePace[0] = 1.0f;

            solvable = Generator.generateSudokuWithBlockConstraints(
                    selectedGeneratorGridSizeWidth[0],
                    selectedGeneratorGridSizeHeight[0],
                    Difficulty.EXPERT
            );

            originalSolvable = ((Grid) solvable).shallowCopy();
            SudokuSolver.solvePace[0] = oldPace;
        }
        ImGui.separator();

        ImGui.setNextItemWidth(100);
        ImGui.sliderInt("Grid Width##Randomblocks", selectedRandomGeneratorWidth, 4, 6);
        ImGui.setNextItemWidth(100);
        ImGui.sameLine();
        ImGui.sliderInt("Grid Height##Randombloks", selectedRandomGeneratorHeight, 4, 6);

        ImGui.sameLine();

        if (ImGui.button("Generate Random Blocks Grid")) {
            var oldPace = SudokuSolver.solvePace[0];
            SudokuSolver.solvePace[0] = 1.0f;

            solvable = Generator.generateSudokuWithRandomBlockConstraint(
                    selectedRandomGeneratorWidth[0],
                    selectedRandomGeneratorHeight[0],
                    Difficulty.EXPERT
            );

            originalSolvable = ((Grid) solvable).shallowCopy();
            SudokuSolver.solvePace[0] = oldPace;
        }
        ImGui.separator();

        if (ImGui.button("Generate MultiGrid")) {
            var oldPace = SudokuSolver.solvePace[0];
            SudokuSolver.solvePace[0] = 1.0f;

            solvable = Generator.generateMultigridSudoku(Difficulty.EXPERT);

            originalSolvable = ((MultiGrid) solvable).shallowCopy();
            SudokuSolver.solvePace[0] = oldPace;
        }

        ImGui.separator();

        if (ImGui.button("MaxiGrid")) {
            try {
                var innergrid =
                        CsvUtils.importGrid(Path.of(ClassLoader.getSystemResource("exemples/100x100.csv").getFile()));
                var symbolSet = SymbolSets.generateSymbols(innergrid.length);
                solvable = new Grid(innergrid, symbolSet);
                originalSolvable = ((Grid) solvable).shallowCopy();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        ImGui.separator();

        if (solvable == null) {
            ImGui.beginDisabled();
        }
        ImGui.sameLine();
        if (ImGui.button("Solve")) {
            if (solvable instanceof Grid) {
                new Thread(() -> {
                    solving.set(true);
                    solvable = SudokuSolver.solve(
                            new ObservableGrid(
                                    (Grid) solvable,
                                    innerGrid -> ((Grid) solvable).setInnerGrid(innerGrid)
                            ), true, true, true
                    ).getSecond().getGrid();
                    solving.set(false);
                }).start();
            } else if (solvable instanceof MultiGrid) {
                new Thread(() -> {
                    solving.set(true);
                    solvable = SudokuSolver.solve((MultiGrid) solvable, true, true, true).getSecond();
                    solving.set(false);
                }).start();
            }
        }

        ImGui.sameLine();
        if (ImGui.button("Reset")) {
            if (originalSolvable instanceof Grid) {
                solvable = ((Grid) originalSolvable).shallowCopy();
            } else if (originalSolvable instanceof MultiGrid) {
                solvable = ((MultiGrid) originalSolvable).shallowCopy();
            }
        }

        if (solvable == null) {
            ImGui.endDisabled();
        }

        if (disable) {
            ImGui.endDisabled();
        }

        ImGui.setNextItemWidth(100);
        ImGui.sliderFloat("Solve Pace", SudokuSolver.solvePace, 0.0f, 1.0f);

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
        ImGui.text("Solve pace :" + SudokuSolver.solvePace[0]);
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
                    ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoTitleBar
            );

            if (solvable instanceof Grid) {
                drawGrid((Grid) solvable, gridSize, gridPixelSize);
            } else if (solvable instanceof MultiGrid) {
                drawMultiGrid((MultiGrid) solvable, gridSize, gridPixelSize);
            }

            ImGui.popStyleColor();
            ImGui.popStyleVar();

            handleInput();

            ImGui.end();
        }

        //ImGui.showDemoWindow();
    }

    private void drawGrid(Grid grid, Vec2i gridSize, ImVec2 gridPixelSize) {
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);
        for (int y = 0; y < gridSize.getY(); y++) {
            for (int x = 0; x < gridSize.getX(); x++) {

                var isSelected = selected_pos != null && selected_pos.equals(x, y);

                int finalY = y;
                int finalX = x;

                AtomicInteger hashcode = new AtomicInteger(1);

                grid.getConstraints().stream()
                        .filter(c -> c instanceof BlockConstraint || c instanceof PositionListConstraint)
                        .filter(c -> c.isPosAffected(new Vec2i(finalX, finalY)))
                        .forEach(c -> {
                            hashcode.updateAndGet(v -> 17 * v + c.hashCode());
                        });

                if (hashcode.get() != 1) {
                    int color = hashcode.get() % 360;


                    int[] rgb;
                    int[] rgbDarker;
                    if (isSelected) {
                        rgb = Utils.hslToRgb(color % 360, 0.45f, 0.3f);
                        rgbDarker = Utils.hslToRgb(color % 360, 0.45f, 0.25f);

                    } else {
                        rgb = Utils.hslToRgb(color % 360, 0.45f, 0.45f);
                        rgbDarker = Utils.hslToRgb(color % 360, 0.45f, 0.35f);
                    }

                    int[] rgbEvenDarker = Utils.hslToRgb(color % 360, 0.45f, 0.2f);

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
                            (current_symbol == null ? " " : current_symbol) + "##" + y + ":" + x,
                            gridPixelSize
                    );
                } else {
                    if (ImGui.button(
                            (grid.getSymbolAt(x, y) == null ? " " : grid.getSymbolAt(
                                    x,
                                    y
                            ).toString()) + "##" + y + ":" + x,
                            gridPixelSize
                    ) && !solving.get()) {
                        setSelection(x, y);
                        System.out.println("Selected: " + selected_pos);
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

    private void drawMultiGrid(MultiGrid mg, Vec2i gridSize, ImVec2 gridPixelSize) {
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);
        for (int y = 0; y < gridSize.getY(); y++) {
            for (int x = 0; x < gridSize.getX(); x++) {
                if (mg.isNotInGrid(new Vec2i(x, y))) {
                    ImGui.invisibleButton("##" + y + ":" + x, gridPixelSize.x, gridPixelSize.y);
                    ImGui.sameLine();
                    continue;
                }

                var isSelected = selected_pos != null && selected_pos.equals(x, y);

                var pair = mg.getGridFor(x, y);

                var padding = mg.getOffsets()[pair.getFirst()];
                int withoutPaddingX = x - padding.getX();
                int withoutPaddingY = y - padding.getY();


                AtomicInteger hashcode = new AtomicInteger(1);

                pair.getSecond().getConstraints().stream()
                        .filter(c -> c instanceof BlockConstraint || c instanceof PositionListConstraint)
                        .filter(c -> c.isPosAffected(new Vec2i(withoutPaddingX, withoutPaddingY)))
                        .forEach(c -> {
                            hashcode.updateAndGet(v -> 17 * v + c.hashCode());
                        });

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
                            (current_symbol == null ? " " : current_symbol) + "##" + y + ":" + x,
                            gridPixelSize
                    );
                } else {
                    if (ImGui.button(
                            (mg.getSymbolAt(new Vec2i(x, y)) == null ? " " : mg.getSymbolAt(new Vec2i(
                                    x,
                                    y
                            )).toString()) + "##" + y + ":" + x,
                            gridPixelSize
                    ) && !solving.get()) {
                        setSelection(x, y);
                        System.out.println("Selected: " + selected_pos);
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
            current_symbol = null;
        } else if (ImGui.isKeyPressed(ImGuiKey.Backspace)) {
            if (current_symbol != null && !current_symbol.isEmpty()) {
                current_symbol = current_symbol.substring(0, current_symbol.length() - 1);
                if (current_symbol.isEmpty()) {
                    current_symbol = null;
                }
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
        } else if (ImGui.isKeyPressed(ImGuiKey.Enter) || ImGui.isKeyPressed(ImGuiKey.KeyPadEnter)) {
            applyLastChanges();
            selected_pos = null;
        }
    }

    private void setSelection(int x, int y) {
        if (x >= 0 && x < solvable.getSize().getX() && y >= 0 && y < solvable.getSize().getY()) {
            applyLastChanges();
            selected_pos = new Vec2i(x, y);
            current_symbol = solvable.getSymbolAt(new Vec2i(x, y)) == null ? null : solvable.getSymbolAt(new Vec2i(
                    x,
                    y
            )).toString();
        }
    }

    private void applyLastChanges() {
        if (selected_pos != null) {
            solvable.placeUnchecked(selected_pos, getCurrentSymbol(), true, true);
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
