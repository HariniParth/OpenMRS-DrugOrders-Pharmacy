/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders.fragment.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.openmrs.DrugOrder;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.drugorders.api.drugordersService;
import org.openmrs.module.drugorders.drugorders;
import org.openmrs.module.drugorders.page.controller.DrugOrderList;
import org.openmrs.module.drugorders.page.controller.OrderAndDrugOrder;
import org.openmrs.module.uicommons.util.InfoErrorMessageUtil;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author harini-geek
 */
public class DrugOrdersActiveFragmentController {
    
    public void controller(FragmentModel model, HttpSession session, @RequestParam("patientId") Patient patient,
                            @RequestParam(value = "action", required = false) String action,
                            @RequestParam(value = "removeFromGroup", required = false) String removeFromGroup,
                            @RequestParam(value = "singleCheckBox", required=false) long[] singleCheckBox,
                            @RequestParam(value = "groupCheckBox", required=false) long[] groupCheckBox){
        
        /*
          If an order is selected to be removed from an order group:
          - Set the order status to 'Active' (from 'Active-Group').
          - Set the groupId value of the drug order to null.
        */
        if(!removeFromGroup.equals("")){
            drugorders drugorder = Context.getService(drugordersService.class).getDrugOrderByOrderID(Integer.parseInt(removeFromGroup));
            drugorder.setOrderStatus("Active");
            drugorder.setGroupId(null);
        }
        
        /*
          Group the selected orders. 
        */
        if ("GroupOrder".equals(action)) {
            // If one or more orders are selected to be 'grouped':
            if(singleCheckBox.length > 0 || groupCheckBox.length > 0){
                // Set the group ID to 1 + the last group ID index.
                int groupID = Context.getService(drugordersService.class).getLastGroupID() + 1;
                
                // Check if one or more individual orders are selected to be grouped
                if(singleCheckBox.length > 0){
                    for(int i=0;i<singleCheckBox.length;i++){
                        // Retrieve the order ID from  value stored in the checkbox.
                        int id = Integer.parseInt(Long.toString(singleCheckBox[i]));
                        // Fetch the drug order record.
                        drugorders orders = Context.getService(drugordersService.class).getDrugOrderByOrderID(id);
                        // Set the group ID.
                        orders.setGroupId(groupID);
                        // Set the status to "Active-Group".
                        orders.setOrderStatus("Active-Group");
                    }
                }
                
                // Check if one or more group orders are selected to be grouped
                if(groupCheckBox.length > 0){
                    for(int i=0;i<groupCheckBox.length;i++){
                        // Retrieve the group ID from  value stored in the checkbox.
                        int id = Integer.parseInt(Long.toString(groupCheckBox[i]));
                        // Fetch the drug order records.
                        List<drugorders> orders = Context.getService(drugordersService.class).getDrugOrdersByGroupID(id);
                        // Set the updated group ID for each of these orders.
                        for(drugorders order : orders)
                            order.setGroupId(groupID);
                    }
                }
                InfoErrorMessageUtil.flashInfoMessage(session, "Orders Saved!");
            }
            else
                InfoErrorMessageUtil.flashErrorMessage(session, "Check Orders To Be Grouped!");
        }
        
        // Data structure to store the individual drug orders
        List<drugorders> dorders = new ArrayList<>();
        // Data structure to store the group drug orders
        HashMap<Integer,List<drugorders>> groupDorders = new HashMap<>();
        // Retrieve the drug orders created for the Patient
        List<OrderAndDrugOrder> drugOrders = DrugOrderList.getDrugOrdersByPatient(patient);
                
        for(OrderAndDrugOrder drugOrder : drugOrders){
            drugorders dorder = drugOrder.getdrugorders();
            // Check if a drug order is an individual order or a part of a group and then store it appropriately
            switch (dorder.getOrderStatus()) {
                case "Active":
                    dorders.add(dorder);
                    break;
                case "Active-Group":
                    if(groupDorders.get(dorder.getOrderId()) == null){
                        groupDorders.put(dorder.getGroupId(), Context.getService(drugordersService.class).getDrugOrdersByGroupID(dorder.getGroupId()));
                    }   break;
            }
        }
                
        model.addAttribute("existingDrugOrdersExtension", dorders);
        model.addAttribute("existingDrugOrderGroups", groupDorders);
        
        HashMap<Integer,DrugOrder> existingDrugOrdersMain = DrugOrderList.getDrugOrderMainDataByPatient(patient);
        model.addAttribute("existingDrugOrdersMain", existingDrugOrdersMain);
                
    }
}