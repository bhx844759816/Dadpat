package com.benbaba.dadpat.host.bean;

import java.util.List;
import java.util.UUID;

public class BleAdvertisedData {
    private List<UUID> mUuids;
    private String mName;
    private String address;

    public BleAdvertisedData(List<UUID> uuids, String name) {
        mUuids = uuids;
        mName = name;
    }

    public List<UUID> getUuids() {
        return mUuids;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getName() {
        return mName;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof BleAdvertisedData){

        }
        return false;
    }
}
