package com.test.app.specialbarchart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Class: com.test.app.specialbarchart.SpecialBarChart</p>
 * <p>Description: </p>
 * <pre>
 *
 *  </pre>
 *
 * @author lujunjie
 * @date 2018/6/19/13:46.
 */
public class SpecialBarChart extends View {

    private SpecialBarChart specialBarChart;
    private Paint mPaint;
    private List<Integer> paintColors = new ArrayList<Integer>();
    private List<Integer> listRange = new ArrayList<Integer>();
    private List<Integer> listDegree = new ArrayList<Integer>();
    private int mWidth;
    private int mHeight;
    private int sum = 0;
    private int highlightedId = -1;
    private OnChartClickListener listener;

    final public static int DRAG = 1;
    final public static int ZOOM = 2;

    private final int CLICK = 5;

    public int mode = 0;

    private Matrix matrix = new Matrix();
    private Matrix matrix1 = new Matrix();
    private Matrix saveMatrix = new Matrix();

    private PointF mid = new PointF();

    private boolean flag = false;

    float initDis = 1f;

    private float x_down = 0;
    private float y_down = 0;
    float minScale = 1f;
    float maxScale = 500f;
    float lastScale = 1;
    float newScale = 1f;
    float newDis = 1f;

    float[] m = new float[9];
    float[] n = new float[9];
    float translateX;
    float translateY;
    float mScaleFactor;
    private List<RectF> rectFList = new ArrayList<>();


    public SpecialBarChart(Context context) {
        this(context, null);
    }

    public SpecialBarChart(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpecialBarChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(0f);
        setBackgroundColor(Color.GRAY);

    }

    public void setData(List<HashMap<String, Integer>> listData, List<Integer> paintColors) {
        this.listDegree.clear();
        this.paintColors.clear();
        sum = 0;
        for (HashMap<String, Integer> map : listData) {
            this.listDegree.add(map.get("high"));
            sum = sum + map.get("width");
            this.listRange.add(map.get("width"));
        }
        this.paintColors.addAll(paintColors);
//        postInvalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        if (listRange.size() > 0) {
        //画矩形图
        drawRectFView(canvas);
//        }

        this.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        canvas.concat(matrix);
        this.setLayerType(View.LAYER_TYPE_NONE, null);

    }


    //画矩形图
    private void drawRectFView(Canvas canvas) {
        rectFList.clear();
        double pointHeight = (double) mHeight / 100f;
        double moveHeight;
        double sumNew = 0;

        for (int i = 0; i < listDegree.size(); i++) {
            if (listDegree.get(i) == 1) {
                moveHeight = mHeight - 60 * pointHeight;
                mPaint.setColor(paintColors.get(0));
            } else if (listDegree.get(i) == 2) {
                moveHeight = mHeight - 75 * pointHeight;

                mPaint.setColor(paintColors.get(1));
            } else {
                moveHeight = mHeight - 90 * pointHeight;
                mPaint.setColor(paintColors.get(2));
            }
            if (highlightedId == i) {
                mPaint.setColor(Color.WHITE);
            }
            sumNew = sumNew + listRange.get(i);
            //下移的距离
            RectF oval = new RectF((float) ((sumNew - listRange.get(i)) * mWidth / sum), (float) moveHeight, (float) (sumNew * mWidth / sum), (float) mHeight);
            matrix.mapRect(oval);

            canvas.drawRect(oval, mPaint);
            rectFList.add(oval);
        }

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getWidth();
        mHeight = getHeight();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

//        PointF curr = new PointF(event.getX(), event.getY());

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                saveMatrix.set(matrix);
                x_down = event.getX();
                y_down = event.getY();
                // 初始为drag模式
                mode = DRAG;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                // 初始的两个触摸点间的距离
                initDis = spacingNew(event);
                if (initDis > 10f) {
                    saveMatrix.set(matrix);
                    // 设置为缩放模式
                    mode = ZOOM;
                    // 多点触摸的时候 计算出中间点的坐标
                    midPoint(mid, event);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                // drag模式
                if (mode == DRAG) {
                    translateX = event.getX() - x_down;
                    translateY = event.getY() - y_down;
                    if (translateX > 10f || translateX < -10f) {
                        matrix1.set(saveMatrix);
                        matrix1.postTranslate(translateX, 0);
                        // 设置matrix
                        matrix.set(check(matrix1));
                        // 调用ondraw重绘
                        postInvalidate();
                    }

                } else if (mode == ZOOM) {
                    newDis = spacingNew(event);
                    // 计算出缩放比例
                    mScaleFactor = newDis / initDis;

                    matrix1.set(saveMatrix);

                    // 以mid为中心进行缩放
                    matrix1.postScale(mScaleFactor, 1, mid.x, 0);
                    matrix.set(check(matrix1));
                    postInvalidate();

                }

                break;

            case MotionEvent.ACTION_UP:
                mode = 0;
                int xDiff = (int) Math.abs(event.getX() - x_down);
                int yDiff = (int) Math.abs(event.getY() - y_down);

                if (xDiff < CLICK && yDiff < CLICK) {
                    //获取点击坐标
                    float x = event.getX();
                    float y = event.getY();
                    //判断点击点的位置
                    double sumS = 0;
//                    for (int i = 0; i < listDegree.size(); i++) {
//                        sumS = sumS + listRange.get(i);
//                        if (x < (float) ((sumS - listRange.get(i)) * mWidth / sum) || x >= (float) (sumS * mWidth / sum))
//                            continue;
//                        highlightedId = i;
//                        invalidate();
//                        listener.onClick(i);
//                        break;
//                    }
                    for (int i = 0; i < rectFList.size(); i++) {
                        if (!rectFList.get(i).contains(x, y))
                            continue;
                        highlightedId = i;
                        invalidate();
                        listener.onClick(i);
                        break;
                    }
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                mode = 0;
                break;
            default:
                mode = 0;
                break;
        }
        return true;
    }


    public interface OnChartClickListener {
        void onClick(int num);
    }

    /**
     * 设置柱子点击监听的方法
     *
     * @param listener
     */
    public void setOnChartClickListener(OnChartClickListener listener) {
        this.listener = listener;
    }


    //取两点的距离
    private float spacing(MotionEvent event) {
        try {
            double x = event.getX(0) - event.getX(1);
            double y = event.getY(0) - event.getY(1);
            return (float) Math.sqrt(x * x + y * y);
        } catch (IllegalArgumentException ex) {
            Log.v("TAG", ex.getLocalizedMessage());
            return 0;
        }
    }

    //取两点的距离
    private float spacingNew(MotionEvent event) {
        try {
            float x = event.getX(0) - event.getX(1);
            return Math.abs(x);
        } catch (IllegalArgumentException ex) {
            Log.v("TAG", ex.getLocalizedMessage());
            return 0;
        }
    }


    //取两点的中点
    private void midPoint(PointF point, MotionEvent event) {
        try {
            float x = event.getX(0) + event.getX(1);
            float y = event.getY(0) + event.getY(1);
            point.set(x / 2, y / 2);
        } catch (IllegalArgumentException ex) {

            //这个异常是android自带的，网上清一色的这么说。。。。
            Log.v("TAG", ex.getLocalizedMessage());
        }
    }

    private Matrix check(Matrix matrix) {
        matrix.getValues(m);
//        if(m[Matrix.MSCALE_X]>5f){
//            m[Matrix.MSCALE_X] = 5f;
//        }else
        if (m[Matrix.MSCALE_X] < 1f) {
            m[Matrix.MSCALE_X] = 1f;
        }
        if (m[Matrix.MTRANS_X] > 0) {
            m[Matrix.MTRANS_X] = 0f;
        } else if (m[Matrix.MTRANS_X] < -mWidth * m[Matrix.MSCALE_X] + mWidth) {
            m[Matrix.MTRANS_X] = -mWidth * m[Matrix.MSCALE_X] + mWidth;
        }
        matrix.setValues(m);
        return matrix;
    }


}
