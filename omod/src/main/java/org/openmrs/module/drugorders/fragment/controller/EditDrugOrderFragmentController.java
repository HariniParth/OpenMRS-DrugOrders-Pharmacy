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
import org.openmrs.ConceptClass;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.drugorders.api.drugordersService;
import org.openmrs.module.drugorders.drugorders;
import org.openmrs.ui.framework.page.PageModel;
import org.apache.commons.lang.StringUtils;
import org.openmrs.api.APIException;
import org.openmrs.module.drugorders.api.planordersService;
import org.openmrs.module.drugorders.planorders;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author harini-geek
 */
public class EditDrugOrderFragmentController {
        
    public void controller(PageModel model,@RequestParam("patientId") Patient patient,
                            @RequestParam(value = "selectedActivePlan", required = false) String selectedActivePlan,
                            @RequestParam(value = "selectedNonActivePlan", required = false) String selectedNonActivePlan,
                            @RequestParam(value = "selectedActiveGroup", required = false) String selectedActiveGroup,
                            @RequestParam(value = "selectedNonActiveGroup", required = false) String selectedNonActiveGroup,
                            @RequestParam(value = "selectedActiveItem", required = false) String selectedActiveItem,
                            @RequestParam(value = "selectedActiveOrder", required = false) String selectedActiveOrder,
                            @RequestParam(value = "associatedDiagnosis", required = false) String associatedDiagnosis){

        HashMap<Integer,DrugOrder> groupMain = new HashMap<>();
        HashMap<Integer,drugorders> groupExtn = new HashMap<>();
        
        model.addAttribute("associatedDiagnosis", associatedDiagnosis);
        
        ConceptClass reasonConcept = Context.getConceptService().getConceptClassByName("Discontinue Order Reasons");
        List<Concept> discontinueReasons = Context.getConceptService().getConceptsByClass(reasonConcept);
        model.addAttribute("discontinueReasons", discontinueReasons);
               
        if(StringUtils.isNotBlank(selectedActiveGroup)){
            try {
                int group = Integer.parseInt(selectedActiveGroup);
                List<drugorders> groupOrders = Context.getService(drugordersService.class).getDrugOrdersByGroupID(group);
                for(drugorders groupOrder: groupOrders){
                    groupMain.put(groupOrder.getOrderId(), (DrugOrder) Context.getOrderService().getOrder(groupOrder.getOrderId()));
                    groupExtn.put(groupOrder.getOrderId(), Context.getService(drugordersService.class).getDrugOrderByOrderID(groupOrder.getOrderId()));
                }
                model.addAttribute("group", group);
                model.addAttribute("groupOrderAction", "DISCARD ORDER GROUP");
                
            } catch(NumberFormatException | APIException e){
                System.out.println(e.toString());
            }
        }
        
        if(StringUtils.isNotBlank(selectedNonActiveGroup)){
            try {
                int group = Integer.parseInt(selectedNonActiveGroup);
                List<drugorders> groupOrders = Context.getService(drugordersService.class).getDrugOrdersByGroupID(group);
                for(drugorders groupOrder: groupOrders){
                    groupMain.put(groupOrder.getOrderId(), (DrugOrder) Context.getOrderService().getOrder(groupOrder.getOrderId()));
                    groupExtn.put(groupOrder.getOrderId(), Context.getService(drugordersService.class).getDrugOrderByOrderID(groupOrder.getOrderId()));
                }
                model.addAttribute("group", group);
                model.addAttribute("groupOrderAction", "RENEW ORDER GROUP");
                
            } catch(NumberFormatException | APIException e){
                System.out.println(e.toString());
            }
        }
                        
        if(StringUtils.isNotBlank(selectedActivePlan)){
            try {
                int group = 0;
                Concept planConcept = Context.getConceptService().getConceptByName(selectedActivePlan);
                List<drugorders> planOrders = Context.getService(drugordersService.class).getDrugOrdersByPatientAndStatus(patient, "Active-Plan");
                
                for(drugorders planOrder: planOrders){
                    if(planOrder.getAssociatedDiagnosis() == planConcept){
                        groupMain.put(planOrder.getOrderId(), (DrugOrder) Context.getOrderService().getOrder(planOrder.getOrderId()));
                        groupExtn.put(planOrder.getOrderId(), Context.getService(drugordersService.class).getDrugOrderByOrderID(planOrder.getOrderId()));
                        if(group == 0)
                            group = Context.getService(planordersService.class).getDrugOrderByOrderID(planOrder.getOrderId()).getPlanId();
                    }
                }
                model.addAttribute("plan", planConcept.getDisplayString().toUpperCase());
                model.addAttribute("group", group);
                model.addAttribute("groupOrderAction", "DISCARD MED PLAN");
                
            } catch(NumberFormatException | APIException e){
                System.out.println(e.toString());
            }
        }
                        
        if(StringUtils.isNotBlank(selectedNonActivePlan)){
            try {
                int group = Integer.parseInt(selectedNonActivePlan);
                List<planorders> planOrders = Context.getService(planordersService.class).getDrugOrdersByPlanID(group);
                Concept planConcept = planOrders.get(0).getDiseaseId();
                
                for(planorders planOrder : planOrders){
                    groupMain.put(planOrder.getOrderId(), (DrugOrder) Context.getOrderService().getOrder(planOrder.getOrderId()));
                    groupExtn.put(planOrder.getOrderId(), Context.getService(drugordersService.class).getDrugOrderByOrderID(planOrder.getOrderId()));
                }
                model.addAttribute("plan", planConcept.getDisplayString().toUpperCase());
                model.addAttribute("group", group);
                model.addAttribute("groupOrderAction", "RENEW MED PLAN");
                
            } catch(NumberFormatException | APIException e){
                System.out.println(e.toString());
            }
        }
                
        if(StringUtils.isNotBlank(selectedActiveOrder) || StringUtils.isNotBlank(selectedActiveItem)){
            try {
                int group = 0;
                if(StringUtils.isNotBlank(selectedActiveOrder))
                    group = Integer.parseInt(selectedActiveOrder);
                else if(StringUtils.isNotBlank(selectedActiveItem))
                    group = Integer.parseInt(selectedActiveItem);
                
                groupMain.put(group, (DrugOrder) Context.getOrderService().getOrder(group));
                groupExtn.put(group, Context.getService(drugordersService.class).getDrugOrderByOrderID(group));
                
                model.addAttribute("group", group);
                model.addAttribute("groupOrderAction", "DISCONTINUE ORDER");
                
            } catch(NumberFormatException | APIException e){
                System.out.println(e.toString());
            }
        }
        
        model.addAttribute("groupMain", groupMain);
        model.addAttribute("groupExtn", groupExtn);
    }
}