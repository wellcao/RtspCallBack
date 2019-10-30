package cn.aibee.android.location;

import android.graphics.Bitmap;

/**
 * author: CaoHuaiHao
 * date: 2019/10/7
 * description:
 */
public class ByteUtils {
    {
        System.loadLibrary("byteutils_lib");
    }

    public native byte[] getYByte(byte[] bytes,int width,int height);

    public native int[] rgb24ToPixel(byte[] bytes,int width,int height);
}
