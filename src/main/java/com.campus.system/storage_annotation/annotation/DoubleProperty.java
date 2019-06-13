package com.campus.system.storage_annotation.annotation;

public @interface DoubleProperty {
    String nameInDb() default "";
    String desc() default "";
    double defaultValue() default 0.00;
}
