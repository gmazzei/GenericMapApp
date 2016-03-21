package com.generic.mapapp.domain;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by gabriel on 20/03/16.
 */
public class StoreType implements Serializable {

    private Integer id;
    private String name;

    public StoreType(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        StoreType type = (StoreType) o;
        return this.id.equals(type.getId());
    }

}