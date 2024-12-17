package fr.polytech.suuuuuuuuuuudoku;

import fr.polytech.suuuuuuuuuuudoku.symbols.SymbolSets;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GridTest {
    static String resoucesPath = "src/test/java/fr/polytech/suuuuuuuuuuudoku/resources/";

    @Test
    public void testImportExport() {
        var symbols = SymbolSets.DIGITS_1_100;
        try {
            var grid = Grid.fromCsv(resoucesPath + "100x100.csv", true, symbols);
            grid.toCsv(resoucesPath + "100x100_export.csv");

            var grid2 = Grid.fromCsv(resoucesPath + "100x100_export.csv", true, symbols);
            for (int i = 0; i < grid.getGrid().length; i++) {
                for (int j = 0; j < grid.getGrid()[0].length; j++) {
                    assertEquals(grid.getGrid()[i][j], grid2.getGrid()[i][j]);
                }
            }

            //delete the exported file
            var file = new java.io.File(resoucesPath + "100x100_export.csv");
            var result = file.delete();
            assertTrue(result);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


}
