package com.jacob.circle.disc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Package : com.jacob.circle.disc
 * Author : jacob
 * Date : 15-4-1
 * Description : 这个类是用来xxx
 */
public class CircleIndexView extends ViewGroup {

    private Bitmap mBitmapBg;

    private boolean isShowCircle = false;

    public CircleIndexView(Context context) {
        this(context, null);
    }

    public CircleIndexView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleIndexView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBitmapBg = BitmapFactory.decodeResource(getResources(), R.mipmap.roulette);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = 0;
        int height = 0;

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(widthMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = mBitmapBg.getWidth();
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = mBitmapBg.getHeight();
        }

        int layoutSize = Math.max(width, height);
        Log.e("TAG", layoutSize + "/" + width + "/" + height);
        setMeasuredDimension(layoutSize, layoutSize);

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        canvas.drawColor(Color.GRAY);
        if (isShowCircle) {
            canvas.drawBitmap(mBitmapBg, 0, 0, null);
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            int count = getChildCount();
            View view = getChildAt(0);
            int childW = view.getMeasuredWidth();
            int childH = view.getMeasuredHeight();

            int left = getWidth() / 2 - childW / 2;
            int top = getHeight() / 2 - childH / 2;
            view.layout(left, top, left + childW, top + childH);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isShowCircle = true;
                break;
            case MotionEvent.ACTION_MOVE:
                isShowCircle = true;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isShowCircle = false;
                break;
        }
        postInvalidate();
        return true;
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
