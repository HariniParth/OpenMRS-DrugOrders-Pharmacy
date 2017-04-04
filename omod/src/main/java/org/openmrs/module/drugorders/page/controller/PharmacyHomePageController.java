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
 * @author harini-geek
 */
public class PharmacyHomePageController {
    
    public void controller(PageModel model, @RequestParam(value = "patient_full_name", required = false) String patient_full_name,
                                            @RequestParam(value = "homeCheckbox", required=false) String[] homeCheckbox){
        
        List<Patient> allPatients = Context.getPatientService().getAllPatients();
        
        String searchPatient = patient_full_name.trim();
        
        if(!searchPatient.equals("")){
            for(Patient patient : allPatients){
                if((patient.getGivenName()+" "+patient.getFamilyName()).equals(searchPatient)){
                    model.addAttribute("patient", patient);
                }
            }
        } else {
            model.addAttribute("patient", "");
        }
        
        List<Integer> listOfOrders = new ArrayList<>();
        
        if(homeCheckbox.length > 0){
            for (String box : homeCheckbox) {
                String[] selected = box.split(" ");
                
                switch (selected[0]) {
                    case "PLAN":
                        List<planorders> planOrders = Context.getService(planordersService.class).getDrugOrdersByPlanID(Integer.parseInt(selected[1]));
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
            
            for (int orderID : listOfOrders) {
                drugorders drugorder = Context.getService(drugordersService.class).getDrugOrderByOrderID(orderID);
                if(drugorder.getOnHold() == 1)
                    drugorder.setOnHold(0);
            } 
        }                     
        
        List<drugorders> ordersOnHold = Context.getService(drugordersService.class).getOrdersOnHold();
        List<drugorders> ordersForDiscard = Context.getService(drugordersService.class).getOrdersForDiscard();
        
        List<drugorders> patientSingleOrders = new ArrayList<>();
        HashMap<Integer, List<drugorders>> patientGroupOrders = new HashMap<>();
        HashMap<Integer, List<drugorders>> patientPlanOrders = new HashMap<>();
        
        HashMap<Integer,String> ordererName = new HashMap<>();
        HashMap<Integer,String> patientName = new HashMap<>();
        
        for(drugorders order: Iterables.concat(ordersOnHold, ordersForDiscard)){
            Person physician = Context.getPersonService().getPerson(Context.getOrderService().getOrder(order.getOrderId()).getOrderer().getProviderId());
                        
            switch (order.getOrderStatus()) {
                case "Active":
                    patientSingleOrders.add(order);
                    ordererName.put(order.getOrderId(), physician.getGivenName()+" "+physician.getFamilyName());
                    break;
                    
                case "Active-Group":
                    if(!patientGroupOrders.containsKey(order.getGroupId())){
                        
                        List<drugorders> allGroupOrders = Context.getService(drugordersService.class).getDrugOrdersByGroupID(order.getGroupId());
                        
                        List<drugorders> activeGroupOrders = new ArrayList<>();
                        
                        for(drugorders groupOrder : allGroupOrders){
                            if(groupOrder.getOrderStatus().equals("Active-Group")){
                                activeGroupOrders.add(groupOrder);
                                ordererName.put(groupOrder.getOrderId(), physician.getGivenName()+" "+physician.getFamilyName());
                            }
                        }
                        patientGroupOrders.put(order.getGroupId(), activeGroupOrders);
                    }   break;
                    
                case "Active-Plan":
                    planorders planOrder = Context.getService(planordersService.class).getDrugOrderByOrderID(order.getOrderId());
                    if(!patientPlanOrders.containsKey(planOrder.getPlanId())){
                        
                        List<planorders> planOrders = Context.getService(planordersService.class).getDrugOrdersByPlanID(planOrder.getPlanId());
                        
                        List<drugorders> activePlanOrders = new ArrayList<>();
                        
                        for(planorders plan : planOrders){
                            if(Context.getService(drugordersService.class).getDrugOrderByOrderID(plan.getOrderId()).getOrderStatus().equals("Active-Plan")){
                                activePlanOrders.add(Context.getService(drugordersService.class).getDrugOrderByOrderID(plan.getOrderId()));
                                ordererName.put(plan.getOrderId(), physician.getGivenName()+" "+physician.getFamilyName());
                            }
                        }
                    patientPlanOrders.put(planOrder.getPlanId(), activePlanOrders);
                }   break;
            }
            
            //Store the list of Patient's name who have an Order that is On-Hold or For-Discard
            if(!patientName.containsKey(order.getPatientId())){
                Patient patient = Context.getPatientService().getPatient(order.getPatientId());
                patientName.put(order.getPatientId(), patient.getGivenName()+" "+patient.getFamilyName());
            }
        }
        model.addAttribute("patientName", patientName);
        model.addAttribute("ordererName", ordererName);
        model.addAttribute("patientSingles", patientSingleOrders);
        model.addAttribute("patientGroups", patientGroupOrders);
        model.addAttribute("patientPlans", patientPlanOrders);
    }
}
