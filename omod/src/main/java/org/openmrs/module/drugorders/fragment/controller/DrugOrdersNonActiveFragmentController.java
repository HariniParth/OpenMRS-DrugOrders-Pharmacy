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
        
       // Data structure to store the list of non-active single individual drug orders.
        List<drugorders> singleOrders = Context.getService(drugordersService.class).getDrugOrdersByPatientAndStatus(patient, "Non-Active");
        
        // Data structure to store the list of non-active group drug orders.
        HashMap<Integer,List<drugorders>> groupOrders = new HashMap<>();
        
        // Retrieve the list of non-active group orders.
        List<drugorders> groups = Context.getService(drugordersService.class).getDrugOrdersByPatientAndStatus(patient, "Non-Active-Group");
        for(drugorders o : groups){
            if(groupOrders.get(o.getGroupId()) == null){
                List<drugorders> orders = new ArrayList<>();
                // Retrieve the list of non-active orders in the same group as the given 'Non-Active-Group' order.
                for(drugorders order : Context.getService(drugordersService.class).getDrugOrdersByGroupID(o.getGroupId()))
                    if(order.getOrderStatus().equals("Non-Active-Group"))
                        orders.add(order);

                groupOrders.put(o.getGroupId(), orders);
            }
        }
                
        model.addAttribute("singleOrdersExtn", singleOrders);
        model.addAttribute("groupOrdersExtn", groupOrders);
        
        HashMap<Integer,DrugOrder> drugOrdersMain = DrugOrderList.getDrugOrderMainDataByPatient(patient);
        model.addAttribute("drugOrdersMain", drugOrdersMain);
                
    }
}