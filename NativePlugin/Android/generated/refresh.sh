#!/bin/bash
CURRENT_PATH=`pwd`
SUBMODULES_PATH="../submodules"

# libpcap
cd $CURRENT_PATH/$SUBMODULES_PATH/libpcap && ./autogen.sh && ./configure && make
cp $CURRENT_PATH/$SUBMODULES_PATH/libpcap/{grammar.c,grammar.h,scanner.c,scanner.h} $CURRENT_PATH/libpcap/
