//
// Created by 王经彪 on 2020/2/27.
//

#ifndef SOUNDTOUCH_SCOPTED_JAVA_REF_H
#define SOUNDTOUCH_SCOPTED_JAVA_REF_H

#include <jni.h>
#include "constructor_magic.h"

template <typename T>
class JavaRef;

template <>
class JavaRef<jobject>{

public:
    jobject obj() const { return obj_;}
    bool is_null() const {
        // This is not valid for weak references. For weak references you need to
        // use env->IsSameObject(objc_, nullptr), but that should be avoided anyway
        // since it does not prevent the object from being freed immediately
        // thereafter. Consequently, programmers should not use this check on weak
        // references anyway and should first make a ScopedJavaLocalRef or
        // ScopedJavaGlobalRef before checking if it is null.
        return obj_ == nullptr;
    }
protected:
    constexpr JavaRef():  obj_(nullptr){}
    explicit JavaRef(jobject obj): obj_(obj){}
    jobject obj_;

private:

};

#endif //SOUNDTOUCH_SCOPTED_JAVA_REF_H
