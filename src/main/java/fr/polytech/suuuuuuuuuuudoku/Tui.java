package fr.polytech.suuuuuuuuuuudoku;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import fr.polytech.suuuuuuuuuuudoku.algorithm.Generator;
import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec2i;
import fr.polytech.suuuuuuuuuuudoku.constraints.BlockConstraint;
import fr.polytech.suuuuuuuuuuudoku.grid.Grid;
import fr.polytech.suuuuuuuuuuudoku.grid.MultiGrid;
import fr.polytech.suuuuuuuuuuudoku.grid.Solvable;

import java.io.IOException;
import java.util.List;

import static java.lang.System.exit;

public class Tui {
    private final Terminal terminal;
    private final TextGraphics textGraphics;
    private int line = 0;
    private int usedColors = 0;
    private Thread loaderThread;


    public Tui() throws IOException {
        System.setProperty("com.googlecode.lanterna.terminal.UnixTerminal.sttyCommand", "stty");
        DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
        defaultTerminalFactory.setInitialTerminalSize(new TerminalSize(100, 50));
        terminal = defaultTerminalFactory.createTerminal();
        terminal.setCursorVisible(false);
        terminal.clearScreen();
        terminal.flush();
        textGraphics = terminal.newTextGraphics();
        loaderThread = new Thread(() -> {
            try {
                String[] spinner = {"|", "/", "-", "\\"};
                int i = 0;
                while (!Thread.currentThread().isInterrupted()) {
                    textGraphics.putString(0, line + 1, spinner[i % spinner.length]);
                    terminal.flush();
                    i++;
                    Thread.sleep(100);
                }
            } catch (InterruptedException | IOException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    void start() throws IOException, InterruptedException {
        welcomeMessage();
        Grid grid = null;
        switch (selectMode()) {
            case 0 -> { // Generate
//                List<Pair<Vec2i, Grid>> grids = new ArrayList<>();
//                Vec2i[] positions = {
//                        new Vec2i(0, 6),
//                        new Vec2i(6, 0),
//                        new Vec2i(6, 6),
//                        new Vec2i(6, 12),
//                        new Vec2i(12, 6),
//                };
//                for (int i = 0; i < 5; i++) {
//                    var gridd = CsvUtils.importGrid(Path.of("src/test/java/fr/polytech/suuuuuuuuuuudoku/resources/" +
//                            "/multigrid_2/" + i + ".csv"));
//                    grids.add(new Pair<>(positions[i], gridd));
//                }
//                var gridd = new MultiGrid(grids);
//                displayMultiGrid(gridd);
                int size = selectSize();
                loaderThread.start();
                grid = Generator.generateClassicNxN(size);
                loaderThread.interrupt();
                displayGrid(grid, Vec2i.zero());
            }
            case 1 -> { // Enter sudoku
                exit(0);
            }
        }
        play(grid);

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

    private void displayMultiGrid(MultiGrid grid) throws IOException {
        for (int i = 0; i < grid.getGrids().length; i++) {
            displayGrid(grid.getGrids()[i], grid.getPaddings()[i]);
        }
    }

    private void displayGrid(Grid grid, Vec2i padding) throws IOException {
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
        terminal.flush();
        line = oldLine;
        usedColors += blocks.size();
    }

    /**
     * Permet de jouer au Sudoku en utilisant le terminal pour les entrées utilisateur.
     *
     * @param grid La grille de Sudoku à résoudre.
     * @throws IOException Si une erreur d'entrée/sortie se produit.
     */
    private void play(Solvable grid) throws IOException {
        Vec2i position = Vec2i.zero();
        KeyStroke keyStroke;
        boolean disco = false;
        String enteredValue = null;
        Vec2i max;
        if (grid instanceof MultiGrid) {
            max = ((MultiGrid) grid).getSize();
        } else {
            max = ((Grid) grid).getZize();
        }
        int spacing = String.valueOf(Math.max(max.getY(), max.getY())).length();

        do {
            if (!disco) {
                usedColors = 0;
            }

            if (grid instanceof MultiGrid) {
                displayMultiGrid((MultiGrid) grid);
                textGraphics.setBackgroundColor(TextColor.ANSI.BLACK_BRIGHT);
                textGraphics.putString(position.getX() * 2, position.getY() + line, " ".repeat(spacing));
            } else {
                displayGrid((Grid) grid, Vec2i.zero());
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
            terminal.flush();

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
                    grid.placeUnchecked(position, Integer.parseInt(enteredValue), true, true);
                }
                enteredValue = null;
            } else if (keyStroke.getKeyType() == KeyType.Backspace && enteredValue != null) {
                enteredValue = enteredValue.substring(0, enteredValue.length() - 1);
            } else if (keyStroke.getKeyType() == KeyType.Character) {
                if (keyStroke.getCharacter() == 'd') {
                    disco = !disco;
                } else if (Character.isDigit(keyStroke.getCharacter())) {
                    enteredValue = enteredValue == null ? String.valueOf(keyStroke.getCharacter()) :
                            enteredValue + keyStroke.getCharacter();
                }
            }

        } while (keyStroke.getKeyType() != KeyType.Escape && !grid.isSolved());
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
            for (int i = 0; i < options.length; i++) {
                if (i == selectedOption) {
                    options[i] = "> " + options[i].substring(2);
                } else {
                    options[i] = "  " + options[i].substring(2);
                }
                textGraphics.putString(0, line + i, options[i]);
            }
            terminal.flush();
        } while (keyStroke.getKeyType() != KeyType.Enter);
        line += options.length + 1;
        return selectedOption;
    }

    private int selectSize() throws IOException {
        int selectedSize = 0;
        int[] possibleSizes = {4, 9, 16, 25, 36, 49, 64, 81, 100};
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


