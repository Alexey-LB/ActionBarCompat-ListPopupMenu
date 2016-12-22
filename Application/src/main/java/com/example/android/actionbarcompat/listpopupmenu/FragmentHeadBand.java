package com.example.android.actionbarcompat.listpopupmenu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//  http://developer.alexanderklimov.ru/android/theory/appcompat.php
// getFragmentManager() -> getSupportFragmentManager()
//getActionBar() -> getSupportActionBar()
// android:Theme.Holo.Light  -> Theme.AppCompat:
//Если вы не используете ActionBar, но хотите использовать отдельные стили,
// то наследуйтесь от Theme.AppCompat и добавьте стили,
// используемые в Theme.AppCompat.NoActionBar:
//-------------------------------------

public class FragmentHeadBand extends Fragment  {
    private static final String TAG = "HeadBandFragment";
    //private Menu menuFragment;
   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Нужно помнить, что в методе inflate() последний параметр должен
        // иметь значение false в большинстве случаев.
        View view = inflater.inflate(R.layout.frame_headband,
                container, false);
        // ишем кнопку внутри вювера поскольку это фрагмент а не Активность
        //Button nextButton = (Button) view.findViewById(R.id.button_first);
        return view;
    }
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.main_menu, menu);
//        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.searchView));
//    }

}
