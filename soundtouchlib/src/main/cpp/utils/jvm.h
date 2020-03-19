//
// Created by 王经彪 on 2020/2/27.
//

#ifndef JCH_SOUNDTOUCH_JVM_H
#define JCH_SOUNDTOUCH_JVM_H

#include <jni.h>

namespace jch{

    jint InitGlobalJniVariables(JavaVM* jvm);
    // Return a |JNIEnv*| usable on this thread or NULL if this thread is detached.
    JNIEnv* GetEnv();

    JavaVM* GetJVM();

    JNIEnv* AttachCurrentThreadIfNeeded();

}

#endif //JCH_SOUNDTOUCH_JVM_H
