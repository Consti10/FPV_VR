# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.

cmake_minimum_required(VERSION 3.4.1)

# Specifies a library name, specifies whether the library is STATIC or
# SHARED, and provides relative paths to the source code. You can
# define multiple libraries by adding multiple add.library() commands,
# and CMake builds them for you. When you build your app, Gradle
# automatically packages shared libraries with your APK.

add_library( # Specifies the name of the library.
             parser

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             app/src/main/c-ndk/parser.c )

             # Specifies a path to native header files.
             include_directories(app/src/main/c-ndk/)

add_library( # Specifies the name of the library.
                          qcom

                          # Sets the library as a shared library.
                          SHARED

                          # Provides a relative path to your source file(s).
                          app/src/main/c-ndk/qcom.c )

                          # Specifies a path to native header files.
                          include_directories(app/src/main/c-ndk/)

add_library( # Specifies the name of the library.
             VsyncHelperNative

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             app/src/main/c-ndk/package2/VsyncHelperNative.c )

             # Specifies a path to native header files.
             include_directories(app/src/main/c-ndk/package2/)

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in the
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       parser

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )
target_link_libraries( # Specifies the target library.
                       VsyncHelperNative

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )

target_link_libraries( # Specifies the target library.
                       qcom

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )

target_link_libraries(qcom
                      VsyncHelperNative
                      android
                      log
                      EGL
                      GLESv2)


