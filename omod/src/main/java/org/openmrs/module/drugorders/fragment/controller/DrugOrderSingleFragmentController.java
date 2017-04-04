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
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author harini-geek
 */
public class DrugOrderSingleFragmentController {
    
    public void controller(PageModel model, HttpSession session, @RequestParam("patientId") Patient patient,
                            @RequestParam(value = "action", required = false) String action,
                            @RequestParam(value = "removeFromGroup", required = false) String removeFromGroup,
                            @RequestParam(value = "singleCheckBox", required=false) long[] singleCheckBox,
                            @RequestParam(value = "groupCheckBox", required=false) long[] groupCheckBox){
        
        if(!removeFromGroup.equals("")){
            drugorders drugorder = Context.getService(drugordersService.class).getDrugOrderByOrderID(Integer.parseInt(removeFromGroup));
            drugorder.setOrderStatus("Active");
            drugorder.setGroupId(null);
        }
        
        if ("GroupOrder".equals(action)) {
            if(singleCheckBox.length > 0 || groupCheckBox.length > 0){
                int groupID = Context.getService(drugordersService.class).getLastGroupID() + 1;
                
                if(singleCheckBox.length > 0){
                    for(int i=0;i<singleCheckBox.length;i++){
                        int orderID = Integer.parseInt(Long.toString(singleCheckBox[i]));
                        drugorders orders = Context.getService(drugordersService.class).getDrugOrderByOrderID(orderID);
                        orders.setGroupId(groupID);
                        orders.setOrderStatus("Active-Group");
                    }
                }
                
                if(groupCheckBox.length > 0){
                    for(int i=0;i<groupCheckBox.length;i++){
                        int orderID = Integer.parseInt(Long.toString(groupCheckBox[i]));
                        List<drugorders> orders = Context.getService(drugordersService.class).getDrugOrdersByGroupID(orderID);
                        for(drugorders order : orders)
                            order.setGroupId(groupID);
                    }
                }
                InfoErrorMessageUtil.flashInfoMessage(session, "Orders Saved!");
            }
            else
                InfoErrorMessageUtil.flashErrorMessage(session, "Check Orders To Be Grouped!");
        }
        
        List<drugorders> dorders = new ArrayList<>();
        HashMap<Integer,List<drugorders>> groupDorders = new HashMap<>();
        
        List<OrderAndDrugOrder> drugOrders = DrugOrderList.getDrugOrdersByPatient(patient);
                
        for(OrderAndDrugOrder drugOrder : drugOrders){
            drugorders dorder = drugOrder.getdrugorders();
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