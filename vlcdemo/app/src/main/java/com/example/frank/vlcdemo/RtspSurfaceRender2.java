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

public class RtspSurfaceRender2 implements GLSurfaceView.Renderer, RtspHelper2.RtspCallback {

    private ByteBuffer mBuffer;

    private GLSurfaceView mGLSurfaceView;

    private RGBProgram mProgram;

    private String mRtspUrl;

    private BaseMovieEncoder mVideoEncoder;

    public RtspSurfaceRender2(GLSurfaceView glSurfaceView) {
        mGLSurfaceView = glSurfaceView;
    }

    public void setRtspUrl(String url) {
        mRtspUrl = url;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    }

 /*   public void startRecording() {
        mGLSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                if (!mVideoEncoder.isRecording()) {
                    File output = CameraHelper.getOutputMediaFile(CameraHelper.MEDIA_TYPE_VIDEO, "");
                    LogUtils.v(String.format("startRecording: %s", output));
                    mVideoEncoder.startRecording(new BaseMovieEncoder.EncoderConfig(output, EGL14.eglGetCurrentContext()));
                }
            }
        });
    }

    public void stopRecording() {
        mGLSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mVideoEncoder.isRecording()) {
                    mVideoEncoder.stopRecording();
                }
            }
        });
    }*/
    private final float scale = 2.134f;
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        System.out.println("______scale:   "+(height+0.0f)/width+"___width   "+width);
        LogUtils.v(String.format("onSurfaceChanged: width = %d, height = %d", width, height));
        mProgram = new RGBProgram(mGLSurfaceView.getContext(), (int) (width*scale), (int) (height*scale));
        mBuffer = ByteBuffer.allocateDirect((int)(width*scale * height*scale * 4)).order(ByteOrder.nativeOrder());
        mVideoEncoder = new MovieEncoder1(mGLSurfaceView.getContext(), (int)( width*scale), (int) (height*scale));
        RtspHelper2.getInstance().createPlayer(mRtspUrl, (int) (width*scale), (int)(height*scale), this);
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
}
