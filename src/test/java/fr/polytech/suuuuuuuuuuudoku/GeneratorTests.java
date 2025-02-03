package fr.polytech.suuuuuuuuuuudoku;

import fr.polytech.suuuuuuuuuuudoku.algorithm.Generator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GeneratorTests {

    @Test
    public void testBasicGenerator() throws InterruptedException {
        long startTime1 = System.currentTimeMillis();
        Generator.generateClassicSudoku(4);
        long endTime1 = System.currentTimeMillis();
        System.out.println("Generation time for 4x4 grid: " + (endTime1 - startTime1) + "ms");
        assertTrue((endTime1 - startTime1) < 1000, "Generation time for 4x4 grid exceeded limit");

        long startTime2 = System.currentTimeMillis();
        Generator.generateClassicSudoku(9);
        long endTime2 = System.currentTimeMillis();
        System.out.println("Generation time for 9x9 grid: " + (endTime2 - startTime2) + "ms");
        assertTrue((endTime2 - startTime2) < 1000, "Generation time for 9x9 grid exceeded limit");

        long startTime3 = System.currentTimeMillis();
        Generator.generateClassicSudoku(16);
        long endTime3 = System.currentTimeMillis();
        System.out.println("Generation time for 16x16 grid: " + (endTime3 - startTime3) + "ms");
        assertTrue((endTime3 - startTime3) < 1000, "Generation time for 16x16 grid exceeded limit");

        long startTime4 = System.currentTimeMillis();
        Generator.generateClassicSudoku(25);
        long endTime4 = System.currentTimeMillis();
        System.out.println("Generation time for 25x25 grid: " + (endTime4 - startTime4) + "ms");
        assertTrue((endTime4 - startTime4) < 30000, "Generation time for 25x25 grid exceeded limit");

        long startTime5 = System.currentTimeMillis();
        Generator.generateSudokuWithBlockConstraints(3, 4);
        long endTime5 = System.currentTimeMillis();
        System.out.println("Generation time for 25x25 grid: " + (endTime4 - startTime4) + "ms");
        assertTrue((endTime5 - startTime5) < 20000, "Generation time for 25x25 grid exceeded limit");
    }
}
