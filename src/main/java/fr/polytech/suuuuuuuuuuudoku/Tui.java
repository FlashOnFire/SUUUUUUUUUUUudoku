package fr.polytech.suuuuuuuuuuudoku;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec2i;
import fr.polytech.suuuuuuuuuuudoku.constraints.BlockEqualityMGConstraint;
import fr.polytech.suuuuuuuuuuudoku.grid.Grid;
import fr.polytech.suuuuuuuuuuudoku.grid.MultiGrid;

import java.io.IOException;
import java.util.HashMap;

public class Tui {
    private final Terminal terminal;
    private final TextGraphics textGraphics;
    private int line = 0;

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
//        displayMultiGrid(MultiGrid.getExemple());
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
        HashMap<Integer, Vec2i> paddings = new HashMap<>();
        int totalHeight = 0;
        for (int i = 0; i < grid.getGrids().length; i++) {
            int finalI = i;
            BlockEqualityMGConstraint relatedConstraint =
                    grid.getConstraints().stream()
                        .filter(constraint -> constraint instanceof BlockEqualityMGConstraint)
                        .filter(constraint -> ((BlockEqualityMGConstraint) constraint).getGridIndex1() == finalI || ((BlockEqualityMGConstraint) constraint).getGridIndex2() == finalI)
                        .map(constraint -> (BlockEqualityMGConstraint) constraint)
                        .findFirst().orElseThrow();

            System.out.println("Grid " + i);
            System.out.println(relatedConstraint.getGridIndex1() == i);
//            System.out.println(relatedConstraint.getPadding());
            System.out.println(paddings);
            System.out.println(relatedConstraint.getGridIndex1());
            System.out.println(relatedConstraint.getGridIndex2());

            var padding = (relatedConstraint.getGridIndex1() == i) ? Vec2i.zero() : relatedConstraint.getPadding();

            if (paddings.containsKey(relatedConstraint.getGridIndex1())) {
                padding.add(paddings.get(relatedConstraint.getGridIndex1()));
            }
            System.out.println("line: " + line);
            System.out.println(padding);
            displayGrid(grid.getGrids()[i], padding);
            paddings.put(i, padding);
            System.out.println();
            var height = grid.getGrids()[i].length() + padding.getLine();
            if (height > totalHeight) {
                totalHeight = height;
            }
        }
    }

    private void displayGrid(Grid grid, Vec2i padding) throws IOException {
//        System.out.println(padding);
        var oldLine = line;
        // Afficher la grille
        int gridSize = grid.getInnerGrid().length();
        int blockSize = (int) Math.sqrt(gridSize);
        int spacing = String.valueOf(gridSize).length();

        // Calculate the padding which is the padding + the padding between each character + the number of blocks
        int xPadding =
                padding.getLine() * spacing + padding.getLine() + (padding.getLine() / blockSize) * (spacing);
        int yPadding =
                padding.getColumn() + (padding.getColumn() / blockSize);
        line += yPadding;

//        System.out.println("xPadding: " + xPadding);
//        System.out.println("yPadding: " + yPadding);
//        System.out.println("line: " + line);

        for (int i = 0; i < gridSize; i++) {
            if (i % blockSize == 0 && i != 0) {
                textGraphics.putString(xPadding, line++, "-".repeat(gridSize * (spacing + 1) + blockSize - 1));
            }
            for (int j = 0; j < gridSize; j++) {
                int position = j * (spacing + 1) + j / blockSize + xPadding;
                if (j % blockSize == 0 && j != 0) {
                    textGraphics.putString(position - 1, line, "|");
                }

                textGraphics.putString(position, line,
                        grid.getSymbolAt(j, i) == null ?
                                " ".repeat(spacing + 1) :
                                grid.getSymbolAt(j, i) + " ".repeat(spacing));
            }
            line++;
        }
        terminal.flush();
        line = oldLine;
//        System.out.println("line: " + line);
//        System.out.println();
    }

    private int selectMode() throws IOException {
        // Afficher un selecteur entre generer un sudoku et entrer un sudoku
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


