# Letting the entertainment product work without weakening belt safety
### A good-faith proposal for Peloton's product & safety teams

## The ask, in one line

Keep the belt-safety protection exactly as strong as it is today, and stop the belt-safety *timer* from blacking out the entertainment experience when the belt is parked and cannot move. What follows is the reasoning, the constraints, and a **menu** of ways to get there - not a single mandated fix.

## The shared goal

No one wants a treadmill belt to move with a person - especially a child - on or under it, unattended. That goal is non-negotiable, and **every option below preserves it.** The second goal, which Peloton clearly shares, is that the Tread is also a premium screen people want to watch: there is a dedicated **Entertainment** section and an official Netflix integration. Today those two goals collide. They don't have to.

## The core problem, briefly

The Tread Lock triggers on **screen inactivity** - speed 0, no incline change, no screen touch, not in a class, for 45 seconds. But the hazard it exists to prevent is **belt motion.** Those are different things. A person standing still watching a 30-minute video and an abandoned, ready-to-run machine look identical to a screen-inactivity timer - even though the belt hazard exists in only one of them. The result is that the feature protecting the belt cancels the entertainment feature sitting next to it, including (by repeated member reports) during Peloton's own off-belt strength and floor classes.

## Why this has to come from Peloton (the architecture reality)

On current hardware the lock UI and the belt-arming logic live in the **same subsystem** (the `com.onepeloton.sensorstateindicator` package on the G700). Because the screen lock and the belt lock are fused, there is no safe way for anyone *outside* Peloton to relax the screen behavior without touching belt arming - which is exactly the part no one should touch. Peloton, in firmware, can cleanly separate "lock the screen" from "lock the belt." This is not a request to weaken anything; it is a request to finish a separation you already have the pieces for.

## Principles any solution should meet

So the result is **at least as safe as today**, any option should satisfy all of these:

- The belt's **PIN-to-run** protection stays exactly as strong. The thing that stops a child from starting the belt does not change.
- **No change at all while the belt is armed or runnable.** Relaxation applies only to a provably parked belt.
- **Fail-safe by default:** any uncertainty - sensor glitch, unknown state, lost telemetry - falls back to the full lock.
- **Auditable and simple enough to defend** to CPSC and to a jury. Prefer positive hardware/state signals over soft heuristics.
- The blunt 45-second timer **remains the fallback** whenever a richer signal isn't available.

## Options (a menu, not a mandate)

Each option preserves belt safety; they differ in how far they go, how much engineering they take, and how they're defended. Mix and match.

### Option A - Opt-in "Entertainment / TV mode"
The user explicitly enters a mode that **hard-disables the belt** (motor de-energized / un-armable) until they exit with their PIN. In that mode the screen does not black out.
- **Safety:** belt is *more* locked down than idle - it physically cannot start. The "attractive live screen" deterrence concern is answered by the belt being hard-off.
- **Pros:** simplest to reason about and defend; explicit user intent; no new sensing required.
- **Cons:** requires a deliberate toggle; users must remember to use it.

### Option B - Automatic parked-state detection
When speed = 0, incline parked, not in a class, and the belt is in a confirmed non-armed state, suppress the **screen** lock while keeping the **belt** locked.
- **Safety:** belt cannot start without PIN re-arm; nothing about belt arming changes.
- **Pros:** no user action; "just works."
- **Cons:** needs a rigorous, fail-safe definition of "parked / non-armed"; must default to locked if uncertain.

### Option C - Presence-aware lock (use the sensors already on the machine)
The Tread already has presence sensing and a camera (the strength "movement tracker" uses it). If a person is actively detected present and the belt is parked, extend or suppress the screen lock.
- **Safety:** belt still PIN-gated; this only affects the screen.
- **Pros:** directly targets the "watching on purpose" case; uses hardware already on the machine.
- **Cons:** sensor reliability and privacy must be handled; must fail safe to locked on any detection failure.

### Option D - Media-playback counts as activity
If an active media session (audio/video) is playing and the belt is parked, treat that as "activity" so the inactivity timer doesn't fire.
- **Safety:** belt parked + PIN gate unchanged.
- **Pros:** elegant; uses standard media-session signals; almost no new UI.
- **Cons:** could keep the screen awake while media loops - but since the belt can't move, the only residual is the deterrence point, not a belt hazard.

### Option E - Context-aware, longer timeout in entertainment
When an entertainment app or the Entertainment tab is foreground and the belt is parked, lengthen the inactivity window substantially (or make it content-length aware) instead of 45 seconds.
- **Safety:** belt parked + PIN gate unchanged.
- **Pros:** smallest change; keeps a timer, just a smarter one.
- **Cons:** partial fix; still interrupts eventually.

### Option F - Fix the off-belt-class gap specifically
Ensure the existing "in a class" suppression reliably covers strength and floor classes, so the lock stops firing during Peloton's own off-belt content.
- **Safety:** unchanged - this is already the intended behavior for classes.
- **Pros:** narrow, low-risk bug fix entirely within current design intent; shippable fast.
- **Cons:** only addresses the class case, not free entertainment viewing.

### Option G - Hardware "belt parked" interlock as the gate
Tie any screen-lock relaxation to a **positive signal from the motor controller** that the belt is mechanically parked / de-energized. The screen relaxes only while that hardware signal is asserted.
- **Safety:** strongest and most defensible - a hardware interlock, not a software heuristic.
- **Pros:** easiest to defend to a regulator; hard to argue with a de-energized motor.
- **Cons:** depends on motor-controller telemetry and its reliability.

### A plausible combination (still your call)
Use **G or B** as the safety gate, **C or D** as the trigger, offer **A** as the explicit user-facing version, and ship **F** immediately as a quick win. But the point is the menu and the principles - not this particular stack.

## Peloton's constraints, stated fairly

The lock exists because the Tread+ was recalled after a child's death and dozens of injuries. A conservative, dead-simple timer is easy to reason about, easy to defend to a regulator, and has no clever edge cases to get wrong. That is a legitimate engineering and legal position, and the burden is on any proposal to be **at least as safe** and to **fail safe.** Everything above is built to meet that bar: none of it changes belt arming, and all of it defaults to the full lock when unsure.

## Supporting context

- **Entertainment is a first-class, marketed capability:** a dedicated Entertainment section and an official Netflix integration. There is clear product intent for people to watch this screen.
- **Members report the lock firing during non-running / strength classes and during entertainment viewing** - so even the existing "suppress during classes" intent has gaps.
- **The hardware to tell "present and watching" from "abandoned" already exists** on the machine (presence + camera).
- This is a **member-satisfaction and differentiation** issue, not a nicety: people are frustrated that the premium screen they paid for goes dark while they stretch, do floor work, or line up a video before a run.

## What this is NOT asking for

- **Not** removing, weakening, or bypassing the PIN-to-run protection.
- **Not** letting the belt move under any relaxed condition.
- **Not** a user-side modification - as explained, that can't be done safely from outside Peloton today, which is the entire reason this is addressed to your team.

## Closing

The goal is a Tread that is exactly as safe as it is now **and** lets people actually use the entertainment features you built. The belt is the thing that must stay locked; the screen going dark in a parked state is a side effect of a coarse rule, not a safety requirement. You already have the hardware and the software pieces to tell the two apart. Pick whichever of the above fits your risk model - happy to go deeper on any of them.
