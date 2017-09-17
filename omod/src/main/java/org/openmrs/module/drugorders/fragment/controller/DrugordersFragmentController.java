/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders.fragment.controller;

import java.util.ArrayList;
import java.util.List;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.drugorders.api.drugordersService;
import org.openmrs.module.drugorders.drugorders;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

/**
 *
 * @author harini-parthasarathy
 */
public class DrugordersFragmentController {
    
    public void controller(FragmentModel model, @FragmentParam("patientId") Patient patient){
        
        List<drugorders> drugOrders = new ArrayList<>();
        
        // Retrieve the list of active individual drug orders.
        drugOrders.addAll(getActiveOrders(patient, "Active"));
        // Retrieve the list of active group drug orders.
        drugOrders.addAll(getActiveOrders(patient, "Active-Plan"));
        // Retrieve the list of active med plan drug orders.
        drugOrders.addAll(getActiveOrders(patient, "Active-Group"));
        
        model.addAttribute("drugorders", drugOrders);
    }
    
    private List<drugorders> getActiveOrders(Patient patient, String status){
        
        // Get the list of all Orders for the Patient.
        List<Order> orders = Context.getOrderService().getAllOrdersByPatient(patient);
        
        List<drugorders> drugorders = new ArrayList<>();        
        for(Order order : orders){
            if(Context.getService(drugordersService.class).getDrugOrderByOrderID(order.getOrderId()).getOrderStatus().equals(status)){
                drugorders.add(Context.getService(drugordersService.class).getDrugOrderByOrderID(order.getOrderId()));
            }
        }
        return drugorders;
    }
}