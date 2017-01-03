package com.portfolio.alexey.connector;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.portfolio.alexey.connector.Util;

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

    private final float textSizeFinal = 18f;
    private float textSize = textSizeFinal;

    private final int offsetLeftRightFinal = 20;//   1/20 смещенеие от левого и правого края имеджа
    private final int offsetGapFinal = 5;//  5 dpi //через сколько дпи по У рисуем линии
    private final int offsetXFinal = 5;// смещение от края по Х в др
    private int offsetX = offsetXFinal;// смещение от края по Х в
    private final int columnWithFinal = 10;//  10 dpi - ширина столбика термометра
    private  float minTemperature;
    private float maxTemperature;

    public boolean onFahrenheit = false;

    private  float k_Temperature;//коэффициент перехода от градусов к пикселям
    private  int minTemperaturePoint;//точка на градуснике которая соответствует minTemperature
    private  int maxTemperaturePoint;//точка на градуснике которая соответствует maxTemperature

    private  int width = 0;//  10 dpi,
    private  int height = 0;//  10 dpi
    private  boolean chengSize = false;//  10 dpi

    private  float mRangeTemperature = 0;
    private  float[] stepNet = {0.1f,0.25f,0.5f,1f,2.5f,5f,10f,25f,50f,100f};
    private  int[] stepDp = {offsetGapFinal,offsetGapFinal +1,offsetGapFinal +2,
            offsetGapFinal +3,offsetGapFinal +4,offsetGapFinal +5,
            offsetGapFinal +6,offsetGapFinal +7};
    private final float minRange = 3;//C
    private float mstep;// в градусах на деление
//    offsetGap = 5;//  5 dpi //через сколько дпи по У рисуем линии
    //--
    private void calckStep(){
        int i,j;float rangeWidth, y,k = 0;
        //------ расчет в пикселях--------------
        for(j = 0;j < stepDp.length;j++) {

            rangeWidth = height / (stepDp[j] * density);//реальное количество делений на градуснике
            y = mRangeTemperature / rangeWidth;//ищем ЦЕНУ минимального деления
            for (i = 0; i < stepNet.length; i++) if (stepNet[i] > y) break;
            //контроль диапазона
            if(i >= stepNet.length) i--;
            y = (mRangeTemperature / stepNet[i]) /  rangeWidth;//получили коэфициент использования шкалы
            // чем ближе он к 1 тем полнее заполняет шкалу
            if(y > k) {
                k = y;
                mstep = stepNet[i];//сколько градусов на деление!! mstep * rangeWidth = ДИАПАЗОН выода температуры
                offsetGap = (int)(stepDp[j] * density);

                Log.w(TAG, " net= " + (int)rangeWidth+"  step= " + stepNet[i] + "  stepDp= " + stepDp[j] + "   count=" + (int)(mRangeTemperature / stepNet[i])
                        + "   RangeTemp=" + (int)mRangeTemperature+"   mRangeTempALL= " +(int)(stepNet[i] *rangeWidth));
            } else{
                Log.i(TAG, " net= " + (int)rangeWidth+"  step= " + stepNet[i] + "  stepDp= " + stepDp[j] + "   count=" + (int)(mRangeTemperature / stepNet[i])
                        + "   RangeTemp=" + (int)mRangeTemperature+"   mRangeTempALL= " +(int)(stepNet[i] *rangeWidth));
            }

        }
    }
    private void calckFon(){
        float y, rangeWidth;int i;
        //контроль входных данных
        mRangeTemperature = (maxTemperature - minTemperature) *1.2f;
        //--расчет  начала
        //---контроль минимума диапазона --
        y = minRange;
        if(onFahrenheit) y = minRange * 2;
        if (y > mRangeTemperature) mRangeTemperature = y;
        calckStep();
        rangeWidth = height / (offsetGap * density);
        Log.v(TAG, " net= " + (int)rangeWidth+"  step= " + mstep  + "   count=" + (int)(mRangeTemperature / mstep)
               + "   RangeTemp=" + (int)mRangeTemperature+"   mRangeTempALL= " +(int)(mstep *rangeWidth));
    }
    private  float[] testMin = {-20,-23.23f,-25.5f,-30.76f,-30f,  0f, -10f, -50f};
    private  float[] testMax = {-22, -3.5f  , 10.75f, 30.1f,70.5f,5f,15f,25f};
    private void testFon(){
        calckFon();
        for(int i = 0;i <testMin.length; i++){
            minTemperature = testMin[i];
            maxTemperature = testMax[i];
            calckFon();
        }
    }
    public Thermometer(float density_,float minTemperature_,float maxTemperature_, boolean fahrenheit){
        super();
        onFahrenheit = fahrenheit;
        if(fahrenheit){
            minTemperature = Util.getFahrenheit(minTemperature_);
            maxTemperature = Util.getFahrenheit(maxTemperature_);
        }else {
            minTemperature = minTemperature_;
            maxTemperature = maxTemperature_;
        }
        //контроль входных данных
        if(minTemperature > maxTemperature)  {
            float y = maxTemperature;
            maxTemperature = minTemperature;
            minTemperature = y;
        }
        density = density_;
        if(density != 0) {
            offsetLeftRight = (int)(offsetLeftRightFinal * density);
            offsetGap = (int)(offsetGapFinal * density);
            columnWith = (int)(columnWithFinal * density);
            offsetX = (int)(offsetXFinal * density);
            textSize = (textSizeFinal * density);
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
                +"  l=" + bounds.left+"  r=" + bounds.right
                +"  t=" + bounds.top+"  b=" + bounds.bottom
        );
        if((width != bounds.width()) || (height != bounds.height()))chengSize = true;
        else chengSize = false;
        // устнавливаем размеры для рисования
        width = bounds.width();
        height = bounds.height();
        //
        testFon();
    }
    private  void drawThermometerFon(Canvas canvas) {
        //MIN
        Path path;int x,endX;
        int colWith = columnWith *4;
        int startX = (width - colWith) /2;
        drawLine(startX,startX + colWith,0, minTemperaturePoint, mPath);
        drawCanvas(canvas, 0xc000c0ff, mPath,  Paint.Style.FILL);//Paint.Style.STROKE
        //MAX
        startX = (width - colWith) /2;
        drawLine(startX,startX + colWith, maxTemperaturePoint,height, mPath);
        drawCanvas(canvas, 0xc0ff0000, mPath, Paint.Style.FILL);//80ffc0c0 Paint.Style.STROKE

        //-----------

        // mPath.reset();
        mPaint.setColor(0xFF000000);
        mPaint.setTextSize(textSize);
        for(int y = offsetGap, i = -2; y < height; y += offsetGap, i++){
            if((i % 10) == 0){
                x = offsetX;
                path = new Path();
            }else{
                path = null;
                if((i % 5) == 0)x = offsetX *2 + offsetX / 2;
                else x = offsetX *4;

            }
            endX = width - x;
            mPath.addRect(getXpixel(x),getYpixel(y),getXpixel(endX),getYpixel(y-(int)density),Path.Direction.CW);
            if(path != null){
                path.moveTo(getXpixel(x), getYpixel(y));
                path.lineTo(getXpixel(endX), getYpixel(y));
                int st = (int)(-mstep * i);
                int stM = Math.abs(st % 10);
                st = (st % 100) / 10;
                canvas.drawTextOnPath(Integer.toString(st), path,(st < 0)?0:offsetX/2 ,  -textSize*1/10 , mPaint);
                canvas.drawTextOnPath(Integer.toString(stM), path, endX - textSize , -textSize*1/10, mPaint);
            }
        }
        drawCanvas(canvas, 0xFF000000, mPath, Paint.Style.FILL);//Paint.Style.STROKE--почемуто хреново рисует!

    }
    //можно использовать один и тот же Path, НО обязательно path.reset(); при повторном использовании
    //Paint- хранит цвет кисть и т д -
    private void drawThermometerColumn(Canvas canvas) {
        //
        int startX, level = hightColumn;

//        //сначала стираем столбик
        startX = (width - columnWith) /2;
        drawLine(startX,startX + columnWith,0,height, mPath);
        drawCanvas(canvas, 0xffFFFFFF, mPath,   Paint.Style.FILL);//Paint.Style.STROKE

 //       mPath = new Path();
        startX = (width - columnWith) /2;
        drawLine(startX,startX + columnWith,0,level, mPath);
        drawCanvas(canvas, 0xFF404040, mPath,   Paint.Style.FILL);//Paint.Style.STROKE
    //    drawCanvas(canvas, 0xFFA00000, mPath);
        //
   //     mPath = new Path();
        startX = (width - columnWith + (int)(6 * density)) /2;
        drawLine(startX,startX + columnWith -(int)(6 * density),0,level, mPath);
        drawCanvas(canvas, 0xFF808080, mPath,   Paint.Style.FILL);//Paint.Style.STROKE
   //     drawCanvas(canvas, 0xFFE08080, mPath);//
        //
  //      mPath = new Path();
        startX = (width - columnWith + (int)(8 * density)) /2;
        drawLine(startX,startX + columnWith -(int)(8 * density),0,level, mPath);
        drawCanvas(canvas, 0xFFc0c0c0, mPath ,   Paint.Style.FILL);//Paint.Style.STROKE
   //     drawCanvas(canvas, 0xFFE08080, mPath );
        startX = (width - columnWith + (int)(9 * density)) /2;
        drawLine(startX,startX + columnWith -(int)(9 * density),0,level, mPath);
        drawCanvas(canvas, 0xFFffffff, mPath,   Paint.Style.FILL);//Paint.Style.STROKE
    }
    //можно использовать один и тот же Path,
    // НО!! обязательно path.reset(); после canvas.drawPath()
    // иначе продолжает рисовать ОДНИМ И тем же цветом!!
    //Paint- хранит цвет кисть
    private  void drawCanvas(Canvas canvas, int color, Path mPath , Paint.Style style) {
        mPath.close();//перед новым использованием обязательно закрываем
 //       Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        paint.setColorFilter(mPaint.getColorFilter());
//        paint.setAlpha(mPaint.getAlpha());
        //
//        paint.setColor(color);//новый цвет фигуры
//        canvas.drawPath(mPath, paint);//рисуем
        mPaint.setColor(color);//новый цвет фигуры
        mPaint.setStrokeWidth(density);
         mPaint.setStyle(style);//Paint.Style.STROKE
        canvas.drawPath(mPath, mPaint);//рисуем
        //можно использовать один и тот же Path,
        // НО!! обязательно path.reset(); после canvas.drawPath()
        mPath.reset();//сбрасываем запомненную фигуру(возможно просто отрисовывает)
    }
    private int getYpixel(int y){
        int maxHeightYpixel = height -1;
    //    y = y * (int)density;
        if(y < 0)y = 0;
        if(y > maxHeightYpixel) y = maxHeightYpixel;
        ////ПО УМОЛЧАНИЮ- 0 У, верхний левый УГОЛ экрана!
        // переворачиваем- ИДЕМ СНИЗУ вверх,
        return maxHeightYpixel - y;
    }
    private int getXpixel(int x){
        int maxwidthXpixel = width - 1;
     //   x = x * (int)density;
        if(x < 0) x = 0;
        if(x >  maxwidthXpixel) x = maxwidthXpixel;
        return x;
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
        //
        if(endX < 0) endX = 0;
        if(endY < 0)endY = 0;
        if(endX >= width) endX = width -1;
        if(endY >= height)endY = height -1;
        if((x == endX) && (y == endY)) return;//это точка ее не рисует,
        mPath.moveTo(x, y);
        mPath.lineTo(endX, y);
        mPath.lineTo(endX, endY);
        mPath.lineTo(x, endY);
        mPath.lineTo(x, y);
       // Log.v(TAG,"x= "+x +"  endX= " + endX +"  y= "+y+"  endY= "+ endY);
    }
//    private  void drawThermometerFon(Canvas canvas) {
//        //MIN
//        int colWith = columnWith *4;
//        int startX = (width - colWith) /2;
//        drawLine(startX,startX + colWith,0, minTemperaturePoint, mPath);
//        drawCanvas(canvas, 0xc000c0ff, mPath,  Paint.Style.FILL);//Paint.Style.STROKE
//        //MAX
//        startX = (width - colWith) /2;
//        drawLine(startX,startX + colWith, maxTemperaturePoint,height, mPath);
//        drawCanvas(canvas, 0xc0ff0000, mPath, Paint.Style.FILL);//80ffc0c0 Paint.Style.STROKE
//
//        //-----------
//        //     Path mPath = new Path();
//        int offset = width /offsetLeftRight;//смещение от края имеджа
//        int step = height /offsetGap;//смещение от края имеджа
//        int x,endX; boolean flag;
//        // mPath.reset();
//        mPaint.setColor(0xFF000000);
//        mPaint.setTextSize(18f * density);
//        for(int y = offsetGap, i = (int)(-2 * density); y < height; y += offsetGap, i++){
//            if((i % 10) == 0){
//                flag = true;
//                x = offsetX;
//            }else{
//                flag = false;
//                if((i % 5) == 0)x = offsetX *2 + offsetX / 2;
//                else x = offsetX *4;
//            }
//            endX = width - x;
//            //  drawLine(x,endX,y, y+(int)density, mPath);
//            mPath.addRect(endX,y+(int)density,x,y,Path.Direction.CW);
//            if(flag){
//                Path path = new Path();
//                path.moveTo(x, y);
//                path.lineTo(endX, y);
//                // path.addCircle(x, y, radius, Path.Direction.CW);
//                int st = (int)(-mstep * i);
//                int stM = Math.abs(st % 10);
//                st = (st % 100) / 10;
//                mPaint.setColor(0xFFFFFFFF);
//                mPaint.setFakeBoldText(true);
//                canvas.drawTextOnPath(Integer.toString(st), path,(st < 0)?0:offsetX *density/2 ,  18 * density*4/10 , mPaint);
//                canvas.drawTextOnPath(Integer.toString(stM), path, endX - 18 * density , 18 * density*4/10, mPaint);
////                mPaint.setColor(0xFF000000);
////                mPaint.setFakeBoldText(true);
////                canvas.drawTextOnPath(Integer.toString(st), path,(st < 0)?0:offsetX *density/2 ,  18 * density*4/10 , mPaint);
////                canvas.drawTextOnPath(Integer.toString(stM), path, endX - 18 * density , 18 * density*4/10, mPaint);
//            }
//        }
//        drawCanvas(canvas, 0xFF000000, mPath, Paint.Style.FILL);//Paint.Style.STROKE--почемуто хреново рисует!
//
//    }
}
