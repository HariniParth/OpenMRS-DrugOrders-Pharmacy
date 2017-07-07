/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders.fragment.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.DrugOrder;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.drugorders.api.drugordersService;
import org.openmrs.module.drugorders.drugorders;
import org.apache.commons.lang.StringUtils;
import org.openmrs.api.APIException;
import org.openmrs.module.drugorders.api.planordersService;
import org.openmrs.module.drugorders.planorders;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.util.ByFormattedObjectComparator;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author harini-parthasarathy
 */
public class EditDrugOrdersFragmentController {
        
    public void controller(FragmentModel model,@RequestParam("patientId") Patient patient, UiUtils ui,
                            @RequestParam(value = "selectedActivePlan", required = false) Integer selectedActivePlan,
                            @RequestParam(value = "selectedNonActivePlan", required = false) String selectedNonActivePlan,
                            @RequestParam(value = "selectedActiveGroup", required = false) String selectedActiveGroup,
                            @RequestParam(value = "selectedNonActiveGroup", required = false) String selectedNonActiveGroup,
                            @RequestParam(value = "selectedActiveItem", required = false) Integer selectedActiveItem,
                            @RequestParam(value = "selectedActiveOrder", required = false) String selectedActiveOrder){

        // Data structure to store data from the drug_orders table
        HashMap<Integer,DrugOrder> groupMain = new HashMap<>();
        // Data structure to store data from the drug_order_extn table
        HashMap<Integer,drugorders> groupExtn = new HashMap<>();
        // To sort the list of concepts by their name
        Comparator comparator = new ByFormattedObjectComparator(ui);
        
        /*
          Fetch list of concepts that identify the reason to discontinue orders.
        */
        ConceptClass reasonConcept = Context.getConceptService().getConceptClassByName("Discontinue Order Reasons");
        List<Concept> discontinueReasons = Context.getConceptService().getConceptsByClass(reasonConcept);
        Collections.sort(discontinueReasons, comparator);
        model.addAttribute("discontinueReasons", discontinueReasons);
               
        /*
          Discontinue orders in the selected active group.
        */
        if(StringUtils.isNotBlank(selectedActiveGroup)){
            try {
                // Retrieve the group ID of the group to be discontinued.
                int group = Integer.parseInt(selectedActiveGroup);
                // Retrieve the list of all drug orders in the selected group.
                List<drugorders> groupOrders = Context.getService(drugordersService.class).getDrugOrdersByGroupID(group);
                for(drugorders groupOrder: groupOrders){
                    if(groupOrder.getOrderStatus().equals("Active-Group")){
                        // Store the order ID as key and DrugOrder as value.
                        groupMain.put(groupOrder.getOrderId(), (DrugOrder) Context.getOrderService().getOrder(groupOrder.getOrderId()));
                        // Store the order ID as key and drugorders as value.
                        groupExtn.put(groupOrder.getOrderId(), Context.getService(drugordersService.class).getDrugOrderByOrderID(groupOrder.getOrderId()));
                    }
                }
                model.addAttribute("group", group);
                model.addAttribute("groupOrderAction", "DISCARD ORDER GROUP");
                
            } catch(NumberFormatException | APIException ex){
                Logger.getLogger(EditDrugOrdersFragmentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        /*
          Renew orders from the selected non-active group.
        */
        if(StringUtils.isNotBlank(selectedNonActiveGroup)){
            try {
                // Retrieve the group ID of the group to be renewed.
                int group = Integer.parseInt(selectedNonActiveGroup);
                // Retrieve the list of all drug orders in the selected group.
                List<drugorders> groupOrders = Context.getService(drugordersService.class).getDrugOrdersByGroupID(group);
                for(drugorders groupOrder: groupOrders){
                    // Store the order ID as key and DrugOrder as value.
                    groupMain.put(groupOrder.getOrderId(), (DrugOrder) Context.getOrderService().getOrder(groupOrder.getOrderId()));
                    // Store the order ID as key and drugorders as value.
                    groupExtn.put(groupOrder.getOrderId(), Context.getService(drugordersService.class).getDrugOrderByOrderID(groupOrder.getOrderId()));
                }
                model.addAttribute("group", group);
                model.addAttribute("groupOrderAction", "RENEW ORDER GROUP");
                
            } catch(NumberFormatException | APIException ex){
                Logger.getLogger(EditDrugOrdersFragmentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
                
        /*
          Discontinue orders in the selected active plan.
        */
        if(selectedActivePlan != null){
            try {
                // Retrieve the plan ID of the med plan orders to be discontinued.
                int ID = selectedActivePlan;
                // Retrieve the list of all drug orders in the selected med plan order.
                List<planorders> planOrders = Context.getService(planordersService.class).getPlanOrdersByPlanID(ID);
                
                for(planorders planOrder: planOrders){
                    if(Context.getService(drugordersService.class).getDrugOrderByOrderID(planOrder.getOrderId()).getOrderStatus().equals("Active-Plan") || Context.getService(drugordersService.class).getDrugOrderByOrderID(planOrder.getOrderId()).getOrderStatus().equals("Draft-Plan")){
                        // Store the order ID as key and DrugOrder as value.
                        groupMain.put(planOrder.getOrderId(), (DrugOrder) Context.getOrderService().getOrder(planOrder.getOrderId()));
                        // Store the order ID as key and drugorders as value.
                        groupExtn.put(planOrder.getOrderId(), Context.getService(drugordersService.class).getDrugOrderByOrderID(planOrder.getOrderId()));
                    }
                }
                // Store the name of the plan
                model.addAttribute("plan", planOrders.get(0).getDiseaseId().getDisplayString().toUpperCase());
                // Store the name of the plan ID
                model.addAttribute("group", ID);
                model.addAttribute("groupOrderAction", "DISCARD MED PLAN");
                
            } catch(NumberFormatException | APIException ex){
                Logger.getLogger(EditDrugOrdersFragmentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
                        
        /*
          Renew orders from the selected non-active plan.
        */
        if(StringUtils.isNotBlank(selectedNonActivePlan)){
            try {
                // Retrieve the plan ID of the med plan orders to be renewed.
                int ID = Integer.parseInt(selectedNonActivePlan);
                // Retrieve the list of all drug orders in the selected med plan order.
                List<planorders> planOrders = Context.getService(planordersService.class).getPlanOrdersByPlanID(ID);
                Concept planConcept = planOrders.get(0).getDiseaseId();
                
                for(planorders planOrder : planOrders){
                    // Store the order ID as key and DrugOrder as value.
                    groupMain.put(planOrder.getOrderId(), (DrugOrder) Context.getOrderService().getOrder(planOrder.getOrderId()));
                    // Store the order ID as key and drugorders as value.
                    groupExtn.put(planOrder.getOrderId(), Context.getService(drugordersService.class).getDrugOrderByOrderID(planOrder.getOrderId()));
                }
                // Store the name of the plan
                model.addAttribute("plan", planConcept.getDisplayString().toUpperCase());
                // Store the name of the plan ID
                model.addAttribute("group", ID);
                model.addAttribute("groupOrderAction", "RENEW MED PLAN");
                
            } catch(NumberFormatException | APIException ex){
                Logger.getLogger(EditDrugOrdersFragmentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
                
        /*
          Discontinue selected order
        */
        if(StringUtils.isNotBlank(selectedActiveOrder) || selectedActiveItem != null){
            try {
                int id = 0;
                // Check if the order selected to be discontinued is an individual drug order or an order from a group / med plan.
                if(StringUtils.isNotBlank(selectedActiveOrder))
                    id = Integer.parseInt(selectedActiveOrder);
                else if(selectedActiveItem != null)
                    id = selectedActiveItem;
                
                // Store the order ID as key and DrugOrder as value.
                groupMain.put(id, (DrugOrder) Context.getOrderService().getOrder(id));
                // Store the order ID as key and drugorders as value.
                groupExtn.put(id, Context.getService(drugordersService.class).getDrugOrderByOrderID(id));
                
                model.addAttribute("group", id);
                model.addAttribute("orderStatus", Context.getService(drugordersService.class).getDrugOrderByOrderID(id).getOrderStatus());
                model.addAttribute("groupOrderAction", "DISCONTINUE ORDER");
                
            } catch(NumberFormatException | APIException ex){
                Logger.getLogger(EditDrugOrdersFragmentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        model.addAttribute("groupMain", groupMain);
        model.addAttribute("groupExtn", groupExtn);
    }
}