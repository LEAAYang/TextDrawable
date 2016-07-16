package cn.longmaster.lmkit.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RoundRectShape;

/**
 * Created by YangHao on 2016/7/16.
 * 可居中显示文字的Drawable
 */
public class TextDrawable extends ShapeDrawable {

    private String mText;
    private int mTextSize;
    private Paint mTextPaint;
    private Paint mBorderPaint;
    private RoundRectShape mBorderRectShape;
    private float mBorderWidth;

    public TextDrawable(Builder builder) {
        super();

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setFakeBoldText(builder.isBold);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(builder.textColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        if (builder.borderWidth > 0) {
            mBorderPaint = new Paint();
            mBorderPaint.setColor(builder.borderColor);
            mBorderPaint.setStyle(Paint.Style.STROKE);
            mBorderPaint.setStrokeWidth(builder.borderWidth);
            mBorderPaint.setAntiAlias(true);

            mBorderWidth = builder.borderWidth;
        }

        getPaint().setColor(builder.backgroundColor);
        if (builder.isRound) {
            setShape(new OvalShape());
        } else if (builder.isRoundRect) {
            setShape(new RoundRectShape(builder.roundRadius, null, null));
            if (builder.borderWidth > 0) {
                float[] innerRadius = new float[]{
                        builder.roundRadius[0] - builder.borderWidth,
                        builder.roundRadius[1] - builder.borderWidth,
                        builder.roundRadius[2] - builder.borderWidth,
                        builder.roundRadius[3] - builder.borderWidth,
                        builder.roundRadius[4] - builder.borderWidth,
                        builder.roundRadius[5] - builder.borderWidth,
                        builder.roundRadius[6] - builder.borderWidth,
                        builder.roundRadius[7] - builder.borderWidth,
                };
                mBorderRectShape = new RoundRectShape(builder.roundRadius, null, innerRadius);
            }
        }
        mText = builder.text;
        mTextSize = builder.textSize;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawBorder(canvas);

        Rect r = getBounds();
        int count = canvas.save();
        canvas.translate(r.left, r.top);
        drawText(canvas);
        canvas.restoreToCount(count);
    }

    private void drawBorder(Canvas canvas) {
        if (mBorderPaint == null) {
            return;
        }
        RectF rect = new RectF(getBounds());
        rect.inset(mBorderWidth, mBorderWidth);

        if (getShape() instanceof OvalShape) {
            canvas.drawOval(rect, mBorderPaint);
        } else if (mBorderRectShape != null) {
            mBorderRectShape.draw(canvas, mBorderPaint);
        } else {
            canvas.drawRect(rect, mBorderPaint);
        }
    }

    private void drawText(Canvas canvas) {
        canvas.drawText(mText, getWidth() / 2, getHeight() / 2 - ((mTextPaint.descent() + mTextPaint.ascent()) / 2), mTextPaint);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        int fontSize = this.mTextSize <= 0 ? (Math.min(getWidth(), getHeight()) / 2) : this.mTextSize;
        mTextPaint.setTextSize(fontSize);
        mBorderRectShape.resize(getWidth(), getHeight());
    }

    public int getWidth() {
        return getBounds().width();
    }

    public int getHeight() {
        return getBounds().height();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String text;
        private int textColor;
        private int textSize;
        private boolean isBold;
        private int backgroundColor;
        private boolean isRound;
        private boolean isRoundRect;
        private float[] roundRadius;
        private float borderWidth;
        private int borderColor;

        private Builder() {
            text = "";
            backgroundColor = Color.TRANSPARENT;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder textColor(int Color) {
            this.textColor = Color;
            return this;
        }

        public Builder textSize(int size) {
            this.textSize = size;
            return this;
        }

        public Builder bold() {
            isBold = true;
            return this;
        }

        public Builder background(int color) {
            this.backgroundColor = color;
            return this;
        }

        /**
         * 如果同时设置了round和roundRect，则会被roundRect覆盖
         */
        public Builder round() {
            isRound = true;
            return this;
        }

        /**
         * 如果同时设置了round和roundRect，则roundRect会覆盖round
         */
        public Builder roundRect(float lt, float rt, float rb, float lb) {
            isRoundRect = true;
            roundRadius = new float[]{lt, lt, rt, rt, rb, rb, lb, lb};
            return this;
        }

        /**
         * 如果同时设置了round和roundRect，则roundRect会覆盖round
         */
        public Builder roundRect(float radius) {
            isRoundRect = true;
            roundRadius = new float[]{radius, radius, radius, radius, radius, radius, radius, radius};
            return this;
        }

        public Builder withBorder(float borderWidth, int borderColor) {
            this.borderColor = borderColor;
            this.borderWidth = borderWidth;
            return this;
        }

        public TextDrawable build() {
            return new TextDrawable(this);
        }
    }
}
