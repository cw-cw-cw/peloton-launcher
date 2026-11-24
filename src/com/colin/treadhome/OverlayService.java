package com.colin.treadhome;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.IBinder;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class OverlayService extends Service {

    private WindowManager wm;
    private View bubble;
    private WindowManager.LayoutParams lp;

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { return START_STICKY; }

    private int dp(float v) {
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, v, getResources().getDisplayMetrics()));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startAsForeground();
        addBubble();
    }

    private void startAsForeground() {
        String ch = "treadhome_overlay";
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(
                    ch, "Tread Home", NotificationManager.IMPORTANCE_MIN);
            nm.createNotificationChannel(channel);
            Notification n = new Notification.Builder(this, ch)
                    .setContentTitle("Tread Home")
                    .setContentText("Home button active")
                    .setSmallIcon(android.R.drawable.ic_menu_view)
                    .build();
            startForeground(1, n);
        }
    }

    private void addBubble() {
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        final TextView btn = new TextView(this);
        btn.setText("\u2302");
        btn.setTextColor(Color.parseColor("#FFFFFF"));
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        btn.setGravity(Gravity.CENTER);
        GradientDrawable bgd = new GradientDrawable();
        bgd.setShape(GradientDrawable.OVAL);
        bgd.setColor(Color.parseColor("#D01C1C22"));
        bgd.setStroke(dp(2), Color.parseColor("#E0152A"));
        btn.setBackground(bgd);
        bubble = btn;

        int sz = dp(54);
        int type = Build.VERSION.SDK_INT >= 26
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_PHONE;
        lp = new WindowManager.LayoutParams(
                sz, sz, type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        lp.gravity = Gravity.BOTTOM | Gravity.START;
        lp.x = dp(18);
        lp.y = dp(140);

        btn.setOnTouchListener(new View.OnTouchListener() {
            float downX, downY;
            int startX, startY;
            boolean moved;
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = e.getRawX();
                        downY = e.getRawY();
                        startX = lp.x;
                        startY = lp.y;
                        moved = false;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) (e.getRawX() - downX);
                        int dy = (int) (e.getRawY() - downY);
                        if (Math.abs(dx) > dp(6) || Math.abs(dy) > dp(6)) moved = true;
                        lp.x = startX + dx;
                        lp.y = startY - dy;
                        if (lp.x < 0) lp.x = 0;
                        if (lp.y < 0) lp.y = 0;
                        try { wm.updateViewLayout(bubble, lp); } catch (Exception ignored) {}
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (!moved) goHome();
                        return true;
                }
                return false;
            }
        });

        try { wm.addView(bubble, lp); } catch (Exception ignored) {}
    }

    private void goHome() {
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(i);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (wm != null && bubble != null) {
            try { wm.removeView(bubble); } catch (Exception ignored) {}
        }
    }
}
