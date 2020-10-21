package com.lqcode.adjump.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;


import com.lqcode.adjump.R;
import com.lqcode.adjump.event.RemoveLayoutMessage;
import com.lqcode.adjump.frame.CacheTools;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class Kuang extends RelativeLayout {

    private List<Rect> rectList;
    private Context context;
    private static final String TAG = Kuang.class.getSimpleName();
    private Bitmap mBitmap = null;
    private Canvas mBitmapCanvas = null;
    private Paint mPaint = null;

    public Kuang(Context context, List<Rect> rectList) {
        super(context);
        this.context = context;
        this.rectList = rectList;
        init();
        setWillNotDraw(false);
    }

    public Kuang(Context context) {
        super(context);
        this.context = context;
        init();
        setWillNotDraw(false);
    }

    public Kuang(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
        setWillNotDraw(false);
    }

    public Kuang(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
        setWillNotDraw(false);
    }

    RelativeLayout relativeLayout;
    View layoutOK;
    View layoutExit;

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.choose, this);
        relativeLayout = findViewById(R.id.choose_layout);
        mBitmap = CacheTools.getInstance().getBitmap();
        mBitmapCanvas = new Canvas(mBitmap);

        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4f);

        relativeLayout.setOnTouchListener(new FloatingListener());

        findViewById(R.id.ok).setOnClickListener(v -> {
            Toast.makeText(context,
                    "lastChooseX==>" + lastChooseX + "----lastChooseY==>" + lastChooseY,
                    Toast.LENGTH_SHORT)
                    .show();
            EventBus.getDefault().post(new RemoveLayoutMessage());
        });

        findViewById(R.id.again).setOnClickListener(view -> {
            lastChooseX = 0;
            lastChooseY = 0;
            //
//            mBitmapCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//            mBitmapCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.MULTIPLY);
//            Paint clearPaint = new Paint();
//            clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//            clearPaint.setStyle(Paint.Style.FILL);
//            mBitmapCanvas.drawRect(lastRect, clearPaint);
//
//            invalidate();



            //

            layoutOK.setVisibility(GONE);
            layoutExit.setVisibility(VISIBLE);
            lastRect = null;
        });

        findViewById(R.id.exit).setOnClickListener(view -> EventBus.getDefault().post(new RemoveLayoutMessage()));

        for (Rect rect : rectList) {
            // 画TextView的4个边.
            mBitmapCanvas.drawRect(rect, mPaint);
        }

        layoutOK = findViewById(R.id.ok_layout);
        layoutExit = findViewById(R.id.exit_layout);


    }


    private float y = 0;

    private class FloatingListener implements View.OnTouchListener {


        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //获取点击的xy坐标
                    y = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    //根据手指移动来算出移动的xy坐标
                    relativeLayout.setTranslationY(v.getTranslationY() + event.getRawY() - y);
                    y = event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return true;
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw: ====>>>>");
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
    }


    private float lastChooseX = 0;
    private float lastChooseY = 0;
    private Rect lastRect;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent: ======>>>>>>>");

        int statusBarHeight1 = -1;
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = getResources().getDimensionPixelSize(resourceId);
        }
        if (event.getAction() == MotionEvent.ACTION_UP && lastChooseX == 0 && lastChooseY == 0)
            for (Rect rect : rectList) {
                if (event.getRawX() > rect.left &&
                        event.getRawX() < rect.right &&
                        event.getRawY() - statusBarHeight1 > rect.top &&
                        event.getRawY() - statusBarHeight1 < rect.bottom) {
                    lastRect = rect;
                    lastChooseX = event.getRawX();
                    lastChooseY = event.getRawY();
                    Paint paint = new Paint();
                    paint.setColor(Color.parseColor("#808bffc4"));
                    paint.setStyle(Paint.Style.FILL);
                    mBitmapCanvas.drawRect(lastRect, paint);
                    layoutOK.setVisibility(VISIBLE);
                    layoutExit.setVisibility(GONE);
                    break;
                }
            }
        return true;
    }

}
