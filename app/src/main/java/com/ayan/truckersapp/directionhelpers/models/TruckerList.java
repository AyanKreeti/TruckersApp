package com.ayan.truckersapp.directionhelpers.models;

import java.util.ArrayList;

public class TruckerList {
    ArrayList<Trucker> truckers;

    public TruckerList(ArrayList<Trucker> truckers) {
        this.truckers = truckers;
    }

    public ArrayList<Trucker> getTruckers() {
        return truckers;
    }

    public void setTruckers(ArrayList<Trucker> truckers) {
        this.truckers = truckers;
    }
}
