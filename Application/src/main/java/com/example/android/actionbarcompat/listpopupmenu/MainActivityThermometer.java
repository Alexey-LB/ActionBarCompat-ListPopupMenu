package com.example.android.actionbarcompat.listpopupmenu;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.portfolio.alexey.connector.BluetoothLeServiceNew;
import com.portfolio.alexey.connector.DrawableThermometer;
import com.portfolio.alexey.connector.Sensor;
import com.portfolio.alexey.connector.Util;

import static java.lang.Thread.sleep;
import com.kyleduo.switchbutton.SwitchButton;
/**
 * Created by lesa on 27.12.2016.
 */

public class MainActivityThermometer  extends AppCompatActivity {// ActionBarActivity {
    public  final static String TAG = "MA.Therm";
    private Menu menuFragment;
    //    public  final static String EXTRAS_DEVICE_NAME = "EXTRAS_DEVICE_NAME";
//    public  final static String EXTRAS_DEVICE_NAME_FILTR = "EXTRAS_DEVICE_NAME_FILTR";
//    public  final static String EXTRAS_DEVICE_ADDRESS = "EXTRAS_DEVICE_ADDRESS";
//    public  final static String EXTRAS_DEVICE_ITEM = "EXTRAS_DEVICE_ITEM";
//
// //ОБЯЗАТЕЛЬНО !! ввести в практику передачу типа для порожденного окна!!
//    public  final static String EXTRA_BAR_TITLE = "EXTRA_BAR_TITLE";
//
    public  final static int MAIN_ACTIVITY_THERMOMETER = 0xFFFF1235;

    private View thermometer;
    private int itemSensor = 0;
    private Sensor sensor;
    private SwitchButton mSwitchOffSensor;
    private SwitchButton mSwitchResetMeasurement;
    private Drawable marker_fon;
    private Drawable numbe_cur_fon;
    RunDataHub app;

    private DrawableThermometer mThermometerDrawable = new DrawableThermometer(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setFullscreen(this);// работает отлично! один раз объевил, работает пока окно не умрет!
        setContentView(R.layout.activity_main_thermometer);
        //-------------------------------------------------------
        final Intent intent = getIntent();
        itemSensor = getIntent().getIntExtra(MainActivityWork.EXTRAS_DEVICE_ITEM, itemSensor);
        app = ((RunDataHub) getApplicationContext());
        //--------ПРИМЕМ ЕСЛИ сервис не запущен и нет доступа к сенсорам, выходим!--
        if((app == null) || (app.mBluetoothLeServiceM == null)
                || (app.mBluetoothLeServiceM.arraySensors == null)
                || (app.mBluetoothLeServiceM.arraySensors.get(itemSensor) == null)){

            Log.e(TAG,"ERR = ((app == null) || (app.mBluetoothLeServiceM == null)" +
                    "                || (app.mBluetoothLeServiceM.arraySensors == null) || (sensor == null)");
            finish();
        }//
        sensor = app.mBluetoothLeServiceM.arraySensors.get(itemSensor);
        //------------------------------
        //настраиваем и включаем тулбар
       // Util.setSupportV7appActionBar(getSupportActionBar(),TAG, intent.getStringExtra(Util.EXTRAS_BAR_TITLE));
        //в тулбаре ничего нЕ пишем здесь, все заносим в резюие, поскольку там идет обновления после сетингов
        Util.setSupportV7appActionBar(getSupportActionBar(),TAG,"");
        //убираем системный бар
        thermometer = findViewById(R.id.LayoutMainThermometer);
        thermometer.findViewById(R.id.reset_min_max).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //сброс минимум и максимум
                if(sensor != null) sensor.resetMinMaxValueTemperature();
            }
        });
        View v = thermometer.findViewById(R.id.LinearLayoutWarning);
        v.setVisibility(View.INVISIBLE);//выключаем видимость его, пока не сработало
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //сброс минимум и максимум
                if(sensor != null) sensor.resetNotificationVibrationLevelMinMax();
                v.setVisibility(View.INVISIBLE);//выключаем видимость его, пока не сработало
            }
        });
        //-------- ПЕРЕКЛЮЧАТЕЛИ --- Размеры его можно узнать ТОЛЬко после его отображения,
        //  https://github.com/kyleduo/switchbutton
        // не удобно расчитывать, по этому возьмем примерно
        mSwitchOffSensor = (SwitchButton)thermometer
                .findViewById(R.id.switchOffSensor);
        mSwitchOffSensor.setTextColor(getResources()
                .getColor(R.color.colorTextlight));//  sw.setBackColor(android.content.res.ColorStateList.valueOf(0xFFd0d0d0));
        //!!ВНИМАНИЕ размер кнопки-ЗАВИСИТ ОТ ДЛИННЫ ТЕКСТА!!!,
        // если тест длиннее указаний размера кнопки- равнение на длинну текста!
        mSwitchOffSensor.setText("",getString(R.string.sDiscon));//
       // mSwitchOffSensor.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);//
   //     mSwitchOffSensor.setTextSize(14f);//
      //  mSwitchOffSensor.setMinWidth(200);
        //размер по ШИРИНЕ переключателя можно определить примерно ТАК
        //Ratio(2f) * ThumbSize(45f,45f) + 20dp = 110dp, + layout_marginRight(3dp)== 113dp!!
    //    mSwitchOffSensor.setBackMeasureRatio(2f);
   //     mSwitchOffSensor.setThumbSize(45f,45f);

        /////       sw.setThumbSize(50f,50f);//размер круга- большлго пальца
        //ловит касания и перемещение ЗДЕСЬ обработка сдвига переключателя
        // и фиксация во включенром сотоянии, в обновлении мы возвращяем его назад и
        // выполняем функцию которой он предназначен!
        mSwitchOffSensor.setOnTouchListener(new CompoundButton.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {return onTouchSwitchButton(v,event);}
        });
        //
        mSwitchResetMeasurement = (SwitchButton)thermometer
                .findViewById(R.id.switchResetMeasurement);
        mSwitchResetMeasurement.setTextColor(getResources()
                .getColor(R.color.colorTextlight));//  sw.setBackColor(android.content.res.ColorStateList.valueOf(0xFFd0d0d0));
        //!!ВНИМАНИЕ размер кнопки-ЗАВИСИТ ОТ ДЛИННЫ ТЕКСТА!!!,
        // если тест длиннее указаний размера кнопки- равнение на длинну текста!
        mSwitchResetMeasurement.setText("",getString(R.string.sReset));//
     //   mSwitchResetMeasurement.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);//
 //       mSwitchResetMeasurement.setTextSize(14f);//
        /////       sw.setThumbSize(50f,50f);//размер круга- большлго пальца
        //ловит касания и перемещение ЗДЕСЬ обработка сдвига переключателя
        // и фиксация во включенром сотоянии, в обновлении мы возвращяем его назад и
        // выполняем функцию которой он предназначен!
        mSwitchResetMeasurement.setOnTouchListener(new CompoundButton.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {return onTouchSwitchButton(v,event);}
        });
        //ловит изменеия состояния
//        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {Log.v(TAG,"onCheckedChanged--");}});
        //-------------------------------------------------------------
        //устанавливаем ГРАДУСНИК со шкалой, в выделенный View
        thermometer.findViewById(R.id.thermometer_fon).setBackground(mThermometerDrawable);
        //  при запуске если в горизогнтальном положении был, учитываем это
        onConfigurationChanged(getResources().getConfiguration());
        //
        Log.e(TAG, "----onCreate END----- = " + getSupportActionBar().getHeight() );//+ ((PointF)mSwitchOffSensor.getBackSizeF()).toString());

        View view = findViewById(R.id.linearLayoutSwitch);
        // инициализируем укзатл на рисунок с 2 уровнями(там 2 рисунка)
        marker_fon = (Drawable)findViewById(R.id.marker_fon).getBackground();
        //нажати на меркер- СБРОС СИГНАЛИЗАЦИИ!!
        findViewById(R.id.marker_fon).setOnClickListener(
            new View.OnClickListener(){
                 @Override
                 public void onClick(View v) {
                     //СБРОС СИГНАЛИЗАЦИИ!!
                     sensor.resetNotificationVibrationLevelMinMax();
                 }
             }
        );
        //нажати  СБРОС СИГНАЛИЗАЦИИ!! ИЛИ переход в темометр
        findViewById(R.id.numbe_cur).setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        //СБРОС СИГНАЛИЗАЦИИ!!
                        sensor.resetNotificationVibrationLevelMinMax();
                    }
                }
        );
        numbe_cur_fon = (Drawable)findViewById(R.id.numbe_cur).getBackground();

        Log.v(TAG, "----" + mSwitchOffSensor.getWidth()
                + " = " + mSwitchOffSensor.getBackMeasureRatio()
                + " = " + view.getWidth());
    }
    //Кнопки ИОС для сброса измерения и ОТКЛЮчения термометра --
    private boolean onTouchSwitchButton(View v, MotionEvent event) {
        //https://github.com/kyleduo/switchbutton
        SwitchButton sw = (SwitchButton)v;
        View view = findViewById(R.id.frameLayoutSwitchOffSensor);
     //   Log.v(TAG, "----" + sw.getWidth() + " = " + sw.getBackMeasureRatio() + " = " + view.getWidth());//((PointF)mSwitchOffSensor.getBackSizeF()).toString());

//        Log.v(TAG,"onTouch--" + event.getX() + "  size= " + v.getWidth()
//                +"  start= "+ (event.getX() -v.getX())
//                + "  progress= " + sw.getProcess() + "   Checked= "+sw.isChecked());
        //  if(event.getAction() == MotionEvent.ACTION_DOWN){return true;}
        //ЕСЛИ возвратить ИСТИНО- то это означает конец обработки слушателя
        // блокируя дальнейшее  распространение, переключатель не перепрыгивает из вкл/выкл
        // при отпускании МЫ контролируем где это произошло, и
        // если это вблизи 30% от переключения, разрешаем переключаться,
        // иначе возвращяем назад все
        if(event.getAction() == MotionEvent.ACTION_UP){
            boolean on = sw.isChecked();
            if(!on){//если выключен
                if(sw.getProcess() < 0.8f){
                    sw.setProcess(0f);//возвращяем все назад
                    return true;// обработка завершена
                } else{
                    //преходим в новое состояние сами собой
                   //sw.toggleImmediatelyNoEvent();
                    sw.toggleNoEvent();
                }
            } else {
                //блокируем во включенном состоянии, при обновлении экрана- возвращяем в 0
                sw.setProcess(1f);//возвращяем все назад
                return true;// обработка завершена
            }
        }
        return false;
    }
//    private boolean onTouchSwitchButton(View v, MotionEvent event) {
//        SwitchButton sw = (SwitchButton)v;
//        Log.v(TAG,"onTouch--" + event.getX() + "  size= " + v.getWidth()
//                +"  start= "+ (event.getX() -v.getX())
//                + "  progress= " + sw.getProcess() + "   Checked= "+sw.isChecked());
//        //  if(event.getAction() == MotionEvent.ACTION_DOWN){return true;}
//        //ЕСЛИ возвратить ИСТИНО- то это означает конец обработки слушателя
//        // блокируя дальнейшее  распространение, переключатель не перепрыгивает из вкл/выкл
//        // при отпускании МЫ контролируем где это произошло, и
//        // если это вблизи 30% от переключения, разрешаем переключаться,
//        // иначе возвращяем назад все
//        if(event.getAction() == MotionEvent.ACTION_UP){
//            boolean on = sw.isChecked();
//            //если вЫключен, и мы НЕ превысили порог переключения ВКЛ (70%)
//            if(!on && (sw.getProcess() < 0.7f)){
//                sw.setProcess(0f);//возвращяем все назад
//                return true;// обработка завершена
//            }  else{
//                //если включен, и мы НЕ превысили порог переключения ВЫКЛ (30%)
//                if(on && (sw.getProcess() > 0.3f)){
//                    // запрешяем обработку, возвращяем назад
//                    sw.setProcess(1f);//возвращяем все назад
//                    return true;// обработка завершена
//                }
//            }
//        }
//        return false;
//    }
//вызывается при смене ориентации экрана, необходимо указать разрешений в МАНИФЕСТЕ
    //перебрасывает минимум и максимум в другой слой и все
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //-- если горизонтально расположен ---
        ViewGroup horizontal, vertical;View v,v2;float width,density;
        horizontal = (ViewGroup)thermometer.findViewById(R.id.telephon_horizontal);
        vertical = (ViewGroup)thermometer.findViewById(R.id.telephon_vertical);

        switch (newConfig.orientation){
            case  Configuration.ORIENTATION_PORTRAIT://вкртикально
                if(horizontal.getChildCount() > 0){//были в горизонтальном положении
                    v = horizontal.getChildAt(0);
                    v2 = horizontal.getChildAt(1);
                    horizontal.removeAllViews();
                    vertical.addView(v,0);
                    vertical.addView(v2);
                    horizontal.setVisibility(View.GONE);
                }
                break;
            case  Configuration.ORIENTATION_LANDSCAPE:
                if(horizontal.getChildCount() == 0){
                    v = vertical.getChildAt(0);
                    v2 = vertical.getChildAt(2);
                    vertical.removeViewAt(2);
                    vertical.removeViewAt(0);
                    horizontal.addView(v);
                    horizontal.addView(v2);
                    horizontal.setVisibility(View.VISIBLE);
                }
                break;
        }
        //-- вычисление размеров переключателей
        density = getResources().getDisplayMetrics().density;
        width = getResources().getDisplayMetrics().widthPixels;
        //размер градусника+отступ слева (2), отступ справа (4) от градусника,
        // слой отступ слева (0), справа (0)= 6dp
        //получаем ИЗ РЕСУРСОВ(там указаг размер в dp) домноженный на density = Pixels!!
        // по ЭТОМУ РАСЧЕТ В ПИКСЕЛАЙ!!
        width  = width - getResources().getDimension(R.dimen.frame_thermometer_wigth)
                - getResources().getDimension(R.dimen.ic_img_gap) *2;
        // делим на 2, поскольку ереключателей 2
        width  = width/2;
        //дополнительный значения внутри переключателя, компенсируем
        //делим на размер кнопки (радиуса)// по ЭТОМУ РАСЧЕТ В ПИКСЕЛАЙ!!
        width = (width - 10 * density) / getResources().getDimension(R.dimen.switch_button_size);

        if (width > 7) width = 7;//ограничим длинну переключателя максимом
        //!!ВНИМАНИЕ размер кнопки-ЗАВИСИТ ОТ ДЛИННЫ ТЕКСТА!!!,
        // если тест длиннее указаний размера кнопки- равнение на длинну текста!
        if(mSwitchOffSensor != null)mSwitchOffSensor.setBackMeasureRatio(width);
        if(mSwitchResetMeasurement != null)mSwitchResetMeasurement.setBackMeasureRatio(width);
        //--
        Log.w(TAG," Orientation==== " +newConfig.orientation + "   Ratio= "+ width
                + "   Pixels= "+ getResources().getDisplayMetrics().widthPixels
                + "   density= "+ getResources().getDisplayMetrics().density
        );
    }

    final int iconActionSetting = 234567896;
    public boolean onCreateOptionsMenu(Menu menu){
        menuFragment = menu;//запомил, чтоб потом  изменить меню или удалить ПРИ ВЫХОДЕ из фрейма

        menu.add(Menu.NONE,iconActionSetting,Menu.NONE,"Setting")
                .setIcon(R.drawable.ic_settings_blue_32dp)//ic_settings_blue_32dp
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        // menu.clear();
        //       MenuInflater menuInflater= getMenuInflater();
        //       menuInflater.inflate(R.menu.poplist_menu,menu);
        // inflater.inflate(R.menu.myfragment_options, menu);
        //пока отключил редактирование НЕ к чему, ДА программно ПОРОЖДАЯ- встает в нужном месте
//        menu.add(Menu.NONE,iconActionEdit,Menu.NONE,"Edit")
//                .setIcon(R.drawable.ic_clear_black_24dp)
//                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    synchronized private void updateViewItem(Sensor sensor, View view){
        if((sensor == null) || (view == null) || (mHandlerWork == false)) return;
        int color,level;
        boolean b = (sensor.mConnectionState == BluetoothLeServiceNew.STATE_CONNECTED);
        //читаем уровень сигнала для каждого в отведенное время для него
        // поскольку он один берем с 0
        // сенсор опрашиваем каждые 2.4 секунды
        if((mHandlerLoop & 0x7) == 0)sensor.readRSSIandBatteryLevel();
        //if((lloop & 0x1F) == (i << 3))sensor.readRSSIandBatteryLevel();
        //
        Util.setTextToTextView(sensor.getStringTime(),R.id.time, view);
        Util.setLevelToImageView(b? sensor.battery_level: 0, R.id.battery, view);
        Util.setLevelToImageView(sensor.rssi, R.id.signal, view);
        //     || (sensor.mBluetoothDeviceAddress == null);//режим ИММИТАЦИИ- отладки
        // по умолчанию из метода toString -> заталкивается в R.id.text1, по этому мы сами это НЕ делаем
        //для тестов, если иммитация значений на ртутном столбике термометра- показываем эти значения
        if(mThermometerDrawable.mHandlerWork){
            ///значение столбика
            Util.setTextToTextView(String.format("%2.1f",mThermometerDrawable.testValueTemperature)
                    , R.id.numbe_cur, view);
            // откуда начинаем
            Util.setTextToTextView(String.format("%2.1f",mThermometerDrawable.bottomTemperatureScale)
                    ,R.id.numbe_min, view);
            //шаг шкалы
            Util.setTextToTextView(String.format("%2.1f",mThermometerDrawable.mstep)
                    ,R.id.numbe_max, view);
        } else{
            // ЭТО НОРМАЛЬНАЯ РАБОТА  - тест сброшен -
            Util.setTextToTextView(b? sensor.getString_2_ValueTemperature(true):getString(R.string.sDisconnected)
                    , R.id.numbe_cur, view);
            Util.setTextToTextView(sensor.getString_1_ValueTemperature(true),R.id.numbe_min, view);
            Util.setTextToTextView(sensor.getString_3_ValueTemperature(true),R.id.numbe_max, view);
            if(mThermometerDrawable != null){
                // float f = (float)Math.random()*100 - 20;
                mThermometerDrawable.setColumnTemperature
                        //??!!
                                (b?sensor.getValue(sensor.intermediateValue):-100f); //     fon.invalidate();
            }
        }
        // СИГНАЛИЗАЦИЯ-- в случае СРАБАТЫВАНИЯ сигнализации меняем фон
        //мигаем фоном ПОКА ОТКЛЮЧИЛИ!!
//        Util.alarmFonViewFon(sensor, view.findViewById(R.id.marker_fon).getBackground()
//                ,0,1,(lloop & 1) == 1?true:false);
        //мигание маркером
        Util.alarmFonViewFon(sensor, view.findViewById(R.id.marker).getBackground()
                ,sensor.markerColor,8,(mHandlerLoop & 1) == 1?true:false);

        // фон числа -- если ПРЕВЫШЕНИЕ- весь фон закрываем цветом УРОВНЯ
        // если сброса аларма НЕ БыЛО, а уровень вернулс к норме- ОКАНТОВКА ЧИСЛА цветом сработавшего уровня!
        level = 0;
        if(sensor.minLevelNotification.onLevel) level = 1;//нижний уровень
        else {
            if(sensor.maxLevelNotification.onLevel)level = 2;//верхний уровень
            else{
                if(sensor.minLevelNotification.onNotification)level = 3;//температура в норме- было НЕ сброшенный аларм мин
                else if(sensor.maxLevelNotification.onNotification)level = 4;//температура в норме- было НЕ сброшенный аларм Мах
            }
        }
        if(view.findViewById(R.id.numbe_cur) instanceof TextView){
            TextView tv = (TextView)view.findViewById(R.id.numbe_cur);
            color = getResources().getColor(R.color.colorText);
            if(level != 0){
                if((level & 1) == 1){//min
                    color = getResources().getColor(R.color.colorMinThermometer);
                } else{//max
                    color = getResources().getColor(R.color.colorMaxThermometer);
                }
            }
            if(tv.getCurrentTextColor() != color) tv.setTextColor(color);
        }
     //   if(numbe_cur_fon.getLevel() != level)numbe_cur_fon.setLevel(level);//

        //---положение переключателей-----положение переключателей-----------------------
        //ловим в сотоянии ВКЛЮЧЕН, запускаем функцию на выполнение и сбрасываем переключатель
        //отключение сенсора
        if(mSwitchOffSensor.isChecked()){
            mSwitchOffSensor.setChecked(false);// в исхождное положение
            sensor.switchOffSensor();
        }
        //сброс измерения сенсора
        if(mSwitchResetMeasurement.isChecked()){
            mSwitchResetMeasurement.setChecked(false);
            sensor.resetMeasurement();//сброс измерения на самом сенсоре
            // для сброса Мин Мах к текущей температуре
            sensor.resetMinMaxValueTemperature();

        }
        //ОТОБРАЖЕНИЕ и положение переключателей
        // коннекта ЕСТЬ!
        if(sensor.mConnectionState > 0){
            if(!mSwitchOffSensor.isEnabled() || !mSwitchResetMeasurement.isEnabled()){
                color = getResources().getColor(R.color.colorTextlight);
                mSwitchOffSensor.setTextColor(color);
                mSwitchOffSensor.setEnabled(true);
                //устанавливает цвет, иначе цвет СРАЗУ НЕ установить!(setEnabled()-инициирует обновление)
                mSwitchResetMeasurement.setTextColor(color);
                mSwitchResetMeasurement.setEnabled(true);
            }
        } else{
            //если установлн то сбрасываем
            if(mSwitchOffSensor.isEnabled() || mSwitchResetMeasurement.isEnabled()){
                color = getResources().getColor(R.color.colorBackgroundGrey);
                mSwitchOffSensor.setTextColor(color);
                mSwitchOffSensor.setEnabled(false);
                //устанавливает цвет, иначе цвет СРАЗУ НЕ установить!(setEnabled()-инициирует обновление)
                mSwitchResetMeasurement.setTextColor(color);
                mSwitchResetMeasurement.setEnabled(false);
            }
        }
     }
    private int mHandlerLoop = 0;
    private boolean mHandlerWork = true;
    private Handler mHandler = new Handler();
    @Override
    public  void onPause() {
        super.onPause();
        mHandlerWork = false;
    }
    @Override
    public  void onResume() {
        super.onResume();
        //при возвращениие из других окон, может быть системный бар, по этому еще раз его отменяем
        //делаем Здесь обновление термометра, чтоб при выходе из настроек сенсора, изменения были учтены
        mThermometerDrawable.setSettingThermometer(getResources().getDisplayMetrics().density
                ,sensor.minLevelNotification.valueLevel
                ,sensor.maxLevelNotification.valueLevel
                ,sensor.minLevelNotification.switchNotification
                ,sensor.maxLevelNotification.switchNotification
                ,sensor.onFahrenheit,false);
        /// тоже что выше НО с ЗАПУСКОМ ТЕСТА ртутного столбика!
//        mThermometerDrawable.setSettingThermometer(getResources().getDisplayMetrics().density
//                ,sensor.minTemperature,sensor.maxTemperature,sensor.onFahrenheit,true);


 //вывод ЦВЕТНОГО МАРКЕРА, делаем в резюме- поскольку можем прийти из сеттингов
 //Util.setDrawableToImageView(sensor.markerColor,R.id.marker, thermometer);
 thermometer.findViewById(R.id.marker).getBackground().setLevel(sensor.markerColor);

        // название ТОЖЕ может поменятся, о этому еще раз
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
       if(actionBar != null) actionBar.setTitle("  " + sensor.deviceLabel);
        //---
        mHandlerWork = true;
        //сам заводится и работает
        mHandler.postDelayed(new Runnable() {
            public void run() {
            //    Log.v(TAG,"mHandler --");
                updateViewItem(sensor, thermometer);
                //если только начали в этом окне, сбросим счетчик выдачи уровня сигнала и батарей
                // чтоб батарея быстрее была опрошена, после смены окна
                if(mHandlerLoop == 0)sensor.loop_rssi = 0;
                mHandlerLoop++;
                // повторяем через каждые 300 миллисекунд
                if(mHandlerWork) mHandler.postDelayed(this, 300);
            }
        },500);
        Log.e(TAG, "----onResume() ----------");// + mSwitchOffSensor.getWidth());
        //--
        //если блутуз не существует то и включать нечего!
        if(!app.mBluetoothLeServiceM.isBluetoothAdapterExist()) return;//выходим на запрос ВКЛ блутуза 2 раза!!
        //вызываем окно включения блутуз модуля
        if (!app.mBluetoothLeServiceM.mBluetoothAdapter.isEnabled()) {
            // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
            // fire an intent to display a dialog asking the user to grant permission to enable it.
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
    public static final int REQUEST_ENABLE_BT = 15;
    @Override//сюда прилетают ответы при возвращении из других ОКОН активити
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, getString(R.string.bluetooth_adapter_is_turned_off), Toast.LENGTH_LONG).show();
            Log.e(TAG, getString(R.string.bluetooth_adapter_is_turned_off));
            finish();
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(menuFragment != null)menuFragment.clear();
        if (isFinishing()) {
            Log.e(TAG,"onDestroy() ==============isFinishing() =========== isFinishing() ======");
        } else {
            Log.e(TAG,"onDestroy() -----------WORK ------------- WORK -------------");
        }
    }

    //http://developer.alexanderklimov.ru/android/theory/fragments.php
    //ДОБАВЛЕНИЯ СВОЕГО МЕНЮ ИЗ ФРАГМЕНТА!!
    // СЮДА прилетают ВСЕ клики по меню, также и кнопка назад!!
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Log.i(TAG,"android.R.id.home");
                //--сохранение настроек
                RunDataHub app = ((RunDataHub) getApplicationContext());
                if(app.mBluetoothLeServiceM != null){
                    //записываем УСТАНОВКИ если есть изменения в них
                    app.mBluetoothLeServiceM.testChangesAndSettingPutFile();//app.mBluetoothLeServiceM.settingPutFile();
                }
                finish();
                return true;
            case iconActionSetting:
                Log.i(TAG,"iconActionSetting-");
                  //-------Setting --
                  final Intent intent = new Intent(this, MainSettingSetting.class);
                  intent.putExtra(MainActivityWork.EXTRAS_DEVICE_ITEM, itemSensor);
                  intent.putExtra(Util.EXTRAS_BAR_TITLE,"  " + getString(R.string.title_Thermometer));
                  startActivity(intent);//
               // startActivityForResult(intent,MainActivityThermometer.MAIN_ACTIVITY_THERMOMETER);//
                return true;
            default:
                // Not one of ours. Perform default menu processing
                return super.onOptionsItemSelected(item);
        }
    }
}
