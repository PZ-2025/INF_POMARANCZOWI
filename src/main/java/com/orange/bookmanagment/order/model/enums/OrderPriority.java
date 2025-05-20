package com.orange.bookmanagment.order.model.enums;

public enum OrderPriority {
    LOW,MEDIUM,HIGH;

    public static boolean existByName(String name){

        for (OrderPriority orderPriority : OrderPriority.values()) {
            if (orderPriority.name().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
