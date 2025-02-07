package fr.polytech.suuuuuuuuuuudoku;

import fr.polytech.suuuuuuuuuuudoku.grid.Grid;
import fr.polytech.suuuuuuuuuuudoku.grid.SymbolSets;
import fr.polytech.suuuuuuuuuuudoku.utils.CsvUtils;
import fr.polytech.suuuuuuuuuuudoku.utils.Vec2i;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Basic tests for the grid.
 */
public class GridTest {

    @Test
    public void testImportExport() throws FileNotFoundException {

        Path path = Path.of("100x100_export.csv");
        var gridValue = CsvUtils.importGrid(Path.of(ClassLoader.getSystemResource("exemples/100x100.csv").getFile()));
        var symbol = SymbolSets.generateSymbols(gridValue.length);
        Grid grid = new Grid(gridValue, symbol);
        CsvUtils.exportGrid(path, grid);

        var gridValue2 = CsvUtils.importGrid(path);
        Grid grid2 = new Grid(gridValue2, symbol);
        for (int i = 0; i < grid.length(); i++) {
            for (int j = 0; j < grid.getInnerGrid().get()[0].length; j++) {
                assertEquals(grid.getInnerGrid().get()[i][j], grid2.getInnerGrid().get()[i][j]);
            }
        }

        //delete the exported file
        assertTrue(new File("100x100_export.csv").delete());
    }


    @Test
    public void testMultigrid() throws FileNotFoundException {
        var grid = CsvUtils.importMultiGrid(Path.of(ClassLoader.getSystemResource("exemples/multigrid_1").getFile()));

        assertEquals(5, grid.getGrids().length);
        assertEquals(1, grid.getSymbolAt(new Vec2i(10, 10)));
        assertEquals(7, grid.getSymbolAt(Vec2i.zero()));
    }

}
