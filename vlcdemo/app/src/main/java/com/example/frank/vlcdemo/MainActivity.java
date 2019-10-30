package com.example.frank.vlcdemo;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.nio.ByteBuffer;

public class MainActivity extends Activity implements RtspHelper2.RtspCallback {

//    public static final String URL = "rtsp://admin:admin123@10.31.11.79:554/cam/realmonitor?channel=1@subtype=0";

    public static final String URL_MAIN = "rtsp://admin:1234567a@192.168.100.5/Streaming/Channels/1";
    public static final String URL_SUB = "rtsp://admin:1234567a@192.168.100.5/h264/ch1/sub/av_stream";

    private Button mBtBig,mBtSmall,mBtMain,mBtSub;

//    public static final String URL = "rtsp://10.31.0.61:8554/test.mkv";

    private GLSurfaceView mSurfaceView;
    private RtspSurfaceRender mRender;
    private Handler mHandler = new Handler(){
        @Override
        public void dispatchMessage(Message msg) {
            mSurfaceView.onPause();
            mSurfaceView.onResume();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtBig = findViewById(R.id.bt_change_size_big);
        mBtSmall = findViewById(R.id.bt_change_size_small);
        mBtMain = findViewById(R.id.bt_change_stream_main);
        mBtSub = findViewById(R.id.bt_change_stream_sub);

        mSurfaceView = findViewById(R.id.surface);
        mSurfaceView.setEGLContextClientVersion(3);

        mRender = new RtspSurfaceRender(mSurfaceView);
        mRender.setRtspUrl(URL_SUB);

        mSurfaceView.setRenderer(mRender);
        mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mBtBig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mRender.setRGBScale(0.5f,0.5f);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mSurfaceView.getLayoutParams();
                params.width = 1200;
                params.height = 675;
                mSurfaceView.setLayoutParams(params);
               mHandler.sendEmptyMessageDelayed(0,500);
            }
        });

        mBtSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mSurfaceView.getLayoutParams();
                params.width = 600;
                params.height = 337;
                mSurfaceView.setLayoutParams(params);
                mHandler.sendEmptyMessageDelayed(0,500);
            }
        });

        mBtMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRender.setRtspUrl(URL_MAIN);
                mHandler.sendEmptyMessageDelayed(0,500);
            }
        });

        mBtSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRender.setRtspUrl(URL_SUB);
                mHandler.sendEmptyMessageDelayed(0,500);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        mSurfaceView.onResume();
     // mSurfaceView2.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSurfaceView.onPause();
       // mSurfaceView2.onPause();
    }

    @Override
    protected void onDestroy() {
        mRender.onSurfaceDestoryed();
        super.onDestroy();
    }

    @Override
    public void onPreviewFrame(ByteBuffer buffer, int width, int height) {

    }
}
