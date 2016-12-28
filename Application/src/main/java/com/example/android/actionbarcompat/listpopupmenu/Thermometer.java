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
    //используем как эталон для остальных фигур
    private Paint mPaintEtalon = new Paint(Paint.ANTI_ALIAS_FLAG);

    @Override
    public void draw(Canvas canvas) {
        if((width <= 0) || (height <= 0))return;
        // рисуем фон градусника, шкалу
        drawThermometerFon(canvas);
        //рисуем столбик
        drawThermometerColumn(canvas);
    }

    @Override
    public void setAlpha(int alpha) {mPaintEtalon.setAlpha(alpha);}

    @Override
    public void setColorFilter(ColorFilter colorFilter) { mPaintEtalon.setColorFilter(colorFilter);}

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        Log.v(TAG,  "  w=" + bounds.width()  +"  h=" + bounds.height()
                +"  cX=" + bounds.centerX()  +"  cY=" + bounds.centerY()
                +"\n  l=" + bounds.left+"  r=" + bounds.right
                +"  t=" + bounds.top+"  b=" + bounds.bottom
        );
        // устнавливаем размеры для рисования
        width = bounds.width();
        height = bounds.height();
    }
    // высота столбика термометра
    public void setColumn(int column) {
        hightColumn = column;
    }
    private  int hightColumn = 0;//   0 /10000

    private final int offsetLeftRight = 20;//   1/20 смещенеие от левого и правого края имеджа
    private final int offsetGap = 5;//  5 dpi //через сколько дпи по У рисуем линии
    private final int columnWith = 10;//  10 dpi - ширина столбика термометра

    private  int width = 0;//  10 dpi
    private  int height = 0;//  10 dpi

    private void drawLine(int x,int endX,int y,int endY,Path mPath){
        if(x < 0) x = 0;
        if(y < 0)y = 0;
        if(x > width) x = width;
        if(y > height)y = height;
        mPath.moveTo(x, y);
        mPath.lineTo(endX, y);
        mPath.lineTo(endX, endY);
        mPath.lineTo(x, endY);
        mPath.lineTo(x, y);
    }
    private  void drawThermometerFon(Canvas canvas) {
        Paint paint =  new Paint(Paint.ANTI_ALIAS_FLAG);
        Path path = new Path();
        paint.setAlpha(mPaintEtalon.getAlpha());
        paint.setColorFilter(mPaintEtalon.getColorFilter());
        int offset = width /offsetLeftRight;//смещение от края имеджа
        int step = height /offsetGap;//смещение от края имеджа
        int x,endX;
        // path.reset();
        paint.setColor(0xFF000000);
        for(int y = offsetGap, i = -2; y < height; y += offsetGap, i++){
            if((i % 10) == 0)x = offsetGap;
            else{
                if((i % 5) == 0)x = offsetGap *2 + offsetGap / 2;
                else x = offsetGap *4;
            }
            endX = width - x;
            drawLine(x,endX,y, y-1,path);
        }
        path.close();
        canvas.drawPath(path, paint);
    }
    private void drawThermometerColumn(Canvas canvas) {
        Paint paint =  new Paint(Paint.ANTI_ALIAS_FLAG);
        Path path = new Path();
        paint.setAlpha(mPaintEtalon.getAlpha());
        paint.setColorFilter(mPaintEtalon.getColorFilter());
        //  path.reset();
        paint.setColor(0xFFA00000);
        int startX = (width - columnWith) /2;
        drawLine(startX,startX + columnWith,0,height,path);
        path.close();
        canvas.drawPath(path, paint);
        //
        paint =  new Paint(Paint.ANTI_ALIAS_FLAG);
        path = new Path();
        paint.setAlpha(mPaintEtalon.getAlpha());
        paint.setColorFilter(mPaintEtalon.getColorFilter());
        //  path.reset();
        paint.setColor(0xFFE08080);
        startX = (width - columnWith + 6) /2;
        drawLine(startX,startX + columnWith -6,0,height,path);
        path.close();
        canvas.drawPath(path, paint);
        //
        paint =  new Paint(Paint.ANTI_ALIAS_FLAG);
        path = new Path();
        paint.setAlpha(mPaintEtalon.getAlpha());
        paint.setColorFilter(mPaintEtalon.getColorFilter());
        //  path.reset();
        paint.setColor(0xFFFFC0C0);
        startX = (width - columnWith + 8) /2;
        drawLine(startX,startX + columnWith -8,0,height,path);
        path.close();
        canvas.drawPath(path, paint);

    }
}
