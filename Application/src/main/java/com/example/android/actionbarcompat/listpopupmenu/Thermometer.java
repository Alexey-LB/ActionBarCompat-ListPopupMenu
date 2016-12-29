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
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path mPath = new Path();

    private  int hightColumn = 0;//
    //
    private final float density;
    private int offsetLeftRight = 20;//   1/20 смещенеие от левого и правого края имеджа
    private int offsetGap = 5;//  5 dpi //через сколько дпи по У рисуем линии
    private int columnWith = 10;//  10 dpi - ширина столбика термометра

    private final int offsetLeftRightFinal = 20;//   1/20 смещенеие от левого и правого края имеджа
    private final int offsetGapFinal = 5;//  5 dpi //через сколько дпи по У рисуем линии
    private final int columnWithFinal = 10;//  10 dpi - ширина столбика термометра
    private final float minTemperature;
    private final float maxTemperature;

    private  float k_Temperature;//коэффициент перехода от градусов к пикселям
    private  int minTemperaturePoint;//точка на градуснике которая соответствует minTemperature
    private  int maxTemperaturePoint;//точка на градуснике которая соответствует maxTemperature

    private  int width = 0;//  10 dpi
    private  int height = 0;//  10 dpi
    private  boolean chengSize = false;//  10 dpi

    Thermometer(float density_,float minTemperature_,float maxTemperature_){
        super();
        minTemperature = minTemperature_;
        maxTemperature = maxTemperature_;
        density = density_;
        if(density != 0) {
            offsetLeftRight = (int)(offsetLeftRightFinal * density);
            offsetGap = (int)(offsetGapFinal * density);
            columnWith = (int)(columnWithFinal * density);
        }
        // ДЛЯ размера меню И ТД, используется density !!!
       // density = getResources().getDisplayMetrics().density;
    }
    // высота столбика термометра
    public void setColumnTemperature(float temperature) {
        //входную температуру для отображения Вычитаем минимальное значение, домножаем на коэфициент
        // перехода к пикселам и добавляем смещение пикселов на экране
        hightColumn = (int)((temperature - minTemperature) * k_Temperature + minTemperaturePoint);
        invalidateSelf();
  //      Log.i(TAG," column= " + column +"  hightC= "+ hightColumn +" minT= " +minTemperature + " maxT= " +maxTemperature
  //              +"  k_T= " + k_Temperature + " minP= " +minTemperaturePoint + " maxP= " +maxTemperaturePoint);
    }


    @Override
    public void draw(Canvas canvas) {
        if((width <= 0) || (height <= 0))return;
        //
  //      Log.i(TAG, " --="+canvas.getHeight()+ " --="+canvas.getDensity());

//        if(density != 0) {
//            offsetLeftRight = offsetLeftRightFinal * density;
//            offsetGap = offsetGapFinal * density;
//            columnWith = columnWithFinal * density;
//        }
        //
        // рисуем фон градусника, шкалу, ЕСЛИ ИЗМЕНИЛИСЬ РАЗМЕРЫ!!
        if(chengSize){
            // 52dp = 44dp + 4dp * 2
            minTemperaturePoint = (int)(52 * density);//точка на градуснике которая соответствует minTemperature
            maxTemperaturePoint = height -(int)(52 * density);//точка на градуснике которая соответствует maxTemperature
            k_Temperature = (float)(maxTemperaturePoint - minTemperaturePoint)/(maxTemperature - minTemperature);
            drawThermometerFon(canvas);
        }

        //рисуем столбик
        drawThermometerColumn(canvas);
    }

    @Override
    public void setAlpha(int alpha) {mPaint.setAlpha(alpha);}

    @Override
    public void setColorFilter(ColorFilter colorFilter) { mPaint.setColorFilter(colorFilter);}

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
        if((width != bounds.width()) || (height != bounds.height()))chengSize = true;
        else chengSize = false;
        // устнавливаем размеры для рисования
        width = bounds.width();
        height = bounds.height();
    }
    private  void drawThermometerFon(Canvas canvas) {
   //     Path mPath = new Path();
        int offset = width /offsetLeftRight;//смещение от края имеджа
        int step = height /offsetGap;//смещение от края имеджа
        int x,endX;
        // mPath.reset();
        mPaint.setColor(0xFF000000);
        for(int y = offsetGap, i = (int)(-2 * density); y < height; y += offsetGap, i++){
            if((i % 10) == 0)x = offsetGap;
            else{
                if((i % 5) == 0)x = offsetGap *2 + offsetGap / 2;
                else x = offsetGap *4;
            }
            endX = width - x;
            drawLine(x,endX,y, y-(int)density, mPath);
        }
        drawCanvas(canvas, 0xFF000000, mPath);
    }
    //можно использовать один и тот же Path, НО обязательно path.reset(); при повторном использовании
    //Paint- хранит цвет кисть и т д -
    private void drawThermometerColumn(Canvas canvas) {
        //
        int level, startX;
        //       mPath = new Path();
        startX = (width - columnWith *2) /2;
        drawLine(startX,startX + columnWith *2,0, minTemperaturePoint, mPath);
        drawCanvas(canvas, 0xc000c0ff, mPath);
        //
        startX = (width - columnWith *2) /2;
        drawLine(startX,startX + columnWith *2, maxTemperaturePoint,height, mPath);
       // drawCanvas(canvas, 0xc0ffc0c0, mPath);
        drawCanvas(canvas, 0xc0ff0000, mPath);//80ffc0c0
        //
//        Log.i(TAG,"hightColumn= " + hightColumn);
  //      Path mPath = new Path();
        level = hightColumn;
//        //сначала стираем столбик
        startX = (width - columnWith) /2;
        drawLine(startX,startX + columnWith,0,height, mPath);
        drawCanvas(canvas, 0xffFFFFFF, mPath);


        //
 //       mPath = new Path();
        startX = (width - columnWith) /2;
        drawLine(startX,startX + columnWith,0,level, mPath);
        drawCanvas(canvas, 0xFFA00000, mPath);
        //
   //     mPath = new Path();
        startX = (width - columnWith + (int)(6 * density)) /2;
        drawLine(startX,startX + columnWith -(int)(6 * density),0,level, mPath);
        drawCanvas(canvas, 0xFFE08080, mPath);
        //
  //      mPath = new Path();
        startX = (width - columnWith + (int)(8 * density)) /2;
        drawLine(startX,startX + columnWith -(int)(8 * density),0,level, mPath);
        drawCanvas(canvas, 0xFFE08080, mPath );
    }
    //можно использовать один и тот же Path,
    // НО!! обязательно path.reset(); после canvas.drawPath()
    // иначе продолжает рисовать ОДНИМ И тем же цветом!!
    //Paint- хранит цвет кисть
    private  void drawCanvas(Canvas canvas, int color, Path mPath ) {
        mPath.close();//перед новым использованием обязательно закрываем
 //       Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        paint.setColorFilter(mPaint.getColorFilter());
//        paint.setAlpha(mPaint.getAlpha());
        //
//        paint.setColor(color);//новый цвет фигуры
//        canvas.drawPath(mPath, paint);//рисуем
        mPaint.setColor(color);//новый цвет фигуры
        canvas.drawPath(mPath, mPaint);//рисуем
        //можно использовать один и тот же Path,
        // НО!! обязательно path.reset(); после canvas.drawPath()
        mPath.reset();//сбрасываем запомненную фигуру(возможно просто отрисовывает)
    }
    private void drawLine(int x,int endX,int y,int endY, Path mPath ){
        //ПО УМОЛЧАНИЮ- 0 У, верхний левый УГОЛ экрана!
        // переворачиваем- ИДЕМ СНИЗУ вверх,
        y = height - y;
        endY = height - endY;
        if(x < 0) x = 0;
        if(y < 0)y = 0;
        if(x >= width) x = width -1;
        if(y >= height)y = height -1;

        mPath.moveTo(x, y);
        mPath.lineTo(endX, y);
        mPath.lineTo(endX, endY);
        mPath.lineTo(x, endY);
        mPath.lineTo(x, y);
    }
}
