package com.example.frank.vlcdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.SyncStateContract;
import android.util.Log;

import com.inuker.library.BitmapUtils;
import com.inuker.library.utils.ImageUtils;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayCallback;
import org.videolan.libvlc.MediaPlayer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import cn.aibee.android.location.ByteUtils;

/**
 * Created by liwentian on 2017/10/12.
 */

public class RtspHelper {

    private MediaPlayer mMediaPlayer;

    private LibVLC mVlc;

    private static RtspHelper sInstance = new RtspHelper();

    private ByteBuffer mByteBuffer;

    public static RtspHelper getInstance() {
        return sInstance;
    }

    public interface RtspCallback {
        void onPreviewFrame(ByteBuffer buffer, int width, int height);
    }

    private RtspHelper() {

    }

    boolean tag = true;

    public void createPlayer(String url, final int width, final int height, final RtspCallback callback) {
        System.out.println("____play url: " + url);
        releasePlayer();
        mByteBuffer = ByteBuffer.allocateDirect(width * height * 4)
                .order(ByteOrder.nativeOrder());
        try {
            final ArrayList<String> options = new ArrayList<String>();
            options.add("--aout=opensles");
            options.add("--audio-time-stretch"); // time stretching
            options.add("-vvv"); // verbosity
            mVlc = new LibVLC(MyApplication.getContext(), options);

            // Create media player
            mMediaPlayer = new MediaPlayer(mVlc);
            mMediaPlayer.setVideoFormat("RGBA", width, height, width*4);
            mMediaPlayer.setVideoCallback(mByteBuffer, new MediaPlayCallback() {
                @Override
                public void onDisplay(final ByteBuffer byteBuffer) {
                    callback.onPreviewFrame(byteBuffer, width, height);
                  //  System.out.println("____yByte :  " + byteBuffer.array().length);
                    if (tag){
                      //  System.out.println("______current thread:   "+Thread.currentThread());
                        byteBuffer.rewind();
                       // ByteUtils byteUtils = new ByteUtils();
                       // byte[] yByte = byteUtils.getYByte(byteBuffer.array(), width, height);
                        //int[] ints = byteUtils.rgb24ToPixel(byteBuffer.array(), width, height);
                        //System.out.println("____yByte :  " + yByte.length+"____ints:  "+ints);
                      /*  Bitmap bitmap = getGreyBitmap(byteBuffer.array(), width, height) ;
                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/aaa.png");
                        saveBitmap(bitmap, file.getPath());
                        System.out.println("____bitmap width :  " + bitmap.getWidth());
                        tag = false;*/
                    }
                }
            });

            Media m = new Media(mVlc, Uri.parse(url));
            int cache = 1500;
            m.addOption(":network-caching=" + cache);
            m.addOption(":file-caching=" + cache);
            m.addOption(":live-cacheing=" + cache);
            m.addOption(":sout-mux-caching=" + cache);
            m.addOption(":codec=mediacodec,iomx,all");
            mMediaPlayer.setMedia(m);
            mMediaPlayer.play();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void setUrl(String url){
        mMediaPlayer.stop();
        Media m = new Media(mVlc, Uri.parse(url));
        int cache = 1500;
        m.addOption(":network-caching=" + cache);
        m.addOption(":file-caching=" + cache);
        m.addOption(":live-cacheing=" + cache);
        m.addOption(":sout-mux-caching=" + cache);
        m.addOption(":codec=mediacodec,iomx,all");
        mMediaPlayer.setMedia(m);
        mMediaPlayer.play();
    }

    private Bitmap createBitmap(byte[] values, int picW, int picH) {
        if (values == null || picW <= 0 || picH <= 0)
            return null;
        //使用8位来保存图片
        Bitmap bitmap = Bitmap
                .createBitmap(picW, picH, Bitmap.Config.ARGB_8888);
        int pixels[] = new int[picW * picH];
        for (int i = 0; i < pixels.length; ++i) {
            //关键代码，生产灰度图
            pixels[i] = values[i] * 256 * 256 + values[i] * 256 + values[i] + 0xFF000000;
        }
        bitmap.setPixels(pixels, 0, picW, 0, 0, picW, picH);
        values = null;
        pixels = null;
        return bitmap;
    }

    private int[] rgb24ToPixel(byte[] rgb24, int width, int height) {
        int[] pix = new int[rgb24.length / 3];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int idx = width * i + j;
                int rgbIdx = idx * 3;
                int red = rgb24[rgbIdx];
                int green = rgb24[rgbIdx + 1];
                int blue = rgb24[rgbIdx + 2];
                int color = (blue & 0x000000FF) | (green << 8 & 0x0000FF00) | (red << 16 & 0x00FF0000);
                pix[idx] = color;
            }
        }
        return pix;
    }

    private byte[] rgb24ToY(byte[] rgb32, int width, int height) {
        byte[] y = new byte[width*height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int idx = width * i + j;
                int rgbIdx = idx * 4;
                int red = rgb32[rgbIdx];
                int green = rgb32[rgbIdx + 1];
                int blue = rgb32[rgbIdx + 2];
                int grey = (int)(0.299*red + 0.587*green + 0.114*blue);
                y[idx] = (byte) grey;
            }
        }
        return y;
    }

    public static void saveBitmap(Bitmap bitmap,String path) {
        String savePath;
        File filePic;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            savePath = path;
        } else {
            Log.d("xxx", "saveBitmap: 1return");
            return;
        }
        try {
            filePic = new File(savePath + ".jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("xxx", "saveBitmap: 2return");
            return;
        }
        Log.d("xxx", "saveBitmap: " + filePic.getAbsolutePath());
    }

    private Bitmap getGreyBitmap(byte[] rgb32, int width, int height){
        int[] pixels = new int[width * height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int index = width * i + j;
                int rgbIndex = index * 4;
                int red = rgb32[rgbIndex];
                int gre = rgb32[rgbIndex + 1];
                int blu = rgb32[rgbIndex + 2];
                int alp = rgb32[rgbIndex + 3];
                int color = (blu & 0x000000FF) | (gre << 8 & 0x0000FF00)
                        | (red << 16 & 0x00FF0000) | (alp << 24 & 0xFF000000);
                pixels[index] = color;
            }
        }
        System.out.println("_____bytes1  :  800   "+pixels[800]);
        int[] pixels2 = new ByteUtils().rgb24ToPixel(rgb32,width,height);
        System.out.println("_____bytes2  :  800   "+pixels2[800]);
        int alpha = 0xFF << 24;
        for(int i = 0; i < height; i++)  {
            for(int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];

                int red = ((grey  & 0x00FF0000 ) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);

                grey = (int)((float) red * 0.3 + (float)green * 0.59 + (float)blue * 0.11);
                grey = alpha | (grey << 16) | (grey << 8) | grey;
                pixels[width * i + j] = grey;
            }
        }
        return createBitmap(pixels, width, height);
    }

    private Bitmap createBitmap(int[] pixels, int width, int height) {
        if (pixels == null || width <= 0 || height <= 0) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public void releasePlayer() {
        if (mVlc == null) {
            return;
        }

        mMediaPlayer.setVideoCallback(null, null);
        mMediaPlayer.stop();

        mVlc.release();
        mVlc = null;
    }
}
