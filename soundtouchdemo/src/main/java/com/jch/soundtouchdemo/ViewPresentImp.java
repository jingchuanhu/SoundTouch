package com.jch.soundtouchdemo;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;

import com.jch.soundtouchlib.JchSoundTouch;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ViewPresentImp implements MainActivity.ViewPresent, JchSoundTouch.JchSoundTouchCallback {

    private static final String TAG = ViewPresentImp.class.getSimpleName();
    private Context context;
    private JchSoundTouch soundTouch;
    private int channel = 1;
    private int sampleRate = 48000;
    private int audioFormate = AudioFormat.ENCODING_PCM_16BIT;
    private int bufferSizeInByte = 1024;
    private boolean starting;
    private boolean endWithError;
    private ByteBuffer dataBuf;
    AudioRecord audioRecord;
    LocalAudioTrack mAudioTrack;
//    AudioTrack audioTrack;

    @Override
    public void init(Context context, float speed, float pitch, float tempo) {
        this.context = context;
        starting = false;
        soundTouch = new JchSoundTouch(channel, sampleRate, this);
//        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, sampleRate, channel, audioFormate, bufferSizeInByte);
        Log.d(TAG, "init: " + audioRecord.getState());
        mAudioTrack = new LocalAudioTrack(context);
        mAudioTrack.init(sampleRate, channel);
//        if (Build.VERSION.SDK_INT >= 21) {
//            audioTrack = createAudioTrackOnLollipopOrHigher(sampleRate, channel, bufferSizeInByte);
//        } else {
//            audioTrack = createAudioTrackOnLowerThanLollipop(sampleRate, channel, bufferSizeInByte);
//        }

        setPitch(pitch);
        setSpeed(speed);
        setTempo(tempo);
    }

    @Override
    public void setSpeed(float speed) {
        soundTouch.setSpeed(speed);
    }

    @Override
    public void setTempo(float tempo) {
        soundTouch.setTempo(tempo);
    }

    @Override
    public void setPitch(float pitch) {
        soundTouch.setPitch(pitch);
    }

    @Override
    public void startSoundTouch() {
        starting = true;
        new AudioThread().start();
    }

    @Override
    public void stopSoundTouch() {

        starting = false;
    }

    @Override
    public void release() {

//        audioTrack.release();
        mAudioTrack.release();
        audioRecord.release();
        soundTouch.realse();
    }

    private class AudioThread extends Thread {

        @Override
        public void run() {

            if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                audioRecord.startRecording();
            }
            mAudioTrack.start();
            mAudioTrack.enableSpeakerPhone(true);

            dataBuf = ByteBuffer.allocateDirect(bufferSizeInByte);
            soundTouch.attachDataBuffer(dataBuf);
            endWithError = true;
            while (starting || !endWithError) {
                audioRecord.read(dataBuf, bufferSizeInByte);
                if (!soundTouch.processData(bufferSizeInByte)) {
                    starting = false;
                    endWithError = true;
                    Log.e(TAG, "soundTouch error: " + soundTouch.getErrorMsg());
                }
            }

            if (!endWithError) {
                soundTouch.flush();
            }

            audioRecord.stop();
            mAudioTrack.stop();
//            audioTrack.stop();

        }
    }

    @Override
    public void onProcessed(int bufferSize) {
        Log.d(TAG, "onProcessed: " + bufferSize);
        mAudioTrack.playByte(dataBuf);
//        writeBytes(audioTrack, dataBuf, bufferSizeInByte);
    }

    private int writeBytes(AudioTrack audioTrack, ByteBuffer byteBuffer, int sizeInBytes) {
        if (Build.VERSION.SDK_INT >= 21) {
            return audioTrack.write(byteBuffer, sizeInBytes, AudioTrack.WRITE_BLOCKING);
        } else {
            return audioTrack.write(byteBuffer.array(), byteBuffer.arrayOffset(), sizeInBytes);
        }
    }


    /*****************JchSoundTouchCallback****************/
    // Creates and AudioTrack instance using AudioAttributes and AudioFormat as input.
    // It allows certain platforms or routing policies to use this information for more
    // refined volume or routing decisions.
    @TargetApi(21)
    private static AudioTrack createAudioTrackOnLollipopOrHigher(
            int sampleRateInHz, int channelConfig, int bufferSizeInBytes) {
        // TODO(henrika): use setPerformanceMode(int) with PERFORMANCE_MODE_LOW_LATENCY to control
        // performance when Android O is supported. Add some logging in the mean time.
        final int nativeOutputSampleRate =
                AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_VOICE_CALL);
        if (sampleRateInHz != nativeOutputSampleRate) {
            Log.w(TAG, "Unable to use fast mode since requested sample rate is not native");
        }
        // Create an audio track where the audio usage is for VoIP and the content type is speech.
        return new AudioTrack(new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build(),
                new AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRateInHz)
                        .setChannelMask(channelConfig)
                        .build(),
                bufferSizeInBytes, AudioTrack.MODE_STREAM, AudioManager.AUDIO_SESSION_ID_GENERATE);
    }

    @SuppressWarnings("deprecation") // Deprecated in API level 25.
    private static AudioTrack createAudioTrackOnLowerThanLollipop(
            int sampleRateInHz, int channelConfig, int bufferSizeInBytes) {
        return new AudioTrack(AudioManager.STREAM_VOICE_CALL, sampleRateInHz, channelConfig,
                AudioFormat.ENCODING_PCM_16BIT, bufferSizeInBytes, AudioTrack.MODE_STREAM);
    }

}
