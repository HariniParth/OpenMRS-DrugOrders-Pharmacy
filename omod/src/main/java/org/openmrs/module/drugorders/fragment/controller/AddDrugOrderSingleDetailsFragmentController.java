/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders.fragment.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptSearchResult;
import org.openmrs.OrderFrequency;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.allergyapi.api.PatientService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author harini-geek
 */
public class AddDrugOrderSingleDetailsFragmentController {
    
    /**
     *
     * @param model
     * @param patient
     * @param patientService
     */
    
    public void controller(PageModel model, @RequestParam("patientId") Patient patient,
                            @SpringBean("allergyService") PatientService patientService){
                
        Date startDate = Calendar.getInstance().getTime();
        model.addAttribute("startDate", startDate);
        
        int number_of_allergic_drugs = patientService.getAllergies(patient).size();
        if(number_of_allergic_drugs >=1){
            ArrayList<String> allergen = new ArrayList<>();
            for(int i=0;i<number_of_allergic_drugs;i++){
                allergen.add(patientService.getAllergies(patient).get(i).getAllergen().toString());
                model.addAttribute("allergicDrugs", allergen);
            }
        } else {
            model.addAttribute("allergicDrugs", "null");
        }        
        
        List<Concept> drugs = ConceptList("Drug");
        
        List<String> drugsNames = new ArrayList<>();
        for(Concept drug : drugs){
            drugsNames.add(drug.getDisplayString());
        }
        model.addAttribute("drugsNames", drugsNames);
        
        List<Concept> doses = ConceptList("Units of Dose");
        model.addAttribute("doses", doses);        
        
        List<Concept> priorities = ConceptList("Order Priority");
        model.addAttribute("priorities", priorities);
        
        List<Concept> durations = ConceptList("Units of Duration");
        model.addAttribute("durations", durations);
        
        List<Concept> quantities = ConceptList("Units of Quantity");
        model.addAttribute("quantities", quantities);        
        
        List<Concept> routes = ConceptList("Routes of drug administration");
        model.addAttribute("routes", routes);
        
        List<OrderFrequency> frequencies = Context.getOrderService().getOrderFrequencies(true);
        model.addAttribute("frequencies", frequencies);
    }
    
    private List<Concept> ConceptList(String conceptString){
        ConceptClass conceptClass = Context.getConceptService().getConceptClassByName(conceptString);
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
    
    
    public List<SimpleObject> getDiseaseNameSuggestions(@RequestParam(value = "query", required = false) String query,
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