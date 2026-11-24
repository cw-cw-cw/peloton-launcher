# How it works

The Tread's tablet is a stock **Android 13** device running behind Peloton's locked-down kiosk launcher. This project is a normal Android launcher app that you set as the home screen. Nothing exotic - no root, no custom ROM.

## Building without Android Studio

The app is pure Android framework code with **zero dependencies**, so it builds with the raw SDK command-line tools instead of Gradle:

1. `aapt2 link` turns the manifest into a base APK.
2. `javac` compiles the Java against `android.jar`.
3. `d8` turns the `.class` files into `classes.dex`.
4. `zip` drops the dex into the APK, `zipalign` aligns it, `apksigner` signs it.

See `build.sh`. It needs a JDK 17 and the Android SDK `build-tools` + `platform-33`.

## Becoming the home screen

The app declares `CATEGORY_HOME` and is set as the default with `adb shell cmd package set-home-activity`. After that, the Peloton "P" button opens the launcher; Peloton itself is one tap away on its own tile.

## Auto-detecting installed apps

It queries `queryIntentActivities(ACTION_MAIN + CATEGORY_LAUNCHER)`. On Android 11+ an app can't see other apps unless it declares `QUERY_ALL_PACKAGES` (in the manifest), so that permission is what makes the grid populate. A small **prefix blocklist** (`com.onepeloton.*`, `com.peloton.*`, `com.android.*`, etc.) hides Peloton/system internals so you only see real apps.

## One consistent icon look

Full-color app icons clash, so each is reduced to a soft-white glyph:
- **Adaptive icons:** take the foreground layer, inset it ~20% (to undo the adaptive zoom), and tint it white with a `SRC_IN` color filter.
- **Legacy icons:** desaturate with a `ColorMatrix` (saturation 0).

## Auto-fitting the grid

Columns are computed from the screen width (`content / targetTileWidth`), and tiles are sized to fill, so it packs in as many as the display allows.

## The Peloton tile (a gotcha)

Peloton's normal entry point (`com.onepeloton.odyssey`) is a trampoline activity that bounces back to the system home. The actual Peloton UI is `com.peloton.launcher/.LauncherActivity`, which is a **HOME-category** activity - it doesn't show up in a normal launcher query, so the tile launches it by explicit component.

## The floating Home button

A foreground `Service` adds a small draggable overlay button (`TYPE_APPLICATION_OVERLAY`, the `SYSTEM_ALERT_WINDOW` permission). It floats above every app - including full-screen ones like Peloton and SmartTube that hide the nav bar - so one tap always returns you to the launcher. A `BootReceiver` restarts it after a reboot, and the launcher also re-starts it on resume. It is **navigation only** (see the safety doc).

## Name and device

`OWNER_NAME` is a one-line config at the top of `MainActivity.java`. The device type ("Tread+") is derived from `Build.MODEL` (`g700`). The Peloton account name and the Tread's nickname are **not** exposed to other apps, so the greeting name is a build-time setting rather than something pulled live.
