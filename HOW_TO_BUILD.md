# How to build for HTC


## setup SDK

make folder ./third_party/wavesdk/build

```bash
mkdir -p third_party/wavesdk
mkdir -p third_party/wavesdk/build
```

download 3.2.0 VIVE SDK from [@HTC VIVE Native SDK download archive](https://developer.vive.com/resources/vive-wave/download/archive/)

if it impossible - use already downloaded SDK from ./third_party/downloaded

unzip SDK

copy "repo" folder from unpacked SDK into ./third_party/wavesdk/ folder

unpack ./third_party/wavesdk/repo/com/htc/vr/wvr_client/3.2.0/wvr_client-3.2.0.aar into ./third_party/wavesdk/build

(change .arr to .zip and use unzip)

check exist of file: ./third_party/wavesdk/build/wvr_client-3.2.0/jni/arm64-v8a/libwvr_api.so


## download VBR repo into ./app/src/main/cpp/vrb

```bash
git submodule update --init --recursive
```

if it not work - make next two steps (only if previous command not work !):
step 1
download repo from [@github.com/MozillaReality/vrb commit 4d13824588bc7127ab9d5ef015b2a1a4cfbb7430](https://github.com/MozillaReality/vrb/tree/4d13824588bc7127ab9d5ef015b2a1a4cfbb7430)
and place into ./app/src/main/cpp/vrb

step 2
and also you should the same download [@github.com/floooh/gliml commit aea9653b2e3a98de647087c04d9c8337c8683f92](https://github.com/floooh/gliml/tree/aea9653b2e3a98de647087c04d9c8337c8683f92)
and place into ./app/src/main/cpp/vrb/third_party/gliml


## build

```bash
./gradlew
./gradlew assembleWavevrArm64
```


## result APK

result APK located in  ./app/build/outputs/apk/wavevrArm64/debug/FirefoxReality-wavevr-arm64-debug.apk
