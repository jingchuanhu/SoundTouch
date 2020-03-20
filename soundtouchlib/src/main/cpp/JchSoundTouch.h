//
// Created by jch on 20-3-18.
//

#ifndef SOUNDTOUCH_JCHSOUNDTOUCH_H
#define SOUNDTOUCH_JCHSOUNDTOUCH_H

#include "scopted_java_ref.h"
#include "SoundTouch.h"
#include <jni.h>
#include <syslog.h>
#include <string>
#include <android/log.h>
#include "soundtouch/WavFile.h"

#define LOGV(TAG, ...)   __android_log_print((int)ANDROID_LOG_INFO, TAG, __VA_ARGS__)

extern "C" {

const static char *_TAG_ = "JchSoundTouch";

namespace jch {
    using namespace soundtouch;


    class JchSoundTouch {

    public:

        explicit JchSoundTouch(const ScopedJavaLocalRef<jobject> &callBack) : channels_(1),
                                                                              sampleRate_(48000),
                                                                              director_buffer_address_(nullptr),
                                                                              processCallback_(callBack),
                                                                              soundTouch_(new SoundTouch()),
                                                                              dump_(false),
                                                                              audioFormat_(sizeof(short)){
        };

        const char *GetVersion();

        bool setDumpFile(const std::string &file);

        void SetTempo(float tempo);

        void SetPitchSemiTones(float pitch);

        void SetSpeed(float speed);

        void SetChannels(int channels);

        void SetSampleRate(int sampleRate);

        void CacheDirectBuffer(JNIEnv *env, const JavaParamRef<jobject> &byte_buffer);

        int ProcessData();

        const std::string GetErrorStr();

        void flush();

        ~JchSoundTouch() {
            delete wavOutFile_;
            LOGV("JchSoundTouch", "release");
        }

    private:

        /**
         * int16
         * @param buf
         * @param samples
         */
        int ProcessData(short *buf, size_t samples, size_t bufferSize);

        void OnProcessedData(short *buf, size_t samples);

        int dumpData(short* buf, int samples);

        int channels_;
        int sampleRate_;
        int audioFormat_;
        void *director_buffer_address_;
        ScopedJavaGlobalRef<jobject> processCallback_;
        jmethodID processMethodId_;
        int director_buffer_capacity_in_bytes_;
        SoundTouch *soundTouch_;
        std::string errorMsg_;
        bool dump_;
        WavOutFile *wavOutFile_;

    };

} ;

}


#endif //SOUNDTOUCH_JCHSOUNDTOUCH_H
