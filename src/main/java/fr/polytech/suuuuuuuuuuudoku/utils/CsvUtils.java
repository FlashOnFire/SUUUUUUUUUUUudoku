package fr.polytech.suuuuuuuuuuudoku.utils;

import fr.polytech.suuuuuuuuuuudoku.grid.Grid;
import fr.polytech.suuuuuuuuuuudoku.grid.MultiGrid;
import fr.polytech.suuuuuuuuuuudoku.grid.SymbolSets;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Utility class for reading and writing Grid from CSV files.
 */
public class CsvUtils {

    /**
     * Creates a Grid from a CSV string.
     *
     * @param file the CSV file representing the grid
     * @return the created Grid
     * @throws FileNotFoundException if the file is not found
     */
    static public Integer[][] importGrid(Path file) throws FileNotFoundException {
        return new BufferedReader(new FileReader(file.toFile()))
                .lines()
                .map(line -> Arrays.stream(line.split(",(?<!\\r)")).map(cell -> cell.equals(".") ? null :
                                           Integer.parseInt(cell))
                                   .toArray(Integer[]::new))
                .toArray(Integer[][]::new);
    }

    /**
     * Creates a MultiGrid from a folder containing CSVs.
     *
     * @param folder the CSV folder representing the grids
     * @return the created Grid
     * @throws FileNotFoundException if the folder is not found
     */
    static public MultiGrid importMultiGrid(Path folder) throws FileNotFoundException {
        // list the files
        var files = folder.toFile().listFiles();
        if (files == null) {
            throw new FileNotFoundException("Folder not found");
        }
        files = Arrays.stream(files).sorted().toArray(File[]::new);

        // load the grids
        ArrayList<Grid> grids = Arrays.stream(files).filter(file -> !file.getName().equals("offset.csv")).map(file -> {
            try {
                var gridValue = importGrid(file.toPath());
                var symbols = SymbolSets.generateSymbols(gridValue.length);
                return new Grid(gridValue, symbols);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toCollection(ArrayList::new));

        // load the offset
        ArrayList<Vec2i> offsets = new BufferedReader(new FileReader(
                Arrays.stream(files).filter(file -> file.getName().equals("offset.csv")).findFirst().orElseThrow(FileNotFoundException::new)))
                .lines()
                .map(line -> {
                    String[] parts = line.split(",(?<!\\r)");
                    return new Vec2i(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                })
                .collect(Collectors.toCollection(ArrayList::new));

        assert offsets.size() == grids.size();
        // merge the grids
        var mergedGrids = grids.stream()
                               .map(grid -> new Pair<>(offsets.removeFirst(), grid))
                               .collect(Collectors.toList());
        return new MultiGrid(mergedGrids);
    }

    /**
     * Converts the grid to a CSV string.
     *
     * @param path the path to save the CSV file to
     * @param grid the grid to convert
     */
    static public void exportGrid(Path path, Grid grid) {
        var csvData = Arrays.stream(grid.getInnerGrid().get())
                            .map(line -> Arrays.stream(line)
                                               .map(cell -> cell == null ? "." : cell.toString())
                                               .collect(Collectors.joining(",")))
                            .collect(Collectors.joining("\n"));

        try (var writer = new BufferedWriter(new FileWriter(path.toFile()))) {
            writer.write(csvData);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Converts the MultiGrid to a folder of CSVs.
     *
     * @param folder the folder to save the CSV files to
     * @param grid   the MultiGrid to convert
     */
    static public void exportMultiGrid(Path folder, MultiGrid grid) {
        // save the grids
        for (int i = 0; i < grid.getGrids().length; i++) {
            exportGrid(folder.resolve(i + ".csv"), grid.getGrids()[i]);
        }

        // save the offset
        var offset = grid.getOffsets();
        try (var writer = new BufferedWriter(new FileWriter(folder.resolve("offset.csv").toFile()))) {
            for (Vec2i vec2i : offset) {
                writer.write(vec2i.getX() + "," + vec2i.getY() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }
}
