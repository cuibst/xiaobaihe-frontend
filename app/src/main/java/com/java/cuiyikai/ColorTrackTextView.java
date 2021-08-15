package com.java.cuiyikai;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class ColorTrackTextView extends TextView {

    private Paint mOriginPaint;
    private Paint mChangePaint;
    //设置变色百分比
    private float currentProgress = 0.0f;
    //设置变色朝向
    Direction mdirection = Direction.LEFT_TO_RIGHT;


    public enum Direction {
        LEFT_TO_RIGHT, RIGHT_TO_LEFT;
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
        int orginColor = array.getColor(R.styleable.ColorTrackTextView_originColor, getTextColors().getDefaultColor());
        int changeColor = array.getColor(R.styleable.ColorTrackTextView_changeColor, getTextColors().getDefaultColor());
        mOriginPaint = getPaintByColor(orginColor);
        mChangePaint = getPaintByColor(changeColor);
        array.recycle();
    }

    /**
     * 根据颜色值获取画笔
     *
     * @return
     */
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
        int middle = (int) (currentProgress * getWidth());
        //从左变到右
        if (mdirection == Direction.LEFT_TO_RIGHT) {
            drawText(canvas, mOriginPaint, middle, getWidth());
            drawText(canvas, mChangePaint, 0, middle);
        }
        //从右边变到左
        else if (mdirection == Direction.RIGHT_TO_LEFT) {
            drawText(canvas, mOriginPaint, 0, getWidth() - middle);
            drawText(canvas, mChangePaint, getWidth() - middle, getWidth());
        }
    }

    /**
     * * @description 文字图像绘制
     *
     * @param canvas
     * @param paint
     * @param start
     * @param end
     * @return void
     */
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

    public void setCurrentProgress(float currentProgress) {
        this.currentProgress = currentProgress;
        invalidate();
    }

    //设置变色朝向
    public void setMdirection(Direction mdirection) {
        this.mdirection = mdirection;
    }

    public void setChangeColor(int changeColor) {
        this.mChangePaint.setColor(changeColor);
    }

    public void setOriginColor(int originColor) {
        this.mOriginPaint.setColor(originColor);
    }
}