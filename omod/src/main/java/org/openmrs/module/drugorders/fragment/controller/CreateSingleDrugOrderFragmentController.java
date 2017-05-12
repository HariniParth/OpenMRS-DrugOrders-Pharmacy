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
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author harini-geek
 */
public class CreateSingleDrugOrderFragmentController {
    
    /**
     *
     * @param model
     * @param patient
     */
    
    public void controller(PageModel model, @RequestParam("patientId") Patient patient){
              
        // Retrieve the current date and time
        Date startDate = Calendar.getInstance().getTime();
        model.addAttribute("startDate", startDate);
                
        // Retrieve the list of concepts belonging to the concept class "Units of Dose".
        List<Concept> doses = ConceptList("Units of Dose");
        model.addAttribute("doses", doses);        
        
        // Retrieve the list of concepts belonging to the concept class "Order Priority".
        List<Concept> priorities = ConceptList("Order Priority");
        model.addAttribute("priorities", priorities);
        
        // Retrieve the list of concepts belonging to the concept class "Units of Duration".
        List<Concept> durations = ConceptList("Units of Duration");
        model.addAttribute("durations", durations);
        
        // Retrieve the list of concepts belonging to the concept class "Units of Quantity".
        List<Concept> quantities = ConceptList("Units of Quantity");
        model.addAttribute("quantities", quantities);        
        
        // Retrieve the list of concepts belonging to the concept class "Routes of drug administration".
        List<Concept> routes = ConceptList("Routes of drug administration");
        model.addAttribute("routes", routes);
        
        // Retrieve the list of OrderFrequency class values.
        List<OrderFrequency> frequencies = Context.getOrderService().getOrderFrequencies(true);
        model.addAttribute("frequencies", frequencies);
    }
    
    /*
      Get the list of concepts belonging to a particular concept class
    */
    private List<Concept> ConceptList(String conceptString){
        ConceptClass conceptClass = Context.getConceptService().getConceptClassByName(conceptString);
        return Context.getConceptService().getConceptsByClass(conceptClass);
    }
    
    /*
      Get drug name suggestions as the user types the first few characters of the drug name field
    */
    public List<SimpleObject> getDrugNameSuggestions(@RequestParam(value = "query", required = false) String query,
                                                     @SpringBean("conceptService") ConceptService service,
                                                     UiUtils ui) {
        
        // Select the Concept Class by name "Drug".
        ConceptClass drugConcept = Context.getConceptService().getConceptClassByName("Drug");
        List<ConceptClass> requireClasses = new ArrayList<>();
        requireClasses.add(drugConcept);
        
        // Narrow down the list of concepts belonging to the class conceptClass based on the text typed by the user 'query'.
        List<ConceptSearchResult> results = Context.getConceptService().getConcepts(query, null, false, requireClasses, null, null, null, null, 0, 100);
        
        List<Concept> names = new ArrayList<>();
        for (ConceptSearchResult con : results) {
            names.add(con.getConcept());
        }
        // Get the name property of the concepts.
        String[] properties = new String[] { "name"};
        return SimpleObject.fromCollection(names, ui, properties);
    }
    
    /*
      Get disease name suggestions as the users types the first few characters of a disease.
    */
    public List<SimpleObject> getDiseaseNameSuggestions(@RequestParam(value = "query", required = false) String query,
                                                        @SpringBean("conceptService") ConceptService service,
                                                        UiUtils ui) {
        
        // Select the Concept Class by name "Diagnosis".
        ConceptClass diseaseConcept = Context.getConceptService().getConceptClassByName("Diagnosis");
        List<ConceptClass> requireClasses = new ArrayList<>();
        requireClasses.add(diseaseConcept);
        
        // Narrow down the list of concepts belonging to the class conceptClass based on the text typed by the user 'query'.
        List<ConceptSearchResult> results = Context.getConceptService().getConcepts(query, null, false, requireClasses, null, null, null, null, 0, 100);
        
        List<Concept> names = new ArrayList<>();
        for (ConceptSearchResult con : results) {
            names.add(con.getConcept());
        }
        // Get the name property of the concepts.
        String[] properties = new String[] { "name"};
        return SimpleObject.fromCollection(names, ui, properties);
    }
}