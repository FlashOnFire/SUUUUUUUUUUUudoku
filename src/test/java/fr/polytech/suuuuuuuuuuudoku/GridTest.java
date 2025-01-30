package fr.polytech.suuuuuuuuuuudoku;

import fr.polytech.suuuuuuuuuuudoku.algorithm.Box2D;
import fr.polytech.suuuuuuuuuuudoku.algorithm.Pair;
import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec2i;
import fr.polytech.suuuuuuuuuuudoku.constraints.BlockEqualityConstraint;
import fr.polytech.suuuuuuuuuuudoku.grid.Grid;
import fr.polytech.suuuuuuuuuuudoku.grid.MultiGrid;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GridTest {
    static String RESSOURCES_PATH = "src/test/java/fr/polytech/suuuuuuuuuuudoku/resources/";

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
        List<Pair<Vec2i, Grid>> grids = new ArrayList<>();
        Vec2i[] positions = {
                Vec2i.zero(),
                new Vec2i(0, 12),
                new Vec2i(6, 6),
                new Vec2i(12, 0),
                new Vec2i(12, 12),
        };
        for (int i = 0; i < 5; i++) {
            var grid = CsvUtils.importGrid(Path.of(RESSOURCES_PATH + "/multigrid_1/" + i + ".csv"));
            grids.add(new Pair<>(positions[i], grid));
        }

        Collections.shuffle(grids);

        var constraints = new BlockEqualityConstraint[]{
                new BlockEqualityConstraint(0, new Box2D(6, 6, 3, 3), 2, new Box2D(0, 0, 3, 3)),
                new BlockEqualityConstraint(1, new Box2D(6, 0, 3, 3), 2, new Box2D(0, 6, 3, 3)),
                new BlockEqualityConstraint(2, new Box2D(6, 0, 3, 3), 3, new Box2D(0, 6, 3, 3)),
                new BlockEqualityConstraint(2, new Box2D(6, 6, 3, 3), 4, new Box2D(0, 0, 3, 3))
        };

        var grid = new MultiGrid(grids);

        assertEquals(constraints.length, grid.getConstraints().size());
        for (BlockEqualityConstraint constraint : constraints) {
            assertTrue(grid.getConstraints().contains(constraint));
        }

        for (int i = 0; i < grid.getGrids().length; i++) {
            assertEquals(grids.get(i).getSecond(), grid.getGrids()[i]);
            assertEquals(grids.get(i).getFirst(), grid.getPaddings()[i]);
        }
    }

}
