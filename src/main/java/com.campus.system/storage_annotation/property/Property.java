package com.campus.system.storage_annotation.property;

public class Property {
    private String mNameInDb;
    private String mDesc;

    public Property(String nameInDb, String desc) {
        mNameInDb = nameInDb;
        mDesc = desc;
    }

    public String getNameInDb() {
        return mNameInDb;
    }
}
