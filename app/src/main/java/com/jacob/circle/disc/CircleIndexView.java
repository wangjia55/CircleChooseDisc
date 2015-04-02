package com.jacob.circle.disc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
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

    /**
     * 选择圆盘的背景
     */
    private Bitmap mBitmapBg;

    /**
     * 当前是否显示出了圆盘
     */
    private boolean isShowCircle = false;

    /**
     * 26个字母放在一个圆盘中，这是没一个字母对应圆盘的角度
     */
    private static float sPerAngle = 360.0f / 26;

    /**
     * 旋转的角度
     */
    private float mAngle = 0;

    /**
     * 开始的角度
     */
    private float mStartAngle = 0;

    /**
     * 屏幕中心点的坐标
     */
    private float mCenterXY;

    /**
     * 字母数组，用于判断外部传入的字母对应的index 和角度
     */
    private String[] mLetters = new String[]{
            "a", "b", "c",
            "d", "e", "f",
            "g", "h", "i",
            "j", "k", "l",
            "m", "n", "o",
            "p", "q", "r",
            "s", "t", "u",
            "v", "w", "x",
            "y", "z"};


    /**
     * 事件回调的接口
     */
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
        mCenterXY = layoutSize / 2.0f;
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
        canvas.save();
        if (isShowCircle) {
            canvas.rotate((mAngle + mStartAngle), getWidth() / 2, getHeight() / 2);
            canvas.drawBitmap(mBitmapBg, 0, 0, null);
        }
        canvas.restore();
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


    /**
     * 记录上一次的位置
     */
    private float mLastX;
    private float mLastY;

    /**
     * @param event
     *
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();
        float startAngle = 0;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isShowCircle = true;
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                isShowCircle = true;
                startAngle = getTranslateAngle(mLastX, mLastY);
                float endAngle = getTranslateAngle(x, y);

                //得到当前是第几象限制
                int quadrant = getQuaDrant(x, y);
                switch (quadrant) {
                    case 2:
                    case 3:
                        //这里2，3像限之所以是start-end，
                        // 是因为在顺时针旋转的情况下，这两个象限所获取的角度是逐渐减小的
                        //所以为了和1，4象限一样保证，角度差值是正值，所以用start-end
                        //当然同理，逆时针旋转角度差都是负数
                        mAngle += startAngle - endAngle;
                        break;
                    case 1:
                    case 4:
                        mAngle += endAngle - startAngle;
                        break;
                }
                //对超过范围的角度进行重置
                if (mAngle >= 360 || mAngle <= -360) {
                    mAngle = 0;
                }
                updateCenterLetterAndView();

                //每次处理完成之后需要更新上次位置的信息
                mLastX = x;
                mLastY = y;
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
     * 当旋转圆盘后需要更新中心点的View的背景
     */
    private void updateCenterLetterAndView() {
        int index = (int) ((mAngle + mStartAngle) / sPerAngle);
        int length = mLetters.length;
        if (index > 0) {
            index = (length - index) % length;
        } else {
            index = Math.abs(index);
        }
        updateChildDrawable(mLetters[index]);
        if (mIndexListener != null){
            mIndexListener.onIndexChange(mLetters[index].toUpperCase());
        }
        requestLayout();
    }


    /**
     * 获得当前触摸点所在第几象限
     */
    private int getQuaDrant(float x, float y) {
        float tempX = x - mCenterXY;
        float tempY = y - mCenterXY;

        if (tempX >= 0 && tempY <= 0) {
            return 1;
        } else if (tempX >= 0 && tempY >= 0) {
            return 4;
        } else if (tempX <= 0 && tempY <= 0) {
            return 2;
        } else {
            return 3;
        }
    }

    /**
     * 获取某个触摸点的角度
     */
    private float getTranslateAngle(float xTouch, float yTouch) {
        float x = xTouch - mCenterXY;
        float y = yTouch - mCenterXY;
        float distance = (float) Math.sqrt(x * x + y * y);
        return (float) (Math.asin(y / distance) * 180 / Math.PI);
    }

    /**
     * 获取当前字母的序号
     */
    private int getLetterPosition(String letter) {
        int length = mLetters.length;
        for (int i = 0; i < length; i++) {
            if (letter.toLowerCase().equals(mLetters[i])) {
                return i;
            }
        }
        return 0;
    }

    /**
     * 获取正常情况下字符对应的角度
     */
    private float getLetterAngle(String letter) {
        int position = getLetterPosition(letter);
        return -1 * sPerAngle * position;
    }


    /**
     * 可以通过外部直接设置当前的字母序号
     */
    public void setIndexLetter(String letter) {
        if (letter == null) return;
        if (letter.length() > 1) {
            letter = letter.substring(0, 1);
        }
        updateChildDrawable(letter);
        mStartAngle = getLetterAngle(letter);
        invalidate();
    }


    /**
     * 根据字符的名字找出对应的资源文件，并让中心的ImageView替换图片
     */
    private void updateChildDrawable(String letter) {
        letter = letter.toLowerCase();
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

    /**
     * 字符旋转更改后的事件回调接口
     */
    public interface OnIndexChangeListener {
        void onIndexChange(String letter);
    }
}
