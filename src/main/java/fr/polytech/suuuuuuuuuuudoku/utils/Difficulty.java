package fr.polytech.suuuuuuuuuuudoku.utils;

/**
 * Enum representing the difficulty levels for the Sudoku game.
 */
public enum Difficulty {
    /**
     * Easy difficulty level.
     */
    EASY,

    /**
     * Medium difficulty level.
     */
    MEDIUM,

    /**
     * Hard difficulty level.
     */
    HARD,

    /**
     * Very hard difficulty level.
     */
    VERY_HARD,

    /**
     * Impossible difficulty level.
     */
    IMPOSSIBLE,

    /**
     * Insane difficulty level.
     */
    INSANE,

    /**
     * Expert difficulty level.
     */
    EXPERT;

    /**
     * Returns an array of difficulty level names as strings.
     *
     * @return an array of difficulty level names
     */
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

    /**
     * Converts an integer value to the corresponding Difficulty enum.
     *
     * @param value the integer value representing the difficulty level
     * @return the corresponding Difficulty enum
     * @throws IllegalArgumentException if the value is invalid
     */
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

    /**
     * Returns the integer value corresponding to the Difficulty enum.
     *
     * @return the integer value of the difficulty level
     */
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

    /**
     * Returns the string representation of the Difficulty enum.
     *
     * @return the string representation of the difficulty level
     */
    @Override
    public String toString() {
        return switch (this) {
            case EASY -> "Easy";
            case MEDIUM -> "Medium";
            case HARD -> "Hard";
            case VERY_HARD -> "Very Hard";
            case IMPOSSIBLE -> "Impossible";
            case INSANE -> "Insane";
            case EXPERT -> "Expert";
        };
    }
}
