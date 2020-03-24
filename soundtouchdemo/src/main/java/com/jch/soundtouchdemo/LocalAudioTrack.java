package com.jch.soundtouchdemo;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.nio.ByteBuffer;

public class LocalAudioTrack {

    private static final String TAG = LocalAudioTrack.class.getSimpleName();
    private static AudioTrack audioTrack;
    private int sampleRate;
    private int bufferSize;
    // Requested size of each recorded buffer provided to the client.
    private static final int CALLBACK_BUFFER_SIZE_MS = 10;

    // Average number of callbacks per second.
    private static final int BUFFERS_PER_SECOND = 1000 / CALLBACK_BUFFER_SIZE_MS;
    private static final int BITS_PER_SAMPLE = 16;
    private int channels;
    private boolean isAlive = false;
    private Context context;

    public LocalAudioTrack(Context context) {
        this.context = context;
    }

    public void init(int sampleRate, int channels) {
        this.sampleRate = sampleRate;
        this.channels = channels;

        int minBufferSizeInBytes = AudioTrack.getMinBufferSize(sampleRate, channelCountToConfiguration(channels), AudioFormat.ENCODING_PCM_16BIT);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, channelCountToConfiguration(channels), AudioFormat.ENCODING_PCM_16BIT, minBufferSizeInBytes, AudioTrack.MODE_STREAM);
    }

    public void start() {

        final int bytesPerFrame = channelCountToConfiguration(channels) * (BITS_PER_SAMPLE / 8);
        bufferSize = bytesPerFrame * (sampleRate / BUFFERS_PER_SECOND);
        audioTrack.play();
        isAlive = true;
//        trackThread = new TrackThread();
//        trackThread.isAlive = true;
//        trackThread.start();

    }

    public void enableSpeakerPhone(boolean speakerPhone){
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } else {
            audioManager.setMode(AudioManager.MODE_IN_CALL);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                    audioManager.getStreamMaxVolume(AudioManager.MODE_IN_COMMUNICATION), AudioManager.FX_KEY_CLICK);
        } else {
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                    audioManager.getStreamMaxVolume(AudioManager.MODE_IN_CALL), AudioManager.FX_KEY_CLICK);
        }

        audioManager.setSpeakerphoneOn(speakerPhone);            //扬声器播放
    }

    public void playByte(ByteBuffer buffer, int size){
        if (!isAlive) {
            return;
        }
        buffer.rewind();

//        int sizeInBytes = buffer.capacity();
        buffer.position(0);
        buffer.rewind();
//        buffer.flip();
        int bytesWritten = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bytesWritten = writeOnLollipop(audioTrack, buffer, size);
        } else {
            bytesWritten = writePreLollipop(audioTrack, buffer, size);
        }

        if (bytesWritten != size) {
            Log.e(TAG, "AudioTrack.write failed: " + bytesWritten);
        }
    }

    public void playByte(ByteBuffer buffer) {

        playByte(buffer, buffer.capacity());

    }

    public void stop() {

        isAlive = false;
        audioTrack.stop();
        audioTrack.flush();
    }

    public void release(){
        audioTrack.release();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private int writeOnLollipop(AudioTrack audioTrack, ByteBuffer byteBuffer, int sizeInBytes) {
        return audioTrack.write(byteBuffer, sizeInBytes, AudioTrack.WRITE_BLOCKING);
    }

    private int writePreLollipop(AudioTrack audioTrack, ByteBuffer byteBuffer, int sizeInBytes) {
        return audioTrack.write(byteBuffer.array(), byteBuffer.arrayOffset(), sizeInBytes);
    }

    private int channelCountToConfiguration(int channels) {
        return (channels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO);
    }


}
