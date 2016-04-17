package wayfarer.gemgame.game.util;

import javax.vecmath.Point3d;

/**
 * Project GemGame
 * Created by wayfarer on 12/7/15.
 */
public class Point3D extends Point3d {
    private static final double EPSILON = 0.001;

    public Point3D(double x, double y, double z) {
        super(x, y, z);
    }

    @Override
    public boolean equals(Object o) {
        try {
            Point3D other = (Point3D)o;
            return (Math.abs(this.x-other.x) < EPSILON) && (Math.abs(this.y-other.y) < EPSILON)
                    && (Math.abs(this.z-other.z) < EPSILON);
        } catch (ClassCastException var4) {
            return false;
        } catch (NullPointerException var5) {
            return false;
        }
    }
}
