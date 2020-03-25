# Copyright (C) 2010 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_C_INCLUDES += $(LOCAL_PATH)/../cpp $(LOCAL_PATH)/../cpp/utils $(LOCAL_PATH)/../cpp/soundtouch/include
# *** Remember: Change -O0 into -O2 in add-applications.mk ***

LOCAL_MODULE    := jchSoundtouch
LOCAL_SRC_FILES := ../cpp/jchSoundTouch-jni.cpp ../cpp/JchSoundTouch.cpp ../cpp/utils/jvm.cpp \
                ../cpp/soundtouch/AAFilter.cpp  ../cpp/soundtouch/FIFOSampleBuffer.cpp \
                ../cpp/soundtouch/mmx_optimized.cpp \
                ../cpp/soundtouch/FIRFilter.cpp ../cpp/soundtouch/cpu_detect_x86.cpp \
                ../cpp/soundtouch/sse_optimized.cpp ../cpp/soundtouch/WavFile.cpp \
                ../cpp/soundtouch/RateTransposer.cpp ../cpp/soundtouch/SoundTouch.cpp \
                ../cpp/soundtouch/InterpolateCubic.cpp ../cpp/soundtouch/InterpolateLinear.cpp \
                ../cpp/soundtouch/InterpolateShannon.cpp ../cpp/soundtouch/TDStretch.cpp \
                ../cpp/soundtouch/BPMDetect.cpp ../cpp/soundtouch/PeakFinder.cpp

# for native audio
LOCAL_SHARED_LIBRARIES += -lgcc
# --whole-archive -lgcc
# for logging
LOCAL_LDLIBS    += -llog
LOCAL_LDLIBS += -latomic
# for native asset manager
#LOCAL_LDLIBS    += -landroid

# Custom Flags:
# -fvisibility=hidden : don't export all symbols
LOCAL_CFLAGS += -fvisibility=hidden -fdata-sections -ffunction-sections

# OpenMP mode : enable these flags to enable using OpenMP for parallel computation
#LOCAL_CFLAGS += -fopenmp
#LOCAL_LDFLAGS += -fopenmp


# Use ARM instruction set instead of Thumb for improved calculation performance in ARM CPUs
LOCAL_ARM_MODE := arm

include $(BUILD_SHARED_LIBRARY)