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
public class AdministrationFragmentController {
    
    public void controller(PageModel model, @RequestParam(value = "selectedMedPlan", required = false) String selectedMedPlan,
                                            @RequestParam(value = "selectedPlanItem", required = false) Integer selectedPlanItem){

        model.addAttribute("selectedMedPlan", selectedMedPlan);
        
        HashMap<String, List<standardplans>> selectedPlan = new HashMap<>();
        List<standardplans> plans = new ArrayList<>();
        
        if(!selectedMedPlan.equals("")){
            plans = Context.getService(standardplansService.class).getMedicationPlans(Context.getService(newplansService.class).getMedicationPlan(Context.getConceptService().getConceptByName(selectedMedPlan)).getId());
            selectedPlan.put(selectedMedPlan, plans);
        }
        else if(selectedPlanItem != null){
            standardplans plan = Context.getService(standardplansService.class).getMedicationPlan(selectedPlanItem);
            plans.add(plan);
            selectedPlan.put(Context.getService(newplansService.class).getMedicationPlan(plan.getPlanId()).getPlanName().getDisplayString(), plans);
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
    
    public List<Concept> getConcepts(String ConceptClassName){
        ConceptClass conceptClass = Context.getConceptService().getConceptClassByName(ConceptClassName);
        return Context.getConceptService().getConceptsByClass(conceptClass);
    }
    
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