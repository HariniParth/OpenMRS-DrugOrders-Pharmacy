/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders.page.controller;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.OrderFrequency;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.drugorders.api.newplansService;
import org.openmrs.module.drugorders.api.standardplansService;
import org.openmrs.module.drugorders.drugordersActivator;
import org.openmrs.module.drugorders.newplans;
import org.openmrs.module.drugorders.standardplans;
import org.openmrs.module.uicommons.util.InfoErrorMessageUtil;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author harini-geek
 */
public class AdministrationPageController {
        
    public void controller(PageModel model, HttpSession session, @RequestParam(value = "planId", required = false) String planId,
                            @RequestParam(value = "definePlanId", required = false) String definePlanId,
                            @RequestParam(value = "definePlanName", required = false) String definePlanName,
                            @RequestParam(value = "definePlanDesc", required = false) String definePlanDesc,
                            @RequestParam(value = "adminPlan", required = false) String adminPlan,
                            @RequestParam(value = "adminDrug", required = false) String adminDrug,
                            @RequestParam(value = "adminRoute", required = false) String adminRoute,
                            @RequestParam(value = "adminDose", required = false) String adminDose,
                            @RequestParam(value = "adminDoseUnits", required = false) String adminDoseUnits,
                            @RequestParam(value = "adminQuantity", required = false) String adminQuantity,
                            @RequestParam(value = "adminQuantityUnits", required = false) String adminQuantityUnits,
                            @RequestParam(value = "adminDuration", required = false) Integer adminDuration,
                            @RequestParam(value = "adminDurationUnits", required = false) String adminDurationUnits,
                            @RequestParam(value = "adminFrequency", required = false) String adminFrequency,
                            @RequestParam(value = "groupCheckBox", required=false) long[] groupCheckBox,
                            @RequestParam(value = "planToDiscard", required = false) String planToDiscard,
                            @RequestParam(value = "discardReason", required = false) String discardReason,                            
                            @RequestParam(value = "drugId", required = false) String drugId,
                            @RequestParam(value = "action", required = false) String action){
                
        if (StringUtils.isNotBlank(action)) {
            try {
                if (null != action) switch (action) {
                    case "definePlan":
                        newplans newplan = new newplans();
                        
                        if(Context.getService(newplansService.class).getMedicationPlan(ConceptName(definePlanName.trim())) == null){
                            if(ConceptName(definePlanName.trim()) == null){
                                drugordersActivator activator = new drugordersActivator();
                                Concept planConcept =  activator.saveConcept(definePlanName.trim(), Context.getConceptService().getConceptClassByName("Diagnosis"));
                                newplan.setPlanName(planConcept);
                            } 
                            else
                                newplan.setPlanName(ConceptName(definePlanName.trim()));
                        } else
                            newplan.setPlanName(Context.getService(newplansService.class).getMedicationPlan(ConceptName(definePlanName.trim())).getPlanName());
                        
                        
                        newplan.setPlanDesc(definePlanDesc);
                        newplan.setPlanStatus("Active");
                        Context.getService(newplansService.class).saveMedicationPlan(newplan);
                        InfoErrorMessageUtil.flashInfoMessage(session, "Plan Saved!");
                        break;
                        
                    case "addPlan":
                        standardplans medPlans = new standardplans();
                        medPlans.setPlanId(Context.getService(newplansService.class).getMedicationPlan(ConceptName(adminPlan)).getId());
                        
                        if(ConceptName(adminDrug.trim()) == null){
                            drugordersActivator activator = new drugordersActivator();
                            Concept drugConcept =  activator.saveConcept(adminDrug.trim(), Context.getConceptService().getConceptClassByName("Drug"));
                            medPlans.setDrugId(drugConcept);
                        }
                        else
                            medPlans.setDrugId(ConceptName(adminDrug.trim()));
                        
                        medPlans.setPlanStatus("Active");
                        medPlans.setRoute(ConceptName(adminRoute));
                        medPlans.setDose(Double.valueOf(adminDose));
                        medPlans.setDoseUnits(ConceptName(adminDoseUnits));
                        medPlans.setDuration(adminDuration);
                        medPlans.setDurationUnits(ConceptName(adminDurationUnits));
                        medPlans.setQuantity(Double.valueOf(adminQuantity));
                        medPlans.setQuantityUnits(ConceptName(adminQuantityUnits));
                        
                        OrderFrequency orderFrequency = Context.getOrderService().getOrderFrequencyByConcept(ConceptName(adminFrequency));
                        if (orderFrequency == null) {
                            medPlans.setFrequency(setOrderFrequency(adminFrequency));
                        } else {
                            medPlans.setFrequency(orderFrequency);
                        }   
                        
                        if(!(planId.equals(""))){
                            Context.getService(standardplansService.class).deleteMedicationPlan(Context.getService(standardplansService.class).getMedicationPlan(Integer.parseInt(planId)));
                        }   Context.getService(standardplansService.class).saveMedicationPlan(medPlans);
                        
                        InfoErrorMessageUtil.flashInfoMessage(session, "Plan Updated!");
                        break;
                        
                    case "renamePlan":
                        newplans oldPlan = Context.getService(newplansService.class).getMedicationPlan(Integer.parseInt(definePlanId));
                        oldPlan.setPlanName(ConceptName(definePlanName.trim()));
                        InfoErrorMessageUtil.flashInfoMessage(session, "Plan Renamed!");
                        break;
                        
                    case "deletePlan":
                        if(groupCheckBox.length > 0){
                            for(int i=0;i<groupCheckBox.length;i++){
                                int id = Integer.parseInt(Long.toString(groupCheckBox[i]));
                                standardplans medPlan = Context.getService(standardplansService.class).getMedicationPlan(id);
                                medPlan.setPlanStatus("Non-Active");
                                medPlan.setDiscardReason(discardReason);
                            }
                        }
                        if(Context.getService(standardplansService.class).getMedicationPlans(Context.getService(newplansService.class).getMedicationPlan(ConceptName(planToDiscard)).getId()).isEmpty()){
                            Context.getService(newplansService.class).getMedicationPlan(ConceptName(planToDiscard)).setPlanStatus("Non-Active");
                            Context.getService(newplansService.class).getMedicationPlan(ConceptName(planToDiscard)).setDiscardReason(discardReason);
                            InfoErrorMessageUtil.flashInfoMessage(session, "Plan Discarded!");
                        }                        
                        break;
                        
                    case "deleteDrug":
                        if(groupCheckBox.length > 0){
                            int id = Integer.parseInt(Long.toString(groupCheckBox[0]));
                            standardplans medPlan = Context.getService(standardplansService.class).getMedicationPlan(id);
                            medPlan.setPlanStatus("Non-Active");
                            medPlan.setDiscardReason(discardReason);
                            InfoErrorMessageUtil.flashInfoMessage(session, "Drug removed from Plan!");
                        }   
                        break;
                }
                
            } catch(APIException | NumberFormatException e){
                System.out.println("Error message "+e.getMessage());
            }
        }
        
        if(!planToDiscard.isEmpty())
            model.addAttribute("recordedMedPlan", planToDiscard);
        else 
            if(!adminPlan.isEmpty())
                model.addAttribute("recordedMedPlan", adminPlan);
        else
            model.addAttribute("recordedMedPlan", null);
        
        HashMap<Concept,List<standardplans>> allMedicationPlans = new HashMap<>();
        
        List<newplans> newPlans = Context.getService(newplansService.class).getAllMedicationPlans();
        List<newplans> activePlans = new ArrayList<>();
        for(newplans newPlan : newPlans){
            if(newPlan.getPlanStatus().equals("Active"))
                activePlans.add(newPlan);
        }
        
        model.addAttribute("newPlans", activePlans);
        
        for(newplans newPlan : newPlans){
            List<standardplans> medicationPlans = Context.getService(standardplansService.class).getMedicationPlans(newPlan.getId());
            List<standardplans> activeItems = new ArrayList<>();
            for(standardplans medPlan : medicationPlans){
                if(medPlan.getPlanStatus().equals("Active"))
                    activeItems.add(medPlan);
            }
            allMedicationPlans.put(newPlan.getPlanName(), activeItems);
        }
        
        model.addAttribute("allMedicationPlans", allMedicationPlans);
    }
    
    private Concept ConceptName(String conceptString){
        return Context.getConceptService().getConceptByName(conceptString);
    }
    
    private OrderFrequency setOrderFrequency(String Frequency) {
        OrderFrequency orderFrequency = new OrderFrequency();
        orderFrequency.setFrequencyPerDay(0.0);
        orderFrequency.setConcept(ConceptName(Frequency));
        orderFrequency = (OrderFrequency) Context.getOrderService().saveOrderFrequency(orderFrequency);
        return orderFrequency;
    }
}