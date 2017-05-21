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
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.drugorders.api.drugordersService;
import org.openmrs.module.drugorders.drugorders;
import org.openmrs.module.drugorders.page.controller.DrugOrderList;
import org.openmrs.module.drugorders.page.controller.OrderAndDrugOrder;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author harini-geek
 */
public class DrugOrdersNonActiveFragmentController {
    
    public void controller(FragmentModel model, @RequestParam("patientId") Patient patient){
        
        // Data structure to store the individual drug orders
        List<drugorders> i_orders = new ArrayList<>();
        // Data structure to store the group drug orders
        HashMap<Integer,List<drugorders>> g_orders = new HashMap<>();
        // Retrieve the drug orders created for the Patient
        List<OrderAndDrugOrder> drugOrders = DrugOrderList.getDrugOrdersByPatient(patient);
        
        /*
          Get the list of non-active individual and group drug orders placed for the Patient.
        */
        for(OrderAndDrugOrder drugOrder : drugOrders){
            drugorders dorder = drugOrder.getdrugorders();
            // Check if a drug order is an individual order or a part of a group and then store it appropriately
            switch (dorder.getOrderStatus()) {
                case "Non-Active":
                    i_orders.add(dorder);
                    break;
                case "Non-Active-Group":
                    if(g_orders.get(dorder.getGroupId()) == null){
                        // If an order with status 'Non-Active-Group' if found, retrieve all the non-active orders in the given order's group.
                        List<drugorders> orders = new ArrayList<>();
                        for(drugorders order : Context.getService(drugordersService.class).getDrugOrdersByGroupID(dorder.getGroupId()))
                            if(order.getOrderStatus().equals("Non-Active-Group"))
                                orders.add(order);
                        
                        g_orders.put(dorder.getGroupId(), orders);
                    }
                    break;
            }
        }
                
        model.addAttribute("oldDrugOrdersExtension", i_orders);
        model.addAttribute("oldDrugOrderGroups", g_orders);
        
        HashMap<Integer,DrugOrder> oldDrugOrdersMain = DrugOrderList.getDrugOrderMainDataByPatient(patient);
        model.addAttribute("oldDrugOrdersMain", oldDrugOrdersMain);
                
    }
}