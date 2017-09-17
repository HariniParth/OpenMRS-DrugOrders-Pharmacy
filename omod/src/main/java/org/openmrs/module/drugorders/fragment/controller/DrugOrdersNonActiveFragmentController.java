/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders.fragment.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.drugorders.api.drugordersService;
import org.openmrs.module.drugorders.drugorders;
import org.openmrs.module.drugorders.page.controller.DrugOrderList;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author harini-parthasarathy
 */
public class DrugOrdersNonActiveFragmentController {
    
    public void controller(FragmentModel model, @RequestParam("patientId") Patient patient){
        
        // Get the list of all Orders for the Patient.
        List<Order> orders = Context.getOrderService().getAllOrdersByPatient(patient);
        
        // Data structure to store the list of non-active single individual drug orders.
        List<drugorders> singleOrders = new ArrayList<>();
        for(Order order : orders){
            if(Context.getService(drugordersService.class).getDrugOrderByOrderID(order.getOrderId()).getOrderStatus().equals("Non-Active")){
                singleOrders.add(Context.getService(drugordersService.class).getDrugOrderByOrderID(order.getOrderId()));
            }
        }
        
        // Data structure to store the list of non-active group drug orders.
        HashMap<Integer,List<drugorders>> groupOrders = new HashMap<>();
        
        // Retrieve the list of non-active group orders.
        List<drugorders> groups = new ArrayList<>();
        for(Order order : orders){
            if(Context.getService(drugordersService.class).getDrugOrderByOrderID(order.getOrderId()).getOrderStatus().equals("Non-Active-Group")){
                groups.add(Context.getService(drugordersService.class).getDrugOrderByOrderID(order.getOrderId()));
            }
        }
        
        for(drugorders o : groups){
            if(groupOrders.get(o.getGroupId()) == null){
                List<drugorders> groupDrugOrders = new ArrayList<>();
                // Retrieve the list of non-active orders in the same group as the given 'Non-Active-Group' order.
                for(drugorders order : Context.getService(drugordersService.class).getDrugOrdersByGroupID(o.getGroupId()))
                    if(order.getOrderStatus().equals("Non-Active-Group"))
                        groupDrugOrders.add(order);

                groupOrders.put(o.getGroupId(), groupDrugOrders);
            }
        }
                
        model.addAttribute("singleOrdersExtn", singleOrders);
        model.addAttribute("groupOrdersExtn", groupOrders);
        
        HashMap<Integer,DrugOrder> drugOrdersMain = DrugOrderList.getDrugOrderMainDataByPatient(patient);
        model.addAttribute("drugOrdersMain", drugOrdersMain);
                
    }
}