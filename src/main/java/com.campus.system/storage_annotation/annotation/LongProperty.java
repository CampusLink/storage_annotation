package com.campus.system.storage_annotation.annotation;

public @interface LongProperty {
    String nameInDb() default "";
    String desc() default "";
    long defaultValue() default 0L;
}
