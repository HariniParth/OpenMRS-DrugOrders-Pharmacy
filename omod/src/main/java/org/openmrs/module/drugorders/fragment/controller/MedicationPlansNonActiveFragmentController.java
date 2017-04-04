/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders.fragment.controller;

import java.util.HashMap;
import java.util.List;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Patient;
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
public class MedicationPlansNonActiveFragmentController {
    
    public void controller(PageModel model, @RequestParam("patientId") Patient patient){

        //Data structure to store the 'Drug Order' object properties for all the non-active orders for the given disease
        HashMap<Integer, HashMap<Concept, HashMap<Integer, DrugOrder>>> NonActivePlanMain = new HashMap<>();

        //Data structure to store the 'drugorders' object properties for all the non-active orders for the given disease
        HashMap<Integer, HashMap<Concept, HashMap<Integer, drugorders>>> NonActivePlanExtension = new HashMap<>();
        
        List<drugorders> nonActiveMedOrders = Context.getService(drugordersService.class).getDrugOrdersByPatientAndStatus(patient, "Non-Active-Plan");
        
        for(drugorders nonActiveMedOrder : nonActiveMedOrders){
            planorders nonActiveMedPlan = Context.getService(planordersService.class).getDrugOrderByOrderID(nonActiveMedOrder.getOrderId());
            
            if(!NonActivePlanMain.containsKey(nonActiveMedPlan.getPlanId())){
                List<planorders> ordersByPlan = Context.getService(planordersService.class).getDrugOrdersByPlanID(nonActiveMedPlan.getPlanId());
                
                HashMap<Concept, HashMap<Integer, DrugOrder>> planMain = new HashMap<>();
                HashMap<Concept, HashMap<Integer, drugorders>> planExtn = new HashMap<>();
                
                HashMap<Integer,DrugOrder> orderMain = new HashMap<>();
                HashMap<Integer,drugorders> orderExtn = new HashMap<>();
                
                for(planorders orderByPlan : ordersByPlan){
                    int order = orderByPlan.getOrderId();
                    orderMain.put(order, (DrugOrder) Context.getOrderService().getOrder(order));
                    orderExtn.put(order, Context.getService(drugordersService.class).getDrugOrderByOrderID(order));
                }
                
                planMain.put(nonActiveMedPlan.getDiseaseId(), orderMain);
                planExtn.put(nonActiveMedPlan.getDiseaseId(), orderExtn);
                
                NonActivePlanMain.put(nonActiveMedPlan.getPlanId(), planMain);
                NonActivePlanExtension.put(nonActiveMedPlan.getPlanId(), planExtn);
            }
        }
        model.addAttribute("NonActivePlanMain", NonActivePlanMain);
        model.addAttribute("NonActivePlanExtension", NonActivePlanExtension);
    }
}