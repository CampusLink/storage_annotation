package com.campus.system.storage_annotation.property;

public class DoubleProperty extends Property{
    private double mDefalut;

    public DoubleProperty(String nameInDb, String desc, double defalut) {
        super(nameInDb, desc);
        mDefalut = defalut;
    }
}
