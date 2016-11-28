/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.actionbarcompat.listpopupmenu;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
//import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SpinnerAdapter;

/**
 * This sample shows you how to use {@link android.support.v7.widget.PopupMenu PopupMenu} from
 * ActionBarCompat to create a list, with each item having a dropdown menu.
 * <p>
 * The interesting part of this sample is in {@link PopupListFragment}.
 *
 * This Activity extends from {@link ActionBarActivity}, which provides all of the function
 * necessary to display a compatible Action Bar on devices running Android v2.1+.
 */
//
public class MainActivity extends ActionBarActivity {

  //  private PopupListFragment popupListFragment= new PopupListFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set content view (which contains a PopupListFragment)
        setContentView(R.layout.sample_main);
        getBaseContext();
        getApplication();

        PopupListFragment plf = new  PopupListFragment();
    //    setContentView(plf.getListView());

   //     setContentView(plf.getListView());


        android.app.ActionBar  ab = getActionBar();
        System.out.println("ActionBar=" + ab);
        if(ab != null)ab.setIcon(R.drawable.alexey_photor_fo_visa);

//        actionBar.setDisplayHomeAsUpEnabled(false);
//        actionBar.setHomeButtonEnabled(false);
//        actionBar.setDisplayUseLogoEnabled(false);
//        actionBar.setDisplayShowTitleEnabled(false);
//        actionBar.setDisplayShowHomeEnabled(false);
     //   popupListFragment
        //  ab.setTitle("Датчики");
       // ab.setIcon(R.drawable.rounded_a);
       // View.SYSTEM_UI_FLAG_FULLSCREEN
    }
//    Анимация Floating Action Button в Android
//    https://geektimes.ru/company/nixsolutions/blog/276128/
//
//
//    Design
//    Downloads!
//    https://developer.android.com/design/downloads/index.html
//
//    Design
//    Action Bar
//    https://developer.android.com/design/patterns/actionbar.html
//
//    Настройка ActionBar — панели действий
//    http://www.fandroid.info/nastrojka-paneli-dejstvij-actionbar/
//
//    Android Design Support Library — поддержка компонентов Material Design в приложениях с Android 2.1 до Android 5+ (с примерами)
//    http://www.fandroid.info/android-design-support-library-podderzhka-komponentov-material-design-v-prilozheniyah-s-android-2-1-do-android-5-s-primerami/
//
//    Настройка ActionBar вкладки на Android 4
//    http://ru.androids.help/q11418
//    https://www.youtube.com/watch?v=NYVcfa6Bke4
//
//    Меню
//    https://developer.android.com/guide/topics/ui/menus.html#context-menu
//
//    Menu Resource
//    https://developer.android.com/guide/topics/resources/menu-resource.html
    //Android Design Support Library — поддержка компонентов Material Design в приложениях с Android 2.1 до Android 5+ (с примерами)
    // http://www.fandroid.info/android-design-support-library-podderzhka-komponentov-material-design-v-prilozheniyah-s-android-2-1-do-android-5-s-primerami/

//Develop API Guides User Interface Меню
    // https://developer.android.com/guide/topics/ui/menus.html#context-menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater= getMenuInflater();
        menuInflater.inflate(R.menu.poplist_menu,menu);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.edit_a://.new_game_:
                View v =((View)findViewById(R.id.textViewName));
                if(v != null){
                    v.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
                }
                FragmentManager fm= getFragmentManager();

                Fragment f =  fm.findFragmentById(R.layout.list_item);

                LayoutInflater lf = getLayoutInflater();
                Resources r =getResources();

                System.out.println("Menu-edit_a  v=" + v);
                ;

                return true;
            case R.id.add_a://
                android.app.ActionBar  ab = getActionBar();
                System.out.println("Menu-add_a  ab=" + getActionBar());
                ;return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
