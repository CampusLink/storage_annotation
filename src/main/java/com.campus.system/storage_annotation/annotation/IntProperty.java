package com.campus.system.storage_annotation.annotation;

public @interface IntProperty {
    String nameInDb() default "";
    String desc() default "";
    int defaultValue() default 0;
    boolean autoIncrease() default false;
}
