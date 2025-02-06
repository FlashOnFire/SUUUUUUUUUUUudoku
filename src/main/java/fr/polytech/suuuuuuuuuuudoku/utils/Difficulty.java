package fr.polytech.suuuuuuuuuuudoku.utils;

public enum Difficulty {
    EASY, MEDIUM, HARD, VERY_HARD, IMPOSSIBLE, INSANE, EXPERT;

    public static String[] getValues() {
        return new String[]{
                "EASY",
                "MEDIUM",
                "HARD",
                "VERY_HARD",
                "IMPOSSIBLE",
                "INSANE",
                "EXPERT"
        };
    }

    public static Difficulty fromInt(int value) {
        return switch (value) {
            case 0 -> EASY;
            case 1 -> MEDIUM;
            case 2 -> HARD;
            case 3 -> VERY_HARD;
            case 4 -> IMPOSSIBLE;
            case 5 -> INSANE;
            case 6 -> EXPERT;
            default -> throw new IllegalArgumentException("Invalid value: " + value);
        };
    }

    public int getValue() {
        return switch (this) {
            case EASY -> 0;
            case MEDIUM -> 1;
            case HARD -> 2;
            case VERY_HARD -> 3;
            case IMPOSSIBLE -> 4;
            case INSANE -> 5;
            case EXPERT -> 6;
        };
    }
}
