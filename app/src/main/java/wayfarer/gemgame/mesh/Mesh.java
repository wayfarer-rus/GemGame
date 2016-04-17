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
package wayfarer.gemgame.mesh;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import wayfarer.gemgame.util.RawResourceReader;
import wayfarer.gemgame.util.ShaderHelper;
import wayfarer.gemgame.util.TextureHelper;

public class Mesh {
    public static final String TAG = "Mesh";

    private final Context mActivityContext;

    /** How many bytes per float. */
    private final int mBytesPerFloat = 4;

    /** Size of the position data in elements. */
    private final int mPositionDataSize = 3;

    /** Size of the color data in elements. */
    private final int mColorDataSize = 4;

    /** Size of the normal data in elements. */
    private final int mNormalDataSize = 3;

    /** Size of the texture coordinate data in elements. */
    private final int mTextureCoordinateDataSize = 2;
    private final int mFragmentShaderResId;
    private final int mVertexShaderResId;
    private int mTextureResId;

    /** This will be used to pass in the transformation matrix. */
    private int mMVPMatrixHandle;

    /** This will be used to pass in the modelview matrix. */
    private int mMVMatrixHandle;

    /** This will be used to pass in the texture. */
    private int mTextureUniformHandle;

    /** This will be used to pass in model position information. */
    private int mPositionHandle;

    /** This will be used to pass in model color information. */
    private int mColorHandle;

    /** This will be used to pass in model texture coordinate information. */
    private int mTextureCoordinateHandle;

    /** Store our model data in a float buffer. */
    private final FloatBuffer mMeshPositions;
    private final FloatBuffer mMeshColors;
    private final FloatBuffer mMeshTextureCoordinates;

    /** This is a handle to our cube shading program. */
    private int mProgramHandle;

    /** This is a handle to our texture data. */
    private int mTextureDataHandle;
    private boolean initialized = false;
    private float[] mPosition = {0.0f, 0.0f, 0.0f};

    /**
     * Store the model matrix. This matrix is used to move models from object space (where each model can be thought
     * of being located at the center of the universe) to world space.
     */
    private float[] mModelMatrix = new float[16];
    private float[] mScale = {1.0f, 1.0f, 1.0f};
    private float[] mModelViewMatrix = new float[16];
    private float[] mModelViewProjectionMatrix = new float[16];

    protected String getVertexShader(int res)
    {
        return RawResourceReader.readTextFileFromRawResource(mActivityContext, res);
    }

    protected String getFragmentShader(int res)
    {
        return RawResourceReader.readTextFileFromRawResource(mActivityContext, res);
    }

    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public Mesh(Context context, int fragmentShaderRes, int vertexShaderRes, int textureRes) {
        mActivityContext = context;
        mFragmentShaderResId = fragmentShaderRes;
        mVertexShaderResId = vertexShaderRes;
        mTextureResId = textureRes;
        // Define points for a rectangle mesh.
        // X, Y, Z
        final float[] meshPositionData =
                {
                        // In OpenGL counter-clockwise winding is default. This means that when we look at a triangle,
                        // if the points are counter-clockwise we are looking at the "front". If not we are looking at
                        // the back. OpenGL has an optimization where all back-facing triangles are culled, since they
                        // usually represent the backside of an object and aren't visible anyways.

                        // Front face
                        -1.0f, 1.0f, 0.0f,
                        -1.0f, -1.0f, 0.0f,
                        1.0f, 1.0f, 0.0f,
                        -1.0f, -1.0f, 0.0f,
                        1.0f, -1.0f, 0.0f,
                        1.0f, 1.0f, 0.0f
                };

        // R, G, B, A
        final float[] meshColorData =
                {
                        0.0f, 0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 0.0f, 1.0f
                };

        // S, T (or X, Y)
        // Texture coordinate data.
        // Because images have a Y axis pointing downward (values increase as you move down the image) while
        // OpenGL has a Y axis pointing upward, we adjust for that here by flipping the Y axis.
        final float[] meshTextureCoordinateData =
                {
                        // Front face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f
                };

        // Initialize the buffers.
        mMeshPositions = ByteBuffer.allocateDirect(meshPositionData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mMeshPositions.put(meshPositionData).position(0);

        mMeshColors = ByteBuffer.allocateDirect(meshColorData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mMeshColors.put(meshColorData).position(0);

        mMeshTextureCoordinates = ByteBuffer.allocateDirect(meshTextureCoordinateData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mMeshTextureCoordinates.put(meshTextureCoordinateData).position(0);
    }

    public void init() {
        Log.d(TAG, "+ enter init");

        if (!initialized) {
            Log.d(TAG, "Creating programm and texture");
            final String vertexShader = getVertexShader(mVertexShaderResId);
            final String fragmentShader = getFragmentShader(mFragmentShaderResId);

            final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
            final int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);

            mProgramHandle = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
                    new String[]{"a_Position", "a_Color", "a_TexCoordinate"});

            // Load the texture
            mTextureDataHandle = TextureHelper.loadTexture(mActivityContext, mTextureResId);
            initialized = true;
        } else {
            Log.d(TAG, "Already initialized.");
        }

        Log.d(TAG, "- leave init");
    }

    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     *
     */
    public void draw(float[] viewMatrix, float[] projectionMatrix) {
        // Set our per-vertex lighting program.
        GLES20.glUseProgram(mProgramHandle);

        // Set program handles for cube drawing.
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVMatrix");
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Texture");
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Color");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_TexCoordinate");

        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);

        // Pass in the position information
        mMeshPositions.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
                0, mMeshPositions);

        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Pass in the color information
        mMeshColors.position(0);
        GLES20.glVertexAttribPointer(mColorHandle, mNormalDataSize, GLES20.GL_FLOAT, false,
                0, mMeshColors);

        GLES20.glEnableVertexAttribArray(mColorHandle);

        // Pass in the texture coordinate information
        mMeshTextureCoordinates.position(0);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false,
                0, mMeshTextureCoordinates);

        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);

        // Move mesh in space
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, mPosition[0], mPosition[1], mPosition[2]);
        Matrix.scaleM(mModelMatrix, 0, mScale[0], mScale[1], mScale[2]);
        //Matrix.rotateM(mModelMatrix, 0, 0.0f, 1.0f, 0.0f, 0.0f);

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mModelViewMatrix, 0, viewMatrix, 0, mModelMatrix, 0);
        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mModelViewProjectionMatrix, 0, projectionMatrix, 0, mModelViewMatrix, 0);

        // Pass in the modelview matrix.
        GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mModelViewMatrix, 0);
        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mModelViewProjectionMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
    }

    public void setPosition(float x, float y, float z) {
        mPosition[0] = x;
        mPosition[1] = y;
        mPosition[2] = z;
    }

    public void setScaling(float sx, float sy, float sz) {
        mScale[0] = sx;
        mScale[1] = sy;
        mScale[2] = sz;
    }

    public void setTexrure(int textureResId) {
        mTextureResId = textureResId;

        if (initialized) {
            mTextureDataHandle = TextureHelper.loadTexture(mActivityContext, mTextureResId);
        }
    }

    public float[] getModelView() {
        return mModelViewMatrix;
    }
}