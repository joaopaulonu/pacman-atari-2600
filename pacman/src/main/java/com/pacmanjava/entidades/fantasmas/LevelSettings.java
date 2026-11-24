// LevelSettings.java
package com.pacmanjava.entidades.fantasmas;

import java.util.List;

/** Parâmetros de dificuldade por fase (velocidades como fração da velocidade máxima). */
public record LevelSettings(
        int level,
        double ghostSpeed,
        double tunnelSpeed,
        double frightenedSpeed,
        double elroy1Speed,
        double elroy2Speed,
        int elroy1Dots,
        int elroy2Dots,
        double frightenedSeconds,
        List<Double> modeDurations) {

    private static final double INFINITY = Double.POSITIVE_INFINITY;
    private static final double ONE_FRAME = 1.0 / 60.0;

    private static final double[] GHOST_SPEED = {0.70, 0.75, 0.80};
    private static final double[] TUNNEL_SPEED = {0.35, 0.40, 0.45};
    private static final double[] FRIGHT_SPEED = {0.50, 0.55, 0.60};
    private static final double[] ELROY1_SPEED = {0.80, 0.90, 1.00};
    private static final double[] ELROY2_SPEED = {0.85, 0.95, 1.05};
    private static final int[] FRIGHT_SECONDS = {6, 5, 4, 3, 2, 5, 2, 2, 1, 5, 2, 1, 1, 3, 1, 1, 0, 1};

    //revise
    public LevelSettings {
        modeDurations = List.copyOf(modeDurations);
    }


    public static LevelSettings forLevel(int level) {

        if (level < 1) {
            throw new IllegalArgumentException("level must be >= 1");
        }

        int tier = level == 1 ? 0 : level <= 4 ? 1 : 2; //difficulty tier
        int elroy1 = elroyDots(level); //dots required for elroy
        double fright = level <= FRIGHT_SECONDS.length ? FRIGHT_SECONDS[level - 1] : 0; //seconds per level (if higher, = 0)

        //
        List<Double> schedule = switch (tier) {
            case 0 -> List.of(7.0, 20.0, 7.0, 20.0, 5.0, 20.0, 5.0, INFINITY);
            case 1 -> List.of(7.0, 20.0, 7.0, 20.0, 5.0, 1033.0, ONE_FRAME, INFINITY);
            default -> List.of(5.0, 20.0, 5.0, 20.0, 5.0, 1037.0, ONE_FRAME, INFINITY);
        };
        return new LevelSettings(level, GHOST_SPEED[tier], TUNNEL_SPEED[tier], FRIGHT_SPEED[tier], ELROY1_SPEED[tier], ELROY2_SPEED[tier], elroy1, elroy1 / 2, fright, schedule);
    }

    private static int elroyDots(int level) {
        if (level == 1) return 20;
        if (level == 2) return 30;
        if (level <= 5) return 40;
        if (level <= 8) return 50;
        if (level <= 11) return 60;
        if (level <= 14) return 80;
        if (level <= 18) return 100;
        return 120;
    }
}