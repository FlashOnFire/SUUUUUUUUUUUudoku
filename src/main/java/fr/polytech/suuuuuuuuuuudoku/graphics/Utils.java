package fr.polytech.suuuuuuuuuuudoku.graphics;

import java.util.HashMap;

public class Utils {
    public <O, M> M[][] applyMapping(O[][] original, M[][] mapped, HashMap<O, M> mapping) {
        assert original.length == mapped.length;
        if (original.length == 0) return mapped;
        assert original[0].length == mapped[0].length;

        for (int i = 0; i < original.length; i++) {
            for (int j = 0; j < original[0].length; j++) {
                mapped[i][j] = mapping.get(original[i][j]);
            }
        }

        return mapped;
    }
}
