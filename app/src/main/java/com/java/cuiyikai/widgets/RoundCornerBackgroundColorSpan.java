package com.java.cuiyikai.widgets;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * A span with round corner and colored background at the same time.
 */
public class RoundCornerBackgroundColorSpan extends ReplacementSpan {

    private final int cornerRadius;
    private final int backgroundColor;
    private final int textColor;

    public RoundCornerBackgroundColorSpan(int cornerRadius, int backgroundColor, int textColor) {
        this.cornerRadius = cornerRadius;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence charSequence, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        RectF rect = new RectF(x, top, x + measureText(paint, charSequence, start, end), fm.bottom - fm.top);
        paint.setColor(backgroundColor);
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint);
        paint.setColor(textColor);
        canvas.drawText(charSequence, start, end, x, y, paint);
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence charSequence, int start, int end, @Nullable Paint.FontMetricsInt fontMetricsInt) {
        return Math.round(paint.measureText(charSequence, start, end));
    }

    private float measureText(Paint paint, CharSequence text, int start, int end) {
        return paint.measureText(text, start, end);
    }
}
