package com.mirageStudios.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

public class NeonBackgroundLayout extends FrameLayout {

    // padding attributes (user inputs)
    // when padding has non-zero value other padding values get ignored.
    private float padding = 0;
    private float topPadding = 0;
    private float leftPadding = 0;
    private float bottomPadding = 0;
    private float rightPadding = 0;
    private float innerBackgroundPadding = 0;


    // geometrical user inputs (dp)
    private float cornerRadius = 6;
    private float strokeWidth = 4;
    private float shadowMultiplier = 2;

    // graphical user inputs
    private int strokeColor = 0xFFFFB300;
    private int shadowColor = 0xFFFFB300;
    private int innerBackgroundColor = 48000000;

    // computational variables (px)
    /**
     * background right padding (px)
     */
    private int brp;

    /**
     * background top padding (px)
     */
    private int btp;

    /**
     * background left padding (px)
     */
    private int blp;

    /**
     * background bottom padding (px)
     */
    private int bbp;

    /**
     * inner background padding (px)
     */
    private int ibp;

    /**
     * corner radius (px)
     */
    private int cr;

    /**
     * stroke width (px)
     */
    private int sw;

    /**
     * half stroke width (px)
     */
    private int hsw;

    /**
     * blur radius (equals stroke width * shadow multiplier) (px)
     */
    private int br;

    // runtime computational variables (based on view's width and height)

    /**
     * stroke box width (px)
     */
    private int sbw;

    /**
     * stroke box height (px)
     */
    private int sbh;

    /**
     * center ratio = 2cr / (sbh|sbw) (px)
     */
    private float centerRatio;

    /**
     * inner background corner radius (px)
     */
    private int ibcr;

    /**
     * circle center (px)
     */
    private Point cc;

    /**
     * top center (px)
     */
    private Point tc;

    /**
     * bottom center (px)
     */
    private Point bc;

    /**
     * right center (px)
     */
    private Point rc;

    /**
     * left center (px)
     */
    private Point lc;

    /**
     * right top center (px)
     */
    private Point rtc;

    /**
     * left top center (px)
     */
    private Point ltc;

    /**
     * right bottom center (px)
     */
    private Point rbc;

    /**
     * left bottom center (px)
     */
    private Point lbc;

    /**
     * inner background right top center (px)
     */
    private Point ibrtc;

    /**
     * inner background left top center (px)
     */
    private Point ibltc;

    /**
     * inner background right bottom center (px)
     */
    private Point ibrbc;

    /**
     * inner background left bottom center (px)
     */
    private Point iblbc;

    private final static int MODE_INVALID_SIZES = -1;
    private final static int MODE_CIRCLE = 0;
    private final static int MODE_HORIZONTAL_STRETCHED_CIRCLE = 1;
    private final static int MODE_VERTICAL_STRETCHED_CIRCLE = 2;
    private final static int MODE_RECTANGLE_WITH_ROUND_CORNERS = 3;
    @Mode
    private int mode = MODE_INVALID_SIZES;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        calculateRuntimeComputationalValues(getWidth(), getHeight());
    }

    public final static int STYLE_SHADOW_ONLY = 0b001;//1
    public final static int STYLE_STROKE_ONLY = 0b010;//2
    public final static int STYLE_STROKE_AND_SHADOW = STYLE_STROKE_ONLY | STYLE_SHADOW_ONLY;//3
    public final static int STYLE_INNER_BACKGROUND_ONLY = 0b100;//4
    public final static int STYLE_SHADOW_WITH_INNER_BACKGROUND = STYLE_SHADOW_ONLY | STYLE_INNER_BACKGROUND_ONLY;//5
    public final static int STYLE_STROKE_WITH_INNER_BACKGROUND = STYLE_STROKE_ONLY | STYLE_INNER_BACKGROUND_ONLY;//6
    public final static int STYLE_STROKE_WITH_INNER_BACKGROUND_AND_SHADOW = STYLE_STROKE_ONLY | STYLE_INNER_BACKGROUND_ONLY | STYLE_SHADOW_ONLY;//7

    @IntDef({
            STYLE_SHADOW_ONLY,
            STYLE_STROKE_ONLY,
            STYLE_STROKE_AND_SHADOW,
            STYLE_INNER_BACKGROUND_ONLY,
            STYLE_SHADOW_WITH_INNER_BACKGROUND,
            STYLE_STROKE_WITH_INNER_BACKGROUND,
            STYLE_STROKE_WITH_INNER_BACKGROUND_AND_SHADOW
    })
    private @interface Style {
    }

    @Style
    private int style = STYLE_STROKE_AND_SHADOW;

    private Paint bodyPaint;
    private Paint shadowPaint;
    private Paint innerBackgroundPaint;

    public NeonBackgroundLayout(Context context) {
        super(context);
        init();
    }

    public NeonBackgroundLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NeonBackgroundLayout(Context context, @Nullable AttributeSet attrs,
                                int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.NeonBackgroundLayout,
                defStyleAttr, 0);
        try {
            padding = a.getDimensionPixelSize(R.styleable.NeonBackgroundLayout_neon_padding, 0);
            topPadding = a.getDimensionPixelSize(R.styleable.NeonBackgroundLayout_neon_topPadding, 0);
            leftPadding = a.getDimensionPixelSize(R.styleable.NeonBackgroundLayout_neon_leftPadding, 0);
            bottomPadding = a.getDimensionPixelSize(R.styleable.NeonBackgroundLayout_neon_bottomPadding, 0);
            rightPadding = a.getDimensionPixelSize(R.styleable.NeonBackgroundLayout_neon_rightPadding, 0);
            innerBackgroundPadding = a.getDimensionPixelSize(R.styleable.NeonBackgroundLayout_neon_innerBackgroundPadding, 0);

            cornerRadius = a.getDimensionPixelSize(R.styleable.NeonBackgroundLayout_neon_cornerRadius, (int) dpToPx(6, getContext()));
            strokeWidth = a.getDimensionPixelSize(R.styleable.NeonBackgroundLayout_neon_strokeWidth, (int) dpToPx(4, getContext()));
            shadowMultiplier = a.getFloat(R.styleable.NeonBackgroundLayout_neon_shadowMultiplier, 2f);

            if (cornerRadius < 0) cornerRadius = 0;
            strokeColor = a.getColor(R.styleable.NeonBackgroundLayout_neon_strokeColor, 0xFFFFB300);
            shadowColor = a.getColor(R.styleable.NeonBackgroundLayout_neon_shadowColor, 0xFFFFB300);
            innerBackgroundColor = a.getColor(R.styleable.NeonBackgroundLayout_neon_innerBackgroundColor, 48000000);

            style = a.getInteger(R.styleable.NeonBackgroundLayout_neon_style, STYLE_STROKE_AND_SHADOW);
        } finally {
            a.recycle();
        }
        init();
    }

    private void init() {
        calculateComputationalValues();
        initBlurPainting();
        // disabling Hardware Acceleration for this view
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    private void calculateComputationalValues() {

        if (padding != 0) {
            topPadding = padding;
            leftPadding = padding;
            bottomPadding = padding;
            rightPadding = padding;
        }

        brp = (int) rightPadding;
        btp = (int) topPadding;
        blp = (int) leftPadding;
        bbp = (int) bottomPadding;
        ibp = (int) innerBackgroundPadding;

        if (ibp < 0) ibp = 0;

        cr = (int) cornerRadius;
        sw = (int) strokeWidth;
        hsw = (int) Math.floor(sw / 2f);
        br = (int) Math.ceil(sw * shadowMultiplier);
        if (cr == 0) {
            cr = 1;
        }
        if (cr > ibp) {
            ibcr = cr - ibp;
        } else {
            ibcr = 0;
        }
    }

    private void initBlurPainting() {
        bodyPaint = new Paint();
        bodyPaint.setAntiAlias(true);
        bodyPaint.setColor(strokeColor);
        bodyPaint.setStrokeWidth(sw);
        bodyPaint.setStyle(Paint.Style.STROKE);

        shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);
        shadowPaint.setColor(shadowColor);
        shadowPaint.setStrokeWidth(sw);
        shadowPaint.setStyle(Paint.Style.STROKE);
        shadowPaint.setMaskFilter(new BlurMaskFilter(br, BlurMaskFilter.Blur.NORMAL));

        innerBackgroundPaint = new Paint();
        innerBackgroundPaint.setAntiAlias(true);
        innerBackgroundPaint.setColor(innerBackgroundColor);
        innerBackgroundPaint.setStyle(Paint.Style.FILL);
    }

    private void calculateRuntimeComputationalValues(int w, int h) {
        if (blp + 2 * br + brp < w && btp + 2 * br + bbp < h) {
            sbw = w - blp - brp - 2 * br - sw;
            sbh = h - btp - bbp - 2 * br - sw;

            if (sbw > sbh) {
                if (2 * cr >= sbh) {
                    // horizontal stretched circle
                    mode = MODE_HORIZONTAL_STRETCHED_CIRCLE;
                    cr = Math.round(sbh / 2f);
                    lc = new Point(blp + br + cr + hsw, btp + br + cr + hsw);
                    rc = new Point(w - brp - br - cr - hsw, btp + br + cr + hsw);
                    ibcr = cr - hsw - ibp;
                } else {
                    // rectangle with rounded corners
                    mode = MODE_RECTANGLE_WITH_ROUND_CORNERS;
                    centerRatio = 2 * cr / (float) sbh;
                    ibcr = Math.round((sbh / 2f - hsw - ibp) * centerRatio);
                    calculateBoxValues(w, h);
                }
            } else if (sbw == sbh) {
                if (2 * cr >= sbw) {
                    // circle
                    mode = MODE_CIRCLE;
                    cr = Math.round(sbw / 2f);
                    cc = new Point(blp + br + cr + hsw, btp + br + cr + hsw);
                    ibcr = cr - hsw - ibp;
                } else {
                    // square with rounded corners
                    mode = MODE_RECTANGLE_WITH_ROUND_CORNERS;
                    centerRatio = 2 * cr / (float) sbw;
                    ibcr = Math.round((sbw / 2f - hsw - ibp) * centerRatio);
                    calculateBoxValues(w, h);
                }
            } else { // sbw < sbh
                if (2 * cr >= sbw) {
                    // vertical stretched circle
                    mode = MODE_VERTICAL_STRETCHED_CIRCLE;
                    cr = Math.round(sbw / 2f);
                    tc = new Point(blp + br + cr + hsw, btp + br + cr + hsw);
                    bc = new Point(blp + br + cr + hsw, h - bbp - br - cr - hsw);
                    ibcr = cr - hsw - ibp;
                } else {
                    // rectangle with rounded corners
                    mode = MODE_RECTANGLE_WITH_ROUND_CORNERS;
                    centerRatio = 2 * cr / (float) sbw;
                    ibcr = Math.round((sbw / 2f - hsw - ibp) * centerRatio);
                    calculateBoxValues(w, h);
                }
            }

            if (ibcr < 0) ibcr = 0;
        } else {
            mode = MODE_INVALID_SIZES;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calculateRuntimeComputationalValues(w, h);
    }

    @IntDef({MODE_INVALID_SIZES, MODE_CIRCLE, MODE_HORIZONTAL_STRETCHED_CIRCLE, MODE_VERTICAL_STRETCHED_CIRCLE, MODE_RECTANGLE_WITH_ROUND_CORNERS})
    private @interface Mode {
    }

    private void calculateBoxValues(int w, int h) {
        ltc = new Point(blp + br + cr + hsw, btp + br + cr + hsw);
        rtc = new Point(w - brp - br - cr - hsw, btp + br + cr + hsw);
        lbc = new Point(blp + br + cr + hsw, h - bbp - br - cr - hsw);
        rbc = new Point(w - brp - br - cr - hsw, h - bbp - br - cr - hsw);

        ibltc = new Point(blp + br + hsw + ibp + ibcr, btp + br + hsw + ibp + ibcr);
        ibrtc = new Point(w - brp - br - hsw - ibp - ibcr, btp + br + hsw + ibp + ibcr);
        iblbc = new Point(blp + br + hsw + ibp + ibcr, h - bbp - br - hsw - ibp - ibcr);
        ibrbc = new Point(w - brp - br - hsw - ibp - ibcr, h - bbp - br - hsw - ibp - ibcr);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        switch (mode) {
            case MODE_CIRCLE:
                drawCircle(canvas);
                break;
            case MODE_HORIZONTAL_STRETCHED_CIRCLE:
                drawHorizontalStretchedCircle(canvas);
                break;
            case MODE_VERTICAL_STRETCHED_CIRCLE:
                drawVerticalStretchedCircle(canvas);
                break;
            case MODE_RECTANGLE_WITH_ROUND_CORNERS:
                drawRectangleWithRoundCorners(canvas);
                break;
        }
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // this is a ViewGroup. don't draw here. draw in dispatchDraw method
    }

    private void drawCircle(Canvas canvas) {
        if ((style & STYLE_SHADOW_ONLY) == STYLE_SHADOW_ONLY) {
            canvas.drawCircle(cc.x, cc.y, cr, shadowPaint);
        }
        if ((style & STYLE_INNER_BACKGROUND_ONLY) == STYLE_INNER_BACKGROUND_ONLY) {
            canvas.drawCircle(cc.x, cc.y, ibcr, innerBackgroundPaint);
        }
        if ((style & STYLE_STROKE_ONLY) == STYLE_STROKE_ONLY) {
            canvas.drawCircle(cc.x, cc.y, cr, bodyPaint);
        }
    }

    private void drawHorizontalStretchedCircle(Canvas canvas) {
        if ((style & STYLE_SHADOW_ONLY) == STYLE_SHADOW_ONLY) {
            // top line
            canvas.drawLine(lc.x, lc.y - cr, rc.x, rc.y - cr, shadowPaint);
            // bottom line
            canvas.drawLine(lc.x, lc.y + cr, rc.x, rc.y + cr, shadowPaint);
            // left half circle
            canvas.drawArc(new RectF(lc.x - cr, lc.y - cr, lc.x + cr, lc.y + cr),
                    90, 180, false, shadowPaint);
            // right half circle
            canvas.drawArc(new RectF(rc.x - cr, rc.y - cr, rc.x + cr, rc.y + cr),
                    270, 180, false, shadowPaint);
        }
        if ((style & STYLE_INNER_BACKGROUND_ONLY) == STYLE_INNER_BACKGROUND_ONLY) {
            canvas.drawRect(new RectF(lc.x, lc.y - ibcr, rc.x, rc.y + ibcr), innerBackgroundPaint);
            // left half circle
            canvas.drawArc(new RectF(lc.x - ibcr, lc.y - ibcr, lc.x + ibcr, lc.y + ibcr),
                    90, 180, true, innerBackgroundPaint);
            // right half circle
            canvas.drawArc(new RectF(rc.x - ibcr, rc.y - ibcr, rc.x + ibcr, rc.y + ibcr),
                    270, 180, true, innerBackgroundPaint);
        }
        if ((style & STYLE_STROKE_ONLY) == STYLE_STROKE_ONLY) {
            // top line
            canvas.drawLine(lc.x, lc.y - cr, rc.x, rc.y - cr, bodyPaint);
            // bottom line
            canvas.drawLine(lc.x, lc.y + cr, rc.x, rc.y + cr, bodyPaint);
            // left half circle
            canvas.drawArc(new RectF(lc.x - cr, lc.y - cr, lc.x + cr, lc.y + cr),
                    90, 180, false, bodyPaint);
            // right half circle
            canvas.drawArc(new RectF(rc.x - cr, rc.y - cr, rc.x + cr, rc.y + cr),
                    270, 180, false, bodyPaint);
        }
    }

    private void drawVerticalStretchedCircle(Canvas canvas) {
        if ((style & STYLE_SHADOW_ONLY) == STYLE_SHADOW_ONLY) {
            // left line
            canvas.drawLine(tc.x - cr, tc.y, bc.x - cr, bc.y, shadowPaint);
            // right line
            canvas.drawLine(tc.x + cr, tc.y, bc.x + cr, bc.y, shadowPaint);
            // top half circle
            canvas.drawArc(new RectF(tc.x - cr, tc.y - cr, tc.x + cr, tc.y + cr),
                    180, 180, false, shadowPaint);
            // bottom half circle
            canvas.drawArc(new RectF(bc.x - cr, bc.y - cr, bc.x + cr, bc.y + cr),
                    0, 180, false, shadowPaint);
        }
        if ((style & STYLE_INNER_BACKGROUND_ONLY) == STYLE_INNER_BACKGROUND_ONLY) {
            canvas.drawRect(new RectF(tc.x - ibcr, tc.y, bc.x + ibcr, bc.y), innerBackgroundPaint);
            // top half circle
            canvas.drawArc(new RectF(tc.x - ibcr, tc.y - ibcr, tc.x + ibcr, tc.y + ibcr),
                    180, 180, true, innerBackgroundPaint);
            // bottom half circle
            canvas.drawArc(new RectF(bc.x - ibcr, bc.y - ibcr, bc.x + ibcr, bc.y + ibcr),
                    0, 180, true, innerBackgroundPaint);
        }
        if ((style & STYLE_STROKE_ONLY) == STYLE_STROKE_ONLY) {
            // left line
            canvas.drawLine(tc.x - cr, tc.y, bc.x - cr, bc.y, bodyPaint);
            // right line
            canvas.drawLine(tc.x + cr, tc.y, bc.x + cr, bc.y, bodyPaint);
            // top half circle
            canvas.drawArc(new RectF(tc.x - cr, tc.y - cr, tc.x + cr, tc.y + cr),
                    180, 180, false, bodyPaint);
            // bottom half circle
            canvas.drawArc(new RectF(bc.x - cr, bc.y - cr, bc.x + cr, bc.y + cr),
                    0, 180, false, bodyPaint);
        }
    }

    private void drawRectangleWithRoundCorners(Canvas canvas) {
        if ((style & STYLE_SHADOW_ONLY) == STYLE_SHADOW_ONLY) {
            // left top corner
            canvas.drawArc(new RectF(ltc.x - cr, ltc.y - cr, ltc.x + cr, ltc.y + cr),
                    180, 90, false, shadowPaint);
            // left bottom corner
            canvas.drawArc(new RectF(lbc.x - cr, lbc.y - cr, lbc.x + cr, lbc.y + cr),
                    90, 90, false, shadowPaint);
            // right bottom corner
            canvas.drawArc(new RectF(rbc.x - cr, rbc.y - cr, rbc.x + cr, rbc.y + cr),
                    0, 90, false, shadowPaint);
            // right top corner
            canvas.drawArc(new RectF(rtc.x - cr, rtc.y - cr, rtc.x + cr, rtc.y + cr),
                    270, 90, false, shadowPaint);
            // left line
            canvas.drawLine(ltc.x - cr, ltc.y, lbc.x - cr, lbc.y, shadowPaint);
            // right line
            canvas.drawLine(rtc.x + cr, rtc.y, rbc.x + cr, rbc.y, shadowPaint);
            // top line
            canvas.drawLine(ltc.x, ltc.y - cr, rtc.x, rtc.y - cr, shadowPaint);
            // bottom line
            canvas.drawLine(lbc.x, lbc.y + cr, rbc.x, rbc.y + cr, shadowPaint);
        }
        if ((style & STYLE_INNER_BACKGROUND_ONLY) == STYLE_INNER_BACKGROUND_ONLY) {
            canvas.drawArc(new RectF(ibrbc.x - ibcr, ibrbc.y - ibcr, ibrbc.x + ibcr, ibrbc.y + ibcr), 0, 90, true, innerBackgroundPaint);
            canvas.drawArc(new RectF(iblbc.x - ibcr, iblbc.y - ibcr, iblbc.x + ibcr, iblbc.y + ibcr), 90, 90, true, innerBackgroundPaint);
            canvas.drawArc(new RectF(ibltc.x - ibcr, ibltc.y - ibcr, ibltc.x + ibcr, ibltc.y + ibcr), 180, 90, true, innerBackgroundPaint);
            canvas.drawArc(new RectF(ibrtc.x - ibcr, ibrtc.y - ibcr, ibrtc.x + ibcr, ibrtc.y + ibcr), 270, 90, true, innerBackgroundPaint);

            canvas.drawRect(new RectF(ibltc.x, ibltc.y - ibcr, ibrtc.x, ibrtc.y), innerBackgroundPaint);
            canvas.drawRect(new RectF(ibltc.x - ibcr, ibltc.y, ibrbc.x + ibcr, ibrbc.y), innerBackgroundPaint);
            canvas.drawRect(new RectF(iblbc.x, iblbc.y, ibrbc.x, ibrbc.y + ibcr), innerBackgroundPaint);
        }
        if ((style & STYLE_STROKE_ONLY) == STYLE_STROKE_ONLY) {
            // left top corner
            canvas.drawArc(new RectF(ltc.x - cr, ltc.y - cr, ltc.x + cr, ltc.y + cr),
                    180, 90, false, bodyPaint);
            // left bottom corner
            canvas.drawArc(new RectF(lbc.x - cr, lbc.y - cr, lbc.x + cr, lbc.y + cr),
                    90, 90, false, bodyPaint);
            // right bottom corner
            canvas.drawArc(new RectF(rbc.x - cr, rbc.y - cr, rbc.x + cr, rbc.y + cr),
                    0, 90, false, bodyPaint);
            // right top corner
            canvas.drawArc(new RectF(rtc.x - cr, rtc.y - cr, rtc.x + cr, rtc.y + cr),
                    270, 90, false, bodyPaint);
            // left line
            canvas.drawLine(ltc.x - cr, ltc.y, lbc.x - cr, lbc.y, bodyPaint);
            // right line
            canvas.drawLine(rtc.x + cr, rtc.y, rbc.x + cr, rbc.y, bodyPaint);
            // top line
            canvas.drawLine(ltc.x, ltc.y - cr, rtc.x, rtc.y - cr, bodyPaint);
            // bottom line
            canvas.drawLine(lbc.x, lbc.y + cr, rbc.x, rbc.y + cr, bodyPaint);
        }
    }

    private static float dpToPx(float valueDp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueDp,
                displayMetrics);
    }

    /**
     * @param padding the general background padding on all 4 sides. (Will override other paddings if the value is non zero) (px)
     */
    public void setBackgroundPadding(float padding) {
        this.padding = padding;
        init();
        calculateBoxValues(getWidth(), getHeight());
        invalidate();
        requestLayout();
    }

    /**
     * @param leftPadding   left background padding (px)
     * @param topPadding    top background padding (px)
     * @param rightPadding  right background padding (px)
     * @param bottomPadding bottom background padding (px)
     */
    public void setBackgroundPadding(float leftPadding, float topPadding, float rightPadding, float bottomPadding) {
        this.leftPadding = leftPadding;
        this.topPadding = topPadding;
        this.rightPadding = rightPadding;
        this.bottomPadding = bottomPadding;
        init();
        calculateBoxValues(getWidth(), getHeight());
        invalidate();
        requestLayout();
    }

    /**
     * @param innerBackgroundPadding value of the padding between stroke and inner background (px)
     */
    public void setInnerBackgroundPadding(float innerBackgroundPadding) {
        this.innerBackgroundPadding = innerBackgroundPadding;
        init();
        calculateBoxValues(getWidth(), getHeight());
        invalidate();
        requestLayout();
    }

    /**
     * @param cornerRadius the background's corner radius (px)
     */
    public void setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;
        init();
        calculateBoxValues(getWidth(), getHeight());
        invalidate();
        requestLayout();
    }

    /**
     * @param strokeWidth width of the stroke and the shadow bodies (px)
     */
    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        init();
        calculateBoxValues(getWidth(), getHeight());
        invalidate();
        requestLayout();
    }

    /**
     * @param shadowMultiplier shadow body's multiplier value. used in calculating blur radius. blur radius = stroke width * shadow multiplier
     */
    public void setShadowMultiplier(float shadowMultiplier) {
        this.shadowMultiplier = shadowMultiplier;
        init();
        calculateBoxValues(getWidth(), getHeight());
        invalidate();
        requestLayout();
    }

    /**
     * @param strokeColor integer value of the stroke body's color (#AARRGGBB)
     */
    public void setStrokeColor(@ColorInt int strokeColor) {
        this.strokeColor = strokeColor;
        init();
        calculateBoxValues(getWidth(), getHeight());
        invalidate();
        requestLayout();
    }

    /**
     * @param shadowColor integer value of the shadow body's color (#AARRGGBB)
     */
    public void setShadowColor(@ColorInt int shadowColor) {
        this.shadowColor = shadowColor;
        init();
        calculateBoxValues(getWidth(), getHeight());
        invalidate();
        requestLayout();
    }

    /**
     * @param innerBackgroundColor integer value of the inner background body's color (#AARRGGBB)
     */
    public void setInnerBackgroundColor(@ColorInt int innerBackgroundColor) {
        this.innerBackgroundColor = innerBackgroundColor;
        init();
        calculateBoxValues(getWidth(), getHeight());
        invalidate();
        requestLayout();
    }
}
