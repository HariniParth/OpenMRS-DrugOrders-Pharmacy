/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders.fragment.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptSearchResult;
import org.openmrs.OrderFrequency;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.drugorders.api.newplansService;
import org.openmrs.module.drugorders.api.standardplansService;
import org.openmrs.module.drugorders.standardplans;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author harini-geek
 */
public class AdministrationActionsFragmentController {
    
    public void controller(PageModel model, @RequestParam(value = "selectedMedPlan", required = false) Integer selectedMedPlan,
                                            @RequestParam(value = "selectedPlanItem", required = false) Integer selectedPlanItem){

        model.addAttribute("selectedMedPlan", selectedMedPlan);
        
        HashMap<Integer,HashMap<Concept, List<standardplans>>> selectedPlan = new HashMap<>();
        HashMap<Concept, List<standardplans>> plansByName = new HashMap<>();
        
        /*
          Get the list of standard plan items for the selected standard plan that are currently active.
        */
        if(selectedMedPlan != null){
            List<standardplans> plans = Context.getService(standardplansService.class).getMedPlansByPlanID(Context.getService(newplansService.class).getMedPlanByPlanID(selectedMedPlan).getId());
            List<standardplans> activePlans = new ArrayList<>();
            for(standardplans plan : plans)
                if(plan.getPlanStatus().equals("Active"))
                    activePlans.add(plan);
            
            plansByName.put(Context.getService(newplansService.class).getMedPlanByPlanID(selectedMedPlan).getPlanName(), activePlans);
            selectedPlan.put(selectedMedPlan, plansByName);
        }
        
        // Get the selected standard plan item.
        else if(selectedPlanItem != null){
            List<standardplans> plans = new ArrayList<>();
            standardplans plan = Context.getService(standardplansService.class).getMedPlanByID(selectedPlanItem);
            plans.add(plan);
            plansByName.put(Context.getService(newplansService.class).getMedPlanByPlanID(plan.getPlanId()).getPlanName(), plans);
            selectedPlan.put(plan.getPlanId(), plansByName);
        }
            
        model.addAttribute("selectedPlan", selectedPlan);
               
        List<Concept> durations = getConcepts("Units of Duration");
        model.addAttribute("durations", durations);
        
        List<Concept> routes = getConcepts("Routes of drug administration");
        model.addAttribute("routes", routes);
        
        List<Concept> doses = getConcepts("Units of Dose");
        model.addAttribute("doses", doses);
        
        List<Concept> quantities = getConcepts("Units of Quantity");
        model.addAttribute("quantities", quantities);
 
        List<OrderFrequency> frequencies = Context.getOrderService().getOrderFrequencies(true);
        model.addAttribute("frequencies", frequencies);
    }
    
    /*
      Get the list of concepts belonging to the given concept class
    */
    public List<Concept> getConcepts(String ConceptClassName){
        ConceptClass conceptClass = Context.getConceptService().getConceptClassByName(ConceptClassName);
        return Context.getConceptService().getConceptsByClass(conceptClass);
    }
    
    /*
      Get drug name suggestions as the user types the first few characters of the drug name field
    */
    public List<SimpleObject> getDrugNameSuggestions(@RequestParam(value = "query", required = false) String query,
                                                     @SpringBean("conceptService") ConceptService service,
                                                     UiUtils ui) {
        
        ConceptClass drugConcept = Context.getConceptService().getConceptClassByName("Drug");
        List<ConceptClass> requireClasses = new ArrayList<>();
        requireClasses.add(drugConcept);
        
        List<ConceptSearchResult> results = Context.getConceptService().getConcepts(query, null, false, requireClasses, null, null, null, null, 0, 100);
        
        List<Concept> names = new ArrayList<>();
        for (ConceptSearchResult con : results) {
            names.add(con.getConcept());
        }
        String[] properties = new String[] { "name"};
        return SimpleObject.fromCollection(names, ui, properties);
    }
        
    /*
      Get plan name suggestions as the users types the first few characters of a plan.
    */
    public List<SimpleObject> getPlanNameSuggestions(@RequestParam(value = "query", required = false) String query,
                                                     @SpringBean("conceptService") ConceptService service,
                                                     UiUtils ui) {
        
        ConceptClass diseaseConcept = Context.getConceptService().getConceptClassByName("Diagnosis");
        List<ConceptClass> requireClasses = new ArrayList<>();
        requireClasses.add(diseaseConcept);
        
        List<ConceptSearchResult> results = Context.getConceptService().getConcepts(query, null, false, requireClasses, null, null, null, null, 0, 100);
        
        List<Concept> names = new ArrayList<>();
        for (ConceptSearchResult con : results) {
            names.add(con.getConcept());
        }
        String[] properties = new String[] { "name"};
        return SimpleObject.fromCollection(names, ui, properties);
    }
}