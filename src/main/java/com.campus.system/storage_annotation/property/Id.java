package com.campus.system.storage_annotation.property;

public class Id extends IntProperty {
    public Id(String nameInDb, String desc, int defalut, boolean autoIncrease) {
        super("Id", desc, defalut, autoIncrease);
    }
}
