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
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author harini-geek
 */
public class CurrentDrugOrdersFragmentController {
    
    public void controller(FragmentModel model, @RequestParam("patientId") Patient patient){
        
        // Storing HashMap<Plan-ID, Plan-Name>
        HashMap<Integer, Concept> planName = new HashMap<>();
        // Storing HashMap<Order-ID, Orderer-Name>
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
            planorders planOrder = Context.getService(planordersService.class).getPlanOrderByOrderID(drugorder.getOrderId());
            
            // If the drug orders associated with this standard plan are not retrieved yet, then retrieve the orders.
            if(!patientPlanOrders.containsKey(planOrder.getPlanId())){
                // Retrieve the set of drug orders that are associated with the given planorders ID.
                List<planorders> planOrders = Context.getService(planordersService.class).getPlanOrdersByPlanID(planOrder.getPlanId());
                List<drugorders> activePlanOrders = new ArrayList<>();
                
                // From the set of retrieved drug orders, select the orders that are currently active.
                for(planorders plan : planOrders){
                    if(Context.getService(drugordersService.class).getDrugOrderByOrderID(plan.getOrderId()).getOrderStatus().equals("Active-Plan")){
                        activePlanOrders.add(Context.getService(drugordersService.class).getDrugOrderByOrderID(plan.getOrderId()));
                    }
                }
                // Store the data about the set of drug orders made for a medication plan by the plan ID
                patientPlanOrders.put(planOrder.getPlanId(), activePlanOrders);
                // Store the name of the disease associated with the plan order ID
                planName.put(planOrder.getPlanId(), planOrder.getDiseaseId());
            }
            // Save and retrieve the orderer contact information.
            Person person = Context.getOrderService().getOrder(drugorder.getOrderId()).getOrderer().getPerson();
            OrdererName.put(drugorder.getOrderId(), person.getGivenName()+" "+person.getFamilyName());
        }
        
        /*
          Get the list of all active 'grouped' drug orders placed for the Patient.
        */
        HashMap<Integer, List<drugorders>> patientGroupOrders = new HashMap<>();
        List<drugorders> groupOrders = Context.getService(drugordersService.class).getDrugOrdersByPatientAndStatus(patient, "Active-Group");
        
        for(drugorders groupOrder : groupOrders){
            // If the orders belonging to this order's group are not retrieved yet, then retrieve the orders.
            if(!patientGroupOrders.containsKey(groupOrder.getGroupId())){
                List<drugorders> allGroupOrders = Context.getService(drugordersService.class).getDrugOrdersByGroupID(groupOrder.getGroupId());
                List<drugorders> activeGroupOrders = new ArrayList<>();

                // From the list of orders retrieved, select the orders that are currently active.
                for(drugorders drugorder : allGroupOrders){
                    if(drugorder.getOrderStatus().equals("Active-Group")){
                        activeGroupOrders.add(drugorder);
                    }
                }
                // Store the list of 'grouped' drug orders by the group ID.
                patientGroupOrders.put(groupOrder.getGroupId(), activeGroupOrders);
            }
            // Save and retrieve the orderer contact information.
            Person person = Context.getOrderService().getOrder(groupOrder.getOrderId()).getOrderer().getPerson();
            OrdererName.put(groupOrder.getOrderId(), person.getGivenName()+" "+person.getFamilyName());
        }
        
        model.addAttribute("patientSingleOrders", patientSingleOrders);
        model.addAttribute("patientGroupOrders", patientGroupOrders);
        model.addAttribute("patientPlanOrders", patientPlanOrders);
        model.addAttribute("planName", planName);
        model.addAttribute("OrdererName", OrdererName);
    }
}