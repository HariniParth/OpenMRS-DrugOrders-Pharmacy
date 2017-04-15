/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders.fragment.controller;

import java.util.HashMap;
import java.util.List;
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
    
    public void controller(PageModel model, @RequestParam("patientId") Patient patient,
                            @RequestParam(value = "activatePlan", required = false) Integer activatePlan){
        
        //Activate saved draft med plans
        if(activatePlan != null){
            List<planorders> planOrders = Context.getService(planordersService.class).getDrugOrdersByPlanID(activatePlan);
            for(planorders order : planOrders)
                Context.getService(drugordersService.class).getDrugOrderByOrderID(order.getOrderId()).setOrderStatus("Active-Plan");
        }
        
        //Data structure to store the 'Drug Order' object properties for all the active orders for the given disease
        HashMap<Integer,HashMap<Integer,DrugOrder>> ActivePlanMain = new HashMap <>();
        //Data structure to store the 'drugorders' object properties for all the active orders for the given disease
        HashMap<Integer,HashMap<Integer,drugorders>> ActivePlanExtn = new HashMap <>();
        
        List<drugorders> orders = Context.getService(drugordersService.class).getDrugOrdersByPatientAndStatus(patient, "Active-Plan");
        for(drugorders order : orders){
            planorders p_order = Context.getService(planordersService.class).getDrugOrderByOrderID(order.getOrderId());
            
            if(!ActivePlanMain.containsKey(p_order.getPlanId())){
                
                HashMap<Integer,DrugOrder> main = new HashMap<>();
                HashMap<Integer,drugorders> extn = new HashMap<>();
                List<planorders> plans = Context.getService(planordersService.class).getDrugOrdersByPlanID(p_order.getPlanId());
                
                for(planorders plan : plans){
                    int id = plan.getOrderId();
                    if(Context.getService(drugordersService.class).getDrugOrderByOrderID(id).getOrderStatus().equals("Active-Plan")){
                        main.put(id, (DrugOrder) Context.getOrderService().getOrder(id));
                        extn.put(id, Context.getService(drugordersService.class).getDrugOrderByOrderID(id));
                    }
                }
                
                ActivePlanMain.put(p_order.getPlanId(), main);
                ActivePlanExtn.put(p_order.getPlanId(), extn);
            }
        }
            
        model.addAttribute("ActivePlanMain", ActivePlanMain);
        model.addAttribute("ActivePlanExtn", ActivePlanExtn);
        
        
        //Data structure to store the 'Drug Order' object properties for all the draft orders for the given disease
        HashMap<Integer,HashMap<Integer,DrugOrder>> DraftPlanMain = new HashMap <>();
        //Data structure to store the 'drugorders' object properties for all the draft orders for the given disease
        HashMap<Integer,HashMap<Integer,drugorders>> DraftPlanExtn = new HashMap <>();
        
        orders = Context.getService(drugordersService.class).getDrugOrdersByPatientAndStatus(patient, "Draft-Plan");
        for(drugorders order : orders){
            planorders p_order = Context.getService(planordersService.class).getDrugOrderByOrderID(order.getOrderId());
            
            if(!DraftPlanMain.containsKey(p_order.getPlanId())){
                
                HashMap<Integer,DrugOrder> main = new HashMap<>();
                HashMap<Integer,drugorders> extn = new HashMap<>();
                List<planorders> plans = Context.getService(planordersService.class).getDrugOrdersByPlanID(p_order.getPlanId());
                
                for(planorders plan : plans){
                    int id = plan.getOrderId();
                    if(Context.getService(drugordersService.class).getDrugOrderByOrderID(id).getOrderStatus().equals("Draft-Plan")){
                        main.put(id, (DrugOrder) Context.getOrderService().getOrder(id));
                        extn.put(id, Context.getService(drugordersService.class).getDrugOrderByOrderID(id));
                    }
                }
                
                DraftPlanMain.put(p_order.getPlanId(), main);
                DraftPlanExtn.put(p_order.getPlanId(), extn);
            }
        }
            
        model.addAttribute("DraftPlanMain", DraftPlanMain);
        model.addAttribute("DraftPlanExtn", DraftPlanExtn);
    }
}