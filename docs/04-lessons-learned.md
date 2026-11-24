# Lessons learned (build notes and gotchas)

Things that bit us, so they don't bite you.

## Android 11+ package visibility

`getLaunchIntentForPackage(otherApp)` and `queryIntentActivities(...)` silently return **nothing** for other apps unless your manifest declares `QUERY_ALL_PACKAGES` (or explicit `<queries>`). The very first build had a working header and an **empty** app grid for exactly this reason. Add the permission.

## Building without Gradle is great for something this small

For a single-activity app with no libraries, the `aapt2` -> `javac` -> `d8` -> `zipalign` -> `apksigner` pipeline is faster and has fewer moving parts than a full Gradle project, and it needs no Android Studio - just a JDK and the SDK command-line tools.

## Foreground services and `adb`

Starting the overlay as a foreground service **from `adb`** (`am start-foreground-service` for another app's uid) gets refused on Android 13 ("Requires permission not exported from uid ..."). Start it **from inside the app** instead (in `onCreate` / `onResume`); that runs as the app itself and is allowed.

## HOME vs LAUNCHER categories

Peloton's real home UI is `com.peloton.launcher/.LauncherActivity`, which is a **HOME**-category activity, not a **LAUNCHER** one. It won't appear in a normal launcher query and `resolve-activity` returns "No activity found." Launch it by explicit component instead.

## The odyssey trampoline

`com.onepeloton.odyssey`'s only launchable activity is a trampoline that bounces straight back to whatever the system home is. Launching it from the launcher just flipped back to the launcher. Use the `com.peloton.launcher` component for the Peloton tile.

## The transfer corruption that cost the most time

Moving the Java source across the machine-to-machine bridge as **base64** occasionally injected Cyrillic look-alike characters into long base64 runs (e.g. the stretch encoding the word `PackageManager`), silently corrupting the file - it compiled to garbage or truncated. The fix was to transfer the source as **plain text** and verify it with a checksum (or route it through a gist). Lesson: checksum any bulk text you move between systems; don't trust a long opaque blob to come through clean.

## The device won't give you the Peloton name

The only names the system exposes to other apps are generic (`g700`, `PLTN-RTR01`). The friendly Tread name and your Peloton account name live in Peloton's private data and aren't readable, so personalization has to be a build-time config (`OWNER_NAME`), not something pulled live.

## Expect the lock mid-build

If the person is standing at the Tread while you iterate, the 45-second lock fires constantly and covers whatever you just launched. That's not a bug - it's the safety lock doing its job. Just enter the PIN and carry on.
