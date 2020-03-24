//
// Created by jch on 20-3-18.
//

#include "JchSoundTouch.h"
#include <stdexcept>
#include <zconf.h>

namespace jch {

    void JchSoundTouch::SetChannels(int channels) {
        channels_ = channels;
        soundTouch_->setChannels(channels);
    }

    void JchSoundTouch::SetSampleRate(int sampleRate) {
        sampleRate_ = sampleRate;
        soundTouch_->setSampleRate(sampleRate);
    }

    void JchSoundTouch::SetAudioFormat(int audioFormat) {
        audioFormat_ = audioFormat;
    }

    void JchSoundTouch::CacheDirectBuffer(JNIEnv *env,
                                          const jch::JavaParamRef<jobject> &byte_buffer) {
        director_buffer_address_ = env->GetDirectBufferAddress(byte_buffer.obj());
        jlong capacity = env->GetDirectBufferCapacity(byte_buffer.obj());
        director_buffer_capacity_in_bytes_ = static_cast<size_t>(capacity);

        jclass clazz = jch::AttachCurrentThreadIfNeeded()->GetObjectClass(processCallback_.obj());
        processMethodId_ = jch::AttachCurrentThreadIfNeeded()->GetMethodID(clazz, "onProcessed", "(I)V");
    }


    int JchSoundTouch::ProcessData() {
        LOGV(_TAG_, "ProcessData");
        short *buf = static_cast<int16_t *>(director_buffer_address_);  // byteArray -> shortArray
        size_t shortBufSize = director_buffer_capacity_in_bytes_ / audioFormat_;
        size_t samples = shortBufSize / channels_;
        return ProcessData(buf, samples, director_buffer_capacity_in_bytes_);
    }


    int JchSoundTouch::ProcessData(short *buf, size_t samples, size_t bufferSize) {

        LOGV(_TAG_, __func__);
        soundTouch_->putSamples(buf, samples);
        int processSamples = 0;
        try {
            do {

                processSamples = soundTouch_->receiveSamples(buf, samples);

                LOGV(_TAG_, "processSamples num: %d", processSamples);
                OnProcessedData(buf, processSamples);       // todo  由于数据不全导致存在脏数据？
            } while (processSamples != 0);
        } catch (const std::runtime_error &e) {

            const char *err = e.what();
            // An exception occurred during processing, return the error message
            LOGV(_TAG_, "JNI exception in SoundTouch::processFile: %s", err);
            errorMsg_ = err;
            return -1;
        }
        LOGV(_TAG_, "process end");
        return 0;
    }

    void JchSoundTouch::OnProcessedData(short *buf, size_t samples) {
        LOGV("JchSoundTouch", "OnProcessedData after putSamples");
        size_t  proccesedSize = samples * audioFormat_* channels_;
        memcpy(director_buffer_address_, buf, proccesedSize);

        dumpData(static_cast<short *>(director_buffer_address_), samples);
        jch::AttachCurrentThreadIfNeeded()->CallVoidMethod(processCallback_.obj(), processMethodId_, proccesedSize);
        LOGV("JchSoundTouch", "OnProcessedData after putSamples end");

    }

    int JchSoundTouch::PlayFile(const std::string &fileName) {

        // open input file
        fileStr_ = fileName;
        WavInFile inFile(fileName.c_str());
        sampleRate_ = inFile.getSampleRate();
        audioFormat_ = inFile.getNumBits() >> 3;
        channels_ = inFile.getNumChannels();
        int bufSampleSize = director_buffer_capacity_in_bytes_ / (channels_*audioFormat_);
        try {
            while (inFile.eof() == 0) {
                int readSamples;
                if (audioFormat_ == 1) {
                    readSamples = inFile.read(static_cast<unsigned char *>(director_buffer_address_), bufSampleSize);
                } else if(audioFormat_ == 2){
                    readSamples = inFile.read(static_cast<short *>(director_buffer_address_), bufSampleSize);
                } else if(audioFormat_ == 4){
                    readSamples = inFile.read(static_cast<float *>(director_buffer_address_), bufSampleSize);
                }

                ProcessData(static_cast<short *>(director_buffer_address_), readSamples, director_buffer_capacity_in_bytes_);
            }

            Flush();
        }catch (const std::runtime_error &e){
            errorMsg_ = e.what();
            LOGV(_TAG_, "play file error : %s", e.what());
            return -1;
        }

        return 0;
    }

    void JchSoundTouch::Flush() {
        LOGV(_TAG_, __func__);
        soundTouch_->flush();

        int processSamples = 0;
        int16_t *buf = static_cast<int16_t *>(director_buffer_address_);  // byteArray -> shortArray
        size_t shortBufSize = director_buffer_capacity_in_bytes_ / audioFormat_;
        size_t samples = shortBufSize / channels_;
        do {
            processSamples = soundTouch_->receiveSamples(buf, samples);
            dumpData(buf, processSamples);
            OnProcessedData(buf, processSamples);       // todo  由于数据不全导致存在脏数据？
        } while (processSamples != 0);

    }

    const char *JchSoundTouch::GetVersion() {
        return soundTouch_->getVersionString();
    }

    void JchSoundTouch::SetTempo(float tempo) {
        soundTouch_->setTempo(tempo);
    }

    void JchSoundTouch::SetPitchSemiTones(float pitch) {
        soundTouch_->setPitchSemiTones(pitch);
    }

    void JchSoundTouch::SetRate(float speed) {
        soundTouch_->setRate(speed);
    }

    void JchSoundTouch::SetTempoChange(double newTempo) {
        soundTouch_->setTempoChange(newTempo);
    }

    void JchSoundTouch::SetPitch(double newPitch) {
        soundTouch_->setPitch(newPitch);
    }

    void JchSoundTouch::SetPitchOctaves(double newPitch) {
        soundTouch_->setPitchOctaves(newPitch);
    }

    void JchSoundTouch::SetRateChange(double newRate) {
        soundTouch_->setRateChange(newRate);
    }

    void JchSoundTouch::EnableAAFilter(bool enable) {
        soundTouch_->setSetting(SETTING_USE_AA_FILTER, enable?1: 0);
    }

    void JchSoundTouch::SetAAFilterLength(int length) {
        soundTouch_->setSetting(SETTING_AA_FILTER_LENGTH, length);
    }

    void JchSoundTouch::EnableQuickSeek(bool enable) {
        soundTouch_->setSetting(SETTING_USE_QUICKSEEK, enable?1:0);
    }

    const std::string JchSoundTouch::GetErrorStr() {
        return errorMsg_;
    }

    bool JchSoundTouch::setDumpFile(const std::string &file) {
        dump_ = true;
        try {
            wavOutFile_ = new WavOutFile(file.c_str(), sampleRate_, audioFormat_ * 8, channels_);
        } catch (const std::runtime_error &e) {
            errorMsg_ = e.what();
            LOGV(_TAG_, "dumpFile error:%s", e.what());
            return false;
        }

        return true;
    }

    int JchSoundTouch::dumpData(short *buf, int samples) {

        if (!dump_) {
            return 0;
        }
        LOGV(_TAG_, "dump data %d", samples);
        wavOutFile_->write(buf, samples);
        return 0;
    }
}