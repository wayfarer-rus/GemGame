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

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Point3d;

import wayfarer.gemgame.game.Hexagon;
import wayfarer.gemgame.mesh.Mesh;

/**
 * Provides drawing instructions for a GLSurfaceView object. This class
 * must override the OpenGL ES drawing lifecycle methods:
 * <ul>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceCreated}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onDrawFrame}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceChanged}</li>
 * </ul>
 */
public class GemGameGLRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "GemGameGLRenderer";

    private List<Mesh> mMeshList = new ArrayList<Mesh>();

    /**
     * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
     * it positions things relative to our eye.
     */
    private float[] mViewMatrix = new float[16];

    /** Store the projection matrix. This is used to project the scene onto a 2D viewport. */
    private float[] mProjectionMatrix = new float[16];

    private float[] mCameraPosition = {0.0f, 0.0f, 10.0f};

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        Log.d(TAG, "+ enter onSurfaceCreated");
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        // Set the background clear color to black.
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Use culling to remove back faces.
        GLES20.glEnable(GLES20.GL_CULL_FACE);

        // Enable depth testing
        //GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        setupCamera();

        for (Mesh m : mMeshList) {
            m.init();
        }
        Log.d(TAG, "- leave onSurfaceCreated");
    }

    private void setupCamera() {
        // Position the eye in front of the origin.
        final float eyeX = mCameraPosition[0];
        final float eyeY = mCameraPosition[1];
        final float eyeZ = mCameraPosition[2];

        // We are looking toward the distance
        final float lookX = mCameraPosition[0];
        final float lookY = mCameraPosition[1];
        final float lookZ = 0.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);

        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 100.0f;

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        setupCamera();

        for (Mesh m : mMeshList) {
            m.draw(mViewMatrix, mProjectionMatrix);
        }
    }

    public void addMesh(Mesh mesh) {
        mMeshList.add(mesh);
    }

    public void setCamera(float x, float y, float z) {
        mCameraPosition[0] = x;
        mCameraPosition[1] = y;
        mCameraPosition[2] = z;
    }

    public Point3d getCameraPos() {
        return new Point3d(mCameraPosition[0],
                mCameraPosition[1],
                mCameraPosition[2]);
    }

    public void addAllMeshes(List<Hexagon> meshList) {
        mMeshList.addAll(meshList);
    }

    public float[] getCurrentProjection() {
        return mProjectionMatrix;
    }

    public float[] getCurrentView() {
        return mViewMatrix;
    }

    public float[] getCurrentModelView() {
        // get model view for first hexagon.
        // Yeah-yeah! this is hack, but I don't know what to do here.
        return mMeshList.get(1).getModelView();
    }
}