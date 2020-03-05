package com.ayan.truckersapp.directionhelpers.models;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderList {
    @SerializedName("orders")
    List<Order> orderList;

    public OrderList(List<Order> orderList) {
        this.orderList = orderList;
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }
}
