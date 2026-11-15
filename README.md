# Animation Lock

A tiny Android app that keeps your animation speed at **0.5x**, even when you turn Developer options off and on.

## The problem

You can make Android feel faster by setting the three animation speeds to 0.5x in Developer options.
But some banking and UPI apps refuse to open while Developer options is on.
And on Samsung phones (One UI), turning Developer options off puts all three speeds back to 1x.

So you can have fast animations or working banking apps, not both.

## What this app does

It watches the three speed settings:

- `window_animation_scale`
- `transition_animation_scale`
- `animator_duration_scale`

The moment any of them changes, it sets it back to `0.5`. This takes about a second.
It also checks every 15 minutes and after every restart, just in case.

It has no screen and does not keep running. Android wakes it only when a setting changes.
Tapping its icon sets the speeds right away and shows "Animation speed locked at 0.5x".

Tested on a Samsung Galaxy S25 Ultra, One UI with Android 17.

## Install

You need a computer with [adb](https://developer.android.com/tools/releases/platform-tools) once, for one permission.

1. Download the latest APK from [Releases](../../releases) and install it on the phone.
2. Turn on Developer options and USB debugging, and connect the phone to the computer.
3. Give the app permission to change system settings:

   ```
   adb shell pm grant shivay.animlock android.permission.WRITE_SECURE_SETTINGS
   ```

4. Let it run in the background without limits:

   ```
   adb shell dumpsys deviceidle whitelist +shivay.animlock
   adb shell cmd appops set shivay.animlock RUN_ANY_IN_BACKGROUND allow
   ```

5. Open **Animation Lock** once from the app drawer. You should see "Animation speed locked at 0.5x".
6. Turn Developer options off. Your speeds stay at 0.5x.

The permission stays after Developer options is off. You only do this once.
An update from Releases installs over the old app and keeps the permission.

## Check that it works

Change one of the speeds, or turn Developer options off.
Then look at the three values in any settings viewer, or run:

```
adb shell settings get global window_animation_scale
```

It should say `0.5`.

## Remove it

Uninstall the app. Your speeds stay at whatever they were last. Nothing else on the phone is changed.

## Change the speed

The speed is `WANTED_SCALE` in `src/shivay/animlock/AnimationScales.java`. Change it and build again.

## Build it yourself

No Android Studio or Gradle needed. You need JDK 17 and the Android SDK with `platforms;android-35` and `build-tools;36.0.0`.

```
export JAVA_HOME=/path/to/jdk-17
export ANDROID_HOME=/path/to/android-sdk
export KEYSTORE_PATH=/path/to/your.keystore
export KEYSTORE_PASSWORD=your-password
bash build.sh
```

The APK is at `build/anim-lock.apk`. Your own key works, but an APK signed with a different key cannot update this one. Uninstall first.

Every push to `main` builds a signed APK with GitHub Actions and adds it to Releases (`.github/workflows/release.yml`).
The workflow needs two repo secrets: `KEYSTORE_BASE64` (the keystore file in base64) and `KEYSTORE_PASSWORD`.
