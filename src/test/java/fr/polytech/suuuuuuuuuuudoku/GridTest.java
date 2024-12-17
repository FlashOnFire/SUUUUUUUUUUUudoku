package fr.polytech.suuuuuuuuuuudoku;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GridTest {
    static String RESSOURCES_PATH = "src/test/java/fr/polytech/suuuuuuuuuuudoku/resources/";

    @Test
    public void testImportExport() throws FileNotFoundException {

        var grid = Grid.fromCsv(Path.of(RESSOURCES_PATH + "100x100.csv"));
        grid.toCsv(Path.of(RESSOURCES_PATH + "100x100_export.csv"));

        var grid2 = Grid.fromCsv(Path.of(RESSOURCES_PATH + "100x100_export.csv"));
        for (int i = 0; i < grid.getGrid().length; i++) {
            for (int j = 0; j < grid.getGrid()[0].length; j++) {
                assertEquals(grid.getGrid()[i][j], grid2.getGrid()[i][j]);
            }
        }

        //delete the exported file
        assertTrue(new File(RESSOURCES_PATH + "100x100_export.csv").delete());
    }


}
