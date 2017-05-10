/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders.fragment.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.openmrs.Concept;
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
 * @author harini-geek
 */
public class CurrentDrugOrdersFragmentController {
    
    public void controller(PageModel model, @RequestParam("patientId") Patient patient){
        
        HashMap<Integer, Concept> planName = new HashMap<>();
        HashMap<Integer, String> OrdererName = new HashMap<>();
        
        /*
          Get the list of all active individual drug orders placed for the Patient.
        */
        List<drugorders> patientSingleOrders = Context.getService(drugordersService.class).getDrugOrdersByPatientAndStatus(patient, "Active");
        for(drugorders drugorder : patientSingleOrders){
            Person person = Context.getOrderService().getOrder(drugorder.getOrderId()).getOrderer().getPerson();
            OrdererName.put(drugorder.getOrderId(), person.getGivenName()+" "+person.getFamilyName());
        }
            
        /*
          Get the list of all active medication plan drug orders placed for the Patient.
        */
        HashMap<Integer, List<drugorders>> patientPlanOrders = new HashMap<>();
        List<drugorders> drugorders = Context.getService(drugordersService.class).getDrugOrdersByPatientAndStatus(patient, "Active-Plan");
        
        for(drugorders drugorder : drugorders){
            planorders planOrder = Context.getService(planordersService.class).getDrugOrderByOrderID(drugorder.getOrderId());
            
            // If the orders belonging to this standard plan are not retrieved yet, then retrieve the orders
            if(!patientPlanOrders.containsKey(planOrder.getPlanId())){
                List<planorders> planOrders = Context.getService(planordersService.class).getDrugOrdersByPlanID(planOrder.getPlanId());
                List<drugorders> activePlanOrders = new ArrayList<>();
                
                for(planorders plan : planOrders){
                    if(Context.getService(drugordersService.class).getDrugOrderByOrderID(plan.getOrderId()).getOrderStatus().equals("Active-Plan")){
                        activePlanOrders.add(Context.getService(drugordersService.class).getDrugOrderByOrderID(plan.getOrderId()));
                        
                        // If the given order within the selected medication plan is active, save the order and retrieve the orderer contact information
                        Person person = Context.getOrderService().getOrder(drugorder.getOrderId()).getOrderer().getPerson();
                        OrdererName.put(drugorder.getOrderId(), person.getGivenName()+" "+person.getFamilyName());
                    }
                }
                patientPlanOrders.put(planOrder.getPlanId(), activePlanOrders);
                planName.put(planOrder.getPlanId(), planOrder.getDiseaseId());
            }
        }
        
        /*
          Get the list of all active group drug orders placed for the Patient.
        */
        HashMap<Integer, List<drugorders>> patientGroupOrders = new HashMap<>();
        List<drugorders> groupOrders = Context.getService(drugordersService.class).getDrugOrdersByPatientAndStatus(patient, "Active-Group");
        
        for(drugorders groupOrder : groupOrders){
            // If the orders belonging to this order's group are not retrieved yet, then retrieve the orders
            if(!patientGroupOrders.containsKey(groupOrder.getGroupId())){
                List<drugorders> allGroupOrders = Context.getService(drugordersService.class).getDrugOrdersByGroupID(groupOrder.getGroupId());
                List<drugorders> activeGroupOrders = new ArrayList<>();

                for(drugorders drugorder : allGroupOrders){
                    if(drugorder.getOrderStatus().equals("Active-Group")){
                        activeGroupOrders.add(drugorder);
                        
                        // If the given order within the selected order group is active, save the order and retrieve the orderer contact information
                        Person person = Context.getOrderService().getOrder(groupOrder.getOrderId()).getOrderer().getPerson();
                        OrdererName.put(groupOrder.getOrderId(), person.getGivenName()+" "+person.getFamilyName());
                    }
                }
                patientGroupOrders.put(groupOrder.getGroupId(), activeGroupOrders);
            }            
        }
        
        model.addAttribute("patientSingleOrders", patientSingleOrders);
        model.addAttribute("patientGroupOrders", patientGroupOrders);
        model.addAttribute("patientPlanOrders", patientPlanOrders);
        model.addAttribute("planName", planName);
        model.addAttribute("OrdererName", OrdererName);
    }
}
