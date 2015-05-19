package example.turnablepageviewtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class TurnablePageView extends ImageView {
    private final int TURN_SPEED = 100;
    private Handler mHandler;
    private int mDegree;

    private enum State { INIT, RUNNING, TURNED}
    private State mState;
    private Bitmap mBmp;

    private final Runnable turnRight = new Runnable() {
        @Override
        public void run() {
            invalidate();
            mDegree += 10;
            if (mDegree > 180) {
                mDegree = 180;
                mState = State.TURNED;
            } else {
                mHandler.postDelayed(turnRight, TURN_SPEED);
            }
        }
    };

    private final Runnable turnLeft = new Runnable() {
        @Override
        public void run() {
            invalidate();
            mDegree -= 10;
            if (mDegree < 0) {
                mDegree = 0;
                mState = State.INIT;
            } else {
                mHandler.postDelayed(turnLeft, TURN_SPEED);
            }

        }
    };

    public TurnablePageView(Context context) {
        super(context);
        initView();
    }

    public TurnablePageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        mState = State.INIT;
        mHandler = new Handler();
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mState.equals(State.INIT)) {
                    setDrawingCacheEnabled(true);
                    mBmp = getDrawingCache();
                    mState = State.RUNNING;
                    mHandler.post(turnRight);
                } else if (mState.equals(State.TURNED)) {
                    mState = State.RUNNING;
                    mHandler.post(turnLeft);

                }
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mState.equals(State.INIT)) {
            super.onDraw(canvas);
            return;
        }

        Rect leftRect = new Rect(0, 0, canvas.getWidth()/2, canvas.getHeight());
        Rect rightRect = new Rect(canvas.getWidth()/2, 0, canvas.getWidth(), canvas.getHeight());
        Camera camera = new Camera();
        Matrix matrix = new Matrix();

        canvas.drawBitmap(mBmp, rightRect, rightRect, null);

        camera.save();
        //camera.setLocation(0, 0, -(getWidth()/100 + 8)); //カメラとの距離を調整
        if (mDegree < 90) {
            camera.rotate(0, mDegree, 0);
            camera.getMatrix(matrix);
            matrix.preTranslate(-getWidth() / 2, -getHeight() / 2);
            matrix.postTranslate(getWidth() / 2, getHeight() / 2);
            canvas.concat(matrix);
            canvas.drawBitmap(mBmp, leftRect, leftRect, null);
        } else {
            camera.rotate(0, mDegree - 180, 0);
            camera.getMatrix(matrix);
            matrix.preTranslate(-getWidth() / 2, -getHeight() / 2);
            matrix.postTranslate(getWidth() / 2, getHeight() / 2);
            canvas.concat(matrix);
            Paint paint = new Paint();
            paint.setColor(Color.parseColor("#de9122"));
            canvas.drawRect(rightRect, paint);
        }
        camera.restore();

    }
}
