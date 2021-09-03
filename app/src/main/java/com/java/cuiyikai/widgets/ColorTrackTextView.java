package com.java.cuiyikai.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.java.cuiyikai.R;

@SuppressLint("AppCompatCustomView")
public class ColorTrackTextView extends TextView {

    private Paint mOriginPaint;
    private Paint mChangePaint;
    //设置变色朝向
    Direction mDirection = Direction.LEFT_TO_RIGHT;


    public enum Direction {
        LEFT_TO_RIGHT, RIGHT_TO_LEFT
    }

    public ColorTrackTextView(Context context) {
        this(context, null);
    }

    public ColorTrackTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorTrackTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ColorTrackTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initPaint(context, attrs);
    }

    /**
     * 初始化
     */
    private void initPaint(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ColorTrackTextView);
        int originColor = array.getColor(R.styleable.ColorTrackTextView_originColor, getTextColors().getDefaultColor());
        int changeColor = array.getColor(R.styleable.ColorTrackTextView_changeColor, getTextColors().getDefaultColor());
        mOriginPaint = getPaintByColor(originColor);
        mChangePaint = getPaintByColor(changeColor);
        array.recycle();
    }

    private Paint getPaintByColor(int color) {
        Paint paint = new Paint();
        //设置颜色
        paint.setColor(color);
        //抗锯齿
        paint.setAntiAlias(true);
        //防抖动
        paint.setDither(true);
        //设置字体大小
        paint.setTextSize(getTextSize());
        return paint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //设置变色百分比
        float currentProgress = 0.0f;
        int middle = (int) (currentProgress * getWidth());
        //从左变到右
        if (mDirection == Direction.LEFT_TO_RIGHT) {
            drawText(canvas, mOriginPaint, middle, getWidth());
            drawText(canvas, mChangePaint, 0, middle);
        }
        //从右边变到左
        else if (mDirection == Direction.RIGHT_TO_LEFT) {
            drawText(canvas, mOriginPaint, 0, getWidth() - middle);
            drawText(canvas, mChangePaint, getWidth() - middle, getWidth());
        }
    }

    private void drawText(Canvas canvas, Paint paint, int start, int end) {
        canvas.save();
        //根据进度计算中间值
        Rect rect = new Rect(start, 0, end, getHeight());
        canvas.clipRect(rect);
        String text = getText().toString();
        //计算起始位置
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int x = getWidth() / 2 - bounds.width() / 2;
        //计算基线
        Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
        int dy = (fontMetricsInt.bottom - fontMetricsInt.top) / 2 - fontMetricsInt.bottom;
        int baseLine = getHeight() / 2 + dy;
        canvas.drawText(text, x, baseLine, paint);//初始颜色
        canvas.restore();
    }
}