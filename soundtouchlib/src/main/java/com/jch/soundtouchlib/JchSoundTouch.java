package com.jch.soundtouchlib;

import java.lang.annotation.Native;
import java.nio.ByteBuffer;

public class JchSoundTouch {

    static {
        System.loadLibrary("soundtouch");
    }

    private ByteBuffer dataBuf;
    private int bufSize;
    private int channels;
    private int sampleRte;

    private long nativeInstance;

    interface JchSoundTouchCallback{

        void onProcessed(int bufferSize);
    }

    public JchSoundTouch(int channels, int sampleRte) {
        this.channels = channels;
        this.sampleRte = sampleRte;
    }

    public void processData(ByteBuffer dataBuffer, int bufSize){

        this.dataBuf = dataBuffer;
        this.bufSize = bufSize;
    }

    private native static long nativeGetInstance(JchSoundTouchCallback callback);
    private native void nativeSetChannels(long handle, int channels);
    private native void nativeSetSampleRte(long handle, int sampleRte);
    private native void nativeSetSpeed(long handle, float speed);
    private native void nativeSetTempo(long handle, float tempo);
    private native String nativeGetErrorMsg();
    private native void nativeSetPitchSemiTones(long handle, float pitch);
    private native String nativegGetVersion(long handle);
    private native void nativeCacheBuffer(long handle, ByteBuffer buffer);
    private native int processData(long handle);
    private native void flush(long handle);
    private native void release(long handle);
}
