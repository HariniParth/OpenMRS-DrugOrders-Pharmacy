/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders.fragment.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptSearchResult;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.allergyapi.api.PatientService;
import org.openmrs.module.drugorders.api.newplansService;
import org.openmrs.module.drugorders.api.standardplansService;
import org.openmrs.module.drugorders.newplans;
import org.openmrs.module.drugorders.standardplans;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.util.ByFormattedObjectComparator;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author harini-geek
 */

public class CreateMedPlanDrugOrderFragmentController {
    
    /**
     *
     * @param model
     * @param planName
     * @param patient
     * @param patientService
     */
    
    public void controller(FragmentModel model, @RequestParam("patientId") Patient patient,
            @RequestParam(value = "planName", required = false) String planName,
            @SpringBean("allergyService") PatientService patientService){

        model.addAttribute("planName", planName.trim());
        
        List<standardplans> medplans = new ArrayList<>();
        
        // Check if there exists standard medications plans for the disease selected.
        if(Context.getService(newplansService.class).getMedPlanByPlanName(Context.getConceptService().getConceptByName(planName)) != null && Context.getService(newplansService.class).getMedPlanByPlanName(Context.getConceptService().getConceptByName(planName)).getPlanStatus().equals("Active")){
            newplans newPlan = Context.getService(newplansService.class).getMedPlanByPlanName(Context.getConceptService().getConceptByName(planName));
            // Retrieve the list of the plans that are currently active.
            List<standardplans> standardplans = Context.getService(standardplansService.class).getMedPlansByPlanID(newPlan.getId());
            
            for(standardplans standardplan : standardplans)
                if(standardplan.getPlanStatus().equals("Active"))
                    medplans.add(standardplan);
        }
        model.addAttribute("medplans", medplans);
        
        /*
          Get the list of drugs that the Patient is allergic to.
          If no drug is recorded as allergic, store the 'null' value.
        */        
        int number_of_allergic_drugs = patientService.getAllergies(patient).size();
        if(number_of_allergic_drugs >=1){
            ArrayList<String> allergen = new ArrayList<>();
            for(int i=0;i<number_of_allergic_drugs;i++){
                allergen.add(patientService.getAllergies(patient).get(i).getAllergen().toString().trim());
            }
            model.addAttribute("allergicDrugs", allergen);
        } else {
            model.addAttribute("allergicDrugs", "null");
        } 
        
    }
    
    /*
      Get plan name suggestions as the user starts typing the first few characters.
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
            
            //Based on the characters typed, check if a plan exists and is currently active.
            newplans plan = Context.getService(newplansService.class).getMedPlanByPlanName(con.getConcept());
            if(plan != null && plan.getPlanStatus().equals("Active")){
                if(Context.getService(standardplansService.class).getMedPlansByPlanID(plan.getId()).size() > 0)
                    names.add(con.getConcept());
            }
        }
        // Sort the list of concepts by their name
        Comparator comparator = new ByFormattedObjectComparator(ui);
        Collections.sort(names, comparator);
        // Get the name property of the concepts.
        String[] properties = new String[] { "name"};
        return SimpleObject.fromCollection(names, ui, properties);
    }
}
