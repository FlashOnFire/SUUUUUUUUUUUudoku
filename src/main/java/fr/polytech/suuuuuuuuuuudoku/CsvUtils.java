package fr.polytech.suuuuuuuuuuudoku;

import fr.polytech.suuuuuuuuuuudoku.grid.Grid;
import fr.polytech.suuuuuuuuuuudoku.symbols.SymbolSets;

import java.io.*;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CsvUtils {

    /**
     * Creates a Grid from a CSV string.
     *
     * @param file the CSV string representing the grid
     * @return the created Grid
     */
    static public Grid importGrid(Path file) throws FileNotFoundException {
        String[][] grid = new BufferedReader(new FileReader(file.toFile()))
                .lines()
                .map(line -> Arrays.stream(line.split(",(?<!\\r)")).map(cell -> cell.equals(".") ? " " : cell)
                                   .toArray(String[]::new))
                .toArray(String[][]::new);

        var symbols = SymbolSets.generateSymbols(grid.length);
        return new Grid(grid, symbols);
    }

    /**
     * Converts the grid to a CSV string.
     *
     * @param path the path to save the CSV file to
     */
    static public void exportGrid(Path path, Grid grid) {
        var csvData = Arrays.stream(grid.getGrid().getInner())
                            .map(line -> Arrays.stream(line)
                                               .map(cell -> cell.equals(" ") ? "." : cell)
                                               .collect(Collectors.joining(",")))
                            .collect(Collectors.joining("\n"));

        try (var writer = new BufferedWriter(new FileWriter(path.toFile()))) {
            writer.write(csvData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
