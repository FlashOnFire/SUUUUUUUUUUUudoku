package fr.polytech.suuuuuuuuuuudoku.graphics;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import fr.polytech.suuuuuuuuuuudoku.algorithm.Generator;
import fr.polytech.suuuuuuuuuuudoku.algorithm.SudokuSolver;
import fr.polytech.suuuuuuuuuuudoku.constraints.BlockConstraint;
import fr.polytech.suuuuuuuuuuudoku.grid.Grid;
import fr.polytech.suuuuuuuuuuudoku.grid.MultiGrid;
import fr.polytech.suuuuuuuuuuudoku.grid.Solvable;
import fr.polytech.suuuuuuuuuuudoku.grid.SymbolSets;
import fr.polytech.suuuuuuuuuuudoku.utils.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.System.exit;

/**
 * The Tui class represents a text-based user interface for the Sudoku game.
 */
public class Tui {
    /**
     * The terminal used for displaying the TUI.
     */
    private final Terminal terminal;

    /**
     * The text graphics used for drawing on the terminal.
     */
    private final TextGraphics textGraphics;

    /**
     * A map that associates grid size options with their corresponding values.
     */
    private final HashMap<Integer, Integer> mapGridSize = new HashMap<>() {{
        put(0, 4);
        put(1, 9);
        put(2, 16);
        put(3, 25);
        put(4, -1);
    }};

    /**
     * An array of grid size options.
     */
    private final String[] sizes = {"4", "9", "16", "25", "multigrid"};

    /**
     * An array of generation duration descriptions for each grid size.
     */
    private final String[] generationDurations = {
            "(mediane 4ms)",
            "(mediane 39ms)",
            "(mediane 587ms)",
            "(mediane 6s 435ms)",
            "(mediane 1s 572ms)"
    };

    /**
     * The thread used for displaying the loader animation.
     */
    private Thread loaderThread;

    /**
     * The current line position in the terminal.
     */
    private int line = 0;

    /**
     * The number of colors used for displaying blocks.
     */
    private int usedColors = 0;

    /**
     * The horizontal scroll position.
     */
    private int scrollX = 0;

    /**
     * The vertical scroll position.
     */
    private int scrollY = 0;

    /**
     * Constructs a new Tui instance and initializes the terminal.
     *
     * @throws IOException if an I/O error occurs
     */
    public Tui() throws IOException {
        System.setProperty("com.googlecode.lanterna.terminal.UnixTerminal.sttyCommand", "stty");
        DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
        defaultTerminalFactory.setInitialTerminalSize(new TerminalSize(100, 50));
        terminal = defaultTerminalFactory.createTerminal();
        terminal.setCursorVisible(false);
        terminal.clearScreen();
        terminal.flush();
        textGraphics = terminal.newTextGraphics();

    }

    /**
     * The main method to start the TUI.
     *
     * @param args Command line arguments
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the thread is interrupted
     * @throws URISyntaxException   if a URI syntax error occurs
     */
    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
        Tui tui = new Tui();
        tui.start();
    }

    /**
     * Cleans the terminal screen.
     *
     * @throws IOException if an I/O error occurs
     */
    private void cleanTerminal() throws IOException {
        line = 0;
        terminal.clearScreen();
        terminal.flush();
    }

    /**
     * Starts the Tui and handles the main game loop.
     *
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the thread is interrupted
     * @throws URISyntaxException   if a URI syntax error occurs
     */
    void start() throws IOException, InterruptedException, URISyntaxException {
        welcomeMessage();
        Solvable<?> grid;

        switch (selectMode(new String[]{"Generer un sudoku", "Entrer un sudoku", "Ouvrir un fichier"})) {
            case 0 -> { // Generate
                String[] options = IntStream.range(0, sizes.length)
                                            .mapToObj(i -> sizes[i] + " " + generationDurations[i])
                                            .toArray(String[]::new);
                int size = mapGridSize.get(selectMode(options));

                Difficulty difficulty = Difficulty.fromInt(selectMode(Difficulty.getValues()));

                startLoader();
                if (size == -1) {
                    grid = Generator.generateMultigridSudoku(difficulty).getSecond();
                } else {
                    grid = Generator.generateSudokuWithBlockConstraints((int) Math.sqrt(size), (int) Math.sqrt(size),
                            difficulty).getSecond();
                }
                stopLoader();
            }
            case 1 -> { // Enter
                int size = mapGridSize.get(selectMode(sizes));
                if (size == -1) {
                    Grid[] grids = new Grid[5];
                    for (int i = 0; i < 5; i++) {
                        grids[i] = new Grid(new Integer[9][9], SymbolSets.generateSymbols(9));
                    }
                    var paddings = MultiGrid.getRandomOffset();
                    grid = new MultiGrid(IntStream.range(0, 5).mapToObj(i -> new Pair<>(paddings[i], grids[i])).collect(Collectors.toList()));
                } else {
                    grid = new Grid(new Integer[size][size], SymbolSets.generateSymbols(size));
                }
            }
            case 2 -> { // Open a file
                // List all the files in the resources folder
                var resourceUrl = ClassLoader.getSystemResource("exemples");
                if (resourceUrl == null) {
                    textGraphics.putString(0, line, "Aucun fichier trouvé");
                    terminal.flush();
                    return;
                }

                Path path;
                if (resourceUrl.getProtocol().equals("jar")) {
                    var fileSystem = FileSystems.newFileSystem(resourceUrl.toURI(), Collections.emptyMap());
                    path = fileSystem.getPath("exemples");
                } else {
                    path = Path.of(resourceUrl.toURI());
                }
                try (var stream = Files.list(path)) {
                    Path[] files = stream.toArray(Path[]::new);
                    String[] options =
                            Arrays.stream(files).map(Path::getFileName).map(Path::toString).toArray(String[]::new);
                    int selected = selectMode(options);
                    if (Files.isDirectory(files[selected])) {
                        grid = CsvUtils.importMultiGrid("exemples/" + files[selected].getFileName());
                    } else {
                        Integer[][] t = CsvUtils.importGrid("exemples/" + files[selected].getFileName());
                        var symbols = SymbolSets.generateSymbols(t.length);
                        grid = new Grid(t, symbols);
                    }
                } catch (IOException e) {
                    System.out.println("Resource not found");
                    return;
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + selectMode(new String[]{"> Generer un " +
                    "sudoku",
                    "  Entrer un sudoku", "  Ouvrir un fichier"}));
        }
        cleanTerminal();
        showHelper();
        line += 4;
        play(grid);
        gameOver();
    }

    /**
     * Starts the loader animation in a separate thread.
     */
    private void startLoader() {
        if (loaderThread != null && loaderThread.isAlive()) {
            loaderThread.interrupt();
        }
        loaderThread = new Thread(() -> {
            try {
                String[] spinner = {"|", "/", "-", "\\"};
                int i = 0;
                synchronized (this) {
                    while (!Thread.currentThread().isInterrupted()) {
                        textGraphics.putString(0, line + 1, spinner[i % spinner.length]);
                        terminal.flush();
                        i++;
                        this.wait(100);
                    }
                }
            } catch (InterruptedException | IOException e) {
                Thread.currentThread().interrupt();
            }
        });
        loaderThread.start();
    }

    /**
     * Stops the loader animation.
     */
    private void stopLoader() {
        if (loaderThread != null) {
            loaderThread.interrupt();
        }
    }

    /**
     * Displays a welcome message on the terminal.
     *
     * @throws IOException if an I/O error occurs
     */
    private void welcomeMessage() throws IOException {
        textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
        String welcomeMessage = "Bienvenue dans le jeu de Sudoku !";
        textGraphics.putString(0, 0, welcomeMessage);
        line += 2;
        terminal.flush();
        textGraphics.setForegroundColor(TextColor.ANSI.DEFAULT);
    }

    /**
     * Solves the given Sudoku grid using selected solving methods.
     *
     * @param grid The Sudoku grid to solve
     * @return The solved Sudoku grid
     * @throws IOException if an I/O error occurs
     */
    private Solvable<?> solve(Solvable<?> grid) throws IOException {
        String[] options = {"> [ ] Deducing", "  [ ] Backtracking "};
        for (int i = 0; i < options.length; i++) {
            textGraphics.putString(0, line + i,
                    options[i] + " ".repeat(terminal.getTerminalSize().getColumns() - options[i].length()));
        }
        terminal.flush();

        boolean[] selectedOptions = {false, false};
        int selectedOption = 0;
        KeyStroke keyStroke;
        do {
            keyStroke = terminal.readInput();
            if (keyStroke.getKeyType() == KeyType.ArrowDown && selectedOption == 0) {
                selectedOption = 1;
            } else if (keyStroke.getKeyType() == KeyType.ArrowUp && selectedOption == 1) {
                selectedOption = 0;
            } else if (keyStroke.getKeyType() == KeyType.Character && keyStroke.getCharacter() == ' ') {
                selectedOptions[selectedOption] = !selectedOptions[selectedOption];
                options[selectedOption] = selectedOptions[selectedOption] ?
                        options[selectedOption].substring(0, 2) + "[X] " + options[selectedOption].substring(6) :
                        options[selectedOption].substring(0, 2) + "[ ] " + options[selectedOption].substring(6);
            }
            displayOptions(options, selectedOption);
        } while (keyStroke.getKeyType() != KeyType.Enter);

        line++;
        if (!selectedOptions[0] && !selectedOptions[1]) {
            textGraphics.putString(0, line, "Vous devez sélectionner au moins une méthode de résolution");
            terminal.flush();
            line--;
            return grid;
        }
        startLoader();
        grid.computeAllEmptyCellsPossibilities();
        if (grid instanceof MultiGrid) {
            grid = SudokuSolver.solve((MultiGrid) grid, selectedOptions[0], selectedOptions[1], true).getSecond();
        } else {
            grid = SudokuSolver.solve((Grid) grid, selectedOptions[0], selectedOptions[1], true).getSecond();
        }
        stopLoader();
        line--;

        for (int i = 0; i < options.length; i++) {
            textGraphics.putString(0, line + i, " ".repeat(options[i].length()));
        }

        return grid;
    }

    /**
     * Displays the given options and highlights the selected option.
     *
     * @param options        The options to display
     * @param selectedOption The index of the selected option
     * @throws IOException if an I/O error occurs
     */
    private void displayOptions(String[] options, int selectedOption) throws IOException {
        for (int i = 0; i < options.length; i++) {
            if (i == selectedOption) {
                options[i] = "> " + options[i].substring(2);
            } else {
                options[i] = "  " + options[i].substring(2);
            }
            textGraphics.putString(0, line + i, options[i]);
        }
        terminal.flush();
    }

    /**
     * Displays the game over message and exits the application.
     *
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the thread is interrupted
     */
    private void gameOver() throws IOException, InterruptedException {
        textGraphics.setForegroundColor(TextColor.ANSI.RED);

        textGraphics.putString(0, line, "  __  __               _   _____  _                  _          _           " +
                "    __   _ \n");
        textGraphics.setForegroundColor(TextColor.ANSI.BLUE);

        textGraphics.putString(0, line + 1, " |  \\/  |             (_) |  __ \\( )                (_)        (_)    " +
                "         /_/  | |\n");
        textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);

        textGraphics.putString(0, line + 2, " | \\  / | ___ _ __ ___ _  | |  | |/  __ ___   _____  _ _ __     _  ___ " +
                " _   _  ___  | |\n");
        textGraphics.setForegroundColor(TextColor.ANSI.GREEN);

        textGraphics.putString(0, line + 3, " | |\\/| |/ _ \\ '__/ __| | | |  | |  / _` \\ \\ / / _ \\| | '__|   | |/" +
                " _ \\| | | |/ _ \\ | |\n");
        textGraphics.setForegroundColor(TextColor.ANSI.BLUE);

        textGraphics.putString(0, line + 4, " | |  | |  __/ | | (__| | | |__| | | (_| |\\ V / (_) | | |      | | (_) " +
                "| |_| |  __/ |_|\n");
        textGraphics.setForegroundColor(TextColor.ANSI.MAGENTA);

        textGraphics.putString(0, line + 5, " |_|  |_|\\___|_|  \\___|_| |_____/   \\__,_| \\_/ \\___/|_|_|      | " +
                "|\\___/ \\__,_|\\___| (_)\n");
        textGraphics.setForegroundColor(TextColor.ANSI.CYAN);

        textGraphics.putString(0, line + 6, "                                                              _/ |      " +
                "               \n");
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);

        textGraphics.putString(0, line + 7, "                                                             |__/       " +
                "               \n");
        terminal.flush();

        // Sleep for 3 seconds
        Thread.sleep(2000);
        exit(0);
    }

    /**
     * Displays a multi-grid Sudoku on the terminal.
     *
     * @param grid The MultiGrid object containing multiple Sudoku grids.
     * @throws IOException if an I/O error occurs
     */
    private void displayMultiGrid(MultiGrid grid) throws IOException {
        for (int i = 0; i < grid.getGrids().length; i++) {
            displayGrid(grid.getGrids()[i], grid.getOffsets()[i]);
        }
    }

    /**
     * Displays the given Sudoku grid on the terminal with the specified padding.
     *
     * @param grid    The Sudoku grid to display.
     * @param padding The padding to apply around the grid.
     * @throws IOException if an I/O error occurs
     */
    private void displayGrid(Grid grid, Vec2i padding) throws IOException {
        int gridSize = grid.getInnerGrid().length();
        int spacing = String.valueOf(gridSize).length();
        List<BlockConstraint> blocks = grid.getConstraints().stream()
                                           .filter(BlockConstraint.class::isInstance)
                                           .map(BlockConstraint.class::cast)
                                           .toList();

        // Calculate the padding which is the padding + the padding between each character + the number of blocks
        int xPadding = padding.getX() * (spacing + 1);
        int yPadding = padding.getY();
        line += yPadding;

        for (int i = scrollY; i < scrollY + Math.min((terminal.getTerminalSize().getRows() - (line)), gridSize);
             i++) {
            for (int j = scrollX; j < scrollX + Math.min((terminal.getTerminalSize().getColumns() / (spacing + 1)),
                    gridSize); j++) {
                // Determine the block color
                int finalI = i;
                int finalJ = j;
                var blockColor = blocks.stream()
                                       .filter(blockConstraint ->
                                               finalI >= blockConstraint.getBlock().x()
                                                       && finalI < blockConstraint.getBlock().dx()
                                                       && finalJ >= blockConstraint.getBlock().y()
                                                       && finalJ < blockConstraint.getBlock().dy())
                                       .findFirst()
                                       .map(blockConstraint -> {
                                           int blockIndex = blocks.indexOf(blockConstraint) + usedColors;
                                           float hue = (blockIndex * 50) % 360; // Hue value between 0 and 360
                                           float saturation = 1.0f; // Saturation value between 0 and 1
                                           float lightness = 0.25f; // Lightness value between 0 and 1

                                           // Convert HSL to RGB
                                           int[] rgb = Utils.hslToRgb(hue, saturation, lightness);
                                           return new TextColor.RGB(rgb[0], rgb[1], rgb[2]);
                                       });

                blockColor.ifPresent(textGraphics::setBackgroundColor);

                // display the symbol at the right place
                textGraphics.putString((j - scrollX) * (spacing + 1) + xPadding, line + (i - scrollY),
                        // Theirs no value just display the spacing
                        grid.getSymbolAt(finalJ, finalI) == null ? " ".repeat(spacing + 1) :
                                // Display the value with the right spacing (taking care of the end of the line)
                                grid.getSymbolAt(finalJ, finalI) + " ".repeat(spacing - grid.getSymbolAt(finalJ,
                                        finalI).toString().length() + 1));
                textGraphics.setBackgroundColor(TextColor.ANSI.DEFAULT);
            }
        }
        line -= yPadding;
        usedColors += blocks.size();
    }

    /**
     * Allows playing Sudoku using the terminal for user inputs.
     *
     * @param grid The Sudoku grid to solve.
     * @throws IOException If an I/O error occurs.
     */
    private void play(Solvable<?> grid) throws IOException {
        Vec2i position = Vec2i.zero();
        Vec2i lastPosition = Vec2i.zero();
        KeyStroke keyStroke;
        boolean disco = false;
        String enteredValue = null;
        Vec2i max;
        int spacing;
        int timeLinePos = 0;
        boolean enableScrollX = false;
        boolean enableScrollY = false;


        if (grid instanceof MultiGrid) {
            spacing = String.valueOf(((MultiGrid) grid).getGrids()[0].getSymbols().size()).length();
        } else {
            spacing = String.valueOf(grid.getSymbols().size()).length();
        }
        max = grid.getSize();

        if (terminal.getTerminalSize().getRows() / (spacing + 1) < grid.getSize().getX()) {
            enableScrollX = true;
        }
        if (terminal.getTerminalSize().getColumns() / (spacing + 1) < grid.getSize().getY()) {
            enableScrollY = true;
        }

        do {
            if (!disco) {
                usedColors = 0;
            }

            if (grid instanceof MultiGrid) {
                displayMultiGrid((MultiGrid) grid);
                if (((MultiGrid) grid).isNotInGrid(lastPosition)) {
                    // Clear the last position
                    textGraphics.setBackgroundColor(TextColor.ANSI.DEFAULT);
                    textGraphics.putString(lastPosition.getX() * (spacing + 1), lastPosition.getY() + line,
                            " ".repeat(spacing));
                }
            } else {
                displayGrid((Grid) grid, Vec2i.zero());
            }
            textGraphics.setBackgroundColor(TextColor.ANSI.BLACK_BRIGHT);
            textGraphics.putString((position.getX() - scrollX) * (spacing + 1), position.getY() + line - scrollY,
                    grid.getSymbolAt(new Vec2i(position.getX(),
                            position.getY())) == null ? " ".repeat(spacing) :
                            grid.getSymbolAt(new Vec2i(position.getX(),
                                    position.getY())).toString());

            textGraphics.setBackgroundColor(TextColor.ANSI.DEFAULT);
            String displayValue = "Valeur entrée: " + (enteredValue != null ? enteredValue : "");
            int padding = terminal.getTerminalSize().getColumns() - 15 - displayValue.length();
            textGraphics.putString(0, line - 1, displayValue + " ".repeat(padding));

            line -= 5;
            List<Move2i> moveList = grid.getMoves();
            if (!moveList.isEmpty()) {
                showTimeLine(moveList.size(), timeLinePos);
            }
            line += 5;
            terminal.flush();

            lastPosition = new Vec2i(position.getX(), position.getY());
            keyStroke = terminal.readInput();

            if (keyStroke.getKeyType() == KeyType.ArrowDown) {
                if (position.getY() < max.getY() - 1) {

                    if (enableScrollY &&
                            position.getY() >= (terminal.getTerminalSize().getRows() - line) / 2
                            && scrollY < max.getY() - (terminal.getTerminalSize().getRows() - line)) {
                        scrollY++;
                    }
                    position = position.add(new Vec2i(0, 1));
                }
            } else if (keyStroke.getKeyType() == KeyType.ArrowUp) {
                if (position.getY() > 0) {
                    if (enableScrollY
                            && scrollY > 0
                            && (position.getY() / scrollY) <= terminal.getTerminalSize().getRows() / (spacing) / 2) {
                        scrollY--;
                    }
                    position = position.add(new Vec2i(0, -1));
                }
            } else if (keyStroke.getKeyType() == KeyType.ArrowLeft) {
                if (position.getX() > 0) {
                    if (enableScrollX
                            && scrollX > 0
                            && (position.getX() / scrollX) <= terminal.getTerminalSize().getColumns() / (spacing + 1) / 2) {
                        scrollX--;
                    }
                    position = position.add(new Vec2i(-1, 0));
                }
            } else if (keyStroke.getKeyType() == KeyType.ArrowRight) {
                if (position.getX() < max.getX() - 1) {
                    if (enableScrollX
                            && position.getX() >= terminal.getTerminalSize().getColumns() / (spacing + 1) / 2
                            && scrollX < max.getX() - terminal.getTerminalSize().getColumns() / (spacing + 1)) {
                        scrollX++;
                    }
                    position = position.add(new Vec2i(1, 0));
                }
            } else if (keyStroke.getKeyType() == KeyType.Enter && enteredValue != null) {
                grid.placeUnchecked(new Vec2i(position), Integer.parseInt(enteredValue), true, true);
                timeLinePos = -1;
                enteredValue = null;
            } else if (keyStroke.getKeyType() == KeyType.Delete) {
                grid.placeUnchecked(position, null, true, true);
                enteredValue = null;
            } else if (keyStroke.getKeyType() == KeyType.Backspace && enteredValue != null) {
                enteredValue = enteredValue.substring(0, enteredValue.length() - 1);
            } else if (keyStroke.getKeyType() == KeyType.PageUp && (timeLinePos > 0 || timeLinePos == -1)) {
                if (timeLinePos == -1) {
                    timeLinePos = moveList.size();
                }
                timeLinePos--;
                grid.placeUnchecked(moveList.get(timeLinePos).position(),
                        moveList.get(timeLinePos).previous_value(), true, false);
            } else if (keyStroke.getKeyType() == KeyType.PageDown && timeLinePos < moveList.size()) {
                grid.placeUnchecked(moveList.get(timeLinePos).position(), moveList.get(timeLinePos).value(), true
                        , false);
                timeLinePos++;
            } else if (keyStroke.getKeyType() == KeyType.Character) {

                if (Character.isDigit(keyStroke.getCharacter())) {
                    enteredValue = enteredValue == null ? String.valueOf(keyStroke.getCharacter()) :
                            enteredValue + keyStroke.getCharacter();
                } else {
                    switch (keyStroke.getCharacter()) {
                        case 'd' -> disco = !disco;
                        case 's' -> {
                            line -= 3;
                            var solved = solve(grid);
                            if (solved == null || !solved.isSolved()) {
                                textGraphics.setBackgroundColor(TextColor.ANSI.RED);
                                textGraphics.putString(0, line, "La grille n'a pas pu être résolue");
                                textGraphics.setBackgroundColor(TextColor.ANSI.DEFAULT);
                            } else {
                                grid = solved;
                            }
                            line += 3;
                            timeLinePos = -1;
                        }
                        case 'q' -> exit(0);
                    }
                }
            }
        } while (keyStroke.getKeyType() != KeyType.Escape);

        line += max.getY() + 1;
    }


    /**
     * Displays the user interface instructions.
     *
     * @throws IOException if an I/O error occurs
     */
    private void showHelper() throws IOException {
        textGraphics.putString(0, line, "Utilisez les touches fléchées pour vous déplacer dans la grille");
        textGraphics.putString(0, line + 1, "Utilisez les touches numériques pour entrer une valeur");
        textGraphics.putString(0, line + 2, "Utilisez la touche 'Entrée' pour valider une valeur");
        textGraphics.putString(0, line + 3, "Utilisez la touche 'Suppr' pour effacer une valeur");
        textGraphics.putString(0, line + 4, "Utilisez la touche 'Page Up' pour revenir en arrière");
        textGraphics.putString(0, line + 5, "Utilisez la touche 'Page Down' pour avancer dans le temps");
        textGraphics.putString(0, line + 6, "Utilisez la touche 'Espacement' pour activer/désactiver des options " +
                "(solver)");
        textGraphics.putString(0, line + 7, "Utilisez la touche 'd' pour activer/désactiver le mode disco");
        textGraphics.putString(0, line + 8, "Utilisez la touche 's' pour résoudre la grille");
        textGraphics.putString(0, line + 9, "Utilisez la touche 'q' pour quitter");
        line += 10;
        terminal.flush();
    }

    /**
     * Displays a timeline of moves made in the game.
     *
     * @param size        The total number of moves.
     * @param timeLinePos The current position in the timeline.
     */
    private void showTimeLine(int size, int timeLinePos) {
        // display something like <-----|-->
        textGraphics.putString(0, line + 1, "<");
        textGraphics.putString(1, line + 1, "-".repeat(size + 2));
        textGraphics.putString(size + 2, line + 1, ">");
        if (timeLinePos != -1) {
            textGraphics.putString(timeLinePos + 1, line + 1, "|");
        } else {
            textGraphics.putString(size + 1, line + 1, "|");
        }
    }

    /**
     * Displays the game mode options and allows the user to select an option.
     *
     * @return the option selected by the user (0 to generate a sudoku, 1 to enter a sudoku)
     * @throws IOException if an I/O error occurs
     */
    private int selectMode(String[] options) throws IOException {
        //add double space to the first option
        for (int i = 0; i < options.length; i++) {
            options[i] = "  " + options[i];
        }
        options[0] = "> " + options[0].substring(2);

        for (int i = 0; i < options.length; i++) {
            textGraphics.putString(0, line + i, options[i]);
        }
        terminal.flush();

        // Attendre l'input de l'utilisateur
        int selectedOption = 0;
        KeyStroke keyStroke;
        do {
            keyStroke = terminal.readInput();
            if (keyStroke.getKeyType() == KeyType.ArrowDown && selectedOption < options.length - 1) {
                selectedOption++;
            } else if (keyStroke.getKeyType() == KeyType.ArrowUp && selectedOption > 0) {
                selectedOption--;
            }
            displayOptions(options, selectedOption);
        } while (keyStroke.getKeyType() != KeyType.Enter);
        line += options.length + 1;
        return selectedOption;
    }
}


