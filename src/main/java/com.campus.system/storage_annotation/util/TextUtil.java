package com.campus.system.storage_annotation.util;

public class TextUtil {
    public static String upcaseFirstForMethod(String content){
        if(content == null || content.length() == 0){
            return "";
        }
        char c = content.charAt(0);
        if(content.length() == 1){
            return String.valueOf(c).toUpperCase();
        }
        if(c == 'm'){
            return upcaseFirstForMethod(content.substring(1, content.length()));
        }
        return String.valueOf(c).toUpperCase() + content.substring(1, content.length());
    }
}
