/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders.page.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.OrderFrequency;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.drugorders.api.newplansService;
import org.openmrs.module.drugorders.api.standardplansService;
import org.openmrs.module.drugorders.newplans;
import org.openmrs.module.drugorders.standardplans;
import org.openmrs.module.uicommons.util.InfoErrorMessageUtil;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author harini-parthasarathy
 */
public class AdministrationPageController {
        
    public void controller(PageModel model, HttpSession session,
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
                            @RequestParam(value = "planToDiscard", required = false) Integer planToDiscard,
                            @RequestParam(value = "discardReason", required = false) String discardReason,                            
                            @RequestParam(value = "drugId", required = false) String drugId,
                            @RequestParam(value = "action", required = false) String action){
                
        if (StringUtils.isNotBlank(action)) {
            try {
                if (null != action) switch (action) {
                    /*
                      When a plan is defined, store the name of the plan and description. Set the plan status 'Active'.
                      The name of the plan is stored as a concept.
                    */
                    case "definePlan":
                        // Check if a concept for the given plan name (disease) exists
                        if(ConceptName(definePlanName.trim()) != null){
                            // Define a medication plan if it does not already exists
                            if(Context.getService(newplansService.class).getMedPlanByPlanName(ConceptName(definePlanName.trim())) == null){
                                newplans newplan = new newplans();
                                newplan.setPlanName(ConceptName(definePlanName.trim()));
                                newplan.setPlanDesc(definePlanDesc);
                                newplan.setPlanStatus("Active");
                                Context.getService(newplansService.class).saveMedPlan(newplan);
                                InfoErrorMessageUtil.flashInfoMessage(session, "Plan Saved!");
                            } else 
                                InfoErrorMessageUtil.flashErrorMessage(session, "Plan already exists!");
                        } else 
                                InfoErrorMessageUtil.flashErrorMessage(session, "Create a diagnosis concept!");
                        
                        break;
                    
                    /*
                      Add a drug with standard formulations and general consumption actions to the plan. Set the status of the record 'Active'.
                      Save these formulations in the standardplans table and group the plans with the plan ID (Identifying the disease).
                    */
                    case "extendPlan":
                        // Check if a concept for the given drug name exists
                        if(ConceptName(adminDrug.trim()) != null){
                            // Check if the medication plan already includes the selected drug.
                            List<standardplans> plans = Context.getService(standardplansService.class).getMedPlansByPlanID(Context.getService(newplansService.class).getMedPlanByPlanName(ConceptName(adminPlan)).getId());

                            for(standardplans plan : plans)
                                if(plan.getDrugId() == ConceptName(adminDrug.trim())){
                                    standardplans sp = Context.getService(standardplansService.class).getMedPlanByID(plan.getId());
                                    sp.setDiscardReason("Modified Formulations");
                                    sp.setPlanStatus("Non-Active");
                                }

                            // Extend the new standard medication plan to include the drug with the given formulations.
                            standardplans medPlans = new standardplans();
                            medPlans.setPlanId(Context.getService(newplansService.class).getMedPlanByPlanName(ConceptName(adminPlan)).getId());
                            medPlans.setDrugId(ConceptName(adminDrug.trim()));
                            medPlans.setPlanStatus("Active");
                            medPlans.setRoute(ConceptName(adminRoute));
                            medPlans.setDose(Double.valueOf(adminDose));
                            medPlans.setDoseUnits(ConceptName(adminDoseUnits));
                            medPlans.setDuration(adminDuration);
                            medPlans.setDurationUnits(ConceptName(adminDurationUnits));
                            medPlans.setQuantity(Double.valueOf(adminQuantity));
                            medPlans.setQuantityUnits(ConceptName(adminQuantityUnits));

                            /*
                              Set the OrderFrequency type. 
                              If it does not existc create a new OrderFrequency record for the selected frequency value.
                            */
                            OrderFrequency orderFrequency = Context.getOrderService().getOrderFrequencyByConcept(ConceptName(adminFrequency));
                            if (orderFrequency == null) {
                                medPlans.setFrequency(setOrderFrequency(adminFrequency));
                            } else {
                                medPlans.setFrequency(orderFrequency);
                            }   

                            Context.getService(standardplansService.class).saveMedPlan(medPlans);

                            InfoErrorMessageUtil.flashInfoMessage(session, "Plan Updated!");
                        } else 
                            InfoErrorMessageUtil.flashErrorMessage(session, "Create a drug concept!");
                                                
                        break;
                        
                    /*
                      Rename the plan by replacing the concept ID of the existing name with the concept ID of the new name.
                    */
                    case "editPlan":
                        // Check if a concept for the given plan name (disease) exists
                        if(ConceptName(definePlanName.trim()) != null){
                            newplans oldPlan = Context.getService(newplansService.class).getMedPlanByPlanID(Integer.parseInt(definePlanId));
                            
                            if(oldPlan.getPlanName() != ConceptName(definePlanName.trim()) && Context.getService(newplansService.class).getMedPlanByPlanName(ConceptName(definePlanName.trim())) != null){
                                InfoErrorMessageUtil.flashErrorMessage(session, "Cannot modify plan, plan already exists!");
                            } 
                            else {
                                oldPlan.setPlanName(ConceptName(definePlanName.trim()));
                                oldPlan.setPlanDesc(definePlanDesc.trim());
                                InfoErrorMessageUtil.flashInfoMessage(session, "Plan Modified!");
                            }
                        } 
                        else
                            InfoErrorMessageUtil.flashErrorMessage(session, "Create a diagnosis concept!");
                        
                        break;
                        
                    /*
                      Set the status of the medication plan as non-active.
                      If one or more check-boxes corresponding to drugs that are a part of the plan are checked, set the status of that plan item to 'Non-Active'.
                    */
                    case "discardPlan":
                        if(groupCheckBox.length > 0){
                            for(int i=0;i<groupCheckBox.length;i++){
                                int id = Integer.parseInt(Long.toString(groupCheckBox[i]));
                                standardplans medPlan = Context.getService(standardplansService.class).getMedPlanByID(id);
                                medPlan.setPlanStatus("Non-Active");
                                medPlan.setDiscardReason(discardReason);
                            }
                        }
                        
                        /*
                          Before discarding a medication plan, ensure that all the plan items (standard drug orders) associated with that plan are discarded.
                          If one or more standard plan records associated with a plan is active, the plan cannot be discontinued.
                        */
                        boolean allDrugsDiscarded = true;
                        List<standardplans> allPlans = Context.getService(standardplansService.class).getMedPlansByPlanID(Context.getService(newplansService.class).getMedPlanByPlanID(planToDiscard).getId());
                        for(standardplans plan : allPlans)
                            if(plan.getPlanStatus().equals("Active"))
                                allDrugsDiscarded = false;
                        
                        if(allDrugsDiscarded){
                            Context.getService(newplansService.class).getMedPlanByPlanID(planToDiscard).setPlanStatus("Non-Active");
                            Context.getService(newplansService.class).getMedPlanByPlanID(planToDiscard).setDiscardReason(discardReason);
                            InfoErrorMessageUtil.flashInfoMessage(session, "Plan Discarded!");
                        }                        
                        break;
                    
                    /*
                      If a check-box corresponding to a drug is checked, it is set to be discontinued.
                    */
                    case "discardDrug":
                        if(groupCheckBox.length > 0){
                            int id = Integer.parseInt(Long.toString(groupCheckBox[0]));
                            standardplans medPlan = Context.getService(standardplansService.class).getMedPlanByID(id);
                            // Set the status of the drug to 'Non-Active' and the reason for removing the drug from the medication plan.
                            medPlan.setPlanStatus("Non-Active");
                            medPlan.setDiscardReason(discardReason);
                            InfoErrorMessageUtil.flashInfoMessage(session, "Drug removed from Plan!");
                        }   
                        break;
                }
                
            } catch(APIException | NumberFormatException ex){
                Logger.getLogger(AdministrationPageController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        /*
          Check if a plan has been selected to be discarded or updated (in order to highlight the plan in the table).
        */
        if(planToDiscard != null)
            model.addAttribute("recordedMedPlan", Context.getService(newplansService.class).getMedPlanByPlanID(planToDiscard).getPlanName().getDisplayString());
        else if(!adminPlan.isEmpty())
                model.addAttribute("recordedMedPlan", adminPlan);
        else
            model.addAttribute("recordedMedPlan", null);
        
        // Get the list of all defined and currently active medication plans.
        HashMap<Concept,List<standardplans>> allMedicationPlans = new HashMap<>();
        
        List<newplans> newPlans = Context.getService(newplansService.class).getAllMedPlans();
        List<newplans> activePlans = new ArrayList<>();
        for(newplans newPlan : newPlans){
            if(newPlan.getPlanStatus().equals("Active"))
                activePlans.add(newPlan);
        }
        
        model.addAttribute("newPlans", activePlans);
        
        // Get the list of all plan items (drugs) defined in the medication plan that are currently active.
        for(newplans newPlan : newPlans){
            List<standardplans> medicationPlans = Context.getService(standardplansService.class).getMedPlansByPlanID(newPlan.getId());
            List<standardplans> activeItems = new ArrayList<>();
            for(standardplans medPlan : medicationPlans){
                if(medPlan.getPlanStatus().equals("Active"))
                    activeItems.add(medPlan);
            }
            allMedicationPlans.put(newPlan.getPlanName(), activeItems);
        }
        
        model.addAttribute("allMedicationPlans", allMedicationPlans);
    }
    
    // Return the concept defined for a given string.
    private Concept ConceptName(String conceptString){
        return Context.getConceptService().getConceptByName(conceptString);
    }
    
    // Save an OrderFrequency record.
    private OrderFrequency setOrderFrequency(String Frequency) {
        OrderFrequency orderFrequency = new OrderFrequency();
        orderFrequency.setFrequencyPerDay(0.0);
        orderFrequency.setConcept(ConceptName(Frequency));
        orderFrequency = (OrderFrequency) Context.getOrderService().saveOrderFrequency(orderFrequency);
        return orderFrequency;
    }
}