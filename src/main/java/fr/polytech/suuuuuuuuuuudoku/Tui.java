package fr.polytech.suuuuuuuuuuudoku;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import fr.polytech.suuuuuuuuuuudoku.algorithm.Generator;

import java.io.IOException;

public class Tui {
    static private int line = 0;

    static void start() throws IOException, InterruptedException {
        // generer un sudoku, entrer un sudoku
        System.setProperty("com.googlecode.lanterna.terminal.UnixTerminal.sttyCommand", "stty");
        DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
        Terminal terminal = null;
        terminal = defaultTerminalFactory.createTerminal();
        // Afficher un message de bienvenue
        terminal.setCursorVisible(false);
        terminal.clearScreen();
        terminal.flush();
        TextGraphics textGraphics = terminal.newTextGraphics();
        textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
        String welcomeMessage = "Bienvenue dans le jeu de Sudoku !";
        textGraphics.putString(0, 0, welcomeMessage);
        line += 2;
        terminal.flush();

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

        switch (selectedOption) {
            case 0:
                // Generer un sudoku
                //Demander la taille du sudoku

                textGraphics.putString(0, line, "Entrez la taille du sudoku (4, 9, 16, 25, 36, 49, 64, 81, 100)");
                line += 1;
                terminal.flush();
                int size = 0;
                do {
                    keyStroke = terminal.readInput();
                    if (keyStroke.getKeyType() == KeyType.Character) {
                        try {
                            size = Integer.parseInt(keyStroke.getCharacter().toString());
                            //clean the line
                            textGraphics.putString(0, line, " ".repeat(terminal.getTerminalSize().getColumns()));
                            textGraphics.putString(0, line, "Vous avez entré: " + size);
                        } catch (NumberFormatException e) {
                            textGraphics.putString(0, line, "Entrée invalide, veuillez entrer un nombre.");
                        }
                        terminal.flush();
                    }
                } while (size == 0);
                line += 1;

                var grid = Generator.generateClassicNxN(size);

                //display the grid
                int gridSize = grid.getGrid().getInner().length;
                int blockSize = (int) Math.sqrt(gridSize);

                for (int i = 0; i < gridSize; i++) {
                    if (i % blockSize == 0 && i != 0) {
                        textGraphics.putString(0, line++, "-".repeat(gridSize * 2 + blockSize - 1));
                    }
                    for (int j = 0; j < gridSize; j++) {
                        if (j % blockSize == 0 && j != 0) {
                            textGraphics.putString(j * 2 + j / blockSize - 1, line, "|");
                        }
                        textGraphics.putString(j * 2 + j / blockSize, line, grid.getGrid().getInner()[i][j] == null ?
                                "  " :
                                grid.getGrid().getInner()[i][j] + " ");
                    }
                    line++;
                }
                terminal.flush();
                terminal.flush();

                break;
            case 1:
                // Entrer un sudoku
                break;
        }


//        Thread.sleep(2000);
//        terminal.clearScreen();
//        terminal.close();
    }
}


