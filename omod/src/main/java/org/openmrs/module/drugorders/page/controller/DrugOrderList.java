/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders.page.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.drugorders.api.drugordersService;
import org.openmrs.module.drugorders.drugorders;

/**
 *
 * @author harini-parthasarathy
 */
public class DrugOrderList {
    
    public DrugOrderList(){
        
    }
    
    /*
      Get references to all drug_order_extn records that are Drug Orders.
    */
    public static List<OrderAndDrugOrder> getDrugOrdersByPatient(Patient p) {
        ArrayList<OrderAndDrugOrder> drugOrders = new ArrayList<>();
        // Retrieve the list of all orders placed for the given Patient.
        List<Order> orders = Context.getOrderService().getAllOrdersByPatient(p);
        // Select the type of order records that are of the type 'Drug Order'.
        int drugOrderTypeId = Context.getOrderService().getOrderTypeByName("Drug Order").getOrderTypeId();
        drugorders drugOrder;
        // For each order in the list of orders, find if it is a drug order.
        for (Order order : orders) {
            if (order.getOrderType().getOrderTypeId() == drugOrderTypeId) {
                drugOrder = Context.getService(drugordersService.class).getDrugOrderByOrderID(order.getOrderId());
                // Add the order to the list to be returned.
                drugOrders.add(new OrderAndDrugOrder(order, drugOrder));
            }
        }
        return drugOrders;
    }
    
    /*
      Get references to all drug_orders records that are Drug Orders.
    */
    public static HashMap<Integer,DrugOrder> getDrugOrderMainDataByPatient(Patient p){
        HashMap<Integer,DrugOrder> drugOrdersMain = new HashMap<>();
        // Retrieve the list of all orders placed for the given Patient.
        List<Order> orders = Context.getOrderService().getAllOrdersByPatient(p);
        // Select the type of order records that are of the type 'Drug Order'.
        int drugOrderTypeId = Context.getOrderService().getOrderTypeByName("Drug Order").getOrderTypeId();
        org.openmrs.DrugOrder drugOrderMain;
        // For each order in the list of orders, find if it is a drug order.
        for (Order order : orders) {
            if (order.getOrderType().getOrderTypeId() == drugOrderTypeId){
                drugOrderMain = (org.openmrs.DrugOrder) Context.getOrderService().getOrder(order.getOrderId());
                // Add the order to the list to be returned.
                drugOrdersMain.put(drugOrderMain.getOrderId(),drugOrderMain);
            }
        }
        return drugOrdersMain;
    }
}