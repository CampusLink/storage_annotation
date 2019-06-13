package com.campus.system.storage_annotation.annotation;

import com.campus.system.storage_annotation.model.Date;

public @interface DateProperty {
    String nameInDb() default "";
    String desc() default "";
    int[] defalutValue() default {1992,12,03};
}
