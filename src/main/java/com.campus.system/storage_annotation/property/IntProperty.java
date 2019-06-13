package com.campus.system.storage_annotation.property;

public class IntProperty extends Property {
    private int mDefalut;
    private boolean mAutoIncrease;

    public IntProperty(String nameInDb, String desc, int defalut, boolean autoIncrease) {
        super(nameInDb, desc);
        mDefalut = defalut;
        mAutoIncrease = autoIncrease;
    }
}
