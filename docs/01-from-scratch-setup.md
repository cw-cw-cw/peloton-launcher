# Setting it up from scratch

This is the whole thing, start to finish, assuming you have never done anything like this before. Budget about 20-30 minutes the first time.

## What you need

- A Peloton Tread or Tread+ (this was built and tested on the **G700**, which runs **Android 13**).
- A computer (Mac or Windows) on the **same Wi-Fi network** as the Tread.
- That's it. No cable, no rooting, no opening anything up.

## Step 1 - Put "adb" on your computer

`adb` (Android Debug Bridge) is the tool your computer uses to talk to the Tread's tablet.

- **Mac:** install Homebrew (https://brew.sh) if you don't have it, then run `brew install android-platform-tools`.
- **Windows:** download "SDK Platform Tools" from https://developer.android.com/tools/releases/platform-tools, unzip it, and use `adb.exe` from inside that folder.

Check it works: run `adb version`. You should see a version number.

## Step 2 - Turn on Developer Mode on the Tread

Peloton hides the normal Android settings, so the way in is their three-dot menu:

1. On the Tread screen, tap the **three-dot (...) menu** (bottom-right).
2. Tap **Device Settings**.
3. Scroll down to **About Tablet**.
4. Find **Build Number** and tap it **seven times fast**. If it asks for your Tread Lock PIN, enter it.
5. You'll see **"You are now a developer!"**
6. Back out. **Developer options** now shows up (under System on this build).

## Step 3 - Turn on Wireless debugging

1. Open **Developer options** -> scroll to **Wireless debugging** -> turn it **on**.
2. Tap the words **"Wireless debugging"** (not just the switch) to open its screen. Note the **IP address & port** shown there - that's your **connect address**.
3. Tap **"Pair device with pairing code."** A popup shows a **6-digit code** and a **different** IP:port - that's your **pair address**.

## Step 4 - Pair and connect from your computer

In a terminal:

```
adb pair <ip>:<pairPort> <6-digit-code>
adb connect <ip>:<connectPort>
adb devices
```

`adb devices` should now list your Tread. (On Android 13 the pairing is one-time; after that you just `adb connect`.)

## Step 5 - Install the launcher

From this project folder:

```
./install.sh <ip>:<connectPort>
```

That installs the app, sets it as the home screen, and turns on the floating Home button. Press the Peloton **home button** (the "P" at the bottom) and you'll land on the launcher.

## Step 6 - Add your apps

The launcher is just a home screen - it doesn't come with apps. Sideload what you want and they appear automatically:

- **An app store without Google:** Aurora Store (https://auroraoss.com) - gives you the normal Play catalog using an anonymous account. Install it with `adb install aurora.apk`.
- **Netflix:** install from Aurora Store.
- **YouTube with your account + Premium:** SmartTube (https://github.com/yuliskov/SmartTube) - sign in with a code, no ads, no Google services needed.
- **YouTube, no login:** NewPipe (https://newpipe.net).

Anything you install shows up on the launcher the next time you return to it.

## If something resets

Peloton pushes software updates that can wipe sideloaded apps or reset the home screen. If that happens, just run `./install.sh` again. (Don't disable Peloton's updater to prevent it - see the safety doc for why.)
