package com.campus.system.storage_annotation.annotation;

public @interface Id {
    boolean autoIncrease() default true;
    String nameInDb() default "";
    String desc() default "";
    int defaultValue() default 0;
}
