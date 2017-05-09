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
        HashMap<Integer, HashMap<Concept, HashMap<Integer, drugorders>>> NonActivePlanExtn = new HashMap<>();
        
        /*
          Retrieve the list of non-active medication plan orders.
        */
        List<drugorders> orders = Context.getService(drugordersService.class).getDrugOrdersByPatientAndStatus(patient, "Non-Active-Plan");
        
        for(drugorders order : orders){
            planorders nonActiveMedPlan = Context.getService(planordersService.class).getDrugOrderByOrderID(order.getOrderId());
            
            // If the selected plan related orders are not already retrieved, retrieve the orders
            if(!NonActivePlanMain.containsKey(nonActiveMedPlan.getPlanId())){
                List<planorders> plans = Context.getService(planordersService.class).getDrugOrdersByPlanID(nonActiveMedPlan.getPlanId());
                
                HashMap<Concept, HashMap<Integer, DrugOrder>> p_main = new HashMap<>();
                HashMap<Concept, HashMap<Integer, drugorders>> p_extn = new HashMap<>();
                
                HashMap<Integer,DrugOrder> o_main = new HashMap<>();
                HashMap<Integer,drugorders> o_extn = new HashMap<>();
                
                for(planorders plan : plans){
                    int id = plan.getOrderId();
                    o_main.put(id, (DrugOrder) Context.getOrderService().getOrder(id));
                    o_extn.put(id, Context.getService(drugordersService.class).getDrugOrderByOrderID(id));
                }
                
                p_main.put(nonActiveMedPlan.getDiseaseId(), o_main);
                p_extn.put(nonActiveMedPlan.getDiseaseId(), o_extn);
                
                NonActivePlanMain.put(nonActiveMedPlan.getPlanId(), p_main);
                NonActivePlanExtn.put(nonActiveMedPlan.getPlanId(), p_extn);
            }
        }
        model.addAttribute("NonActivePlanMain", NonActivePlanMain);
        model.addAttribute("NonActivePlanExtn", NonActivePlanExtn);
    }
}