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
public class MedicationPlansFragmentController {
    
    public void controller(PageModel model, @RequestParam("patientId") Patient patient){
        
        //Data structure to store the 'Drug Order' object properties for all the active orders for the given disease
        HashMap<Concept,HashMap<Integer,DrugOrder>> ActivePlanMain = new HashMap <>();
        
        //Data structure to store the 'drugorders' object properties for all the active orders for the given disease
        HashMap <Concept,HashMap<Integer,drugorders>> ActivePlanExtension = new HashMap <>();
        
        List<drugorders> activeMedOrders = Context.getService(drugordersService.class).getDrugOrdersByPatientAndStatus(patient, "Active-Plan");
        
        for(drugorders activeMedOrder : activeMedOrders){
            planorders activeMedPlan = Context.getService(planordersService.class).getDrugOrderByOrderID(activeMedOrder.getOrderId());
            
            if(!ActivePlanMain.containsKey(activeMedPlan.getDiseaseId())){
                
                HashMap<Integer,DrugOrder> drugOrderMain = new HashMap<>();
                HashMap<Integer,drugorders> drugOrderExtension = new HashMap<>();
                List<planorders> ordersForPlan = Context.getService(planordersService.class).getDrugOrdersByPlanID(activeMedPlan.getPlanId());
                
                for(planorders orderPlan : ordersForPlan){
                    int order = orderPlan.getOrderId();
                    if(Context.getService(drugordersService.class).getDrugOrderByOrderID(order).getOrderStatus().equals("Active-Plan")){
                        drugOrderMain.put(order, (DrugOrder) Context.getOrderService().getOrder(order));
                        drugOrderExtension.put(order, Context.getService(drugordersService.class).getDrugOrderByOrderID(order));
                    }
                }
                
                ActivePlanMain.put(activeMedPlan.getDiseaseId(), drugOrderMain);
                ActivePlanExtension.put(activeMedPlan.getDiseaseId(), drugOrderExtension);
            }
        }
            
        model.addAttribute("ActivePlanMain", ActivePlanMain);
        model.addAttribute("ActivePlanExtension", ActivePlanExtension);
    }
}
