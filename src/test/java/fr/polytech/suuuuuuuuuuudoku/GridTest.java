package fr.polytech.suuuuuuuuuuudoku;

import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec2i;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GridTest {
    static String RESSOURCES_PATH = "src/test/resources/";

    @Test
    public void testImportExport() throws FileNotFoundException {

        var grid = CsvUtils.importGrid(Path.of(RESSOURCES_PATH + "100x100.csv"));
        CsvUtils.exportGrid(Path.of(RESSOURCES_PATH + "100x100_export.csv"), grid);

        var grid2 = CsvUtils.importGrid(Path.of(RESSOURCES_PATH + "100x100_export.csv"));
        for (int i = 0; i < grid.length(); i++) {
            for (int j = 0; j < grid.getInnerGrid().get()[0].length; j++) {
                assertEquals(grid.getInnerGrid().get()[i][j], grid2.getInnerGrid().get()[i][j]);
            }
        }

        //delete the exported file
        assertTrue(new File(RESSOURCES_PATH + "100x100_export.csv").delete());
    }


    @Test
    public void testMultigrid() throws FileNotFoundException {
        var grid = CsvUtils.importMultiGrid(Path.of(RESSOURCES_PATH + "multigrid_1"));

        assertEquals(5, grid.getGrids().length);
        assertEquals(1, grid.getSymbolAt(new Vec2i(10, 10)));
        assertEquals(7, grid.getSymbolAt(Vec2i.zero()));
    }

}
