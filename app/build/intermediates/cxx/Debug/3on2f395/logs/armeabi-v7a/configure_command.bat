@echo off
"C:\\Users\\qamar\\AppData\\Local\\Android\\Sdk\\ndk\\23.2.8568313\\ndk-build.cmd" ^
  "NDK_PROJECT_PATH=null" ^
  "APP_BUILD_SCRIPT=C:\\Users\\qamar\\AndroidStudioProjects\\moonlight-android\\app\\src\\main\\jni\\Android.mk" ^
  "NDK_APPLICATION_MK=C:\\Users\\qamar\\AndroidStudioProjects\\moonlight-android\\app\\src\\main\\jni\\Application.mk" ^
  "APP_ABI=armeabi-v7a" ^
  "NDK_ALL_ABIS=armeabi-v7a" ^
  "NDK_DEBUG=1" ^
  "APP_PLATFORM=android-16" ^
  "NDK_OUT=C:\\Users\\qamar\\AndroidStudioProjects\\moonlight-android\\app\\build\\intermediates\\cxx\\Debug\\3on2f395/obj" ^
  "NDK_LIBS_OUT=C:\\Users\\qamar\\AndroidStudioProjects\\moonlight-android\\app\\build\\intermediates\\cxx\\Debug\\3on2f395/lib" ^
  "PRODUCT_FLAVOR=nonRoot" ^
  "APP_SHORT_COMMANDS=false" ^
  "LOCAL_SHORT_COMMANDS=false" ^
  -B ^
  -n
