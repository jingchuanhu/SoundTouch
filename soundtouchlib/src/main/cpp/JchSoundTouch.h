//
// Created by jch on 20-3-18.
//

#ifndef SOUNDTOUCH_JCHSOUNDTOUCH_H
#define SOUNDTOUCH_JCHSOUNDTOUCH_H

#include "scopted_java_ref.h"
#include "SoundTouch.h"
#include <jni.h>
#include <syslog.h>
#include <android/log.h>

#define LOGV(TAG, ...)   __android_log_print((int)ANDROID_LOG_INFO, TAG, __VA_ARGS__)


extern "C" {

namespace jch {
    using namespace soundtouch;


    class JchSoundTouch {

    public:

        explicit JchSoundTouch(const JavaParamRef<jobject> &callBack) : channels_(1),
                                                                        sampleRate_(8000),
                                                                        director_buffer_address_(
                                                                                nullptr),
                                                                        processCallback_(
                                                                                callBack.obj()),
                                                                        soundTouch_(
                                                                                new SoundTouch()),
                                                                        errorMsg_(
                                                                                nullptr) {
            jch::AttachCurrentThreadIfNeeded();
            jclass clazz = jch::GetEnv()->GetObjectClass(processCallback_.obj());
            processMethodId_ = jch::GetEnv()->GetMethodID(clazz, "onProcessed", "(I)V");
        };

        const char *GetVersion();

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

        int channels_;
        int sampleRate_;
        void *director_buffer_address_;
        JavaParamRef<jobject> processCallback_;
        jmethodID processMethodId_;
        int director_buffer_capacity_in_bytes_;
        SoundTouch *soundTouch_;
        std::string errorMsg_;

    };

} ;

}


#endif //SOUNDTOUCH_JCHSOUNDTOUCH_H
