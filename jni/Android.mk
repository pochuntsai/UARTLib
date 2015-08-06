LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := hello-uart
LOCAL_SRC_FILES := hello-uart.c
LOCAL_LDLIBS += -LLOG
LOCAL_LDLIBS += -lm
LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
include $(BUILD_SHARED_LIBRARY)
