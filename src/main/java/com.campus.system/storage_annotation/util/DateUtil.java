package com.campus.system.storage_annotation.util;


import java.util.Date;

public class DateUtil {
    public static boolean dateIsNull(Date date){
        if(date == null || date.getTime() == 0){
            return true;
        }
        return false;
    }
}
