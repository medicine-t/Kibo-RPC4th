# Kibo Robot Programming Challenge 4th(2023)


#### INSTALL DEPENDENCIES
- openjdk8 (adoptopenjdk)
  - [記事](https://qiita.com/t-motoki/items/e015950f89e0d17d22d0)
- ADBのインストール
  - `brew install --cask android-platform-tools`
- SDK周りはAndroidStudioでダウンロード
  - ProgrammingManual参照

# How to build(on Mac)
例
```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/adoptopenjdk-8.jdk/Contents/Home
export ANDROID_HOME=$HOME/Library/Android/Sdk
./gradlew --assembleDebug
```
環境構築した上でAndroid Studioでも可
出力ファイルは `./app/build/outputs/apk/debug/app-debug.apk`
