/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders.fragment.controller;

import java.util.ArrayList;
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
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author harini-geek
 */

public class AddNewOrderFragmentController {
    
    /**
     *
     * @param model
     * @param planName
     * @param patient
     * @param patientService
     */
    
    public void controller(PageModel model, @RequestParam("patientId") Patient patient,
            @RequestParam(value = "planName", required = false) String planName,
            @SpringBean("allergyService") PatientService patientService){

        model.addAttribute("planName", planName.trim());
        
        /*
          Get the list of standard medications plans for the disease name typed.
          Only plans that are currently active must be retrieved.
        */
        List<standardplans> medplans = new ArrayList<>();
        if(Context.getService(newplansService.class).getMedicationPlan(Context.getConceptService().getConceptByName(planName)) != null && Context.getService(newplansService.class).getMedicationPlan(Context.getConceptService().getConceptByName(planName)).getPlanStatus().equals("Active")){
            newplans newPlan = Context.getService(newplansService.class).getMedicationPlan(Context.getConceptService().getConceptByName(planName));
            List<standardplans> standardplans = Context.getService(standardplansService.class).getMedicationPlans(newPlan.getId());
            
            for(standardplans standardplan : standardplans)
                if(standardplan.getPlanStatus().equals("Active"))
                    medplans.add(standardplan);
        }
        model.addAttribute("medplans", medplans);
        
        /*
          Get the list of drugs that the Patient is allergic to.
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
        
        ConceptClass planConcept = Context.getConceptService().getConceptClassByName("Diagnosis");
        List<ConceptClass> requireClasses = new ArrayList<>();
        requireClasses.add(planConcept);
        
        List<ConceptSearchResult> results = Context.getConceptService().getConcepts(query, null, false, requireClasses, null, null, null, null, 0, 100);
        
        List<Concept> names = new ArrayList<>();
        for (ConceptSearchResult con : results) {
            newplans plan = Context.getService(newplansService.class).getMedicationPlan(con.getConcept());
            if(plan != null && plan.getPlanStatus().equals("Active")){
                if(Context.getService(standardplansService.class).getMedicationPlans(plan.getId()).size() > 0)
                    names.add(con.getConcept());
            }
        }
        String[] properties = new String[] { "name"};
        return SimpleObject.fromCollection(names, ui, properties);
    }
}
