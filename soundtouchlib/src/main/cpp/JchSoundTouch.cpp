//
// Created by jch on 20-3-18.
//

#include "JchSoundTouch.h"
#include <stdexcept>

namespace jch {

    void JchSoundTouch::SetChannels(int channels) {
        channels_ = channels;
        soundTouch_->setChannels(channels);
    }

    void JchSoundTouch::SetSampleRate(int sampleRate) {
        sampleRate_ = sampleRate;
        soundTouch_->setSampleRate(sampleRate);
    }

    void JchSoundTouch::CacheDirectBuffer(JNIEnv *env,
                                          const jch::JavaParamRef<jobject> &byte_buffer) {
        director_buffer_address_ = env->GetDirectBufferAddress(byte_buffer.obj());
        jlong capacity = env->GetDirectBufferCapacity(byte_buffer.obj());
        director_buffer_capacity_in_bytes_ = static_cast<size_t>(capacity);

        jclass clazz =  jch::AttachCurrentThreadIfNeeded()->GetObjectClass(processCallback_.obj());
        processMethodId_ =  jch::AttachCurrentThreadIfNeeded()->GetMethodID(clazz, "onProcessed", "(I)V");
    }

    int JchSoundTouch::ProcessData() {
        LOGV(_TAG_, "ProcessData");
        short *buf = static_cast<int16_t *>(director_buffer_address_);  // byteArray -> shortArray
        size_t shortBufSize = director_buffer_capacity_in_bytes_/ sizeof(int16_t);
        size_t samples = shortBufSize/channels_;
        return ProcessData(buf, shortBufSize, samples);
    }


    int JchSoundTouch::ProcessData(short *buf, size_t samples, size_t bufferSize) {

        LOGV(_TAG_, "ProcessData before putSamples");
        soundTouch_->putSamples(buf, samples);
        LOGV(__FILE__, "after putSamples");
        int processSamples = 0;
        try {
            do{

                processSamples = soundTouch_->receiveSamples(buf, samples);
                LOGV(_TAG_, "processSamples num: %d", processSamples);
                OnProcessedData(buf, processSamples);       // todo  由于数据不全导致存在脏数据？
            }while (processSamples != 0);
        }catch (const std::runtime_error &e){

            const char *err = e.what();
            // An exception occurred during processing, return the error message
            LOGV(_TAG_,"JNI exception in SoundTouch::processFile: %s", err);
            errorMsg_ = err;
            return -1;
        }
        LOGV(_TAG_, "process end");
        return 0;
    }

    void JchSoundTouch::OnProcessedData(short *buf, size_t samples) {
        LOGV("JchSoundTouch", "OnProcessedData after putSamples");
        memcpy(director_buffer_address_, buf, samples* sizeof(int16_t));

        jch::AttachCurrentThreadIfNeeded()->CallVoidMethod(processCallback_.obj(), processMethodId_, director_buffer_capacity_in_bytes_);
        LOGV("JchSoundTouch", "OnProcessedData after putSamples end");

    }

    void JchSoundTouch::flush() {
        soundTouch_->flush();

        int processSamples = 0;
        int16_t *buf = static_cast<int16_t *>(director_buffer_address_);  // byteArray -> shortArray
        size_t shortBufSize = director_buffer_capacity_in_bytes_/ sizeof(int16_t);
        size_t samples = shortBufSize/channels_;
        do{
            processSamples = soundTouch_->receiveSamples(buf, samples);
            OnProcessedData(buf, processSamples);       // todo  由于数据不全导致存在脏数据？
        }while (processSamples != 0);

    }

    const char* JchSoundTouch::GetVersion() {
        return soundTouch_->getVersionString();
    }

    void JchSoundTouch::SetTempo(float tempo) {
        soundTouch_->setTempo(tempo);
    }

    void JchSoundTouch::SetPitchSemiTones(float pitch) {
        soundTouch_->setPitchSemiTones(pitch);
    }

    void JchSoundTouch::SetSpeed(float speed) {
        soundTouch_->setRate(speed);
    }

    const std::string JchSoundTouch::GetErrorStr() {
        return errorMsg_;
    }


}