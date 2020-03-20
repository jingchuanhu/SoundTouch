//
// Created by jch on 20-3-18.
//
#include <jni.h>
#include "scopted_java_ref.h"
#include "JchSoundTouch.h"
#include "jvm.h"

extern "C" {

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved) {

    return jch::InitGlobalJniVariables(jvm);
}

JNIEXPORT void JNICALL
JNI_OnUnLoad(JavaVM *jvm, void *reserved) {

}

JNIEXPORT jlong JNICALL
Java_com_jch_soundtouchlib_JchSoundTouch_nativeGetInstance(JNIEnv *env, jclass clazz,
                                                           jobject callback) {
    // TODO: implement getInstance()
    jch::ScopedJavaLocalRef<jobject> callbackRef(env,jch::JavaParamRef<jobject >(callback));
    return reinterpret_cast<long>(new jch::JchSoundTouch(callbackRef));
}

JNIEXPORT void JNICALL
Java_com_jch_soundtouchlib_JchSoundTouch_nativeSetChannels(JNIEnv *env, jobject thiz, jlong handle,
                                                           jint channels) {
    reinterpret_cast<jch::JchSoundTouch *>(handle)->SetChannels(channels);
}
JNIEXPORT void JNICALL
Java_com_jch_soundtouchlib_JchSoundTouch_nativeSetSampleRte(JNIEnv *env, jobject thiz, jlong handle,
                                                            jint sample_rte) {
    reinterpret_cast<jch::JchSoundTouch *>(handle)->SetSampleRate(sample_rte);
}

JNIEXPORT void JNICALL
Java_com_jch_soundtouchlib_JchSoundTouch_nativeSetSpeed(JNIEnv *env, jobject thiz, jlong handle,
                                                        jfloat speed) {
    reinterpret_cast<jch::JchSoundTouch*>(handle)->SetSpeed(speed);
}
JNIEXPORT void JNICALL
Java_com_jch_soundtouchlib_JchSoundTouch_nativeSetTempo(JNIEnv *env, jobject thiz, jlong handle,
                                                        jfloat tempo) {
    reinterpret_cast<jch::JchSoundTouch*>(handle)->SetTempo(tempo);
}
JNIEXPORT void JNICALL
Java_com_jch_soundtouchlib_JchSoundTouch_nativeSetPitchSemiTones(JNIEnv *env, jobject thiz,
                                                                 jlong handle, jfloat pitch) {
    reinterpret_cast<jch::JchSoundTouch*>(handle)->SetPitchSemiTones(pitch);
}

JNIEXPORT jstring JNICALL
Java_com_jch_soundtouchlib_JchSoundTouch_nativeGetVersion(JNIEnv *env, jobject thiz,
                                                          jlong handle) {
    const char *version = reinterpret_cast<jch::JchSoundTouch *>(handle)->GetVersion();
    return env->NewStringUTF(version);
}

JNIEXPORT void JNICALL
Java_com_jch_soundtouchlib_JchSoundTouch_nativeCacheBuffer(JNIEnv *env, jobject thiz, jlong handle,
                                                           jobject buffer) {
    reinterpret_cast<jch::JchSoundTouch *>(handle)->CacheDirectBuffer(env, jch::JavaParamRef<jobject>(
            buffer));
}

JNIEXPORT jint JNICALL
Java_com_jch_soundtouchlib_JchSoundTouch_nativeProcessData(JNIEnv *env, jobject thiz, jlong handle) {
    LOGV(_TAG_, "nativeProcessData");
    reinterpret_cast<jch::JchSoundTouch *>(handle)->ProcessData();
    LOGV(_TAG_, "nativeProcessData end");
    return 0;
}

JNIEXPORT jstring JNICALL
Java_com_jch_soundtouchlib_JchSoundTouch_nativeGetErrorMsg(JNIEnv *env, jobject thiz, jlong handle) {
    std::string errorMsg = reinterpret_cast<jch::JchSoundTouch *>(handle)->GetErrorStr();
    return env->NewStringUTF(errorMsg.c_str());
}

JNIEXPORT void JNICALL
Java_com_jch_soundtouchlib_JchSoundTouch_nativeFlush(JNIEnv *env, jobject thiz, jlong handle) {
    reinterpret_cast<jch::JchSoundTouch*>(handle)->flush();
}

JNIEXPORT void JNICALL
Java_com_jch_soundtouchlib_JchSoundTouch_nativeDumpFile(JNIEnv *env, jobject thiz, jlong handle, jstring out_file_name) {

    const std::string fileName = env->GetStringUTFChars(out_file_name, JNI_FALSE);
    reinterpret_cast<jch::JchSoundTouch*>(handle)->setDumpFile(fileName);
    env->ReleaseStringUTFChars(out_file_name, fileName.c_str());
}

JNIEXPORT void JNICALL
Java_com_jch_soundtouchlib_JchSoundTouch_nativeRelease(JNIEnv *env, jobject thiz, jlong handle) {

    delete ((jch::JchSoundTouch*)handle);
}

}