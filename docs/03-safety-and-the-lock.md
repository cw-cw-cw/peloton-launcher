# Safety: the Tread Lock, and what this project refuses to touch

This is the most important document in the repo. Read it before you change anything.

## The one hard line

This project is a **home-screen launcher and app organizer - nothing more.** It does **not** modify, extend, disable, hide, or interfere with the Peloton **Tread Lock** (the belt safety stop) or anything that controls speed, incline, or whether the belt can run. The 45-second lock keeps working exactly as Peloton built it. If you're standing still, it will still take over the screen and ask for your PIN. That's intended, and this software does not change it.

## Where the lock actually lives

On the G700, the "Enter Tread Lock Passcode" screen is owned by the Android package:

```
com.onepeloton.sensorstateindicator
```

(You can confirm it yourself: when the lock is up, a UI dump shows that package owning the passcode screen.)

## Why that package is off-limits - what the community found

It is tempting to think that package is "just the lock screen" and that disabling or bypassing it would only remove an annoyance. It is not, and it would not.

An independent Tread-tinkering project (`ericwhyne/pelaton-improve`) documented that `com.onepeloton.sensorstateindicator` is also the mechanism that **arms the belt controller** - disable it and the physical speed/incline controls stop responding (they flash red). In plain terms: **the PIN lock and the belt's ability to run are the same subsystem.** The belt is gated behind that lock. So "remove the 45-second timeout" or "hide the lock" is not a cosmetic tweak - it is reaching into the thing that stops the belt when no one is attending the machine.

That mechanism exists for a reason. The Tread+ line was recalled after a child's death and dozens of injuries, and the auto-stop lock is the safety remediation that came out of it.

## What this launcher will never do (and why)

- **Never** disables, modifies, or hides `com.onepeloton.sensorstateindicator` or any lock / belt-arming package.
- **Never** changes the 45-second timeout.
- **Never** draws over or suppresses the passcode screen to keep video playing "on top" of it - hiding the lock is just defeating it from a different direction.
- **Never** disables Peloton's OTA / firmware updaters (`com.onepeloton.fwupdateservice` and friends). On a safety-critical machine, blocking firmware and security updates just to protect some sideloaded apps is the wrong trade. If an update wipes the launcher, you reinstall it.
- The **floating Home button is navigation only.** It moves you between apps. The belt stays locked and cannot run until you enter your PIN. It does not touch the safety stop.

## What the code actually does

The launcher only **reads** the list of launchable apps and **launches** them. There is no code that disables packages, no root, and no writes into any Peloton package. The blocklist in `MainActivity.java` only hides Peloton's internal apps from the grid - it never modifies them.

## The honest bottom line

A treadmill is not a TV. When you are moving (a walk, a run, or any Peloton class), the lock never fires, so you can watch whatever you want uninterrupted - that's the real win here. When you are standing still, the lock will interrupt you, by design. If you want to watch something standing still with no interruptions, use a separate screen, not the treadmill. Don't try to defeat the lock. It is the part that stops the belt when someone - a kid, a pet, you after a stumble - is on or under it and not running.
