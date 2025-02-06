package fr.polytech.suuuuuuuuuuudoku;

import fr.polytech.suuuuuuuuuuudoku.algorithm.Generator;
import fr.polytech.suuuuuuuuuuudoku.utils.Difficulty;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * Tests to benchmark the generator.
 */
public class GeneratorTests {

    @Test
    public void benchmarkTests() throws InterruptedException {
        int[] gridSizes = {4, 9, 16};
        final int N = 10;

        System.out.println("Grid size, Moyenne, Min, Max, Médiane");
        for (int gridSize : gridSizes) {
            long[] durations = new long[N];
            for (int i = 0; i < N; i++) {
                long startTime = System.currentTimeMillis();
                Generator.generateSudokuWithBlockConstraints((int) Math.sqrt(gridSize), (int) Math.sqrt(gridSize),
                        Difficulty.EXPERT);
                // Generator.generateMultigridSudoku(9, 5, Difficulty.EXPERT);
                long endTime = System.currentTimeMillis();
                durations[i] = endTime - startTime;
            }
            Arrays.sort(durations);
            long sum = Arrays.stream(durations).sum();
            long moy = sum / durations.length;
            long min = durations[0];
            long max = durations[durations.length - 1];
            long median = durations[durations.length / 2];

            System.out.println(gridSize + "," + moy + "ms," + min + "ms," + max + "ms," + median + "ms");
        }
    }
}
