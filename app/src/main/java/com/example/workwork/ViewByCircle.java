package com.example.workwork;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.PI;

/**
 * TODO: document your custom view class.
 */
public class ViewByCircle extends View {

    private List<ConsumeItem> consumeItemList;
    private float[] Typelist;
    private Paint paint;
    private Paint paintBoarder;
    private TextPaint mTextPaint;
    private float totalAngle = 360f;
    private RectF mRectF;
    private List<Integer> colorList=new ArrayList<>();
    private int cenX;
    private int cenY;

    public ViewByCircle(Context context) {
        super(context);
        init(null, 0);
    }

    public ViewByCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ViewByCircle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

            // Load attributes
            final TypedArray a = getContext().obtainStyledAttributes(
                    attrs, R.styleable.ViewByCircle, defStyle, 0);

            a.recycle();

            // Set up a default TextPaint object
            mTextPaint = new TextPaint();
            mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
            mTextPaint.setTextAlign(Paint.Align.LEFT);

            // Update TextPaint and text measurements from attributes
            consumeItemList= DataSupport.findAll(ConsumeItem.class);
            paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);

            paintBoarder = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintBoarder.setStyle(Paint.Style.STROKE);
            paintBoarder.setAntiAlias(true);
            paintBoarder.setColor(Color.BLACK);

            mTextPaint=new TextPaint(Paint.ANTI_ALIAS_FLAG);
            mTextPaint.setColor(Color.BLACK);
            mTextPaint.setStyle(Paint.Style.FILL);
            mTextPaint.setTextSize(50);

            Typelist=new float[5];
            for(ConsumeItem item:consumeItemList)
            {
                Typelist[item.getType()]+=item.getValue();
            }
        colorList.add(Color.rgb(155, 187, 90));
        colorList.add(Color.rgb(191, 79, 75));
        colorList.add(Color.rgb(242, 167, 69));
        colorList.add(Color.rgb(60, 173, 213));
        colorList.add( Color.rgb(90, 79, 88));

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureValue(widthMeasureSpec), measureValue(heightMeasureSpec));
    }

    private int measureValue(int measureSpec) {
        int result = 100;//设置最小值
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) { //fill_parent或者设置了具体的宽高
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) { //wrap_content
            result = Math.min(result, specSize);
        }
        return result;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        String[] Types={"饮食","娱乐","网络通信","购物","生活水电"};
        //定位圆的大小和位置
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int rWidth = width - getPaddingLeft() - getPaddingRight();
        int rHeight = height - getPaddingTop() - getPaddingBottom();
        int mRadious = Math.min(rWidth, rHeight) / 2;
        //圆心坐标
         cenX = mRadious + getPaddingLeft();
         cenY = mRadious + getPaddingTop();
        //用于存放当前百分比的圆心角度
        float currentAngle = 0.0f;
        float offsetAngle = 0f;//角度偏移量
        float sum=0;
        for(float i:Typelist)
            sum+=i;
        for (int i = 0; i < 5; i++) {
            currentAngle = per2Radious(totalAngle, Typelist[i]/sum);//得到当前角度
            paint.setColor(colorList.get(i));//给画笔设置颜色
            if (mRectF == null) {//设置圆所需的范围
                mRectF = new RectF(getPaddingLeft(), getPaddingTop(), width - getPaddingRight(), width - getPaddingRight());
            }
            //在饼图中显示所占比例
            canvas.drawArc(mRectF, offsetAngle, currentAngle, true, paint);
            //边框
            canvas.drawArc(mRectF, offsetAngle, currentAngle, true, paintBoarder);
            //下次的起始角度
            offsetAngle += currentAngle;

        }
        for(int i=0;i<5;i++)
        {
            currentAngle = per2Radious(totalAngle, Typelist[i]/sum);
            float degree = offsetAngle + currentAngle / 3*2;
            Log.d("AAA", "onDraw: "+degree);
            if(Typelist[i]/sum>0.05f) {
                //先将度数定位到当前所在条目的一半位置
                //根据角度所在不同象限来计算出文字的起始点坐标
                float dx = 0, dy = 0;
                if (degree > 0 && degree <= 90f) {//在第四象限
                    dx = (float) (cenX + mRadious * 2.3 / 3 * Math.cos(2 * PI / 360 * degree));//注意Math.sin(x)中x为弧度值，并非数学中的角度，所以需要将角度转换为弧度
                    dy = (float) (cenY + mRadious * 2.7 / 3 * Math.sin(2 * PI / 360 * degree));
                } else if (degree > 90f && degree <= 180f) {//在第三象限
                    dx = (float) (cenX - mRadious * 2.3 / 3 * Math.cos(2 * PI / 360 * (180f - degree)));
                    dy = (float) (cenY + mRadious * 2.7 / 3 * Math.sin(2 * PI / 360 * (180f - degree)));
                } else if (degree > 180f && degree <= 270f) {//在第二象限
                    dx = (float) (cenX - mRadious * 2.3 / 3 * Math.cos(2 * PI / 360 * (270f - degree)));
                    dy = (float) (cenY - mRadious * 2.7 / 3 * Math.sin(2 * PI / 360 * (270f - degree)));
                } else {
                    dx = (float) (cenX + mRadious * 2.3 / 3 * Math.cos(2 * PI / 360 * (360f - degree)));
                    dy = (float) (cenY - mRadious * 2.7 / 3 * Math.sin(2 * PI / 360 * (360f - degree)));
                }
                //文字的基本线坐标设置为半径的2.3/3位置处，起点y坐标设置为半径的2.7/3位置处
                DecimalFormat decimalFormat=new DecimalFormat(".00");
                canvas.drawText(decimalFormat.format(Typelist[i] / sum * 100f) + "%"+""+Types[i], dx, dy, mTextPaint);
                dy+=60;
                canvas.drawText(Typelist[i]+"元",dx,dy,mTextPaint);
            }
            offsetAngle += currentAngle;
        }


    }

    public float per2Radious(float totalAngle, float percentage) {
        float angle = 0.0f;
            float v = percentage;//先获取百分比
            float itemPer = totalAngle * v;//获取对应角度的百分比
            angle = round(itemPer, 2);//精确到小数点后面2位
        return angle;
    }

    /**
     * 四舍五入到小数点后scale位
     */
    public float round(float v, int scale) {
        if (scale < 0)
            throw new IllegalArgumentException("The scale must be a positive integer or zero");

        BigDecimal bgNum1 = new BigDecimal(v);
        BigDecimal bgNum2 = new BigDecimal("1");
        return bgNum1.divide(bgNum2, scale, BigDecimal.ROUND_HALF_UP).floatValue();
    }


}
