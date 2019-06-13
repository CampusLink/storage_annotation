package com.campus.system.storage_annotation.annotation;

public @interface StringProperty {
    String nameInDb();
    int length() default 225;
    String desc() default "";
    String defaultValue() default "";
}
