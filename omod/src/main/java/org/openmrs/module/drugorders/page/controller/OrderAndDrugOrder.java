/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders.page.controller;

import org.openmrs.Order;
import org.openmrs.module.drugorders.drugorders;

/**
 *
 * @author harini-geek
 */
public class OrderAndDrugOrder {
    
    private final Order order;
    private final drugorders drugorders;
    
    public OrderAndDrugOrder(Order order, drugorders drugorders) {
        this.order = order;
        this.drugorders = drugorders;
    }

    public Order getOrder() {
        return order;
    }

    public drugorders getdrugorders() {
        return drugorders;
    }
    
}
