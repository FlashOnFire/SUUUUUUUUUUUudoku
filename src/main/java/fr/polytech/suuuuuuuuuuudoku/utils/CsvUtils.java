package fr.polytech.suuuuuuuuuuudoku.utils;

import fr.polytech.suuuuuuuuuuudoku.grid.Grid;
import fr.polytech.suuuuuuuuuuudoku.grid.MultiGrid;
import fr.polytech.suuuuuuuuuuudoku.grid.SymbolSets;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for reading and writing Grid from CSV files.
 */
public class CsvUtils {

    /**
     * Private constructor to prevent instantiation of the CsvUtils class.
     */
    private CsvUtils() {
    }

    /**
     * Reads a grid from a CSV file.
     *
     * @param fileName the name of the CSV file
     * @return the grid read from the CSV file
     */
    static public Integer[][] importGrid(String fileName) {
        try (var inputStream = ClassLoader.getSystemResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new FileNotFoundException("Resource not found: " + fileName);
            }
            return new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .map(line -> Arrays.stream(line.split(",(?<!\\r)")).map(cell -> cell.equals(".") ? null :
                                               Integer.parseInt(cell))
                                       .toArray(Integer[]::new))
                    .toArray(Integer[][]::new);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a MultiGrid from a folder containing CSVs.
     *
     * @param folder the CSV folder representing the grids
     * @return the created Grid
     * @throws IOException        if an I/O error occurs
     * @throws URISyntaxException if the URI is invalid
     */
    static public MultiGrid importMultiGrid(String folder) throws IOException, URISyntaxException {
        // list the files
        var resourceUrl = ClassLoader.getSystemResource(folder);
        Path path;
        if (resourceUrl.getProtocol().equals("jar")) {

            var uri = resourceUrl.toURI();
            var fileSystem = FileSystems.getFileSystem(uri);
            if (!fileSystem.isOpen()) {
                fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
            }
            path = fileSystem.getPath(folder);
        } else {
            path = Path.of(resourceUrl.toURI());
        }

        try (var stream = Files.list(path)) {
            Path[] files = stream.toArray(Path[]::new);

            // load the grids
            List<Grid> grids =
                    Arrays.stream(files).filter(file -> !file.getFileName().toString().equals("offset.csv")).sorted()
                          .map(file -> {
                              var gridValue = importGrid(folder + "/" + file.getFileName());
                              var symbols = SymbolSets.generateSymbols(gridValue.length);
                              return new Grid(gridValue, symbols);
                          }).toList();

            // load the offset
            try (var linesStream = Files.lines(
                    Arrays.stream(files)
                          .filter(file -> file.getFileName().toString().equals("offset.csv"))
                          .findFirst()
                          .orElseThrow(FileNotFoundException::new))) {
                ArrayList<Vec2i> offsets = linesStream
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
        }
    }
}
