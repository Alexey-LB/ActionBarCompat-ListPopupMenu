package com.example.android.actionbarcompat.listpopupmenu;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by lesa on 12.12.2016.
 */

public class DialogFragmentNote  extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        Context context = getActivity().getApplicationContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setBackgroundColor(Color.BLUE);
        TextView text = new TextView(context);
        text.setText("Это область фрагмента");
        layout.addView(text);

        return layout;
        ///--XML -------------
//        View myFragmentView = inflater.inflate(R.layout.fragmentlayout,
//                container, false);
//
//        return myFragmentView;

        //-------------
    }
}
