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
import android.widget.ImageView;

/**
 * Package : com.jacob.circle.disc
 * Author : jacob
 * Date : 15-4-1
 * Description : 这个类是用来xxx
 */
public class CircleIndexView extends ViewGroup {

    private Context mContext;
    private Bitmap mBitmapBg;

    private boolean isShowCircle = false;

    private static float sPerAngle = 360.0f / 26;

    private String[] mLetters = new String[]{
            "A", "B", "C",
            "D", "E", "F",
            "G", "H", "I",
            "J", "K", "L",
            "M", "N", "O",
            "P", "Q", "R",
            "S", "T", "U",
            "V", "W", "X",
            "Y", "Z"};

    private String mCurrentLetters;
    private OnIndexChangeListener mIndexListener;

    public CircleIndexView(Context context) {
        this(context, null);
    }

    public CircleIndexView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleIndexView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
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
        if (isShowCircle) {
            canvas.drawBitmap(mBitmapBg, 0, 0, null);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            View view = getChildAt(0);
            int childW = view.getMeasuredWidth();
            int childH = view.getMeasuredHeight();

            int left = getWidth() / 2 - childW / 2;
            int top = getHeight() / 2 - childH / 2;
            view.layout(left, top, left + childW, top + childH);
        }
    }

    private float mLastX;
    private float mLastY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = 0;
        float y = 0;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isShowCircle = true;
                x = event.getX();
                y = event.getY();

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


    /**
     * 获取当前字母的序号
     */
    private int getLetterPosition(String letter) {
        int length = mLetters.length;
        for (int i = 0; i < length; i++) {
            if (letter.toUpperCase().equals(mLetters[i])) {
                return i;
            }
        }
        return 0;
    }


    public void setIndexLetter(String letter) {
        if (letter == null) return;
        if (letter.length() > 1) {
            letter = letter.substring(0, 1);
        }
        this.mCurrentLetters = letter;
        updateChildDrawable(mCurrentLetters);
    }


    private void updateChildDrawable(String letter) {
        int drawable = getResources().getIdentifier("letter_" + letter, "mipmap", mContext.getPackageName());
        ImageView view = (ImageView) getChildAt(0);
        view.setImageResource(drawable);
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }


    public void setOnIndexChangeListener(OnIndexChangeListener listener) {
        this.mIndexListener = listener;
    }

    public interface OnIndexChangeListener {
        void onIndexChange(String letter);
    }
}
