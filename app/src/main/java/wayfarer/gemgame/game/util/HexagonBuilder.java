package wayfarer.gemgame.game.util;

import android.content.Context;

import javax.vecmath.Point3d;

import wayfarer.gemgame.game.Hexagon;

/**
 * Project GemGame
 * Created by wayfarer on 12/6/15.
 */
public class HexagonBuilder {
    private Point3d mPoint;
    private Context mContext;
    private int mFragmentShaderRes;
    private int mVertexShaderRes;
    private int mTextureRes;
    private String mId;
    private float mRadius;
    private String mText;

    public HexagonBuilder(String id, Context context) {
        this.mId = id;
        this.mContext = context;
    }

    public HexagonBuilder(String id,  Context context, Point3d point) {
        this.mId = id;
        this.mPoint = point;
        this.mContext = context;
    }

    public HexagonBuilder setFragmentShader(int fragmentShaderRes) {
        this.mFragmentShaderRes = fragmentShaderRes;
        return this;
    }

    public HexagonBuilder setVertexShader(int vertexShaderRes) {
        this.mVertexShaderRes = vertexShaderRes;
        return this;
    }

    public HexagonBuilder setTexture(int textureRes) {
        this.mTextureRes = textureRes;
        return this;
    }

    public HexagonBuilder setRadius(float radius) {
        this.mRadius = radius;
        return this;
    }

    public HexagonBuilder setText(String text) {
        this.mText = text;
        return this;
    }

    public Hexagon build() {
        Hexagon result = new Hexagon(mContext, mFragmentShaderRes, mVertexShaderRes, mTextureRes, mId);
        result.setPoint(mPoint);
        result.setRadius(mRadius);
        result.setText(mText);
        return result;
    }
}
