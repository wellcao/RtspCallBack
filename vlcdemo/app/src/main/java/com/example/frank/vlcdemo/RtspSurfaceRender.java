package com.example.frank.vlcdemo;

import android.opengl.GLSurfaceView;

import com.inuker.library.RGBProgram;
import com.inuker.library.encoder.BaseMovieEncoder;
import com.inuker.library.encoder.MovieEncoder1;
import com.inuker.library.utils.LogUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;

/**
 * Created by liwentian on 2017/10/12.
 */

public class RtspSurfaceRender implements GLSurfaceView.Renderer, RtspHelper.RtspCallback {

    private ByteBuffer mBuffer;

    private GLSurfaceView mGLSurfaceView;

    private RGBProgram mProgram;

    private String mRtspUrl;

    private BaseMovieEncoder mVideoEncoder;

    public RtspSurfaceRender(GLSurfaceView glSurfaceView) {
        mGLSurfaceView = glSurfaceView;
    }

    public void setRtspUrl(String url) {
        mRtspUrl = url;
    }


    private final float scale = 2.134f;

    private final int STREAM_WIDTH = 1920;
    private final int STREAM_HEIGHT = 1080;

    private float mScaleX = 1.0f;
    private float mScaleY = 1.0f;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        System.out.println("______scale:   " + (height + 0.0f) / width + "___videoview width   " + width);
        mProgram = new RGBProgram(mGLSurfaceView.getContext(), STREAM_WIDTH, STREAM_HEIGHT,mScaleX,mScaleY);
        mBuffer = ByteBuffer.allocateDirect((STREAM_WIDTH * STREAM_HEIGHT * 4)).order(ByteOrder.nativeOrder());
        mVideoEncoder = new MovieEncoder1(mGLSurfaceView.getContext(), STREAM_WIDTH, STREAM_HEIGHT);
        RtspHelper.getInstance().createPlayer(mRtspUrl, STREAM_WIDTH, STREAM_HEIGHT, this);
        System.out.println("______scale:   " + (height + 0.0f) / width + "___stream width   " + STREAM_WIDTH);
    }

    public void onSurfaceDestoryed() {
        RtspHelper.getInstance().releasePlayer();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(1f, 1f, 1f, 1f);

        mProgram.useProgram();

        synchronized (mBuffer) {
            mProgram.setUniforms(mBuffer.array(), 90);
        }

        mProgram.draw();
    }

    @Override
    public void onPreviewFrame(final ByteBuffer buffer, int width, int height) {
        synchronized (mBuffer) {
            mBuffer.rewind();

            buffer.rewind();
            mBuffer.put(buffer);
        }

        mGLSurfaceView.post(new Runnable() {
            @Override
            public void run() {
                mVideoEncoder.frameAvailable(buffer.array(), System.nanoTime());
            }
        });

        mGLSurfaceView.requestRender();
    }

    public void setRGBScale(float scaleX, float scaleY) {
        RtspHelper.getInstance().createPlayer(mRtspUrl, STREAM_WIDTH, STREAM_HEIGHT, this);
    }
}
