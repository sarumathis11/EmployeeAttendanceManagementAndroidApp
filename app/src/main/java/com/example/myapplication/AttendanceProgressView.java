package com.example.myapplication;

//package com.example.myapplication.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AttendanceProgressView extends View {
    private Paint bgPaint, progressPaint, textPaint, sparkPaint;
    private RectF arcRect = new RectF();
    private float progress = 0f; // 0..1
    private List<Float> trendData = new ArrayList<>();
    private int strokeWidthPx;

    public AttendanceProgressView(Context context) { this(context, null); }
    public AttendanceProgressView(Context context, AttributeSet attrs) { this(context, attrs, 0); }
    public AttendanceProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        strokeWidthPx = dpToPx(12);

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeWidth(strokeWidthPx);
        bgPaint.setColor(Color.parseColor("#E0E0E0"));

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setStrokeWidth(strokeWidthPx);
        progressPaint.setColor(Color.parseColor("#3DDC84")); // green-ish

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(spToPx(18));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.BLACK);

        sparkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sparkPaint.setStyle(Paint.Style.STROKE);
        sparkPaint.setStrokeWidth(dpToPx(2));
        sparkPaint.setColor(Color.parseColor("#5B2EE6"));
    }

    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int size = Math.min(w, h) - dpToPx(32); // make room for sparkline & padding
        int left = (w - size) / 2;
        int top = dpToPx(8);

        float halfStroke = strokeWidthPx / 2f;
        arcRect.set(left + halfStroke, top + halfStroke,
                left + size - halfStroke, top + size - halfStroke);
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // background circle
        canvas.drawArc(arcRect, -90, 360, false, bgPaint);
        // progress arc
        canvas.drawArc(arcRect, -90, progress * 360f, false, progressPaint);

        // center percent text
        float cx = arcRect.centerX();
        float cy = arcRect.centerY();
        String text = Math.round(progress * 100f) + "%";
        // vertical adjust for text baseline
        float textY = cy - ((textPaint.descent() + textPaint.ascent()) / 2);
        canvas.drawText(text, cx, textY, textPaint);

        // sparkline below arc
        float sparkTop = arcRect.bottom + dpToPx(12);
        drawSparkline(canvas, sparkTop);
    }

    private void drawSparkline(Canvas canvas, float top) {
        if (trendData == null || trendData.size() < 2) return;

        float left = dpToPx(16);
        float right = getWidth() - dpToPx(16);
        float width = right - left;
        float height = dpToPx(48);

        float min = Collections.min(trendData);
        float max = Collections.max(trendData);
        float range = max - min;
        if (range == 0) range = 1f; // avoid division by zero

        Path path = new Path();
        for (int i = 0; i < trendData.size(); i++) {
            float x = left + (i / (float) (trendData.size() - 1)) * width;
            float v = trendData.get(i);
            float normalized = (v - min) / range; // 0..1
            float y = top + height - normalized * height;
            if (i == 0) path.moveTo(x, y);
            else path.lineTo(x, y);
        }
        canvas.drawPath(path, sparkPaint);
    }

    // public API
    public void setTrendData(List<Float> list) {
        if (list == null) list = new ArrayList<>();
        this.trendData = list;
        invalidate();
    }

    public void animateProgress(float targetFraction) {
        targetFraction = Math.max(0f, Math.min(1f, targetFraction));
        ValueAnimator va = ValueAnimator.ofFloat(progress, targetFraction);
        va.setDuration(700);
        va.addUpdateListener(animation -> {
            progress = (float) animation.getAnimatedValue();
            invalidate();
        });
        va.start();
    }

    // utility
    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
    private float spToPx(int sp) {
        return sp * getResources().getDisplayMetrics().scaledDensity;
    }
}

