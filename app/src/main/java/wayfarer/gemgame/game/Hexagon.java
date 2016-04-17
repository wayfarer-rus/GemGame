package wayfarer.gemgame.game;

import android.content.Context;

import javax.vecmath.Point3d;

import wayfarer.gemgame.mesh.Mesh;

/**
 * Project GemGame
 * Created by wayfarer on 12/6/15.
 */
public class Hexagon extends Mesh {
    private static final String TAG = "Hexagon";

    private Point3d mPoint;
    private final String id;
    private float mRadius;
    private String mText;

    public Hexagon(Context context, int fragmentShaderRes, int vertexShaderRes, int textureRes, String id) {
        super(context, fragmentShaderRes, vertexShaderRes, textureRes);
        this.id = id;
    }

    public void setPoint(Point3d point) {
        this.mPoint = point;
        this.setPosition((float)point.x, (float)point.y, (float)point.z);
    }

    public void setRadius(float radius) {
        this.mRadius = radius;
    }

    public void setText(String text) {
        this.mText = text;
    }

    public String getId() {
        return id;
    }

    public Point3d getPoint() {
        return mPoint;
    }
}
