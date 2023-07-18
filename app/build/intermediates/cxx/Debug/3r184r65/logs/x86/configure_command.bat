@echo off
"C:\\Users\\HP\\AppData\\Local\\Android\\Sdk\\ndk\\23.2.8568313\\ndk-build.cmd" ^
  "NDK_PROJECT_PATH=null" ^
  "APP_BUILD_SCRIPT=C:\\vmsProject\\moonlightAntplay\\app\\src\\main\\jni\\Android.mk" ^
  "NDK_APPLICATION_MK=C:\\vmsProject\\moonlightAntplay\\app\\src\\main\\jni\\Application.mk" ^
  "APP_ABI=x86" ^
  "NDK_ALL_ABIS=x86" ^
  "NDK_DEBUG=1" ^
  "APP_PLATFORM=android-16" ^
  "NDK_OUT=C:\\vmsProject\\moonlightAntplay\\app\\build\\intermediates\\cxx\\Debug\\3r184r65/obj" ^
  "NDK_LIBS_OUT=C:\\vmsProject\\moonlightAntplay\\app\\build\\intermediates\\cxx\\Debug\\3r184r65/lib" ^
  "PRODUCT_FLAVOR=nonRoot" ^
  "APP_SHORT_COMMANDS=false" ^
  "LOCAL_SHORT_COMMANDS=false" ^
  -B ^
  -n
