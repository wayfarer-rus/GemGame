/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wayfarer.gemgame.gl;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;

import javax.vecmath.Point3d;

/**
 * A view container where OpenGL ES graphics can be drawn on screen.
 * This view can also be used to capture touch events, such as a user
 * interacting with drawn objects.
 */
public class GemGameGLSurfaceView extends GLSurfaceView {
    public static final String TAG = "GemGameGLSurfaceView";

    private final GemGameGLRenderer mRenderer;

    public GemGameGLSurfaceView(Context context) {
        super(context);
        Log.d(TAG, "+ enter GemGameGLSurfaceView()");
        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        Log.d(TAG, "Creating GLRenderer");
        mRenderer = new GemGameGLRenderer();
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        Log.d(TAG, "- leave GemGameGLSurfaceView()");
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;

    public GemGameGLRenderer getRenderer() {
        return mRenderer;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        Rect frame = getHolder().getSurfaceFrame();
        frame.width();
        frame.height();
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.
        float x = e.getX();
        float y = e.getY();
        Point3d cameraPos;

        switch (e.getAction()) {
            case MotionEvent.ACTION_UP:
                cameraPos = mRenderer.getCameraPos();
                PointF worldCoords = GetWorldCoords(new PointF(x, y), cameraPos, frame.width(), frame.height());
                Log.d(TAG, "Frame: " + frame);
                Log.d(TAG, "Screen coords: " + x + ", " + y);
                Log.d(TAG, "world coords: " + worldCoords);
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = x - mPreviousX;
                float dy = y - mPreviousY;
                Log.d(TAG, "deltaX=" + dx + " | delataY=" + dy);
                cameraPos = mRenderer.getCameraPos();
                mRenderer.setCamera((float)cameraPos.x-dx/100, (float)cameraPos.y+dy/100, (float)cameraPos.z);
                requestRender();
        }

        mPreviousX = x;
        mPreviousY = y;
        return true;
    }

    /**
     * Calculates the transform from screen coordinate
     * system to world coordinate system coordinates
     * for a specific point, given a camera position.
     *
     * @param touch Vec2 point of screen touch, the
    actual position on physical screen (ej: 160, 240)
     * @param cam camera object with x,y,z of the
    camera and screenWidth and screenHeight of
    the device.
     * @return position in WCS.
     */
    public PointF GetWorldCoords( PointF touch, Point3d cam, float screenW, float screenH)
    {
        // Initialize auxiliary variables.
        PointF worldPos = new PointF();
        // Auxiliary matrix and vectors
        // to deal with ogl.
        float[] invertedMatrix, transformMatrix,
                normalizedInPoint, outPoint;
        invertedMatrix = new float[16];
        transformMatrix = new float[16];
        normalizedInPoint = new float[4];
        outPoint = new float[4];

        // Invert y coordinate, as android uses
        // top-left, and ogl bottom-left.
        int oglTouchY = (int) (screenH - touch.y);

       /* Transform the screen point to clip
       space in ogl (-1,1) */
        normalizedInPoint[0] =
                (float) ((touch.x) * 2.0f / screenW - 1.0);
        normalizedInPoint[1] =
                (float) ((oglTouchY) * 2.0f / screenH - 1.0);
        normalizedInPoint[2] = - 1.0f;
        normalizedInPoint[3] = 1.0f;

       /* Obtain the transform matrix and
       then the inverse. */
        Log.d(TAG, "Proj " + mRenderer.getCurrentProjection());
        Log.d(TAG, "Model " + mRenderer.getCurrentModelView());
        Matrix.multiplyMM(
                transformMatrix, 0,
                mRenderer.getCurrentProjection(), 0,
                mRenderer.getCurrentModelView(), 0);
        Matrix.invertM(invertedMatrix, 0,
                transformMatrix, 0);

       /* Apply the inverse to the point
       in clip space */
        Matrix.multiplyMV(
                outPoint, 0,
                invertedMatrix, 0,
                normalizedInPoint, 0);

        if (outPoint[3] == 0.0)
        {
            // Avoid /0 error.
            Log.e("World coords", "ERROR!");
            return worldPos;
        }

        // Divide by the 3rd component to find
        // out the real position.
        worldPos.set(
                outPoint[0] / outPoint[3],
                outPoint[1] / outPoint[3]);


        return worldPos;
    }

}
