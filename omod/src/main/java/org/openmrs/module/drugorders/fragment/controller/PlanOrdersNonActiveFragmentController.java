/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders.fragment.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.openmrs.CareSetting;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.drugorders.api.drugordersService;
import org.openmrs.module.drugorders.api.planordersService;
import org.openmrs.module.drugorders.drugorders;
import org.openmrs.module.drugorders.planorders;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author harini-parthasarathy
 */
public class PlanOrdersNonActiveFragmentController {
    
    public void controller(FragmentModel model, @RequestParam("patientId") Patient patient){

        // Get the records for CareSetting 'Outpatient'.
        CareSetting careSetting = Context.getOrderService().getCareSettingByName("Outpatient");
        // Get the records for OrderType 'Drug Order'
        OrderType orderType = Context.getOrderService().getOrderTypeByName("Drug Order");
        // Get the list of all Orders for the Patient.
        List<Order> orders = Context.getOrderService().getOrders(patient, careSetting, orderType, true);
        
        /* 
          =============================================================================================
          To Display the list of medication plan related drug orders, with the status "Non-Active-Plan"
          =============================================================================================
        */
        
        /* 
          Data structure to store the 'Drug Order' object properties for all the non-active orders for the given disease
          Storing HashMap<Plan-ID, HashMap<Plan-Name, HashMap<Order-ID, DrugOrder>>>
        */
        HashMap<Integer, HashMap<Concept, HashMap<Integer, DrugOrder>>> NonActivePlanMain = new HashMap<>();
        /* 
          Data structure to store the 'drugorders' object properties for all the non-active orders for the given disease
          Storing HashMap<Plan-ID, HashMap<Plan-Name, HashMap<Order-ID, drugorders>>>
        */
        HashMap<Integer, HashMap<Concept, HashMap<Integer, drugorders>>> NonActivePlanExtn = new HashMap<>();
        // Store the list of Orders records.
        HashMap<Integer, Order> Orders = new HashMap<>();
        // Store the list of Orderer records.
        HashMap<Integer, String> Orderer = new HashMap<>();
        
        // Retrieve the list of medication plan related drug orders, having the status "Non-Active-Plan"
        
        List<drugorders> drugorders = new ArrayList<>();        
        for(Order order : orders){
            // Store the list of Order records
            Orders.put(order.getOrderId(), Context.getOrderService().getOrder(order.getOrderId()));
            // Store the list of Orderer name records
            Orderer.put(order.getOrderId(), Context.getOrderService().getOrder(order.getOrderId()).getCreator().getGivenName() + " " + Context.getOrderService().getOrder(order.getOrderId()).getCreator().getFamilyName());
            
            if(Context.getService(drugordersService.class).getDrugOrderByOrderID(order.getOrderId()).getOrderStatus().equals("Non-Active-Plan")){
                drugorders.add(Context.getService(drugordersService.class).getDrugOrderByOrderID(order.getOrderId()));
            }
        }
        
        for(drugorders order : drugorders){
            // Retrieve the corresponding planorders record.
            planorders nonActiveMedPlan = Context.getService(planordersService.class).getPlanOrderByOrderID(order.getOrderId());
            
            // If the selected plan related orders are not already retrieved, retrieve the orders
            if(!NonActivePlanMain.containsKey(nonActiveMedPlan.getStandardPlanId())){
                List<planorders> plans = Context.getService(planordersService.class).getPlanOrdersByPlanID(nonActiveMedPlan.getId());
                
                // Storing HashMap<Plan-Name, HashMap<Order-ID, DrugOrder>>
                HashMap<Concept, HashMap<Integer, DrugOrder>> p_main = new HashMap<>();
                // Storing HashMap<Plan-Name, HashMap<Order-ID, drugorders>>
                HashMap<Concept, HashMap<Integer, drugorders>> p_extn = new HashMap<>();
                
                // Storing HashMap<Order-ID, DrugOrder>
                HashMap<Integer,DrugOrder> o_main = new HashMap<>();
                // Storing HashMap<Order-ID, drugorders>
                HashMap<Integer,drugorders> o_extn = new HashMap<>();
                
                for(planorders plan : plans){
                    int id = plan.getOrderId();
                    if(Context.getService(drugordersService.class).getDrugOrderByOrderID(id).getOrderStatus().equals("Non-Active-Plan")){
                        o_main.put(id, (DrugOrder) Context.getOrderService().getOrder(id));
                        o_extn.put(id, Context.getService(drugordersService.class).getDrugOrderByOrderID(id));
                    }
                }
                
                p_main.put(Context.getService(drugordersService.class).getDrugOrderByOrderID(nonActiveMedPlan.getOrderId()).getAssociatedDiagnosis(), o_main);
                p_extn.put(Context.getService(drugordersService.class).getDrugOrderByOrderID(nonActiveMedPlan.getOrderId()).getAssociatedDiagnosis(), o_extn);
                
                NonActivePlanMain.put(nonActiveMedPlan.getStandardPlanId(), p_main);
                NonActivePlanExtn.put(nonActiveMedPlan.getStandardPlanId(), p_extn);
            }
        }
        model.addAttribute("NonActivePlanMain", NonActivePlanMain);
        model.addAttribute("NonActivePlanExtn", NonActivePlanExtn);
        model.addAttribute("Orders", Orders);
        model.addAttribute("Orderer", Orderer);
    }
}