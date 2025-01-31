package fr.polytech.suuuuuuuuuuudoku;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import fr.polytech.suuuuuuuuuuudoku.algorithm.Pair;
import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec2i;
import fr.polytech.suuuuuuuuuuudoku.constraints.BlockConstraint;
import fr.polytech.suuuuuuuuuuudoku.grid.Grid;
import fr.polytech.suuuuuuuuuuudoku.grid.MultiGrid;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Tui {
    private final Terminal terminal;
    private final TextGraphics textGraphics;
    private int line = 0;
    private int usedColors = 1;

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
        List<Pair<Vec2i, Grid>> grids = new ArrayList<>();
        Vec2i[] positions = {
                new Vec2i(0, 6),
                new Vec2i(6, 0),
                new Vec2i(6, 6),
                new Vec2i(6, 12),
                new Vec2i(12, 6),
        };
        for (int i = 0; i < 5; i++) {
            var grid = CsvUtils.importGrid(Path.of("src/test/java/fr/polytech/suuuuuuuuuuudoku/resources/" +
                    "/multigrid_2/" + i + ".csv"));
            grids.add(new Pair<>(positions[i], grid));
        }
        var grid = new MultiGrid(grids);
        displayMultiGrid(grid);
//        int selectedMode = selectMode();
//        switch (selectedMode) {
//            case 0:
//                // Generer un sudoku
//                //Demander la taille du sudoku
//                int size = selectSize();
//                var grid = Generator.generateClassicNxN(size);
//                displayGrid(grid, Vec2i(0,0);
//
//
//                break;
//            case 1:
//                // Entrer un sudoku
//                break;
//        }
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
        int xPadding = padding.getX() * spacing + padding.getX();
        int yPadding = padding.getY();
        line += yPadding;

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                // Determine the block color
                int finalI = i;
                int finalJ = j;
                blocks.stream()
                      .filter(blockConstraint ->
                              finalI >= blockConstraint.getBlock().x()
                                      && finalI < blockConstraint.getBlock().dx()
                                      && finalJ >= blockConstraint.getBlock().y()
                                      && finalJ < blockConstraint.getBlock().dy())
                      .findFirst()
                      .ifPresent(blockConstraint -> textGraphics.setBackgroundColor(new TextColor.Indexed((blocks.indexOf(blockConstraint) + usedColors))));

                textGraphics.putString(j * (spacing + 1) + xPadding, line,
                        grid.getSymbolAt(j, i) == null ? " ".repeat(spacing + 1) :
                                grid.getSymbolAt(j, i) + " ".repeat(spacing));
                textGraphics.setBackgroundColor(TextColor.ANSI.DEFAULT);
            }
            line++;
        }
        terminal.flush();
        line = oldLine;
        usedColors += blocks.size();
    }

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
            System.out.println(keyStroke.getKeyType());
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
        line += 3;
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
}


