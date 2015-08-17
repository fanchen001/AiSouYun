LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := host
LOCAL_SRC_FILES := com_fanchen_aisou_jni_HostURL.c \
				   com_fanchen_aisou_jni_HostURL.h
include $(BUILD_SHARED_LIBRARY)