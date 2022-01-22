#include "myHeader.h"
#include "cf_wayzer_imeFix_JNIImpl.h"
#include <iostream>
#include <stdio.h>

JNIEnv *_jenv;
jclass _jcls;
HWND window;

JNIEXPORT jboolean JNICALL Java_cf_wayzer_imeFix_JNIImpl_setup(JNIEnv *jenv, jclass jcls)
{
  _jenv = jenv;
  _jcls = jcls;
  window = FindWindow(NULL, "Mindustry");
  if (window == NULL)
  {
    log("Not found window");
    return false;
  }
  log("Begin Setup!");
  return setupImm() && setupCtf();
}

void log(std::string str)
{
  static jmethodID logMethod = _jenv->GetStaticMethodID(_jcls, "log", "(Ljava/lang/String;)V");
  static int failToLog = false;
  if (failToLog)
    return;

  if (_jenv->ExceptionCheck())
  {
    _jenv->ExceptionDescribe();
    _jenv->ExceptionClear();
    failToLog = true;
    return;
  }
  jstring jstr = _jenv->NewStringUTF(str.c_str());
  _jenv->CallStaticVoidMethod(_jcls, logMethod, jstr);
}

JNIEXPORT jboolean JNICALL Java_cf_wayzer_imeFix_JNIImpl_setPos(JNIEnv *, jclass, jint x, jint y)
{
  return setPos(x, y);
}
JNIEXPORT jboolean JNICALL Java_cf_wayzer_imeFix_JNIImpl_setOpen(JNIEnv *, jclass, jboolean show)
{
  return setOpen(show);
};