# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.
cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -std=c99 c++11 -Wall -fvisibility=hidden -fdata-sections -ffunction-sections -fexceptions")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

include_directories(soundtouch/include)
include_directories(utils)

add_library( # Sets the name of the library.
        soundtouch

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
        soundtouch/AAFilter.cpp
        soundtouch/BPMDetect.cpp
        soundtouch/FIFOSampleBuffer.cpp
        soundtouch/FIRFilter.cpp
        soundtouch/cpu_detect_x86.cpp
        soundtouch/InterpolateCubic.cpp
        soundtouch/InterpolateLinear.cpp
        soundtouch/InterpolateShannon.cpp
        soundtouch/PeakFinder.cpp
        soundtouch/RateTransposer.cpp
        soundtouch/SoundTouch.cpp
        soundtouch/sse_optimized.cpp
        soundtouch/TDStretch.cpp
        soundtouch/WavFile.cpp
        soundtouch/mmx_optimized.cpp
        jchSoundTouch-jni.cpp
        soundtouch-jni.cpp
        JchSoundTouch.cpp
        utils/jvm.cpp
        )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log)

#find_library(gcc-lib gcc)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        soundtouch

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib})