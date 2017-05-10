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
import java.util.Calendar;
import java.util.Date;
import org.openmrs.api.context.Context;
import org.openmrs.module.drugorders.api.drugordersService;
import org.openmrs.module.drugorders.api.planordersService;
import org.openmrs.module.drugorders.drugorders;
import org.openmrs.module.drugorders.planorders;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author harini-geek
 */
public class SelectedOrdersViewFragmentController {
    
    public void controller(PageModel model, @RequestParam("patientId") Patient patient,
                            @RequestParam(value = "planID", required = false) String planID,
                            @RequestParam(value = "groupID", required = false) String groupID,
                            @RequestParam(value = "orderID", required = false) String orderID){
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1);
        Date expiryDate = cal.getTime();
        model.addAttribute("expiryDate", expiryDate);
        
        HashMap<Integer,String> provider = new HashMap<>();
        HashMap<Integer,DrugOrder> groupOrderMain = new HashMap<>();
        HashMap<Integer,drugorders> groupOrderExtn = new HashMap<>();
        
        StringBuilder sb = new StringBuilder();
        if(!planID.equals("")){
            
            //Get the list of Med Plan Orders ordered for this Patient to treat this Disease
            List<planorders> plans = Context.getService(planordersService.class).getDrugOrdersByPlanID(Integer.parseInt(planID));
            sb.append(plans.get(0).getDiseaseId().getDisplayString());
            
            for(planorders plan : plans){
                
                drugorders dorder = Context.getService(drugordersService.class).getDrugOrderByOrderID(plan.getOrderId());
                if(dorder.getOrderStatus().equals("Active-Plan")){
                    groupOrderExtn.put(dorder.getOrderId(), dorder);
                    DrugOrder DOrder = (DrugOrder) Context.getOrderService().getOrder(dorder.getOrderId());
                    groupOrderMain.put(dorder.getOrderId(), DOrder);
                    provider.put(dorder.getOrderId(), DOrder.getOrderer().getPerson().getGivenName() + " " + DOrder.getOrderer().getPerson().getFamilyName() + ", " + StringUtils.capitalize(DOrder.getOrderer().getIdentifier()));                    
                }
            }
        }
        
        if(!groupID.equals("")){
            //Fetch the list of Drug Orders associated with the given Group Order ID
            List<drugorders> drugorders = Context.getService(drugordersService.class).getDrugOrdersByGroupID(Integer.parseInt(groupID));
            
            for(drugorders drugorder : drugorders){
                
                if(drugorder.getOrderStatus().equals("Active-Group")){
                    
                    groupOrderExtn.put(drugorder.getOrderId(), drugorder);
                    DrugOrder DrugOrder = (DrugOrder) Context.getOrderService().getOrder(drugorder.getOrderId());
                    groupOrderMain.put(DrugOrder.getOrderId(), DrugOrder);
                    provider.put(DrugOrder.getOrderId(), DrugOrder.getOrderer().getPerson().getGivenName() + " " + DrugOrder.getOrderer().getPerson().getFamilyName() + ", " + StringUtils.capitalize(DrugOrder.getOrderer().getIdentifier()));
                }
            }
        }
        
        if(!orderID.equals("")){
            //Fetch the Drug Order associated with the given Order ID
            int order = Integer.parseInt(orderID);
            drugorders drugorder = Context.getService(drugordersService.class).getDrugOrderByOrderID(order);
            groupOrderExtn.put(order, drugorder);
            
            DrugOrder DrugOrder = (DrugOrder) Context.getOrderService().getOrder(order);
            groupOrderMain.put(order, DrugOrder);
            
            provider.put(DrugOrder.getOrderId(), DrugOrder.getOrderer().getPerson().getGivenName() + " " + DrugOrder.getOrderer().getPerson().getFamilyName() + ", " + StringUtils.capitalize(DrugOrder.getOrderer().getIdentifier()));
        }
        
        model.addAttribute("planID", sb.toString());
        model.addAttribute("groupID", groupID);
        model.addAttribute("orderID", orderID);
        
        model.addAttribute("groupOrderMain", groupOrderMain);
        model.addAttribute("groupOrderExtn", groupOrderExtn);
        model.addAttribute("provider", provider);
        
    }
}