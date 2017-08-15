/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders.fragment.controller;

import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.openmrs.DrugOrder;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.drugorders.api.drugordersService;
import org.openmrs.module.drugorders.api.planordersService;
import org.openmrs.module.drugorders.drugorders;
import org.openmrs.module.drugorders.planorders;
import org.openmrs.module.uicommons.util.InfoErrorMessageUtil;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author harini-parthasarathy
 */
public class PlanOrdersActiveFragmentController {
    
    public void controller(FragmentModel model, @RequestParam("patientId") Patient patient, HttpSession session,
                            @RequestParam(value = "activatePlan", required = false) Integer activatePlan){
        
        // Activate saved draft med plan drug orders.
        if(activatePlan != null){
            // Get the list of med plan related drug orders that are currently in 'Draft' status and set the status to 'Active'.
            List<planorders> planOrders = Context.getService(planordersService.class).getPlanOrdersByPlanID(activatePlan);
            
            // Check if Physician has provided instructions to the Patient and to the Pharmacist.
            boolean detailsProvided = true;
            for(planorders order : planOrders){
                drugorders d = Context.getService(drugordersService.class).getDrugOrderByOrderID(order.getOrderId());
                if(d.getPatientInstructions() == null || d.getPharmacistInstructions() == null){
                    detailsProvided = false;
                    InfoErrorMessageUtil.flashErrorMessage(session, "Please update instructions on Order Number "+d.getOrderId());   
                }
            }
            
            if(detailsProvided){
                for(planorders order : planOrders){
                    drugorders d = Context.getService(drugordersService.class).getDrugOrderByOrderID(order.getOrderId());
                    d.setOrderStatus("Active-Plan");
                }
            }
        }
        
        /* 
          =========================================================================================
          To Display the list of medication plan related drug orders, with the status "Active-Plan"
          =========================================================================================        */
        
        /* 
          Data structure to store the 'Drug Order' object properties for all the active orders made for a medication plan
          Storing HashMap<Plan-ID, HashMap<Order-ID, DrugOrder>>
        */
        HashMap<Integer,HashMap<Integer,DrugOrder>> ActivePlanMain = new HashMap <>();
        /* 
          Data structure to store the 'drugorders' object properties for all the active orders made for a medication plan
          Storing HashMap<Plan-ID, HashMap<Order-ID, drugorders>>
        */
        HashMap<Integer,HashMap<Integer,drugorders>> ActivePlanExtn = new HashMap <>();
        
        // Retrieve the list of medication plan related drug orders, having the status "Active-Plan"
        List<drugorders> orders = Context.getService(drugordersService.class).getDrugOrdersByPatientAndStatus(patient, "Active-Plan");
        for(drugorders order : orders){
            // Retrieve the corresponding planorders record.
            planorders p_order = Context.getService(planordersService.class).getPlanOrderByOrderID(order.getOrderId());
            
            // If the selected plan related drug orders are not already retrieved, retrieve the orders and store the objects in ActivePlanMain and ActivePlanExtn HashMap.
            if(!ActivePlanMain.containsKey(p_order.getPlanId())){

                // Storing HashMap<Order-ID, DrugOrder>
                HashMap<Integer,DrugOrder> main = new HashMap<>();
                // Storing HashMap<Order-ID, drugorders>
                HashMap<Integer,drugorders> extn = new HashMap<>();
                
                // Fetch the references to the related drug orders made as a part of the same plan order.
                List<planorders> plans = Context.getService(planordersService.class).getPlanOrdersByPlanID(p_order.getPlanId());

                for(planorders plan : plans){
                    int id = plan.getOrderId();
                    // Select the drug orders that are currently active.
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
        
        
        /* 
          ========================================================================================
          To Display the list of medication plan related drug orders, with the status "Draft-Plan"
          ========================================================================================
        */
        
        /* 
          Data structure to store the 'Drug Order' object properties for all the draft orders made for a medication plan
          Storing HashMap<Plan-ID, HashMap<Order-ID, DrugOrder>>
        */
        HashMap<Integer,HashMap<Integer,DrugOrder>> DraftPlanMain = new HashMap <>();
        /* 
          Data structure to store the 'drugorders' object properties for all the draft orders made for a medication plan
          Storing HashMap<Plan-ID, HashMap<Order-ID, drugorders>>
        */
        HashMap<Integer,HashMap<Integer,drugorders>> DraftPlanExtn = new HashMap <>();
        
        // Retrieve the list of medication plan related drug orders, with the status "Draft-Plan"
        orders = Context.getService(drugordersService.class).getDrugOrdersByPatientAndStatus(patient, "Draft-Plan");
        for(drugorders order : orders){
            planorders p_order = Context.getService(planordersService.class).getPlanOrderByOrderID(order.getOrderId());

            // If the selected plan related orders are not already retrieved, retrieve the orders
            if(!DraftPlanMain.containsKey(p_order.getPlanId())){

                // Storing HashMap<Order-ID, DrugOrder>
                HashMap<Integer,DrugOrder> main = new HashMap<>();
                // Storing HashMap<Order-ID, drugorders>
                HashMap<Integer,drugorders> extn = new HashMap<>();
                
                // Fetch the references to the related drug orders made as a part of the same plan order.
                List<planorders> plans = Context.getService(planordersService.class).getPlanOrdersByPlanID(p_order.getPlanId());

                for(planorders plan : plans){
                    int id = plan.getOrderId();
                    // Select the drug orders that are currently in draft status.
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