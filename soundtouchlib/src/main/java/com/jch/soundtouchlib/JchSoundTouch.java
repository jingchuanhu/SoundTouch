package com.jch.soundtouchlib;

import java.nio.ByteBuffer;

public class JchSoundTouch {

    public enum AudioFormat{

        PCM_BIT8(1),PCM_BIT16(2), PCM_BIT32(4);

        private int value;

        private AudioFormat(int value) {
            this.value = value;
        }
    }

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

    public void playFile(String file){
        nativePlayFile(nativeInstance, file);
    }

    public void setBufSize(int bufSize) {
        this.bufSize = bufSize;
    }

    public void setChannels(int channels) {
        this.channels = channels;
        nativeSetChannels(nativeInstance, channels);
    }

    public void setAudioFormat(AudioFormat audioFormat){
        nativeSetAudioFormat(nativeInstance, audioFormat.value);
    }

    public void setSampleRte(int sampleRte) {
        this.sampleRte = sampleRte;
        nativeSetSampleRte(nativeInstance, sampleRte);
    }

    public void setSpeed(float speed){
        nativeSetRate(nativeInstance, speed);
    }

    public void dumpFile(String fileName){
        nativeDumpFile(nativeInstance, fileName);
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
        return nativeGetVersion(nativeInstance);
    }

    public void flush(){
        nativeFlush(nativeInstance);
    }

    public void release(){
        nativeRelease(nativeInstance);
    }

    public void setRateChange(float newRate){
        nativeSetRateChange(nativeInstance, newRate);
    }

    public void setTempChange(float newTempo){
        nativeSetTempoChange(nativeInstance, newTempo);
    }

    public void setPitchOctaves(float pitch){
        nativeSetPitchOctaves(nativeInstance, pitch);
    }

    public void enableAAFilter(boolean enable){
        nativeEnableAAFilter(nativeInstance, enable);
    }

    public void setAAFilterLength(int length){
        nativeSetAAFilterLength(nativeInstance, length);
    }

    public void enableQuickSeek(boolean enable){
        nativeEnableQuickSeek(nativeInstance, enable);
    }

    private native static long nativeGetInstance(JchSoundTouchCallback callback);
    private native void nativeSetChannels(long handle, int channels);
    private native void nativeSetSampleRte(long handle, int sampleRte);
    private native void nativeSetAudioFormat(long handle, int audioFormat);
    private native void nativeSetRate(long handle, float speed);
    private native void nativeSetRateChange(long handle, float newRate);
    private native void nativeSetTempo(long handle, float tempo);
    private native void nativeSetTempoChange(long handle, float tempo);
    private native void nativeSetPitchOctaves(long handle, float pitch);
    private native void nativeEnableAAFilter(long handle, boolean enable);
    private native void nativeSetAAFilterLength(long handle, int length);
    private native void nativeEnableQuickSeek(long handle, boolean enable);
    private native String nativeGetErrorMsg(long handle);
    private native void nativeSetPitchSemiTones(long handle, float pitch);
    private native String nativeGetVersion(long handle);
    private native void nativeCacheBuffer(long handle, ByteBuffer buffer);
    private native void nativePlayFile(long handle, String file);
    private native int nativeProcessData(long handle);
    private native void nativeFlush(long handle);
    private native void nativeRelease(long handle);
    private native void nativeDumpFile(long handle, String outFileName);
}
