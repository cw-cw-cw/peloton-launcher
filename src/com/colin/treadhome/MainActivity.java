package com.colin.treadhome;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    // Set your name for the greeting, or "" for none. Device type auto-detects from the model.
    static final String OWNER_NAME = "";

    private TextView clock;
    private GridLayout grid;
    private final Handler handler = new Handler(Looper.getMainLooper());

    static final int BG_TOP    = Color.parseColor("#17171C");
    static final int BG_BOTTOM = Color.parseColor("#26262E");
    static final int CARD      = Color.parseColor("#2A2A33");
    static final int CARD_EDGE = Color.parseColor("#45454F");
    static final int ACCENT    = Color.parseColor("#E0152A");
    static final int TEXT      = Color.parseColor("#FFFFFF");
    static final int SUBTEXT   = Color.parseColor("#9A9AA5");
    static final int GLYPH     = Color.parseColor("#ECECF1");

    private int dp(float v) {
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, v, getResources().getDisplayMetrics()));
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        startOverlay();

        GradientDrawable bg = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, new int[]{BG_TOP, BG_BOTTOM});

        ScrollView scroll = new ScrollView(this);
        scroll.setBackground(bg);
        scroll.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(48);
        root.setPadding(pad, dp(34), pad, dp(34));
        scroll.addView(root);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout greetWrap = new LinearLayout(this);
        greetWrap.setOrientation(LinearLayout.VERTICAL);
        greetWrap.setLayoutParams(new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView greeting = new TextView(this);
        greeting.setText(greetingText());
        greeting.setTextColor(TEXT);
        greeting.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
        greeting.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));

        TextView sub = new TextView(this);
        sub.setText(ownerLine());
        sub.setTextColor(SUBTEXT);
        sub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        sub.setPadding(0, dp(3), 0, 0);

        greetWrap.addView(greeting);
        greetWrap.addView(sub);

        clock = new TextView(this);
        clock.setTextColor(TEXT);
        clock.setTextSize(TypedValue.COMPLEX_UNIT_SP, 34);
        clock.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        clock.setGravity(Gravity.END);

        header.addView(greetWrap);
        header.addView(clock);
        root.addView(header);

        View rule = new View(this);
        LinearLayout.LayoutParams ruleLp = new LinearLayout.LayoutParams(dp(52), dp(4));
        ruleLp.topMargin = dp(18);
        ruleLp.bottomMargin = dp(26);
        rule.setLayoutParams(ruleLp);
        GradientDrawable rd = new GradientDrawable();
        rd.setColor(ACCENT);
        rd.setCornerRadius(dp(2));
        rule.setBackground(rd);
        root.addView(rule);

        grid = new GridLayout(this);
        root.addView(grid);

        setContentView(scroll);
        refreshGrid();
        tick();
    }

    private void startOverlay() {
        try {
            Intent svc = new Intent(this, OverlayService.class);
            if (Build.VERSION.SDK_INT >= 26) startForegroundService(svc);
            else startService(svc);
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startOverlay();
        refreshGrid();
    }

    private void refreshGrid() {
        if (grid == null) return;
        grid.removeAllViews();
        int pad = dp(48);
        int margin = dp(9);
        int targetTileW = dp(160);
        int screenW = getResources().getDisplayMetrics().widthPixels;
        int content = screenW - 2 * pad;
        int cols = content / (targetTileW + 2 * margin);
        if (cols < 3) cols = 3;
        if (cols > 8) cols = 8;
        grid.setColumnCount(cols);
        int tileW = (content - cols * 2 * margin) / cols;
        int tileH = tileW;
        int iconSz = Math.min(dp(60), Math.round(tileW * 0.42f));
        List<App> apps = resolveApps();
        for (App a : apps) {
            View tile = makeTile(a, iconSz);
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = tileW;
            lp.height = tileH;
            lp.setMargins(margin, margin, margin, margin);
            tile.setLayoutParams(lp);
            grid.addView(tile);
        }
    }

    private String deviceType() {
        String m = Build.MODEL == null ? "" : Build.MODEL.toLowerCase(Locale.US);
        if (m.equals("g700")) return "Tread+";
        if (m.contains("tread")) return "Tread";
        return "Peloton";
    }

    private String ownerLine() {
        String t = deviceType();
        return OWNER_NAME.isEmpty() ? ("Peloton " + t) : (OWNER_NAME + "'s " + t);
    }

    private String greetingText() {
        int h = Integer.parseInt(new SimpleDateFormat("H", Locale.US).format(new Date()));
        String part = h < 12 ? "morning" : (h < 17 ? "afternoon" : "evening");
        return OWNER_NAME.isEmpty() ? ("Good " + part) : ("Good " + part + ", " + OWNER_NAME);
    }

    private void tick() {
        clock.setText(new SimpleDateFormat("h:mm", Locale.US).format(new Date()));
        handler.postDelayed(this::tick, 10000);
    }

    private static class App {
        final String label;
        final String pkg;
        final String component;
        final boolean peloton;
        App(String label, String pkg, String component, boolean peloton) {
            this.label = label;
            this.pkg = pkg;
            this.component = component;
            this.peloton = peloton;
        }
    }

    private boolean hidden(String pkg) {
        if (pkg.equals(getPackageName())) return true;
        if (pkg.startsWith("com.onepeloton.")) return true;
        if (pkg.startsWith("com.peloton.")) return true;
        if (pkg.startsWith("com.android.")) return true;
        if (pkg.startsWith("com.mediatek.")) return true;
        if (pkg.startsWith("org.chromium.")) return true;
        if (pkg.equals("com.google.android.webview")) return true;
        return false;
    }

    private String friendly(String pkg, String fallback) {
        if (pkg.equals("org.smarttube.stable")) return "YouTube";
        if (pkg.equals("org.schabi.newpipe")) return "YouTube Lite";
        if (pkg.equals("com.netflix.mediaclient")) return "Netflix";
        if (pkg.equals("com.aurora.store")) return "App Store";
        return fallback;
    }

    private boolean pkgExists(String pkg) {
        try {
            getPackageManager().getPackageInfo(pkg, 0);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private List<App> resolveApps() {
        PackageManager pm = getPackageManager();
        Intent main = new Intent(Intent.ACTION_MAIN, null);
        main.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> ris = pm.queryIntentActivities(main, 0);
        LinkedHashMap<String, App> byPkg = new LinkedHashMap<String, App>();
        for (ResolveInfo ri : ris) {
            if (ri.activityInfo == null) continue;
            String pkg = ri.activityInfo.packageName;
            if (hidden(pkg)) continue;
            if (byPkg.containsKey(pkg)) continue;
            String label = friendly(pkg, String.valueOf(ri.loadLabel(pm)));
            byPkg.put(pkg, new App(label, pkg, null, false));
        }
        List<App> out = new ArrayList<App>(byPkg.values());
        Collections.sort(out, new Comparator<App>() {
            @Override
            public int compare(App x, App y) {
                return x.label.compareToIgnoreCase(y.label);
            }
        });
        if (pkgExists("com.peloton.launcher")) {
            out.add(new App("Peloton", "com.peloton.launcher",
                    "com.peloton.launcher/.LauncherActivity", true));
        }
        return out;
    }

    private Drawable monochrome(String pkg) {
        try {
            Drawable d = getPackageManager().getApplicationIcon(pkg);
            if (Build.VERSION.SDK_INT >= 26 && d instanceof AdaptiveIconDrawable) {
                Drawable fg = ((AdaptiveIconDrawable) d).getForeground();
                if (fg != null) {
                    InsetDrawable inset = new InsetDrawable(fg.mutate(), 0.20f);
                    inset.setColorFilter(new PorterDuffColorFilter(GLYPH, PorterDuff.Mode.SRC_IN));
                    return inset;
                }
            }
            Drawable base = d.mutate();
            ColorMatrix cm = new ColorMatrix();
            cm.setSaturation(0f);
            base.setColorFilter(new ColorMatrixColorFilter(cm));
            return base;
        } catch (Exception e) {
            return null;
        }
    }

    private void launch(App a) {
        try {
            Intent i;
            if (a.component != null) {
                i = new Intent(Intent.ACTION_MAIN);
                i.setComponent(ComponentName.unflattenFromString(a.component));
            } else {
                i = getPackageManager().getLaunchIntentForPackage(a.pkg);
            }
            if (i != null) {
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        } catch (Exception ignored) {
        }
    }

    private View makeTile(final App a, int iconSz) {
        LinearLayout tile = new LinearLayout(this);
        tile.setOrientation(LinearLayout.VERTICAL);
        tile.setGravity(Gravity.CENTER);
        tile.setPadding(dp(10), dp(14), dp(10), dp(12));

        GradientDrawable card = new GradientDrawable();
        card.setColor(CARD);
        card.setCornerRadius(dp(20));
        card.setStroke(a.peloton ? dp(2) : dp(1), a.peloton ? ACCENT : CARD_EDGE);
        tile.setBackground(card);

        ImageView icon = new ImageView(this);
        LinearLayout.LayoutParams ilp = new LinearLayout.LayoutParams(iconSz, iconSz);
        ilp.bottomMargin = dp(10);
        icon.setLayoutParams(ilp);
        Drawable g = monochrome(a.pkg);
        if (g != null) {
            icon.setImageDrawable(g);
        }

        TextView label = new TextView(this);
        label.setText(a.label);
        label.setTextColor(TEXT);
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        label.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        label.setGravity(Gravity.CENTER);
        label.setMaxLines(1);

        tile.addView(icon);
        tile.addView(label);

        tile.setClickable(true);
        tile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launch(a);
            }
        });
        tile.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                int act = e.getAction();
                if (act == MotionEvent.ACTION_DOWN) {
                    v.setAlpha(0.72f);
                } else if (act == MotionEvent.ACTION_UP || act == MotionEvent.ACTION_CANCEL) {
                    v.setAlpha(1f);
                }
                return false;
            }
        });

        return tile;
    }

    @Override
    public void onBackPressed() {
    }
}
