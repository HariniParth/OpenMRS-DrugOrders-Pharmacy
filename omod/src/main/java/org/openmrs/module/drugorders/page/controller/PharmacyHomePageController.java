/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders.page.controller;

import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.drugorders.api.drugordersService;
import org.openmrs.module.drugorders.api.planordersService;
import org.openmrs.module.drugorders.drugorders;
import org.openmrs.module.drugorders.planorders;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author harini-parthasarathy
 */
public class PharmacyHomePageController {
    
    public void controller(PageModel model, @RequestParam(value = "patient_full_name", required = false) String patient_full_name,
                                            @RequestParam(value = "homeCheckbox", required=false) String[] homeCheckbox){
        
        // Retrieve the list of all registered patients.
        List<Patient> allPatients = Context.getPatientService().getAllPatients();
        // Trim the string representing the name of the Patient being searched.
        String searchPatient = patient_full_name.trim();
        // Find the Patient record of the Patient whose name is entered.
        if(!searchPatient.equals("")){
            boolean patientFound = false;
            List<Patient> patients = new ArrayList<>();
            for(Patient p : allPatients){
                if((p.getGivenName()+" "+p.getFamilyName()).equals(searchPatient)){
                    patientFound = true;
                    patients.add(p);
                }
            }
            if(patientFound)
                model.addAttribute("patients", patients);
            else
                model.addAttribute("patients", "");
        } else {
            model.addAttribute("patients", "");
        }
        
        /*
          Remove the hold on the list of selected drug orders.
        */
        List<Integer> listOfOrders = new ArrayList<>();
        // Check if more than one order(s) is selected to be removed from hold.
        if(homeCheckbox.length > 0){
            for (String box : homeCheckbox) {
                String[] selected = box.split(" ");
                
                /*
                  If one or more check-boxes corresponding to a single, group or medication plan order(s) are checked,
                  retrieve the list of drug orders selected.                  
                */
                switch (selected[0]) {
                    case "PLAN":
                        List<planorders> planOrders = Context.getService(planordersService.class).getPlanOrdersByPlanID(Integer.parseInt(selected[1]));
                        for(planorders planOrder : planOrders){
                            listOfOrders.add(planOrder.getOrderId());
                        }   
                        break;
                        
                    case "GROUP":
                        List<drugorders> drugorders = Context.getService(drugordersService.class).getDrugOrdersByGroupID(Integer.parseInt(selected[1]));
                        for(drugorders drugorder : drugorders){
                            listOfOrders.add(drugorder.getOrderId());
                        }   
                        break;
                        
                    case "SINGLE":
                        listOfOrders.add(Integer.parseInt(selected[1]));
                        break;
                }
            }
            
            // Remove the hold (if applied) on the list of drug orders selected.
            for (int orderID : listOfOrders) {
                drugorders drugorder = Context.getService(drugordersService.class).getDrugOrderByOrderID(orderID);
                if(drugorder.getOnHold() == 1)
                    drugorder.setOnHold(0);
            } 
        }                     
        
        /*
          Fetch the list of orders for all patients that are put on hold or requested to be discontinued.
        */
        List<drugorders> ordersOnHold = Context.getService(drugordersService.class).getOrdersOnHold();
        List<drugorders> ordersForDiscard = Context.getService(drugordersService.class).getOrdersForDiscard();
        
        // Data structures to store the single, group and plan orders. <OrderID, Order>
        List<drugorders> singleOrders = new ArrayList<>();
        HashMap<Integer, List<drugorders>> planOrders = new HashMap<>();
        HashMap<Integer, List<drugorders>> groupOrders = new HashMap<>();
        
        // Data structures to store the Orderer's and Patient's name and ID.
        HashMap<Integer,String> ordererName = new HashMap<>();
        HashMap<Integer,String> patientName = new HashMap<>();
        HashMap<Integer, Integer> patientID = new HashMap<>();
        // Retrieve all the orders placed on hold and all the orders requested to be discarded.
        for(drugorders order: Iterables.concat(ordersOnHold, ordersForDiscard)){
            Person physician = Context.getPersonService().getPerson(Context.getOrderService().getOrder(order.getOrderId()).getOrderer().getProviderId());
            /*
              Here, we segregate the drug orders into three groups - Single, Group and Plan depending on its status, 
              so that each single order or group/plan order set can be represented in a separate row.
            */
            switch (order.getOrderStatus()) {
                case "Active":
                    singleOrders.add(order);
                    patientID.put(order.getOrderId(), Context.getOrderService().getOrder(order.getOrderId()).getPatient().getPatientId());
                    ordererName.put(order.getOrderId(), physician.getGivenName()+" "+physician.getFamilyName());
                    break;
                    
                case "Active-Group":
                    // If all the orders in the order group containing this order are not retrieved -
                    if(!groupOrders.containsKey(order.getGroupId())){
                        // If the given order is a part of an order group, retrieve all the drug orders in that group.
                        List<drugorders> allGroupOrders = Context.getService(drugordersService.class).getDrugOrdersByGroupID(order.getGroupId());
                        List<drugorders> activeGroupOrders = new ArrayList<>();
                        // From the retrieved set of orders, fetch the orders that are currently active.
                        for(drugorders groupOrder : allGroupOrders){
                            if(groupOrder.getOrderStatus().equals("Active-Group")){
                                activeGroupOrders.add(groupOrder);
                                patientID.put(groupOrder.getOrderId(), Context.getOrderService().getOrder(groupOrder.getOrderId()).getPatient().getPatientId());
                                ordererName.put(groupOrder.getOrderId(), physician.getGivenName()+" "+physician.getFamilyName());
                            }
                        }
                        // Store the active orders in a group as value and the group ID as the key.
                        groupOrders.put(order.getGroupId(), activeGroupOrders);
                    }   break;
                    
                case "Active-Plan":
                    planorders planorders = Context.getService(planordersService.class).getPlanOrderByOrderID(order.getOrderId());
                    // If all the orders in the medication plan order containing this order are not retrieved -
                    if(!planOrders.containsKey(planorders.getPlanId())){
                        // Retrieve the list of all the drug orders related to the selected medication plan order.
                        List<planorders> allPlanOrders = Context.getService(planordersService.class).getPlanOrdersByPlanID(planorders.getPlanId());
                        List<drugorders> activePlanOrders = new ArrayList<>();
                        
                        for(planorders planOrder : allPlanOrders){
                            // From the list of retrieved orders, fetch the orders that are currently active.
                            if(Context.getService(drugordersService.class).getDrugOrderByOrderID(planOrder.getOrderId()).getOrderStatus().equals("Active-Plan")){
                                activePlanOrders.add(Context.getService(drugordersService.class).getDrugOrderByOrderID(planOrder.getOrderId()));
                                patientID.put(planOrder.getOrderId(), Context.getOrderService().getOrder(planOrder.getOrderId()).getPatient().getPatientId());
                                ordererName.put(planOrder.getOrderId(), physician.getGivenName()+" "+physician.getFamilyName());
                            }
                        }
                    // Store the active orders in the plan order as value and the plan ID as the key.
                    planOrders.put(planorders.getPlanId(), activePlanOrders);
                }   break;
            }
            
            //Store the list of Patient's name who have an Order that is On-Hold or For-Discard
            if(!patientName.containsKey(Context.getOrderService().getOrder(order.getOrderId()).getPatient().getPatientId())){
                Patient patient = Context.getOrderService().getOrder(order.getOrderId()).getPatient();
                patientName.put(patient.getPatientId(), patient.getGivenName()+" "+patient.getFamilyName());
            }
        }
        
        model.addAttribute("patientID", patientID);
        model.addAttribute("patientName", patientName);
        model.addAttribute("ordererName", ordererName);
        model.addAttribute("patientSingles", singleOrders);
        model.addAttribute("patientGroups", groupOrders);
        model.addAttribute("patientPlans", planOrders);
    }
}
