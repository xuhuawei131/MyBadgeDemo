package com.lingdian.mybadgedemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;

/**
 * Created by lingdian on 17/9/1.
 */

public class MyBadgeView extends android.support.v7.widget.AppCompatImageView {
    private static final int RADIUS = 20;//圆形半径
    private static final int BOARDER = 4;
    private static final int BIG_RADIUS = RADIUS + BOARDER;//外圈的半径

    private static final int BASE_Y = BIG_RADIUS;//
    private static final int BASE_X = BIG_RADIUS;//

    //内圈的画笔
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //外圈的画笔
    private final Paint mPaintBig = new Paint(Paint.ANTI_ALIAS_FLAG);
    //文本的画笔
    private final Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);


    // 圈中的数字
    private int num;
    //被依附图片的范围
    private Rect imageBound = new Rect();

    //数字的字体大小
    private float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics());

    //文本的矩形
    private Rect textRect = null;
    //图片的右上角左边
    private int imgRightTopX = 0;
    private int imgRightTopY = 0;

    public MyBadgeView(Context context) {
        super(context);
        init(null);
    }

    public MyBadgeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MyBadgeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public void setBadgeNum(int num) {
        this.num = num;
        invalidate();
    }

    private void init(AttributeSet attrs) {
        //绘制图形的画笔 内圈
        mPaint.setAntiAlias(true);
        mPaint.setColor(0xFFFC4C47);

        //绘制图形的画笔 外圈
        mPaintBig.setAntiAlias(true);
        mPaintBig.setColor(0xFFFFFFFF);


        //文字画笔
        mTextPaint.setColor(0xFFFFFFFF);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }


    private Rect measureTextBound(Paint paint, String text) {
        Rect textBound = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBound);
        return textBound;
    }

    /**
     * 获取字体高度
     * http://blog.csdn.net/lvxiangan/article/details/8540774
     *
     * @param baseY
     * @return
     */
    private Paint.FontMetrics getFontMetrics(float baseY) {
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        // 计算每一个坐标
        float topY = baseY + fontMetrics.top;
        float ascentY = baseY + fontMetrics.ascent;
        float descentY = baseY + fontMetrics.descent;
        float bottomY = baseY + fontMetrics.bottom;
        return fontMetrics;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        Drawable drawable = this.getDrawable();
        int width = 0;
        int height = 0;

        int drawWidth = 0;
        int drawHeight = 0;
        if (drawable != null) {
            drawWidth = this.getDrawable().getIntrinsicWidth();
            drawHeight = this.getDrawable().getIntrinsicWidth();
        }

        width = getMeasuredWidth() + BIG_RADIUS * 2;
        height = getMeasuredHeight() + BIG_RADIUS * 2;
        setMeasuredDimension(width, height);

        //图片的右上角左边  也是圆形的中心位置
        imgRightTopX = drawWidth + BASE_X;
        imgRightTopY = BASE_Y;

        imageBound = new Rect(BASE_X, BASE_Y, BASE_X + drawWidth, BASE_Y + drawHeight);

    }


    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);


        BitmapDrawable bitmapDrawable = (BitmapDrawable) getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();

        canvas.drawBitmap(bitmap, null, imageBound, null);

        String numStr;

        if (num > 99) {
            numStr = "+99";
        } else {
            numStr = String.valueOf(num);
        }

        //文本的矩形
        textRect = measureTextBound(mTextPaint, numStr);


        if (isCircle()) {//圆形的
            //绘制外面的大圆形
            canvas.drawCircle(imgRightTopX, imgRightTopY,
                    BIG_RADIUS, mPaintBig);
            //绘制里面的小圆形
            canvas.drawCircle(imgRightTopX, imgRightTopY,
                    RADIUS, mPaint);
            //下面的是 左边对齐
//            canvas.drawText(numStr, RADIUS+drawWidth - textRect.width() / 2, RADIUS + textRect.height() / 2, mTextPaint);
            //下面的是文字居中  文字的y 是文字的下边  不是我们传统的左上角
            canvas.drawText(numStr, imgRightTopX, imgRightTopY + textRect.height() / 2, mTextPaint);
        } else {//画一个椭圆形

            drawOuterRect(canvas);

            drawInnerRect(canvas);

            drawText(canvas, numStr);
        }


    }


    /**
     * 是否需要画圆形还是椭圆形
     *
     * @return true 为圆形
     */
    boolean isCircle() {
        float textWidth = getTextWidth() / 2;
        float textHeight = textRect.height() / 2;
        return (textHeight * textHeight + textWidth * textWidth) <= RADIUS * RADIUS;
    }

    /**
     * 文本的宽度
     * @return
     */
    private int getTextWidth() {
        return textRect.width()+14;
    }

    /**
     * 获取字体的高度
     * @return
     */
    private int getTextHeight(){
        return textRect.height()+14;
    }
    /**
     * 绘制外圈的图形
     * @param canvas
     */
    void drawOuterRect(Canvas canvas) {
        //文本的宽度
        int textWidth = getTextWidth();
        //中间矩形的宽度
        float rectWidth = (float) (textWidth - Math.sqrt(2) * RADIUS);
        float rectHeight = BIG_RADIUS + BIG_RADIUS;

        //中间矩形的左上角 x左边
        float centerRectLeftX = imgRightTopX - rectWidth;

        //外圈的顶部y值
        float BASE_OUTER_TOP_Y = 0;

        //画 左边半球
        RectF ovalRectLeft = new RectF(centerRectLeftX - BIG_RADIUS, BASE_OUTER_TOP_Y, centerRectLeftX + BIG_RADIUS, BASE_OUTER_TOP_Y + rectHeight);
        //画 中间矩形
        RectF rectCenter = new RectF(centerRectLeftX, BASE_OUTER_TOP_Y, imgRightTopX, BASE_OUTER_TOP_Y + rectHeight);
        //画 右边半球
        RectF ovalRectRight = new RectF(imgRightTopX - BIG_RADIUS, BASE_OUTER_TOP_Y, imgRightTopX + BIG_RADIUS, BASE_OUTER_TOP_Y + rectHeight);

        // 绘制中间的矩形
        canvas.drawRect(rectCenter, mPaintBig);
        // 绘制左半球的半圆  0度表示右边水平的位置，90度表示先顺时针，也就是圆形的最下面的那个点， 旋转90作为起点
        canvas.drawArc(ovalRectLeft, 90, 180, false,
                mPaintBig);
        // 绘制右半球的半圆  -90表示 逆时针旋转90读 也就是顺时针270度，在圆形的最上面
        canvas.drawArc(ovalRectRight, -90, 180, false,
                mPaintBig);


    }

    /**
     * 绘制内圈的图形
     * @param canvas
     */
    void drawInnerRect(Canvas canvas) {
        //文本的宽度
        int textWidth = getTextWidth();
        //中间矩形的宽度
        float rectWidth = (float) (textWidth - Math.sqrt(2) * RADIUS);
        float rectHeight = RADIUS + RADIUS;
        //中间矩形的左上角 x左边
        float centerRectLeftX = imgRightTopX - rectWidth;
        // 内圈的顶部y值
        float BASE_INNER_TOP_Y = BIG_RADIUS - RADIUS;

        //画 左边半球
        RectF ovalRectLeft = new RectF(centerRectLeftX - RADIUS, BASE_INNER_TOP_Y, centerRectLeftX + RADIUS, BASE_INNER_TOP_Y + rectHeight);

        //画 中间矩形
        RectF rectCenter = new RectF(centerRectLeftX, BASE_INNER_TOP_Y, imgRightTopX, BASE_INNER_TOP_Y + rectHeight);

        //画 右边半球
        RectF ovalRectRight = new RectF(imgRightTopX - RADIUS, BASE_INNER_TOP_Y, imgRightTopX + RADIUS, BASE_INNER_TOP_Y + rectHeight);


        // 绘制中间的矩形
        canvas.drawRect(rectCenter, mPaint);
        // 绘制左半球的半圆  0度表示右边水平的位置，90度表示先顺时针，也就是圆形的最下面的那个点， 旋转90作为起点
        canvas.drawArc(ovalRectLeft, 90, 180, false,
                mPaint);
        // 绘制右半球的半圆  -90表示 逆时针旋转90读 也就是顺时针270度，在圆形的最上面
        canvas.drawArc(ovalRectRight, -90, 180, false,
                mPaint);


    }

    /**
     * 绘制文字
     *
     * @param canvas 画布
     * @param numStr 要绘制的文本
     */
    private void drawText(Canvas canvas, String numStr) {
        //文本的宽度
        int textWidth = getTextWidth();
        //中间矩形的宽度
        float rectWidth = (float) (textWidth - Math.sqrt(2) * RADIUS);


        int textHeight=getTextHeight();

        if(textHeight>RADIUS){//如果字体高度比 内圈半径还大  那么会出现位置错乱
            textHeight=RADIUS;
        }

        //绘制文字
        canvas.drawText(numStr, imgRightTopX - rectWidth / 2, BASE_Y + textHeight / 2, mTextPaint);

    }

}
