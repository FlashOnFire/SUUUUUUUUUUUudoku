package fr.polytech.suuuuuuuuuuudoku;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import fr.polytech.suuuuuuuuuuudoku.algorithm.Generator;
import fr.polytech.suuuuuuuuuuudoku.algorithm.SudokuSolver;
import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec2i;
import fr.polytech.suuuuuuuuuuudoku.constraints.BlockConstraint;
import fr.polytech.suuuuuuuuuuudoku.grid.*;
import fr.polytech.suuuuuuuuuuudoku.symbols.SymbolSets;

import java.io.IOException;
import java.util.List;

import static java.lang.System.exit;

public class Tui {
    private final Terminal terminal;
    private final TextGraphics textGraphics;
    private Thread loaderThread;
    private int line = 0;
    private int usedColors = 0;


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

    void start() throws IOException, InterruptedException {
        welcomeMessage();
        Grid grid = null;
        switch (selectMode()) {
            case 0 -> { // Generate
//                List<Pair<Vec2i, Grid>> grids = new ArrayList<>();
//                Vec2i[] positions = {
//                        new Vec2i(6, 0),
//                        new Vec2i(0, 6),
//                        new Vec2i(6, 6),
//                        new Vec2i(12, 6),
//                        new Vec2i(6, 12),
//                };
//                for (int i = 0; i < 5; i++) {
//                    var gridd = CsvUtils.importGrid(Path.of("src/test/java/fr/polytech/suuuuuuuuuuudoku/resources/" +
//                            "/multigrid_2/" + i + ".csv"));
//                    grids.add(new Pair<>(positions[i], gridd));
//                }
//                grid = new MultiGrid(grids);
                int size = selectSize();
                startLoader();
                grid = Generator.generateClassicSudoku(size);
                stopLoader();
            }
            case 1 -> {
                int size = selectSize();
                grid = new Grid(new Integer[size][size], SymbolSets.generateSymbols(size));
            }
        }
        play(grid);
        gameOver();

    }

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

    private void stopLoader() {
        if (loaderThread != null) {
            loaderThread.interrupt();
        }
    }

    private void welcomeMessage() throws IOException {
        // Afficher un message de bienvenue
        textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
        String welcomeMessage = "Bienvenue dans le jeu de Sudoku !";
        textGraphics.putString(0, 0, welcomeMessage);
        line += 2;
        terminal.flush();
        textGraphics.setForegroundColor(TextColor.ANSI.DEFAULT);
    }

    private Solvable<?> solve(Solvable<?> grid) throws IOException {
        // Show a selector to choose the solving methods
        String[] options = {"> [ ] Deducing", "  [ ] Backtracking "};
        for (int i = 0; i < options.length; i++) {
            textGraphics.putString(0, line + i,
                    options[i] + " ".repeat(terminal.getTerminalSize().getColumns() - options[i].length()));
        }
        terminal.flush();

        // Wait for the user input
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
        //check that at least one option is selected
        if (!selectedOptions[0] && !selectedOptions[1]) {
            textGraphics.putString(0, line, "Vous devez sélectionner au moins une méthode de résolution");
            terminal.flush();
            line--;
            return grid;
        }
        startLoader();
        if (grid instanceof MultiGrid) {
            grid = SudokuSolver.solve((MultiGrid) grid, selectedOptions[0], selectedOptions[1], true).getSecond();
        } else {
            grid = SudokuSolver.solve((Grid) grid, selectedOptions[0], selectedOptions[1], true).getSecond();
        }
        stopLoader();
        line--;

        // clean the selector
        for (int i = 0; i < options.length; i++) {
            textGraphics.putString(0, line + i, " ".repeat(options[i].length()));
        }

        return grid;
    }

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

    private void displayMultiGrid(MultiGrid grid, boolean flush) throws IOException {
        for (int i = 0; i < grid.getGrids().length; i++) {
            displayGrid(grid.getGrids()[i], grid.getPaddings()[i], false);
        }
        if (flush) {
            terminal.flush();
        }
    }

    private void displayGrid(Grid grid, Vec2i padding, boolean flush) throws IOException {
        var oldLine = line;

        // Afficher la grille
        int gridSize = grid.getInnerGrid().length();
        int spacing = String.valueOf(gridSize).length(); // Calculate the spacing between each character
        List<BlockConstraint> blocks = grid.getConstraints().stream()
                                           .filter(BlockConstraint.class::isInstance)
                                           .map(BlockConstraint.class::cast)
                                           .toList();

        // Calculate the padding which is the padding + the padding between each character + the number of blocks
        int xPadding = padding.getX() * (spacing + 1);
        int yPadding = padding.getY();
        line += yPadding;

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
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
                                           int[] rgb = hslToRgb(hue, saturation, lightness);
                                           return new TextColor.RGB(rgb[0], rgb[1], rgb[2]);
                                       });

                blockColor.ifPresent(textGraphics::setBackgroundColor);

                // display the symbol at the right place
                textGraphics.putString(finalJ * (spacing + 1) + xPadding, line,
                        // Theirs no value just display the spacing
                        grid.getSymbolAt(finalJ, finalI) == null ? " ".repeat(spacing + 1) :
                                // Display the value with the right spacing (taking care of the end of the line)
                                grid.getSymbolAt(finalJ, finalI) + " ".repeat(spacing - grid.getSymbolAt(finalJ,
                                        finalI).toString().length() + 1));
                textGraphics.setBackgroundColor(TextColor.ANSI.DEFAULT);
            }
            line++;
        }
        if (flush) {
            terminal.flush();
        }
        line = oldLine;
        usedColors += blocks.size();
    }

    /**
     * Permet de jouer au Sudoku en utilisant le terminal pour les entrées utilisateur.
     *
     * @param grid La grille de Sudoku à résoudre.
     * @throws IOException Si une erreur d'entrée/sortie se produit.
     */
    private void play(Solvable<?> grid) throws IOException {
        Vec2i position = Vec2i.zero();
        Vec2i lastPosition = Vec2i.zero();
        KeyStroke keyStroke;
        boolean disco = false;
        String enteredValue = null;
        Vec2i max;
        int spacing;
        int timeLinePos = -1;

        if (grid instanceof MultiGrid) {
            spacing = String.valueOf(((MultiGrid) grid).getGrids()[0].getSymbols().size()).length();
            max = ((MultiGrid) grid).getSize();
        } else {
            spacing = String.valueOf(((Grid) grid).getSymbols().size()).length();
            max = ((Grid) grid).getSize();
        }

        if (grid.isSolved()) {
            return;
        }

        do {
            if (!disco) {
                usedColors = 0;
            }

            if (grid instanceof MultiGrid) {
                displayMultiGrid((MultiGrid) grid, false);
                if (!((MultiGrid) grid).isInGrid(lastPosition)) {
                    // Clear the last position
                    textGraphics.setBackgroundColor(TextColor.ANSI.DEFAULT);
                    textGraphics.putString(lastPosition.getX() * (spacing + 1), lastPosition.getY() + line,
                            " ".repeat(spacing));
                }
                textGraphics.setBackgroundColor(TextColor.ANSI.BLACK_BRIGHT);
                textGraphics.putString(position.getX() * (spacing + 1), position.getY() + line,
                        ((MultiGrid) grid).getSymbolAtPaddingBased(new Vec2i(position.getX(),
                                position.getY())) == null ? " ".repeat(spacing) :
                                ((MultiGrid) grid).getSymbolAtPaddingBased(new Vec2i(position.getX(),
                                        position.getY())).toString());
            } else {
                displayGrid((Grid) grid, Vec2i.zero(), false);
                textGraphics.setBackgroundColor(TextColor.ANSI.BLACK_BRIGHT);
                textGraphics.putString(position.getX() * (spacing + 1), position.getY() + line,
                        ((Grid) grid).getSymbolAt(position.getX(),
                                position.getY()) == null ? " ".repeat(spacing) :
                                ((Grid) grid).getSymbolAt(position.getX(), position.getY()).toString());
            }

            // Afficher la valeur entrée sur la grille
            textGraphics.setBackgroundColor(TextColor.ANSI.DEFAULT);
            String displayValue = "Valeur entrée: " + (enteredValue != null ? enteredValue : "");
            int padding = terminal.getTerminalSize().getColumns() - 15 - displayValue.length();
            textGraphics.putString(0, line - 1, displayValue + " ".repeat(padding));

            // display move navigator
            line += max.getY() + 1;
            List<?> moveList;
            if (grid instanceof MultiGrid) {
                moveList = ((MultiGrid) grid).getMoves();
            } else {
                moveList = ((Grid) grid).getMoves();
            }
            if (!moveList.isEmpty()) {
                showTimeLine(moveList.size(), timeLinePos);
            }
            line -= max.getY() + 1;
            terminal.flush();

            lastPosition = new Vec2i(position.getX(), position.getY());
            keyStroke = terminal.readInput();

            if (keyStroke.getKeyType() == KeyType.ArrowDown && position.getY() < max.getY() - 1) {
                position = position.add(new Vec2i(0, 1));
            } else if (keyStroke.getKeyType() == KeyType.ArrowUp && position.getY() > 0) {
                position = position.add(new Vec2i(0, -1));
            } else if (keyStroke.getKeyType() == KeyType.ArrowLeft && position.getX() > 0) {
                position = position.add(new Vec2i(-1, 0));
            } else if (keyStroke.getKeyType() == KeyType.ArrowRight && position.getX() < max.getX() - 1) {
                position = position.add(new Vec2i(1, 0));
            } else if (keyStroke.getKeyType() == KeyType.Enter && enteredValue != null) {
                if (grid instanceof MultiGrid) {
                    ((MultiGrid) grid).placeUncheckedPaddingBased(position, Integer.parseInt(enteredValue), true, true);
                } else {
                    ((Grid) grid).placeUnchecked(position, Integer.parseInt(enteredValue), true, true);
                }
                enteredValue = null;
            } else if (keyStroke.getKeyType() == KeyType.Delete) {
                if (grid instanceof MultiGrid) {
                    ((MultiGrid) grid).placeUncheckedPaddingBased(position, null, true, true);
                } else {
                    ((Grid) grid).placeUnchecked(position, null, true, true);
                }
                enteredValue = null;
            } else if (keyStroke.getKeyType() == KeyType.Backspace && enteredValue != null) {
                enteredValue = enteredValue.substring(0, enteredValue.length() - 1);
            } else if (keyStroke.getKeyType() == KeyType.PageUp && (timeLinePos > 0 || timeLinePos == -1)) {
                if (timeLinePos == -1) {
                    timeLinePos = moveList.size() - 1;
                }
                timeLinePos--;
                if (moveList.get(timeLinePos) instanceof Move2i) {
                    var move = (Move2i) moveList.get(timeLinePos);
                    ((Grid) grid).placeUnchecked(move.position(), move.previous_value(), true, false);
                } else if (moveList.get(timeLinePos) instanceof Move3i) {
                    var move = (Move3i) moveList.get(timeLinePos);
                    ((MultiGrid) grid).placeUnchecked(move.position(), move.previous_value(), true, false);
                }

            } else if (keyStroke.getKeyType() == KeyType.PageDown && timeLinePos < moveList.size() - 1) {
                timeLinePos++;
                if (moveList.get(timeLinePos) instanceof Move2i) {
                    var move = (Move2i) moveList.get(timeLinePos);
                    ((Grid) grid).placeUnchecked(move.position(), move.value(), true, false);
                } else if (moveList.get(timeLinePos) instanceof Move3i) {
                    var move = (Move3i) moveList.get(timeLinePos);
                    ((MultiGrid) grid).placeUnchecked(move.position(), move.value(), true, false);
                }

            } else if (keyStroke.getKeyType() == KeyType.Character) {

                if (Character.isDigit(keyStroke.getCharacter())) {
                    enteredValue = enteredValue == null ? String.valueOf(keyStroke.getCharacter()) :
                            enteredValue + keyStroke.getCharacter();
                } else {
                    switch (keyStroke.getCharacter()) {
                        case 'd' -> disco = !disco;
                        case 's' -> {
                            line += max.getY() + 1;
                            grid = solve(grid);
                            if (!grid.isSolved()) {
                                //display a message
                                textGraphics.setBackgroundColor(TextColor.ANSI.RED);
                                textGraphics.putString(0, line, "La grille n'a pas pu être résolue");
                                textGraphics.setBackgroundColor(TextColor.ANSI.DEFAULT);
                            }
                            line -= max.getY() + 1;

                        }
                        case 'q' -> exit(0);
                    }
                }
            }


        } while (keyStroke.getKeyType() != KeyType.Escape);

        line += max.getY() + 1;
    }

    private void showTimeLine(int size, int timeLinePos) {
        // display something like <-----|-->
        textGraphics.putString(0, line + 1, "<");
        textGraphics.putString(1, line + 1, "-".repeat(size));
        textGraphics.putString(size + 1, line + 1, ">");
        if (timeLinePos != -1) {
            textGraphics.putString(timeLinePos + 1, line + 1, "|");
        } else {
            textGraphics.putString(size, line + 1, "|");
        }
    }

    /**
     * Affiche les options de mode de jeu et permet à l'utilisateur de sélectionner une option.
     *
     * @return l'option sélectionnée par l'utilisateur (0 pour générer un sudoku, 1 pour entrer un sudoku)
     * @throws IOException si une erreur d'entrée/sortie se produit
     */
    private int selectMode() throws IOException {
        String[] options = {"> Generer un sudoku", "  Entrer un sudoku"};
        for (int i = 0; i < options.length; i++) {
            textGraphics.putString(0, line + i, options[i]);
        }
        terminal.flush();

        // Attendre l'input de l'utilisateur
        int selectedOption = 0;
        KeyStroke keyStroke;
        do {
            keyStroke = terminal.readInput();
            if (keyStroke.getKeyType() == KeyType.ArrowDown && selectedOption == 0) {
                selectedOption = 1;
            } else if (keyStroke.getKeyType() == KeyType.ArrowUp && selectedOption == 1) {
                selectedOption = 0;
            }
            displayOptions(options, selectedOption);
        } while (keyStroke.getKeyType() != KeyType.Enter);
        line += options.length + 1;
        return selectedOption;
    }

    private int selectSize() throws IOException {
        int selectedSize = 0;
        int[] possibleSizes = {4, 9, 16};
        textGraphics.putString(0, line, "Entrez la taille du sudoku");

        // Afficher un selecteur pour les tailles possibles
        StringBuilder sizes = new StringBuilder();
        displaySizes(selectedSize, possibleSizes, sizes);

        // Attendre l'input de l'utilisateur
        KeyStroke keyStroke;
        do {
            keyStroke = terminal.readInput();
            if (keyStroke.getKeyType() == KeyType.ArrowLeft) {
                if (selectedSize == 0) {
                    selectedSize = possibleSizes.length - 1;
                } else {
                    selectedSize--;
                }
            } else if (keyStroke.getKeyType() == KeyType.ArrowRight) {
                if (selectedSize == possibleSizes.length - 1) {
                    selectedSize = 0;
                } else {
                    selectedSize++;
                }
            }

            sizes = new StringBuilder();
            displaySizes(selectedSize, possibleSizes, sizes);
        } while (keyStroke.getKeyType() != KeyType.Enter);
        line += 4;
        return possibleSizes[selectedSize];
    }

    private void displaySizes(int selectedSize, int[] possibleSizes, StringBuilder sizes) throws IOException {
        int padding = 0;
        for (int i = 0; i < possibleSizes.length; i++) {
            if (i == selectedSize) {
                textGraphics.setBackgroundColor(TextColor.ANSI.YELLOW);
                textGraphics.putString(padding, line + 1, String.valueOf(possibleSizes[i]));
                textGraphics.setBackgroundColor(TextColor.ANSI.DEFAULT);
            } else {
                textGraphics.putString(padding, line + 1, String.valueOf(possibleSizes[i]));
            }
            padding += String.valueOf(possibleSizes[i]).length() + 1;
        }
        terminal.flush();
    }

    // Function to convert HSL to RGB
    private int[] hslToRgb(float h, float s, float l) {
        float c = (1 - Math.abs(2 * l - 1)) * s;
        float x = c * (1 - Math.abs((h / 60) % 2 - 1));
        float m = l - c / 2;
        float r = 0, g = 0, b = 0;

        if (0 <= h && h < 60) {
            r = c;
            g = x;
            b = 0;
        } else if (60 <= h && h < 120) {
            r = x;
            g = c;
            b = 0;
        } else if (120 <= h && h < 180) {
            r = 0;
            g = c;
            b = x;
        } else if (180 <= h && h < 240) {
            r = 0;
            g = x;
            b = c;
        } else if (240 <= h && h < 300) {
            r = x;
            g = 0;
            b = c;
        } else if (300 <= h && h < 360) {
            r = c;
            g = 0;
            b = x;
        }

        int[] rgb = new int[3];
        rgb[0] = Math.round((r + m) * 255);
        rgb[1] = Math.round((g + m) * 255);
        rgb[2] = Math.round((b + m) * 255);
        return rgb;
    }
}


