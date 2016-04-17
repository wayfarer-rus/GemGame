package wayfarer.gemgame.game.util;

/**
 * Project GemGame
 * Created by wayfarer on 12/6/15.
 */
public class MathUtil {
    private MathUtil() {}

    public static float sin(float angle) {
        return (float) Math.sin(Math.PI/180*angle);
    }

    public static float cos(float angle) {
        return (float) Math.cos(Math.PI/180*angle);
    }
}
