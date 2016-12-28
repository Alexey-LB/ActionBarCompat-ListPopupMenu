package com.example.android.actionbarcompat.listpopupmenu;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * Created by lesa on 28.12.2016.
 */

public class Thermometer extends Drawable {
    static String TAG = "ThermDRAW";
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path mPath = new Path();

    @Override
    public void draw(Canvas canvas) {
        canvas.drawPath(mPath, mPaint);
    }


    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
    public void setColumn(int column) {
        hightColumn = column;
    }
    private  int hightColumn = 0;//   0 /10000

    private final int offsetLeftRight = 20;//   1/20
    private final int offsetGap = 5;//  5 dpi
    private void drawLine(int x,int endX,int y){
        if((x <= 0) || (y <= 0))return;
        mPath.moveTo(x, y);
        mPath.lineTo(endX, y);
        mPath.lineTo(endX, y -1);
        mPath.lineTo(x, y -1);
        mPath.lineTo(x, y);
    }
    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        Log.v(TAG,  "  w=" + bounds.width()  +"  h=" + bounds.height()
                +"  cX=" + bounds.centerX()  +"  cY=" + bounds.centerY()
                +"\n  l=" + bounds.left+"  r=" + bounds.right
                +"  t=" + bounds.top+"  b=" + bounds.bottom
        );
        int width = bounds.width();
        int height = bounds.height();
        int offset = width /offsetLeftRight;//смещение от края имеджа
        int step = height /offsetGap;//смещение от края имеджа
        int x,endX;
        mPath.reset();

        for(int y = 5, i = -2; y < height; y += offsetGap, i++){
            if((i % 10) == 0){
                x = offsetGap;
                mPaint.setColor(0xFFFF0000);
            }
            else{
                if((i % 5) == 0)x = offsetGap *2 + offsetGap / 2;
                else x = offsetGap *4;
            }
            endX = width - x;
            drawLine(x,endX,y);
        }

//        mPath.moveTo(0, height / 2);
//        mPath.lineTo(width / 4, 0);
//        mPath.lineTo(width * 3 / 4, 0);
//        mPath.lineTo(width, height / 2);
//        mPath.lineTo(width * 3 / 4, height);
//        mPath.lineTo(width / 4, height);
        mPath.close();
    }
}
