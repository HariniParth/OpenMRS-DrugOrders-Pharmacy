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
        List<drugorders> dorders = new ArrayList<>();
        // Data structure to store the group drug orders
        HashMap<Integer,List<drugorders>> groupDorders = new HashMap<>();
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
                    dorders.add(dorder);
                    break;
                case "Non-Active-Group":
                    if(groupDorders.get(dorder.getGroupId()) == null){
                        groupDorders.put(dorder.getGroupId(), Context.getService(drugordersService.class).getDrugOrdersByGroupID(dorder.getGroupId()));
                    }
                    break;
            }
        }
                
        model.addAttribute("oldDrugOrdersExtension", dorders);
        model.addAttribute("oldDrugOrderGroups", groupDorders);
        
        HashMap<Integer,DrugOrder> oldDrugOrdersMain = DrugOrderList.getDrugOrderMainDataByPatient(patient);
        model.addAttribute("oldDrugOrdersMain", oldDrugOrdersMain);
                
    }
}