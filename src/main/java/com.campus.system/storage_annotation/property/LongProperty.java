package com.campus.system.storage_annotation.property;

public class LongProperty extends Property {
    private long mDefalut;

    public LongProperty(String nameInDb, String desc, long defaultValue) {
        super(nameInDb, desc);
        mDefalut = defaultValue;
    }
}
