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
public class CurrentGroupOrdersFragmentController {
    
    public void controller(PageModel model, @RequestParam("patientId") Patient patient){
        
        List<drugorders> allOrders = new ArrayList<>();
        /*
          Get the list of all active drug orders placed for the Patient.
          Individual, Group, Plan orders.
        */
        allOrders.addAll(Context.getService(drugordersService.class).getDrugOrdersByPatientAndStatus(patient, "Active"));
        allOrders.addAll(Context.getService(drugordersService.class).getDrugOrdersByPatientAndStatus(patient, "Active-Plan"));
        allOrders.addAll(Context.getService(drugordersService.class).getDrugOrdersByPatientAndStatus(patient, "Active-Group"));
        
        HashMap<Integer, Concept> planName = new HashMap<>();
        HashMap<Integer, String> OrdererName = new HashMap<>();
        List<drugorders> patientSingleOrders = new ArrayList<>();
        HashMap<Integer, List<drugorders>> patientPlanOrders = new HashMap<>();
        HashMap<Integer, List<drugorders>> patientGroupOrders = new HashMap<>();
                        
        for(drugorders order : allOrders){
            switch (order.getOrderStatus()) {
                case "Active":
                    patientSingleOrders.add(order);
                    break;
                case "Active-Group":
                    if(!patientGroupOrders.containsKey(order.getGroupId())){
                        
                        List<drugorders> allGroupOrders = Context.getService(drugordersService.class).getDrugOrdersByGroupID(order.getGroupId());
                        
                        List<drugorders> activeGroupOrders = new ArrayList<>();
                        
                        for(drugorders groupOrder : allGroupOrders){
                            if(groupOrder.getOrderStatus().equals("Active-Group")){
                                activeGroupOrders.add(groupOrder);
                            }
                        }
                        patientGroupOrders.put(order.getGroupId(), activeGroupOrders);
                    }   
                    break;
                case "Active-Plan":
                    planorders planOrder = Context.getService(planordersService.class).getDrugOrderByOrderID(order.getOrderId());
                    if(!patientPlanOrders.containsKey(planOrder.getPlanId())){
                        
                        List<planorders> planOrders = Context.getService(planordersService.class).getDrugOrdersByPlanID(planOrder.getPlanId());
                        
                        List<drugorders> activePlanOrders = new ArrayList<>();
                        
                        for(planorders plan : planOrders){
                            if(Context.getService(drugordersService.class).getDrugOrderByOrderID(plan.getOrderId()).getOrderStatus().equals("Active-Plan")){
                                activePlanOrders.add(Context.getService(drugordersService.class).getDrugOrderByOrderID(plan.getOrderId()));
                            }
                        }
                        
                        patientPlanOrders.put(planOrder.getPlanId(), activePlanOrders);
                        planName.put(planOrder.getPlanId(), planOrder.getDiseaseId());
                    }   
                    break;
            }
            Person person = Context.getOrderService().getOrder(order.getOrderId()).getOrderer().getPerson();
            OrdererName.put(order.getOrderId(), person.getGivenName()+" "+person.getFamilyName());
        }
        
        model.addAttribute("patientSingleOrders", patientSingleOrders);
        model.addAttribute("patientGroupOrders", patientGroupOrders);
        model.addAttribute("patientPlanOrders", patientPlanOrders);
        model.addAttribute("planName", planName);
        model.addAttribute("OrdererName", OrdererName);
    }
}
