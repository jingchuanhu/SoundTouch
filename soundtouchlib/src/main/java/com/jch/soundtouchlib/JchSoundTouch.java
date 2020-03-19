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

    public interface JchSoundTouchCallback{

        void onProcessed(int bufSize);
    }

    public JchSoundTouch(int channels, int sampleRte, JchSoundTouchCallback callback) {
        this.channels = channels;
        this.sampleRte = sampleRte;
        nativeInstance = nativeGetInstance(callback);
        nativeSetChannels(nativeInstance, channels);
        nativeSetSampleRte(nativeInstance, sampleRte);
    }

    /**
     *
     * @param bufSize
     * @return true - ok, false - error
     */
    public boolean processData(int bufSize){

        this.bufSize = bufSize;
        return nativeProcessData(nativeInstance) == 0;
    }

    public void attachDataBuffer(ByteBuffer dataBuf) {
        this.dataBuf = dataBuf;
        nativeCacheBuffer(nativeInstance, dataBuf);
    }

    public void setBufSize(int bufSize) {
        this.bufSize = bufSize;
    }

    public void setChannels(int channels) {
        this.channels = channels;
        nativeSetChannels(nativeInstance, channels);
    }

    public void setSampleRte(int sampleRte) {
        this.sampleRte = sampleRte;
        nativeSetSampleRte(nativeInstance, sampleRte);
    }

    public void setSpeed(float speed){
        nativeSetSpeed(nativeInstance, speed);
    }

    public void setTempo(float tempo){
        nativeSetTempo(nativeInstance, tempo);
    }

    public void setPitch(float pitch){
        nativeSetPitchSemiTones(nativeInstance, pitch);
    }

    public String getErrorMsg(){
        return nativeGetErrorMsg(nativeInstance);
    }

    public String getVersion(){
        return nativegGetVersion(nativeInstance);
    }

    public void flush(){
        nativeFlush(nativeInstance);
    }

    public void realse(){
        nativeRelease(nativeInstance);
    }

    private native static long nativeGetInstance(JchSoundTouchCallback callback);
    private native void nativeSetChannels(long handle, int channels);
    private native void nativeSetSampleRte(long handle, int sampleRte);
    private native void nativeSetSpeed(long handle, float speed);
    private native void nativeSetTempo(long handle, float tempo);
    private native String nativeGetErrorMsg(long handle);
    private native void nativeSetPitchSemiTones(long handle, float pitch);
    private native String nativegGetVersion(long handle);
    private native void nativeCacheBuffer(long handle, ByteBuffer buffer);
    private native int nativeProcessData(long handle);
    private native void nativeFlush(long handle);
    private native void nativeRelease(long handle);
}
