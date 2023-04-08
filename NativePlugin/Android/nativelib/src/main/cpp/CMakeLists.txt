# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.22.1)

# Declares and names the project.

project("nativelib")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
        nativelib

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        nativelib.cpp)

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

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

#libpcap
set(LIBPCAP_ROOT ./libpcap)
add_definitions(-DPACKAGE_VERSION="1.10.3")
add_definitions(-DHAVE_STRERROR)
add_definitions(-DHAVE_SOCKLEN_T)
include_directories(${LIBPCAP_ROOT})

configure_file(${LIBPCAP_ROOT}/grammar.c ${LIBPCAP_ROOT}/grammar.c)
configure_file(${LIBPCAP_ROOT}/grammar.h ${LIBPCAP_ROOT}/grammar.h)
configure_file(${LIBPCAP_ROOT}/scanner.c ${LIBPCAP_ROOT}/scanner.c)
configure_file(${LIBPCAP_ROOT}/scanner.h ${LIBPCAP_ROOT}/scanner.h)

ADD_LIBRARY(pcap STATIC
        ${LIBPCAP_ROOT}/pcap.c
        ${LIBPCAP_ROOT}/pcap-common.c
        ${LIBPCAP_ROOT}/pcap-usb-linux-common.c
        ${LIBPCAP_ROOT}/pcap-util.c
        ${LIBPCAP_ROOT}/sf-pcap.c
        ${LIBPCAP_ROOT}/sf-pcapng.c
        ${LIBPCAP_ROOT}/pcap-linux.c
        ${LIBPCAP_ROOT}/fad-gifc.c
        ${LIBPCAP_ROOT}/nametoaddr.c
        ${LIBPCAP_ROOT}/etherent.c
        ${LIBPCAP_ROOT}/savefile.c
        ${LIBPCAP_ROOT}/fmtutils.c
        ${LIBPCAP_ROOT}/gencode.c
        ${LIBPCAP_ROOT}/grammar.c
        ${LIBPCAP_ROOT}/scanner.c
        ${LIBPCAP_ROOT}/bpf_filter.c
        ${LIBPCAP_ROOT}/optimize.c
        ${LIBPCAP_ROOT}/missing/strlcpy.c)

target_link_libraries( # Specifies the target library.
        nativelib

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib} pcap)