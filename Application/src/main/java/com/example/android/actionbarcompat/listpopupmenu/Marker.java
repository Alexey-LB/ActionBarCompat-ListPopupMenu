package com.example.android.actionbarcompat.listpopupmenu;

import java.util.TreeMap;


public class Marker {
    final  static String TEG = "Marker";
//    public  static String[]  markerKey = {"white","red","yellow","green","cyan","blue","purple","black"};

    private final  static int[] img = {
            R.drawable.marker_white,R.drawable.marker_red,
            R.drawable.marker_yellow,R.drawable.marker_green,
            R.drawable.marker_cyan,R.drawable.marker_blue,
            R.drawable.marker_purple,R.drawable.marker_black};

    static public int getNextItem(int item){
        item = item  & 0x7;
        item++;
        if(item >= img.length) item = 0;
        return item;
    }
    static public int getIdImg(int item){
        return img[item & 0x7];
    }
}
