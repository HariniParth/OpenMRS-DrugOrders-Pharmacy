/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders.fragment.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Collections;
import java.util.Comparator;
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
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.util.ByFormattedObjectComparator;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author harini-parthasarathy
 */
public class AdministrationActionsFragmentController {
    
    public void controller(FragmentModel model, @RequestParam(value = "selectedMedPlan", required = false) Integer selectedMedPlan,
                           @RequestParam(value = "selectedPlanItem", required = false) Integer selectedPlanItem, UiUtils ui){

        model.addAttribute("selectedMedPlan", selectedMedPlan);
        
        // Storing HashMap< Plan-ID, HashMap< Plan-Name, List-of-drugs>>
        HashMap<Integer,HashMap<Concept, List<standardplans>>> selectedPlan = new HashMap<>();
        // Storing HashMap< Plan-Name, List-of-drugs>>
        HashMap<Concept, List<standardplans>> plansByName = new HashMap<>();
        
        // If a medication plan is selected, retrieve the list of standard plan items (drugs) for the selected standard plan.
        if(selectedMedPlan != null){
            List<standardplans> plans = Context.getService(standardplansService.class).getMedPlansByPlanID(Context.getService(newplansService.class).getMedPlanByPlanID(selectedMedPlan).getId());
            List<standardplans> activePlans = new ArrayList<>();
            
            // From this list of standard plan items, retrieve the records that are currently active.
            for(standardplans plan : plans)
                if(plan.getPlanStatus().equals("Active"))
                    activePlans.add(plan);
            
            // Store the list of active standard plan items for the selected medication plan.
            plansByName.put(Context.getService(newplansService.class).getMedPlanByPlanID(selectedMedPlan).getPlanName(), activePlans);
            selectedPlan.put(selectedMedPlan, plansByName);
        }
        
        // If a standard plan item is selected, retrieve the details.
        else if(selectedPlanItem != null){
            List<standardplans> plans = new ArrayList<>();
            standardplans plan = Context.getService(standardplansService.class).getMedPlanByID(selectedPlanItem);
            plans.add(plan);
            plansByName.put(Context.getService(newplansService.class).getMedPlanByPlanID(plan.getNewPlanId()).getPlanName(), plans);
            selectedPlan.put(plan.getNewPlanId(), plansByName);
        }
            
        model.addAttribute("selectedPlan", selectedPlan);
               
        // To sort the list of concepts by their name
        Comparator comparator = new ByFormattedObjectComparator(ui);
        
        // Retrieve the list of concepts belonging to the concept class "Units of Duration".
        List<Concept> durations = getConcepts("Units of Duration");
        Collections.sort(durations, comparator);
        model.addAttribute("durations", durations);
        
        // Retrieve the list of concepts belonging to the concept class "Routes of drug administration".
        List<Concept> routes = getConcepts("Routes of drug administration");
        Collections.sort(routes, comparator);
        model.addAttribute("routes", routes);
        
        // Retrieve the list of concepts belonging to the concept class "Units of Dose".
        List<Concept> doses = getConcepts("Units of Dose");
        Collections.sort(doses, comparator);
        model.addAttribute("doses", doses);
        
        // Retrieve the list of concepts belonging to the concept class "Units of Quantity".
        List<Concept> quantities = getConcepts("Units of Quantity");
        Collections.sort(quantities, comparator);
        model.addAttribute("quantities", quantities);
 
        // Retrieve the list of OrderFrequency class values.
        List<OrderFrequency> frequencies = Context.getOrderService().getOrderFrequencies(true);
        Collections.sort(frequencies, comparator);
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
        
        // Select the Concept Class by name "Drug".
        ConceptClass conceptClass = Context.getConceptService().getConceptClassByName("Drug");
        List<ConceptClass> requireClasses = new ArrayList<>();
        requireClasses.add(conceptClass);
        
        // Narrow down the list of concepts belonging to the class conceptClass based on the text typed by the user 'query'.
        List<ConceptSearchResult> results = Context.getConceptService().getConcepts(query, null, false, requireClasses, null, null, null, null, 0, 100);
        
        List<Concept> names = new ArrayList<>();
        for (ConceptSearchResult con : results) {
            if(con.getConceptName().isLocalePreferred())
                names.add(con.getConcept());
        }
        // Sort the list of concepts by their name
        Comparator comparator = new ByFormattedObjectComparator(ui);
        Collections.sort(names, comparator);
        // Get the name property of the concepts.
        String[] properties = new String[] { "name" };
        return SimpleObject.fromCollection(names, ui, properties);
    }
        
    /*
      Get plan name suggestions as the users types the first few characters of a plan.
    */
    public List<SimpleObject> getPlanNameSuggestions(@RequestParam(value = "query", required = false) String query,
                                                     @SpringBean("conceptService") ConceptService service,
                                                     UiUtils ui) {
        
        // Select the Concept Class by name "Diagnosis".
        ConceptClass conceptClass = Context.getConceptService().getConceptClassByName("Diagnosis");
        List<ConceptClass> requireClasses = new ArrayList<>();
        requireClasses.add(conceptClass);
        
        // Narrow down the list of concepts belonging to the class conceptClass based on the text typed by the user 'query'.
        List<ConceptSearchResult> results = Context.getConceptService().getConcepts(query, null, false, requireClasses, null, null, null, null, 0, 100);
        
        List<Concept> names = new ArrayList<>();
        for (ConceptSearchResult con : results) {
            if(con.getConceptName().isLocalePreferred())
                names.add(con.getConcept());
        }
        // Sort the list of concepts by their name
        Comparator comparator = new ByFormattedObjectComparator(ui);
        Collections.sort(names, comparator);
        // Get the name property of the concepts.
        String[] properties = new String[] { "name"};
        return SimpleObject.fromCollection(names, ui, properties);
    }
}