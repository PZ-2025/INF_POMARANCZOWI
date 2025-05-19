package com.orange.bookmanagment.order.model.enums;

public enum OrderStatus {
    PLACED,IN_PROGRESS,FINISHED,CANCELLED;


    public static boolean existByName(String name){

        for(OrderStatus orderStatus : OrderStatus.values()){
            if (orderStatus.name().equals(name)){
                return true;
            }
        }
        return false;
    }
}
