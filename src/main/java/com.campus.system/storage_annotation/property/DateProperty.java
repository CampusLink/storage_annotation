package com.campus.system.storage_annotation.property;

import com.campus.system.storage_annotation.model.Date;

public class DateProperty extends Property{
    private Date mDefault;
    public DateProperty(String nameInDb, String desc, Date defaultValue) {
        super(nameInDb, desc);
        mDefault = defaultValue;
    }
}
