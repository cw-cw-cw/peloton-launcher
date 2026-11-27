## Summary

<!-- One or two sentences: what changed, and what a reviewer should look at first. -->

## Why

<!-- Link the issue (Fixes #123) or say why this change is needed. -->

## Type of change

<!-- Check every box that applies. -->

- [ ] Bug fix
- [ ] Launcher behavior (grid, icons, Home button, boot)
- [ ] UI
- [ ] Docs
- [ ] Build or release (`install.sh`, `build.sh`, version, committed APK)
- [ ] Other

## Testing done

<!--
Name the device and Android version. This launcher is built for a real Peloton Tread (tested on G700, Android 13).
Say whether you used an emulator or a real Tread.
Say whether ./build.sh succeeded and whether ./install.sh was run against a device.
Write "not run" and why if you skipped either.
-->

- Device / model:
- Android version:
- Emulator or real Tread:
- `./build.sh` verified:
- `./install.sh` verified:

## Screenshots

<!-- UI changes: paste before/after shots. Otherwise write "N/A". -->

## Build / release impact

<!--
install.sh installs the committed peloton-launcher.apk, not build/treadhome.apk.
Check the manifest (versionCode, versionName, uses-permission) and the APK diff.
Check a box only if this PR changes that.
-->

- [ ] Committed release APK (`peloton-launcher.apk`) was rebuilt and replaced
- [ ] `versionCode` or `versionName` changed
- [ ] Android permissions added, removed, or changed
- [ ] None of the above

## Risk and permission band

<!--
State the band. Explicitly flag every Band 3 item this PR touches.

Band 3:
- safety-related behavior: the Tread Lock, the 45-second timeout, belt arming, speed, incline, com.onepeloton.sensorstateindicator, or firmware/OTA packages
- a new or changed Android permission
- replacing the committed release APK (peloton-launcher.apk)
- a public-facing claim about Peloton (affiliation, endorsement, or what the lock or belt does)

Docs, icons, the grid, and navigation-only overlay work that stays a home screen are not Band 3.
If any Band 3 box is checked, add one line on why docs/03-safety-and-the-lock.md still holds. If it does not hold, do not open the PR.
-->

**Band:**

- [ ] Band 3: safety-related behavior
- [ ] Band 3: new or changed Android permission
- [ ] Band 3: release APK
- [ ] Band 3: public-facing claim about Peloton

## Reviewer checklist

<!-- Confirm each item. The safety boundary is docs/03-safety-and-the-lock.md. -->

- [ ] Safety boundary still holds: nothing disables, hides, draws over, or changes the Tread Lock, the 45-second timeout, `com.onepeloton.sensorstateindicator`, belt arming, speed, incline, or firmware/OTA. The floating Home button stays navigation only.
- [ ] The blocklist only hides tiles. It does not disable packages or write into any Peloton package.
- [ ] Band 3 boxes above are unchecked, or each checked item is explained and still inside that boundary.
- [ ] Testing notes match the diff: device/model, Android version, emulator vs real Tread, `./build.sh`, and `./install.sh`.
- [ ] UI changes include screenshots. Release notes above match the manifest, permissions, and `peloton-launcher.apk`.
- [ ] Public text still says this project is not affiliated with Peloton and does not claim to change belt safety.
