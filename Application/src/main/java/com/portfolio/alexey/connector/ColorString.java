package com.portfolio.alexey.connector;
/**
 * Created by lesa on 01.11.2016.
 */
public class ColorString {
   // 30 black.31 red.32 green. 33 yellow.34 blue.35 magenta. 36 cyan.37 white
        //30 черный, 31 красный,32 зеленый,33 желтый,34 синий,35 пурпурного,36 Cyan,37 белый,
        // 40 черный фон, 41 красном фоне,42 зеленый фон,43 желтый фон,44 синий фон,45 пурпурного фон,46 Cyan фон,47 белый фон
        //1 сделать яркий (обычно просто жирным шрифтом),21 остановка яркий (нормализует дерзновение),4 подчеркивание,24 остановка подчеркивание,0 ясно все форматирование
        // Таким образом, "033[34;43m" -Blue text with yellow background
        // "\033[0m" - вернутся к исходной палитере
        //"033[1;31m" -  красный по ярче!! ОШИБКИ
        //"033[1;41;34m" -  фон красный  цифры синие !! ЭТО Прерывения ОБРАБОТКА catch
        public static String getRed(String err){
            return "\033[1;31m" + err + "\033[0m";
        }
        public static String getBlueFonRed(String err){
            return "\033[1;41;34m" + err + "\033[0m";
        }
        public static String getGreen(String str){
            return "\033[1;32m" + str + "\033[0m";
        }
        public static String getBlueFonGreen(String str){return "\033[1;34;42m" + str + "\033[0m";}
        public static String getBlueFonBlack(String str){return "\033[1;30;42m" + str + "\033[0m";}
        public static String getRedFonBlack(String str){return "\033[1;31;40m" + str + "\033[0m";}
        public static String getRedFonCyan(String str){
        return "\033[1;31;46m" + str + "\033[0m";
    }


    public static String get(String col, String str){
//        int i=0,j=0;
//        if(col != null){
//            if(col.length() > 1){
//                i = col.charAt(0);
//                j = col.charAt(1);
//            }else i = col.charAt(0);
//        }
        //"31;46" -- мы даем строку цвет шрифта и можемпоменять цвет фона, и т д
        //"\033[1;" - управляющие символы И включение более яркого шрифта!
        // "\033[0m" - отмена всего
        return "\033[1;"+ col + "m" + str + "\033[0m";
    }

}
