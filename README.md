# Peloton Launcher (Tread Home)

A lightweight custom Android home screen for the Peloton Tread / Tread+ (G700, Android 13) that turns the built-in tablet into a clean media hub. It auto-detects your installed apps, renders their icons in one consistent monochrome style, fits them to the screen, and adds a floating Home button so you are never trapped inside a full-screen app.

## Safety -- please read

This is a **home-screen launcher only**. It does **not** modify, extend, disable, hide, or interfere with the Peloton Tread Lock (the belt safety stop) or any speed, incline, or belt-arming behavior. The 45-second safety lock still works exactly as Peloton designed it: if you are stationary, it will still engage. Do not attempt to change those systems -- a treadmill can cause serious injury.

## What it does

- Sets itself as the tablet home screen (the Peloton home button opens it; Peloton is one tap away on its own tile)
- Auto-detects installed apps -- install something new from an app store and it just appears
- Renders every app icon as a uniform soft-white glyph so nothing clashes
- Auto-fits the tile grid to the screen width
- A draggable, always-on-top Home button to jump back from any app (Peloton, SmartTube, Netflix, etc.)
- Hides Peloton / system internals (diagnostics, firmware, WebView tools, and so on)

## Requirements

- A Peloton Tread or Tread+ (tested on the G700 running Android 13)
- A computer with adb (Android platform-tools) on the same network
- Developer options + Wireless debugging enabled on the tablet

## Install (prebuilt APK)

1. On the Tread: three-dot menu -> Device Settings -> About Tablet -> tap Build Number 7 times to unlock Developer options.
2. Developer options -> turn on Wireless debugging -> Pair device with pairing code.
3. On your computer: adb pair <ip>:<pairPort> <code>  then  adb connect <ip>:<connectPort>
4. Run:  ./install.sh <ip>:<connectPort>

That installs the app, makes it the home screen, and enables the floating Home button.

## Build from source

Set your name in src/com/colin/treadhome/MainActivity.java (leave empty for none):

    static final String OWNER_NAME = "Your Name";

Then run ./build.sh (needs JDK 17 and the Android SDK build-tools + platform 33). The signed APK lands in build/treadhome.apk.

## Notes

- Not affiliated with, endorsed by, or supported by Peloton. Peloton, Tread, and related names belong to their owners.
- Sideloading and changing the launcher may void your warranty and may conflict with Peloton's Terms of Service. Your device, your risk.
- Peloton software updates can remove sideloaded apps or reset the home launcher; just re-run the installer.
- Android application id: com.colin.treadhome

## License

MIT -- see LICENSE.
