package com.yezhou.progressdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ProgressBar;

/**
 * Created by Administrator on 2018/1/7.
 */

public class LevelProgressBar extends ProgressBar {
    private final int EMPTY_MESSAGE = 1;

    // xml中定义的属性
    private int levelTextChooseColor;
    private int levelTextUnChooseColor;
    private int levelTextSize;
    private int progressStartColor;
    private int progressEndColor;
    private int progressBgColor;
    private int progressHeight;

    // java 代码中需要设置的属性
    private int levels;
    private int unitLevelProgress;
    private String[] levelTexts;
    private int currentLevel;
    private int animInterval;    // 控制进度条移动速度
    private int targetProgress;    // 等级/等级总数*MAX(未经过设置就是100)转换成int

    private Paint mPaint;
    private int mTotalWidth;
    private int textHeight;

    public void setStrStrar(String strStrar) {
        this.strStrar = strStrar;
    }

    String strStrar;    // 若需要初始点文字则此处有值

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int progress = getProgress();
            // 小于目标值时增加进度，大于目标值时减小进度
            if (progress < targetProgress) {
                Log.d("admin_progress", getProgress()+ "ppb");
                setProgress(++progress);    // 设置进度值 先加再赋值
//                Log.d("admin_progress", progress + "cp");
//                Log.d("admin_progress", getProgress()+ "ppa");
                handler.sendEmptyMessageDelayed(EMPTY_MESSAGE, animInterval);    // what delay 延迟时间
            } else if (progress > targetProgress){
                setProgress(--progress);
                handler.sendEmptyMessageDelayed(EMPTY_MESSAGE, animInterval);
            } else {    // 等于target就不再发送消息，不对进度进行操作
                handler.removeMessages(EMPTY_MESSAGE);
            }
        }
    };

    public LevelProgressBar(Context context) {
        super(context);
    }

    public LevelProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LevelProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainStyledAttributes(attrs);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(levelTextSize);
        mPaint.setColor(levelTextUnChooseColor);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {    // 要使用wrap_content模式，
                                                                                                // 则需要重写onMeasure（）方法，
                                                                                                // 并在其中对wrap_content模式做特殊的处理.
                                                                                                // 默认大小为父View允许的最大空间，
                                                                                                // 这时需要设置View需要的具体大小，
                                                                                                // 否则最后的效果会和match_parent一样
        int width = MeasureSpec.getSize(widthMeasureSpec);    // 测量 http://blog.csdn.net/chunqiuwei/article/details/50722061
        int height = MeasureSpec.getSize(heightMeasureSpec);    // 父布局高度
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);    // 布局高度模式

        // layout_height为wrap_content时计算View的高度
        if (heightMode != MeasureSpec.EXACTLY) {
            textHeight = (int) (mPaint.descent() - mPaint.ascent());    // 得到的是基于当前文本类型和文本大小，文本区域可能的最大高度
            // 10dp为等级文字与进度条之间的间隔
            height = getPaddingTop() + getPaddingBottom() + textHeight + progressHeight + dpTopx(20);
            // xml 文件中设置PaddingTop() PaddingBottom()会在此处应用进来但内边距只会显示空白 不会显示具体内容(进度条 文字)
            // textHeight 系统估算的值 progressHeight xml文件中具体设置的值 但最后是显示不全
            // 过多了就会往下撑大 但text progress占用的位置不变 所以大小只要显示全就好了
//            Log.d("admin_onMeasure", getPaddingTop() + "pt");    // 0 xml中设置就有值了
//            Log.d("admin_onMeasure", getPaddingBottom() + "pb");    // 0
//            Log.d("admin_onMeasure", textHeight+ "th");    // 52
//            Log.d("admin_onMeasure", progressHeight + "ph");    // 30 xml 文件中设置的10dp
//            Log.d("admin_onMeasure", dpTopx(20) + "dp20");    // 60
//            Log.d("admin_onMeasure", height+ "h");    // 82 + 60  textHeitht + progressHeight + dpTopx(20)
//            Log.d("admin_onMeasure", MeasureSpec.getSize(heightMeasureSpec) + "mh");    // 1536
        }
        setMeasuredDimension(width, height);
        mTotalWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();    // 内左边距 mTotalWidth用于计算文字的位置
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        // 留出padding的位置
        canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop());

        if (strStrar != null) {
            int textWidth = (int) mPaint.measureText(strStrar);
            mPaint.setColor(levelTextChooseColor);
            mPaint.setTextSize(levelTextSize);    // 设置字的大小
            canvas.drawText(strStrar, 0, textHeight, mPaint);

        }

        // 绘制等级文字
        for (int i = 0; i < levels; i++) {
            int textWidth = (int) mPaint.measureText(levelTexts[i]);    // 取得字符串显示的宽度值
//            Log.d("admin_draw_text_width", textWidth + "tw");
            mPaint.setColor(levelTextUnChooseColor);    // 设置未被选中的字的颜色
            mPaint.setTextSize(levelTextSize);    // 设置字的大小
//            Log.d("admin_draw_level", currentLevel + "cl");    // 选择1时 currentLevel直接变为1
//            Log.d("admin_draw_level", targetProgress + "tp");    // 变为对应等级的进度
//            Log.d("admin_draw_level", getProgress() + "p");
            // 到达指定等级时 变色
            Log.d("admin_draw_level", (getProgress() / unitLevelProgress )+"  " + getProgress() + "  " + unitLevelProgress);
            if (i + 1 <= getProgress() / unitLevelProgress) {
                // i + 1 == getProgress() / unitLevelProgress && getProgress() == unitLevelProgress * (getProgress() / unitLevelProgress ) 实现到达相应进度(非目标进度) 描述字变色(闪一下)
                // i + 1 == getProgress() / unitLevelProgress 实现到达相应等级描数字点亮 直至到达下一等级
                // i + 1 <= getProgress() / unitLevelProgress 到达一个等级 之前所有等级的描述字全亮
                mPaint.setColor(levelTextChooseColor);
            }
//            if (getProgress() == targetProgress && currentLevel >= 1 && currentLevel <= levels  && i == currentLevel - 1) {    // i == currentLevel - 1 画到正确的进度上
//                mPaint.setColor(levelTextChooseColor);
//            }
            canvas.drawText(levelTexts[i], mTotalWidth / levels * (i + 1) - textWidth, textHeight, mPaint);    // x和y，它表示文字的绘制区域的起点，它是框住文本区域的矩形的左下角的点 文字从下往上画 锚点在左下角
    }

        int lineY = textHeight + progressHeight /2 + dpTopx(10);    // 进度条y位置
//        // 绘制进度条背景
        mPaint.setColor(progressBgColor);
        mPaint.setStrokeCap(Paint.Cap.ROUND);    // StrokeCap为Round，因此，startX和stopX要考虑半圆的宽度，不然开始和结尾处的笔尖圆形会画到区域之外
        mPaint.setStrokeWidth(progressHeight);    // 描边宽度
        canvas.drawLine(0 + progressHeight / 2, lineY, mTotalWidth - progressHeight / 2, lineY, mPaint);

        // 绘制进度条
        int reachedPartEnd = (int) (getProgress() * 1.0f / getMax() * mTotalWidth) ;    // 进度通过比值得到
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        // 设置进度条渐变色
        // 渐变色起始坐标 颜色 渐变色终止坐标和颜色 图像超出原始边界时的呈现方式
        Shader shader = new LinearGradient(0, lineY, getWidth(), lineY, progressStartColor, progressEndColor, Shader.TileMode.CLAMP);
        mPaint.setShader(shader);
        if (reachedPartEnd>0) {
            canvas.drawLine(progressHeight/2,lineY,reachedPartEnd,lineY,mPaint);
        }
        else if (reachedPartEnd>50)
        {
            canvas.drawLine(progressHeight/2, lineY, reachedPartEnd - progressHeight/2, lineY, mPaint);    // - progressHeight/2
            // 当reachedParEnd小于progressHeight/2时会为负数
        }
        /** 原作者修改代码 但体验不够
        int accurateEnd = reachedPartEnd - progressHeight / 2;
        int accurateStart = 0 + progressHeight / 2;
        if (accurateEnd > accurateStart) {
            canvas.drawLine(accurateStart, lineY, accurateEnd, lineY, mPaint);
        } else {
            canvas.drawLine(accurateStart, lineY, accurateStart, lineY, mPaint);
        }*/
        mPaint.setShader(null);
        canvas.restore();
    }

    private void obtainStyledAttributes(AttributeSet attrs) {    // 获取xml中设置的属性值
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.LevelProgressBar);
        levelTextUnChooseColor = a.getColor(R.styleable.LevelProgressBar_levelTextUnChooseColor, 0x000000);
        levelTextChooseColor = a.getColor(R.styleable.LevelProgressBar_levelTextChooseColor, 0x333333);
        levelTextSize = (int) a.getDimension(R.styleable.LevelProgressBar_levelTextSize, dpTopx(15));
        progressStartColor = a.getColor(R.styleable.LevelProgressBar_progressStartColor, 0xccffcc);
        progressEndColor = a.getColor(R.styleable.LevelProgressBar_progressEndColor, 0x00ff00);
        progressBgColor = a.getColor(R.styleable.LevelProgressBar_progressBgColor, 0x000000);
        progressHeight = (int) a.getDimension(R.styleable.LevelProgressBar_progressHeight, dpTopx(20));
        a.recycle();
    }

    private int dpTopx(int dp) {    // 中dp和sp之间转化 https://www.cnblogs.com/xilinch/p/4444833.html
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());    // 单位 对应值 封装了显示区域的各种属性值
    }

    // 设置等级数
    public void setLevels(int levels) {
        this.levels = levels;
        this.unitLevelProgress =(int) (getMax() / levels * 1f);
    }

    // 设置不同等级对应的文字
    public void setLevelTexts(String[] texts) {
        levelTexts = texts;
    }

    // 设置当前等级 及当前等级所对应的进度值
    public void setCurrentLevel(int level) {
        this.currentLevel = level;
        this.targetProgress = (int) (level * 1f / levels * getMax());
    }

    // 设置动画间隔，每隔animInterval秒进度+1或-1
    public void setAnimInterval(final int animInterval) {
        this.animInterval = animInterval;
        handler.sendEmptyMessage(EMPTY_MESSAGE);
    }
}
