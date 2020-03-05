package com.ayan.truckersapp.directionhelpers.models;

import java.util.ArrayList;

public class ScheduleList {
    ArrayList<Schedule> schedules;

    public ScheduleList(ArrayList<Schedule> schedules) {
        this.schedules = schedules;
    }

    public ArrayList<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(ArrayList<Schedule> schedules) {
        this.schedules = schedules;
    }
}
