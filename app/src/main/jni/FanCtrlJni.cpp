
#include <jni.h>
#include <utils/Log.h>
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <errno.h>
#include <linux/ioctl.h>

#define LOG_TAG "fmtx"

/**
 * Created by zhangzhaolei on 2018/1/30.
 */

jboolean disableFan(JNIEnv *env, jobject thiz)
{
    int ret = 0;
    ret = system("echo off > /sys/class/gpio_fan/fan_en");
    ALOGD("%s, [ret = %d]\n", __func__, ret);
    return ret == 0 ? JNI_TRUE : JNI_FALSE;
}

jboolean enableFan(JNIEnv *env, jobject thiz)
{
    int ret = 0;
    ret = system("echo on > /sys/class/gpio_fan/fan_en");
    ALOGD("%s, [ret = %d]\n", __func__, ret);
    return ret == 0 ? JNI_TRUE : JNI_FALSE;
}

static const char *classPathNameRx = "com/erobbing/cputempmonitor/FanCtrlNative";

static JNINativeMethod methodsRx[] = {
    {"disableFan", "()Z", (void*)disableFan},
    {"enableFan", "()Z", (void*)enableFan},
};

/*
 * Register several native methods for one class.
 */
static jint registerNativeMethods(JNIEnv* env, const char* className,
    JNINativeMethod* gMethods, int numMethods)
{
    jclass clazz;

    clazz = env->FindClass(className);
    if (env->ExceptionCheck()) {
        env->ExceptionDescribe();
        env->ExceptionClear();
    }
    if (clazz == NULL) {
        ALOGE("Native registration unable to find class '%s'", className);
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        ALOGE("RegisterNatives failed for '%s'", className);
        return JNI_FALSE;
    }

    ALOGD("%s, success\n", __func__);
    return JNI_TRUE;
}

/*
 * Register native methods for all classes we know about.
 *
 * returns JNI_TRUE on success.
 */
static jint registerNatives(JNIEnv* env)
{
    jint ret = JNI_FALSE;

    if (registerNativeMethods(env, classPathNameRx,methodsRx,
        sizeof(methodsRx) / sizeof(methodsRx[0]))) {
        ret = JNI_TRUE;
    }

    ALOGD("%s, done\n", __func__);
    return ret;
}

// ----------------------------------------------------------------------------

/*
 * This is called by the VM when the shared library is first loaded.
 */

typedef union {
    JNIEnv* env;
    void* venv;
} UnionJNIEnvToVoid;

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    UnionJNIEnvToVoid uenv;
    uenv.venv = NULL;
    jint result = -1;
    JNIEnv* env = NULL;

    ALOGI("JNI_OnLoad");

    if (vm->GetEnv(&uenv.venv, JNI_VERSION_1_4) != JNI_OK) {
        ALOGE("ERROR: GetEnv failed");
        goto fail;
    }
    env = uenv.env;

    if (registerNatives(env) != JNI_TRUE) {
        ALOGE("ERROR: registerNatives failed");
        goto fail;
    }

    result = JNI_VERSION_1_4;

fail:
    return result;
}

