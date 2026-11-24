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

## The design tension worth raising with Peloton

This project does **not** defeat the lock, and nothing in this section is a workaround. But there is a real product-design contradiction baked into the Tread, and it's worth putting in front of Peloton's team in good faith. Here it is laid out, so the argument doesn't have to be re-made from scratch each time.

### The dissonance, point by point

1. **They built and sell an entertainment machine, then cancel it.** The Tread has a dedicated **Entertainment** section, and Peloton added Netflix as an official partner - they are explicitly inviting you to watch things on this screen. Yet the moment you are stationary and not in a class, the 45-second lock blacks that entertainment out. One feature negates the feature sitting right next to it.

2. **The lock conflates "no one is watching the screen" with "the machine is unsafe."** The hazard the recall addressed is the **belt moving** with someone on or under it, unattended. That is a belt-motion question. But the lock triggers on *screen inactivity* (speed 0, no incline change, no touch, not in a class), which is a *different* question. Standing still watching a 30-minute video on purpose and walking away from a ready machine look identical to the current rule - even though the belt hazard exists in only one of them.

3. **Their own off-belt content trips the running-safety lock.** Peloton ships strength and floor classes meant to be done *off* the belt, and members have repeatedly reported the Tread Lock firing *during* those classes. A safety stop designed around a running belt is interrupting content Peloton itself designed to be done while not running.

4. **They already have the sensors to tell the difference - and don't use them here.** The Tread has presence sensing and a camera; the strength "movement tracker" literally uses that camera to watch you move. The hardware can distinguish "a person is here, stationary, watching" from "abandoned and ready to run." The lock draws on none of it - it just counts to 45.

### Peloton's side, stated fairly

The recall followed a child's death and dozens of injuries, so Peloton chose a conservative, fail-safe, dead-simple rule. A blunt timer is easy to reason about, easy to defend to a regulator, and has no clever edge cases to get wrong. That is a legitimate engineering and legal position, and any proposal has to be **at least as safe** - not a loosening.

### A middle ground that keeps the safety goal intact

The key realization: the real safety requirement is about the **belt**, not the **screen**. Decouple the two and both goals can be met.

- **Leave the lock exactly as-is whenever the belt is armed and runnable.** No change there at all.
- **Add an explicit, opt-in Entertainment / TV mode** in which the belt is *fully disabled* - it cannot move without exiting the mode and re-entering the PIN. In that state the belt is arguably *safer* than normal (hard-locked out), so the screen has no reason to black out. You get uninterrupted viewing **and** a belt that physically can't start.
- **Or use the presence/camera signal Peloton already has:** if a person is detected present but stationary, and the belt is parked (0 speed, 0 incline, not armed), suppress the *screen* blackout while keeping the *belt* locked. Belt safety is untouched; only the blackout is relaxed.
- Either way, the **PIN-to-run protection - the thing that actually stops a child from starting the belt - stays exactly as strong.**

The ask is not "turn off the safety feature." It is: *stop letting a belt-safety timer break the entertainment product you built, when you already have cleaner ways to tell a watching human from an abandoned machine.*

---

For a full, option-by-option version of this argument written for Peloton's team - with safety principles and a menu of solutions - see **[05-proposal-for-peloton.md](05-proposal-for-peloton.md)**.
