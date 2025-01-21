package fr.polytech.suuuuuuuuuuudoku;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import fr.polytech.suuuuuuuuuuudoku.algorithm.Generator;
import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec2i;
import fr.polytech.suuuuuuuuuuudoku.grid.Grid;

import java.io.IOException;

public class Tui {
    private final Terminal terminal;
    private final TextGraphics textGraphics;
    private int line = 0;

    public Tui() throws IOException {
        System.setProperty("com.googlecode.lanterna.terminal.UnixTerminal.sttyCommand", "stty");
        DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
        terminal = defaultTerminalFactory.createTerminal();
        terminal.setCursorVisible(false);
        terminal.clearScreen();
        terminal.flush();
        textGraphics = terminal.newTextGraphics();
    }

    void start() throws IOException, InterruptedException {
        welcomeMessage();
        int selectedMode = selectMode();
        switch (selectedMode) {
            case 0:
                // Generer un sudoku
                //Demander la taille du sudoku
                int size = selectSize();
                var grid = Generator.generateClassicNxN(size);
                displayGrid(grid);


                break;
            case 1:
                // Entrer un sudoku
                break;
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

    private void displayGrid(Grid grid) throws IOException {
        // Afficher la grille
        grid.display();
        int gridSize = grid.getInnerGrid().length();
        int blockSize = (int) Math.sqrt(gridSize);
        int spacing = String.valueOf(gridSize).length();

        for (int i = 0; i < gridSize; i++) {
            if (i % blockSize == 0 && i != 0) {
                textGraphics.putString(0, line++, "-".repeat(gridSize * (spacing + 1) + blockSize - 1));
            }
            for (int j = 0; j < gridSize; j++) {
                int position = j * (spacing + 1) + j / blockSize;
                if (j % blockSize == 0 && j != 0) {
                    textGraphics.putString(position - 1, line, "|");
                }

                textGraphics.putString(position, line,
                        grid.getInnerGrid().at(new Vec2i(i, j)) == null ?
                                " ".repeat(spacing + 1) :
                                grid.getInnerGrid().at(new Vec2i(i, j)) + " ".repeat(spacing));
            }
            line++;
        }
        terminal.flush();
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


