package com.example.bottombar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.bottombar.R;
import com.example.bottombar.step.service.StepService;

import java.util.ArrayList;
import java.util.List;
import java.math.*;

public class LineView extends View {
    //主画笔
    private Paint mPaint;
    //标题矩形
    private Rect titleRect;
    //数据区域矩形
    private Rect dataRect;
    String titleName = "步数";
    String Steptitle = "步数";
    String Caltitle = "卡路里";
    String[] yStrs = {"4000","3000","2000","1000","0"};
    String[] xStrs = {"0点", "2点", "6点", "10点", "14点", "18点", "22点"};
    Integer[] dataArray = {0, 0, 0, 0, 0, 0, 0};
    Integer[] CaldataArray ={0,0,0,0,0,0,0};
    Integer[] StepdataArray ={0,0,0,0,0,0,0};

    //定时器
    private CountDownTimer timer;
    //手势点击记录周期
    private int weekPosition = -1;
    /**
     * 曲线上数据点
     */
    private List<Point> pointList = new ArrayList<>();
    //   左上角标题文本颜色  x轴底部文本颜色  x轴刻度颜色
    private int textTitleColor, textXColor, scaleXColor;
    //    y轴左边文本颜色  贝塞尔数据线颜色  手势点击后数据标注线颜色
    private int textYColor, lineBesselColor, lineClickColor;
    //              虚线颜色       y轴左边文本距离     x轴下边文本距离底部的高度
    private int lineImaginaryColor, yScaleDataWidth, xScaleDataHeight;
    //    x轴刻度线宽度  贝塞尔数据线宽度
    private int scaleXWidth, lineBesselWidth;
    //            虚线宽度  手势点击后数据标注线宽度   数据区域距离顶部距离
    private float lineImaginaryWidth, lineClickWidth, dataMarginTop;
    //数据区域距离右边的距离
    private int dataMarginRightWidth;
    //数据区域内部padding距离
    private int dataPaddingLeft;
    private int dataPaddingRight;
    private int dataPaddingTop;
    private int dataPaddingBottom;

    public LineView(Context context) {
        this(context, null);
    }
    public LineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initXmlAttrs(context, defStyleAttr);
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2);
    }

    public void initData()
    {
        double d;

        StepdataArray[0]=CaldataArray[0]=0;
        for(int i=1;i<7;i++) {
            StepdataArray[i] = StepService.TIME_STEP[i]-StepService.TIME_STEP[i-1];
            if(StepdataArray[i]<0) StepdataArray[i]=CaldataArray[i]=0;
            else
            {
                d=StepdataArray[i]*0.04;
                int k=(int) Math.round(d);
                CaldataArray[i]=k;
            }
        }
    }
    public void setStepData()
    {
        initPaint();
        titleName =Steptitle;
        for(int i=1;i<7;i++) {
            dataArray[i]=StepdataArray[i];
        }
        this.invalidate();
    }
    public void setCalData()
    {
        initPaint();
        titleName =Caltitle;
        for(int i=1;i<7;i++) {
            dataArray[i]=CaldataArray[i];
        }
        this.invalidate();
    }

    private void initXmlAttrs(Context context, int attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VFXBesselChart);
        if (typedArray == null) return;
        textTitleColor = typedArray.getColor(R.styleable.VFXBesselChart_text_title_color, getResources().getColor(R.color.red));
        textXColor = typedArray.getColor(R.styleable.VFXBesselChart_text_x_color, getResources().getColor(R.color.black));
        scaleXColor = typedArray.getColor(R.styleable.VFXBesselChart_scale_x_color, getResources().getColor(R.color.gray_d5));
        textYColor = typedArray.getColor(R.styleable.VFXBesselChart_text_y_color, getResources().getColor(R.color.black));
        lineBesselColor = typedArray.getColor(R.styleable.VFXBesselChart_line_bessel_color, getResources().getColor(R.color.gold));
        lineClickColor = typedArray.getColor(R.styleable.VFXBesselChart_line_click_color, getResources().getColor(R.color.black_an));
        lineImaginaryColor = typedArray.getColor(R.styleable.VFXBesselChart_line_imaginary_color, getResources().getColor(R.color.gray_d5));
        scaleXWidth = typedArray.getInteger(R.styleable.VFXBesselChart_scale_x_width, 2);
        lineImaginaryWidth = typedArray.getDimension(R.styleable.VFXBesselChart_line_imaginary_width, 2);
        lineBesselWidth = typedArray.getInteger(R.styleable.VFXBesselChart_line_bessel_widht, 3);
        lineClickWidth = typedArray.getDimension(R.styleable.VFXBesselChart_line_click_widht, 2);
        dataMarginTop = typedArray.getDimension(R.styleable.VFXBesselChart_data_margin_top, 120);
        yScaleDataWidth = typedArray.getInteger(R.styleable.VFXBesselChart_width_y_scale_data, 150);
        xScaleDataHeight = typedArray.getInteger(R.styleable.VFXBesselChart_height_x_scale_data, 60);
        dataMarginRightWidth = typedArray.getInteger(R.styleable.VFXBesselChart_width_data_margin_right, 10);
        dataPaddingLeft = typedArray.getInteger(R.styleable.VFXBesselChart_padding_data_left, 10);
        dataPaddingRight = typedArray.getInteger(R.styleable.VFXBesselChart_padding_data_right, 10);
        dataPaddingTop = typedArray.getInteger(R.styleable.VFXBesselChart_padding_data_top, 10);
        dataPaddingBottom = typedArray.getInteger(R.styleable.VFXBesselChart_padding_data_bottom, 20);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制左上角标题  浮动收益
        drawTitle(canvas);
        //数据区域  (灰色矩形)
        drawDataRect(canvas);
        //y轴左边数据 ($10000.00)
        drawYStrs(canvas);
        //x轴下面的数据  (3.20)
        drawXStrs(canvas);
        //绘制五条虚线
       // drawImaginary(canvas);
        //绘制x轴的刻度
        drawXScales(canvas);

        //绘制主要数据
        drawMainDatas(canvas);
        //绘制点击事件的显示数据
        drawClickData(canvas);

    }
    //绘制左上角标题  浮动收益
    private void drawTitle(Canvas canvas) {
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.setColor(textTitleColor);
        mPaint.setTextSize(50);
        titleRect = new Rect();
        mPaint.getTextBounds(titleName, 0, titleName.length(), titleRect);
        canvas.drawText(
                titleName,
                50,
                titleRect.height() + 40,
                mPaint
        );
    }
    //数据区域  (灰色矩形)
    private void drawDataRect(Canvas canvas) {
       // mPaint.setColor(Color.parseColor("#66CDAA"));03a0a9
        mPaint.setColor(Color.parseColor("#03a0a9"));
        dataRect = new Rect(
                yScaleDataWidth, (int) dataMarginTop,
                getWidth() - dataMarginRightWidth, getHeight() - xScaleDataHeight
        );
        canvas.drawRect(
                dataRect,
                mPaint
        );
    }
    //y轴左边数据 ($10000.00)
    private void drawYStrs(Canvas canvas) {
        mPaint.setTextSize(25);
        mPaint.setColor(textYColor);
        mPaint.setTextAlign(Paint.Align.RIGHT);
        for (int i = 0; i < yStrs.length; i++) {
            canvas.drawText(
                    yStrs[i],
                    yScaleDataWidth -15,
                    (dataRect.height() - 30) / 4 * i + 20 + dataMarginTop,
                    mPaint
            );
        }
    }
    //x轴下面的数据  (3.20)
    private void drawXStrs(Canvas canvas) {

        mPaint.setTextSize(25);
        mPaint.setColor(textXColor);

        for (int i = 0; i < 7; i++) {
            if (i == 0) {
                mPaint.setTextAlign(Paint.Align.LEFT);
            } else if (i == 6) {
                mPaint.setTextAlign(Paint.Align.RIGHT);
            } else {
                mPaint.setTextAlign(Paint.Align.CENTER);
            }
            Rect xDataRect = new Rect();
            mPaint.getTextBounds(xStrs[i], 0, xStrs[i].length(), xDataRect);
            canvas.drawText(
                    xStrs[i],
                    yScaleDataWidth + 10 + ((dataRect.width() - 20) / 6) * i,
                    dataMarginTop + dataRect.height() + (int) (xDataRect.height() * 1.5),
                    mPaint
            );
        }

    }

    //绘制五条虚线
    private void drawImaginary(Canvas canvas) {

        Paint imaginaryPaint = new Paint();
        imaginaryPaint.setStyle(Paint.Style.FILL);
        imaginaryPaint.setColor(lineImaginaryColor);
        imaginaryPaint.setStrokeWidth(lineImaginaryWidth);
        //绘制长度为4的实线后再绘制长度为4的空白区域，0位间隔
        DashPathEffect effect = new DashPathEffect(new float[]{4, 4}, 0);
        imaginaryPaint.setPathEffect(effect);
        imaginaryPaint.setAntiAlias(true);

        int allImaginaryHeight = dataRect.height() - dataPaddingTop - dataPaddingBottom;

        for (int i = 0; i < 5; i++) {
            //两点一线
            canvas.drawLine(
                    yScaleDataWidth + dataPaddingLeft, dataMarginTop + dataPaddingTop + allImaginaryHeight / 4 * i,
                    getWidth() - dataMarginRightWidth - dataPaddingRight, dataMarginTop + dataPaddingTop + allImaginaryHeight / 4 * i,
                    imaginaryPaint
            );
        }

    }

    //绘制x轴的刻度
    private void drawXScales(Canvas canvas) {
        Paint xScalesPaint = new Paint();
        xScalesPaint.setAntiAlias(true);
        xScalesPaint.setStyle(Paint.Style.STROKE);
        xScalesPaint.setColor(scaleXColor);
        xScalesPaint.setStrokeWidth(scaleXWidth);
        int allXScalesWidth = getWidth() - yScaleDataWidth - dataPaddingLeft - dataPaddingRight - dataMarginRightWidth;
        for (int i = 0; i < 7; i++) {
            canvas.drawLine(
                    yScaleDataWidth + dataPaddingLeft + allXScalesWidth / 6 * i, getHeight() - xScaleDataHeight,
                    yScaleDataWidth + dataPaddingLeft + allXScalesWidth / 6 * i, getHeight() - xScaleDataHeight - dataPaddingBottom + 10,
                    xScalesPaint
            );
        }
    }

    //绘制主要数据
    private void drawMainDatas(Canvas canvas) {

        Paint dataPaint = new Paint();
        dataPaint.setAntiAlias(true);
        dataPaint.setStyle(Paint.Style.STROKE);
        dataPaint.setColor(lineBesselColor);
        dataPaint.setStrokeWidth(lineBesselWidth);

        Path path = new Path();
        path.reset();
        //计算整个数据区域的宽高
        int allDataHeight = dataRect.height() - dataPaddingTop - dataPaddingBottom;
        int allDataWidth = dataRect.width() - dataPaddingLeft - dataPaddingRight;
        /**
         * 计算数据的XY坐标
         */
        for (int i = 0; i < dataArray.length; i++) {
            Point point = new Point();
            point.set(
                    yScaleDataWidth + dataPaddingLeft + allDataWidth / 6 * i,
                    (int) (getHeight() - xScaleDataHeight - dataPaddingBottom - (allDataHeight / 10000.0 * dataArray[i]))
            );
            pointList.add(point);
        }
        //数据数据点绘制贝塞尔区线
        drawScrollLine(canvas, dataPaint);
    }
    //绘制点击事件的显示数据
    private void drawClickData(Canvas canvas) {

        //如果为-1代表没有点击
        if (weekPosition == -1) return;

        //这里重新重建画笔
        Paint weekPaint = new Paint();
        weekPaint.setAntiAlias(true);
        weekPaint.setStrokeWidth(lineClickWidth);
        weekPaint.setTextSize(20);
        weekPaint.setColor(lineClickColor);

        //绘制竖线（两点一线）
        canvas.drawLine(
                //y轴标注数据宽度 + 数据模块左padding值 + 六天总宽度 / 6 * 第几天
                yScaleDataWidth + dataPaddingLeft + (dataRect.width() - dataPaddingLeft - dataPaddingRight) / 6 * weekPosition,
                //数据模块上padding值 + 数据模块距离view顶部距离
                dataPaddingTop + dataMarginTop,
                yScaleDataWidth + dataPaddingLeft + (dataRect.width() - dataPaddingLeft - dataPaddingRight) / 6 * weekPosition,
                //整个view高度 - x轴下方标注数据高度 - 数据模块下padding值
                getHeight() - xScaleDataHeight - dataPaddingBottom,
                weekPaint
        );
        //绘制矩形数据
        Point weekPoint = pointList.get(weekPosition);      //获取数据点
        String weekDataStr =  dataArray[weekPosition]+"kcal";

        ToastUtils.showToast(getContext(), weekDataStr);

        Rect weekRect = new Rect();
        weekPaint.getTextBounds(weekDataStr, 0, weekDataStr.length(), weekRect);

        weekPaint.setColor(Color.parseColor("#aac69d5e"));
        weekPaint.setTextAlign(Paint.Align.CENTER);
        //如果是前五天（周一到周五）显示在数据线右边 否则 显示在数据线左边
        if (weekPosition < 5) {
            /**
             * 精细计算了一下宽度，高度同理，宽度根据文本宽度+预留padding值
             * 宽度：当前数据点x坐标 + 文本宽度 + 预留padding值（30）
             */
            Rect weekBgRect = new Rect(
                    weekPoint.x + 5, weekPoint.y - 35,
                    weekPoint.x + weekRect.width() + 30, weekPoint.y - 5
            );
            canvas.drawRect(weekBgRect, weekPaint);
            weekPaint.setColor(Color.WHITE);
            canvas.drawText(
                    weekDataStr,
                    weekPoint.x + 5 + weekBgRect.width() / 2,
                    weekPoint.y - 12,
                    weekPaint
            );
        }
        else {
            Rect weekBgRect = new Rect(
                    weekPoint.x - weekRect.width() - 30, weekPoint.y - 35,
                    weekPoint.x - 5, weekPoint.y - 5
            );
            canvas.drawRect(weekBgRect, weekPaint);
            weekPaint.setColor(Color.WHITE);
            canvas.drawText(
                    weekDataStr,
                    weekPoint.x - 5 - weekBgRect.width() / 2,
                    weekPoint.y - 12,
                    weekPaint
            );
        }

    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                //两天之间的宽度
                int weekScale = pointList.get(1).x - pointList.get(0).x;
                for (int i = 0; i < pointList.size(); i++) {
                    //for循环遍历计算手势点击坐标离哪天最近
                    if (Math.abs(event.getX() - pointList.get(i).x) < weekScale / 2) {
                        //标注点击
                        weekPosition = i;
                        //倒计时4秒隐藏（倒计时之前先取消上次倒计时）
                        if (timer != null)
                            timer.cancel();
                        //开启倒计时
                        startCountDownTime(4);
                        invalidate();
                        break;
                    }
                }
                break;
        }
        return true;
    }

    //多个数据点绘制贝塞尔区线
    private void drawScrollLine(Canvas canvas, Paint dataPaint) {
        Point startp = new Point();
        Point endp = new Point();
        for (int i = 0; i < pointList.size() - 1; i++) {
            startp = pointList.get(i);
            endp = pointList.get(i + 1);
            int wt = (startp.x + endp.x) / 2;
            Point p3 = new Point();
            Point p4 = new Point();
            p3.y = startp.y;
            p3.x = wt;
            p4.y = endp.y;
            p4.x = wt;
            Path path = new Path();
            path.moveTo(startp.x, startp.y);
            path.cubicTo(p3.x, p3.y, p4.x, p4.y, endp.x, endp.y);
            canvas.drawPath(path, dataPaint);
        }
    }

    private void startCountDownTime(long time) {

        timer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //每隔countDownInterval秒会回调一次onTick()方法
            }

            @Override
            public void onFinish() {
                //倒计时结束
                weekPosition = -1;
                invalidate();
            }
        };
        timer.start();// 开始计时
        //timer.cancel(); // 取消
    }
}
