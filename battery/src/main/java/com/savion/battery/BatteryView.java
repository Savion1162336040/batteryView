package com.savion.battery;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;

/**
 * @Author: savion
 * @Date: 2022/2/23 10:23
 * @Des:
 **/
public class BatteryView extends View {
    private int batteryFullColor = Color.GREEN;
    private int batteryEmptyColor = Color.WHITE;
    private int batteryFrameColor = Color.BLACK;
    private float electrodeXRatio = 0.1f;
    private float electrodeYRatio = 0.5f;
    /**
     * @author savion
     * @date 2022/2/23
     * @desc 描边宽度
     **/
    private float batteryFrameWidth = 5f;
    private Paint paint;
    private float corner = 10f;
    @IntRange(from = 0, to = 100)
    private int batteryPower = 0;
    //自动监听电池变化
    private boolean autoObserve = true;
    //是否在充电
    private boolean isCharge = false;
    private Drawable chargeDrawable;
    private BatteryBroadCast batteryBroadCast;
    private BatteryBroadCast.BatteryChangeCallBack batteryChangeCallBack;

    public BatteryView(Context context) {
        this(context, null);
    }

    public BatteryView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BatteryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray t = getContext().obtainStyledAttributes(attrs, R.styleable.BatteryView);
        chargeDrawable = t.getDrawable(R.styleable.BatteryView_battery_charge_drawable);
        batteryFrameColor = t.getColor(R.styleable.BatteryView_battery_frame_color, batteryFrameColor);
        batteryFrameWidth = t.getDimension(R.styleable.BatteryView_battery_frame_width, batteryFrameWidth);
        batteryFullColor = t.getColor(R.styleable.BatteryView_battery_full_color, batteryFullColor);
        batteryEmptyColor = t.getColor(R.styleable.BatteryView_battery_empty_color, batteryEmptyColor);
        batteryPower = t.getInt(R.styleable.BatteryView_battery_power, batteryPower);
        electrodeXRatio = t.getFloat(R.styleable.BatteryView_battery_electrode_width_ratio, electrodeXRatio);
        electrodeYRatio = t.getFloat(R.styleable.BatteryView_battery_electrode_height_ratio, electrodeYRatio);
        corner = t.getDimension(R.styleable.BatteryView_battery_corner, corner);
        isCharge = t.getBoolean(R.styleable.BatteryView_battery_is_charge, isCharge);
        autoObserve = t.getBoolean(R.styleable.BatteryView_battery_auto_observe, autoObserve);
        t.recycle();
        paint = new Paint();

        batteryBroadCast = new BatteryBroadCast();
        batteryBroadCast.setChangeCallBack((level, isCharge) -> {
            if (autoObserve) {
                updateAll(level, isCharge != null && isCharge.isCharging());
            }
            if (batteryChangeCallBack != null) {
                batteryChangeCallBack.onBatteryChange(level, isCharge);
            }
        });
        batteryBroadCast.registe(context);
    }

    public void setBatteryChangeCallBack(BatteryBroadCast.BatteryChangeCallBack batteryChangeCallBack) {
        this.batteryChangeCallBack = batteryChangeCallBack;
    }

    public void onDestory(Context context) {
        if (batteryBroadCast != null) {
            batteryBroadCast.unregiste(context);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.e("savion", "batteryview attachedToWindow");

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.e("savion", "batteryview detachedFromWindow");
        onDestory(getContext());
    }

    /**
     * @author savion
     * @date 2022/2/23
     * @desc 更新电量
     **/
    public void updatePower(@IntRange(from = 0, to = 100) int power) {
        int bp = (int) NumberUtil.between(power, 100, 0);
        if (bp != this.batteryPower) {
            this.batteryPower = bp;
            invalidate();
        }
    }

    /**
     * @author savion
     * @date 2022/2/23
     * @desc 更新充电状态
     **/
    public void updateIsCharge(boolean isCharge) {
        if (isCharge != this.isCharge) {
            this.isCharge = isCharge;
            invalidate();
        }
    }

    public void updateAll(int power, boolean isCharge) {
        int bp = (int) NumberUtil.between(power, 100, 0);
        if (bp != this.batteryPower
                || this.isCharge != isCharge) {
            this.batteryPower = bp;
            this.isCharge = isCharge;
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) (width * 0.5f);
        setMeasuredDimension(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制背景
        int batteryWidth = (int) (getWidth() * (1f - electrodeXRatio));
        int batteryHeight = getHeight();

        int batteryFullWidth = (int) (batteryWidth - batteryFrameWidth * 2f);
        int batteryFullHeight = (int) (batteryHeight - batteryFrameWidth * 2f);
        //绘制边框
        if (batteryFrameWidth > 0) {
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(batteryFrameWidth);
            paint.setColor(batteryFrameColor);
            canvas.drawRoundRect(0, 0, batteryWidth, getHeight(), corner, corner, paint);
        }

        //绘制有电空心背景
        if (batteryFrameWidth > 0) {
            paint.setColor(batteryEmptyColor);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRoundRect(batteryFrameWidth, batteryFrameWidth, batteryFrameWidth + batteryFullWidth, batteryFrameWidth + batteryFullHeight, corner, corner, paint);
        }

        //绘制有电部分
        if (batteryPower > 0) {
            paint.setColor(batteryFullColor);
            paint.setStyle(Paint.Style.FILL);

            float fullWidth = (batteryFullWidth) * (batteryPower / 100f);

            canvas.drawRoundRect(batteryFrameWidth, batteryFrameWidth, batteryFrameWidth + fullWidth, batteryFrameWidth + batteryFullHeight, corner, corner, paint);
        }

        //画充电标记
        if (isCharge && chargeDrawable != null) {
            float chargeShowRectL = batteryFrameWidth;
            float chargeShowRectT = batteryFrameWidth;
            float chargeShowRectR = chargeShowRectL + batteryFullWidth;
            float chargeShowRectB = chargeShowRectT + batteryFullHeight;

            float scaleRatio = Math.min((chargeShowRectR - chargeShowRectL) * 1f / chargeDrawable.getIntrinsicWidth(),
                    (chargeShowRectB - chargeShowRectT) * 1f / chargeDrawable.getIntrinsicHeight());
            float showWidth = chargeDrawable.getIntrinsicWidth() * scaleRatio;
            float showHeight = chargeDrawable.getIntrinsicHeight() * scaleRatio;

            chargeDrawable.setBounds((int) (chargeShowRectL + (chargeShowRectR - chargeShowRectL - showWidth) / 2f),
                    (int) (chargeShowRectT + (chargeShowRectB - chargeShowRectT - showHeight) / 2f),
                    (int) (chargeShowRectR - (chargeShowRectR - chargeShowRectL - showWidth) / 2f),
                    (int) (chargeShowRectB - (chargeShowRectB - chargeShowRectT - showHeight) / 2f));

            chargeDrawable.draw(canvas);
        }

        //绘制电极头
        if (electrodeXRatio > 0 && electrodeYRatio > 0) {
            paint.setColor(batteryFrameColor);
            paint.setStyle(Paint.Style.FILL);
            float height = electrodeYRatio * getHeight();
            canvas.drawRoundRect((batteryWidth - batteryFrameWidth),
                    getHeight() / 2f - height / 2f,
                    getWidth(),
                    getHeight() / 2f + height / 2f,
                    corner,
                    corner,
                    paint);
        }
    }
}
