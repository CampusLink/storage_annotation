package com.campus.system.storage_annotation.property;

public class StringProperty extends Property {
    private String mDefalut;
    private int mLength;

    public StringProperty(String nameInDb, String desc
            , String defalut, int length) {
        super(nameInDb, desc);
        mDefalut = defalut;
        mLength = length;
    }
}
