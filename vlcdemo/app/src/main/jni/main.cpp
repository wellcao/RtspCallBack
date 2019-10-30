//
// Created by caocuooo on 2019/10/7.
//

#include "cn_aibee_android_location_ByteUtils.h"
#include <string>

#include <android/log.h>

#define TAG    "myhello-jni-test" // 这个是自定义的LOG的标识
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,TAG,__VA_ARGS__) // 定义LOGD类型


extern "C"
JNIEXPORT jbyteArray JNICALL Java_cn_aibee_android_location_ByteUtils_getYByte(JNIEnv *env,jobject cls,jbyteArray data,jint width,jint height){
    jbyte *bytedata = (*env).GetByteArrayElements(data,0);
    char* old = (char *)bytedata;
    char * newchararray = new char[width*height];
    jbyteArray newbytearray = (*env).NewByteArray(width*height);
    for (int i = 0; i <height ; ++i) {
        for (int j = 0; j < width; ++j) {
            int idx = width * i + j;
            int rgbIdx = idx * 4;
            int red = old[rgbIdx];
            int green = old[rgbIdx + 1];
            int blue = old[rgbIdx + 2];
            int grey = (int)(0.299*red + 0.587*green + 0.114*blue);
            newchararray[idx] = static_cast<char>(grey);
        }
    }
    LOGD("___byte convert:  "+ sizeof(newchararray));
    (*env).SetByteArrayRegion(newbytearray , 0, sizeof(newchararray),
                              reinterpret_cast<const jbyte *>(newchararray));
    return newbytearray ;
}

extern "C"
JNIEXPORT jintArray JNICALL Java_cn_aibee_android_location_ByteUtils_rgb24ToPixel(JNIEnv *env,jobject cls,jbyteArray data,jint width,jint height){
    jbyte *bytedata = (*env).GetByteArrayElements(data,0);
    char* old = (char *)bytedata;
    int* pixelarray = new int[width*height];
    jintArray newintarray = (*env).NewIntArray(width*height);
    for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
            int index = width * i + j;
            int rgbIndex = index * 4;
            int red = old[rgbIndex];
            int gre = old[rgbIndex + 1];
            int blu = old[rgbIndex + 2];
            int alp = old[rgbIndex + 3];
            int grey = (int)(0.299*red + 0.587*gre + 0.114*blu);;
            pixelarray[index] = grey;
        }
    }
    LOGD("___pixel[] size:  "+ sizeof(pixelarray));
    (*env).SetIntArrayRegion(newintarray , 0, sizeof(pixelarray),
                              pixelarray);
    return newintarray;
}