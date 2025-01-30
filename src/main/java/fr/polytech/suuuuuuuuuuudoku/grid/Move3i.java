package fr.polytech.suuuuuuuuuuudoku.grid;

import fr.polytech.suuuuuuuuuuudoku.algorithm.Vec3i;

public record Move3i(Vec3i position, Integer value, Integer previous_value) {
}
