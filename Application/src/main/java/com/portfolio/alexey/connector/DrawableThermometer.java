package com.portfolio.alexey.connector;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.example.android.actionbarcompat.listpopupmenu.*;


/**
 * Created by lesa on 28.12.2016.
 */

public class DrawableThermometer extends Drawable {
    static String TAG = "DrawTHERM";
    //используем как эталон для остальных фигур
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path mPath = new Path();

    private  int hightColumn = 0;//высота столюбика ртути
    public   float testValueTemperature = 0;//значение темепературы в градусах
    //
    private float density;//пикселей на dp

    private final int offsetGapFinal = 5;//  5 dpi //через сколько дпи по У рисуем линии
    private int offsetGap = offsetGapFinal;//  5 dpi //через сколько дпи по У рисуем линии
    private final int columnWithFinal = 14;
    private  int columnWith = columnWithFinal;//  14 dpi - ширина столбика термометра

    private final float textSizeFinal = 18f;//размер текста надписи в градусах
    private float textSize = textSizeFinal;


    private final int offsetXFinal = 5;// смещение от края по Х в др
    private int offsetX = offsetXFinal;// смещение от края по Х в

    public boolean minSwitch = false;//ПЕРЕКЛЮЧатель разрешение
    public boolean maxSwitch = false;//ПЕРЕКЛЮЧатель разрешение

    private float minTemperature; //минимальная температура, при которой срабатывает сигнализация
    private float maxTemperature;//максимальная температура, при которой срабатывает сигнализация

    private  int minTemperaturePoint;//точка на градуснике в ПИКСЕЛАХ которая соответствует minTemperature
    private  int maxTemperaturePoint;//точка на градуснике в ПИКСЕЛАХ которая соответствует maxTemperature

    public  float bottomTemperatureScale;//начало шкалы градусника в ГРАДУСАХ
    public   float topTemperatureScale;//конец шкалы градусника в ГРАДУСАХ

    public  boolean onFahrenheit;
    private  boolean chengSize = false;//  признак изменения размеров градусника и НЕОБХОДИМОСТЬ заново отрисовать ФОН

    public float mstep;// в градусах на деление ШКАЛЫ термометра

    private  int width = 0;//  ширина градусника в пикселях
    private  int height = 0;//   высота градусника в пикселях

    private  float mRangeTemperature = 0;//диапазон теператру, который необходимо вывести на градуснике

    //private  float[] stepNet = {0.1f,0.25f,0.5f,1f,2.5f,5f,10f,25f,50f,100f};//разрешенные шаги на шкале градусника
    private  float[] stepNet = {0.1f,0.2f,0.5f,1f,2f,5f,10f,20f,50f,100f};//разрешенные шаги на шкале градусника
    private  int[] stepDp = {offsetGapFinal,offsetGapFinal +1,offsetGapFinal +2,//разрешенные МИНИМАЛЬНЫЕ растояния между делениями
            offsetGapFinal +3,offsetGapFinal +4,offsetGapFinal +5,
            offsetGapFinal +6,offsetGapFinal +7};
    private final float minRange = 3;//C минимальная шкала градусника

    //private Canvas canvasFon;// = new Canvas();
    private final Activity mActivity;
    public DrawableThermometer(Activity activity){
        super();
        mActivity = activity ;//  14 dpi - ширина столбика термометра
    //    columnWith = (int)mActivity.getResources().getDimension(R.dimen.thermometer_column_wigth);//  14 dpi - ширина столбика термометра
    }
    //--
    private float  roundingFloat(float f, float round){
        int i = Math.round(f/round);
        f = (float)i;
        return f * round;
    }
    private void calckStep(){
        int i,j;float rangeWidth, y,k = 0;
        //------ расчет в пикселях, задаем ШАГ между делениями по оси У --------------
        for(j = 0;j < stepDp.length;j++) {
            //ШАГ между делениями по оси У в пикселах = (stepDp[j] * density)
            rangeWidth = height / (stepDp[j] * density);//реальное количество делений на градуснике
            y = mRangeTemperature / rangeWidth;//ищем ЦЕНУ минимального деления ИЗ разрешенных значений
            for (i = 0; i < stepNet.length; i++) if (stepNet[i] > y) break;
            //контроль диапазона
            if(i >= stepNet.length) i--;
            y = (mRangeTemperature / stepNet[i]) /  rangeWidth;//получили коэфициент использования шкалы
            y = y* (float)Math.pow(0.95f,i);//для нас важнее более мелкое деление
            // чем ближе он к 1 тем полнее заполняет шкалу
            if(y > k) {
                k = y;
                mstep = stepNet[i];//сколько градусов на деление!! mstep * rangeWidth = ДИАПАЗОН выода температуры
                offsetGap = Math.round(stepDp[j] * density);
//                Log.w(TAG, " net= " + Math.round(rangeWidth)+"  step= " + stepNet[i] + "  stepDp= " + stepDp[j] + "   count=" + Math.round((mRangeTemperature / stepNet[i])
//                        + "   RangeTemp=" + Math.round(mRangeTemperature+"   mRangeTempALL= " +Math.round((stepNet[i] *rangeWidth));
            } else{
//                Log.i(TAG, " net= " + Math.round(rangeWidth+"  step= " + stepNet[i] + "  stepDp= " + stepDp[j] + "   count=" + (int)(mRangeTemperature / stepNet[i])
//                        + "   RangeTemp=" + Math.round(mRangeTemperature+"   mRangeTempALL= " +Math.round((stepNet[i] *rangeWidth));
            }
        }
        // выбор сделан, устанавливаем начало - мин значение и максимальное.
        // полученный диапазон вывода на градусник будет равен заданному или больше его, определяем это
         k = (maxTemperature - minTemperature) / mstep;//шкалу которую надо вывести, этот диапазон обязателен
         rangeWidth = (float)height / (float)offsetGap;//реальное количество делений на градуснике
        mRangeTemperature = rangeWidth * mstep; //  диапазон выводимах значений
        y = (rangeWidth - k) / 2;// вычислили отступ вверх и вниз от maxTemperature и minTemperature
        // minTemperaturePoint - округляем до шага шкалы по У
        bottomTemperatureScale = roundingFloat(minTemperature - mstep * y,mstep);//начало шкалы градусника в ГРАДУСАХ
        topTemperatureScale = bottomTemperatureScale + mRangeTemperature;//конец шкалы градусника в ГРАДУСАХ
        //установка в пикселах УРОВНЕЙ срабатывания сигнализации
        // точка на градуснике в ПИКСЕЛАХ которая соответствует minTemperature
        minTemperaturePoint = Math.round((offsetGap * (minTemperature - bottomTemperatureScale))/mstep);
        //точка на градуснике в ПИКСЕЛАХ которая соответствует maxTemperature
        maxTemperaturePoint = Math.round((offsetGap * (maxTemperature - bottomTemperatureScale))/mstep);
        //
        Log.d(TAG, " min= " + minTemperature + " / " + bottomTemperatureScale +"  max= " + maxTemperature +" / " + topTemperatureScale
                + " step= " + mstep+"  offsetGap= " + offsetGap
                + "   min/max Point=" + minTemperaturePoint+" / " +maxTemperaturePoint);
    }
    private void calckFon(){
        float y, rangeWidth;int i;
        //контроль входных данных
        mRangeTemperature = (maxTemperature - minTemperature) *1.2f;// на 20% выводим больше, для отображения ЗОН сигнализации
        //--расчет  начала
        //---контроль минимума диапазона --
        y = minRange;
        if(onFahrenheit) y = minRange * 9/5;
        if (y > mRangeTemperature) mRangeTemperature = y;
        calckStep();
        rangeWidth = height / (offsetGap * density);
        Log.v(TAG, " net= " + Math.round(rangeWidth)+"  step= " + mstep  + "   count=" + Math.round(mRangeTemperature / mstep)
               + "   RangeTemp=" + Math.round(mRangeTemperature)+"   mRangeTempALL= " +Math.round(mstep *rangeWidth));
    }
    private  float[] testMin = {-22,-23.23f,-25.5f,-30.76f,-30f,  0f, -10f, -50f};
    private  float[] testMax = {-20, -3.5f  , 10.75f, 30.1f,70.5f,5f,15f,25f};
    private void testFon(){
        calckFon();
        for(int i = 0;i <testMin.length; i++){
            minTemperature = testMin[i];
            maxTemperature = testMax[i];
            calckFon();
        }
    }

    public void setSettingThermometer(float density_,float minTemperature_,float maxTemperature_
            , boolean minSwitchTemperature, boolean maxSwitchTemperature
            , boolean fahrenheit,boolean test){
        onFahrenheit = fahrenheit;
        minSwitch = minSwitchTemperature;
        maxSwitch = maxSwitchTemperature;
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
            offsetGap = Math.round(offsetGapFinal * density);  ///5 dpi //через сколько дпи по У рисуем линии
            columnWith = Math.round(columnWithFinal * density);//  14 dpi - ширина столбика термометра
            offsetX = Math.round(offsetXFinal * density);// смещение от края по Х в др
            textSize = (textSizeFinal * density);//размер текста надписи в градусах
        }
        // ДЛЯ размера меню И ТД, используется density !!!
        // density = getResources().getDisplayMetrics().density;
        mHandlerWork = false;
        Log.w(TAG,"minTemp= " + minTemperature + " maxTemp= " + maxTemperature + "  class= " + this);
        if(test){
            //необходимо всегда УДАЛЯТЬ старые запуски, иначе ОНИ НАЧИНАЮТ ЖИТЬ ВСЕ ВМЕСТЕ!!!
            mHandler.removeCallbacks(runnable);
            mHandlerWork = true;
            //сам заводится и работает
            mHandler.postDelayed(runnable,500);
        }
        calckFon();//пересчитать фон!!
        //    invalidateSelf();//Запуск ПЕРЕРИСОВАТЬ!!
    }

    private Runnable runnable = new Runnable(){
        public void run() {
            //         Log.v(TAG,"mHandler --");

            setColumnTemperature(getNextTestTemp());

            // повторяем через каждые 300 миллисекунд
            if(mHandlerWork) mHandler.postDelayed(this, 3000);
        }
    };


    private float getNextTestTemp(){
        int value;
        value = Math.round(bottomTemperatureScale);
        value = value + iCount * Math.round(mstep * 10);
   //     Log.i(TAG,"*testTemp= " + value);
        iCount++;
        if(value >= (topTemperatureScale - (mstep * 11))) iCount= 1;
        return (float)value;
    }
    private int iCount = 1;

    public  boolean mHandlerWork = false;
    private Handler mHandler = new Handler();

    // высота столбика термометра
    public void setColumnTemperature(float temperature) {
        //входную температуру для отображения Вычитаем минимальное значение, домножаем на коэфициент
        // перехода к пикселам и добавляем смещение пикселов на экране
        testValueTemperature = temperature;
       int n = calckColumnTemperature(temperature);//
        if(n != hightColumn){//если есть изменения
            hightColumn = n;
            invalidateSelf();//Запуск ПЕРЕРИСОВАТЬ!!
      }
   //     Log.i(TAG,"IN temperature =" + temperature+"  hightColumn= "+ hightColumn +" minT= " +minTemperature + " maxT= " +maxTemperature
    //            + " minP= " +minTemperaturePoint + " maxP= " +maxTemperaturePoint);
    }
    //вычисление столбика высоты столбика термометра в пикселах
    public  int calckColumnTemperature(float temperature){
            return Math.round(((temperature - bottomTemperatureScale) * offsetGap / mstep));//
    }

    @Override
    public void draw(Canvas canvas) {
        if((width <= 0) || (height <= 0))return;
        //
  //      Log.i(TAG, "draw --="+canvas.getHeight()+ " --="+canvas.getDensity());
        //
        // (ПОЧЕМУТО НЕ работает, получается всегда заново отрисовывает)рисуем фон градусника, шкалу, ЕСЛИ ИЗМЕНИЛИСЬ РАЗМЕРЫ!!
//        if(chengSize){
//            drawThermometerFon(canvas);
//            chengSize = false;
//        }
        //надо всегда отрисовывать, иначе ничего не отображается
       drawThermometerFon(canvas);
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
        if((width != bounds.width()) || (height != bounds.height())){
            chengSize = true;
        } else chengSize = false;
        // устнавливаем размеры для рисования
        width = bounds.width();
        height = bounds.height();
        //testFon();
        calckFon();
    }
    private  void drawThermometerFon(Canvas canvas) {
        //MIN
        Path path;int x,y,i,shift;
        int colWith = columnWith *2;
        int startX = (width - colWith) /2;
        int endX = startX + colWith;
        Resources res = mActivity.getResources();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            yourTitle.setTextColor(getActivity().getResources().getColor(android.R.color.white, getActivity().getTheme()));
//        }else {
//            yourTitle.setTextColor(getActivity().getResources().getColor(android.R.color.white));
//        }
        if(minSwitch) {
            // MIN
            drawRect(startX, 0, endX, minTemperaturePoint, mPath);
            drawCanvas(canvas, res.getColor(R.color.colorMinThermometer), mPath, Paint.Style.FILL);//Paint.Style.STROKE
        }
        if(maxSwitch) {
            //MAX
            drawRect(startX, maxTemperaturePoint, endX, height, mPath);
            drawCanvas(canvas, res.getColor(R.color.colorMaxThermometer), mPath, Paint.Style.FILL);//80ffc0c0 Paint.Style.STROKE
        }
        //-----------
        // mPath.reset();values/colors.xml
        mPaint.setColor(res.getColor(R.color.colorScaleDivisionThermometer));
        mPaint.setTextSize(res.getDimension(R.dimen.TextScaleDivisionThermometerSize));
        // в зависимости от ЗНАКА, мы идем вперед или догоняем остаток
        shift =  Math.abs(Math.round((bottomTemperatureScale / mstep)) % 10);
        if(bottomTemperatureScale < 0) shift = 10 - shift;
//        Log.v(TAG,"valueTemperature= " +testValueTemperature+ "   shift= " + shift
//                + "   countShift=" + (bottomTemperatureScale / mstep)+ " bottTemp= "+ bottomTemperatureScale);
   //     Log.i(TAG,"minTemp= " + minTemperature + " maxTemp= " + maxTemperature + "  class= " + this);
        for( y = 0, i = shift ; y < height; y += offsetGap, i++){
            if((i % 10) == 0){
                x = offsetX;
                path = new Path();
            }else{
                path = null;
                if((i % 5) == 0)x = offsetX *3 + offsetX / 2;
                else x = offsetX *5 + offsetX / 2 ;

            }
            endX = width - x;
            drawRect(x,y,endX,y-Math.round(density),mPath);
            if(path != null){
                //формируем линию, вдоль которой будем писать(y- переворачиваем)
                path.moveTo(getXpixel(x), getYpixel(y));
                path.lineTo(getXpixel(endX), getYpixel(y));
                int st = Math.round(bottomTemperatureScale + mstep * (i - shift)),stM;
                if((st / 10) != 0){
                    stM = Math.abs(st % 10);
                    st = st / 10;//st = (st % 100) / 10;
                    // пишем текст
                    if(Math.abs(st) >= 10) canvas.drawTextOnPath(Integer.toString(st), path,(st < 0)?offsetX/2:0 ,  -textSize*1/10 , mPaint);
                    else canvas.drawTextOnPath(Integer.toString(st), path,offsetX/2,  -textSize*1/10 , mPaint);
                } else{
                    //если меньше 10, знак минуса ВЫВОДИТЬ НАДО! а 0 первый НЕ выводим!
                    stM = st % 10;
                }
                // пишем текст, если отрицательный, немного сдигаем его для отображения МИНУСА
                if(stM >= 0)canvas.drawTextOnPath(Integer.toString(stM), path, endX - textSize , -textSize*1/10, mPaint);
                else canvas.drawTextOnPath(Integer.toString(stM), path, endX - textSize *13/10, -textSize*1/10, mPaint);
            }
        }
     //   Log.w(TAG,"------drawThermometerFon");
        drawCanvas(canvas, 0xFF000000, mPath, Paint.Style.FILL);//Paint.Style.STROKE--почемуто хреново рисует!
    }
    //можно использовать один и тот же Path, НО обязательно path.reset(); при повторном использовании
    //Paint- хранит цвет кисть и т д -
    private void drawThermometerColumn(Canvas canvas) {
        //
        int startX,endX, levelY = hightColumn;
        Resources res = mActivity.getResources();
        //сначала стираем столбик ( реально мы рисуем по чистому листу, просто
        startX = (width - columnWith) /2;
        endX = startX + columnWith;
        drawRect(startX,0 ,endX,height,mPath);
        drawCanvas(canvas, 0xFFFFFFFF, mPath,   Paint.Style.FILL);//Paint.Style.STROKE

        startX = (width - columnWith) /2;
        drawRect(startX,0 ,endX,levelY,mPath);
        drawCanvas(canvas, res.getColor(R.color.colorColumnThermometer), mPath,   Paint.Style.FILL);//Paint.Style.STROKE

        startX = (width - columnWith + Math.round((8 * density))) /2;
        endX = startX + columnWith -Math.round((8 * density));
        drawRect(startX,0 ,endX,levelY,mPath);
        drawCanvas(canvas, 0x80ffffff, mPath,   Paint.Style.FILL);//Paint.Style.STROKE
         //
        startX = (width - columnWith + Math.round((12 * density))) /2;
        endX = startX + columnWith -Math.round((12 * density));
        drawRect(startX,0 ,endX,levelY,mPath);
        drawCanvas(canvas, 0xC0FFFFFF, mPath ,   Paint.Style.FILL);//Paint.Style.STROKE

        startX = (width - columnWith + Math.round((14 * density))) /2;
        endX = startX + columnWith -Math.round((14 * density));
        drawRect(startX,0 ,endX,levelY,mPath);
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
     //    y = y * Math.round(density);
        if(y < 0)y = 0;
        if(y > height) y = height;
        ////ПО УМОЛЧАНИЮ- 0 У, верхний левый УГОЛ экрана!
        // переворачиваем- ИДЕМ СНИЗУ вверх,
        return height - y;
    }
    private int getXpixel(int x){
     //   x = x * Math.round(density);
        if(x < 0) x = 0;
        if(x >  width) x = width;
        return x;
    }
    private void drawRect(int x,int y,int endX,int endY, Path mPath ){
        //ПО УМОЛЧАНИЮ- 0 У, верхний левый УГОЛ экрана!
        // переворачиваем- ИДЕМ СНИЗУ вверх,
        y = height - y;
        endY = height - endY;
        if(x < 0) x = 0;
        if(y < 0)y = 0;
        if(x > width) x = width;
        if(y > height)y = height;
        //
        if(endX < 0) endX = 0;
        if(endY < 0)endY = 0;
        if(endX > width) endX = width;
        if(endY > height)endY = height;
        //
        mPath.addRect(x,y ,endX,endY,Path.Direction.CW);

        // Log.v(TAG,"x= "+x +"  endX= " + endX +"  y= "+y+"  endY= "+ endY);
    }
    private void drawLine(int x,int endX,int y,int endY, Path mPath ){
        //ПО УМОЛЧАНИЮ- 0 У, верхний левый УГОЛ экрана!
        // переворачиваем- ИДЕМ СНИЗУ вверх,
        y = height - y;
        endY = height - endY;
        if(x < 0) x = 0;
        if(y < 0)y = 0;
        if(x > width) x = width;
        if(y > height)y = height;
        //
        if(endX < 0) endX = 0;
        if(endY < 0)endY = 0;
        if(endX > width) endX = width;
        if(endY > height)endY = height;
        if((x == endX) && (y == endY)) return;//это точка ее не рисует,
        mPath.moveTo(x, y);
        mPath.lineTo(endX, y);
        mPath.lineTo(endX, endY);
        mPath.lineTo(x, endY);
        mPath.lineTo(x, y);
       // Log.v(TAG,"x= "+x +"  endX= " + endX +"  y= "+y+"  endY= "+ endY);
    }
}
