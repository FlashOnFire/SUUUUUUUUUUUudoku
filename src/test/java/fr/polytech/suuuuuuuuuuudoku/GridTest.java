package fr.polytech.suuuuuuuuuuudoku;

import fr.polytech.suuuuuuuuuuudoku.utils.CsvUtils;
import fr.polytech.suuuuuuuuuuudoku.utils.Vec2i;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Basic tests for the grid.
 */
public class GridTest {

    @Test
    public void testMultigrid() throws IOException, URISyntaxException {
        var grid = CsvUtils.importMultiGrid("exemples/multigrid_1");

        assertEquals(5, grid.getGrids().length);
        assertEquals(1, grid.getSymbolAt(new Vec2i(10, 10)));
        assertEquals(7, grid.getSymbolAt(Vec2i.zero()));
    }

}
