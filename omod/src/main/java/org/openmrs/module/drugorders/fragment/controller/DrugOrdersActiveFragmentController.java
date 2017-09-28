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
import org.openmrs.CareSetting;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.drugorders.api.drugordersService;
import org.openmrs.module.drugorders.drugorders;
import org.openmrs.module.uicommons.util.InfoErrorMessageUtil;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author harini-parthasarathy
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
        
        // Get the records for CareSetting 'Outpatient'.
        CareSetting careSetting = Context.getOrderService().getCareSettingByName("Outpatient");
        // Get the records for OrderType 'Drug Order'
        OrderType orderType = Context.getOrderService().getOrderTypeByName("Drug Order");
        // Get the list of all Orders for the Patient.
        List<Order> orders = Context.getOrderService().getOrders(patient, careSetting, orderType, true);
        
        // Data structure to store the list of active single individual drug orders.
        List<drugorders> singleOrders = new ArrayList<>();
        for(Order order : orders){
            if(Context.getService(drugordersService.class).getDrugOrderByOrderID(order.getOrderId()).getOrderStatus().equals("Active")){
                singleOrders.add(Context.getService(drugordersService.class).getDrugOrderByOrderID(order.getOrderId()));
            }
        }
        
        // Data structure to store the list of active group drug orders.
        HashMap<Integer,List<drugorders>> groupOrders = new HashMap<>();
        
        // Retrieve the list of active group orders.
        List<drugorders> groups = new ArrayList<>();
        for(Order order : orders){
            if(Context.getService(drugordersService.class).getDrugOrderByOrderID(order.getOrderId()).getOrderStatus().equals("Active-Group") || Context.getService(drugordersService.class).getDrugOrderByOrderID(order.getOrderId()).getOrderStatus().equals("Draft-Group")){
                groups.add(Context.getService(drugordersService.class).getDrugOrderByOrderID(order.getOrderId()));
            }
        }
        
        for(drugorders o : groups){
            if(groupOrders.get(o.getGroupId()) == null){
                List<drugorders> groupDrugOrders = new ArrayList<>();
                // Retrieve the list of active orders in the same group as the given 'Active-Group' order.
                for(drugorders order : Context.getService(drugordersService.class).getDrugOrdersByGroupID(o.getGroupId()))
                    if(order.getOrderStatus().equals("Active-Group") || order.getOrderStatus().equals("Draft-Group"))
                        groupDrugOrders.add(order);

                groupOrders.put(o.getGroupId(), groupDrugOrders);
            }
        }
                
        model.addAttribute("singleOrdersExtn", singleOrders);
        model.addAttribute("groupOrdersExtn", groupOrders);
        
        // Store the list of Orders records.
        HashMap<Integer, Order> Orders = new HashMap<>();
        // Store the list of Orderer records.
        HashMap<Integer, String> Orderer = new HashMap<>();
        // Store the list of Drug Order records.
        HashMap<Integer, DrugOrder> drugOrdersMain = new HashMap<>();
        // Get the list of all DrugOrder records.
        List<Order> drugOrders = Context.getOrderService().getOrders(patient, careSetting, orderType, true);
        for(Order order: drugOrders){
            Orders.put(order.getOrderId(), Context.getOrderService().getOrder(order.getOrderId()));
            Orderer.put(order.getOrderId(), Context.getOrderService().getOrder(order.getOrderId()).getCreator().getGivenName() + " " + Context.getOrderService().getOrder(order.getOrderId()).getCreator().getFamilyName());
            drugOrdersMain.put(order.getOrderId(), (DrugOrder) Context.getOrderService().getOrder(order.getOrderId()));
        }
        
        model.addAttribute("Orders", Orders);
        model.addAttribute("Orderer", Orderer);
        model.addAttribute("drugOrdersMain", drugOrdersMain);
                
    }
}