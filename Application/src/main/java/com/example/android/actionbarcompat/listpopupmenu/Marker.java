package com.example.android.actionbarcompat.listpopupmenu;

import java.util.TreeMap;


public class Marker {
    final  static String TEG = "Marker";
    public  static String[]  markerKey = {"white","red","yellow","green","cyan","blue","purple","black"};
    public static TreeMap<String, Integer> marker = new TreeMap();
    //    {{
//        marker.put("white",R.drawable.marker_white);
//        marker.put("red",R.drawable.marker_red);
//        marker.put("yellow",R.drawable.marker_yellow);
//        marker.put("green",R.drawable.marker_green);
//        marker.put("cyan",R.drawable.marker_cyan);
//        marker.put("blue",R.drawable.marker_blue);
//        marker.put("purple",R.drawable.marker_purple);
//        marker.put("black",R.drawable.marker_black);
//        whiteId = marker.get("white");
//    }}
    private static int whiteId;
    Marker(){
        marker.put("white",R.drawable.marker_white);
        marker.put("red",R.drawable.marker_red);
        marker.put("yellow",R.drawable.marker_yellow);
        marker.put("green",R.drawable.marker_green);
        marker.put("cyan",R.drawable.marker_cyan);
        marker.put("blue",R.drawable.marker_blue);
        marker.put("purple",R.drawable.marker_purple);
        marker.put("black",R.drawable.marker_black);
        whiteId = marker.get("white");
//      //  LevelListDrawable lld = LevelListDrawable.createFromXml(R.drawable.marker,this);
//        LevelListDrawable lld = new LevelListDrawable();
//      //  getResources().
//
//
//        lld.addLevel(0,0,getDrawable(R.drawable.marker_white));
    }
    public  static int get(String key){
        int id = whiteId;
        if(key != null){
            //          Log.d(TAG, " key= "+key + "  id= " + id);
            id = marker.get(key);
            if(id <= 0)id = whiteId;
        }
        //      Log.i(TAG, " key= "+key + "  id= " + id);
        return id;
    }
    // ходим ПО кругу!!
    public  static String getNextKey(String key){
        String k = "white";
        if(key != null){
            //map - сохраняет по в СВОЕМ порялдке значения которые ей заталкивыют
            // И выводит Не так как заталкивали!!
            // по этому делаем иначе
            for(int i = 0; i < markerKey.length; i++){
                //сраниваем строки, ключи
                //              Log.d(TAG, " key= "+markerKey[i] + "   key= "+key + "   compInt= " + markerKey[i].compareTo(key));
                if(markerKey[i].compareTo(key) == 0){
                    if((i+1) < markerKey.length){
                        k = markerKey[i+1];
                    }
                    break;
                }
            }
//            Set set = marker.entrySet();
//            Iterator it= set.iterator();
//        //контроль, если мы дошли до последней позиции, начинаем с начала
//        //если у нас был последний ключь, автоматически у нас уже указан ПЕРВЫЙ ключ
//            for(int i = 1; i < set.size(); i++){
//                Map.Entry me = (Map.Entry)it.next();
//                //сраниваем строки, ключи
//                Log.d(TAG, " key= "+me.getKey().toString() + "   key= "+key + "   compInt= " + me.getKey().toString().compareTo(key));
//                if(me.getKey().toString().compareTo(key) == 0){
//
//                    // иначе берем следующий ключь
//                    me = (Map.Entry)it.next();
//                    k = (String) me.getKey();
//                    break;
//                }
//            }
        }
        //       Log.i(TAG, " key= "+k);
        return k;
    }
    // ходим ПО кругу!!
    public  static int getItemKey(String key){
        if(key != null) {
            for (int i = 0; i < markerKey.length; i++)
                if (markerKey[i].compareTo(key) == 0) return i;
        }
        return 0;
    }

}
